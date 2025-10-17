package Myaven.util;

import Myaven.management.RotationManager;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import org.jetbrains.annotations.NotNull;

public class RotationUtil {
   static Minecraft F = Minecraft.getMinecraft();
   public static float L;
   public static float C;
   public static float N;
   public static float K;
   private static String[] O;

   public static float getYawToPosition(double n, double n2) {
      return (float)(Math.atan2(n - F.thePlayer.posX, n2 - F.thePlayer.posZ) * (float) (180.0 / Math.PI) * -1.0);
   }

   public static double getYawDifference(Entity entity) {
      return Math.abs(MathHelper.wrapAngleTo180_double((double)(getYawToPosition(entity.posX, entity.posZ) - F.thePlayer.rotationYaw)));
   }

   public static float getAngleDifference(float from, float to) {
      float delta = to % 360.0F - from % 360.0F;
      return ((delta + 180.0F) % 360.0F + 360.0F) % 360.0F - 180.0F;
   }

   public static Vec3 getEntityHitVec(Entity entity) {
      AxisAlignedBB bb = entity.getEntityBoundingBox();
      Vec3 eyePos = ClientUtil.getPlayerEyesPosition();
      float yaw = RotationManager.getCurrentYaw();
      float pitch = RotationManager.getCurrentPitch();
      Vec3 lookVec = getLookVector(yaw, pitch);
      Vec3 hitVec = getIntercept(eyePos, lookVec, bb);
      if (hitVec != null) {
         return hitVec;
      } else {
         double x = clampDouble(eyePos.xCoord, bb.minX, bb.maxX);
         double y = clampDouble(eyePos.yCoord, bb.minY, bb.maxY);
         double z = clampDouble(eyePos.zCoord, bb.minZ, bb.maxZ);
         return new Vec3(x, y, z);
      }
   }

   public static Vec3 getBlockHitVec(BlockPos blockPos) {
      Vec3 eyePos = ClientUtil.getPlayerEyesPosition();
      float yaw = RotationManager.getCurrentYaw();
      float pitch = RotationManager.getCurrentPitch();
      Vec3 lookVec = getLookVector(yaw, pitch);
      AxisAlignedBB bb = new AxisAlignedBB(
         (double)blockPos.getX() + 0.05,
         (double)blockPos.getY() + 0.05,
         (double)blockPos.getZ() + 0.05,
         (double)(blockPos.getX() + 1) - 0.05,
         (double)(blockPos.getY() + 1) - 0.05,
         (double)(blockPos.getZ() + 1) - 0.05
      );
      Vec3 hitVec = getIntercept(eyePos, lookVec, bb);
      if (hitVec != null) {
         return hitVec;
      } else {
         double x = Math.max(bb.minX, Math.min(eyePos.xCoord, bb.maxX));
         double y = Math.max(bb.minY, Math.min(eyePos.yCoord, bb.maxY));
         double z = Math.max(bb.minZ, Math.min(eyePos.zCoord, bb.maxZ));
         return new Vec3(x, y, z);
      }
   }

   private static Vec3 getLookVector(float yaw, float pitch) {
      double radYaw = Math.toRadians((double)(-yaw - 180.0F));
      double radPitch = Math.toRadians((double)(-pitch));
      double x = Math.sin(radYaw) * Math.cos(radPitch);
      double y = Math.sin(radPitch);
      double z = Math.cos(radYaw) * Math.cos(radPitch);
      return new Vec3(x, y, z);
   }

   private static Vec3 getIntercept(Vec3 start, Vec3 dir, AxisAlignedBB bb) {
      Vec3 end = start.addVector(dir.xCoord * 6.0, dir.yCoord * 6.0, dir.zCoord * 6.0);
      MovingObjectPosition mop = bb.calculateIntercept(start, end);
      return mop != null ? mop.hitVec : null;
   }

   public static Vec3 clampVecToBlock(Vec3 hitVec, BlockPos blockpos) {
      double x = clampDouble(hitVec.xCoord, (double)blockpos.getX() + 0.05, (double)(blockpos.getX() + 1) - 0.05);
      double y = clampDouble(hitVec.yCoord, (double)blockpos.getY() + 0.05, (double)(blockpos.getZ() + 1) - 0.05);
      double z = clampDouble(hitVec.zCoord, (double)blockpos.getZ() + 0.05, (double)(blockpos.getZ() + 1) - 0.05);
      return new Vec3(x, y, z);
   }

   public static double clampDouble(double val, double min, double max) {
      return val < min ? min : Math.min(val, max);
   }

   public static float clampFloat(float val, float min, float max) {
      return val < min ? min : Math.min(val, max);
   }

