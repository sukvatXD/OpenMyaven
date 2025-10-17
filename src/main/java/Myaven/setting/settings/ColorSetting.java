package Myaven.setting.settings;

import Myaven.setting.Setting;
import java.awt.Color;

public class ColorSetting extends Setting {
   private String color;

   public ColorSetting(String name, String alpha) {
      this.name = name;
      this.color = alpha;
   }

   public int F() {
      Color c = new Color(Integer.parseInt(this.color, 16));
      return new Color(c.getRed(), c.getGreen(), c.getBlue(), 255).getRGB();
   }

   public String getColor() {
      return this.color;
   }

   public void setColor(String alpha) {
      this.color = alpha;
   }
}
