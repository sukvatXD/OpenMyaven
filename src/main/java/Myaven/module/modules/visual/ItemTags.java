package Myaven.module.modules.visual;

import Myaven.Myaven;
import Myaven.module.Category;
import Myaven.module.Module;
import Myaven.setting.Setting;
import Myaven.setting.settings.BooleanSetting;
import Myaven.setting.settings.PercentageSetting;
import Myaven.setting.settings.SliderSetting;
import Myaven.util.ItemUtil;
import java.awt.Color;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.StringUtils;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.opengl.GL11;

public class ItemTags extends Module {
   public static SliderSetting scale;
   public static PercentageSetting opacity;
   public static BooleanSetting megawalls;
   public static BooleanSetting bedwars;
   public static BooleanSetting swordAndBows;
   public static BooleanSetting blocks;
   public static BooleanSetting gApple;
   public static BooleanSetting all;
   public static BooleanSetting nbtOnly;

   public ItemTags() {
      super("ItemTags", false, Category.Visual, false, "Render text bar on dropped items");
      this.addSettings(new Setting[]{scale, opacity, megawalls, bedwars, swordAndBows, blocks, gApple, all, nbtOnly});
   }

   @SubscribeEvent
   public void onRender(RenderWorldLastEvent event) {
      for (Entity entity : this.mc.theWorld.loadedEntityList) {
         if (entity instanceof EntityItem) {
            EntityItem entityItem = (EntityItem)entity;
            ItemStack itemStack = entityItem.getEntityItem();
            Item item = itemStack.getItem();
            String itemName = itemStack.getDisplayName();
            String stripItemName = StringUtils.stripControlCodes(itemName);
            int stackSize = itemStack.stackSize;
            if (all.getState()) {
               if (nbtOnly.getState() && itemStack.hasTagCompound() || !nbtOnly.getState()) {
                  int nbtColor = -1;
                  if (itemStack.hasTagCompound() && itemStack.getTagCompound().hasKey("display")) {
                     NBTTagCompound tag = itemStack.getTagCompound().getCompoundTag("display");
                     if (tag.hasKey("color")) {
                        nbtColor = tag.getInteger("color");
                     }
                  }

                  if (itemStack.hasTagCompound()) {
                     this.drawTags(entity, itemName + " §fx" + stackSize, nbtColor, event.partialTicks, (float)scale.getRoundedValue());
                  } else {
                     this.drawTags(entity, itemName + " §fx" + stackSize, 16777215, event.partialTicks, (float)scale.getRoundedValue());
                  }
               }
            } else if (nbtOnly.getState() && itemStack.hasTagCompound() || !nbtOnly.getState()) {
               if (megawalls.getState()) {
                  int nbtColorx = -1;
                  if (itemStack.hasTagCompound() && itemStack.getTagCompound().hasKey("display")) {
                     NBTTagCompound tag = itemStack.getTagCompound().getCompoundTag("display");
                     if (tag.hasKey("color")) {
                        nbtColorx = tag.getInteger("color");
                     }
                  }

                  if (stripItemName.startsWith("Phoenix's Tears of Regen")) {
                     this.drawTags(entity, "§6Phoenix's Tears of Regen §fx" + stackSize, nbtColorx, event.partialTicks, (float)scale.getRoundedValue());
                  }

                  if (stripItemName.startsWith("Squid's Absorption")) {
                     this.drawTags(entity, "§9Squid's Absorption §fx" + stackSize, nbtColorx, event.partialTicks, (float)scale.getRoundedValue());
                  }

                  if (stripItemName.startsWith("Matey")) {
                     this.drawTags(entity, itemName + " §fx" + stackSize, nbtColorx, event.partialTicks, (float)scale.getRoundedValue());
                  }

                  if (stripItemName.startsWith("Regen-Ade")) {
                     this.drawTags(entity, "§bRegen-ades §fx" + stackSize, nbtColorx, event.partialTicks, (float)scale.getRoundedValue());
                  }

                  if (stripItemName.startsWith("Ultra Pasteurized Milk Bucket")) {
                     this.drawTags(entity, "§fMilk Bucket §fx" + stackSize, nbtColorx, event.partialTicks, (float)scale.getRoundedValue());
                  }

                  if (stripItemName.startsWith("Junk Apple")) {
                     this.drawTags(entity, itemName + " §fx" + stackSize, nbtColorx, event.partialTicks, (float)scale.getRoundedValue());
                  }

                  if (item == Items.pumpkin_pie) {
                     this.drawTags(entity, itemName + " §fx" + stackSize, 16711610, event.partialTicks, (float)scale.getRoundedValue());
                  }

                  if (item == Items.golden_apple) {
                     this.drawTags(entity, itemName + " §fx" + stackSize, 16755200, event.partialTicks, (float)scale.getRoundedValue());
                  }

                  if (item == Items.diamond) {
                     this.drawTags(entity, itemName + " §fx" + stackSize, 5636095, event.partialTicks, (float)scale.getRoundedValue());
                  }

                  if (item == Items.diamond_sword) {
                     this.drawTags(entity, itemName + " §fx" + stackSize, 5636095, event.partialTicks, (float)scale.getRoundedValue());
                  }

                  if (isDiamond(item)) {
                     this.drawTags(entity, itemName + " §fx" + stackSize, 5636095, event.partialTicks, (float)scale.getRoundedValue());
                  }
               }

               if (bedwars.getState()) {
                  if (item == Items.diamond) {
                     this.drawTags(entity, itemName + " §fx" + stackSize, 5636095, event.partialTicks, (float)scale.getRoundedValue());
                  }

                  if (item == Items.iron_ingot) {
                     this.drawTags(entity, itemName + " §fx" + stackSize, 11184810, event.partialTicks, (float)scale.getRoundedValue());
                  }

                  if (item == Items.gold_ingot) {
                     this.drawTags(entity, itemName + " §fx" + stackSize, 16777045, event.partialTicks, (float)scale.getRoundedValue());
                  }

                  if (item == Items.emerald) {
                     this.drawTags(entity, itemName + " §fx" + stackSize, 5635925, event.partialTicks, (float)scale.getRoundedValue());
                  }
               }

               if (swordAndBows.getState() && (item instanceof ItemSword || item instanceof ItemBow)) {
                  this.drawTags(entity, itemName + " §fx" + stackSize, 16733525, event.partialTicks, (float)scale.getRoundedValue());
               }

               if (blocks.getState() && ItemUtil.isPlaceableBlock(itemStack)) {
                  this.drawTags(entity, itemName + " §fx" + stackSize, 16777215, event.partialTicks, (float)scale.getRoundedValue());
               }

               if (gApple.getState() && item == Items.golden_apple) {
                  this.drawTags(entity, itemName + " §fx" + stackSize, 16755200, event.partialTicks, (float)scale.getRoundedValue());
               }
            }
         }
      }
   }

