package Myaven.module.modules.visual;

import Myaven.Myaven;
import Myaven.events.Render2DEvent;
import Myaven.module.Category;
import Myaven.module.Module;
import Myaven.module.modules.config.Theme;
import Myaven.setting.Setting;
import Myaven.setting.settings.BooleanSetting;
import Myaven.setting.settings.ColorSetting;
import Myaven.setting.settings.DescriptionSetting;
import Myaven.setting.settings.ModeSetting;
import Myaven.util.ClientUtil;
import Myaven.util.PlayerUtil;
import Myaven.util.RaytraceUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class HUD extends Module {
   public static DescriptionSetting themeDescription;
   public static ModeSetting theme;
   public static ColorSetting customColor;
   public static BooleanSetting watermark;
   public static BooleanSetting health;
   public static BooleanSetting bps;
   public static BooleanSetting release;
   public static BooleanSetting username;

   public HUD() {
      super("HUD", false, Category.Visual, false, "Aka \"Heads up display\"");
      this.addSettings(new Setting[]{themeDescription, theme, customColor, watermark, health, bps, release, username});
   }

   @SubscribeEvent
   public void onRender(Render2DEvent event) {
      String leftCorner = theme.getCurrent();
      int color;
      switch (leftCorner) {
         case "THEME":
            color = Theme.computeThemeColor(0.0);
            break;
         case "THEME_CUSTOM":
            color = Theme.computeCustomThemeColor(0.0);
            break;
         default:
            color = customColor.F();
      }

      leftCorner = "";
      if (bps.getState()) {
         leftCorner = "" + ClientUtil.roundToTwoDecimals(PlayerUtil.getBPS()) + " BPS";
      }

      ScaledResolution sr = new ScaledResolution(Minecraft.getMinecraft());
      FontRenderer font = Minecraft.getMinecraft().fontRendererObj;
      font.drawStringWithShadow(leftCorner, 0.0F, (float)(sr.getScaledHeight() - font.FONT_HEIGHT), color);
      String upLeftCorner = "";
      if (watermark.getState()) {
         upLeftCorner = "Myaven";
      }

      font.drawStringWithShadow(upLeftCorner, 0.0F, 0.0F, color);
      String upLeftCorner2 = "";
      if (username.getState()) {
         upLeftCorner2 = "User " + Myaven.username;
      }

      font.drawStringWithShadow(upLeftCorner2, 0.0F, (float)(this.mc.fontRendererObj.FONT_HEIGHT + 1), color);
      String rightCorner = "";
      if (release.getState()) {
         rightCorner = "" + "Myaven Release 1.6 2025/10/9 b12";
      }

      font.drawStringWithShadow(
         rightCorner, (float)(sr.getScaledWidth() - font.getStringWidth(rightCorner)), (float)(sr.getScaledHeight() - font.FONT_HEIGHT), color
      );
      String center = "";
      if (health.getState()) {
         center = RaytraceUtil.getHealthColor(RaytraceUtil.getTotalHealth(this.mc.thePlayer), this.mc.thePlayer.getMaxHealth())
            + ClientUtil.roundToTwoDecimals((double)(RaytraceUtil.getHealth(this.mc.thePlayer) + RaytraceUtil.getAbsorption(this.mc.thePlayer)))
            + "❤";
      }

      font.drawStringWithShadow(
         center,
         (float)sr.getScaledWidth() / 2.0F - (float)font.getStringWidth(center) / 2.0F,
         (float)sr.getScaledHeight() / 2.0F + (float)font.FONT_HEIGHT + 2.0F,
         -1
      );
   }

   // $VF: Irreducible bytecode was duplicated to produce valid code
   static {
      themeDescription = new DescriptionSetting("THEME, THEME_CUSTOM, CUSTOM");
      theme = new ModeSetting("Color", "THEME", "THEME_CUSTOM", "CUSTOM");
      customColor = new ColorSetting("Custom-color", "FFFFFF");
      watermark = new BooleanSetting("Watermark", true);
      health = new BooleanSetting("Health", true);
      bps = new BooleanSetting("BPS", true);
      release = new BooleanSetting("Release", true);
      username = new BooleanSetting("Username", true);
   }
}
