package Myaven.module.modules.visual;

import Myaven.events.Render2DEvent;
import Myaven.module.Category;
import Myaven.module.Module;
import Myaven.setting.Setting;
import Myaven.setting.settings.PercentageSetting;
import Myaven.setting.settings.SliderSetting;
import java.awt.Color;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class InventoryHUD extends Module {
   public static SliderSetting offsetX;
   public static SliderSetting offsetY;
   public static PercentageSetting opacity;
   private RenderItem item = this.mc.getRenderItem();

   public InventoryHUD() {
      super("InventoryHUD", false, Category.Visual, false, "Show your inventory contents on screen");
      this.addSettings(new Setting[]{offsetX, offsetY, opacity});
   }

   @SubscribeEvent
   public void onRender(Render2DEvent event) {
      int x = (int)offsetX.getRoundedValue();
      int y = (int)offsetY.getRoundedValue();
      Gui.drawRect(x - 2, y - 2, x + 162 + 2, y + 54 + 2, new Color(0, 0, 0, 255 * opacity.getPercentage() / 100).getRGB());

      for (int i = 9; i < 36; i++) {
         ItemStack itemStack = this.mc.thePlayer.inventory.mainInventory[i];
         if (itemStack != null) {
            if (itemStack.stackSize <= 1) {
               this.drawStack(itemStack, x, y);
            } else {
               this.drawStackWithText(itemStack, x, y, String.valueOf(itemStack.stackSize));
            }
         }

         x += 18;
         if (i == 17 || i == 26) {
            x = (int)offsetX.getRoundedValue();
            y += 18;
         }
      }
   }

   public void drawStack(ItemStack stack, int x, int y) {
      GlStateManager.pushMatrix();
      RenderHelper.enableGUIStandardItemLighting();
      this.item.renderItemAndEffectIntoGUI(stack, x, y);
      RenderHelper.disableStandardItemLighting();
      GlStateManager.popMatrix();
   }

   public void drawStackWithText(ItemStack stack, int x, int y, String overlayText) {
      GlStateManager.pushMatrix();
      RenderHelper.enableGUIStandardItemLighting();
      this.item.renderItemAndEffectIntoGUI(stack, x, y);
      this.item.renderItemOverlayIntoGUI(this.mc.fontRendererObj, stack, x, y, overlayText);
      RenderHelper.disableStandardItemLighting();
      GlStateManager.popMatrix();
   }

   // $VF: Irreducible bytecode was duplicated to produce valid code
   static {

      offsetX = new SliderSetting("Offset-X", 0.0, 0.0, 1000.0, 1.0);
      offsetY = new SliderSetting("Offset-Y", 0.0, 0.0, 750.0, 1.0);
      opacity = new PercentageSetting("Background-opacity", 50);
   }
}
