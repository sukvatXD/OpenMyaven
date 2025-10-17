package Myaven.module.modules.world;

import Myaven.events.PlayerUpdateEvent;
import Myaven.events.PreMotionEvent;
import Myaven.events.Render2DEvent;
import Myaven.management.RotationManager;
import Myaven.mixins.accessor.AccessorJumpTicks;
import Myaven.mixins.accessor.AccessorKeybinding;
import Myaven.module.Category;
import Myaven.module.Module;
import Myaven.module.modules.config.Theme;
import Myaven.module.modules.visual.FreeLook;
import Myaven.setting.Setting;
import Myaven.setting.settings.BooleanSetting;
import Myaven.setting.settings.ColorSetting;
import Myaven.setting.settings.DescriptionSetting;
import Myaven.setting.settings.ModeSetting;
import Myaven.setting.settings.SliderSetting;
import Myaven.util.BlockUtil;
import Myaven.util.ClientUtil;
import Myaven.util.ItemUtil;
import Myaven.util.PacketUtil;
import Myaven.util.PlayerUtil;
import Myaven.util.RenderUtil;
import Myaven.util.RotationUtil;
import Myaven.util.TimerUtil;
import akka.japi.Pair;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemBlock;
import net.minecraft.network.play.client.C0APacketAnimation;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

public class Scaffold extends Module {
   public static DescriptionSetting modeDescription;
   public static DescriptionSetting extraModeDescription;
   public static ModeSetting mode;
   public static DescriptionSetting rotationModeDesc;
   public static ModeSetting rotationMode;
   public static DescriptionSetting hitVecDesc;
   public static ModeSetting hitVecMode;
   public static SliderSetting hitOffset;
   public static SliderSetting jumpBlocks;
   public static SliderSetting straightAirDelayMs;
   public static SliderSetting diagonalAirDelayMs;
   public static SliderSetting angleStep;
   public static SliderSetting keepYAngleStep;
   public static DescriptionSetting moveFixDesc;
   public static ModeSetting moveFixMode;
   public static BooleanSetting multiPlace;
   public static BooleanSetting swingHand;
   public static BooleanSetting autoSelectItem;
   public static BooleanSetting keepYOnRightClick;
   public static BooleanSetting showItemCounter;
   public static BooleanSetting blockESP;
   public static DescriptionSetting espColorDescription;
   public static ModeSetting espColorMode;
   public static ColorSetting customESPColor;
   private TimerUtil rotationStepTimer = new TimerUtil();
   private TimerUtil placeDelayTimer = new TimerUtil();
   private TimerUtil placeThrottleTimer;
   private TimerUtil miscTimer;
   private boolean isRotating;
   private boolean shouldInitTargetY;
   private double targetFloorY;
   private int previousHotbarSlot;
   private boolean autoSwitchedHotbar;
   private boolean sneakPressed;
   private boolean queuedJump;
   private int groundTicks;
   private BlockUtil.BlockData currentBlockData;
   private BlockUtil.BlockData pendingBlockData;
   private boolean useHigherPlacement;
   private long sneakStartTimeMs;
   private boolean jumpKeySynced;
   private boolean hasMovementInput;
   private boolean movementStarted;
   private int airTicks;
   private boolean placedThisTick;
   private long sneakHoldDurationMs;
   private long airPlaceDelayMs;
   private boolean suppressingUseItem;
   private boolean inJumpPhase;
   private int jumpPhaseTicks;
   private boolean needYawDecrease;
   private boolean needYawIncrease;
   public static boolean hasPlacePreview;

   public Scaffold() {
      super("Scaffold", false, Category.World, true, "Bridge automatically for you");
      this.placeThrottleTimer = new TimerUtil();
      this.miscTimer = new TimerUtil();
      this.isRotating = false;
      this.shouldInitTargetY = true;
      this.autoSwitchedHotbar = false;
      this.sneakPressed = false;
      this.queuedJump = false;
      this.groundTicks = 0;
      this.useHigherPlacement = false;
      this.sneakStartTimeMs = -1L;
      this.jumpKeySynced = false;
      this.hasMovementInput = false;
      this.movementStarted = false;
      this.airTicks = 0;
      this.placedThisTick = false;
      this.sneakHoldDurationMs = ClientUtil.getRandomDoubleInMillis(BridgeAssist.minDelay.getRoundedValue(), BridgeAssist.maxDelay.getRoundedValue());
      this.airPlaceDelayMs = (long)straightAirDelayMs.getRoundedValue();
      this.suppressingUseItem = false;
      this.inJumpPhase = false;
      this.jumpPhaseTicks = 0;
      this.needYawDecrease = false;
      this.needYawIncrease = false;
      this.addSettings(
         new Setting[]{
            modeDescription,
            extraModeDescription,
            mode,
            rotationModeDesc,
            rotationMode,
            hitVecDesc,
            hitVecMode,
            hitOffset,
            jumpBlocks,
            straightAirDelayMs,
            diagonalAirDelayMs,
            angleStep,
            keepYAngleStep,
            moveFixDesc,
            moveFixMode,
            multiPlace,
            swingHand,
            autoSelectItem,
            keepYOnRightClick,
            showItemCounter,
            blockESP,
            espColorDescription,
            espColorMode,
            customESPColor
         }
      );
   }

