package Myaven.module.modules.visual;

import Myaven.module.Category;
import Myaven.module.Module;
import Myaven.setting.Setting;
import Myaven.setting.settings.BooleanSetting;
import Myaven.setting.settings.PercentageSetting;
import Myaven.setting.settings.SliderSetting;
import Myaven.util.ClientUtil;
import Myaven.util.RenderUtil;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.opengl.GL11;

public class ItemESP extends Module {
   public static SliderSetting scale;
   public static PercentageSetting opacity;
   public static BooleanSetting enableBedwarsResources;
   public static BooleanSetting renderDiamondArmor;
   public static BooleanSetting renderGoldenApples;
   private static String[] a;
   private static String[] b;
   private static long[] d;
   private static Integer[] g;

   public ItemESP() {
      super("ItemESP", false, Category.Visual, false, "Render a box on items");
      this.addSettings(new Setting[]{scale, opacity, enableBedwarsResources, renderDiamondArmor, renderGoldenApples});
   }

   @SubscribeEvent
   public void onRender(RenderWorldLastEvent event) {
      HashMap hashMap = new HashMap();
      HashMap<Double, Integer> hashMap2 = new HashMap<>();

      for (Entity entity : this.mc.theWorld.loadedEntityList) {
         if (entity instanceof EntityItem && entity.ticksExisted >= 3) {
            EntityItem entityItem = (EntityItem)entity;
            if (entityItem.getEntityItem().stackSize != 0) {
               Item getItem = entityItem.getEntityItem().getItem();
               if (getItem != null) {
                  int stackSize = entityItem.getEntityItem().stackSize;
                  double a = computeItemKey(getItem, entity.posX, entity.posY, entity.posZ);
                  Integer n = hashMap2.get(a);
                  int n2;
                  if (n == null) {
                     n2 = stackSize;
                     ArrayList<EntityItem> list = (ArrayList<EntityItem>)hashMap.get(getItem);
                     if (list == null) {
                        list = new ArrayList<>();
                     }

                     list.add(entityItem);
                     hashMap.put(getItem, list);
                  } else {
                     n2 = n + stackSize;
                  }

                  hashMap2.put(a, n2);
               }
            }
         }
      }

      if (!hashMap.isEmpty()) {
         float renderPartialTicks = ClientUtil.getRenderPartialTicks();
         Iterator var28 = hashMap.entrySet().iterator();

         while (true) {
            Entry<Item, ArrayList<EntityItem>> entry;
            Item item;
            int n4;
            int n3;
            while (true) {
               if (!var28.hasNext()) {
                  return;
               }

               entry = (Entry<Item, ArrayList<EntityItem>>)var28.next();
               item = entry.getKey();
               if (item == Items.iron_ingot && enableBedwarsResources.getState()) {
                  n4 = -1;
                  n3 = -1;
                  break;
               }

               if (item == Items.gold_ingot && enableBedwarsResources.getState()) {
                  n4 = -331703;
                  n3 = -152;
                  break;
               }

               if ((item != Items.diamond || !enableBedwarsResources.getState())
                  && (
                     item != Items.diamond_helmet && item != Items.diamond_chestplate && item != Items.diamond_leggings && item != Items.diamond_boots
                        || !renderDiamondArmor.getState()
                  )) {
                  if (item == Items.golden_apple && renderGoldenApples.getState()) {
                     n4 = -331703;
                     n3 = -152;
                     break;
                  }

                  if (item != Items.emerald || !enableBedwarsResources.getState()) {
                     continue;
                  }

                  n4 = -15216030;
                  n3 = -14614644;
                  break;
               }

               n4 = -10362113;
               n3 = -7667713;
               break;
            }

            for (EntityItem entityItem2 : entry.getValue()) {
               double a2 = computeItemKey(item, entityItem2.posX, entityItem2.posY, entityItem2.posZ);
               double n5 = entityItem2.lastTickPosX + (entityItem2.posX - entityItem2.lastTickPosX) * (double)renderPartialTicks;
               double n6 = entityItem2.lastTickPosY + (entityItem2.posY - entityItem2.lastTickPosY) * (double)renderPartialTicks;
               double n7 = entityItem2.lastTickPosZ + (entityItem2.posZ - entityItem2.lastTickPosZ) * (double)renderPartialTicks;
               double n8 = this.mc.thePlayer.lastTickPosX + (this.mc.thePlayer.posX - this.mc.thePlayer.lastTickPosX) * (double)renderPartialTicks - n5;
               double n9 = this.mc.thePlayer.lastTickPosY + (this.mc.thePlayer.posY - this.mc.thePlayer.lastTickPosY) * (double)renderPartialTicks - n6;
               double n10 = this.mc.thePlayer.lastTickPosZ + (this.mc.thePlayer.posZ - this.mc.thePlayer.lastTickPosZ) * (double)renderPartialTicks - n7;
               GlStateManager.pushMatrix();
               renderItemBoxAndCount(n4, n3, hashMap2.get(a2), n5, n6, n7, (double)MathHelper.sqrt_double(n8 * n8 + n9 * n9 + n10 * n10));
               GlStateManager.popMatrix();
            }
         }
      }
   }