   private void drawTags(Entity entity, String tag, int color, float partialTicks, float addScale) {
      Minecraft mc = Minecraft.getMinecraft();
      addScale /= 3.0F;
      float x = (float)(entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * (double)partialTicks - mc.getRenderManager().viewerPosX);
      float y = (float)(entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * (double)partialTicks - mc.getRenderManager().viewerPosY);
      float z = (float)(entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * (double)partialTicks - mc.getRenderManager().viewerPosZ);
      float size = mc.thePlayer.getDistanceToEntity(entity) / 10.0F;
      if (size < 1.1F) {
         size = 1.1F;
      }

      float scale = size * 1.8F;
      scale /= 100.0F;
      scale += addScale / 50.0F;
      GL11.glPushMatrix();
      GL11.glTranslatef(x, y + 0.5F, z);
      GL11.glRotatef(-mc.getRenderManager().playerViewY, 0.0F, 1.0F, 0.0F);
      if (mc.gameSettings.thirdPersonView == 2) {
         GL11.glRotatef(-mc.getRenderManager().playerViewX, 1.0F, 0.0F, 0.0F);
      } else {
         GL11.glRotatef(mc.getRenderManager().playerViewX, 1.0F, 0.0F, 0.0F);
      }

      GL11.glScalef(-scale, -scale, scale);
      GL11.glDisable(2929);
      GL11.glEnable(3042);
      int yOffset = Myaven.moduleManager.getModule("ItemScale").isEnabled()
         ? (ItemScale.onScale(((EntityItem)entity).getEntityItem()) ? (int)((ItemScale.scale.getRoundedValue() - 1.0) * 14.0) : 0)
         : 0;
      float left = (float)(-mc.fontRendererObj.getStringWidth(tag) / 2) - 4.6F;
      Gui.drawRect(
         (int)left + 2,
         -14 - yOffset,
         mc.fontRendererObj.getStringWidth(tag) / 2,
         -4 - yOffset,
         new Color(0, 0, 0, 255 * opacity.getPercentage() / 100).getRGB()
      );
      mc.fontRendererObj.drawString(tag, left + 4.0F, -12.5F - (float)yOffset, color, false);
      GL11.glEnable(2929);
      GL11.glDisable(3042);
      GlStateManager.resetColor();
      GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
      GlStateManager.popMatrix();
   }

   public static boolean isDiamond(Item item) {
      return item == Items.diamond_boots || item == Items.diamond_leggings || item == Items.diamond_helmet || item == Items.diamond_chestplate;
   }

   // $VF: Irreducible bytecode was duplicated to produce valid code
   static {
      scale = new SliderSetting("Scale", 1.0, 0.01, 5.0, 0.01);
      opacity = new PercentageSetting("Background-opacity", 20);
      megawalls = new BooleanSetting("Megawalls-items", false);
      bedwars = new BooleanSetting("Bedwars-resources", false);
      swordAndBows = new BooleanSetting("Render-swords-and-bows", false);
      blocks = new BooleanSetting("Render-blocks", false);
      gApple = new BooleanSetting("Render-golden-apples", true);
      all = new BooleanSetting("Render-ALL", true);
      nbtOnly = new BooleanSetting("NBT-only", false);
   }
}
