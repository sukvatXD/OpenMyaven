package Myaven.setting.settings;

import Myaven.setting.Setting;

public class PercentageSetting extends Setting {
   private int percentage;

   public PercentageSetting(String name, int percentage) {
      this.name = name;
      this.percentage = percentage;
   }

   public void setPercentageClamped(int value) {
      this.percentage = this.clampValue(value, 0, 100);
   }

   private int clampValue(int value, int min, int max) {
      return Math.min(max, Math.max(min, value));
   }

   public int getPercentage() {
      return this.percentage;
   }

   public void setPercentage(int percentage) {
      this.percentage = percentage;
   }
}
