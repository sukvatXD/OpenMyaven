package Myaven.module.modules.world;

import Myaven.Myaven;
import Myaven.events.PacketReceiveEvent;
import Myaven.events.PacketSendEvent;
import Myaven.events.PlayerUpdateEvent;
import Myaven.events.Render2DEvent;
import Myaven.events.Render3DEvent;
import Myaven.management.RotationManager;
import Myaven.mixins.accessor.AccessorKeybinding;
import Myaven.mixins.accessor.AccessorPlayerController;
import Myaven.module.Category;
import Myaven.module.Module;
import Myaven.module.modules.config.Theme;
import Myaven.setting.Setting;
import Myaven.setting.settings.BooleanSetting;
import Myaven.setting.settings.ColorSetting;
import Myaven.setting.settings.DescriptionSetting;
import Myaven.setting.settings.ModeSetting;
import Myaven.setting.settings.SliderSetting;
import Myaven.util.BlockUtil;
import Myaven.util.ClientUtil;
import Myaven.util.PacketUtil;
import Myaven.util.RenderUtil;
import Myaven.util.RotationUtil;
import Myaven.util.TimerUtil;
import akka.japi.Pair;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.BlockBed;
import net.minecraft.block.BlockBed.EnumPartType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.client.C0APacketAnimation;
import net.minecraft.network.play.client.C07PacketPlayerDigging.Action;
import net.minecraft.network.play.server.S02PacketChat;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.input.Mouse;

public class BedNuker extends Module {
   public static SliderSetting range;
   public static SliderSetting fov;
   public static DescriptionSetting movementModeDescription;
   public static ModeSetting movementMode;
   public static BooleanSetting swingClient;
   public static BooleanSetting requireClick;
   public static BooleanSetting whitelistOwnBed;
   public static BooleanSetting ignoreOutsideLayer;
   public static BooleanSetting autoSelectTool;
   public static BooleanSetting keepRotation;
   public static BooleanSetting allowKillAura;
   public static DescriptionSetting colorModeDescription;
   public static ModeSetting targetColorMode;
   public static ColorSetting customColor;
   public static BooleanSetting showTargetShade;
   public static BooleanSetting showTargetOutline;
   public static BooleanSetting showTargetPercentage;
   public static BooleanSetting showTargetBar;
   private BlockPos targetBedPos = null;
   private List<Pair<BlockPos, EnumFacing>> pathToTarget = new ArrayList<>();
   private boolean hasTarget = false;
   private boolean readyToBreak;
   private boolean switchedHotbar;
   private int previousHotbarSlot;
   private boolean rotationApplied;
   private TimerUtil rotationTimer;
   private boolean shouldResetRotation;
   public static boolean isBreakingBlock;
   private boolean attackKeyOverride;
   public static boolean isRotating;
   private BlockPos lastDigPos;
   private BlockPos renderTargetPos;
   private int totalSteps;
   private boolean isBedwarsGame;
   private TimerUtil rotationThrottle;
   private ArrayList<BlockPos> knownBedPositions;

   public BedNuker() {
      super("BedNuker", false, Category.World, true, "Break the bed near around you");
      this.readyToBreak = false;
      this.switchedHotbar = false;
      this.previousHotbarSlot = -1;
      this.rotationApplied = false;
      this.rotationTimer = new TimerUtil();
      this.shouldResetRotation = false;
      this.attackKeyOverride = false;
      this.lastDigPos = null;
      this.renderTargetPos = null;
      this.totalSteps = 0;
      this.isBedwarsGame = false;
      this.rotationThrottle = new TimerUtil();
      this.knownBedPositions = new ArrayList<>();
      this.addSettings(
         new Setting[]{
            range,
            fov,
            movementModeDescription,
            movementMode,
            swingClient,
            requireClick,
            whitelistOwnBed,
            ignoreOutsideLayer,
            autoSelectTool,
            keepRotation,
            allowKillAura,
            colorModeDescription,
            targetColorMode,
            customColor,
            showTargetShade,
            showTargetOutline,
            showTargetPercentage,
            showTargetBar
         }
      );
   }

