package Myaven.events;


import net.minecraftforge.fml.common.eventhandler.Cancelable;
import net.minecraftforge.fml.common.eventhandler.Event;

@Cancelable
public class JumpEvent extends Event {
   private float motionY;
   private float yaw;

   public JumpEvent(float motionY, float yaw) {
      this.motionY = motionY;
      this.yaw = yaw;
   }

   
   public float getMotionY() {
      return this.motionY;
   }

   
   public float getYaw() {
      return this.yaw;
   }

   
   public void setMotionY(float motionY) {
      this.motionY = motionY;
   }

   
   public void setYaw(float yaw) {
      this.yaw = yaw;
   }
}
