package Myaven.util;

import Myaven.module.modules.config.Teams;
import Myaven.module.modules.misc.AntiBot;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.passive.IAnimals;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.Vec3;

public class TargetUtil {
   static Minecraft mc = Minecraft.getMinecraft();

   public static boolean isInRange(Entity entity, double distance) {
      return mc.thePlayer.getPositionEyes(ClientUtil.getRenderPartialTicks()).distanceTo(RotationUtil.getEntityHitVec(entity)) <= distance;
   }

   public static boolean isInFov(Entity entity, double fov) {
      return ClientUtil.isInFov((float)fov, mc.thePlayer, entity);
   }

   public static List<EntityLivingBase> getLivingEntitiesInRange(double range) {
      List<Entity> list2 = mc.theWorld.loadedEntityList;
      List<EntityLivingBase> list = new ArrayList<>();

      for (Entity entity : list2) {
         if (entity instanceof EntityLivingBase
            && entity != mc.thePlayer
            && !entity.isDead
            && entity.isEntityAlive()
            && ((EntityLivingBase)entity).getHealth() != 0.0F
            && isInRange((EntityLivingBase)entity, range)) {
            list.add((EntityLivingBase)entity);
         }
      }

      return list;
   }

   public static List<EntityLivingBase> filterEntities(
      List<EntityLivingBase> entities, boolean player, boolean mob, boolean animals, boolean teamCheck, boolean botCheck
   ) {
      ArrayList list = new ArrayList();

      for (EntityLivingBase entity : entities) {
         if ((teamCheck && !Teams.isTeammate(entity) || !teamCheck) && (botCheck && !AntiBot.isBot(entity) || !botCheck)) {
            if (player && entity instanceof EntityPlayer) {
               list.add(entity);
            } else if (mob && entity instanceof IMob) {
               list.add(entity);
            } else if (animals && entity instanceof IAnimals) {
               list.add(entity);
            }
         }
      }

      return list;
   }

   public static double getDistanceToEntityHitbox(Entity entity) {
      float borderSize = entity.getCollisionBorderSize();
      AxisAlignedBB boundingBox = entity.getEntityBoundingBox().expand((double)borderSize, (double)borderSize, (double)borderSize);
      return getDistanceToBoundingBox(boundingBox);
   }

   public static double getDistanceToEntityFromPoint(Entity entity, Vec3 point) {
      float borderSize = entity.getCollisionBorderSize();
      return getDistanceToBoxFromPoint(entity.getEntityBoundingBox().expand((double)borderSize, (double)borderSize, (double)borderSize), point);
   }

   public static double getDistanceToBoundingBox(AxisAlignedBB boundingBox) {
      return getDistanceToBoxFromPoint(boundingBox, mc.thePlayer.getPositionEyes(ClientUtil.getRenderPartialTicks()));
   }

   public static double getDistanceToBoxFromPoint(AxisAlignedBB boundingBox, Vec3 point) {
      if (boundingBox.isVecInside(point)) {
         return 0.0;
      } else {
         Vec3 clampedPoint = clampToBoundingBox(point, boundingBox);
         double deltaX = clampedPoint.xCoord - point.xCoord;
         double deltaY = clampedPoint.yCoord - point.yCoord;
         double deltaZ = clampedPoint.zCoord - point.zCoord;
         return Math.sqrt(deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ);
      }
   }

   public static Vec3 clampToBoundingBox(Vec3 vector, AxisAlignedBB boundingBox) {
      double[] coords = new double[]{vector.xCoord, vector.yCoord, vector.zCoord};
      double[] minCoords = new double[]{boundingBox.minX, boundingBox.minY, boundingBox.minZ};
      double[] maxCoords = new double[]{boundingBox.maxX, boundingBox.maxY, boundingBox.maxZ};

      for (int i = 0; i < 3; i++) {
         if (coords[i] > maxCoords[i]) {
            coords[i] = maxCoords[i];
         } else if (coords[i] < minCoords[i]) {
            coords[i] = minCoords[i];
         }
      }

      return new Vec3(coords[0], coords[1], coords[2]);
   }

   private static RuntimeException a(RuntimeException runtimeException) {
      return runtimeException;
   }
}
