package Myaven.util;

import java.awt.Color;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.MathHelper;

public class ColorUtil {
   private static Map<Character, Color> colorCodeMap;
   private static Map<EnumChatFormatting, Color> chatFormattingColorMap;

   public static int toRGBA(Color var0) {
      return toARGB(var0.getRed(), var0.getGreen(), var0.getBlue(), var0.getAlpha());
   }

   public static int gray(int var0) {
      return toARGB(var0, var0, var0, 255);
   }

   public static int grayWithAlpha(int var0, int var1) {
      return toARGB(var0, var0, var0, var1);
   }

   public static int toARGB(int var0, int var1, int var2, int var3) {
      int var4 = MathHelper.clamp_int(var3, 0, 255) << 24;
      var4 |= MathHelper.clamp_int(var0, 0, 255) << 16;
      var4 |= MathHelper.clamp_int(var1, 0, 255) << 8;
      return var4 | MathHelper.clamp_int(var2, 0, 255);
   }

   public static Color getColorByCode(String code) {
      return colorCodeMap.getOrDefault(code.charAt(0), Color.WHITE);
   }

   public static Color getColorByChar(char code) {
      return colorCodeMap.getOrDefault(code, Color.WHITE);
   }

   public static String getHexByChar(char code) {
      Color color = getColorByChar(code);
      return String.format("#%02X%02X%02X", color.getRed(), color.getGreen(), color.getBlue());
   }

   public static Color getColorByFormatting(EnumChatFormatting formatting) {
      return colorCodeMap.getOrDefault(formatting, Color.WHITE);
   }

   public static String getHexByFormatting(EnumChatFormatting formatting) {
      Color c = getColorByFormatting(formatting);
      return String.format("#%02X%02X%02X", c.getRed(), c.getGreen(), c.getBlue());
   }

   // $VF: Irreducible bytecode was duplicated to produce valid code
   static {
      colorCodeMap = new HashMap<>();
      colorCodeMap.put('0', new Color(0, 0, 0));
      colorCodeMap.put('1', new Color(0, 0, 170));
      colorCodeMap.put('2', new Color(0, 170, 0));
      colorCodeMap.put('3', new Color(0, 170, 170));
      colorCodeMap.put('4', new Color(170, 0, 0));
      colorCodeMap.put('5', new Color(170, 0, 170));
      colorCodeMap.put('6', new Color(255, 170, 0));
      colorCodeMap.put('7', new Color(170, 170, 170));
      colorCodeMap.put('8', new Color(85, 85, 85));
      colorCodeMap.put('9', new Color(85, 85, 255));
      colorCodeMap.put('a', new Color(85, 255, 85));
      colorCodeMap.put('b', new Color(85, 255, 255));
      colorCodeMap.put('c', new Color(255, 85, 85));
      colorCodeMap.put('d', new Color(255, 85, 255));
      colorCodeMap.put('e', new Color(255, 255, 85));
      colorCodeMap.put('f', new Color(255, 255, 255));
      chatFormattingColorMap = new HashMap<>();
      chatFormattingColorMap.put(EnumChatFormatting.BLACK, new Color(0, 0, 0));
      chatFormattingColorMap.put(EnumChatFormatting.DARK_BLUE, new Color(0, 0, 170));
      chatFormattingColorMap.put(EnumChatFormatting.DARK_GREEN, new Color(0, 170, 0));
      chatFormattingColorMap.put(EnumChatFormatting.DARK_AQUA, new Color(0, 170, 170));
      chatFormattingColorMap.put(EnumChatFormatting.DARK_RED, new Color(170, 0, 0));
      chatFormattingColorMap.put(EnumChatFormatting.DARK_PURPLE, new Color(170, 0, 170));
      chatFormattingColorMap.put(EnumChatFormatting.GOLD, new Color(255, 170, 0));
      chatFormattingColorMap.put(EnumChatFormatting.GRAY, new Color(170, 170, 170));
      chatFormattingColorMap.put(EnumChatFormatting.DARK_GRAY, new Color(85, 85, 85));
      chatFormattingColorMap.put(EnumChatFormatting.BLUE, new Color(85, 85, 255));
      chatFormattingColorMap.put(EnumChatFormatting.GREEN, new Color(85, 255, 85));
      chatFormattingColorMap.put(EnumChatFormatting.AQUA, new Color(85, 255, 255));
      chatFormattingColorMap.put(EnumChatFormatting.RED, new Color(255, 85, 85));
      chatFormattingColorMap.put(EnumChatFormatting.LIGHT_PURPLE, new Color(255, 85, 255));
      chatFormattingColorMap.put(EnumChatFormatting.YELLOW, new Color(255, 255, 85));
      chatFormattingColorMap.put(EnumChatFormatting.WHITE, new Color(255, 255, 255));
   }
}
