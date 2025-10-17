package Myaven.util;

import Myaven.Myaven;
import Myaven.mixins.accessor.AccessorKeybinding;
import Myaven.mixins.accessor.AccessorTimer;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.block.BlockSign;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.scoreboard.Score;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StringUtils;
import net.minecraft.util.Timer;
import net.minecraft.util.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Range;
import org.lwjgl.input.Keyboard;

public class ClientUtil {
   static Minecraft mc = Minecraft.getMinecraft();
   private static boolean[] w;

   public static void sendChatMessage(String message) {
      mc.ingameGUI.getChatGUI().printChatMessage(new ChatComponentText(message));
   }

   public static void sendPrefixedChat(String message) {
      mc.ingameGUI.getChatGUI().printChatMessage(new ChatComponentText("§l[§c§lMyaven§r§l]§r " + message));
   }

   public static void printSeparator() {
      mc.ingameGUI.getChatGUI().printChatMessage(new ChatComponentText("------------"));
   }

   public static long getDelayInMillis(double min, double max) {

      double result;
      if (!(max > min)) {
         result = max;
      } else {
         result = ThreadLocalRandom.current().nextDouble(min, max);
      }

      return (long)(1000.0 / result);
   }

   public static long getRandomDoubleInMillis(double min, double max) {

      double result;
      if (!(max > min)) {
         result = max;
      } else {
         result = ThreadLocalRandom.current().nextDouble(min, max);
      }

      return (long)result;
   }

   public static int applyAlpha(int color, int alpha) {
      return color & 16777215 | alpha << 24;
   }

   public static boolean isWithinDistance(Vec3 pos1, Vec3 pos2, double distance) {

      return pos1.distanceTo(pos2) <= distance;
   }

   public static float getCameraYaw() {
      return (float)Math.toDegrees(Math.atan2((double)ActiveRenderInfo.getRotationZ(), (double)ActiveRenderInfo.getRotationX()));
   }

   public static float getCameraPitch() {
      return (float)Math.toDegrees(Math.acos((double)ActiveRenderInfo.getRotationXZ()));
   }

   public static Vec3 getCameraPos(double renderPartialTicks) {
      if (mc.gameSettings.thirdPersonView == 0) {
         return new Vec3(mc.thePlayer.posX, mc.thePlayer.posY + (double)mc.thePlayer.getEyeHeight(), mc.thePlayer.posZ);
      } else {
         float cameraDistance = 4.0F;
         Entity renderEntity = mc.getRenderViewEntity();
         float entityEyeHeight = renderEntity.getEyeHeight();
         double interpolatedX = renderEntity.prevPosX + (renderEntity.posX - renderEntity.prevPosX) * renderPartialTicks;
         double interpolatedY = renderEntity.prevPosY + (renderEntity.posY - renderEntity.prevPosY) * renderPartialTicks + (double)entityEyeHeight;
         double interpolatedZ = renderEntity.prevPosZ + (renderEntity.posZ - renderEntity.prevPosZ) * renderPartialTicks;
         double adjustedDistance = 4.0;
         float cameraYaw = getCameraYaw();
         float cameraPitch = getCameraPitch();
         double offsetX = (double)(-MathHelper.sin(cameraYaw / 180.0F * (float) Math.PI) * MathHelper.cos(cameraPitch / 180.0F * (float) Math.PI))
            * adjustedDistance;
         double offsetZ = (double)(MathHelper.cos(cameraYaw / 180.0F * (float) Math.PI) * MathHelper.cos(cameraPitch / 180.0F * (float) Math.PI))
            * adjustedDistance;
         double offsetY = (double)(-MathHelper.sin(cameraPitch / 180.0F * (float) Math.PI)) * adjustedDistance;
         if (!Myaven.moduleManager.getModule("viewClip").isEnabled()) {
            for (int i = 0; i < 8; i++) {
               float cornerOffsetX = (float)((i & 1) * 2 - 1) * 0.1F;
               float cornerOffsetY = (float)((i >> 1 & 1) * 2 - 1) * 0.1F;
               float cornerOffsetZ = (float)((i >> 2 & 1) * 2 - 1) * 0.1F;
               MovingObjectPosition rayTraceResult = mc.theWorld
                  .rayTraceBlocks(
                     new Vec3(interpolatedX + (double)cornerOffsetX, interpolatedY + (double)cornerOffsetY, interpolatedZ + (double)cornerOffsetZ),
                     new Vec3(
                        interpolatedX - offsetX + (double)cornerOffsetX + (double)cornerOffsetZ,
                        interpolatedY - offsetY + (double)cornerOffsetY,
                        interpolatedZ - offsetZ + (double)cornerOffsetZ
                     )
                  );
               if (rayTraceResult != null) {
                  double blockHitDistance = rayTraceResult.hitVec.distanceTo(new Vec3(interpolatedX, interpolatedY, interpolatedZ));
                  if (blockHitDistance < adjustedDistance) {
                     adjustedDistance = blockHitDistance;
                  }
               }
            }
         }

         double finalCameraX = interpolatedX - offsetX * (adjustedDistance / 4.0);
         double finalCameraY = interpolatedY - offsetY * (adjustedDistance / 4.0);
         double finalCameraZ = interpolatedZ - offsetZ * (adjustedDistance / 4.0);
         return new Vec3(finalCameraX, finalCameraY, finalCameraZ);
      }
   }