   public static Vec3 getVectorFromYaw(Vec3 eyePos, float yaw, double distance) {
      double radians = Math.toRadians((double)yaw);
      double dx = -Math.sin(radians) * distance;
      double dz = Math.cos(radians) * distance;
      return new Vec3(eyePos.xCoord + dx, eyePos.yCoord, eyePos.zCoord + dz);
   }

   public static Vec3 getOffsetFromBlock(BlockPos blockPos, EnumFacing facing, double offset) {
      double ex = ClientUtil.getPlayerEyesPosition().xCoord;
      double ez = ClientUtil.getPlayerEyesPosition().zCoord;
      double minX = (double)blockPos.getX() + 0.05;
      double maxX = (double)blockPos.getX() + 0.95;
      double y = (double)blockPos.getY() + 0.05;
      double minZ = (double)blockPos.getZ() + 0.05;
      double maxZ = (double)blockPos.getZ() + 0.95;
      double x;
      double z;
      switch (facing) {
         case UP:
            x = clampDouble(ClientUtil.getPlayerEyesPosition().xCoord, minX, maxX);
            y = Math.max((double)blockPos.getY() + 0.05, Math.min(ClientUtil.getPlayerEyesPosition().yCoord, (double)(blockPos.getY() + 1) - 0.05));
            z = clampDouble(ClientUtil.getPlayerEyesPosition().zCoord, minZ, maxZ);
            break;
         case NORTH:
            z = minZ;
            if (ex <= maxX) {
               if (minX + offset > ex) {
                  x = clampDouble(ClientUtil.getPlayerEyesPosition().xCoord + offset, minX, maxX);
               } else {
                  x = clampDouble(ClientUtil.getPlayerEyesPosition().xCoord - offset, minX, maxX);
               }
            } else {
               x = clampDouble(ClientUtil.getPlayerEyesPosition().xCoord - offset, minX, maxX);
            }
            break;
         case SOUTH:
            z = maxZ;
            if (ex >= minX) {
               if (maxX - offset < ex) {
                  x = clampDouble(ClientUtil.getPlayerEyesPosition().xCoord - offset, minX, maxX);
               } else {
                  x = clampDouble(ClientUtil.getPlayerEyesPosition().xCoord + offset, minX, maxX);
               }
            } else {
               x = clampDouble(ClientUtil.getPlayerEyesPosition().xCoord + offset, minX, maxX);
            }
            break;
         case WEST:
            x = minX;
            if (ez >= minZ) {
               if (maxZ - offset < ez) {
                  z = clampDouble(ClientUtil.getPlayerEyesPosition().zCoord - offset, minZ, maxZ);
               } else {
                  z = clampDouble(ClientUtil.getPlayerEyesPosition().zCoord + offset, minZ, maxZ);
               }
            } else {
               z = clampDouble(ClientUtil.getPlayerEyesPosition().zCoord + offset, minZ, maxZ);
            }
            break;
         case EAST:
            x = maxX;
            if (ez <= maxZ) {
               if (minZ + offset > ez) {
                  z = clampDouble(ClientUtil.getPlayerEyesPosition().zCoord + offset, minZ, maxZ);
               } else {
                  z = clampDouble(ClientUtil.getPlayerEyesPosition().zCoord - offset, minZ, maxZ);
               }
            } else {
               z = clampDouble(ClientUtil.getPlayerEyesPosition().zCoord - offset, minZ, maxZ);
            }
            break;
         default:
            x = Math.max((double)blockPos.getX() + 0.05, Math.min(ClientUtil.getPlayerEyesPosition().xCoord + offset, (double)(blockPos.getX() + 1) - 0.05));
            y = Math.max((double)blockPos.getY() + 0.05, Math.min(ClientUtil.getPlayerEyesPosition().yCoord, (double)(blockPos.getY() + 1) - 0.05));
            z = Math.max((double)blockPos.getZ() + 0.05, Math.min(ClientUtil.getPlayerEyesPosition().zCoord + offset, (double)(blockPos.getZ() + 1) - 0.05));
      }

      return new Vec3(x, y, z);
   }

   public static float[] getRotationFromBlock(BlockPos blockPos) {
      return getRotationToPosition((double)blockPos.getX() + 0.5, (double)blockPos.getY() + 0.5, (double)blockPos.getZ() + 0.5);
   }

   public static float[] getRotationBetween(double rotX, double rotY, double rotZ, double startX, double startY, double startZ) {
      double x = rotX - startX;
      double y = rotY - startY;
      double z = rotZ - startZ;
      double dist = (double)MathHelper.sqrt_double(x * x + z * z);
      float yaw = (float)(Math.atan2(z, x) * 180.0 / Math.PI) - 90.0F;
      float pitch = (float)(-(Math.atan2(y, dist) * 180.0 / Math.PI));
      return new float[]{yaw, pitch};
   }