   @SubscribeEvent
   public void onUpdate(PlayerUpdateEvent event) {
      String find = movementMode.getCurrent();
      switch (find) {
         case "SILENT":
            RotationManager.setMovementMode(RotationManager.MovementMode.SILENT);
            break;
         case "STRICT":
            RotationManager.setMovementMode(RotationManager.MovementMode.STRICT);
            break;
         case "NONE":
            RotationManager.setMovementMode(RotationManager.MovementMode.NONE);
      }

      if (Myaven.moduleManager.getModule("scaffold").isEnabled()) {
         this.resetState();
      } else if (requireClick.getState() && !Mouse.isButtonDown(0)) {
         this.resetState();
      } else if (this.targetBedPos != null && !ClientUtil.isInFov((float)fov.getRoundedValue(), this.targetBedPos)) {
         this.resetState();
      } else {
         if (!this.hasTarget) {
            this.pathToTarget = new ArrayList<>();
            BlockPos found = this.findNearestBed(range.getRoundedValue());
            if (found == null) {
               this.resetState();
               return;
            }

            this.targetBedPos = found;
            this.readyToBreak = false;
            this.hasTarget = true;
         } else if (!this.readyToBreak && this.targetBedPos != null && !this.mc.theWorld.isAirBlock(this.targetBedPos)) {
            if (this.mc.theWorld.getBlockState(this.targetBedPos).getValue(BlockBed.PART) == EnumPartType.HEAD) {
               this.pathToTarget = this.computeApproachPathToBed(this.targetBedPos, range.getRoundedValue());
            } else {
               this.pathToTarget = this.computeApproachPathToBed(this.targetBedPos, range.getRoundedValue());
            }

            this.totalSteps = this.pathToTarget.size() + 1;
            this.readyToBreak = true;
         }

         if (this.readyToBreak) {
            ((AccessorKeybinding)this.mc.gameSettings.keyBindAttack).setPressed(false);
            this.attackKeyOverride = true;
            if (this.mc.theWorld.isAirBlock(this.targetBedPos)) {
               this.resetState();
               return;
            }

            if (!this.pathToTarget.isEmpty()) {
               if (this.mc.theWorld.isAirBlock((BlockPos)this.pathToTarget.get(0).first())) {
                  this.pathToTarget.remove(0);
               } else {
                  if (!BlockUtil.isWithinRange(ClientUtil.getPlayerBlockPos(), (BlockPos)this.pathToTarget.get(0).first(), range.getRoundedValue())) {
                     this.resetState();
                     return;
                  }

                  if (!keepRotation.getState() && this.pathToTarget.get(0).first() != this.lastDigPos) {
                     this.rotateToBlock((BlockPos)this.pathToTarget.get(0).first());
                     this.rotationTimer.reset();
                     this.shouldResetRotation = true;
                  } else if (keepRotation.getState()) {
                     this.rotateToBlock((BlockPos)this.pathToTarget.get(0).first());
                  }

                  if (this.shouldResetRotation && this.rotationTimer.hasTimePassed(10)) {
                     RotationManager.syncRotationWithPlayer();
                     isRotating = false;
                     this.shouldResetRotation = false;
                  }

                  this.selectBestTool();
                  this.performAnimation();
                  this.mc.playerController.onPlayerDamageBlock((BlockPos)this.pathToTarget.get(0).first(), (EnumFacing)this.pathToTarget.get(0).second());
                  this.lastDigPos = (BlockPos)this.pathToTarget.get(0).first();
                  this.renderTargetPos = (BlockPos)this.pathToTarget.get(0).first();
                  isBreakingBlock = true;
               }
            } else {
               if (this.targetBedPos == null) {
                  this.resetState();
                  return;
               }

               if (!BlockUtil.isWithinRange(ClientUtil.getPlayerBlockPos(), this.targetBedPos, range.getRoundedValue())) {
                  this.resetState();
                  return;
               }

               this.rotateToBlock(this.targetBedPos);
               this.selectBestTool();
               this.performAnimation();
               this.mc.playerController.onPlayerDamageBlock(this.targetBedPos, BlockUtil.getClosestFacing(this.targetBedPos));
               this.lastDigPos = this.targetBedPos;
               this.renderTargetPos = this.targetBedPos;
               isBreakingBlock = true;
            }
         }
      }
   }

