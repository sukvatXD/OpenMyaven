package Myaven.enums;

import Myaven.util.ClientUtil;
import java.awt.Color;


public enum Themes {
   RAINBOW(null,null),
   CHERRY(new Color(-14136), new Color(-837014)),
   COTTON_CANDY(new Color(-10225153), new Color(-38708)),
   FLARE(new Color(-1628392), new Color(-676559)),
   FLOWER(new Color(-2644249), new Color(-2925848)),
   GOLD(new Color(-10496), new Color(-1007872)),
   GRAYSCALE(new Color(-986896), new Color(-9539986)),
   ROYAL(new Color(-8532751), new Color(-14792790)),
   SKY(new Color(-6232351), new Color(-15745316)),
   VINE(new Color(-15613907), new Color(-3544378)),
   VAPE(new Color(-15365766), new Color(-13875121)),
   CUSTOM(new Color(-11549705), new Color(-8268550));

   private Color e;
   private Color S;

   public static String[] u() {
      String[] theReturn = new String[values().length];

      for (Themes theme : values()) {
         theReturn[theme.ordinal()] = theme.name().toUpperCase();
      }

      return theReturn;
   }

   private Themes(Color firstGradient, Color secondGradient) {
      this.e = firstGradient;
      this.S = secondGradient;
   }

   public static int I(double offset, double timerMultiplier) {
      long time = System.currentTimeMillis();
      long speed = (long)(2000.0 / timerMultiplier);
      long phase = (long)((double)time + offset * 50.0);
      float hue = (float)((double)(phase % speed) / (double)speed);
      return Color.HSBtoRGB(hue, 1.0F, 1.0F);
   }

   public static int E(double offset, double timerMultiplier, Color c1, Color c2) {
      long time = System.currentTimeMillis();
      long speed = (long)(2000.0 / timerMultiplier);
      long phase = (long)((double)time + offset * 50.0);
      float progress = (float)((double)(phase % speed) / (double)speed);
      float oscillate = progress - 0.5F;
      int r = (int)((float)c1.getRed() * (1.0F - oscillate) + (float)c2.getRed() * oscillate);
      int g = (int)((float)c1.getGreen() * (1.0F - oscillate) + (float)c2.getGreen() * oscillate);
      int b = (int)((float)c1.getBlue() * (1.0F - oscillate) + (float)c2.getBlue() * oscillate);
      return new Color(r, g, b).getRGB();
   }

   
   public Color B() {
      return this.e;
   }

   
   public Color M() {
      return this.S;
   }
}