   public static float getRenderPartialTicks() {
      return ((AccessorTimer)mc).getTimer().renderPartialTicks;
   }

   public static void setKeyPressed(KeyBinding key, boolean pressed) {
      ((AccessorKeybinding)key).setPressed(pressed);
   }

   public static boolean isWholeNumber(double num) {
      return num == Math.floor(num);
   }

   public static boolean isKeyPressed(int keyCode) {
      boolean isPressed = Keyboard.isKeyDown(keyCode);
      boolean justPressed = isPressed && !w[keyCode];
      w[keyCode] = isPressed;
      return justPressed;
   }

   public static Timer getTimer() {
      AccessorTimer accessor = (AccessorTimer)mc;
      return accessor.getTimer();
   }

   public static void playSound(String resourceLocation) {
      Minecraft.getMinecraft().getSoundHandler().playSound(PositionedSoundRecord.create(new ResourceLocation(resourceLocation), 1.0F));
   }

   public static boolean isWorldLoaded() {
      return mc.thePlayer != null && mc.theWorld != null;
   }

   public static Vec3 getPlayerEyesPosition() {
      return mc.thePlayer.getPositionEyes(getRenderPartialTicks());
   }

   public static BlockPos getPlayerBlockPos() {
      return BlockUtil.toBlockPos(getPlayerEyesPosition());
   }

   public static boolean isInWorld() {
      return !isWorldLoaded() ? false : mc.theWorld.isAirBlock(new BlockPos(mc.thePlayer.posX, mc.thePlayer.posY - 1.0, mc.thePlayer.posZ));
   }

   public static boolean isPlayerOnGround() {
      return mc.theWorld
         .getCollidingBoundingBoxes(mc.thePlayer, mc.thePlayer.getEntityBoundingBox().offset(mc.thePlayer.motionX / 3.0, -1.0, mc.thePlayer.motionZ / 3.0))
         .isEmpty();
   }

   public static boolean isJumping() {
      return mc.gameSettings.keyBindJump.isKeyDown();
   }

   public static double interpolate(double old, double now, float partialTicks) {
      return old + (now - old) * (double)partialTicks;
   }

   public static double interpolateDouble(double oldValue, double newValue, double interpolationValue) {
      return oldValue + (newValue - oldValue) * interpolationValue;
   }

   public static float interpolateFloat(float old, float now, float partialTicks) {
      return old + (now - old) * partialTicks;
   }

   public static Vec3 interpolateVec3(Vec3 end, Vec3 start, float multiple) {
      return new Vec3(
         (double)((float)interpolate(end.xCoord, start.xCoord, multiple)),
         (double)((float)interpolate(end.yCoord, start.yCoord, multiple)),
         (double)((float)interpolate(end.zCoord, start.zCoord, multiple))
      );
   }

   public static double interpolateNow(double old, double now) {
      return interpolate(old, now, getRenderPartialTicks());
   }

   public static double getFallDistance(@NotNull Entity entity) {
      double fallDist = -1.0;
      Vec3 pos = new Vec3(entity.posX, entity.posY, entity.posZ);

      int y = (int)Math.floor(pos.yCoord);
      if (pos.yCoord % 1.0 == 0.0) {
         y--;
      }

      for (int i = y; i > -1; i--) {
         Block block = mc.theWorld.getBlockState(new BlockPos((int)Math.floor(pos.xCoord), i, (int)Math.floor(pos.zCoord))).getBlock();
         if (!(block instanceof BlockAir) && !(block instanceof BlockSign)) {
            fallDist = (double)(y - i);
            break;
         }
      }

      return fallDist;
   }

   public static boolean isOnHypixel() {
      return !mc.isSingleplayer() && mc.getCurrentServerData() != null && mc.getCurrentServerData().serverIP.contains("hypixel.net");
   }

