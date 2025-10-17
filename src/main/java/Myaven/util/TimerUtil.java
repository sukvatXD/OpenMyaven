package Myaven.util;

public class TimerUtil {
   public long lastReset = System.currentTimeMillis();

   public void reset() {
      this.lastReset = System.currentTimeMillis();
   }

   public boolean hasReached(float milliSec) {
      return (float)roundToIncrement((double)(this.getElapsed() - this.lastReset), 50.0) >= milliSec;
   }

   public static double roundToIncrement(double val, double inc) {
      double one = 1.0 / inc;
      return (double)Math.round(val * one) / one;
   }

   public boolean hasTimePassed(long time, boolean reset) {
      if (System.currentTimeMillis() - this.lastReset > time) {
         if (reset) {
            this.reset();
         }

         return true;
      } else {
         return false;
      }
   }

   public boolean hasTimePassed(long time) {
      return System.currentTimeMillis() - this.lastReset > time;
   }

   public boolean hasTimePassed(double time) {
      return this.hasTimePassed((long)time);
   }

   public long getElapsed() {
      return System.currentTimeMillis() - this.lastReset;
   }

   private static RuntimeException a(RuntimeException runtimeException) {
      return runtimeException;
   }
}
