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
import Myaven.setting.settings.PercentageSetting;
import Myaven.setting.settings.SliderSetting;
import akka.japi.Pair;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class ArrayListMod extends Module {
   public static PercentageSetting backgroundOpacity;
   public static DescriptionSetting modeDescription;
   public static ModeSetting mode;
   public static DescriptionSetting theme;
   public static ModeSetting color;
   public static ColorSetting customColor;
   public static BooleanSetting showSuffix;
   public static BooleanSetting textShadow;
   public static BooleanSetting lowerCase;
   public static BooleanSetting bar;
   public static SliderSetting offsetX;
   public static SliderSetting offsetY;
   public static SliderSetting scale;

   public ArrayListMod() {
      super("ArrayList", true, Category.Visual, false, "Show a list of modules on screen");
      this.addSettings(
         new Setting[]{backgroundOpacity, modeDescription, mode, theme, color, customColor, showSuffix, textShadow, lowerCase, bar, offsetX, offsetY, scale}
      );
   }

   @SubscribeEvent
   public void onRender(Render2DEvent event) {
      GlStateManager.pushMatrix();
      GlStateManager.scale(scale.getRoundedValue(), scale.getRoundedValue(), scale.getRoundedValue());
      ScaledResolution sr = new ScaledResolution(Minecraft.getMinecraft());
      FontRenderer font = Minecraft.getMinecraft().fontRendererObj;
      List<Pair<Module, Boolean>> printList = new ArrayList<>();
      List<Module> modList = new ArrayList<>();

      for (Module mod : Myaven.moduleManager.F()) {
         if (mod.setVisible() && mod.isEnabled()) {
            modList.add(mod);
         }
      }

      modList.sort(
         Comparator.comparingInt(
            o -> {
               return font.getStringWidth(
                  o.getSuffix() != null && showSuffix.getState()
                     ? (lowerCase.getState() ? o.l(true).toLowerCase() + " " + o.getSuffix().toLowerCase() : o.l(true) + " " + o.getSuffix())
                     : (lowerCase.getState() ? o.l(true).toLowerCase() : o.l(true))
               );
            }
         )
      );
      Collections.reverse(modList);

      for (int i = 0; i < modList.size(); i++) {
         Module modx = modList.get(i);
         Pair<Module, Boolean> pair = new Pair(modx, showSuffix.getState() ? modx.getSuffix() != null : false);
         printList.add(pair);
      }

      float currentY = (float)((int)((double)((float)(1.0 * scale.getRoundedValue())) + offsetY.getRoundedValue()));
      float startX = (float)(
         (int)(
            mode.getCurrent().equalsIgnoreCase("RIGHT")
               ? (double)((float)sr.getScaledWidth() - (float)(4.0 * scale.getRoundedValue())) - offsetX.getRoundedValue()
               : (double)((float)(4.0 * scale.getRoundedValue())) + offsetX.getRoundedValue()
         )
      );
      float barLeftX = mode.getCurrent().equalsIgnoreCase("RIGHT") ? startX : (float)((int)offsetX.getRoundedValue()) + (float)(2.0 * scale.getRoundedValue());
      int offset = 0;
      float scaleVal = (float)scale.getRoundedValue();

      for (Pair<Module, Boolean> target : printList) {
         String fontHeight = ArrayListMod.color.getCurrent();
         int color;
         switch (fontHeight) {
            case "THEME":
               color = Theme.computeThemeColor((double)offset);
               break;
            case "THEME_CUSTOM":
               color = Theme.computeCustomThemeColor((double)offset);
               break;
            default:
               color = customColor.F();
         }

         offset += (int)Theme.colorOffset.getRoundedValue();
         float fontHeight2 = (float)font.FONT_HEIGHT * scaleVal;
         float rectHeight = fontHeight2 + 2.0F * scaleVal;
         float totalStringWidth = target.second()
            ? (float)font.getStringWidth(
                  lowerCase.getState()
                     ? ((Module)target.first()).l(true).toLowerCase() + " " + ((Module)target.first()).getSuffix().toLowerCase()
                     : ((Module)target.first()).l(true) + " " + ((Module)target.first()).getSuffix()
               )
               * scaleVal
            : (float)font.getStringWidth(lowerCase.getState() ? ((Module)target.first()).l(true).toLowerCase() : ((Module)target.first()).l(true)) * scaleVal;
         float nameWidth = (float)font.getStringWidth(lowerCase.getState() ? ((Module)target.first()).l(true).toLowerCase() : ((Module)target.first()).l(true))
            * scaleVal;
         float spaceWidth = (float)font.getStringWidth(" ") * scaleVal;
         float suffixWidth = 0.0F;
         if ((Boolean)target.second()) {
            suffixWidth = (float)font.getStringWidth(
                  lowerCase.getState() ? ((Module)target.first()).getSuffix().toLowerCase() : ((Module)target.first()).getSuffix()
               )
               * scaleVal;
         }

         if (bar.getState()) {
            Gui.drawRect(
               (int)(barLeftX / scaleVal),
               (int)(currentY / scaleVal),
               (int)((barLeftX + 2.0F * scaleVal) / scaleVal),
               (int)((currentY + rectHeight) / scaleVal),
               color
            );
         }

         if (mode.getCurrent().equalsIgnoreCase("RIGHT")) {
            Gui.drawRect(
               (int)((startX - totalStringWidth - (float)(5.0 * scale.getRoundedValue())) / scaleVal),
               (int)(currentY / scaleVal),
               (int)(startX / scaleVal),
               (int)((currentY + rectHeight) / scaleVal),
               new Color(0, 0, 0, 255 * backgroundOpacity.getPercentage() / 100).getRGB()
            );
            if ((Boolean)target.second()) {
               if (lowerCase.getState()) {
                  this.mc
                     .fontRendererObj
                     .drawString(
                        ((Module)target.first()).l(true).toLowerCase(),
                        (startX - totalStringWidth - (float)(2.0 * scale.getRoundedValue())) / scaleVal,
                        (currentY + (float)(1.0 * scale.getRoundedValue())) / scaleVal,
                        color,
                        textShadow.getState()
                     );
                  this.mc
                     .fontRendererObj
                     .drawString(
                        ((Module)target.first()).getSuffix().toLowerCase(),
                        (startX - suffixWidth - (float)(2.0 * scale.getRoundedValue())) / scaleVal,
                        (currentY + (float)(1.0 * scale.getRoundedValue())) / scaleVal,
                        11184810,
                        textShadow.getState()
                     );
               } else {
                  this.mc
                     .fontRendererObj
                     .drawString(
                        ((Module)target.first()).l(true),
                        (startX - totalStringWidth - (float)(2.0 * scale.getRoundedValue())) / scaleVal,
                        (currentY + (float)(1.0 * scale.getRoundedValue())) / scaleVal,
                        color,
                        textShadow.getState()
                     );
                  this.mc
                     .fontRendererObj
                     .drawString(
                        ((Module)target.first()).getSuffix(),
                        (startX - suffixWidth - (float)(2.0 * scale.getRoundedValue())) / scaleVal,
                        (currentY + (float)(1.0 * scale.getRoundedValue())) / scaleVal,
                        11184810,
                        textShadow.getState()
                     );
               }
            } else if (lowerCase.getState()) {
               this.mc
                  .fontRendererObj
                  .drawString(
                     ((Module)target.first()).l(true).toLowerCase(),
                     (startX - nameWidth - (float)(2.0 * scale.getRoundedValue())) / scaleVal,
                     (currentY + (float)(1.0 * scale.getRoundedValue())) / scaleVal,
                     color,
                     textShadow.getState()
                  );
            } else {
               this.mc
                  .fontRendererObj
                  .drawString(
                     ((Module)target.first()).l(true),
                     (startX - nameWidth - (float)(2.0 * scale.getRoundedValue())) / scaleVal,
                     (currentY + (float)(1.0 * scale.getRoundedValue())) / scaleVal,
                     color,
                     textShadow.getState()
                  );
            }
         } else {
            Gui.drawRect(
               (int)(startX / scaleVal),
               (int)(currentY / scaleVal),
               (int)((startX + totalStringWidth + (float)(5.0 * scale.getRoundedValue())) / scaleVal),
               (int)((currentY + rectHeight) / scaleVal),
               new Color(0, 0, 0, 255 * backgroundOpacity.getPercentage() / 100).getRGB()
            );
            if (lowerCase.getState()) {
               this.mc
                  .fontRendererObj
                  .drawString(
                     ((Module)target.first()).l(true).toLowerCase(),
                     (startX + (float)(2.0 * scale.getRoundedValue())) / scaleVal,
                     (currentY + (float)(1.0 * scale.getRoundedValue())) / scaleVal,
                     color,
                     textShadow.getState()
                  );
            } else {
               this.mc
                  .fontRendererObj
                  .drawString(
                     ((Module)target.first()).l(true),
                     (startX + (float)(2.0 * scale.getRoundedValue())) / scaleVal,
                     (currentY + (float)(1.0 * scale.getRoundedValue())) / scaleVal,
                     color,
                     textShadow.getState()
                  );
            }

            if ((Boolean)target.second()) {
               if (lowerCase.getState()) {
                  this.mc
                     .fontRendererObj
                     .drawString(
                        ((Module)target.first()).getSuffix().toLowerCase(),
                        (startX + nameWidth + spaceWidth + (float)(2.0 * scale.getRoundedValue())) / scaleVal,
                        (currentY + (float)(1.0 * scale.getRoundedValue())) / scaleVal,
                        11184810,
                        textShadow.getState()
                     );
               } else {
                  this.mc
                     .fontRendererObj
                     .drawString(
                        ((Module)target.first()).getSuffix(),
                        (startX + nameWidth + spaceWidth + (float)(2.0 * scale.getRoundedValue())) / scaleVal,
                        (currentY + (float)(1.0 * scale.getRoundedValue())) / scaleVal,
                        11184810,
                        textShadow.getState()
                     );
               }
            }
         }

         currentY += rectHeight;
      }

      GlStateManager.popMatrix();
   }

   // $VF: Irreducible bytecode was duplicated to produce valid code
   static {
      backgroundOpacity = new PercentageSetting("Background-opacity", 40);
      modeDescription = new DescriptionSetting("RIGHT, LEFT");
      mode = new ModeSetting("Mode", "RIGHT", "LEFT");
      theme = new DescriptionSetting("THEME, THEME_CUSTOM, CUSTOM");
      color = new ModeSetting("Color", "THEME", "THEME_CUSTOM", "CUSTOM");
      customColor = new ColorSetting("Custom-color", "FFFFFF");
      showSuffix = new BooleanSetting("Show-suffix", true);
      textShadow = new BooleanSetting("Text-shadow", true);
      lowerCase = new BooleanSetting("Lowercase", true);
      bar = new BooleanSetting("Bar", true);
      offsetX = new SliderSetting("Offset-X", 4.0, 0.0, 100.0, 1.0);
      offsetY = new SliderSetting("Offset-Y", 1.0, 0.0, 100.0, 1.0);
      scale = new SliderSetting("Scale", 1.0, 0.25, 3.0, 0.01);
   }
}
