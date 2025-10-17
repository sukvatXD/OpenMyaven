package Myaven.module.modules.visual;

import Myaven.mixins.accessor.AccessorEntityRenderer;
import Myaven.module.Category;
import Myaven.module.Module;
import Myaven.module.modules.config.Teams;
import Myaven.module.modules.misc.AntiBot;
import Myaven.module.modules.player.AutoWeapon;
import Myaven.setting.Setting;
import Myaven.setting.settings.BooleanSetting;
import Myaven.setting.settings.PercentageSetting;
import Myaven.setting.settings.SliderSetting;
import Myaven.util.ClientUtil;
import Myaven.util.RaytraceUtil;
import Myaven.util.RenderUtil;
import java.awt.Color;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.item.ItemTool;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.Vec3;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.client.event.RenderLivingEvent.Specials.Pre;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.jetbrains.annotations.NotNull;

public class NameTags extends Module {
   public static SliderSetting scale;
   public static PercentageSetting backgroundOpacity;
   public static BooleanSetting autoScale;
   public static BooleanSetting onlyName;
   public static BooleanSetting showSelf;
   public static BooleanSetting textShadow;
   public static BooleanSetting showHealth;
   public static BooleanSetting showDistance;
   public static BooleanSetting showHitsToKill;
   public static BooleanSetting showItems;
   public static BooleanSetting showEnchants;
   public static BooleanSetting showTeammates;
   public static BooleanSetting botCheck;
   private int manualAllyOutlineColor = new Color(0, 255, 0, 255).getRGB();
   private int manualEnemyOutlineColor = new Color(255, 0, 0, 255).getRGB();
   private double autoScaleRadius = 8.0;

   public NameTags() {
      super("NameTags", false, Category.Visual, false, "Modify nametags rendering");
      this.addSettings(
         new Setting[]{
            scale,
            autoScale,
            backgroundOpacity,
            onlyName,
            showSelf,
            textShadow,
            showHealth,
            showDistance,
            showHitsToKill,
            showItems,
            showEnchants,
            showTeammates,
            botCheck
         }
      );
   }

   @Override
   public String getSuffix() {
      return String.valueOf(scale.getRoundedValue());
   }