   public static List<String> getSidebarLines() {
      ArrayList lines = new ArrayList();
      if (mc.theWorld == null) {
         return lines;
      } else {
         Scoreboard scoreboard = mc.theWorld.getScoreboard();
         if (scoreboard == null) {
            return lines;
         } else {
            ScoreObjective objective = scoreboard.getObjectiveInDisplaySlot(1);
            if (objective == null) {
               return lines;
            } else {
               Collection<Score> scores = scoreboard.getSortedScores(objective);
               List<Score> list = new ArrayList<>();

               for (Score input : scores) {
                  if (input != null && input.getPlayerName() != null && !input.getPlayerName().startsWith("#")) {
                     list.add(input);
                  }
               }

               if (list.size() > 15) {
                  scores = new ArrayList<>(Lists.newArrayList(Iterables.skip(list, list.size() - 15)));
               } else {
                  scores = list;
               }

               int index = 0;

               for (Score score : scores) {
                  index++;
                  ScorePlayerTeam team = scoreboard.getPlayersTeam(score.getPlayerName());
                  lines.add(ScorePlayerTeam.formatPlayerName(team, score.getPlayerName()));
                  if (index == scores.size()) {
                     lines.add(objective.getDisplayName());
                  }
               }

               Collections.reverse(lines);
               return lines;
            }
         }
      }
   }

   public static String stripFormatting(String s) {
      if (s.isEmpty()) {
         return s;
      } else {
         char[] array = StringUtils.stripControlCodes(s).toCharArray();
         StringBuilder sb = new StringBuilder();

         for (char c : array) {
            if (c < 127 && c > 20) {
               sb.append(c);
            }
         }

         return sb.toString();
      }
   }

   public static boolean isLobby() {
      if (isOnHypixel()) {
         List<String> sidebarLines = getSidebarLines();
         if (!sidebarLines.isEmpty()) {
            String[] parts = stripFormatting(sidebarLines.get(1)).split("  ");
            if (parts.length > 1 && parts[1].charAt(0) == 'L') {
               return true;
            }
         }
      }

      return false;
   }

   public static double roundToTwoDecimals(double v) {
      BigDecimal bd = new BigDecimal(v);
      bd = bd.setScale(2, RoundingMode.HALF_UP);
      return bd.doubleValue();
   }

   public static boolean isBlockUnder(double posX, double posY, double posZ) {


      for (int i = (int)posY; i > -1; i--) {
         if (!(mc.theWorld.getBlockState(new BlockPos(posX, (double)i, posZ)).getBlock() instanceof BlockAir)) {
            return false;
         }
      }

      return true;
   }

   public static boolean isBlockUnder() {
      return isBlockUnder(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ);
   }

   public static boolean isInFov(float fov, @NotNull BlockPos blockPos) {
      return isInFovByYaw(fov, (double)blockPos.getX(), (double)blockPos.getZ());
   }

   public static boolean isInFov(float fov, @NotNull Entity entity) {
      return isInFovByYaw(fov, entity.posX, entity.posZ);
   }

   public static boolean isInFov(float fov, @NotNull Entity self, @NotNull Entity target) {
      return self.posX == target.posX && self.posZ == target.posZ ? true : isInYawFov(self.rotationYaw, fov, target.posX, target.posZ);
   }

   public static boolean isInFovByYaw(float fov, double n2, double n3) {
      fov *= 0.5F;
      double fovToPoint = getAngleDifference(n2, n3);
      return fovToPoint > 0.0 ? fovToPoint < (double)fov : fovToPoint > (double)(-fov);
   }

   public static boolean isInYawFov(float yaw, float fov, double n2, double n3) {
      fov *= 0.5F;
      double fovToPoint = getYawDifference(yaw, n2, n3);
      return fovToPoint > 0.0 ? fovToPoint < (double)fov : fovToPoint > (double)(-fov);
   }

   @Range(
      from = -180L,
      to = 180L
   )
   public static double getAngleDifference(double posX, double posZ) {
      return getYawDifference(mc.thePlayer.rotationYaw, posX, posZ);
   }

   @Range(
      from = -180L,
      to = 180L
   )
   public static double getYawDifference(float yaw, double posX, double posZ) {
      return MathHelper.wrapAngleTo180_double((double)((yaw - getYawTo(posX, posZ)) % 360.0F));
   }

   public static float getYawTo(double n, double n2) {
      return (float)(Math.atan2(n - mc.thePlayer.posX, n2 - mc.thePlayer.posZ) * (float) (180.0 / Math.PI) * -1.0);
   }

   public static int getBestToolForBlock(Block block) {
      float n = 1.0F;
      int n2 = -1;
      int i = 0;

      while (i < InventoryPlayer.getHotbarSize()) {
         ItemStack getStackInSlot = mc.thePlayer.inventory.getStackInSlot(i);
         if (getStackInSlot != null) {
            float ax = getToolStrength(getStackInSlot, block);

            if (ax > n) {
               n = ax;
               n2 = i;
            }
         }

         i++;
      }

      return n2;
   }

   public static float getToolStrength(ItemStack itemStack, Block block) {
      float getStrVsBlock = itemStack.getStrVsBlock(block);
      if (getStrVsBlock > 1.0F) {
         int getEnchantmentLevel = EnchantmentHelper.getEnchantmentLevel(Enchantment.efficiency.effectId, itemStack);
         if (getEnchantmentLevel > 0) {
            getStrVsBlock += (float)(getEnchantmentLevel * getEnchantmentLevel + 1);
         }
      }

      return getStrVsBlock;
   }
}