   @Override
   public void onDisable() {
      this.pendingBlockData = null;
      this.inJumpPhase = false;
      hasPlacePreview = false;
      this.placedThisTick = false;
      this.hasMovementInput = false;
      this.movementStarted = false;
      this.sneakStartTimeMs = -1L;
      this.useHigherPlacement = false;
      this.shouldInitTargetY = true;
      this.queuedJump = false;
      this.groundTicks = 0;
      if (!this.jumpKeySynced) {
         ((AccessorKeybinding)this.mc.gameSettings.keyBindJump).setPressed(Keyboard.isKeyDown(this.mc.gameSettings.keyBindJump.getKeyCode()));
         this.jumpKeySynced = true;
      }

      if (this.autoSwitchedHotbar) {
         this.mc.thePlayer.inventory.currentItem = this.previousHotbarSlot;
         this.autoSwitchedHotbar = false;
      }

      if (this.sneakPressed) {
         KeyBinding.setKeyBindState(this.mc.gameSettings.keyBindSneak.getKeyCode(), Keyboard.isKeyDown(this.mc.gameSettings.keyBindSneak.getKeyCode()));
         ((AccessorKeybinding)this.mc.gameSettings.keyBindSneak).setPressed(Keyboard.isKeyDown(this.mc.gameSettings.keyBindSneak.getKeyCode()));
         this.sneakPressed = false;
      }

      if (this.isRotating) {
         RotationManager.syncRotationWithPlayer();
         this.isRotating = false;
      }

      if (this.suppressingUseItem) {
         this.suppressingUseItem = false;
         ((AccessorKeybinding)this.mc.gameSettings.keyBindUseItem).setPressed(Mouse.isButtonDown(1));
      }
   }