   @SubscribeEvent(
      priority = EventPriority.LOWEST
   )
   public void onRender(RenderWorldLastEvent ev) {
      if (ClientUtil.isWorldLoaded()) {
         double interpolatedX;
         double interpolatedY;
         double interpolatedZ;
         if (this.mc.gameSettings.thirdPersonView > 0) {
            Vec3 thirdPersonPos = ClientUtil.getCameraPos((double)ev.partialTicks);
            interpolatedX = thirdPersonPos.xCoord;
            interpolatedY = thirdPersonPos.yCoord;
            interpolatedZ = thirdPersonPos.zCoord;
         } else {
            interpolatedX = this.mc.thePlayer.lastTickPosX + (this.mc.thePlayer.posX - this.mc.thePlayer.lastTickPosX) * (double)ev.partialTicks;
            interpolatedY = this.mc.thePlayer.lastTickPosY
               + (this.mc.thePlayer.posY - this.mc.thePlayer.lastTickPosY) * (double)ev.partialTicks
               + (double)this.mc.thePlayer.getEyeHeight();
            interpolatedZ = this.mc.thePlayer.lastTickPosZ + (this.mc.thePlayer.posZ - this.mc.thePlayer.lastTickPosZ) * (double)ev.partialTicks;
         }

         ScaledResolution scaledResolution = new ScaledResolution(this.mc);

         for (EntityPlayer en : this.mc.theWorld.playerEntities) {
            if (!en.isDead
               && (en != this.mc.thePlayer || this.mc.gameSettings.thirdPersonView != 0 && showSelf.getState())
               && (showTeammates.getState() || !Teams.isTeammate(en) || en == this.mc.thePlayer)
               && (!botCheck.getState() || !AntiBot.isBot(en) || en == this.mc.thePlayer)
               && !en.getDisplayName().getFormattedText().isEmpty()
               && RenderUtil.isInFrustum(en)) {
               double playerX = en.lastTickPosX + (en.posX - en.lastTickPosX) * (double)ev.partialTicks;
               double playerY = en.lastTickPosY + (en.posY - en.lastTickPosY) * (double)ev.partialTicks;
               double playerZ = en.lastTickPosZ + (en.posZ - en.lastTickPosZ) * (double)ev.partialTicks;
               double renderHeightOffset = playerY
                  - this.mc.getRenderManager().viewerPosY
                  + (!en.isSneaking() ? (double)en.height : (double)en.height - 0.3)
                  + 0.294;
               double heightOffset = playerY + (!en.isSneaking() ? (double)en.height : (double)en.height - 0.3) + 0.294;
               ((AccessorEntityRenderer)this.mc.entityRenderer).callSetupCameraTransform(ClientUtil.getRenderPartialTicks(), 0);
               Vec3 screenCords = RenderUtil.worldToScreen(
                  scaledResolution.getScaleFactor(),
                  playerX - this.mc.getRenderManager().viewerPosX,
                  renderHeightOffset,
                  playerZ - this.mc.getRenderManager().viewerPosZ
               );
               if (screenCords != null) {
                  boolean inFrustum = screenCords.zCoord < 1.0003684;
                  if (inFrustum) {
                     this.mc.entityRenderer.setupOverlayRendering();
                     float scaleSetting = (float)scale.getRoundedValue() * 0.6F;
                     float newScale = scaleSetting;
                     if (autoScale.getState()) {
                        double deltaX = Math.abs(interpolatedX - playerX);
                        if (deltaX < this.autoScaleRadius + 1.0) {
                           double deltaZ = Math.abs(interpolatedZ - playerZ);
                           if (deltaZ < this.autoScaleRadius + 1.0) {
                              double deltaY = Math.abs(interpolatedY - heightOffset);
                              if (deltaY < this.autoScaleRadius + 1.0) {
                                 double distance = Math.sqrt(deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ);
                                 if (distance < this.autoScaleRadius) {
                                    newScale = Math.max((float)((double)scaleSetting * (this.autoScaleRadius / distance)), scaleSetting);
                                 }
                              }
                           }
                        }
                     } else {
                        double deltaX = Math.abs(interpolatedX - playerX);
                        double deltaZ = Math.abs(interpolatedZ - playerZ);
                        double deltaY = Math.abs(interpolatedY - heightOffset);
                        double distance = Math.sqrt(deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ);
                        newScale = (float)((scale.getRoundedValue() + 4.0) / distance);
                     }

                     String name;
                     if (onlyName.getState()) {
                        name = en.getName();
                     } else {
                        name = en.getDisplayName().getFormattedText();
                     }

                     if (showHealth.getState()) {
                        name = name
                           + " "
                           + RaytraceUtil.getHealthColor(RaytraceUtil.getHealth(en), en.getMaxHealth())
                           + ClientUtil.roundToTwoDecimals((double)RaytraceUtil.getHealth(en))
                           + (RaytraceUtil.getAbsorption(en) != 0.0F ? " §6" + ClientUtil.roundToTwoDecimals((double)RaytraceUtil.getAbsorption(en)) : "");
                     }

                     if (showHitsToKill.getState()) {
                        name = name
                           + " "
                           + RaytraceUtil.getFormattedDamage(en, this.mc.thePlayer.inventory.getStackInSlot(AutoWeapon.getBestHotbarWeaponSlot()));
                     }

                     if (showDistance.getState()) {
                        int distance = Math.round(this.mc.thePlayer.getDistanceToEntity(en));
                        String color = "§";
                        if (distance <= 8) {
                           color = "§" + "c";
                        } else if (distance <= 15) {
                           color = color + "6";
                        } else if (distance <= 25) {
                           color = color + "e";
                        } else {
                           color = "";
                        }

                        name = color + distance + "m§r " + name;
                     }

                     int strWidth = this.mc.fontRendererObj.getStringWidth(name) / 2;
                     int x1 = -strWidth - 1;
                     int y1 = -10;
                     int x2 = strWidth + 1;
                     int y2 = -1;
                     GlStateManager.pushMatrix();

                     for (KeyBinding kb : this.mc.gameSettings.keyBindings) {
                        if (kb.getKeyDescription().toLowerCase().contains("zoom") && kb.isKeyDown()) {
                           newScale *= 5.0F;
                        }
                     }

                     GlStateManager.scale(newScale, newScale, newScale);
                     GlStateManager.translate(screenCords.xCoord / (double)newScale, screenCords.yCoord / (double)newScale, 0.0);
                     RenderUtil.drawRect((double)x1, -10.0, (double)x2, -1.0, new Color(0, 0, 0, 255 * backgroundOpacity.getPercentage() / 100).getRGB());
                     if (Teams.isManualAlly(en)) {
                        RenderUtil.drawOutlinedRect((float)x1, -10.0F, (float)x2, -1.0F, 1.0F, this.manualAllyOutlineColor);
                     } else if (Teams.isManualEnemy(en)) {
                        RenderUtil.drawOutlinedRect((float)x1, -10.0F, (float)x2, -1.0F, 1.0F, this.manualEnemyOutlineColor);
                     }

                     this.mc.fontRendererObj.drawString(name, (float)(-strWidth), -9.0F, -1, textShadow.getState());
                     if (showItems.getState()) {
                        this.renderItems(en);
                     }

                     GlStateManager.scale(1.0F, 1.0F, 1.0F);
                     GlStateManager.popMatrix();
                  }
               }
            }
         }
      }
   }

   @SubscribeEvent
   public void onPre(@NotNull Pre<EntityLivingBase> e) {
      if (e.entity instanceof EntityPlayer && (e.entity != this.mc.thePlayer || showSelf.getState()) && !e.entity.isDead) {
         if (!showTeammates.getState() && Teams.isTeammate(e.entity)) {
            e.setCanceled(true);
         }

         if (botCheck.getState() && AntiBot.isBot(e.entity)) {
            e.setCanceled(true);
         }

         EntityPlayer entityPlayer = (EntityPlayer)e.entity;
         if (entityPlayer.getDisplayName().getFormattedText().isEmpty() || entityPlayer != this.mc.thePlayer && AntiBot.isBot(entityPlayer)) {
            return;
         }

         e.setCanceled(true);
      }
   }

