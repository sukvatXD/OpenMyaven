package Myaven.ui.components;

import Myaven.module.Module;
import Myaven.module.modules.config.Theme;
import Myaven.setting.Setting;
import Myaven.setting.settings.DescriptionSetting;
import Myaven.ui.ClickGUI;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.opengl.GL11;

public class DescriptionComponent extends SettingComponent {
	private DescriptionSetting X;
	private Module j;
	private String H = null;

	@Nullable
	@Override
	public Setting getSetting() {
		return this.X;
	}

	public DescriptionComponent(DescriptionSetting desc, ModuleComponent b, int o) {
		super(b);
		this.X = desc;
		this.j = b.module;
		this.a = b.categoryComponent.getX() + b.categoryComponent.getWidth();
		this.width = b.categoryComponent.getY() + b.yPos;
		this.Q = o;
	}

	public DescriptionComponent(DescriptionSetting desc, ModuleComponent b, int o, String customString) {
		super(b);
		this.X = desc;
		this.j = b.module;
		this.a = b.categoryComponent.getX() + b.categoryComponent.getWidth();
		this.width = b.categoryComponent.getY() + b.yPos;
		this.Q = o;
		this.H = customString;
	}

	@Override
	public void render() {
		GL11.glPushMatrix();
		GL11.glScaled(0.5, 0.5, 0.5);
		int color = (int)16777215;
		String draw = ClickGUI.S.getCurrent();
		switch (draw) {
			case "THEME":
				color = Theme.computeThemeColor(0.0);
				break;
			case "THEME_CUSTOM":
				color = Theme.computeCustomThemeColor(0.0);
				break;
			case "CUSTOM":
				color = ClickGUI.c.F();
		}

		if (this.H != null) {
			draw = this.H;
		} else {
			draw = this.X.getText(this.j);
		}

		this.getFontRenderer()
			.drawString(
				draw,
				(double)((float)((this.parentModule.categoryComponent.getX() + 4) * 2)),
				(double)((float)((this.parentModule.categoryComponent.getY() + this.Q + 4) * 2)),
				color,
				true
			);
		GL11.glPopMatrix();
	}

	@Override
	public void updateHeight(int n) {
		this.Q = n;
	}
}
