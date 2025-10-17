package Myaven.ui.components;

import Myaven.Myaven;
import Myaven.module.Category;
import Myaven.module.Module;
import Myaven.module.modules.config.Theme;
import Myaven.setting.Setting;
import Myaven.ui.Component;
import Myaven.ui.font.IFontRenderer;
import Myaven.util.Animation;
import Myaven.util.AnimationTimer;
import Myaven.util.Easing;
import Myaven.util.RenderUtil;
import java.awt.Color;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import org.lwjgl.opengl.GL11;

public class CategoryComponent {
	public List<ModuleComponent> moduleComponents = new CopyOnWriteArrayList();
	public Category category;
	private boolean opened;
	private AnimationTimer timer;
	private int width;
	private int x;
	private int y;
	private int titleHeight;
	public boolean dragging;
	public int xx;
	public int yy;
	public boolean hovering = false;
	public String hoverName;
	public boolean toggled = false;
	public boolean isHovered = false;
	private Animation openAnimation;
	public int scaleFactor;
	private float expandedHeight;

	private static int C;
	private static int xb;
	private static int v;
	private static int p;
	private static int d;
	private static int A;
	private static int z;
	private static int Y;
	private static int i;
	private static int r;

	public CategoryComponent(Category category) {
		this.category = category;
		this.width = 92;
		this.y = 5;
		this.x = 5;
		this.titleHeight = 13;
		this.timer = null;
		this.xx = 0;
		this.opened = false;
		this.dragging = false;
		int tY = this.titleHeight + 3;
		this.scaleFactor = new ScaledResolution(Minecraft.getMinecraft()).getScaleFactor();
		this.openAnimation = new Animation(Easing.EASE_OUT_QUART, 600L);

		for (Module mod : Myaven.moduleManager.I(this.category)) {
			ModuleComponent b = new ModuleComponent(mod, this, tY);
			this.moduleComponents.add(b);
			tY += 16;
		}
	}

	public void refresh(boolean isProfile) {
		this.moduleComponents.clear();
		this.titleHeight = 13;
		int tY = this.titleHeight + 3;
		this.recalculateLayout();
	}

	public void setY(int n) {
		this.y = n;
	}

	public void setX(int y) {
		this.x = y;
	}

	public void setDragging(boolean d) {
		this.dragging = d;
	}

	public boolean isToggled() {
		return this.toggled;
	}

	public void setToggled(boolean on) {
		this.toggled = on;
	}

	public boolean isOpened() {
		return this.opened;
	}

	public void setOpened(boolean open) {
		this.opened = open;
	}

	public void toggleOpen(boolean on) {
		this.opened = on;
		(this.timer = new AnimationTimer(600.0F)).reset();
		this.openAnimation.k();
		this.openAnimation.h(1.0);
	}

	public void render(IFontRenderer renderer) {
		this.width = 92;
		int h = 0;
		if (!this.moduleComponents.isEmpty() && this.opened) {
			for (Component c : this.moduleComponents) {
				h += c.getHeight();
			}

			this.expandedHeight = (float)h;
		}

		this.openAnimation.k(this.opened ? 1.0 : 0.0);
		float animationProgress = (float)this.openAnimation.q();
		float extra = (float)(this.x + this.titleHeight + 4) + (float)h * animationProgress;
		if (!this.opened) {
			if (this.timer == null) {
				extra = (float)(this.x + this.titleHeight) + (float)h * animationProgress + 4.0F;
			} else {
				float smoothValue = this.timer.interpolate(0.0F, this.expandedHeight, 1);
				extra = (float)(this.x + this.titleHeight + 4) + this.expandedHeight - smoothValue;
			}
		}

		GL11.glPushMatrix();
		GL11.glEnable(3089);
		RenderUtil.applyScissor(0.0, (double)(this.x - 2), (double)(this.y + this.width + 4), (double)(extra - (float)this.x + 4.0F));
		RenderUtil.drawRoundedRectBordered(
			(float)(this.y - 2),
			(float)this.x,
			(float)(this.y + this.width + 2),
			extra,
			10.0F,
			C,
			(this.opened || this.isHovered) && Theme.themeSelection.getCurrent().equalsIgnoreCase("RAINBOW")
				? RenderUtil.applyAlphaToColor(RenderUtil.getChromaColor(2L, 0L), 0.5)
				: Theme.computeThemeColor(0.0),
			(this.opened || this.isHovered) && Theme.themeSelection.getCurrent().equalsIgnoreCase("RAINBOW")
				? RenderUtil.applyAlphaToColor(RenderUtil.getChromaColor(2L, 700L), 0.5)
				: Theme.computeThemeColor(0.0)
		);
		this.drawCategoryIcon(this.category, this.y + 1, this.x + 4, this.opened || this.isHovered);
		renderer.drawString(this.hovering ? this.hoverName : this.category.b(), (double)((float)(this.y + 12)), (double)((float)(this.x + 4)), d, false);
		if (!this.hovering) {
			GL11.glPushMatrix();
			renderer.drawString(this.opened ? "-" : "+", (double)((float)(this.y + 80)), (double)((float)((double)this.x + 4.5)), this.opened ? A : z, false);
			GL11.glPopMatrix();
			if (this.opened && !this.moduleComponents.isEmpty()) {
				for (ModuleComponent module : this.moduleComponents) {
					module.render();
				}
			}
		}

		GL11.glDisable(3089);
		GL11.glPopMatrix();
	}

