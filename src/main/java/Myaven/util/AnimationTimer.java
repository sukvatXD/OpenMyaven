package Myaven.util;

public class AnimationTimer {
   public float duration;
   public long startTime;
   public float currentValue;

   public AnimationTimer(float updates) {
      this.duration = updates;
   }

   public float interpolate(float begin, float end, int type) {
      if (this.currentValue == end) {
         return this.currentValue;
      } else {
         float t = (float)(System.currentTimeMillis() - this.startTime) / this.duration;
         switch (type) {
            case 1:
               t = t < 0.5F ? 4.0F * t * t * t : (t - 1.0F) * (2.0F * t - 2.0F) * (2.0F * t - 2.0F) + 1.0F;
               break;
            case 2:
               t = (float)(1.0 - Math.pow((double)(1.0F - t), 5.0));
               break;
            case 3:
               t = this.applyBounceEasing(t);
               break;
            case 4:
               t = this.applyOffset(t);
         }

         float value = begin + t * (end - begin);
         if (end > begin && value > end || end < begin && value < end) {
            value = end;
         }

         if (value == end) {
            this.currentValue = value;
         }

         return value;
      }
   }

   public int interpolateInt(int begin, int end, int type) {
      return Math.round(this.interpolate((float)begin, (float)end, type));
   }

   public void reset() {
      this.currentValue = 0.0F;
      this.startTime = System.currentTimeMillis();
   }

   private float applyBounceEasing(float t) {
      double i2 = 7.5625;
      double i3 = 2.75;
      float i;
      if ((double)t < 0.36363636363636365) {
         i = (float)(7.5625 * (double)t * (double)t);
      } else if ((double)t < 0.7272727272727273) {
         float var8;
         i = (float)(7.5625 * (double)(var8 = (float)((double)t - 0.5454545454545454)) * (double)var8 + 0.75);
      } else if ((double)t < 0.9090909090909091) {
         float var9;
         i = (float)(7.5625 * (double)(var9 = (float)((double)t - 0.8181818181818182)) * (double)var9 + 0.9375);
      } else {
         float var10;
         i = (float)(7.5625 * (double)(var10 = (float)((double)t - 0.9545454545454546)) * (double)var10 + 0.984375);
      }

      return i;
   }

   float applyOffset(float t) {
      return t < 0.5F ? 2.0F * t * t : -1.0F + (4.0F - 2.0F * t) * t;
   }

   private static RuntimeException a(RuntimeException runtimeException) {
      return runtimeException;
   }
}
