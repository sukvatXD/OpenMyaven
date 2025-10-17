package Myaven.ui.components;

import Myaven.Myaven;
import Myaven.module.Module;
import Myaven.module.modules.config.Language;
import Myaven.setting.Setting;
import Myaven.setting.settings.BooleanSetting;
import Myaven.setting.settings.ColorSetting;
import Myaven.setting.settings.DescriptionSetting;
import Myaven.setting.settings.ModeSetting;
import Myaven.setting.settings.PercentageSetting;
import Myaven.setting.settings.SliderSetting;
import Myaven.setting.settings.TextSetting;
import Myaven.ui.Component;
import Myaven.util.RenderUtil;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Iterator;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.opengl.GL11;

public class ModuleComponent implements Component {
	private static int p;
	private static int s;
	private static int d;
	private static int i;
	private static int c;
	private static int u;
	public static int I;
	public static int e;
	public Module module;
	public CategoryComponent categoryComponent;
	public int yPos;
	public ArrayList<SettingComponent> components;
	public boolean expanded;
	private boolean hovered;

	public ModuleComponent(Module mod, CategoryComponent p, int o) {
		this.module = mod;
		this.categoryComponent = p;
		this.yPos = o;
		this.components = new ArrayList();
		this.expanded = false;
		this.initializeSettings();
	}

	public void initializeSettings() {
		int y = this.yPos + 12;
		if (this.module != null && !this.module.getSettings().isEmpty()) {
			this.components.clear();

			for (Setting v : this.module.getSettings()) {
				if (v instanceof ModeSetting
					|| v instanceof SliderSetting
					|| v instanceof PercentageSetting
					|| v instanceof BooleanSetting
					|| v instanceof DescriptionSetting
					|| v instanceof ColorSetting
					|| v instanceof TextSetting) {
					this.components.add(SettingComponent.create(v, this, y));
					y += 12;
				}
			}
		}

		this.components.add(new BindComponent(this, y));
	}

	@Override
	public void updateHeight(int n) {
		this.yPos = n;
		int y = this.yPos + 16;

		for (SettingComponent co : this.components) {
			label24: {
				label23: {
					Setting setting = co.getSetting();
					co.updateHeight(y);
					if (!(co instanceof SliderComponent)) {
						break label23;
					}

					y += 16;
					break label24;
				}

				y += 12;
			}
		}
	}

	public static void begin2D() {
		RenderUtil.prepareGL2D();
	}

	public static void end2D() {
		RenderUtil.releaseGL2D();
		GL11.glEdgeFlag(true);
	}

	public static void setColor(int h) {
		float a = 0.0F;
		float r = 0.0F;
		float g = 0.0F;
		float b = 0.0F;
		GL11.glColor4f(0.0F, 0.0F, 0.0F, 0.0F);
	}

	public static void drawGradientRect(float x, float y, float x1, float y1, int t, int b) {
		begin2D();
		GL11.glShadeModel(7425);
		GL11.glBegin(7);
		setColor(t);
		GL11.glVertex2f(x, y1);
		GL11.glVertex2f(x1, y1);
		setColor(b);
		GL11.glVertex2f(x1, y);
		GL11.glVertex2f(x, y);
		GL11.glEnd();
		GL11.glShadeModel(7424);
		end2D();
	}

	@Override
	public void render() {
		if (this.hovered) {
			RenderUtil.drawRoundedRect(
				(float)this.categoryComponent.getX(),
				(float)(this.categoryComponent.getY() + this.yPos),
				(float)(this.categoryComponent.getX() + this.categoryComponent.getWidth()),
				(float)(this.categoryComponent.getY() + 16 + this.yPos),
				8.0F,
				s
			);
		}

		drawGradientRect(
			(float)this.categoryComponent.getX(),
			(float)(this.categoryComponent.getY() + this.yPos),
			(float)(this.categoryComponent.getX() + this.categoryComponent.getWidth()),
			(float)(this.categoryComponent.getY() + 15 + this.yPos),
			this.module.isEnabled() ? p : -12829381,
			this.module.isEnabled() ? p : -12302777
		);
		int button_rgb = u;
		if (this.module.isEnabled()) {
			button_rgb = ModuleComponent.c;
		}

		GL11.glPushMatrix();
		this.getFontRenderer()
			.drawStringWithShadow(
				this.F(),
				(double)(
					(float)((double)this.categoryComponent.getX() + (double)this.categoryComponent.getWidth() / 2.0 - this.getFontRenderer().measureTextWidth(this.F()) / 2.0)
				),
				(double)((float)(this.categoryComponent.getY() + this.yPos + 4)),
				button_rgb
			);
		GL11.glPopMatrix();
		if (this.expanded && !this.components.isEmpty()) {
			for (SettingComponent c : this.components) {
				c.render();
			}
		}
	}

	@NotNull
	@Override
	public ModuleComponent getParent() {
		return this;
	}

	@Override
	public int getHeight() {
		if (!this.expanded) {
			return 16;
		} else {
			int h = 16;

			for (SettingComponent c : this.components) {
				Setting setting = c.getSetting();
				if (c instanceof SliderComponent) {
					h += 16;
				} else {
					h += 12;
				}
			}

			return h;
		}
	}

	@Override
	public void drawScreen(int x, int y) {
		if (!this.components.isEmpty()) {
			for (SettingComponent c : this.components) {
				c.render(x, y);
			}
		}

		this.hovered = this.isHovered(x, y);
		if (this.hovered && this.categoryComponent.isOpen()) {
			Myaven.clickgui.run(() -> RenderUtil.drawTooltip(this.module.T(), x, y));
		}
	}

	public String F() {
		return Language.applyForName.getState() ? this.module.getText() : this.module.getName();
	}

	@Override
	public void onClick(int x, int y, int b) {
		if (this.isHovered(x, y) && b == 0 && !this.module.getName().equalsIgnoreCase("freelook")) {
			this.module.toggle();
		}

		if (this.isHovered(x, y) && b == 1) {
			this.expanded = !this.expanded;
			this.categoryComponent.recalculateLayout();
		}

		for (SettingComponent c : this.components) {
			c.onClick(x, y, b);
		}
	}

	@Override
	public void mouseReleased(int x, int y, int m) {

        for (SettingComponent c : this.components) {
            c.mouseReleased(x, y, m);
        }
	}

	@Override
	public void keyTyped(char t, int k) {

        for (SettingComponent c : this.components) {
            c.keyTyped(t, k);
        }
	}

	@Override
	public void onGuiClosed() {

        for (SettingComponent c : this.components) {
            c.onGuiClosed();
        }
	}

	public boolean isHovered(int x, int y) {
		return x > this.categoryComponent.getX()
			&& x < this.categoryComponent.getX() + this.categoryComponent.getWidth()
			&& y > this.categoryComponent.getY() + this.yPos
			&& y < this.categoryComponent.getY() + 16 + this.yPos;
	}

	static {

		p = new Color(154, 2, 255).getRGB();
		s = new Color(0, 0, 0, 110).getRGB();
		d = new Color(114, 188, 250).getRGB();
		i = new Color(255, 80, 80).getRGB();
		c = new Color(24, 154, 255).getRGB();
		u = new Color(192, 192, 192).getRGB();
		I = new Color(255, 255, 255, 0).getRGB();
		e = new Color(255, 255, 255).getRGB();
	}
}
