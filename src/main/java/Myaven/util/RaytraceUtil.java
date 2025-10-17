package Myaven.util;

import Myaven.Myaven;
import Myaven.management.RotationManager;
import Myaven.mixins.accessor.AccessorEntityPlayer;
import Myaven.module.modules.config.Teams;
import Myaven.setting.Setting;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.potion.Potion;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.util.MovingObjectPosition.MovingObjectType;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.GLU;

public class RaytraceUtil {
   private static FloatBuffer modelViewMatrixBuffer;
   private static IntBuffer viewportBuffer;
   private static FloatBuffer projectionMatrixBuffer;
   private static FloatBuffer depthBuffer;
   private static float[] screenCoords;
   private static String[] b;
   private static String[] c;
   private static long[] d;
   private static Integer[] e;

   public static EntityLivingBase raycastEntity(double reach) {
      Entity entity = null;
      MovingObjectPosition rayTrace = Myaven.mc.thePlayer.rayTrace(reach, 1.0F);
      Vec3 getPositionEyes = Myaven.mc.thePlayer.getPositionEyes(1.0F);
      float rotationYaw = RotationManager.getCurrentYaw();
      float rotationPitch = RotationManager.getCurrentPitch();
      float cos = MathHelper.cos(-rotationYaw * (float) (Math.PI / 180.0) - (float) Math.PI);
      float sin = MathHelper.sin(-rotationYaw * (float) (Math.PI / 180.0) - (float) Math.PI);
      float n2 = -MathHelper.cos(-rotationPitch * (float) (Math.PI / 180.0));
      Vec3 vec3 = new Vec3((double)(sin * n2), (double)MathHelper.sin(-rotationPitch * (float) (Math.PI / 180.0)), (double)(cos * n2));
      Vec3 addVector = getPositionEyes.addVector(vec3.xCoord * reach, vec3.yCoord * reach, vec3.zCoord * reach);
      Vec3 vec4 = null;
      List<Entity> getEntitiesWithinAABBExcludingEntity = Myaven.mc
         .theWorld
         .getEntitiesWithinAABBExcludingEntity(
            Myaven.mc.getRenderViewEntity(),
            Myaven.mc
               .getRenderViewEntity()
               .getEntityBoundingBox()
               .addCoord(vec3.xCoord * reach, vec3.yCoord * reach, vec3.zCoord * reach)
               .expand(1.0, 1.0, 1.0)
         );
      double n3 = reach;

      for (Entity entity1 : getEntitiesWithinAABBExcludingEntity) {
         if (entity1.canBeCollidedWith()) {
            float getCollisionBorderSize = entity1.getCollisionBorderSize();
            AxisAlignedBB expand = entity1.getEntityBoundingBox()
               .expand((double)getCollisionBorderSize, (double)getCollisionBorderSize, (double)getCollisionBorderSize);
            MovingObjectPosition calculateIntercept = expand.calculateIntercept(getPositionEyes, addVector);
            if (expand.isVecInside(getPositionEyes)) {
               if (0.0 < n3 || n3 == 0.0) {
                  entity = entity1;
                  vec4 = calculateIntercept == null ? getPositionEyes : calculateIntercept.hitVec;
                  n3 = 0.0;
               }
            } else if (calculateIntercept != null) {
               double distanceTo = getPositionEyes.distanceTo(calculateIntercept.hitVec);
               if (distanceTo < n3 || n3 == 0.0) {
                  if (entity1 != Myaven.mc.getRenderViewEntity().ridingEntity || ReflectUtil.canRiderInteract(entity1)) {
                     entity = entity1;
                     vec4 = calculateIntercept.hitVec;
                     n3 = distanceTo;
                  } else if (n3 == 0.0) {
                     entity = entity1;
                     vec4 = calculateIntercept.hitVec;
                  }

                  if (entity1 == Myaven.mc.getRenderViewEntity().ridingEntity) {
                     if (n3 == 0.0) {
                        entity = entity1;
                        vec4 = calculateIntercept.hitVec;
                     }
                  } else {
                     entity = entity1;
                     vec4 = calculateIntercept.hitVec;
                     n3 = distanceTo;
                  }
               }
            }
         }
      }

      if (entity != null && (n3 < reach || rayTrace == null)) {
         rayTrace = new MovingObjectPosition(entity, vec4);
      }

      return rayTrace != null && rayTrace.typeOfHit == MovingObjectType.ENTITY && rayTrace.entityHit instanceof EntityLivingBase
         ? (EntityLivingBase)rayTrace.entityHit
         : null;
   }