	private void drawCategoryIcon(Category category, int x, int y, boolean enchant) {
		RenderItem renderItem = Minecraft.getMinecraft().getRenderItem();
		double scale = 0.55;
		GlStateManager.pushMatrix();
		GlStateManager.scale(0.55, 0.55, 0.55);
		ItemStack itemStack = null;
		switch (category) {
			case Combat:
				itemStack = new ItemStack(Items.diamond_sword);
				break;
			case Movement:
				itemStack = new ItemStack(Items.feather);
				break;
			case Player:
				itemStack = new ItemStack(Items.skull, 1, 3);
				break;
			case World:
				itemStack = new ItemStack(Item.getItemFromBlock(Blocks.grass));
				break;
			case Visual:
				itemStack = new ItemStack(Items.ender_eye);
				break;
			case Misc:
				itemStack = new ItemStack(Items.gunpowder);
				break;
			case Configuration:
				itemStack = new ItemStack(Items.iron_ingot);
		}

		if (itemStack != null) {
			if (enchant && category != Category.Player) {
				itemStack.addEnchantment(Enchantment.unbreaking, 2);
			}

			RenderHelper.enableGUIStandardItemLighting();
			GlStateManager.disableBlend();
			renderItem.renderItemAndEffectIntoGUI(itemStack, (int)((double)x / 0.55), (int)((double)y / 0.55));
			GlStateManager.enableBlend();
			RenderHelper.disableStandardItemLighting();
		}

		GlStateManager.scale(1.0F, 1.0F, 1.0F);
		GlStateManager.popMatrix();
	}

	public void recalculateLayout() {
		int o = this.titleHeight + 3;

        for (ModuleComponent moduleComponent : this.moduleComponents) {
            Component c = (Component) moduleComponent;
            c.updateHeight(o);
            o += c.getHeight();
        }
	}

	public int getWidth() {
		return this.width;
	}

	public void handleDrag(int x, int y) {
		if (this.dragging) {
			this.setY(x - this.xx);
			this.setX(y - this.yy);
		}

		this.isHovered = this.isHovered(x, y);
	}

	public boolean isCloseButton(int x, int y) {
		return x >= this.y + 92 - 13 && x <= this.y + this.width && (float)y >= (float)this.x + 2.0F && y <= this.x + this.titleHeight + 1;
	}

	public boolean isCollapseButton(int x, int y) {
		return x >= this.y + 77 && x <= this.y + this.width - 6 && (float)y >= (float)this.x + 2.0F && y <= this.x + this.titleHeight + 1;
	}

	public boolean isHovered(int x, int y) {
		return x >= this.y - 2 && x <= this.y + this.width + 2 && (float)y >= (float)this.x + 2.0F && y <= this.x + this.titleHeight + 1;
	}

	public boolean isHeaderHovered(int x, int y) {
		return x >= this.y && x <= this.y + this.width && y >= this.x && y <= this.x + this.titleHeight;
	}

	public List<ModuleComponent> getModuleComponents() {
		return this.moduleComponents;
	}

	public boolean isOpen() {
		return this.opened;
	}

	public void setOpen(boolean categoryOpened) {
		this.opened = categoryOpened;
	}

	public int getY() {
		return this.x;
	}

	public void setX2(int y) {
		this.x = y;
	}

	public int getX() {
		return this.y;
	}

	public void setY2(int x) {
		this.y = x;
	}

	static {
		C = new Color(0, 0, 0, 110).getRGB();
		xb = new Color(0, 0, 0, 255).getRGB();
		v = new Color(81, 99, 149).getRGB();
		p = new Color(97, 67, 133).getRGB();
		d = new Color(220, 220, 220).getRGB();
		A = new Color(250, 95, 85).getRGB();
		z = new Color(135, 238, 144).getRGB();
		Y = new Color(210, 210, 210, 200).getRGB();
		i = new Color(210, 210, 210, 255).getRGB();
		r = new Color(100, 100, 100).getRGB();
	}
}
