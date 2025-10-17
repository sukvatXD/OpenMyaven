package Myaven.module.modules.visual;

import Myaven.events.PlayerUpdateEvent;
import Myaven.events.PostAttackEvent;
import Myaven.events.Render2DEvent;
import Myaven.module.Category;
import Myaven.module.Module;
import Myaven.module.modules.combat.KillAura;
import Myaven.module.modules.config.Teams;
import Myaven.module.modules.config.Theme;
import Myaven.module.modules.misc.AntiBot;
import Myaven.setting.Setting;
import Myaven.setting.settings.BooleanSetting;
import Myaven.setting.settings.ColorSetting;
import Myaven.setting.settings.DescriptionSetting;
import Myaven.setting.settings.ModeSetting;
import Myaven.setting.settings.PercentageSetting;
import Myaven.setting.settings.SliderSetting;
import Myaven.util.ClientUtil;
import Myaven.util.RaytraceUtil;
import Myaven.util.RenderUtil;
import Myaven.util.RotationUtil;
import Myaven.util.TimerUtil;
import java.awt.Color;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class TargetHUD extends Module {
   public static DescriptionSetting targetModeDescription;
   public static ModeSetting targetMode;
   public static SliderSetting range;
   public static SliderSetting hudX;
   public static SliderSetting hudY;
   public static SliderSetting stayTime;
   public static PercentageSetting backgroundOpacity;
   public static DescriptionSetting colorModeDescription;
   public static ModeSetting colorMode;
   public static ColorSetting customColor;
   public static BooleanSetting useCustomHealthColor;
   public static BooleanSetting textShadow;
   public static BooleanSetting onlyWhenUsingKillAura;
   public static BooleanSetting includeTeammates;
   public static BooleanSetting botCheck;
   private TimerUtil targetTimer = new TimerUtil();
   private EntityLivingBase currentTarget = null;

   public TargetHUD() {
      super("TargetHUD", false, Category.Visual, false, "Show basic information about the current attacking target");
      this.addSettings(
         new Setting[]{
            targetModeDescription,
            targetMode,
            range,
            hudX,
            hudY,
            stayTime,
            backgroundOpacity,
            colorModeDescription,
            colorMode,
            customColor,
            useCustomHealthColor,
            textShadow,
            onlyWhenUsingKillAura,
            includeTeammates,
            botCheck
         }
      );
   }

   public static int combineColorWithAlpha(int n, int n2) {
      return n & 16777215 | n2 << 24;
   }

   @SubscribeEvent
   public void onRender(Render2DEvent event) {
      if (this.currentTarget != null) {
         if (this.currentTarget.isDead || this.currentTarget.getHealth() <= 0.0F) {
            return;
         }

         if (!includeTeammates.getState() && Teams.isTeammate(this.currentTarget)) {
            return;
         }

         if (botCheck.getState() && AntiBot.isBot(this.currentTarget)) {
            return;
         }

         String TargetName = this.currentTarget.getDisplayName().getFormattedText();
         float health = RotationUtil.clampFloat(this.currentTarget.getHealth() / this.currentTarget.getMaxHealth(), 0.0F, 1.0F);
         String TargetHealth = String.format("%.1f", this.currentTarget.getHealth()) + "§c❤ ";
         String status = health <= RaytraceUtil.getTotalHealth(this.mc.thePlayer) / this.mc.thePlayer.getMaxHealth() ? " §aW" : " §cL";
         TargetName = TargetName + status;
         ScaledResolution scaledResolution = new ScaledResolution(this.mc);
         int n2 = 8;
         int n3 = this.mc.fontRendererObj.getStringWidth(TargetName) + 8 + 20;
         int n4 = (int)hudX.getRoundedValue();
         int n5 = scaledResolution.getScaledHeight() - (int)hudY.getRoundedValue();
         int minX = n4 - 8;
         int minY = n5 - 8;
         int maxX = n4 + n3;
         int maxY = n5 + this.mc.fontRendererObj.FONT_HEIGHT + 5 - 6 + 8;
         int n10 = 255;
         int n11 = Math.min(255, 110);
         int n12 = Math.min(255, 210);
         Gui.drawRect(minX, minY, maxX, maxY + 7, new Color(0, 0, 0, 255 * backgroundOpacity.getPercentage() / 100).getRGB());
         int n13 = minX + 6 + 27;
         int n14 = maxX - 2;
         int n15 = (int)((double)maxY + 0.45);
         Gui.drawRect(n13, n15, n14, n15 + 4, combineColorWithAlpha(Color.black.getRGB(), n11));
         float healthBar = (float)((int)((double)n14 + (double)(n13 - n14) * (1.0 - (double)((double)health < 0.01 ? 0.0F : health))));
         if (healthBar - (float)n13 < 1.0F) {
            healthBar = (float)n13;
         }

         String healthTextColor = colorMode.getCurrent();
         int color;
         switch (healthTextColor) {
            case "THEME":
               color = Theme.computeThemeColor(0.0);
               break;
            case "THEME_CUSTOM":
               color = Theme.computeCustomThemeColor(0.0);
               break;
            default:
               color = customColor.F();
         }

         RenderUtil.drawRect((double)n13, (double)n15, (double)healthBar, (double)(n15 + 4), color);
         int healthTextColor1 = computeHealthColor((double)health);
         RenderUtil.drawRect((double)n13, (double)n15, (double)healthBar, (double)(n15 + 4), useCustomHealthColor.getState() ? color : healthTextColor1);
         GlStateManager.pushMatrix();
         GlStateManager.enableBlend();
         GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
         this.mc
            .fontRendererObj
            .drawString(
               TargetName, (float)(n4 + 25), (float)n5 - 4.0F, new Color(220, 220, 220, 255).getRGB() & 16777215 | clampAlpha(270) << 24, textShadow.getState()
            );
         this.mc
            .fontRendererObj
            .drawString(
               RaytraceUtil.getHealthColor(RaytraceUtil.getTotalHealth(this.currentTarget), this.currentTarget.getMaxHealth()) + TargetHealth,
               (float)(n4 + 25),
               (float)n5 + 6.0F,
               -1,
               textShadow.getState()
            );
         GlStateManager.disableBlend();
         GlStateManager.popMatrix();
         if (this.currentTarget instanceof AbstractClientPlayer) {
            AbstractClientPlayer var34 = (AbstractClientPlayer)this.currentTarget;
            double targetX = (double)(minX + 4);
            double targetY = (double)(minY + 3);
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            drawPlayerHead((float)targetX, (float)targetY, 25.0F, 25.0F, var34);
            Color dynamicColor = new Color(255, 255 - var34.hurtTime * 10, 255 - var34.hurtTime * 10);
            GlStateManager.color(
               (float)dynamicColor.getRed() / 255.0F,
               (float)dynamicColor.getGreen() / 255.0F,
               (float)dynamicColor.getBlue() / 255.0F,
               (float)dynamicColor.getAlpha() / 255.0F
            );
            drawPlayerHead((float)targetX, (float)targetY, 25.0F, 25.0F, var34);
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
         }
      }
   }

   public static int computeHealthColor(double health) {
      return health < 0.3 ? -43691 : (health < 0.5 ? -22016 : (health < 0.7 ? -171 : -11141291));
   }

   public static String formatHealthText(double current, double max) {
      double health = RaytraceUtil.roundDouble(max, 1);
      return (current < 0.3 ? "§c" : (current < 0.5 ? "§6" : (current < 0.7 ? "§e" : "§a"))) + (ClientUtil.isWholeNumber(health) ? (int)health + "" : health);
   }

   @SubscribeEvent
   public void onUpdate(PlayerUpdateEvent event) {
      if (KillAura.currentTarget != null) {
         this.currentTarget = KillAura.currentTarget;
         this.targetTimer.reset();
      } else {
         if (this.mc.currentScreen instanceof GuiChat) {
            this.currentTarget = this.mc.thePlayer;
            return;
         }

         if (onlyWhenUsingKillAura.getState() && !KillAura.combatTick && this.targetTimer.hasTimePassed(stayTime.getRoundedValue() * 1000.0)) {
            this.currentTarget = null;
            return;
         }

         if (targetMode.getCurrent().equalsIgnoreCase("AIM")) {
            EntityLivingBase rayTrace = RaytraceUtil.raycastEntity((double)((int)range.getRoundedValue()));
            if (rayTrace != null && rayTrace instanceof EntityPlayer) {
               this.currentTarget = rayTrace;
               this.targetTimer.reset();
            } else if (this.targetTimer.hasTimePassed(stayTime.getRoundedValue() * 1000.0)) {
               this.currentTarget = null;
            }
         } else if (targetMode.getCurrent().equalsIgnoreCase("HIT") && this.targetTimer.hasTimePassed(stayTime.getRoundedValue() * 1000.0)) {
            this.currentTarget = null;
         }
      }
   }

   @SubscribeEvent
   public void onPostAttack(PostAttackEvent event) {
      if (targetMode.getCurrent().equalsIgnoreCase("HIT") && event.getTarget() instanceof EntityPlayer) {
         this.currentTarget = (EntityLivingBase)event.getTarget();
         this.targetTimer.reset();
      }
   }

   public static int clampAlpha(int n) {
      return n > 255 ? 255 : Math.max(n, 4);
   }

   public static void drawPlayerHead(float x, float y, float width, float height, AbstractClientPlayer player) {
      Minecraft mc = Minecraft.getMinecraft();
      GlStateManager.enableBlend();
      GlStateManager.blendFunc(770, 771);
      mc.getTextureManager().bindTexture(player.getLocationSkin());
      Gui.drawScaledCustomSizeModalRect((int)x, (int)y, 8.0F, 8.0F, 8, 8, (int)width, (int)height, 64.0F, 64.0F);
      GlStateManager.disableBlend();
   }

   // $VF: Irreducible bytecode was duplicated to produce valid code
   static {
      targetModeDescription = new DescriptionSetting("HIT, AIM");
      targetMode = new ModeSetting("Target-mode", "HIT", "AIM");
      range = new SliderSetting("Range", 10.0, 0.0, 30.0, 1.0);
      hudX = new SliderSetting("X", 350.0, 0.0, 800.0, 1.0);
      hudY = new SliderSetting("Y", 150.0, 0.0, 500.0, 1.0);
      stayTime = new SliderSetting("Stay-time", 2.0, 0.1, 20.0, 0.1);
      backgroundOpacity = new PercentageSetting("Background-opacity", 50);
      colorModeDescription = new DescriptionSetting("THEME, THEME_CUSTOM, CUSTOM");
      colorMode = new ModeSetting("Color", "THEME", "THEME_CUSTOM", "CUSTOM");
      customColor = new ColorSetting("Custom-color", "FFFFFF");
      useCustomHealthColor = new BooleanSetting("Custom-health-color", false);
      textShadow = new BooleanSetting("Text-shadow", true);
      onlyWhenUsingKillAura = new BooleanSetting("Only-when-using-killaura", true);
      includeTeammates = new BooleanSetting("Teammates", false);
      botCheck = new BooleanSetting("Bot-check", true);

   }
}