   private void renderItems(EntityPlayer e) {
      int pos = 0;

      for (ItemStack is : e.inventory.armorInventory) {
         if (is != null) {
            pos -= 8;
         }
      }

      if (e.getHeldItem() != null) {
         pos -= 8;
         ItemStack item = e.getHeldItem().copy();
         if (item.hasEffect() && (item.getItem() instanceof ItemTool || item.getItem() instanceof ItemArmor)) {
            item.stackSize = 1;
         }

         this.renderItemStack(item, pos, -20);
         pos += 16;
      }

      for (int i = 3; i >= 0; i--) {
         ItemStack stack = e.inventory.armorInventory[i];
         if (stack != null) {
            this.renderItemStack(stack, pos, -20);
            pos += 16;
         }
      }
   }

   private void renderItemStack(ItemStack stack, int xPos, int yPos) {
      GlStateManager.pushMatrix();
      GlStateManager.disableAlpha();
      this.mc.getRenderItem().zLevel = -150.0F;
      GlStateManager.enableDepth();
      RenderHelper.enableGUIStandardItemLighting();
      this.mc.getRenderItem().renderItemAndEffectIntoGUI(stack, xPos, yPos - 8);
      this.mc.getRenderItem().zLevel = 0.0F;
      GlStateManager.disableDepth();
      GlStateManager.scale(0.5, 0.5, 0.5);
      GlStateManager.translate(0.0F, -10.0F, 0.0F);
      this.renderEnchantLabels(stack, xPos, yPos);
      GlStateManager.enableDepth();
      GlStateManager.scale(2.0F, 2.0F, 2.0F);
      GlStateManager.enableAlpha();
      GlStateManager.popMatrix();
   }

   private void renderEnchantLabels(ItemStack stack, int xPos, int yPos) {
      int newYPos = yPos - 24;
      if (showEnchants.getState()
         && stack.getEnchantmentTagList() != null
         && stack.getEnchantmentTagList().tagCount() < 6
         && (
            stack.getItem() instanceof ItemTool
               || stack.getItem() instanceof ItemSword
               || stack.getItem() instanceof ItemBow
               || stack.getItem() instanceof ItemArmor
         )) {
         NBTTagList nbttaglist = stack.getEnchantmentTagList();

         for (int i = 0; i < nbttaglist.tagCount(); i++) {
            int id = nbttaglist.getCompoundTagAt(i).getShort("id");
            int lvl = nbttaglist.getCompoundTagAt(i).getShort("lvl");
            if (lvl > 0) {
               String abbreviated = this.getEnchantAbbreviation(id);
               this.mc.fontRendererObj.drawString(abbreviated + lvl, (float)(xPos * 2), (float)newYPos, -1, textShadow.getState());
               newYPos += 8;
            }
         }
      }
   }

   private String getEnchantAbbreviation(int id) {
      switch (id) {
         case 0:
            return "pt";
         case 1:
            return "frp";
         case 2:
            return "ff";
         case 3:
            return "blp";
         case 4:
            return "prp";
         case 5:
            return "thr";
         case 6:
            return "res";
         case 7:
            return "aa";
         case 8:
         case 9:
         case 10:
         case 11:
         case 12:
         case 13:
         case 14:
         case 15:
         case 22:
         case 23:
         case 24:
         case 25:
         case 26:
         case 27:
         case 28:
         case 29:
         case 30:
         case 31:
         case 36:
         case 37:
         case 38:
         case 39:
         case 40:
         case 41:
         case 42:
         case 43:
         case 44:
         case 45:
         case 46:
         case 47:
         default:
            return null;
         case 16:
            return "sh";
         case 17:
            return "smt";
         case 18:
            return "ban";
         case 19:
            return "kb";
         case 20:
            return "fa";
         case 21:
            return "lot";
         case 32:
            return "eff";
         case 33:
            return "sil";
         case 34:
            return "ub";
         case 35:
            return "for";
         case 48:
            return "pow";
         case 49:
            return "pun";
         case 50:
            return "flm";
         case 51:
            return "inf";
      }
   }

   // $VF: Irreducible bytecode was duplicated to produce valid code
   static {
      scale = new SliderSetting("Scale", 1.0, 0.1, 3.0, 0.01);
      backgroundOpacity = new PercentageSetting("Background-opacity", 30);
      autoScale = new BooleanSetting("Auto-scale", true);
      onlyName = new BooleanSetting("Only-name", false);
      showSelf = new BooleanSetting("Show-self", false);
      textShadow = new BooleanSetting("Text-shadow", true);
      showHealth = new BooleanSetting("Show-health", true);
      showDistance = new BooleanSetting("Show-distance", false);
      showHitsToKill = new BooleanSetting("Show-hits-to-kill", false);
      showItems = new BooleanSetting("Show-items", false);
      showEnchants = new BooleanSetting("Show-enchants", false);
      showTeammates = new BooleanSetting("Teammates", true);
      botCheck = new BooleanSetting("Bot-check", true);
   }
}