   @SubscribeEvent
   public void onUpdate(PlayerUpdateEvent event) {
      ((AccessorKeybinding)this.mc.gameSettings.keyBindUseItem).setPressed(false);
      this.suppressingUseItem = true;
      this.jumpKeySynced = false;
      String cache = moveFixMode.getCurrent();
      switch (cache) {
         case "SILENT":
            RotationManager.setMovementMode(RotationManager.MovementMode.SILENT);
            break;
         case "STRICT":
            RotationManager.setMovementMode(RotationManager.MovementMode.STRICT);
            break;
         case "NONE":
            RotationManager.setMovementMode(RotationManager.MovementMode.NONE);
      }

      if (autoSelectItem.getState() && (this.mc.thePlayer.getHeldItem() == null || !ItemUtil.isPlaceableBlock(this.mc.thePlayer.getHeldItem()))) {
         if (!this.autoSwitchedHotbar) {
            this.previousHotbarSlot = this.mc.thePlayer.inventory.currentItem;
            this.autoSwitchedHotbar = true;
         }

         boolean found = false;

         for (int i = 0; i < InventoryPlayer.getHotbarSize(); i++) {
            if (this.mc.thePlayer.inventory.mainInventory[i] != null
               && ItemUtil.isPlaceableBlock(this.mc.thePlayer.inventory.mainInventory[i])
               && !found
               && this.mc.thePlayer.inventory.mainInventory[i].stackSize != 0) {
               this.mc.thePlayer.inventory.currentItem = i;
               found = true;
            }
         }

         if (!found) {
            RotationManager.syncRotationWithPlayer();
            return;
         }
      }

      if (this.mc.thePlayer.getHeldItem() != null && this.mc.thePlayer.getHeldItem().getItem() instanceof ItemBlock && this.mc.currentScreen == null) {
         if (this.pendingBlockData == null
            || this.pendingBlockData != null
               && ((Vec3)this.computeRotationAndHitVec(this.pendingBlockData.pos, this.pendingBlockData.facing).second())
                     .distanceTo(ClientUtil.getPlayerEyesPosition())
                  >= 4.5) {
            BlockUtil.BlockData cache1 = BlockUtil.getPlaceableBlock(this.mc.thePlayer.posY - 1.0);
            if (cache1 == null) {
               if (this.pendingBlockData == null) {
                  BlockPos raytrace = BlockUtil.rayTraceWithRotation(
                        new float[]{this.mc.thePlayer.rotationYaw - 180.0F, 90.0F}, 2.0, ClientUtil.getRenderPartialTicks()
                     )
                     .getBlockPos();
                  this.k(raytrace, EnumFacing.UP, 180.0F);
               } else if (this.pendingBlockData.pos.getY() > (int)this.mc.thePlayer.posY) {
                  this.k(this.pendingBlockData.pos, this.pendingBlockData.facing, 180.0F);
                  this.pendingBlockData = null;
               }
            }
         }

         cache = mode.getCurrent();
         switch (cache) {
            case "NORMAL":
               this.placeNormal();
               break;
            case "LEGIT":
               if (!this.jumpKeySynced) {
                  ((AccessorKeybinding)this.mc.gameSettings.keyBindJump).setPressed(Keyboard.isKeyDown(this.mc.gameSettings.keyBindJump.getKeyCode()));
                  this.jumpKeySynced = true;
               }

               BlockUtil.BlockData cacheLegit = BlockUtil.getPlaceableBlock(this.mc.thePlayer.posY - 1.0);
               if (cacheLegit != null) {
                  this.pendingBlockData = cacheLegit;
               }

               this.currentBlockData = cacheLegit;
               if (this.sneakPressed && this.sneakStartTimeMs != -1L && System.currentTimeMillis() - this.sneakStartTimeMs > this.sneakHoldDurationMs) {
                  this.sneakHoldDurationMs = ClientUtil.getRandomDoubleInMillis(
                     BridgeAssist.minDelay.getRoundedValue(), BridgeAssist.maxDelay.getRoundedValue()
                  );
                  KeyBinding.setKeyBindState(this.mc.gameSettings.keyBindSneak.getKeyCode(), false);
                  ((AccessorKeybinding)this.mc.gameSettings.keyBindSneak).setPressed(false);
                  this.sneakPressed = false;
                  this.sneakStartTimeMs = -1L;
               }

               if (cacheLegit != null && (double)cacheLegit.pos.getY() <= this.mc.thePlayer.posY - 1.0 && !BlockUtil.isEntityColliding(cacheLegit.pos)) {
                  long currentTime = System.currentTimeMillis();
                  if ((ClientUtil.isInWorld() || ClientUtil.isPlayerOnGround()) && (PlayerUtil.isMoving() || !ClientUtil.isJumping())) {
                     KeyBinding.setKeyBindState(this.mc.gameSettings.keyBindSneak.getKeyCode(), true);
                     ((AccessorKeybinding)this.mc.gameSettings.keyBindSneak).setPressed(true);
                     this.sneakPressed = true;
                     this.sneakStartTimeMs = currentTime;
                  }

                  this.k(cacheLegit.pos, cacheLegit.facing, (float)angleStep.getRoundedValue());
                  this.rightClickBlock(cacheLegit.pos, (Vec3)this.computeRotationAndHitVec(cacheLegit.pos, cacheLegit.facing).second(), cacheLegit.facing);
                  if (multiPlace.getState()) {
                     cacheLegit = BlockUtil.getPlaceableBlock(this.mc.thePlayer.posY - 1.0);
                     if (cacheLegit != null) {
                        this.pendingBlockData = cacheLegit;
                     }

                     this.currentBlockData = cacheLegit;
                     if (cacheLegit != null
                        && BlockUtil.isSamePosition(BlockUtil.rayTraceFromPlayer(4.0, ClientUtil.getRenderPartialTicks()).getBlockPos(), cacheLegit.pos)) {
                        this.rightClickBlock(cacheLegit.pos, (Vec3)this.computeRotationAndHitVec(cacheLegit.pos, cacheLegit.facing).second(), cacheLegit.facing);
                     }
                  }

                  if (swingHand.getState()) {
                     this.mc.thePlayer.swingItem();
                  } else {
                     PacketUtil.sendPacket(new C0APacketAnimation());
                  }
               }
               break;
            case "KEEP_Y":
            case "KEEP_Y_EXTRA":
               ((AccessorKeybinding)this.mc.gameSettings.keyBindUseItem).setPressed(false);
               if ((!keepYOnRightClick.getState() || !Mouse.isButtonDown(1)) && keepYOnRightClick.getState()) {
                  this.placeNormal();
               } else {
                  if (Keyboard.isKeyDown(this.mc.gameSettings.keyBindJump.getKeyCode())) {
                     if (this.placeDelayTimer.hasTimePassed(this.airPlaceDelayMs + 100L) && !this.queuedJump) {
                        this.targetFloorY = this.mc.thePlayer.posY - 1.0;
                     }
                  } else if (this.shouldInitTargetY) {
                     this.targetFloorY = (double)((int)this.mc.thePlayer.posY - 1);
                     this.shouldInitTargetY = false;
                  }

                  BlockUtil.BlockData extra = BlockUtil.getPlaceableBlock(this.targetFloorY + 1.0);
                  if (!this.placedThisTick && extra != null && (double)extra.pos.getY() != this.targetFloorY && this.airTicks == 2) {
                     this.useHigherPlacement = true;
                  }

                  BlockUtil.BlockData cacheTelly = mode.getCurrent().equalsIgnoreCase("KEEP_Y_EXTRA") && this.useHigherPlacement
                     ? BlockUtil.getPlaceableBlock(this.targetFloorY + 1.0)
                     : BlockUtil.getPlaceableBlock(this.targetFloorY);
                  if (cacheTelly != null) {
                     this.pendingBlockData = cacheTelly;
                  }

                  this.currentBlockData = cacheTelly;
                  if (!this.mc.gameSettings.keyBindForward.isKeyDown()
                     && !this.mc.gameSettings.keyBindLeft.isKeyDown()
                     && !this.mc.gameSettings.keyBindRight.isKeyDown()
                     && !this.mc.gameSettings.keyBindBack.isKeyDown()) {
                     this.shouldInitTargetY = true;
                     this.hasMovementInput = false;
                     this.movementStarted = false;
                  } else {
                     if (!this.movementStarted) {
                        this.movementStarted = true;
                        this.hasMovementInput = false;
                     }

                     if (this.mc.thePlayer.onGround && !this.queuedJump && (double)this.groundTicks >= jumpBlocks.getRoundedValue()) {
                        this.shouldInitTargetY = true;
                        this.queuedJump = true;
                        ((AccessorKeybinding)this.mc.gameSettings.keyBindJump).setPressed(false);
                     }
                  }

                  if (this.movementStarted) {
                     this.hasMovementInput = true;
                  }

                  if (this.queuedJump && PlayerUtil.isOnGround() && (double)this.groundTicks >= jumpBlocks.getRoundedValue()) {
                     if (!this.hasMovementInput) {
                        ((AccessorJumpTicks)this.mc.thePlayer).setJumpTicks(0);
                        ((AccessorKeybinding)this.mc.gameSettings.keyBindJump).setPressed(true);
                        this.queuedJump = false;
                     } else {
                        float actual = FreeLook.isActive ? FreeLook.freeLookYaw : this.mc.thePlayer.rotationYaw;
                        if (actual > RotationManager.currentYaw && Math.abs(RotationUtil.getAngleDifference(RotationManager.currentYaw, actual)) >= 30.0F) {
                           this.needYawDecrease = true;
                        }

                        if (actual < RotationManager.currentYaw && Math.abs(RotationUtil.getAngleDifference(RotationManager.currentYaw, actual)) >= 30.0F) {
                           this.needYawIncrease = true;
                        }

                        RotationManager.currentYaw = RotationManager.currentYaw + RotationUtil.getAngleDifference(RotationManager.currentYaw, actual);
                        RotationManager.movementYaw = actual;
                        RotationManager.currentPitch = 90.0F;
                        this.isRotating = true;
                        ((AccessorJumpTicks)this.mc.thePlayer).setJumpTicks(0);
                        KeyBinding.setKeyBindState(this.mc.gameSettings.keyBindJump.getKeyCode(), true);
                        ((AccessorKeybinding)this.mc.gameSettings.keyBindJump).setPressed(true);
                        KeyBinding.onTick(this.mc.gameSettings.keyBindJump.getKeyCode());
                        this.airTicks = 0;
                        this.useHigherPlacement = true;
                        this.placedThisTick = false;
                        this.placeDelayTimer.reset();
                        this.miscTimer.reset();
                        this.placeThrottleTimer.reset();
                        this.queuedJump = false;
                        this.inJumpPhase = true;
                        this.jumpPhaseTicks = 0;
                        this.airPlaceDelayMs = (long)(this.isDiagonalYaw() ? diagonalAirDelayMs.getRoundedValue() : straightAirDelayMs.getRoundedValue());
                     }
                  }

                  if (!this.queuedJump && this.placeThrottleTimer.hasTimePassed(2L)) {
                     if (cacheTelly != null && (double)cacheTelly.pos.getY() <= this.mc.thePlayer.posY - 1.0 && !BlockUtil.isEntityColliding(cacheTelly.pos)) {
                        this.k(cacheTelly.pos, cacheTelly.facing, (float)keepYAngleStep.getRoundedValue());
                        if (this.placeDelayTimer.hasTimePassed(this.airPlaceDelayMs + 3L)) {
                           this.inJumpPhase = false;
                           this.placedThisTick = true;
                           this.useHigherPlacement = false;
                           this.rightClickBlock(
                              cacheTelly.pos, (Vec3)this.computeRotationAndHitVec(cacheTelly.pos, cacheTelly.facing).second(), cacheTelly.facing
                           );
                           if (multiPlace.getState()) {
                              cacheTelly = BlockUtil.getPlaceableBlock(this.targetFloorY);
                              if (cacheTelly != null) {
                                 this.pendingBlockData = cacheTelly;
                              }

                              this.currentBlockData = cacheTelly;
                              if (cacheTelly != null
                                 && BlockUtil.isSamePosition(
                                    BlockUtil.rayTraceFromPlayer(4.0, ClientUtil.getRenderPartialTicks()).getBlockPos(), cacheTelly.pos
                                 )) {
                                 this.rightClickBlock(
                                    cacheTelly.pos, (Vec3)this.computeRotationAndHitVec(cacheTelly.pos, cacheTelly.facing).second(), cacheTelly.facing
                                 );
                              }
                           }

                           if (PlayerUtil.isOnGround()) {
                              this.groundTicks++;
                           } else {
                              this.airTicks++;
                              this.groundTicks = 0;
                           }

                           if (swingHand.getState()) {
                              this.mc.thePlayer.swingItem();
                           } else {
                              PacketUtil.sendPacket(new C0APacketAnimation());
                           }
                        }
                     } else if (!this.mc.gameSettings.keyBindForward.isKeyDown()
                        && !this.mc.gameSettings.keyBindLeft.isKeyDown()
                        && !this.mc.gameSettings.keyBindRight.isKeyDown()
                        && !this.mc.gameSettings.keyBindBack.isKeyDown()) {
                        ((AccessorKeybinding)this.mc.gameSettings.keyBindJump).setPressed(Keyboard.isKeyDown(this.mc.gameSettings.keyBindJump.getKeyCode()));
                     } else if ((double)this.groundTicks < jumpBlocks.getRoundedValue()) {
                        ((AccessorKeybinding)this.mc.gameSettings.keyBindJump).setPressed(false);
                     }
                  }
               }
         }
      }
   }

