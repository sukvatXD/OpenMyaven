package Myaven.events;

import Myaven.Myaven;
import Myaven.setting.Setting;
import Myaven.util.PlayerUtil;

import net.minecraftforge.fml.common.eventhandler.Cancelable;
import net.minecraftforge.fml.common.eventhandler.Event;

@Cancelable
public class StrafeEvent extends Event {
   private float strafe;
   private float forward;
   private float friction;
   private float yaw;

   public StrafeEvent(float strafe, float forward, float friction, float yaw) {
      this.strafe = strafe;
      this.forward = forward;
      this.friction = friction;
      this.yaw = yaw;
   }

   public void setSpeed(double speed, double motionMultiplier) {
      this.setFriction((float)(this.getForward() != 0.0F && this.getStrafe() != 0.0F ? speed * 0.98F : speed));
      Myaven.mc.thePlayer.motionX *= motionMultiplier;
      Myaven.mc.thePlayer.motionZ *= motionMultiplier;
   }

   public void setSpeed(double speed) {
      this.setFriction((float)(this.getForward() != 0.0F && this.getStrafe() != 0.0F ? speed * 0.98F : speed));
      PlayerUtil.stopAllMotion();
   }

   
   public float getStrafe() {
      return this.strafe;
   }

   
   public float getForward() {
      return this.forward;
   }

   
   public float getFriction() {
      return this.friction;
   }

   
   public float getYaw() {
      return this.yaw;
   }

   
   public void setStrafe(float strafe) {
      this.strafe = strafe;
   }

   
   public void setForward(float forward) {
      this.forward = forward;
   }

   
   public void setFriction(float friction) {
      this.friction = friction;
   }

   
   public void setYaw(float yaw) {
      this.yaw = yaw;
   }

   private static RuntimeException a(RuntimeException runtimeException) {
      return runtimeException;
   }
}