   @SubscribeEvent
   public void onPacket(PacketReceiveEvent event) {
      if (event.getPacket() instanceof S02PacketChat) {
         String text = ((S02PacketChat)event.getPacket()).getChatComponent().getFormattedText();
         if (text.contains("§e§lProtect your bed and destroy the enemy bed") || text.contains("§e§lDestroy the enemy bed and then eliminate them")) {
            this.isBedwarsGame = true;
         }
      }

      if (event.getPacket() instanceof S08PacketPlayerPosLook && this.isBedwarsGame) {
         this.isBedwarsGame = false;
         this.knownBedPositions.clear();
         new Thread(() -> {
            int sX = MathHelper.floor_double(this.mc.thePlayer.posX);
            int sY = MathHelper.floor_double(this.mc.thePlayer.posY + (double)this.mc.thePlayer.getEyeHeight());
            int sZ = MathHelper.floor_double(this.mc.thePlayer.posZ);

            for (int i = sX - 25; i <= sX + 25; i++) {
               for (int j = sY - 25; j <= sY + 25; j++) {
                  for (int k = sZ - 25; k <= sZ + 25; k++) {
                     BlockPos blockPos = new BlockPos(i, j, k);
                     Block block = this.mc.theWorld.getBlockState(blockPos).getBlock();
                     if (block instanceof BlockBed) {
                        this.knownBedPositions.add(blockPos);
                     }
                  }
               }
            }
         }).start();
      }
   }

   @SubscribeEvent
   public void onPacket(PacketSendEvent event) {
      if (isBreakingBlock
         && event.getPacket() instanceof C07PacketPlayerDigging
         && ((C07PacketPlayerDigging)event.getPacket()).getStatus() == Action.ABORT_DESTROY_BLOCK) {
         event.setCanceled(true);
      }
   }

   @SubscribeEvent
   public void onRender(Render3DEvent event) {
      if (this.renderTargetPos != null) {
         String var4 = targetColorMode.getCurrent();
         int color;
         switch (var4) {
            case "THEME":
               color = Theme.computeThemeColor(0.0);
               break;
            case "THEME_CUSTOM":
               color = Theme.computeCustomThemeColor(0.0);
               break;
            default:
               color = customColor.F();
         }

         RenderUtil.drawBlockBox(this.renderTargetPos, color, showTargetOutline.getState(), showTargetShade.getState());
      }
   }

   @SubscribeEvent
   public void onRender(Render2DEvent event) {
      if (this.renderTargetPos != null) {
         String scaledResolution = targetColorMode.getCurrent();
         int color;
         switch (scaledResolution) {
            case "THEME":
               color = Theme.computeThemeColor(0.0);
               break;
            case "THEME_CUSTOM":
               color = Theme.computeCustomThemeColor(0.0);
               break;
            default:
               color = customColor.F();
         }

         if (showTargetBar.getState()) {
            ScaledResolution sr = new ScaledResolution(Minecraft.getMinecraft());
            float curHP = ((AccessorPlayerController)this.mc.playerController).getCurBlockDamageMP();
            if (!this.pathToTarget.isEmpty() && curHP == 0.0F && this.mc.theWorld.isAirBlock((BlockPos)this.pathToTarget.get(0).first())) {
               curHP = 1.0F;
            }

            float percentage = ((float)(this.totalSteps - 1 - this.pathToTarget.size()) + curHP) / (float)this.totalSteps;
            if (this.targetBedPos != null && curHP == 0.0F && this.mc.theWorld.isAirBlock(this.targetBedPos)) {
               percentage = 1.0F;
            }

            int x = (int)((float)sr.getScaledWidth() / 2.0F - 50.0F + 100.0F * percentage);
            int y = sr.getScaledHeight() / 2 + 63;
            FontRenderer font = Minecraft.getMinecraft().fontRendererObj;
            Gui.drawRect(sr.getScaledWidth() / 2 - 50 - 2, y, x + 2, y + font.FONT_HEIGHT + 2, color);
         }

         if (showTargetPercentage.getState()) {
            ScaledResolution scaledResolutionx = new ScaledResolution(Minecraft.getMinecraft());
            FontRenderer font = Minecraft.getMinecraft().fontRendererObj;
            float curHPx = ((AccessorPlayerController)this.mc.playerController).getCurBlockDamageMP();
            if (!this.pathToTarget.isEmpty() && curHPx == 0.0F && this.mc.theWorld.isAirBlock((BlockPos)this.pathToTarget.get(0).first())) {
               curHPx = 1.0F;
            }

            int percentage = (int)(((float)(this.totalSteps - 1 - this.pathToTarget.size()) + curHPx) / (float)this.totalSteps * 100.0F);
            if (this.targetBedPos != null && curHPx == 0.0F && this.mc.theWorld.isAirBlock(this.targetBedPos)) {
               percentage = 100;
            }

            String show = percentage + "%";
            font.drawStringWithShadow(
               show,
               (float)(scaledResolutionx.getScaledWidth() / 2 - font.getStringWidth(show) / 2),
               (float)(scaledResolutionx.getScaledHeight() / 2 + 65),
               16777215
            );
         }
      }
   }