   @SubscribeEvent
   public void onMotion(PreMotionEvent event) {
      if (this.inJumpPhase) {
         this.jumpPhaseTicks++;
      } else {
         this.jumpPhaseTicks = 0;
      }
   }

   private void k(BlockPos blockPos, EnumFacing facing, float angleStep) {
      if (this.rotationStepTimer.hasTimePassed(1L, true)) {
         angleStep = (float)ClientUtil.getRandomDoubleInMillis((double)angleStep, (double)(angleStep + 5.0F));
         if (angleStep >= 179.0F) {
            RotationManager.rotateTowardsYaw(((float[])this.computeRotationAndHitVec(blockPos, facing).first())[0]);
         } else {
            float angStp = angleStep;
            if (this.jumpPhaseTicks >= 3 && this.inJumpPhase) {
               angStp = (float)ClientUtil.getRandomDoubleInMillis(90.0, 95.0);
            }

            if (this.needYawDecrease) {
               if (((float[])this.computeRotationAndHitVec(blockPos, facing).first())[0] > RotationManager.currentYaw) {
                  angStp = -30.0F;
               }

               this.needYawDecrease = false;
            }

            if (this.needYawIncrease) {
               if (((float[])this.computeRotationAndHitVec(blockPos, facing).first())[0] < RotationManager.currentYaw) {
                  angStp = 30.0F;
               }

               this.needYawIncrease = false;
            }

            RotationManager.stepYawTowards(((float[])this.computeRotationAndHitVec(blockPos, facing).first())[0], angStp);
         }

         RotationManager.setPitch(((float[])this.computeRotationAndHitVec(blockPos, facing).first())[1]);
         this.isRotating = true;
      }
   }

