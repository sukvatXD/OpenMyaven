package Myaven.util;

import Myaven.Myaven;
import Myaven.events.JumpEvent;
import Myaven.events.PreMotionEvent;
import Myaven.mixins.accessor.AccessorEntity;
import Myaven.module.modules.visual.FreeLook;
import java.util.Arrays;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.Potion;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class PlayerUtil {
   public static double motionX;
   public static double motionZ;
   public static double lastYaw;
   public static double lastPitch;
   public static double prevYaw;
   public static double prevPitch;
   public static double deltaYaw;
   public static double deltaPitch;
   public static double playerYaw;
   public static double playerPitch;
   public static double yawOffset;
   public static double[] directionTable;
   public static double playerMotion;
   public static double playerSpeed;
   public static double moveSpeed;
   public static int airTicks;
   private static long[] b;
   private static Integer[] d;

   public static float getFixedYaw(float rotationYaw) {
      int moveForward = 0;
      if (GameSettings.isKeyDown(Myaven.mc.gameSettings.keyBindForward)) {
         moveForward++;
      }

      if (GameSettings.isKeyDown(Myaven.mc.gameSettings.keyBindBack)) {
         moveForward--;
      }

      int moveStrafing = 0;
      if (GameSettings.isKeyDown(Myaven.mc.gameSettings.keyBindRight)) {
         moveStrafing++;
      }

      if (GameSettings.isKeyDown(Myaven.mc.gameSettings.keyBindLeft)) {
         moveStrafing--;
      }

      boolean reversed = moveForward < 0;
      double strafingYaw = 90.0 * (moveForward > 0 ? 0.5 : -0.5);
      if (reversed) {
         rotationYaw += 180.0F;
      }

      if (moveStrafing > 0) {
         rotationYaw = (float)((double)rotationYaw + strafingYaw);
      } else if (moveStrafing < 0) {
         rotationYaw = (float)((double)rotationYaw - strafingYaw);
      }

      return rotationYaw;
   }

   public static void handleJump(float yaw, boolean sprinting) {
      JumpEvent event = new JumpEvent((float)getJumpHeight(), yaw);
      MinecraftForge.EVENT_BUS.post(event);
      if (!event.isCanceled()) {
         Myaven.mc.thePlayer.motionY = (double)event.getMotionY();
         if (Myaven.mc.thePlayer.isPotionActive(Potion.jump)) {
            Myaven.mc.thePlayer.motionY = Myaven.mc.thePlayer.motionY
               + (double)((float)(Myaven.mc.thePlayer.getActivePotionEffect(Potion.jump).getAmplifier() + 1) * 0.1F);
         }

         if (sprinting) {
            float f = event.getYaw() * (float) (Math.PI / 180.0);
            Myaven.mc.thePlayer.motionX = Myaven.mc.thePlayer.motionX - (double)(MathHelper.sin(f) * 0.2F);
            Myaven.mc.thePlayer.motionZ = Myaven.mc.thePlayer.motionZ + (double)(MathHelper.cos(f) * 0.2F);
         }

         Myaven.mc.thePlayer.isAirBorne = true;
         ForgeHooks.onLivingJump(Myaven.mc.thePlayer);
      }
   }

   public static double getSpeed() {
      return getSpeed2(Myaven.mc.thePlayer);
   }

   public static double getSpeed2(Entity entity) {
      return Math.sqrt(entity.motionX * entity.motionX + entity.motionZ * entity.motionZ);
   }

   public static double predictMotion(double motion, int ticks) {
      if (ticks == 0) {
         return motion;
      } else {
         double predicted = motion;
         double sum = 0.0 + motion;

         for (int i = 0; i < ticks; i++) {
            predicted = (predicted - 0.08) * 0.98F;
            sum += predicted;
         }

         return sum;
      }
   }

   public static boolean isMoving() {
      return isMoving(Myaven.mc.thePlayer);
   }

   public static boolean isMoving(EntityLivingBase player) {
      
      return player != null && (player.moveForward != 0.0F || player.moveStrafing != 0.0F);
   }

   public static double getSpeed3(EntityPlayer player) {
      return Math.sqrt(player.motionX * player.motionX + player.motionZ * player.motionZ);
   }

   public static double getSpeed4() {
      return getSpeed3(Myaven.mc.thePlayer);
   }

   public static double getSpeed5(double motionX, double motionZ) {
      return Math.sqrt(motionX * motionX + motionZ * motionZ);
   }

   public static double getSpeed6(int motionX, int motionZ) {
      return Math.sqrt((double)(motionX * motionX + motionZ * motionZ));
   }

   public static void getSpeed7() {
      setSpeed(getSpeed4());
   }

   public static void setSpeed(double speed) {
      if (isMoving()) {
         double yaw = getDirection();
         Myaven.mc.thePlayer.motionX = -Math.sin(yaw) * speed / 4.0;
         Myaven.mc.thePlayer.motionZ = Math.cos(yaw) * speed / 4.0;
      }
   }

   public static void setSpeed(double speed, double yaw) {

      if (!isMoving()) {
         return;
      }

      Myaven.mc.thePlayer.motionX = -Math.sin(yaw) * speed / 4.0;
      Myaven.mc.thePlayer.motionZ = Math.cos(yaw) * speed / 4.0;
   }

   public static void resetMotion(double speed) {
      double forward = Minecraft.getMinecraft().gameSettings.keyBindForward.isKeyDown()
         ? 1.0
         : (Minecraft.getMinecraft().gameSettings.keyBindBack.isKeyDown() ? -1.0 : 0.0);
      double strafe = Minecraft.getMinecraft().gameSettings.keyBindLeft.isKeyDown()
         ? 1.0
         : (Minecraft.getMinecraft().gameSettings.keyBindRight.isKeyDown() ? -1.0 : 0.0);
      float yaw = Myaven.mc.thePlayer.rotationYaw;
      if (isMoving()) {
         if (forward != 0.0) {
            if (strafe > 0.0) {
               yaw += (float)(forward > 0.0 ? -45 : 45);
            } else if (strafe < 0.0) {
               yaw += (float)(forward > 0.0 ? 45 : -45);
            }

            strafe = 0.0;
            if (forward > 0.0) {
               forward = 1.0;
            } else if (forward < 0.0) {
               forward = -1.0;
            }
         }

         double cos = Math.cos(Math.toRadians((double)(yaw + 89.5F)));
         double sin = Math.sin(Math.toRadians((double)(yaw + 89.5F)));
         Myaven.mc.thePlayer.motionX = forward * speed * cos + strafe * speed * sin;
         Myaven.mc.thePlayer.motionZ = forward * speed * sin - strafe * speed * cos;
      } else {
         Myaven.mc.thePlayer.motionX = 0.0;
         Myaven.mc.thePlayer.motionZ = 0.0;
      }
   }

   public static double getDirection(float moveForward, float moveStrafing, float rotationYaw) {
      if (moveForward < 0.0F) {
         rotationYaw += 180.0F;
      }

      float forward = 1.0F;
      if (moveForward < 0.0F) {
         forward = -0.5F;
      } else if (moveForward > 0.0F) {
         forward = 0.5F;
      }

      if (moveStrafing > 0.0F) {
         rotationYaw -= 70.0F * forward;
      }

      if (moveStrafing < 0.0F) {
         rotationYaw += 70.0F * forward;
      }

      return Math.toRadians((double)rotationYaw);
   }

   public static double getDirection() {
      float rotationYaw = Myaven.mc.thePlayer.rotationYaw;
      if (Myaven.mc.thePlayer.movementInput.moveForward < 0.0F) {
         rotationYaw += 180.0F;
      }

      float forward = 1.0F;
      if (Myaven.mc.thePlayer.movementInput.moveForward < 0.0F) {
         forward = -0.5F;
      } else if (Myaven.mc.thePlayer.movementInput.moveForward > 0.0F) {
         forward = 0.5F;
      }

      if (Myaven.mc.thePlayer.movementInput.moveStrafe > 0.0F) {
         rotationYaw -= 90.0F * forward;
      }

      if (Myaven.mc.thePlayer.movementInput.moveStrafe < 0.0F) {
         rotationYaw += 90.0F * forward;
      }

      return Math.toRadians((double)rotationYaw);
   }

   public static float getMovingDirection() {
      return calculateDirection(
         FreeLook.isActive ? FreeLook.freeLookYaw : Myaven.mc.thePlayer.rotationYaw, Myaven.movementHook.moveStrafe, Myaven.movementHook.moveForward
      );
   }

   public static boolean isAligned() {
      float direction = getMovingDirection() + 180.0F;
      float movingYaw = (float)(Math.round(direction / 45.0F) * 45);
      return movingYaw % 90.0F == 0.0F;
   }

   public static float calculateDirection(float yaw, float pStrafe, float pForward) {
      float rotationYaw = yaw;
      if (pForward < 0.0F) {
         rotationYaw = yaw + 180.0F;
      }

      float forward = 1.0F;
      if (pForward < 0.0F) {
         forward = -0.5F;
      } else if (pForward > 0.0F) {
         forward = 0.5F;
      }

      if (pStrafe > 0.0F) {
         rotationYaw -= 90.0F * forward;
      }

      if (pStrafe < 0.0F) {
         rotationYaw += 90.0F * forward;
      }

      return rotationYaw;
   }

   public static int getSpeedAmplifier(EntityPlayer player) {
      return player.isPotionActive(Potion.moveSpeed) ? player.getActivePotionEffect(Potion.moveSpeed).getAmplifier() + 1 : 0;
   }

   public static int getSpeedAmplifier2() {
      return getSpeedAmplifier(Myaven.mc.thePlayer);
   }

   public static void stopMotionXZ() {
      Myaven.mc.thePlayer.motionX = Myaven.mc.thePlayer.motionZ = 0.0;
   }

   public static void stopAllMotion() {
      Myaven.mc.thePlayer.motionX = Myaven.mc.thePlayer.motionY = Myaven.mc.thePlayer.motionZ = 0.0;
   }

   public static double getBPS() {
      return getBPS(Myaven.mc.thePlayer);
   }

   public static double getBPS(EntityPlayer player) {
      return player != null && player.ticksExisted >= 1
         ? getSpeed8(player.lastTickPosX, player.lastTickPosZ) * (double)(20.0F * ClientUtil.getTimer().timerSpeed)
         : 0.0;
   }

   public static double getSpeed8(double x, double z) {
      double xSpeed = Myaven.mc.thePlayer.posX - x;
      double zSpeed = Myaven.mc.thePlayer.posZ - z;
      return (double)MathHelper.sqrt_double(xSpeed * xSpeed + zSpeed * zSpeed);
   }

   public static boolean Q(boolean legit) {
      
      return legit
         ? Myaven.mc.thePlayer.moveForward >= 0.8F
            && !Myaven.mc.thePlayer.isCollidedHorizontally
            && (Myaven.mc.thePlayer.getFoodStats().getFoodLevel() > 6 || Myaven.mc.thePlayer.capabilities.allowFlying)
            && !Myaven.mc.thePlayer.isPotionActive(Potion.blindness)
            && !Myaven.mc.thePlayer.isSneaking()
         : isMovingForward();
   }

   public static boolean isMovingForward() {
      return Math.abs(Myaven.mc.thePlayer.moveForward) >= 0.8F || Math.abs(Myaven.mc.thePlayer.moveStrafing) >= 0.8F;
   }

   public static boolean hasMotion(double amount) {
      return Math.abs(Myaven.mc.thePlayer.motionX) > amount && Math.abs(Myaven.mc.thePlayer.motionZ) > amount;
   }

   public static double getBaseMoveSpeed(EntityPlayer player) {
      double baseSpeed = 0.2873;
      if (player.isPotionActive(Potion.moveSpeed)) {
         int amplifier = player.getActivePotionEffect(Potion.moveSpeed).getAmplifier();
         baseSpeed = 0.2873 * (1.0 + 0.2 * (double)(amplifier + 1));
      }

      return baseSpeed;
   }

   public static double getBaseMoveSpeed() {
      return getBaseMoveSpeed(Myaven.mc.thePlayer);
   }

   public static double getBaseJumpMotion() {
      double jumpY = 0.41999998688698;
      if (Myaven.mc.thePlayer.isPotionActive(Potion.jump)) {
         jumpY = 0.41999998688698 + (double)((float)(Myaven.mc.thePlayer.getActivePotionEffect(Potion.jump).getAmplifier() + 1) * 0.1F);
      }

      return jumpY;
   }

   public static int getDepthStriderLevel() {
      return Myaven.mc.thePlayer == null ? 0 : EnchantmentHelper.getDepthStriderModifier(Myaven.mc.thePlayer);
   }

   public static double predictMotion2(double motion, int ticks) {
      if (ticks == 0) {
         return motion;
      } else {
         double predicted = motion;

         for (int i = 0; i < ticks; i++) {
            predicted = (predicted - 0.08) * 0.98F;
         }

         return predicted;
      }
   }

   public static double getDirection2(float rotationYaw, double moveForward, double moveStrafing) {
      if (moveForward < 0.0) {
         rotationYaw += 180.0F;
      }

      float forward = 1.0F;
      if (moveForward < 0.0) {
         forward = -0.5F;
      } else if (moveForward > 0.0) {
         forward = 0.5F;
      }

      if (moveStrafing > 0.0) {
         rotationYaw -= 90.0F * forward;
      }

      if (moveStrafing < 0.0) {
         rotationYaw += 90.0F * forward;
      }

      return Math.toRadians((double)rotationYaw);
   }

   public static void setSpeed3(double increase) {
      if (isMoving()) {
         double yaw = getDirection();
         Myaven.mc.thePlayer.motionX = Myaven.mc.thePlayer.motionX + (double)(-MathHelper.sin((float)yaw)) * increase;
         Myaven.mc.thePlayer.motionZ = Myaven.mc.thePlayer.motionZ + (double)MathHelper.cos((float)yaw) * increase;
      }
   }

   public static void setSpeed4(double increase) {
      if (isMoving()) {
         double yaw = getDirection();
         Myaven.mc.thePlayer.motionX = Myaven.mc.thePlayer.motionX + (double)(-MathHelper.sin((float)yaw)) * increase;
         Myaven.mc.thePlayer.motionZ = Myaven.mc.thePlayer.motionZ + (double)MathHelper.cos((float)yaw) * increase;
      }
   }

   public static void applyMovementIncrement() {
      KeyBinding[] gameSettings = new KeyBinding[]{
         Myaven.mc.gameSettings.keyBindForward, Myaven.mc.gameSettings.keyBindRight, Myaven.mc.gameSettings.keyBindBack, Myaven.mc.gameSettings.keyBindLeft
      };
      int[] down = new int[]{0};
      Arrays.stream(gameSettings).forEach(keyBinding -> {
         down[0] += keyBinding.isKeyDown() ? 1 : 0;
      });
      boolean active = down[0] == 1;
      if (!active) {
         double groundIncrease = 0.0026000750109401644;
         double airIncrease = 5.199896488849598E-4;
         double increase = Myaven.mc.thePlayer.onGround ? 0.0026000750109401644 : 5.199896488849598E-4;
         setSpeed4(-increase);
      }
   }

   public static void checkActiveKeys() {
      KeyBinding[] gameSettings = new KeyBinding[]{
         Myaven.mc.gameSettings.keyBindForward, Myaven.mc.gameSettings.keyBindRight, Myaven.mc.gameSettings.keyBindBack, Myaven.mc.gameSettings.keyBindLeft
      };
      int[] down = new int[]{0};
      Arrays.stream(gameSettings).forEach(keyBinding -> {
         down[0] += keyBinding.isKeyDown() ? 1 : 0;
      });
      boolean active = down[0] == 1;
      if (active) {
         double groundIncrease = 0.0026000750109401644;
         double airIncrease = 5.199896488849598E-4;
         double increase = Myaven.mc.thePlayer.onGround ? 0.0026000750109401644 : 5.199896488849598E-4;
         setSpeed4(increase);
      }
   }

   public static double getBaseHorizontalSpeed(boolean allowSprint) {
      boolean useBaseModifiers = false;
      double horizontalDistance;
      if (((AccessorEntity)Myaven.mc.thePlayer).isInWeb()) {
         horizontalDistance = 0.105;
      } else if (Myaven.mc.thePlayer.isInWater() || Myaven.mc.thePlayer.isInLava()) {
         horizontalDistance = 0.11500000208616258;
         int depthStriderLevel = getDepthStriderLevel();
         if (depthStriderLevel > 0) {
            horizontalDistance = 0.11500000208616258 * directionTable[depthStriderLevel];
            useBaseModifiers = true;
         }
      } else if (Myaven.mc.thePlayer.isSneaking()) {
         horizontalDistance = 0.0663000026345253;
      } else {
         horizontalDistance = 0.221;
         useBaseModifiers = true;
      }

      if (useBaseModifiers) {
         if (Q(false) && allowSprint) {
            horizontalDistance *= 1.3F;
         }

         if (Myaven.mc.thePlayer.isPotionActive(Potion.moveSpeed) && Myaven.mc.thePlayer.getActivePotionEffect(Potion.moveSpeed).getDuration() > 0) {
            horizontalDistance *= 1.0 + 0.2 * (double)(Myaven.mc.thePlayer.getActivePotionEffect(Potion.moveSpeed).getAmplifier() + 1);
         }

         if (Myaven.mc.thePlayer.isPotionActive(Potion.moveSlowdown)) {
            horizontalDistance = 0.29;
         }
      }

      return horizontalDistance;
   }

   public static boolean isOnGround() {
      AxisAlignedBB bb = Myaven.mc.thePlayer.getEntityBoundingBox();
      AxisAlignedBB bbBelow = bb.offset(0.0, -0.01, 0.0);
      return !Myaven.mc.thePlayer.worldObj.getCollidingBoundingBoxes(Myaven.mc.thePlayer, bbBelow).isEmpty();
   }

   public static double[] calculateMotionComponents(float strafe, float forward, boolean onGround, float yaw, boolean sprinting) {
      float friction = 0.02F;
      float playerWalkSpeed = Myaven.mc.thePlayer.getAIMoveSpeed();
      if (onGround) {
         float f4 = 0.54600006F;
         float f = 0.9999998F;
         friction = playerWalkSpeed / 2.0F * 0.9999998F;
      }

      if (sprinting) {
         friction = (float)((double)friction + (double)(playerWalkSpeed / 2.0F) * 0.3);
      }

      float f = strafe * strafe + forward * forward;
      if (f >= 1.0E-4F) {
         f = MathHelper.sqrt_float(f);
         if (f < 1.0F) {
            f = 1.0F;
         }

         f = friction / f;
         strafe *= f;
         forward *= f;
         float f1 = MathHelper.sin(yaw * (float) (Math.PI * 2) / 180.0F);
         float f2 = MathHelper.cos(yaw * (float) (Math.PI * 2) / 180.0F);
         double motionX = (double)(strafe * f2 - forward * f1);
         double motionZ = (double)(forward * f2 + strafe * f1);
         return new double[]{motionX, motionZ};
      } else {
         return null;
      }
   }

   public static double clamp(double value, double minValue, double maxValue) {
      return Math.max(value, Math.min(minValue, maxValue));
   }

   public static float getSin(float var0) {
      return (float)(-Math.sin(Math.toRadians((double)var0)));
   }

   public static float getCos(float var0) {
      return (float)Math.cos(Math.toRadians((double)var0));
   }

   public static double getJumpHeight() {
      return getJumpBoosted(0.42F);
   }

   public static double getJumpBoosted(double motionY) {
      return Myaven.mc.thePlayer.isPotionActive(Potion.jump)
         ? motionY + (double)((float)(Myaven.mc.thePlayer.getActivePotionEffect(Potion.jump).getAmplifier() + 1) * 0.1F)
         : motionY;
   }

   @SubscribeEvent
   public void onPreMotion(PreMotionEvent event) {
      if (Myaven.mc.thePlayer.onGround) {
         airTicks = 0;
      } else {
         airTicks++;
      }
   }

   public static void setSpeed5(double speed) {
      setSpeed6(speed, Myaven.mc.thePlayer.rotationYaw);
   }

   public static void setSpeed6(double speed, float yaw) {
      double forward = (double)Myaven.mc.thePlayer.movementInput.moveForward;
      double strafe = (double)Myaven.mc.thePlayer.movementInput.moveStrafe;
      if (forward == 0.0 && strafe == 0.0) {
         Myaven.mc.thePlayer.motionX = 0.0;
         Myaven.mc.thePlayer.motionZ = 0.0;
      } else {
         if (forward != 0.0) {
            if (strafe > 0.0) {
               yaw += (float)(forward > 0.0 ? -45 : 45);
            } else if (strafe < 0.0) {
               yaw += (float)(forward > 0.0 ? 45 : -45);
            }

            strafe = 0.0;
            if (forward > 0.0) {
               forward = 1.0;
            } else if (forward < 0.0) {
               forward = -1.0;
            }
         }

         Myaven.mc.thePlayer.motionX = forward * speed * Math.cos(Math.toRadians((double)(yaw + 90.0F)))
            + strafe * speed * Math.sin(Math.toRadians((double)(yaw + 90.0F)));
         Myaven.mc.thePlayer.motionZ = forward * speed * Math.sin(Math.toRadians((double)(yaw + 90.0F)))
            - strafe * speed * Math.cos(Math.toRadians((double)(yaw + 90.0F)));
      }
   }
}