   @Override
   public void onDisable() {
      this.resetState();
   }

   private void resetState() {
      this.totalSteps = 0;
      this.renderTargetPos = null;
      isRotating = false;
      this.hasTarget = false;
      this.readyToBreak = false;
      this.targetBedPos = null;
      this.pathToTarget = new ArrayList<>();
      if (isBreakingBlock && this.lastDigPos != null) {
         PacketUtil.sendPacket(new C07PacketPlayerDigging(Action.ABORT_DESTROY_BLOCK, this.lastDigPos, EnumFacing.DOWN));
      }

      isBreakingBlock = false;
      if (this.switchedHotbar) {
         if (this.previousHotbarSlot != -1) {
            this.mc.thePlayer.inventory.currentItem = this.previousHotbarSlot;
         }

         this.previousHotbarSlot = -1;
         this.switchedHotbar = false;
      }

      if (this.rotationApplied) {
         RotationManager.syncRotationWithPlayer();
         this.rotationApplied = false;
      }

      if (this.attackKeyOverride) {
         ((AccessorKeybinding)this.mc.gameSettings.keyBindAttack).setPressed(Mouse.isButtonDown(0));
         this.attackKeyOverride = false;
      }
   }

   private void rotateToBlock(BlockPos blockPos) {
      if (this.rotationThrottle.hasTimePassed(1L, true)) {
         float[] point = RotationUtil.getRotationFromBlock(blockPos);
         RotationManager.setRotation(point[0], point[1]);
         this.rotationApplied = true;
         isRotating = true;
      }
   }

   private void selectBestTool() {
      if (!this.pathToTarget.isEmpty()) {
         int toolSlot = ClientUtil.getBestToolForBlock(BlockUtil.getBlock((BlockPos)this.pathToTarget.get(0).first()));
         if (toolSlot != -1 && autoSelectTool.getState()) {
            if (!this.switchedHotbar) {
               this.previousHotbarSlot = this.mc.thePlayer.inventory.currentItem;
            }

            this.mc.thePlayer.inventory.currentItem = toolSlot;
            this.switchedHotbar = true;
         }
      }
   }

   private void performAnimation() {
      if (swingClient.getState()) {
         mc.thePlayer.swingItem();
      } else {
         PacketUtil.sendPacket(new C0APacketAnimation());
      }
   }


   private List<Pair<BlockPos, EnumFacing>> computeApproachPathToBed(BlockPos bed, double range) {
      BlockPos eyePos = BlockUtil.toBlockPos(ClientUtil.getPlayerEyesPosition());
      if (!BlockUtil.isWithinRange(eyePos, bed, range)) {
         return new ArrayList<>();
      } else {
         BlockPos pos = BlockUtil.rayTraceWithRotation(new float[]{0.0F, 90.0F}, 1.5, ClientUtil.getRenderPartialTicks()).getBlockPos();
         int minX = Math.min(bed.getX(), pos.getX());
         int maxX = Math.max(bed.getX(), pos.getX());
         int minY = Math.min(bed.getY(), pos.getY());
         int maxY = Math.max(bed.getY(), pos.getY());
         int minZ = Math.min(bed.getZ(), pos.getZ());
         int maxZ = Math.max(bed.getZ(), pos.getZ());
         BlockPos closestAirBlock = new BlockPos(pos.getX(), pos.getY(), pos.getZ());

         for (int x = minX; x <= maxX; x++) {
            for (int y = minY; y <= maxY; y++) {
               for (int z = minZ; z <= maxZ; z++) {
                  BlockPos pos2 = new BlockPos(x, y, z);
                  if (!this.isSurroundedByBlocks(pos2)
                     && this.mc.theWorld.isAirBlock(pos2)
                     && BlockUtil.toVec3(pos2).distanceTo(BlockUtil.toVec3(bed)) <= BlockUtil.toVec3(closestAirBlock).distanceTo(BlockUtil.toVec3(bed))) {
                     closestAirBlock = pos2;
                  }
               }
            }
         }

         List<Pair<BlockPos, EnumFacing>> path = new ArrayList<>();
         int cx = closestAirBlock.getX();
         int cy = closestAirBlock.getY();
         int cz = closestAirBlock.getZ();
         int bedX = bed.getX();
         int bedY = bed.getY();
         int bedZ = bed.getZ();

         for (int i = 0; i < 10 && (cx != bedX || cy != bedY || cz != bedZ); i++) {
            int dx = bedX - cx;
            int dy = bedY - cy;
            int dz = bedZ - cz;
            int adx = Math.abs(dx);
            int ady = Math.abs(dy);
            int adz = Math.abs(dz);
            if (adx >= ady && adx >= adz) {
               cx += dx > 0 ? 1 : -1;
            } else if (adz >= adx && adz >= ady) {
               cz += dz > 0 ? 1 : -1;
            } else {
               cy += dy > 0 ? 1 : -1;
            }

            BlockPos bp = new BlockPos(cx, cy, cz);
            path.add(new Pair(bp, BlockUtil.getClosestFacing(bp)));
         }

         path.removeIf(o -> this.mc.theWorld.isAirBlock((BlockPos)o.first()) || this.mc.theWorld.getBlockState((BlockPos)o.first()).getBlock() instanceof BlockBed);
         if (path.size() > 1 && ignoreOutsideLayer.getState()) {
            path.remove(0);
         }

         return path;
      }
   }