   private boolean isJumpInPlace() {
      return !PlayerUtil.isMoving() && this.mc.gameSettings.keyBindJump.isPressed();
   }

   private Pair<float[], Vec3> computeRotationAndHitVec(BlockPos blockpos, EnumFacing facing) {
      float[] floats = new float[]{RotationManager.getCurrentYaw(), RotationManager.getCurrentPitch()};
      Pair<float[], Vec3> theReturn = new Pair(floats, RotationUtil.getBlockHitVec(blockpos));
      if (this.isJumpInPlace() && (double)blockpos.getX() == this.mc.thePlayer.posX && (double)blockpos.getZ() == this.mc.thePlayer.posZ) {
         floats[0] = this.mc.thePlayer.rotationYaw - 180.0F;
         floats[1] = 90.0F;
         theReturn = new Pair(floats, RotationUtil.getBlockHitVec(blockpos));
      } else {
         String var6 = rotationMode.getCurrent();
         switch (var6) {
            case "NORMAL":
               floats[0] = RotationUtil.getRotationToVec(RotationUtil.getBlockHitVec(blockpos))[0];
               floats[1] = RotationUtil.getRotationToVec(RotationUtil.getBlockHitVec(blockpos))[1];
               theReturn = new Pair(floats, RotationUtil.getBlockHitVec(blockpos));
               break;
            case "BACK":
               floats[0] = PlayerUtil.getMovingDirection() - 180.0F;
               floats[1] = RotationUtil.getRotationToVec(RotationUtil.getBlockHitVec(blockpos))[1];
               MovingObjectPosition resultBACK = BlockUtil.rayTraceWithRotation(new float[]{floats[0], floats[1]}, 4.0, ClientUtil.getRenderPartialTicks());
               if (BlockUtil.isSamePosition(resultBACK.getBlockPos(), blockpos)) {
                  theReturn = new Pair(floats, resultBACK.hitVec);
               } else {
                  floats[0] = RotationUtil.getRotationToVec(RotationUtil.getBlockHitVec(blockpos))[0];
                  floats[1] = RotationUtil.getRotationToVec(RotationUtil.getBlockHitVec(blockpos))[1];
                  theReturn = new Pair(floats, RotationUtil.getBlockHitVec(blockpos));
               }
               break;
            case "OFFSET":
               Vec3 rot = RotationUtil.getOffsetFromBlock(blockpos, facing, hitOffset.getRoundedValue());
               floats[0] = RotationUtil.getRotationToVec(rot)[0];
               floats[1] = RotationUtil.getRotationToVec(rot)[1];
               theReturn = new Pair(floats, rot);
               break;
            case "DIAGONAL":
               floats[1] = RotationUtil.getRotationToVec(RotationUtil.getBlockHitVec(blockpos))[1];
               if (!BlockUtil.isSamePosition(
                     BlockUtil.rayTraceWithRotation(
                           new float[]{PlayerUtil.getMovingDirection() - 135.0F, RotationUtil.getRotationToVec(RotationUtil.getBlockHitVec(blockpos))[1]},
                           4.0,
                           ClientUtil.getRenderPartialTicks()
                        )
                        .getBlockPos(),
                     blockpos
                  )
                  && !BlockUtil.isSamePosition(
                     BlockUtil.rayTraceWithRotation(
                           new float[]{PlayerUtil.getMovingDirection() - 180.0F, RotationUtil.getRotationToVec(RotationUtil.getBlockHitVec(blockpos))[1]},
                           4.0,
                           ClientUtil.getRenderPartialTicks()
                        )
                        .getBlockPos(),
                     blockpos
                  )) {
                  floats[0] = RotationUtil.getRotationToVec(RotationUtil.getBlockHitVec(blockpos))[0];
                  floats[1] = RotationUtil.getRotationToVec(RotationUtil.getBlockHitVec(blockpos))[1];
                  theReturn = new Pair(floats, RotationUtil.getBlockHitVec(blockpos));
               } else if (this.isDiagonalYaw()) {
                  floats[0] = PlayerUtil.getMovingDirection() - 180.0F;
                  floats[1] = RotationUtil.getRotationToVec(RotationUtil.getBlockHitVec(blockpos))[1];
                  MovingObjectPosition result = BlockUtil.rayTraceWithRotation(new float[]{floats[0], floats[1]}, 4.0, ClientUtil.getRenderPartialTicks());
                  if (BlockUtil.isSamePosition(result.getBlockPos(), blockpos)) {
                     theReturn = new Pair(floats, result.hitVec);
                  } else {
                     floats[0] = RotationUtil.getRotationToVec(RotationUtil.getBlockHitVec(blockpos))[0];
                     floats[1] = RotationUtil.getRotationToVec(RotationUtil.getBlockHitVec(blockpos))[1];
                     theReturn = new Pair(floats, RotationUtil.getBlockHitVec(blockpos));
                  }
               } else {
                  floats[0] = PlayerUtil.getMovingDirection() - 135.0F;
                  MovingObjectPosition result2 = BlockUtil.rayTraceWithRotation(new float[]{floats[0], floats[1]}, 4.0, ClientUtil.getRenderPartialTicks());
                  if (BlockUtil.isSamePosition(result2.getBlockPos(), blockpos)) {
                     theReturn = new Pair(floats, result2.hitVec);
                  } else {
                     theReturn = new Pair(floats, BlockUtil.getBlockFaceCenter(blockpos, facing));
                  }
               }
         }
      }

      if (!hitVecMode.getCurrent().equalsIgnoreCase("RAYTRACE")) {
         theReturn = new Pair(
            floats,
            hitVecMode.getCurrent().equalsIgnoreCase("MID")
               ? BlockUtil.getHitVector(new BlockUtil.BlockData(blockpos, facing))
               : RotationUtil.getBlockHitVec(blockpos)
         );
      }

      return theReturn;
   }

