package Myaven.ui.components;

import Myaven.config.ConfigManager;
import Myaven.module.modules.config.Language;
import Myaven.module.modules.config.Theme;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

public class BindComponent extends SettingComponent {
	public boolean isBinding;
	private int key;
	private int x;
	private int y;

	public BindComponent(ModuleComponent moduleComponent, int bind) {
		super(moduleComponent);
		this.x = moduleComponent.categoryComponent.getX() + moduleComponent.categoryComponent.getWidth();
		this.y = moduleComponent.categoryComponent.getY() + moduleComponent.yPos;
		this.key = bind;
	}

	@Override
	public void updateHeight(int n) {
		this.key = n;
	}

	@Override
	public void render() {
		GL11.glPushMatrix();
		GL11.glScaled(0.5, 0.5, 0.5);
		this.drawBindText(
			this.isBinding
				? (Language.applyForSettings.getState() ? Language.translate("clickgui.bind.press") : "Press a key...")
				: (Language.applyForSettings.getState() ? Language.translate("clickgui.bind.current") : "Current bind: '§e")
					+ Keyboard.getKeyName(this.parentModule.module.getKey())
					+ "§r'"
		);
		GL11.glPopMatrix();
	}

	@Override
	public void drawScreen(int x, int y) {
		this.y = this.parentModule.categoryComponent.getY() + this.key;
		this.x = this.parentModule.categoryComponent.getX();
	}

	@Override
	public void onClick(int x, int y, int b) {
		if (this.isHovering(x, y) && this.parentModule.expanded) {
			if (b == 0) {
				this.isBinding = !this.isBinding;
			} else if (b > 1 && this.isBinding) {
				this.parentModule.module.setKey(b + 1000);
				ConfigManager.save();
				this.isBinding = false;
			}
		}
	}

	@Override
	public void keyTyped(char t, int keybind) {
		if (this.isBinding) {
			if (keybind != 211 && keybind != 1) {
				this.parentModule.module.setKey(keybind);
			} else {
				this.parentModule.module.setKey(0);
			}

			ConfigManager.save();
			this.isBinding = false;
		}
	}

	public boolean isHovering(int x, int y) {
		return x > this.x && x < this.x + this.parentModule.categoryComponent.getWidth() && y > this.y - 1 && y < this.y + 12;
	}

	@Override
	public int getHeight() {
		return 16;
	}

	private void drawBindText(String s) {
		this.getFontRenderer()
			.drawStringWithShadow(
				s,
				(double)((float)((this.parentModule.categoryComponent.getX() + 4) * 2)),
				(double)((float)((this.parentModule.categoryComponent.getY() + this.key + 3) * 2)),
				Theme.computeThemeColor(0.0)
			);
	}

	@Override
	public void onGuiClosed() {
		this.isBinding = false;
	}

}
