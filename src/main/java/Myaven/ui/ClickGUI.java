package Myaven.ui;

import Myaven.Myaven;
import Myaven.module.Category;
import Myaven.setting.Setting;
import Myaven.setting.settings.ColorSetting;
import Myaven.setting.settings.ModeSetting;
import Myaven.ui.components.BindComponent;
import Myaven.ui.components.CategoryComponent;
import Myaven.ui.components.ModuleComponent;
import Myaven.ui.components.SettingComponent;
import Myaven.ui.font.IFontRenderer;
import Myaven.ui.font.VanillaFontRenderer;
import Myaven.util.AnimationTimer;
import Myaven.util.GradientUtil;
import Myaven.util.RenderUtil;
import Myaven.util.GradientUtil.GradientDirection;
import java.awt.Color;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.ScaledResolution;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

public class ClickGUI extends GuiScreen {
	private ScheduledFuture<?> f;
	private AnimationTimer g;
	private AnimationTimer Q;
	private AnimationTimer C;
	private AnimationTimer p;
	private ScaledResolution F;
	private GuiTextField commandLineInput;
	public static Map<Category, CategoryComponent> u;
	public static List<Category> d;
	private Runnable L;
	private GradientUtil D;
	public static int H;
	public static ModeSetting S = new ModeSetting("Text-color","CUSTOM","THEME", "THEME_CUSTOM");
	public static ColorSetting c = new ColorSetting("Custom-color","00FF7F");
	private int s;
	private static String G;
	private static long[] a;
	private static Integer[] b;
	private static long[] e;
	private static Long[] h;

	public ClickGUI() {
		super();
		this.L = null;
		this.D = new GradientUtil(GradientDirection.LR);
		this.s = 0;
		int y = 5;
		Category[] values;
		int length = (values = Category.values()).length;
		u = new HashMap(length);
		d = new ArrayList(length);
		int i = 0;

		while (i < length) {
			Category c = values[i];
			CategoryComponent f = new CategoryComponent(c);
			f.setX(y);
			u.put(c, f);
			d.add(c);
			y += 20;
			i++;
			if (a != null) {
				break;
			}
		}
	}

	public static IFontRenderer getFont() {
		return VanillaFontRenderer.getInstance();
	}

	public void run(Runnable task) {
		this.L = task;
	}

	public void Q() {
		(this.g = this.C = this.p = new AnimationTimer(500.0F)).reset();
		this.f = Myaven.a().schedule(() -> (this.Q = new AnimationTimer(650.0F)).reset(), 650L, TimeUnit.MILLISECONDS);
	}

	public void initGui() {
		super.initGui();
		this.F = new ScaledResolution(this.mc);
		(this.commandLineInput = new GuiTextField(1, this.mc.fontRendererObj, 22, this.height - 100, 150, 20)).setMaxStringLength(256);
	}

	public void drawScreen(int x, int y, float p) {
		if (this.s != 0) {
			int step = (int)((double)this.s * 0.15);
			if (step == 0) {
				this.s = 0;
			} else {
				for (CategoryComponent category : u.values()) {
					category.setX(category.getY() + step);
				}

				this.s -= step;
			}
		}

		this.D.drawGradient(0, 0, this.width, this.height);
		this.D.renderGradient(0.0F, 0.0F, (float)this.width, (float)this.height, 1.0F, 0.1F);
		int h = this.height / 3;
		int wd = this.width / 2;
		int w_c = 30 - this.g.interpolateInt(0, 30, 3);
		getFont().S("M", (double)(wd + 1 - w_c - 20), (double)h, RenderUtil.getChromaColor(2L, 1500L));
		getFont().S("y", (double)(wd - w_c - 12), (double)h, RenderUtil.getChromaColor(2L, 1200L));
		getFont().S("a", (double)(wd - w_c - 4), (double)h, RenderUtil.getChromaColor(2L, 900L));
		getFont().S("v", (double)(wd - w_c + 4), (double)h, RenderUtil.getChromaColor(2L, 900L));
		getFont().S("e", (double)(wd - w_c + 12), (double)h, RenderUtil.getChromaColor(2L, 600L));
		getFont().S("n", (double)(wd - w_c + 20), (double)h, RenderUtil.getChromaColor(2L, 300L));
		this.drawVerticalLine(wd - 27 - w_c, h - 4, h + 12, Color.white.getRGB());
		this.drawVerticalLine(wd + 27 + w_c, h - 4, h + 12, Color.white.getRGB());
		if (this.Q != null) {
			int var15 = this.Q.interpolateInt(0, 54, 2);
			this.drawHorizontalLine(wd - 27, wd - 27 + var15, h - 4, -1);
			this.drawHorizontalLine(wd + 27, wd + 27 - var15, h + 12, -1);
		}

		for (Category category : d) {
			CategoryComponent c = (CategoryComponent)u.get(category);
			c.render(getFont());
			c.handleDrag(x, y);

			for (Component m : c.getModuleComponents()) {
				m.render(x, y);
			}
		}

		GL11.glColor3f(1.0F, 1.0F, 1.0F);
		if (this.L != null) {
			this.L.run();
		}

		this.L = null;
	}