   private void placeNormal() {
      if (!this.jumpKeySynced) {
         ((AccessorKeybinding)this.mc.gameSettings.keyBindJump).setPressed(Keyboard.isKeyDown(this.mc.gameSettings.keyBindJump.getKeyCode()));
         this.jumpKeySynced = true;
      }

      BlockUtil.BlockData cacheNormal = BlockUtil.getPlaceableBlock(this.mc.thePlayer.posY - 1.0);
      if (cacheNormal != null) {
         this.pendingBlockData = cacheNormal;
      }

      this.currentBlockData = cacheNormal;
      if (cacheNormal != null) {
         this.k(cacheNormal.pos, cacheNormal.facing, (float)angleStep.getRoundedValue());
      }

      if (cacheNormal != null && (double)cacheNormal.pos.getY() <= this.mc.thePlayer.posY - 1.0 && !BlockUtil.isEntityColliding(cacheNormal.pos)) {
         this.rightClickBlock(cacheNormal.pos, (Vec3)this.computeRotationAndHitVec(cacheNormal.pos, cacheNormal.facing).second(), cacheNormal.facing);
         if (multiPlace.getState()) {
            cacheNormal = BlockUtil.getPlaceableBlock(this.mc.thePlayer.posY - 1.0);
            if (cacheNormal != null) {
               this.pendingBlockData = cacheNormal;
            }

            this.currentBlockData = cacheNormal;
            if (cacheNormal != null
               && BlockUtil.isSamePosition(BlockUtil.rayTraceFromPlayer(4.0, ClientUtil.getRenderPartialTicks()).getBlockPos(), cacheNormal.pos)) {
               this.rightClickBlock(cacheNormal.pos, (Vec3)this.computeRotationAndHitVec(cacheNormal.pos, cacheNormal.facing).second(), cacheNormal.facing);
            }
         }

         if (swingHand.getState()) {
            this.mc.thePlayer.swingItem();
         } else {
            PacketUtil.sendPacket(new C0APacketAnimation());
         }
      }
   }

