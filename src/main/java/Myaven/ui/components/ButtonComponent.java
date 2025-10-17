package Myaven.ui.components;

import Myaven.config.ConfigManager;
import Myaven.module.Module;
import Myaven.setting.Setting;
import Myaven.setting.settings.BooleanSetting;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.opengl.GL11;

public class ButtonComponent extends SettingComponent {
	private Module mod;
	private BooleanSetting buttonSetting;

	@Nullable
	@Override
	public Setting getSetting() {
		return this.buttonSetting;
	}

	public ButtonComponent(Module mod, BooleanSetting op, ModuleComponent b, int o) {
		super(b);
		this.mod = mod;
		this.buttonSetting = op;
		this.a = b.categoryComponent.getX() + b.categoryComponent.getWidth();
		this.width = b.categoryComponent.getY() + b.yPos;
		this.Q = o;
	}

	public static void begin2D() {
		GL11.glDisable(2929);
		GL11.glDisable(3553);
		GL11.glBlendFunc(770, 771);
		GL11.glDepthMask(true);
		GL11.glEnable(2848);
		GL11.glHint(3154, 4354);
		GL11.glHint(3155, 4354);
	}

	public static void end2D() {
		GL11.glEnable(3553);
		GL11.glEnable(2929);
		GL11.glDisable(2848);
		GL11.glHint(3154, 4352);
		GL11.glHint(3155, 4352);
	}

	public static void drawRect(float x, float y, float x1, float y1, int c) {
		begin2D();
		applyColor(c);
		drawRect(x, y, x1, y1);
		end2D();
	}

	public static void drawRect(float x, float y, float x1, float y1) {
		GL11.glBegin(7);
		GL11.glVertex2f(x, y1);
		GL11.glVertex2f(x1, y1);
		GL11.glVertex2f(x1, y);
		GL11.glVertex2f(x, y);
		GL11.glEnd();
	}

	public static void applyColor(int h) {
		float a1pha = (float)(h >> 24 & 0xFF) / 350.0F;
		GL11.glColor4f(0.0F, 0.0F, 0.0F, a1pha);
	}

	@Override
	public void render() {
		GL11.glPushMatrix();
		GL11.glScaled(0.5, 0.5, 0.5);
		this.getFontRenderer()
			.drawString(
				(this.buttonSetting.getState() ? "[+]  " : "[-]  ") + this.buttonSetting.getDisplayName(this.mod).replace("-", " "),
				(double)((float)((this.parentModule.categoryComponent.getX() + 4) * 2)),
				(double)((float)((this.parentModule.categoryComponent.getY() + this.Q + 4) * 2)),
				this.buttonSetting.getState() ? this.textColor : this.backgroundColor,
				false
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
		if (this.isHovering(x, y) && b == 0 && this.parentModule.expanded) {
			this.buttonSetting.toggle();
			ConfigManager.save();
			this.parentModule.categoryComponent.recalculateLayout();
		}
	}

	public boolean isHovering(int x, int y) {
		return x > this.a && x < this.a + this.parentModule.categoryComponent.getWidth() && y > this.width && y < this.width + 11;
	}
}