   public static List<EntityLivingBase> getEntitiesInRay(double reach) {
      List<EntityLivingBase> hitEntities = new ArrayList<>();
      Vec3 eyesPos = Myaven.mc.thePlayer.getPositionEyes(1.0F);
      float yaw = RotationManager.getCurrentYaw();
      float pitch = RotationManager.getCurrentPitch();
      float cosYaw = MathHelper.cos(-yaw * (float) (Math.PI / 180.0) - (float) Math.PI);
      float sinYaw = MathHelper.sin(-yaw * (float) (Math.PI / 180.0) - (float) Math.PI);
      float cosPitch = -MathHelper.cos(-pitch * (float) (Math.PI / 180.0));
      float sinPitch = MathHelper.sin(-pitch * (float) (Math.PI / 180.0));
      Vec3 lookVec = new Vec3((double)(sinYaw * cosPitch), (double)sinPitch, (double)(cosYaw * cosPitch));
      Vec3 reachVec = eyesPos.addVector(lookVec.xCoord * reach, lookVec.yCoord * reach, lookVec.zCoord * reach);
      List<Entity> entities = Myaven.mc
         .theWorld
         .getEntitiesWithinAABBExcludingEntity(
            Myaven.mc.getRenderViewEntity(),
            Myaven.mc
               .getRenderViewEntity()
               .getEntityBoundingBox()
               .addCoord(lookVec.xCoord * reach, lookVec.yCoord * reach, lookVec.zCoord * reach)
               .expand(1.0, 1.0, 1.0)
         );

      for (Entity entity : entities) {
         if (entity instanceof EntityLivingBase && entity.canBeCollidedWith()) {
            float border = entity.getCollisionBorderSize();
            AxisAlignedBB aabb = entity.getEntityBoundingBox().expand((double)border, (double)border, (double)border);
            MovingObjectPosition intercept = aabb.calculateIntercept(eyesPos, reachVec);
            if (aabb.isVecInside(eyesPos) || intercept != null) {
               hitEntities.add((EntityLivingBase)entity);
            }
         }
      }

      return hitEntities;
   }

   private static int getSwingCooldownTicks() {
      return Myaven.mc.thePlayer.isPotionActive(Potion.digSpeed)
         ? 6 - (1 + Myaven.mc.thePlayer.getActivePotionEffect(Potion.digSpeed).getAmplifier()) * 1
         : (
            Myaven.mc.thePlayer.isPotionActive(Potion.digSlowdown)
               ? 6 + (1 + Myaven.mc.thePlayer.getActivePotionEffect(Potion.digSlowdown).getAmplifier()) * 2
               : 6
         );
   }

   public static void resetSwingProgress() {
      ItemStack stack = Myaven.mc.thePlayer.getHeldItem();
      if (stack == null || stack.getItem() == null || !ReflectUtil.invokeItemSwing(stack.getItem(), Myaven.mc.thePlayer, stack)) {
         if (!Myaven.mc.thePlayer.isSwingInProgress
            || Myaven.mc.thePlayer.swingProgressInt >= getSwingCooldownTicks() / 2
            || Myaven.mc.thePlayer.swingProgressInt < 0) {
            Myaven.mc.thePlayer.swingProgressInt = -1;
            Myaven.mc.thePlayer.isSwingInProgress = true;
         }
      }
   }

   public static float getTotalHealth(EntityLivingBase entity) {
      return entity.getHealth() + entity.getAbsorptionAmount();
   }

   public static float getHealth(EntityLivingBase entity) {
      return entity.getHealth();
   }

