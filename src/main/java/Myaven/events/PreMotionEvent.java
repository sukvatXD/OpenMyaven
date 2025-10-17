package Myaven.events;


import net.minecraftforge.fml.common.eventhandler.Cancelable;
import net.minecraftforge.fml.common.eventhandler.Event;

@Cancelable
public class PreMotionEvent extends Event {
   private double posX;
   private double posY;
   private double posZ;
   private float yaw;
   private float pitch;
   private boolean onGround;
   private boolean isSprinting;
   private boolean isSneaking;
   private boolean cancelPacket;

   public PreMotionEvent(
      double posX, double posY, double posZ, float yaw, float pitch, boolean onGround, boolean isSprinting, boolean isSneaking, boolean cancelPacket
   ) {
      this.posX = posX;
      this.posY = posY;
      this.posZ = posZ;
      this.yaw = yaw;
      this.pitch = pitch;
      this.onGround = onGround;
      this.isSprinting = isSprinting;
      this.isSneaking = isSneaking;
      this.cancelPacket = cancelPacket;
   }

   
   public double getPosX() {
      return this.posX;
   }

   
   public double getPosY() {
      return this.posY;
   }

   
   public double getPosZ() {
      return this.posZ;
   }

   
   public float getYaw() {
      return this.yaw;
   }

   
   public float getPitch() {
      return this.pitch;
   }

   
   public boolean isOnGround() {
      return this.onGround;
   }

   
   public boolean isSprinting() {
      return this.isSprinting;
   }

   
   public boolean isSneaking() {
      return this.isSneaking;
   }

   
   public boolean isCancelPacket() {
      return this.cancelPacket;
   }

   
   public void setX(double posX) {
      this.posX = posX;
   }

   
   public void setY(double posY) {
      this.posY = posY;
   }

   
   public void setZ(double posZ) {
      this.posZ = posZ;
   }

   
   public void setYaw(float yaw) {
      this.yaw = yaw;
   }

   
   public void setPitch(float pitch) {
      this.pitch = pitch;
   }

   
   public void setGround(boolean onGround) {
      this.onGround = onGround;
   }

   
   public void setSprint(boolean isSprinting) {
      this.isSprinting = isSprinting;
   }

   
   public void setSneak(boolean isSneaking) {
      this.isSneaking = isSneaking;
   }

   
   public void setCancelPacket(boolean cancelPacket) {
      this.cancelPacket = cancelPacket;
   }
}
