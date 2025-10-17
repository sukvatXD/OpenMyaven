package Myaven.setting.settings;

import Myaven.setting.Setting;
import Myaven.util.ClientUtil;

public class SliderSetting extends Setting {
   private double value;
   private double minValue;
   private double maxValue;
   private double stepSize;

   public SliderSetting(String name, double defaultValue, double minValue, double maxValue, double increment) {
      this.name = name;
      this.value = defaultValue;
      this.minValue = minValue;
      this.maxValue = maxValue;
      this.stepSize = increment;
   }

   public double getRoundedValue() {
      return ClientUtil.roundToTwoDecimals(this.value);
   }

   public void setValue(double value) {
      double n = this.clampValue(value, this.minValue, this.maxValue);
      n = (double)Math.round(n * (1.0 / this.stepSize)) / (1.0 / this.stepSize);
      this.value = n;
   }

   private double clampValue(double value, double min, double max) {
      return Math.min(max, Math.max(min, value));
   }

   public double getMinValue() {
      return this.minValue;
   }

   public double getMaxValue() {
      return this.maxValue;
   }

   public double getStepSize() {
      return this.stepSize;
   }
}
