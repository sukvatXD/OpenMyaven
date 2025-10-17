package Myaven.module.modules.visual;

import Myaven.mixins.accessor.AccessorEntityRenderer;
import Myaven.module.Category;
import Myaven.module.Module;
import Myaven.module.modules.config.Teams;
import Myaven.module.modules.config.Theme;
import Myaven.module.modules.misc.AntiBot;
import Myaven.setting.Setting;
import Myaven.setting.settings.BooleanSetting;
import Myaven.setting.settings.ColorSetting;
import Myaven.setting.settings.DescriptionSetting;
import Myaven.setting.settings.ModeSetting;
import Myaven.setting.settings.SliderSetting;
import Myaven.util.ClientUtil;
import Myaven.util.RenderUtil;
import java.awt.Color;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.Vec3;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.opengl.GL11;

public class ESP extends Module {
   public static DescriptionSetting modeDescription;
   public static ModeSetting espMode;
   public static DescriptionSetting colorModeDescription;
   public static DescriptionSetting customColorDescription;
   public static ModeSetting colorMode;
   public static ColorSetting customColor;
   public static DescriptionSetting healthBarDescription;
   public static ModeSetting healthBarMode;
   public static SliderSetting espOffset;
   public static BooleanSetting showSelf;
   public static BooleanSetting includeTeammates;
   public static BooleanSetting botCheckEnabled;

   public ESP() {
      super("ESP", false, Category.Visual, false, "Aka \"Extra sensory perception\"");
      this.addSettings(
         new Setting[]{
            modeDescription,
            espMode,
            colorModeDescription,
            customColorDescription,
            colorMode,
            customColor,
            healthBarDescription,
            healthBarMode,
            espOffset,
            showSelf,
            includeTeammates,
            botCheckEnabled
         }
      );
   }

   @SubscribeEvent
   public void onRender(RenderWorldLastEvent event) {
      for (EntityLivingBase entity : this.mc.theWorld.playerEntities) {
         if (!(entity.getHealth() <= 0.0F)
            && entity instanceof EntityPlayer
            && (botCheckEnabled.getState() && !AntiBot.isBot(entity) || !botCheckEnabled.getState())
            && (!includeTeammates.getState() && !Teams.isTeammate(entity) || includeTeammates.getState())
            && !entity.isDead
            && entity.getUniqueID() != this.mc.thePlayer.getUniqueID()) {
            if (espMode.getCurrent().equals("BOX")) {
               this.renderHealthBar(entity);
            } else {
               this.renderEntityEsp(entity);
               this.renderHealthBar(entity);
            }
         }
      }

      if (showSelf.getState() && this.mc.gameSettings.thirdPersonView != 0) {
         if (espMode.getCurrent().equals("BOX")) {
            this.renderHealthBar(this.mc.thePlayer);
         } else {
            this.renderEntityEsp(this.mc.thePlayer);
            this.renderHealthBar(this.mc.thePlayer);
         }
      }
   }

   private void renderEntityEsp(EntityLivingBase entity) {
      if (RenderUtil.isInFrustum(entity)) {
         String var4 = colorMode.getCurrent();
         int color;
         switch (var4) {
            case "THEME":
               color = Theme.computeThemeColor(0.0);
               break;
            case "THEME_CUSTOM":
               color = Theme.computeCustomThemeColor(0.0);
               break;
            case "TEAM":
               Color c = new Color(Teams.getEntityTeamColor(entity));
               color = new Color(c.getRed(), c.getGreen(), c.getBlue(), 255).getRGB();
               break;
            default:
               Color c2 = new Color(customColor.F());
               color = new Color(c2.getRed(), c2.getGreen(), c2.getBlue(), 255).getRGB();
         }

         var4 = espMode.getCurrent();
         switch (var4) {
            case "BOX":
               this.render2DBoxOverlay(entity, color, 0.0, ClientUtil.getRenderPartialTicks());
               break;
            case "2D":
               RenderUtil.drawEntityESP(entity, 3, 0.0, espOffset.getRoundedValue(), color, false);
               break;
            case "3D":
               RenderUtil.drawEntityESP(entity, 8, 0.0, espOffset.getRoundedValue(), color, false);
         }
      }
   }

