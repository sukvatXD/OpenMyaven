package Myaven.util;

public class Animation {
   private Easing W;
   private long D;
   private long Q;
   private long G;
   private double T;
   private double y;
   private double J;
   private boolean E;

   public Animation(Easing easing, long duration) {
      this.W = easing;
      this.G = System.currentTimeMillis();
      this.D = duration;
   }

   public void k(double destinationValue) {
      this.Q = System.currentTimeMillis();
      if (this.y != destinationValue) {
         this.y = destinationValue;
         this.k();
      } else {
         this.E = this.Q - this.D > this.G;
         if (this.E) {
            this.J = destinationValue;
            return;
         }
      }

      double result = this.W.getFunction().apply(this.E());
      if (this.J > destinationValue) {
         this.J = this.T - (this.T - destinationValue) * result;
      } else {
         this.J = this.T + (destinationValue - this.T) * result;
      }
   }

   public double E() {
      return (double)(System.currentTimeMillis() - this.G) / (double)this.D;
   }

   public void k() {
      this.G = System.currentTimeMillis();
      this.T = this.J;
      this.E = false;
   }

   public void p(Easing easing) {
      this.W = easing;
   }

   public void T(long duration) {
      this.D = duration;
   }

   public void P(long millis) {
      this.Q = millis;
   }

   public void K(long startTime) {
      this.G = startTime;
   }

   public void E(double startValue) {
      this.T = startValue;
   }

   public void h(double destinationValue) {
      this.y = destinationValue;
   }

   public void w(double value) {
      this.J = value;
   }

   public void l(boolean finished) {
      this.E = finished;
   }

   public Easing l() {
      return this.W;
   }

   public long d() {
      return this.D;
   }

   public long h() {
      return this.Q;
   }

   public long n() {
      return this.G;
   }

   public double G() {
      return this.T;
   }

   public double A() {
      return this.y;
   }

   public double q() {
      return this.J;
   }

   public boolean Y() {
      return this.E;
   }

   private static RuntimeException a(RuntimeException runtimeException) {
      return runtimeException;
   }
}