   private BlockPos findNearestBed(double radius) {
      BlockPos posD = BlockUtil.rayTraceWithRotation(new float[]{0.0F, 90.0F}, 1.5, ClientUtil.getRenderPartialTicks()).getBlockPos();
      BlockPos bed = null;

      for (double dx = -radius; dx <= radius; dx += 0.1) {
         for (double dy = -radius; dy <= radius; dy++) {
            for (double dz = -radius; dz <= radius; dz += 0.1) {
               BlockPos pos = posD.add(dx, dy, dz);
               if (this.mc.theWorld.getBlockState(pos).getBlock() instanceof BlockBed
                  && (bed == null || BlockUtil.toVec3(pos).distanceTo(BlockUtil.toVec3(posD)) <= BlockUtil.toVec3(bed).distanceTo(BlockUtil.toVec3(posD)))) {
                  bed = pos;
               }
            }
         }
      }

      return whitelistOwnBed.getState() && this.knownBedPositions.contains(bed) ? null : bed;
   }

   private boolean isSurroundedByBlocks(BlockPos pos) {
      BlockPos[] neighbors = new BlockPos[]{pos.up(), pos.down(), pos.north(), pos.south(), pos.east(), pos.west()};
      BlockPos[] var4 = neighbors;
      int var5 = neighbors.length;
      int var6 = 0;

      while (var6 < var5) {
         BlockPos neighbor = var4[var6];
         Block block = this.mc.theWorld.getBlockState(neighbor).getBlock();
         if (block == Blocks.air) {
            return false;
         }

         var6++;
      }

      return true;
   }

   private boolean arrayContainsTarget(BlockPos[] array, BlockPos target) {
      if (array != null && target != null) {
         for (BlockPos pos : array) {
            if (pos != null && pos.equals(target)) {
               return true;
            }
         }

         return false;
      } else {
         return false;
      }
   }

   // $VF: Irreducible bytecode was duplicated to produce valid code
   static {
      range = new SliderSetting("Range", 4.5, 0.0, 8.0, 0.01);
      fov = new SliderSetting("FOV", 360.0, 0.0, 360.0, 1.0);
      movementModeDescription = new DescriptionSetting("SILENT, STRICT, NONE");
      movementMode = new ModeSetting("Move-fix", "SILENT", "STRICT", "NONE");
      swingClient = new BooleanSetting("Swing", true);
      requireClick = new BooleanSetting("Require-click", false);
      whitelistOwnBed = new BooleanSetting("Whitelist-own-bed", true);
      ignoreOutsideLayer = new BooleanSetting("Ignore-outside-layer", true);
      autoSelectTool = new BooleanSetting("Auto-item", true);
      keepRotation = new BooleanSetting("Keep-rotation", true);
      allowKillAura = new BooleanSetting("Allow-KillAura", false);
      colorModeDescription = new DescriptionSetting("THEME, THEME_CUSTOM, CUSTOM");
      targetColorMode = new ModeSetting("Show-target-color", "THEME", "THEME_CUSTOM", "CUSTOM");
      customColor = new ColorSetting("Custom-color", "FFFFFF");
      showTargetShade = new BooleanSetting("Show-target-shade", false);
      showTargetOutline = new BooleanSetting("Show-target-outline", true);
      showTargetPercentage = new BooleanSetting("Show-target-percentage", false);
      showTargetBar = new BooleanSetting("Show-target-bar", false);
      isBreakingBlock = false;
      isRotating = false;
   }
}