	public void handleMouseInput() throws IOException {
		super.handleMouseInput();
		int dWheel = Mouse.getDWheel();
		if (dWheel != 0) {
			this.Y(dWheel);
		}
	}

	public void Y(int dWheel) {
		if (dWheel > 0) {
			this.s += 30;
		} else if (dWheel < 0) {
			this.s -= 30;
		}
	}

	public void mouseClicked(int x, int y, int m) throws IOException {
		Iterator var4 = d.stream().map(categoryx -> (CategoryComponent)u.get(categoryx)).iterator();

		label98:
		while (true) {
			CategoryComponent category = null;

			while (var4.hasNext()) {
				category = (CategoryComponent)var4.next();
				if (category.isHeaderHovered(x, y) && !category.isCloseButton(x, y) && !category.isCollapseButton(x, y) && m == 0) {
					category.setDragging(true);
					category.xx = x - category.getX();
					category.yy = y - category.getY();
				}

				if (category.isCollapseButton(x, y) && m == 0 || (category.isHeaderHovered(x, y) || category.isCollapseButton(x, y)) && m == 1) {
					category.toggleOpen(!category.isOpened());
				}

				if (category.isCloseButton(x, y) && m == 0) {
					category.setToggled(!category.isToggled());
				}

				if (category.isOpened() && !category.getModuleComponents().isEmpty()) {
					for (Component c : category.getModuleComponents()) {
						c.onClick(x, y, m);
					}
					continue label98;
				}
			}

			if (category != null) {
				d.remove(category.category);
				d.add(category.category);
			}

			return;
		}
	}

	public void mouseReleased(int x, int y, int s) {
		if (s == 0) {
			for (CategoryComponent category : u.values()) {
				category.setDragging(false);
				if (category.isOpened() && !category.getModuleComponents().isEmpty()) {
					for (Component module : category.getModuleComponents()) {
						module.mouseReleased(x, y, s);
					}
				}
			}
		}
	}

	public void keyTyped(char t, int k) {
		if (k == 1 && !this.n()) {
			this.mc.displayGuiScreen(null);
		} else {
			for (CategoryComponent category : u.values()) {
				if (category.isOpened() && !category.getModuleComponents().isEmpty()) {
					for (Component module : category.getModuleComponents()) {
						module.keyTyped(t, k);
					}
				}
			}
		}
	}

	public void onGuiClosed() {
		this.Q = null;
		if (this.f != null) {
			this.f.cancel(true);
			this.f = null;
		}

		for (CategoryComponent c : u.values()) {
			c.dragging = false;

			for (Component m : c.getModuleComponents()) {
				m.onGuiClosed();
			}
		}
	}

	public boolean doesGuiPauseGame() {
		return false;
	}

	private boolean n() {

		for (CategoryComponent c : u.values()) {
			for (ModuleComponent m : c.getModuleComponents()) {
				for (SettingComponent component : m.components) {
					if (component instanceof BindComponent && ((BindComponent)component).isBinding) {
						return true;
					}
				}
			}
		}

		return false;
	}

	public static void S() {
		byte xOffSet = 5;
		int yOffSet = 5;

		for (CategoryComponent category : u.values()) {
			category.setOpened(false);
			category.setY(xOffSet);
			category.setX(yOffSet);
			xOffSet += 100;
			if (xOffSet > 400) {
				xOffSet = 5;
				yOffSet += 120;
			}
		}
	}

}