   private void renderHealthBar(EntityLivingBase entity) {
      String var3 = healthBarMode.getCurrent();
      switch (var3) {
         case "NORMAL":
            RenderUtil.drawEntityESP(entity, 7, 0.0, espOffset.getRoundedValue(), 16777215, false);
            break;
         case "THIN":
            RenderUtil.drawEntityESP(entity, 4, 0.0, espOffset.getRoundedValue(), 16777215, false);
      }
   }

   public void render2DBoxOverlay(EntityLivingBase en, int rgb, double expand, float partialTicks) {
      if (RenderUtil.isInFrustum(en)) {
         ((AccessorEntityRenderer)this.mc.entityRenderer).callSetupCameraTransform(ClientUtil.getRenderPartialTicks(), 0);
         ScaledResolution scaledResolution = new ScaledResolution(this.mc);
         double playerX = en.lastTickPosX + (en.posX - en.lastTickPosX) * (double)partialTicks - this.mc.getRenderManager().viewerPosX;
         double playerY = en.lastTickPosY + (en.posY - en.lastTickPosY) * (double)partialTicks - this.mc.getRenderManager().viewerPosY;
         double playerZ = en.lastTickPosZ + (en.posZ - en.lastTickPosZ) * (double)partialTicks - this.mc.getRenderManager().viewerPosZ;
         AxisAlignedBB bbox = en.getEntityBoundingBox().expand(0.1 + expand, 0.1 + expand, 0.1 + expand);
         AxisAlignedBB axis = new AxisAlignedBB(
            bbox.minX - en.posX + playerX,
            bbox.minY - en.posY + playerY,
            bbox.minZ - en.posZ + playerZ,
            bbox.maxX - en.posX + playerX,
            bbox.maxY - en.posY + playerY,
            bbox.maxZ - en.posZ + playerZ
         );
         Vec3[] corners = new Vec3[8];
         corners[0] = new Vec3(axis.minX, axis.minY, axis.minZ);
         corners[1] = new Vec3(axis.minX, axis.minY, axis.maxZ);
         corners[2] = new Vec3(axis.minX, axis.maxY, axis.minZ);
         corners[3] = new Vec3(axis.minX, axis.maxY, axis.maxZ);
         corners[4] = new Vec3(axis.maxX, axis.minY, axis.minZ);
         corners[5] = new Vec3(axis.maxX, axis.minY, axis.maxZ);
         corners[6] = new Vec3(axis.maxX, axis.maxY, axis.minZ);
         corners[7] = new Vec3(axis.maxX, axis.maxY, axis.maxZ);
         double minX = Double.MAX_VALUE;
         double minY = Double.MAX_VALUE;
         double maxX = Double.MIN_VALUE;
         double maxY = Double.MIN_VALUE;
         boolean isInView = false;

         for (Vec3 corner : corners) {
            double x = corner.xCoord;
            double y = corner.yCoord;
            double z = corner.zCoord;
            Vec3 screenVec = RenderUtil.worldToScreen(scaledResolution.getScaleFactor(), x, y, z);
            if (screenVec != null && !(screenVec.zCoord >= 1.0003684) && !(screenVec.zCoord <= 0.0)) {
               isInView = true;
               double screenX = screenVec.xCoord;
               double screenY = screenVec.yCoord;
               if (screenX < minX) {
                  minX = screenX;
               }

               if (screenY < minY) {
                  minY = screenY;
               }

               if (screenX > maxX) {
                  maxX = screenX;
               }

               if (screenY > maxY) {
                  maxY = screenY;
               }
            }
         }

         if (isInView) {
            this.mc.entityRenderer.setupOverlayRendering();
            ScaledResolution res = new ScaledResolution(this.mc);
            int screenWidth = res.getScaledWidth();
            int screenHeight = res.getScaledHeight();
            minX = Math.max(0.0, minX);
            minY = Math.max(0.0, minY);
            maxX = Math.min((double)screenWidth, maxX);
            maxY = Math.min((double)screenHeight, maxY);
            float red = (float)(rgb >> 16 & 0xFF) / 255.0F;
            float green = (float)(rgb >> 8 & 0xFF) / 255.0F;
            float blue = (float)(rgb & 0xFF) / 255.0F;
            GL11.glPushMatrix();
            GL11.glDisable(3553);
            GL11.glDisable(2929);
            GL11.glEnable(2848);
            GL11.glLineWidth(1.0F);
            GL11.glColor4f(0.0F, 0.0F, 0.0F, 0.4F);
            GL11.glBegin(2);
            GL11.glVertex2d(minX, minY);
            GL11.glVertex2d(maxX, minY);
            GL11.glVertex2d(maxX, maxY);
            GL11.glVertex2d(minX, maxY);
            GL11.glEnd();
            GL11.glColor4f(0.0F, 0.0F, 0.0F, 0.4F);
            GL11.glBegin(2);
            GL11.glVertex2d(minX + 1.0, minY + 1.0);
            GL11.glVertex2d(maxX - 1.0, minY + 1.0);
            GL11.glVertex2d(maxX - 1.0, maxY - 1.0);
            GL11.glVertex2d(minX + 1.0, maxY - 1.0);
            GL11.glEnd();
            GL11.glColor4f(red, green, blue, 1.0F);
            GL11.glBegin(2);
            GL11.glVertex2d(minX + 0.5, minY + 0.5);
            GL11.glVertex2d(maxX - 0.5, minY + 0.5);
            GL11.glVertex2d(maxX - 0.5, maxY - 0.5);
            GL11.glVertex2d(minX + 0.5, maxY - 0.5);
            GL11.glEnd();
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            GL11.glEnable(3553);
            GL11.glEnable(2929);
            GL11.glDisable(2848);
            GL11.glPopMatrix();
         }
      }
   }

