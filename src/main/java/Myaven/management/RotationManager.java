package Myaven.management;

import Myaven.Myaven;
import Myaven.events.MoveInputEvent;
import Myaven.events.PacketReceiveEvent;
import Myaven.events.PlayerUpdateEvent;
import Myaven.events.PreMotionEvent;
import Myaven.module.modules.visual.FreeLook;
import Myaven.setting.Setting;
import Myaven.util.PlayerUtil;
import Myaven.util.RotationUtil;

import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import net.minecraft.util.MathHelper;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class RotationManager {
   public static float lastYaw;
   public static float lastPitch;
   public static float currentYaw;
   public static float currentPitch;
   public static float movementYaw;
   public static MovementMode movementMode = MovementMode.NONE;
   public static boolean overrideActive = false;

   public RotationManager() {
      MinecraftForge.EVENT_BUS.register(this);
   }

   public static float[] getRotationAngles() {
      return new float[]{currentYaw, currentPitch};
   }

   public static void syncRotationWithPlayer() {
      overrideActive = false;
      movementMode = MovementMode.NONE;
      if (FreeLook.isActive) {
         if (currentYaw != FreeLook.freeLookYaw) {
            if (Myaven.mc.thePlayer.isRiding()) {
               currentYaw = FreeLook.freeLookYaw;
            } else {
               currentYaw = currentYaw + RotationUtil.getAngleDifference(currentYaw, FreeLook.freeLookYaw);
            }
         }

         movementYaw = FreeLook.freeLookYaw;
      } else {
         if (currentYaw != Myaven.mc.thePlayer.rotationYaw) {
            if (Myaven.mc.thePlayer.isRiding()) {
               currentYaw = Myaven.mc.thePlayer.rotationYaw;
            } else {
               currentYaw = currentYaw + RotationUtil.getAngleDifference(currentYaw, Myaven.mc.thePlayer.rotationYaw);
               Myaven.mc.thePlayer.rotationYaw = currentYaw;
            }
         }

         movementYaw = Myaven.mc.thePlayer.rotationYaw;
      }

      currentPitch = FreeLook.isActive ? FreeLook.freeLookPitch : Myaven.mc.thePlayer.rotationPitch;
   }

   public static void setRotation(float yaw, float pitch) {
      currentYaw = yaw;
      currentPitch = pitch;
      overrideActive = true;
   }

   public static void rotateYawBy(float yawIncrement) {
      currentYaw += yawIncrement;
      overrideActive = true;
   }

   public static void setYaw(float yaw) {
      currentYaw = yaw;
      overrideActive = true;
   }

   public static void rotateTowardsYaw(float yaw) {
      rotateYawBy(RotationUtil.getAngleDifference(currentYaw, yaw));
   }

   public static void stepYawTowards(float yaw, float step) {
      rotateYawBy(RotationUtil.clampFloat(RotationUtil.getAngleDifference(currentYaw, yaw), -step, step));
   }

   public static void setPitch(float pitch) {
      currentPitch = RotationUtil.clampFloat(pitch, -90.0F, 90.0F);
      overrideActive = true;
   }

   public static float getYaw() {
      return currentYaw;
   }

   public static float getPitch() {
      return currentPitch;
   }

   public static void setMovementMode(MovementMode fix) {
      movementMode = fix;
   }

   @SubscribeEvent
   public void onMotion(PreMotionEvent event) {
      event.setYaw(currentYaw);
      event.setPitch(currentPitch);
   }

   @SubscribeEvent
   public void onMove(MoveInputEvent event) {
      switch (movementMode) {
         case NONE:
            if (FreeLook.isActive) {
               movementYaw = FreeLook.freeLookYaw;
            } else {
               movementYaw = Myaven.mc.thePlayer.rotationYaw;
            }
            break;
         case SILENT:
            float yaw;
            if (FreeLook.isActive) {
               yaw = FreeLook.freeLookYaw;
               movementYaw = overrideActive ? currentYaw : FreeLook.freeLookYaw;
            } else {
               yaw = Myaven.mc.thePlayer.rotationYaw;
               movementYaw = currentYaw;
            }

            float forward = event.getForward();
            float strafe = event.getStrafe();
            double angle = MathHelper.wrapAngleTo180_double(Math.toDegrees(PlayerUtil.getDirection2(yaw, (double)forward, (double)strafe)));
            if (forward == 0.0F && strafe == 0.0F) {
               return;
            }

            float closestForward = 0.0F;
            float closestStrafe = 0.0F;
            float closestDifference = Float.MAX_VALUE;

            for (float predictedForward = -1.0F; predictedForward <= 1.0F; predictedForward++) {
               for (float predictedStrafe = -1.0F; predictedStrafe <= 1.0F; predictedStrafe++) {
                  if (predictedStrafe != 0.0F || predictedForward != 0.0F) {
                     double predictedAngle = MathHelper.wrapAngleTo180_double(
                        Math.toDegrees(PlayerUtil.getDirection2(movementYaw, (double)predictedForward, (double)predictedStrafe))
                     );
                     double difference = Math.abs(angle - predictedAngle);
                     if (difference < (double)closestDifference) {
                        closestDifference = (float)difference;
                        closestForward = predictedForward;
                        closestStrafe = predictedStrafe;
                     }
                  }
               }
            }

            event.setForward(closestForward);
            event.setStrafe(closestStrafe);
            break;
         case STRICT:
            if (FreeLook.isActive) {
               movementYaw = FreeLook.freeLookYaw;
            } else {
               movementYaw = currentYaw;
            }
      }
   }

   @SubscribeEvent
   public void onUpdate(PlayerUpdateEvent event) {
      lastYaw = currentYaw;
      lastPitch = currentPitch;
      if (!overrideActive) {
         if (FreeLook.isActive) {
            if (currentYaw != FreeLook.freeLookYaw) {
               if (Myaven.mc.thePlayer.isRiding()) {
                  currentYaw = FreeLook.freeLookYaw;
               } else {
                  currentYaw = currentYaw + RotationUtil.getAngleDifference(currentYaw, FreeLook.freeLookYaw);
               }
            }

            movementYaw = FreeLook.freeLookYaw;
         } else {
            if (currentYaw != Myaven.mc.thePlayer.rotationYaw) {
               if (Myaven.mc.thePlayer.isRiding()) {
                  currentYaw = Myaven.mc.thePlayer.rotationYaw;
               } else {
                  currentYaw = currentYaw + RotationUtil.getAngleDifference(currentYaw, Myaven.mc.thePlayer.rotationYaw);
                  Myaven.mc.thePlayer.rotationYaw = currentYaw;
               }
            }

            movementYaw = Myaven.mc.thePlayer.rotationYaw;
         }

         currentPitch = FreeLook.isActive ? FreeLook.freeLookPitch : Myaven.mc.thePlayer.rotationPitch;
      }
   }

   @SubscribeEvent
   public void onPacket(PacketReceiveEvent event) {
      if (event.getPacket() instanceof S08PacketPlayerPosLook) {
         currentYaw = ((S08PacketPlayerPosLook)event.getPacket()).getYaw();
         currentPitch = ((S08PacketPlayerPosLook)event.getPacket()).getPitch();
         movementYaw = ((S08PacketPlayerPosLook)event.getPacket()).getYaw();
         if (FreeLook.isActive) {
            FreeLook.freeLookYaw = currentYaw;
            FreeLook.freeLookPitch = currentPitch;
         }

         lastYaw = currentYaw;
         lastPitch = currentPitch;
      }
   }

   
   public static float getLastYaw() {
      return lastYaw;
   }

   
   public static float getLastPitch() {
      return lastPitch;
   }

   
   public static float getCurrentYaw() {
      return currentYaw;
   }

   
   public static float getCurrentPitch() {
      return currentPitch;
   }

   
   public static float getMovementYaw() {
      return movementYaw;
   }

   
   public static RotationManager.MovementMode getMovementMode() {
      return movementMode;
   }

   
   public static boolean getOverride() {
      return overrideActive;
   }

   public static enum MovementMode {
      SILENT,
      STRICT,
      NONE;
   }
}