   public static float[] getRotationToPosition(double posX, double posY, double posZ) {
      double x = posX - F.thePlayer.posX;
      double z = posZ - F.thePlayer.posZ;
      double y = posY - ((double)F.thePlayer.getEyeHeight() + F.thePlayer.posY);
      double d3 = (double)MathHelper.sqrt_double(x * x + z * z);
      float yaw = (float)(MathHelper.atan2(z, x) * 180.0 / Math.PI) - 90.0F;
      float pitch = (float)(-(MathHelper.atan2(y, d3) * 180.0 / Math.PI));
      return new float[]{yaw, pitch};
   }

   public static float[] getRotationToVec(Vec3 vec) {
      return getRotationToPosition(vec.xCoord, vec.yCoord, vec.zCoord);
   }

   public static float[] getRotationToEntity(Entity entity) {
      return getRotationToVec(getEntityHitVec(entity));
   }

   public static Vec3 getInterpolatedLookVec(float partialTicks) {
      if (partialTicks == 1.0F) {
         return getLookVectorFromAngles(RotationManager.getCurrentPitch(), RotationManager.getCurrentYaw());
      } else {
         float f = RotationManager.getLastPitch() + (RotationManager.getCurrentPitch() - RotationManager.getLastPitch()) * partialTicks;
         float f1 = RotationManager.getLastYaw() + (RotationManager.getCurrentYaw() - RotationManager.getLastYaw()) * partialTicks;
         return getLookVectorFromAngles(f, f1);
      }
   }

   @NotNull
   public static Vec3 getLookVectorFromAngles(float pitch, float yaw) {
      float f = MathHelper.cos(-yaw * (float) (Math.PI / 180.0) - (float) Math.PI);
      float f1 = MathHelper.sin(-yaw * (float) (Math.PI / 180.0) - (float) Math.PI);
      float f2 = -MathHelper.cos(-pitch * (float) (Math.PI / 180.0));
      float f3 = MathHelper.sin(-pitch * (float) (Math.PI / 180.0));
      return new Vec3((double)(f1 * f2), (double)f3, (double)(f * f2));
   }

   public static float interpolate(float tickDelta, float old, float newFloat) {
      return old + (newFloat - old) * tickDelta;
   }

   public static float normalizeYaw(float yaw) {
      return wrapYaw(yaw, -180.0F, 180.0F);
   }

   public static float wrapYaw(float yaw, float min, float max) {
      yaw %= 360.0F;
      if (yaw >= max) {
         yaw -= 360.0F;
      }

      if (yaw < min) {
         yaw += 360.0F;
      }

      return yaw;
   }

   public static double getAngleDifference(float[] e) {
      return (double)getAngleBetweenRotations(new float[]{RotationManager.getCurrentYaw(), RotationManager.getCurrentPitch()}, e);
   }

   public static double getAngleDifference(Vec3 e) {
      float[] entityRotation = getRotationToPosition(e.xCoord, e.yCoord, e.zCoord);
      return getAngleDifference(entityRotation);
   }

   public static float getAngleBetween(Entity entity) {
      float[] target = getRotationToPosition(entity.posX, entity.posY + (double)entity.getEyeHeight(), entity.posZ);
      return (float)Math.hypot(
         (double)Math.abs(getAngleDifference(target[0], F.thePlayer.rotationYaw)), (double)Math.abs(target[1] - F.thePlayer.rotationPitch)
      );
   }

   public static float getAngleBetweenEntities(Entity entity, Entity entity2) {
      float[] target = getRotationToPosition(entity.posX, entity.posY + (double)entity.getEyeHeight(), entity.posZ);
      float[] target2 = getRotationToPosition(entity2.posX, entity2.posY + (double)entity2.getEyeHeight(), entity2.posZ);
      return (float)Math.hypot((double)Math.abs(getAngleDifference(target[0], target2[0])), (double)Math.abs(target[1] - target2[1]));
   }

   public static float getAngleBetweenRotations(float[] a, float[] b) {
      return (float)Math.hypot((double)Math.abs(getAngleDifference(a[0], b[0])), (double)Math.abs(a[1] - b[1]));
   }

   public static Vec3 getCurrentLookVector() {
      return getLookVectorFromAngles(RotationManager.getCurrentPitch(), RotationManager.getCurrentYaw());
   }

   public static Vec3 getPlayerLookVector() {
      return getLookVectorFromAngles(F.thePlayer.rotationPitch, F.thePlayer.rotationYaw);
   }

   static {
      L(null);
   }

   public static void L(String[] arr) {
      O = arr;
   }

   public static String[] T() {
      return O;
   }

   private static RuntimeException a(RuntimeException runtimeException) {
      return runtimeException;
   }
}