   @SubscribeEvent(
      priority = EventPriority.LOWEST
   )
   public void onLowest(RenderWorldLastEvent e) {
      if (ClientUtil.isWorldLoaded() && espMode.getCurrent().equals("BOX")) {
         for (EntityLivingBase entity : this.mc.theWorld.playerEntities) {
            if (!(entity.getHealth() <= 0.0F)
               && entity instanceof EntityPlayer
               && (botCheckEnabled.getState() && !AntiBot.isBot(entity) || !botCheckEnabled.getState())
               && (!includeTeammates.getState() && !Teams.isTeammate(entity) || includeTeammates.getState())
               && !entity.isDead
               && entity.getUniqueID() != this.mc.thePlayer.getUniqueID()) {
               this.renderEntityEsp(entity);
            }
         }

         if (showSelf.getState() && this.mc.gameSettings.thirdPersonView != 0) {
            this.renderEntityEsp(this.mc.thePlayer);
         }
      }
   }

   // $VF: Irreducible bytecode was duplicated to produce valid code
   static {
      modeDescription = new DescriptionSetting("BOX, 2D, 3D, NONE");
      espMode = new ModeSetting("Mode", "BOX", "2D", "3D", "NONE");
      colorModeDescription = new DescriptionSetting("TEAM, THEME, THEME_CUSTOM, ");
      customColorDescription = new DescriptionSetting("CUSTOM");
      colorMode = new ModeSetting("Color", "TEAM", "THEME", "THEME_CUSTOM", "CUSTOM");
      customColor = new ColorSetting("Custom-color", "FFFFFF");
      healthBarDescription = new DescriptionSetting("NORMAL, THIN, NONE");
      healthBarMode = new ModeSetting("Health-bar", "NORMAL", "THIN", "NONE");
      espOffset = new SliderSetting("Offset", 0.0, -50.0, 25.0, 1.0);
      showSelf = new BooleanSetting("Show-self", false);
      includeTeammates = new BooleanSetting("Teammates", true);
      botCheckEnabled = new BooleanSetting("Bot-check", true);
   }
}
