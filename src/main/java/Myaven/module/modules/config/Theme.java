package Myaven.module.modules.config;

import Myaven.enums.Themes;
import Myaven.module.Category;
import Myaven.module.Module;
import Myaven.setting.Setting;
import Myaven.setting.settings.ColorSetting;
import Myaven.setting.settings.DescriptionSetting;
import Myaven.setting.settings.ModeSetting;
import Myaven.setting.settings.SliderSetting;
import java.awt.Color;

public class Theme extends Module {
   public static DescriptionSetting themeDescriptionLine1;
   public static DescriptionSetting themeDescriptionLine2;
   public static DescriptionSetting themeDescriptionLine3;
   public static DescriptionSetting themeDescriptionLine4;
   public static ModeSetting themeSelection;
   public static SliderSetting colorOffset;
   public static SliderSetting timerMultiplier;
   public static ColorSetting customColorPrimary;
   public static ColorSetting customColorSecondary;
   private static Setting[] K;
   private static String[] a;
   private static String[] b;
   private static long[] d;
   private static Integer[] g;

   public Theme() {
      super("Theme", true, Category.Configuration, false, "The color theme of the client", false, true);
      this.addSettings(
         new Setting[]{
            themeDescriptionLine1,
            themeDescriptionLine2,
            themeDescriptionLine3,
            themeDescriptionLine4,
            themeSelection,
            colorOffset,
            timerMultiplier,
            customColorPrimary,
            customColorSecondary
         }
      );
   }

   public static int computeCustomThemeColor(double offset) {
      return Themes.E(offset, timerMultiplier.getRoundedValue(), new Color(customColorPrimary.F()), new Color(customColorSecondary.F()));
   }

   public static int computeThemeColor(double offset) {
      String var3 = themeSelection.getCurrent();
      switch (var3) {
         case "RAINBOW":
            return Themes.I(offset, timerMultiplier.getRoundedValue());
         case "CHERRY":
            return Themes.E(offset, timerMultiplier.getRoundedValue(), Themes.CHERRY.B(), Themes.CHERRY.M());
         case "COTTON_CANDY":
            return Themes.E(offset, timerMultiplier.getRoundedValue(), Themes.COTTON_CANDY.B(), Themes.COTTON_CANDY.M());
         case "FLARE":
            return Themes.E(offset, timerMultiplier.getRoundedValue(), Themes.FLARE.B(), Themes.FLARE.M());
         case "FLOWER":
            return Themes.E(offset, timerMultiplier.getRoundedValue(), Themes.FLOWER.B(), Themes.FLOWER.M());
         case "GOLD":
            return Themes.E(offset, timerMultiplier.getRoundedValue(), Themes.GOLD.B(), Themes.GOLD.M());
         case "GRAYSCALE":
            return Themes.E(offset, timerMultiplier.getRoundedValue(), Themes.GRAYSCALE.B(), Themes.GRAYSCALE.M());
         case "ROYAL":
            return Themes.E(offset, timerMultiplier.getRoundedValue(), Themes.ROYAL.B(), Themes.ROYAL.M());
         case "SKY":
            return Themes.E(offset, timerMultiplier.getRoundedValue(), Themes.SKY.B(), Themes.SKY.M());
         case "VINE":
            return Themes.E(offset, timerMultiplier.getRoundedValue(), Themes.VINE.B(), Themes.VINE.M());
         case "VAPE":
            return Themes.E(offset, timerMultiplier.getRoundedValue(), Themes.VAPE.B(), Themes.VAPE.M());
         default:
            return computeCustomThemeColor(offset);
      }
   }

   // $VF: Irreducible bytecode was duplicated to produce valid code
   static {
      themeDescriptionLine1 = new DescriptionSetting("CUSTOM, SKY, GOLD, ");
      themeDescriptionLine2 = new DescriptionSetting("RAINBOW, COTTON_CANDY, ");
      themeDescriptionLine3 = new DescriptionSetting("CHERRY, FLOWER, GRAYSCALE,");
      themeDescriptionLine4 = new DescriptionSetting("ROYAL, VAPE, VINE");
      themeSelection = new ModeSetting(
              "Theme", "CUSTOM", "SKY", "GOLD", "RAINBOW", "COTTON_CANDY", "CHERRY", "FLOWER", "GRAYSCALE", "ROYAL", "VAPE", "VINE"
      );
      colorOffset = new SliderSetting("Offset", 1.0, -10.0, 10.0, 0.1);
      timerMultiplier = new SliderSetting("Timer-multiplier", 0.5, 0.1, 4.0, 0.1);
      customColorPrimary = new ColorSetting("Custom-color-1", "4FC3F7");
      customColorSecondary = new ColorSetting("Custom-color-2", "81D4FA");
   }
}
