package Myaven.events;


import net.minecraftforge.fml.common.eventhandler.Cancelable;
import net.minecraftforge.fml.common.eventhandler.Event;

@Cancelable
public class KnockbackEvent extends Event {
   private float motionX;
   private float motionY;
   private float motionZ;

   public KnockbackEvent(float motionX, float motionY, float motionZ) {
      this.motionX = motionX;
      this.motionY = motionY;
      this.motionZ = motionZ;
   }

   
   public float getMotionX() {
      return this.motionX;
   }

   
   public float getMotionY() {
      return this.motionY;
   }

   
   public float getMotionZ() {
      return this.motionZ;
   }

   
   public void setMotionX(float motionX) {
      this.motionX = motionX;
   }

   
   public void setMotionY(float motionY) {
      this.motionY = motionY;
   }

   
   public void setMotionZ(float motionZ) {
      this.motionZ = motionZ;
   }
}
