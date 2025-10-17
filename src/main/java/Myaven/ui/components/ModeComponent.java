package Myaven.ui.components;

import Myaven.Myaven;
import Myaven.config.ConfigManager;
import Myaven.module.Module;
import Myaven.setting.Setting;
import Myaven.setting.settings.ModeSetting;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

public class ModeComponent extends SettingComponent {
	private ModeSetting b;
	private Module K;
	private static String f;

	public ModeComponent(ModeSetting ModeSetting, ModuleComponent moduleComponent, int o) {
		super(moduleComponent);
		this.b = ModeSetting;
		this.K = moduleComponent.module;
		this.a = moduleComponent.categoryComponent.getX() + moduleComponent.categoryComponent.getWidth();
		this.width = moduleComponent.categoryComponent.getY() + moduleComponent.yPos;
		this.Q = o;
	}

	@Override
	public Setting getSetting() {
		return this.b;
	}

	@Override
	public void render() {
		GL11.glPushMatrix();
		GL11.glScaled(0.5, 0.5, 0.5);
		String value = this.b.getText(this.K);
		this.getFontRenderer()
			.drawString(
				this.b.getDisplayName(this.K).replaceAll("-", " ") + ": " + value,
				(double)((float)((int)((float)(this.parentModule.categoryComponent.getX() + 4) * 2.0F))),
				(double)((float)((int)((float)(this.parentModule.categoryComponent.getY() + this.Q + 3) * 2.0F))),
				this.backgroundColor,
				true
			);
		GL11.glPopMatrix();
	}

	@Override
	public void updateHeight(int n) {
		this.Q = n;
	}

	@Override
	public void drawScreen(int x, int y) {
		this.width = this.parentModule.categoryComponent.getY() + this.Q;
		this.a = this.parentModule.categoryComponent.getX();
	}

	@Override
	public void onClick(int x, int y, int b) {
		if (this.isHovered(x, y) && this.parentModule.expanded) {
			this.swapMode(b, Keyboard.isKeyDown(Myaven.mc.gameSettings.keyBindSneak.getKeyCode()));
			this.parentModule.categoryComponent.recalculateLayout();
		}
	}

	private void swapMode(int b, boolean reserve) {
		boolean next;
		switch (b) {
			case 0:
				next = true;
				break;
			case 1:
				next = false;
				break;
			default:
				return;
		}

		if (reserve) {
			next = !next;
		}

		if (next) {
			this.b.nextMode();
		} else {
			this.b.previousMode();
		}

		ConfigManager.save();
	}
}