   private boolean isDiagonalYaw() {
      float yaw = FreeLook.isActive ? FreeLook.freeLookYaw : this.mc.thePlayer.rotationYaw;
      yaw = RotationUtil.wrapYaw(yaw, 0.0F, 360.0F);
      float delta = yaw % 90.0F;
      return delta > 20.0F && delta < 70.0F;
   }

   private void rightClickBlock(BlockPos blockPos, Vec3 hitVec, EnumFacing facing) {
      if (RotationUtil.getBlockHitVec(blockPos).distanceTo(ClientUtil.getPlayerEyesPosition()) <= (double)this.mc.playerController.getBlockReachDistance()) {
         this.mc.playerController.onPlayerRightClick(this.mc.thePlayer, this.mc.theWorld, this.mc.thePlayer.getHeldItem(), blockPos, facing, hitVec);
      }
   }

   @SubscribeEvent
   public void onRender(Render2DEvent event) {
      if (showItemCounter.getState()) {
         int item = 0;

         for (int i = 0; i < InventoryPlayer.getHotbarSize(); i++) {
            if (this.mc.thePlayer.inventory.getStackInSlot(i) != null && ItemUtil.isPlaceableBlock(this.mc.thePlayer.inventory.getStackInSlot(i))) {
               item += this.mc.thePlayer.inventory.getStackInSlot(i).stackSize;
            }
         }

         ScaledResolution scaledResolution = new ScaledResolution(Minecraft.getMinecraft());
         FontRenderer font = Minecraft.getMinecraft().fontRendererObj;
         String show = item + " blocks left";
         if (item > 32) {
            font.drawStringWithShadow(
               show,
               (float)(scaledResolution.getScaledWidth() / 2 - font.getStringWidth(show) / 2),
               (float)(scaledResolution.getScaledHeight() / 2 + 75),
               16777215
            );
         } else if (item <= 32) {
            font.drawStringWithShadow(
               show,
               (float)(scaledResolution.getScaledWidth() / 2 - font.getStringWidth(show) / 2),
               (float)(scaledResolution.getScaledHeight() / 2 + 75),
               16733525
            );
         }
      }
   }

