package Myaven.ui.components;

import Myaven.config.ConfigManager;
import Myaven.module.Module;
import Myaven.module.modules.config.Theme;
import Myaven.setting.Setting;
import Myaven.setting.settings.PercentageSetting;
import Myaven.setting.settings.SliderSetting;
import Myaven.util.ClientUtil;
import Myaven.util.RenderUtil;
import org.lwjgl.opengl.GL11;

public class SliderComponent extends SettingComponent {
	private SliderSetting A;
	private PercentageSetting J;
	private int t;
	private int b;
	private int z;
	private boolean V = false;
	private double O;
	private Module p;
	private static String[] f;
	private static String[] g;
	private static long[] j;
	private static Integer[] k;

	public SliderComponent(SliderSetting sliderSetting, ModuleComponent moduleComponent, int o) {
		super(moduleComponent);
		this.J = null;
		this.A = sliderSetting;
		this.p = moduleComponent.module;
		this.b = moduleComponent.categoryComponent.getX() + moduleComponent.categoryComponent.getWidth();
		this.z = moduleComponent.categoryComponent.getY() + moduleComponent.yPos;
		this.t = o;
	}

	public SliderComponent(PercentageSetting sliderSetting, ModuleComponent moduleComponent, int o) {
		super(moduleComponent);
		this.A = null;
		this.J = sliderSetting;
		this.p = moduleComponent.module;
		this.b = moduleComponent.categoryComponent.getX() + moduleComponent.categoryComponent.getWidth();
		this.z = moduleComponent.categoryComponent.getY() + moduleComponent.yPos;
		this.t = o;
	}

	@Override
	public Setting getSetting() {
		return (Setting)(this.A != null ? this.A : this.J);
	}

	@Override
	public void render() {
		RenderUtil.drawRoundedRect(
			(float)(this.parentModule.categoryComponent.getX() + 4),
			(float)(this.parentModule.categoryComponent.getY() + this.t + 11),
			(float)(this.parentModule.categoryComponent.getX() + 4 + this.parentModule.categoryComponent.getWidth() - 8),
			(float)(this.parentModule.categoryComponent.getY() + this.t + 15),
			3.0F,
			-12302777
		);
		int l = this.parentModule.categoryComponent.getX() + 4;
		int r = this.parentModule.categoryComponent.getX() + 4 + (int)this.O;
		if (r - l > 84) {
			r = l + 84;
		}

		RenderUtil.drawRoundedRect(
			(float)l,
			(float)(this.parentModule.categoryComponent.getY() + this.t + 11),
			(float)r,
			(float)(this.parentModule.categoryComponent.getY() + this.t + 15),
			3.0F,
			Theme.computeThemeColor(0.0)
		);
		GL11.glPushMatrix();
		GL11.glScaled(0.5, 0.5, 0.5);
		String value;
		if (this.A != null) {
			double input = this.A.getRoundedValue();
			value = this.A.getDisplayName(this.p).replaceAll("-", " ") + ": " + (ClientUtil.isWholeNumber(input) ? (int)input + "" : String.valueOf(input));
		} else {
			int input = this.J.getPercentage();
			value = this.J.getDisplayName(this.p).replaceAll("-", " ") + ": " + input + "%";
		}

		this.getFontRenderer()
			.drawString(
				value,
				(double)((float)((int)((float)(this.parentModule.categoryComponent.getX() + 4) * 2.0F))),
				(double)((float)((int)((float)(this.parentModule.categoryComponent.getY() + this.t + 3) * 2.0F))),
				this.backgroundColor,
				true
			);
		GL11.glPopMatrix();
	}

	@Override
	public void updateHeight(int n) {
		this.t = n;
	}

	@Override
	public void drawScreen(int x, int y) {
		this.z = this.parentModule.categoryComponent.getY() + this.t;
		this.b = this.parentModule.categoryComponent.getX();
		double d = (double)Math.min(this.parentModule.categoryComponent.getWidth() - 8, 0 >= x - this.b ? 0 : x - this.b);
		if (this.A != null) {
			this.O = (double)(this.parentModule.categoryComponent.getWidth() - 8)
				* (this.A.getRoundedValue() - this.A.getMinValue())
				/ (this.A.getMaxValue() - this.A.getMinValue());
		} else {
			this.O = (double)(this.parentModule.categoryComponent.getWidth() - 8) * (double)this.J.getPercentage() / 100.0;
		}

		if (this.V) {
			if (d == 0.0) {
				if (this.A != null) {
					this.A.setValue(this.A.getMinValue());
				} else {
					this.J.setPercentageClamped(0);
				}

				ConfigManager.save();
				this.parentModule.categoryComponent.recalculateLayout();
			} else {
				if (this.A != null) {
					double n = R(
						d / (double)(this.parentModule.categoryComponent.getWidth() - 8) * (this.A.getMaxValue() - this.A.getMinValue()) + this.A.getMinValue(),
						this.A.getStepSize()
					);
					this.A.setValue(n);
				} else {
					double n = R(d / (double)(this.parentModule.categoryComponent.getWidth() - 8) * 100.0, 1.0);
					this.J.setPercentageClamped((int)n);
				}

				ConfigManager.save();
				this.parentModule.categoryComponent.recalculateLayout();
			}
		}
	}

	private static double R(double v, double in) {
		return (double)Math.round(v / in) * in;
	}

	@Override
	public void onClick(int x, int y, int b) {
		if (this.getSetting() != null) {
			if (this.Y(x, y) && b == 0 && this.parentModule.expanded) {
				this.V = true;
			}

			if (this.P(x, y) && b == 0 && this.parentModule.expanded) {
				this.V = true;
			}
		}
	}

	@Override
	public void mouseReleased(int x, int y, int m) {
		this.V = false;
	}

	public boolean Y(int x, int y) {
		return x > this.b && x < this.b + this.parentModule.categoryComponent.getWidth() / 2 + 1 && y > this.z && y < this.z + 16;
	}

	public boolean P(int x, int y) {
		return x > this.b + this.parentModule.categoryComponent.getWidth() / 2
			&& x < this.b + this.parentModule.categoryComponent.getWidth()
			&& y > this.z
			&& y < this.z + 16;
	}

	@Override
	public void onGuiClosed() {
		this.V = false;
	}

}