   public static float getAbsorption(EntityLivingBase entity) {
      return entity.getAbsorptionAmount();
   }

   public static String getHealthColor(float health, float max) {
      if (health <= max / 8.0F) {
         return "§4";
      } else if (health > max / 8.0F && health <= max / 3.0F) {
         return "§c";
      } else if (health > max / 3.0F && (double)health <= (double)max / 1.5) {
         return "§e";
      } else {
         return (double)health > (double)max / 1.5 && health <= max ? "§a" : "§a";
      }
   }

   @NotNull
   public static String getFormattedDamage(EntityPlayer entityPlayer, ItemStack itemStack) {
      int n = (int)Math.ceil(getDamageValue(entityPlayer, itemStack));
      return "§b" + n;
   }

   public static double getDamageValue(EntityPlayer entityPlayer, ItemStack itemStack) {
      double n = 1.0;
      if (itemStack != null && (itemStack.getItem() instanceof ItemSword || itemStack.getItem() instanceof ItemAxe)) {
         n = 1.0 + ItemUtil.getWeaponDamage(itemStack);
      }

      double n2 = 0.0;
      double n3 = 0.0;

      for (int i = 0; i < 4; i++) {
         ItemStack armorItemInSlot = entityPlayer.inventory.armorItemInSlot(i);
         if (armorItemInSlot != null && armorItemInSlot.getItem() instanceof ItemArmor) {
            n2 += (double)((ItemArmor)armorItemInSlot.getItem()).damageReduceAmount * 0.04;
            int getEnchantmentLevel = EnchantmentHelper.getEnchantmentLevel(Enchantment.protection.effectId, armorItemInSlot);
            if (getEnchantmentLevel != 0) {
               n3 += Math.floor(0.75 * (double)(6 + getEnchantmentLevel * getEnchantmentLevel) / 3.0);
            }
         }
      }

      return roundDouble(
         (double)(getHealth(entityPlayer) + getAbsorption(entityPlayer))
            / (n * (1.0 - (n2 + 0.04 * Math.min(Math.ceil(Math.min(n3, 25.0) * 0.75), 20.0) * (1.0 - n2)))),
         1
      );
   }

   public static double roundDouble(double n, int d) {
      if (d == 0) {
         return (double)Math.round(n);
      } else {
         double p = Math.pow(10.0, (double)d);
         return (double)Math.round(n * p) / p;
      }
   }

   public static boolean isSameTeam(EntityLivingBase entity) {
      return Teams.isManualAlly(entity);
   }

   public static boolean isSameTeam2(EntityLivingBase entity) {
      return Teams.isManualEnemy(entity);
   }

   public static float[] worldToScreen(float x, float y, float z, int scaleFactor) {
      GL11.glGetFloat(2982, projectionMatrixBuffer);
      GL11.glGetFloat(2983, depthBuffer);
      GL11.glGetInteger(2978, viewportBuffer);
      if (!GLU.gluProject(x, y, z, projectionMatrixBuffer, depthBuffer, viewportBuffer, modelViewMatrixBuffer)) {
         return null;
      }

      screenCoords[0] = modelViewMatrixBuffer.get(0) / (float)scaleFactor;
      screenCoords[1] = ((float)Display.getHeight() - modelViewMatrixBuffer.get(1)) / (float)scaleFactor;
      screenCoords[2] = modelViewMatrixBuffer.get(2);
      return screenCoords;
   }

   public static void setBlockingState(boolean blocking) {
      ((AccessorEntityPlayer)Myaven.mc.thePlayer).setItemInUseCount(1);
   }

   // $VF: Irreducible bytecode was duplicated to produce valid code
   static {
      modelViewMatrixBuffer = GLAllocation.createDirectFloatBuffer(4);
      viewportBuffer = GLAllocation.createDirectIntBuffer(16);
      projectionMatrixBuffer = GLAllocation.createDirectFloatBuffer(16);
      depthBuffer = GLAllocation.createDirectFloatBuffer(16);
      screenCoords = new float[3];
   }
}