   @SubscribeEvent
   public void onRender(RenderWorldLastEvent event) {
      if (blockESP.getState()) {
         String blockpos = espColorMode.getCurrent();
         int color;
         switch (blockpos) {
            case "THEME":
               color = Theme.computeThemeColor(0.0);
               break;
            case "THEME_CUSTOM":
               color = Theme.computeCustomThemeColor(0.0);
               break;
            default:
               color = customESPColor.F();
         }

         if (this.currentBlockData != null) {
            hasPlacePreview = true;
            RenderUtil.drawBlockBox(this.currentBlockData.pos.offset(this.currentBlockData.facing), color, false, true);
         } else {
            hasPlacePreview = false;
            BlockPos pos = BlockUtil.rayTraceWithRotation(new float[]{0.0F, 90.0F}, 2.0, ClientUtil.getRenderPartialTicks()).getBlockPos();
            if ((int)this.mc.thePlayer.posY - 4 <= pos.getY() && this.mc.theWorld.getBlockState(pos).getBlock() != Blocks.air) {
               RenderUtil.drawBlockBox(pos, color, false, true);
            }
         }
      }
   }

   @Override
   public String getSuffix() {
      return mode.getCurrent();
   }

   // $VF: Irreducible bytecode was duplicated to produce valid code
   static {
      modeDescription = new DescriptionSetting("NORMAL, LEGIT, KEEP_Y,");
      extraModeDescription = new DescriptionSetting("KEEP_Y_EXTRA");
      mode = new ModeSetting("Mode", "NORMAL", "LEGIT", "KEEP_Y", "KEEP_Y_EXTRA");
      rotationModeDesc = new DescriptionSetting("BACK, NORMAL, OFFSET, DIAGONAL");
      rotationMode = new ModeSetting("Rotation", "BACK", "NORMAL", "OFFSET", "DIAGONAL");
      hitVecDesc = new DescriptionSetting("MID, CLOSEST, RAYTRACE");
      hitVecMode = new ModeSetting("HitVec", "MID", "CLOSEST", "RAYTRACE");
      hitOffset = new SliderSetting("Offset", 0.15, 0.0, 1.0, 0.01);
      jumpBlocks = new SliderSetting("Jump blocks", 0.0, 0.0, 6.0, 1.0);
      straightAirDelayMs = new SliderSetting("Straight-air-delay", 150.0, 0.0, 600.0, 1.0);
      diagonalAirDelayMs = new SliderSetting("Diagonal-air-delay", 200.0, 0.0, 600.0, 1.0);
      angleStep = new SliderSetting("Angle-step", 90.0, 1.0, 180.0, 1.0);
      keepYAngleStep = new SliderSetting("Keep-y-angle-step", 30.0, 1.0, 180.0, 1.0);
      moveFixDesc = new DescriptionSetting("SILENT, STRICT, NONE");
      moveFixMode = new ModeSetting("Move-fix", "SILENT", "STRICT", "NONE");
      multiPlace = new BooleanSetting("Multiplace", false);
      swingHand = new BooleanSetting("Swing", true);
      autoSelectItem = new BooleanSetting("Auto-item", true);
      keepYOnRightClick = new BooleanSetting("Keep-Y-on-right-click", false);
      showItemCounter = new BooleanSetting("Item-counter", true);
      blockESP = new BooleanSetting("Block-ESP", true);
      espColorDescription = new DescriptionSetting("THEME, THEME_CUSTOM, CUSTOM");
      espColorMode = new ModeSetting("ESP-Color", "THEME", "THEME_CUSTOM", "CUSTOM");
      customESPColor = new ColorSetting("Custom-color", "FFFFFF");
      hasPlacePreview = false;
   }
}