   public static double computePositionKey(double n, double n2, double n3) {
      if (n == 0.0) {
         n = 1.0;
      }

      if (n2 == 0.0) {
         n2 = 1.0;
      }

      if (n3 == 0.0) {
         n3 = 1.0;
      }

      return (double)Math.round((n + 1.0) * Math.floor(n2) * (n3 + 2.0));
   }

   private static double computeItemKey(Item item, double n, double n2, double n3) {
      double c = computePositionKey(n, n2, n3);
      if (item == Items.iron_ingot) {
         c += 0.155;
      } else if (item == Items.gold_ingot) {
         c += 0.255;
      } else if (item == Items.diamond) {
         c += 0.355;
      } else if (item == Items.emerald) {
         c += 0.455;
      }

      return c;
   }

   public static void renderItemBoxAndCount(int color, int textColor, int count, double x, double y, double z, double size) {
      Minecraft mc = Minecraft.getMinecraft();
      x -= mc.getRenderManager().viewerPosX;
      y -= mc.getRenderManager().viewerPosY;
      z -= mc.getRenderManager().viewerPosZ;
      GL11.glPushMatrix();
      GL11.glBlendFunc(770, 771);
      GL11.glEnable(3042);
      GL11.glLineWidth(2.0F);
      GL11.glDisable(3553);
      GL11.glDisable(2929);
      GL11.glDepthMask(false);
      float r = (float)(color >> 16 & 0xFF) / 255.0F;
      float g = (float)(color >> 8 & 0xFF) / 255.0F;
      float b = (float)(color & 0xFF) / 255.0F;
      float baseSize = Math.min(Math.max(0.2F, (float)(0.01 * size)), 0.4F);
      AxisAlignedBB box = new AxisAlignedBB(
         x - (double)baseSize, y, z - (double)baseSize, x + (double)baseSize, y + (double)baseSize * 2.0, z + (double)baseSize
      );
      double centerX = (box.minX + box.maxX) / 2.0;
      double centerY = (box.minY + box.maxY) / 2.0;
      double centerZ = (box.minZ + box.maxZ) / 2.0;
      double s = scale.getRoundedValue();
      double halfX = (box.maxX - box.minX) / 2.0 * s;
      double halfY = (box.maxY - box.minY) / 2.0 * s;
      double halfZ = (box.maxZ - box.minZ) / 2.0 * s;
      AxisAlignedBB scaled = new AxisAlignedBB(centerX - halfX, centerY - halfY, centerZ - halfZ, centerX + halfX, centerY + halfY, centerZ + halfZ);
      RenderUtil.renderFilledBox(scaled, r, g, b, (float)opacity.getPercentage() / 100.0F);
      GL11.glEnable(3553);
      GL11.glEnable(2929);
      GL11.glDepthMask(true);
      GL11.glDisable(3042);
      GL11.glPopMatrix();
      GlStateManager.pushMatrix();
      GlStateManager.translate((float)x, (float)y + 0.3F, (float)z);
      GlStateManager.rotate(-mc.getRenderManager().playerViewY, 0.0F, 1.0F, 0.0F);
      GlStateManager.rotate(mc.getRenderManager().playerViewX, 1.0F, 0.0F, 0.0F);
      float minScale = Math.min(Math.max(0.02266667F, (float)(0.0015 * size)), 0.07F);
      float totalScale = minScale * (float)s * 1.5F;
      GlStateManager.scale(-totalScale, -totalScale, -totalScale);
      GlStateManager.depthMask(false);
      GlStateManager.disableDepth();
      String value = String.valueOf(count);
      float xOffset = -((float)mc.fontRendererObj.getStringWidth(value) / 2.0F);
      float yOffset = -((float)mc.fontRendererObj.FONT_HEIGHT / 2.35F);
      drawOutlinedText(value, xOffset, yOffset, textColor, -16777216);
      GlStateManager.enableDepth();
      GlStateManager.depthMask(true);
      GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
      GlStateManager.popMatrix();
   }

   public static void drawOutlinedText(String text, float x, float y, int color, int outlineColor) {
      Minecraft mc = Minecraft.getMinecraft();
      mc.fontRendererObj.drawString(text, x - 0.75F, y - 0.75F, outlineColor, false);
      mc.fontRendererObj.drawString(text, x, y - 0.75F, outlineColor, false);
      mc.fontRendererObj.drawString(text, x + 0.75F, y - 0.75F, outlineColor, false);
      mc.fontRendererObj.drawString(text, x - 0.75F, y, outlineColor, false);
      mc.fontRendererObj.drawString(text, x + 0.75F, y, outlineColor, false);
      mc.fontRendererObj.drawString(text, x - 0.75F, y + 0.75F, outlineColor, false);
      mc.fontRendererObj.drawString(text, x, y + 0.75F, outlineColor, false);
      mc.fontRendererObj.drawString(text, x + 0.75F, y + 0.75F, outlineColor, false);
      mc.fontRendererObj.drawString(text, x, y, color, false);
   }

   // $VF: Irreducible bytecode was duplicated to produce valid code
   static {

      scale = new SliderSetting("Scale", 1.0, 0.01, 5.0, 0.01);
      opacity = new PercentageSetting("Opacity", 60);
      enableBedwarsResources = new BooleanSetting("Bedwars-resources", true);
      renderDiamondArmor = new BooleanSetting("Render-diamond-armors", true);
      renderGoldenApples = new BooleanSetting("Render-golden-apples", true);
   }
}
