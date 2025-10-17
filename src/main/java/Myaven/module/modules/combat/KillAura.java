package Myaven.module.modules.combat;

import Myaven.Myaven;
import Myaven.events.PacketSendEvent;
import Myaven.events.PlayerUpdateEvent;
import Myaven.events.SlowdownEvent;
import Myaven.events.StopUseEvent;
import Myaven.management.RotationManager;
import Myaven.mixins.accessor.AccessorEntityPlayer;
import Myaven.mixins.accessor.AccessorKeybinding;
import Myaven.module.Category;
import Myaven.module.Module;
import Myaven.module.modules.config.Theme;
import Myaven.module.modules.world.BedNuker;
import Myaven.setting.Setting;
import Myaven.setting.settings.BooleanSetting;
import Myaven.setting.settings.ColorSetting;
import Myaven.setting.settings.DescriptionSetting;
import Myaven.setting.settings.ModeSetting;
import Myaven.setting.settings.SliderSetting;
import Myaven.util.ClientUtil;
import Myaven.util.PacketUtil;
import Myaven.util.RaytraceUtil;
import Myaven.util.RenderUtil;
import Myaven.util.RotationUtil;
import Myaven.util.TargetUtil;
import Myaven.util.TimerUtil;
import java.util.Comparator;
import java.util.List;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemSword;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.client.C09PacketHeldItemChange;
import net.minecraft.network.play.client.C02PacketUseEntity.Action;
import net.minecraft.util.Vec3;
import net.minecraft.util.MovingObjectPosition.MovingObjectType;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.input.Mouse;

public class KillAura extends Module {
   public static DescriptionSetting modeDescription;
   public static ModeSetting mode;
   public static DescriptionSetting sortDescription1;
   public static DescriptionSetting sortDescription2;
   public static ModeSetting sortMode;
   public static DescriptionSetting autoblockDescription1;
   public static DescriptionSetting autoblockDescription2;
   public static ModeSetting autoblockMode;
   public static SliderSetting fov;
   public static SliderSetting minAPS;
   public static SliderSetting maxAPS;
   public static SliderSetting autoblockRange;
   public static SliderSetting attackRange;
   public static SliderSetting swingRange;
   public static SliderSetting switchDelayMs;
   public static SliderSetting angleStep;
   public static DescriptionSetting moveFixDescription;
   public static ModeSetting moveFixMode;
   public static DescriptionSetting targetRenderDescription;
   public static ModeSetting targetRenderMode;
   public static DescriptionSetting colorDescription;
   public static ModeSetting colorMode;
   public static ColorSetting customColor;
   public static BooleanSetting simulateClicks;
   public static BooleanSetting autoblockRequireRightClick;
   public static BooleanSetting requireMouseDown;
   public static BooleanSetting requireSword;
   public static BooleanSetting screenCheck;
   public static BooleanSetting allowThroughWalls;
   public static BooleanSetting attackPlayers;
   public static BooleanSetting attackMobs;
   public static BooleanSetting attackAnimals;
   public static BooleanSetting attackBots;
   public static BooleanSetting teamCheck;
   private TimerUtil attackTimer = new TimerUtil();
   private TimerUtil rotationTimer = new TimerUtil();
   private boolean holdingUseKey = false;
   private boolean attackTickReady = false;
   private boolean rotationApplied = false;
   private boolean packetCancellation = false;
   public static boolean isBlocking;
   private boolean shouldAutoblock = false;
   public static EntityLivingBase currentTarget;
   private TimerUtil blinkTimer = new TimerUtil();
   private int blinkStep = 0;
   private int spoofSlotIndex = 0;
   private int originalHotbarSlot = 0;
   private boolean savedHotbarSlot = false;
   private TimerUtil blockTimer = new TimerUtil();
   public static boolean canBlink;
   public static boolean isAutoBlockActive;
   public static boolean combatTick;
   private long attackDelay;
   private boolean projectileSwitched;
   private boolean queuedAttack;
   private boolean queuedSwing;
   private EntityLivingBase queuedTarget;
   private boolean fakeBlocking;
   private boolean attackKeySuppressed;
   private boolean shouldUnblock;
   private boolean unblocking;
   private boolean reblocking;
   private TimerUtil unblockTimer;
   private TimerUtil reblockTimer;
   private long lastBlockPacketTime;
   private boolean blinkInProgress;
   public static long lastStopUseTime;
   private boolean heldItemSpoofed;
   private TimerUtil switchTargetTimer;
   private int switchIndex;

   public KillAura() {
      super("KillAura", false, Category.Combat, true, "Attack entities in range");
      this.attackDelay = ClientUtil.getDelayInMillis(minAPS.getRoundedValue(), maxAPS.getRoundedValue());
      this.projectileSwitched = false;
      this.queuedAttack = false;
      this.queuedSwing = false;
      this.queuedTarget = null;
      this.fakeBlocking = false;
      this.attackKeySuppressed = false;
      this.shouldUnblock = false;
      this.unblocking = false;
      this.reblocking = false;
      this.unblockTimer = new TimerUtil();
      this.reblockTimer = new TimerUtil();
      this.lastBlockPacketTime = System.currentTimeMillis();
      this.blinkInProgress = false;
      this.heldItemSpoofed = false;
      this.switchTargetTimer = new TimerUtil();
      this.switchIndex = 0;
      this.addSettings(
         new Setting[]{
            modeDescription,
            mode,
            sortDescription1,
            sortDescription2,
            sortMode,
            autoblockDescription1,
            autoblockDescription2,
            autoblockMode,
            fov,
            minAPS,
            maxAPS,
            autoblockRange,
            attackRange,
            swingRange,
            switchDelayMs,
            angleStep,
            moveFixDescription,
            moveFixMode,
            targetRenderDescription,
            targetRenderMode,
            colorDescription,
            colorMode,
            customColor,
            simulateClicks,
            autoblockRequireRightClick,
            requireMouseDown,
            requireSword,
            screenCheck,
            allowThroughWalls,
            attackPlayers,
            attackMobs,
            attackAnimals,
            attackBots,
            teamCheck
         }
      );
   }

   @Override
   public String getSuffix() {
      return mode.getCurrent();
   }

   @SubscribeEvent
   public void onSlow(SlowdownEvent event) {
      if (combatTick
         && autoblockMode.getCurrent().equalsIgnoreCase("FAKE")
         && this.mc.thePlayer.getHeldItem() != null
         && this.mc.thePlayer.getHeldItem().getItem() instanceof ItemSword) {
         event.setCanceled(true);
      }
   }

   @SubscribeEvent
   public void onRender3D(RenderWorldLastEvent event) {

      if (currentTarget != null
         && (TargetUtil.isInRange(currentTarget, autoblockRange.getRoundedValue()) || TargetUtil.isInRange(currentTarget, swingRange.getRoundedValue()))) {
         String var4 = colorMode.getCurrent();
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

         var4 = targetRenderMode.getCurrent();
         switch (var4) {
            case "BOX":
               RenderUtil.drawEntityESP(currentTarget, 2, 0.0, 0.0, color, false);
               break;
            case "BOX_WITH_DAMAGE":
               if (currentTarget.hurtTime > 5) {
                  color = -65536;
               }

               RenderUtil.drawEntityESP(currentTarget, 2, 0.0, 0.0, color, false);
         }
      }
   }

   @SubscribeEvent
   public void onUpdate(PlayerUpdateEvent event) {

      if ((BedNuker.allowKillAura.getState() || !BedNuker.isBreakingBlock) && !BedNuker.isRotating) {
         if (Myaven.moduleManager.getModule("scaffold").isEnabled()) {
            this.resetCombatState();
         } else {
            String target = moveFixMode.getCurrent();
            switch (target) {
               case "SILENT":
                  RotationManager.setMovementMode(RotationManager.MovementMode.SILENT);
                  break;
               case "STRICT":
                  RotationManager.setMovementMode(RotationManager.MovementMode.STRICT);
                  break;
               case "NONE":
                  RotationManager.setMovementMode(RotationManager.MovementMode.NONE);
            }

            if (!requireSword.getState()
               || (this.mc.thePlayer.getHeldItem() == null || this.mc.thePlayer.getHeldItem().getItem() instanceof ItemSword)
                  && this.mc.thePlayer.getHeldItem() != null) {
               if ((!requireMouseDown.getState() || !Mouse.isButtonDown(0)) && requireMouseDown.getState()) {
                  this.resetCombatState();
               } else {
                  EntityLivingBase targetEntity = this.acquireTarget();
                  currentTarget = targetEntity;
                  if (targetEntity == null) {
                     this.switchIndex = 0;
                     this.resetCombatState();
                     return;
                  }

                  if (!this.savedHotbarSlot) {
                     this.originalHotbarSlot = this.mc.thePlayer.inventory.currentItem;
                     this.savedHotbarSlot = true;
                  }

                  if (!TargetUtil.isInFov(targetEntity, fov.getRoundedValue())) {
                     this.resetCombatState();
                     return;
                  }

                  if (screenCheck.getState() && this.mc.currentScreen != null) {
                     this.resetCombatState();
                     return;
                  }

                  if (!allowThroughWalls.getState() && this.mc.objectMouseOver.typeOfHit == MovingObjectType.BLOCK) {
                     this.resetCombatState();
                     return;
                  }

                  if (!autoblockMode.getCurrent().equalsIgnoreCase("NONE")
                     && (autoblockRequireRightClick.getState() && Mouse.isButtonDown(1) || !autoblockRequireRightClick.getState())
                     && !TargetUtil.filterEntities(
                           TargetUtil.getLivingEntitiesInRange(autoblockRange.getRoundedValue()),
                           attackPlayers.getState(),
                           attackMobs.getState(),
                           attackAnimals.getState(),
                           teamCheck.getState(),
                           !attackBots.getState()
                        )
                        .isEmpty()
                     && this.mc.thePlayer.getHeldItem() != null
                     && this.mc.thePlayer.getHeldItem().getItem() instanceof ItemSword) {
                     if (autoblockMode.getCurrent().equalsIgnoreCase("FAKE")) {
                        ((AccessorKeybinding)this.mc.gameSettings.keyBindUseItem).setPressed(false);
                        ((AccessorEntityPlayer)this.mc.thePlayer).setItemInUseCount(1);
                        this.fakeBlocking = true;
                     } else if (autoblockMode.getCurrent().equalsIgnoreCase("LEGIT")) {
                        this.holdingUseKey = true;
                     } else {
                        isAutoBlockActive = true;
                        ((AccessorKeybinding)this.mc.gameSettings.keyBindUseItem).setPressed(true);
                        if (!autoblockMode.getCurrent().equalsIgnoreCase("BLINK") && !autoblockMode.getCurrent().equalsIgnoreCase("BLINK_CUSTOM_APS")) {
                           this.startBlocking(true);
                        }

                        this.mc.thePlayer.setItemInUse(this.mc.thePlayer.getHeldItem(), this.mc.thePlayer.getHeldItem().getMaxItemUseDuration());
                        this.holdingUseKey = true;
                     }
                  }

                  if (isAutoBlockActive && autoblockRequireRightClick.getState() && !Mouse.isButtonDown(1)) {
                     isAutoBlockActive = false;
                     if (isBlocking) {
                        this.spoofHeldItem();
                        this.restoreHeldItem();
                     }

                     if (this.packetCancellation) {
                        PacketUtil.flushPendingPackets();
                        PacketUtil.setCancelOutgoing(false);
                        this.packetCancellation = false;
                     }

                     this.blinkInProgress = false;
                     canBlink = true;
                     this.blockTimer.reset();
                  }

                  this.shouldAutoblock = this.mc.thePlayer.getHeldItem() != null
                     && this.mc.thePlayer.getHeldItem().getItem() instanceof ItemSword
                     && (autoblockRequireRightClick.getState() && Mouse.isButtonDown(1) || !autoblockRequireRightClick.getState())
                     && !autoblockMode.getCurrent().equalsIgnoreCase("NONE");
                  ((AccessorKeybinding)this.mc.gameSettings.keyBindAttack).setPressed(false);
                  this.attackKeySuppressed = true;
                  combatTick = true;
                  boolean flag = this.updateRotationAndRaytrace(targetEntity);
                  if (autoblockMode.getCurrent().equalsIgnoreCase("LEGIT") && this.shouldAutoblock) {
                     if (this.shouldUnblock) {
                        this.unblockTimer.reset();
                        this.shouldUnblock = false;
                        this.unblocking = true;
                     }

                     if (this.unblocking && this.unblockTimer.hasTimePassed(AutoClicker.sagUnblockDuration.getRoundedValue() * 50.0)) {
                        this.unblocking = false;
                        this.reblockTimer.reset();
                        this.reblocking = true;
                     } else if (this.unblocking) {
                        ((AccessorKeybinding)this.mc.gameSettings.keyBindUseItem).setPressed(false);
                     }

                     if (this.reblocking && this.reblockTimer.hasTimePassed(AutoClicker.sagBlockingTicks.getRoundedValue() * 50.0)) {
                        ((AccessorKeybinding)this.mc.gameSettings.keyBindUseItem).setPressed(false);
                        this.reblocking = false;
                     } else if (this.reblocking) {
                        if (this.reblockTimer.hasTimePassed(AutoClicker.sagBlockingTicks.getRoundedValue() * 50.0)) {
                           this.reblocking = false;
                        }

                        ((AccessorKeybinding)this.mc.gameSettings.keyBindUseItem).setPressed(true);
                     }
                  }

                  if ((autoblockMode.getCurrent().equalsIgnoreCase("BLINK") || autoblockMode.getCurrent().equalsIgnoreCase("BLINK_CUSTOM_APS"))
                     && this.shouldAutoblock) {
                     isAutoBlockActive = true;
                     PacketUtil.setCancelOutgoing(true);
                     this.packetCancellation = true;
                     if (!canBlink
                        && this.blinkInProgress
                        && System.currentTimeMillis() - this.lastBlockPacketTime >= 9L
                        && this.blockTimer.hasTimePassed(10L, true)) {
                        this.spoofHeldItem();
                        if (!this.projectileSwitched) {
                           this.restoreHeldItem();
                        }

                        canBlink = true;
                     }

                     if (!canBlink) {
                        return;
                     }

                     if (!this.blinkTimer.hasTimePassed(50L, true)) {
                        return;
                     }

                     String var5 = autoblockMode.getCurrent();
                     label394:
                     switch (var5) {
                        case "BLINK":
                           if (this.blinkStep >= 3) {
                              this.blinkStep = 0;
                           }

                           this.blinkStep++;
                           switch (this.blinkStep) {
                              case 1:
                                 if (this.projectileSwitched) {
                                    this.restoreHeldItem();
                                 }
                                 break label394;
                              case 2:
                                 this.blinkInProgress = false;
                                 this.blinkStep = 0;
                              default:
                                 break label394;
                           }
                        case "BLINK_CUSTOM_APS":
                           if (this.blinkStep >= 2) {
                              this.blinkStep = 0;
                           }

                           this.blinkStep++;
                           switch (this.blinkStep) {
                              case 1:
                                 if (this.projectileSwitched) {
                                    this.restoreHeldItem();
                                 }

                                 this.blinkInProgress = false;
                                 this.blinkStep = 0;
                           }
                     }
                  }

                  if (!flag) {
                     return;
                  }

                  if (!isAutoBlockActive
                     && System.currentTimeMillis() - lastStopUseTime >= 10L
                     && !this.mc.thePlayer.isUsingItem()
                     && !this.mc.gameSettings.keyBindUseItem.isKeyDown()) {
                     if (this.queuedAttack && this.queuedTarget != null) {
                        this.queuedAttack = false;
                        this.attackTarget(this.queuedTarget, false);
                        this.queuedTarget = null;
                     } else if (this.queuedSwing && this.queuedTarget != null) {
                        this.queuedSwing = false;
                        this.swing();
                        this.queuedTarget = null;
                     }
                  }

                  if (this.attackTimer.hasTimePassed(this.attackDelay, true)) {
                     this.attackDelay = ClientUtil.getDelayInMillis(minAPS.getRoundedValue(), maxAPS.getRoundedValue());
                     this.attackTickReady = true;
                     if (TargetUtil.isInRange(targetEntity, attackRange.getRoundedValue())) {
                        if (!this.mc.thePlayer.isUsingItem()
                           && !this.mc.gameSettings.keyBindUseItem.isKeyDown()
                           && (
                              !this.shouldAutoblock
                                 || autoblockMode.getCurrent().equalsIgnoreCase("LEGIT")
                                 || autoblockMode.getCurrent().equalsIgnoreCase("FAKE")
                           )) {
                           if (System.currentTimeMillis() - lastStopUseTime >= 10L) {
                              this.attackTarget(targetEntity, false);
                           } else {
                              this.queuedAttack = true;
                              this.queuedTarget = targetEntity;
                           }
                        } else if (this.shouldAutoblock) {
                           String var9 = autoblockMode.getCurrent();
                           switch (var9) {
                              case "BLINK":
                              case "BLINK_CUSTOM_APS":
                                 if (!this.blinkInProgress && canBlink) {
                                    isAutoBlockActive = true;
                                    this.attackTarget(targetEntity, false);
                                    this.startBlocking(true);
                                    PacketUtil.flushPendingPackets();
                                    PacketUtil.setCancelOutgoing(true);
                                    this.packetCancellation = true;
                                    this.blinkStep = 0;
                                    this.blinkTimer.reset();
                                    this.blockTimer.reset();
                                    canBlink = false;
                                    this.blinkInProgress = true;
                                 }
                                 break;
                              case "INTERACT":
                                 this.spoofHeldItem();
                                 this.restoreHeldItem();
                                 this.attackTarget(targetEntity, false);
                                 this.startBlocking(true);
                           }
                        }
                     } else if (isAutoBlockActive && !this.blinkInProgress && canBlink) {
                        this.swing();
                        if (!TargetUtil.getLivingEntitiesInRange(autoblockRange.getRoundedValue()).isEmpty()) {
                           this.startBlocking(true);
                        }

                        PacketUtil.flushPendingPackets();
                        PacketUtil.setCancelOutgoing(true);
                        this.packetCancellation = true;
                        this.blinkStep = 0;
                        this.blinkTimer.reset();
                        this.blockTimer.reset();
                        canBlink = false;
                        this.blinkInProgress = true;
                     } else if (!this.mc.thePlayer.isUsingItem() && !this.mc.gameSettings.keyBindUseItem.isKeyDown()) {
                        if (System.currentTimeMillis() - lastStopUseTime >= 10L) {
                           this.swing();
                        } else {
                           this.queuedSwing = false;
                           this.queuedTarget = targetEntity;
                        }
                     }
                  } else {
                     this.attackTickReady = false;
                  }
               }
            } else {
               this.resetCombatState();
            }
         }
      } else {
         this.resetCombatState();
      }
   }

   @SubscribeEvent
   public void onStop(StopUseEvent event) {
      isBlocking = false;
      lastStopUseTime = System.currentTimeMillis();
   }

   @SubscribeEvent
   public void onSend(PacketSendEvent event) {
      if (autoblockRequireRightClick.getState()
         && !isAutoBlockActive
         && Mouse.isButtonDown(1)
         && combatTick
         && (
            event.getPacket() instanceof C08PacketPlayerBlockPlacement
               || event.getPacket() instanceof C02PacketUseEntity
                  && (
                     ((C02PacketUseEntity)event.getPacket()).getAction().equals(Action.INTERACT)
                        || ((C02PacketUseEntity)event.getPacket()).getAction().equals(Action.INTERACT_AT)
                  )
         )) {
         event.setCanceled(true);
      }
   }

   @Override
   public void onDisable() {
      ;
      this.switchIndex = 0;
      this.resetCombatState();
      if (this.holdingUseKey) {
         if (!Mouse.isButtonDown(1)) {
            ((AccessorKeybinding)this.mc.gameSettings.keyBindUseItem).setPressed(false);
         }

         this.holdingUseKey = false;
         isAutoBlockActive = false;
      }
   }

   private void resetCombatState() {
      ;
      this.projectileSwitched = false;
      this.shouldUnblock = false;
      this.reblocking = false;
      this.unblocking = false;
      this.blinkInProgress = false;
      canBlink = true;
      this.blockTimer.reset();
      this.queuedTarget = null;
      this.queuedAttack = false;
      this.queuedSwing = false;
      combatTick = false;
      isAutoBlockActive = false;
      this.savedHotbarSlot = false;
      currentTarget = null;
      if (this.attackKeySuppressed) {
         ((AccessorKeybinding)this.mc.gameSettings.keyBindAttack).setPressed(Mouse.isButtonDown(0));
         this.attackKeySuppressed = false;
      }

      if (this.rotationApplied) {
         RotationManager.syncRotationWithPlayer();
         this.rotationApplied = false;
      }

      if (this.heldItemSpoofed) {
         this.restoreHeldItem();
         this.mc.thePlayer.inventory.currentItem = this.originalHotbarSlot;
         this.heldItemSpoofed = false;
      }

      if (this.packetCancellation) {
         PacketUtil.flushPendingPackets();
         PacketUtil.setCancelOutgoing(false);
         this.packetCancellation = false;
      }

      if (isBlocking) {
         this.spoofHeldItem();
         this.restoreHeldItem();
         isBlocking = false;
      }

      if (this.holdingUseKey) {
         if (!Mouse.isButtonDown(1)) {
            ((AccessorKeybinding)this.mc.gameSettings.keyBindUseItem).setPressed(false);
         }

         this.holdingUseKey = false;
      }

      if (this.fakeBlocking) {
         ((AccessorEntityPlayer)this.mc.thePlayer).setItemInUseCount(this.mc.gameSettings.keyBindUseItem.isKeyDown() ? 1 : 0);
         this.fakeBlocking = false;
      }
   }

   private boolean updateRotationAndRaytrace(EntityLivingBase target) {
      if (!this.rotationTimer.hasTimePassed(1L, true)) {
         return target.posX == this.mc.thePlayer.posX && target.posZ == this.mc.thePlayer.posZ && RaytraceUtil.getEntitiesInRay(3.0).contains(target);
      } else {
         if ((int)target.posX != (int)this.mc.thePlayer.posX
            || (int)target.posZ != (int)this.mc.thePlayer.posZ
            || !RaytraceUtil.getEntitiesInRay(3.0).contains(target)) {
            RotationManager.stepYawTowards(RotationUtil.getRotationToEntity(target)[0], (float)angleStep.getRoundedValue());
         }

         if ((int)target.posX != (int)this.mc.thePlayer.posX
            || (int)target.posZ != (int)this.mc.thePlayer.posZ
            || !RaytraceUtil.getEntitiesInRay(3.0).contains(target)) {
            RotationManager.setPitch(RotationUtil.getRotationToEntity(target)[1]);
         }

         this.rotationApplied = true;
         return RaytraceUtil.getEntitiesInRay(swingRange.getRoundedValue()).contains(target);
      }
   }

   private void swing() {
      if (!isBlocking) {
         if (!this.shouldUnblock && !this.unblocking && !this.reblocking) {
            this.shouldUnblock = true;
         }

         this.mc.thePlayer.swingItem();
      }
   }

   private void spoofHeldItem() {

      if (AutoProjectiles.isThrowing && !AutoProjectiles.isEnabled) {
         AutoProjectiles.hasThrown = true;
         boolean flag = AutoProjectiles.mode.getCurrent().equalsIgnoreCase("PACKET")
            && (
               AutoProjectiles.onlyUsePacketWhileAutoBlocking.getState() && isAutoBlockActive
                  || !AutoProjectiles.onlyUsePacketWhileAutoBlocking.getState() && !isAutoBlockActive
            );
         AutoProjectiles.switchToProjectile(flag);

         for (int i = 0; (double)i < AutoProjectiles.throwAmounts.getRoundedValue(); i++) {
            AutoProjectiles.performThrow(flag);
         }

         AutoProjectiles.isThrowingSequence = false;
         AutoProjectiles.lastThrowTime = System.currentTimeMillis();
         AutoProjectiles.currentItemIndex = this.originalHotbarSlot;
         this.projectileSwitched = true;
      } else {
         int slot = this.spoofSlotIndex + 1 >= 9
            ? (this.originalHotbarSlot == 0 ? this.originalHotbarSlot + 1 : 0)
            : (this.spoofSlotIndex + 1 == this.originalHotbarSlot ? this.spoofSlotIndex + 2 : this.spoofSlotIndex + 1);
         this.spoofSlotIndex = slot;
         PacketUtil.sendPacket(new C09PacketHeldItemChange(slot));
         isBlocking = false;
         this.heldItemSpoofed = true;
         lastStopUseTime = System.currentTimeMillis();
         this.projectileSwitched = false;
      }
   }

   private void restoreHeldItem() {
      if (this.heldItemSpoofed) {
         PacketUtil.sendPacket(new C09PacketHeldItemChange(this.originalHotbarSlot));
         this.projectileSwitched = false;
         this.heldItemSpoofed = false;
      }
   }

   private void startBlocking(boolean interact) {
      if (!isBlocking) {
         if (interact) {
            PacketUtil.sendPacket(new C02PacketUseEntity(currentTarget, Action.INTERACT));
         }

         PacketUtil.sendPacket(new C08PacketPlayerBlockPlacement(this.mc.thePlayer.getHeldItem()));
         this.lastBlockPacketTime = System.currentTimeMillis();
         this.blockTimer.reset();
         isBlocking = true;
      }
   }

   private void attackTarget(EntityLivingBase target, boolean interactAt) {

      if (simulateClicks.getState()) {
         KeyBinding.setKeyBindState(this.mc.gameSettings.keyBindAttack.getKeyCode(), true);
         KeyBinding.onTick(this.mc.gameSettings.keyBindAttack.getKeyCode());
         KeyBinding.setKeyBindState(this.mc.gameSettings.keyBindAttack.getKeyCode(), false);
         if (!this.shouldUnblock && !this.unblocking && !this.reblocking) {
            this.shouldUnblock = true;
         }
      } else {
         if (isBlocking) {
            return;
         }

         if (!this.shouldUnblock && !this.unblocking && !this.reblocking) {
            this.shouldUnblock = true;
         }

         this.swing();
         this.mc.playerController.attackEntity(this.mc.thePlayer, target);
         if (interactAt) {
            Vec3 hitVec = RotationUtil.getEntityHitVec(target);
            hitVec = new Vec3(hitVec.xCoord - target.posX, hitVec.yCoord - target.posY, hitVec.zCoord - target.posZ);
            PacketUtil.sendPacket(new C02PacketUseEntity(target, hitVec));
            PacketUtil.sendPacket(new C02PacketUseEntity(target, Action.INTERACT));
         }
      }
   }

   private void interactWithTarget(EntityLivingBase target, boolean withHitVec) {

      if (withHitVec) {
         PacketUtil.sendPacket(new C02PacketUseEntity(target, Action.INTERACT));
      } else {
         Vec3 hitVec = ClientUtil.getPlayerEyesPosition();
         PacketUtil.sendPacket(new C02PacketUseEntity(target, new Vec3(hitVec.xCoord - target.posX, hitVec.yCoord - target.posY, hitVec.zCoord - target.posZ)));
         PacketUtil.sendPacket(new C02PacketUseEntity(target, Action.INTERACT));
      }
   }

   public EntityLivingBase acquireTarget() {

      List<EntityLivingBase> entitiesWithinRange = TargetUtil.filterEntities(
         TargetUtil.getLivingEntitiesInRange(attackRange.getRoundedValue()),
         attackPlayers.getState(),
         attackMobs.getState(),
         attackAnimals.getState(),
         teamCheck.getState(),
         !attackBots.getState()
      );
      if (entitiesWithinRange.isEmpty()) {
         entitiesWithinRange = TargetUtil.getLivingEntitiesInRange(swingRange.getRoundedValue());
         if (entitiesWithinRange.isEmpty()) {
            return null;
         }

         entitiesWithinRange = TargetUtil.filterEntities(
            entitiesWithinRange, attackPlayers.getState(), attackMobs.getState(), attackAnimals.getState(), teamCheck.getState(), !attackBots.getState()
         );
      }

      if (entitiesWithinRange.isEmpty()) {
         return null;
      } else {
         String var3 = sortMode.getCurrent();
         switch (var3) {
            case "HEALTH":
               entitiesWithinRange.sort(Comparator.comparingDouble(EntityLivingBase::getHealth));
               break;
            case "DISTANCE":
               entitiesWithinRange.sort(Comparator.comparingDouble(this.mc.thePlayer::getDistanceToEntity));
               break;
            case "VIEW":
               entitiesWithinRange.sort(Comparator.comparingDouble(RotationUtil::getYawDifference));
               break;
            case "HURT_TIME":
               entitiesWithinRange.sort(Comparator.comparingInt(entity -> entity.hurtTime));
               break;
            case "ARMOR":
               entitiesWithinRange.sort(Comparator.comparingInt(EntityLivingBase::getTotalArmorValue));
         }

         var3 = mode.getCurrent();
         switch (var3) {
            case "SINGLE":
               return entitiesWithinRange.get(0);
            case "SWITCH":
               if (this.switchTargetTimer.hasTimePassed((long)switchDelayMs.getRoundedValue(), true) && this.attackTickReady) {
                  this.switchIndex++;
               }

               if (this.switchIndex >= entitiesWithinRange.size()) {
                  this.switchIndex = 0;
               }

               return entitiesWithinRange.get(this.switchIndex);
            default:
               return entitiesWithinRange.get(0);
         }
      }
   }

   // $VF: Irreducible bytecode was duplicated to produce valid code
   static {
      modeDescription = new DescriptionSetting("SINGLE, SWITCH");
      mode = new ModeSetting("Mode", "SINGLE", "SWITCH");
      sortDescription1 = new DescriptionSetting("DISTANCE, HEALTH, VIEW,");
      sortDescription2 = new DescriptionSetting("HURT_TIME, ARMOR");
      sortMode = new ModeSetting("Sort", "DISTANCE", "HEALTH", "VIEW", "HURT_TIME", "ARMOR");
      autoblockDescription1 = new DescriptionSetting("NONE, BLINK, BLINK_CUSTOM_APS,");
      autoblockDescription2 = new DescriptionSetting("LEGIT, INTERACT, FAKE");
      autoblockMode = new ModeSetting(
              "Autoblock", "NONE", "BLINK", "BLINK_CUSTOM_APS", "LEGIT", "INTERACT", "FAKE"
      );
      fov = new SliderSetting("FOV", 360.0, 1.0, 360.0, 1.0);
      minAPS = new SliderSetting("Min-APS", 13.0, 1.0, 20.0, 1.0);
      maxAPS = new SliderSetting("Max-APS", 15.0, 1.0, 20.0, 1.0);
      autoblockRange = new SliderSetting("Autoblock-range", 6.0, 0.0, 6.0, 0.1);
      attackRange = new SliderSetting("Attack-range", 3.0, 0.0, 6.0, 0.1);
      swingRange = new SliderSetting("Swing-range", 6.0, 0.0, 6.0, 0.1);
      switchDelayMs = new SliderSetting("Switch-delay", 100.0, 0.0, 1000.0, 1.0);
      angleStep = new SliderSetting("Angle-step", 60.0, 0.0, 180.0, 1.0);
      moveFixDescription = new DescriptionSetting("SILENT, STRICT, NONE");
      moveFixMode = new ModeSetting("Move-fix", "SILENT", "STRICT", "NONE");
      targetRenderDescription = new DescriptionSetting("BOX, BOX_WITH_DAMAGE, NONE");
      targetRenderMode = new ModeSetting("Show-target", "BOX", "BOX_WITH_DAMAGE", "NONE");
      colorDescription = new DescriptionSetting("THEME, THEME_CUSTOM, CUSTOM");
      colorMode = new ModeSetting("Show-target-color", "THEME", "THEME_CUSTOM", "CUSTOM");
      customColor = new ColorSetting("Custom-color", "FFFFFF");
      simulateClicks = new BooleanSetting("Legit", false);
      autoblockRequireRightClick = new BooleanSetting("Autoblock-require-right-click", false);
      requireMouseDown = new BooleanSetting("Require-click", false);
      requireSword = new BooleanSetting("Require-Sword", true);
      screenCheck = new BooleanSetting("Screen-check", true);
      allowThroughWalls = new BooleanSetting("Through-wall", true);
      attackPlayers = new BooleanSetting("Attack-player", true);
      attackMobs = new BooleanSetting("Attack-mobs", false);
      attackAnimals = new BooleanSetting("Attack-animals", false);
      attackBots = new BooleanSetting("Attack-bots", false);
      teamCheck = new BooleanSetting("Team-check", true);
      isBlocking = false;
      canBlink = true;
      isAutoBlockActive = false;
      combatTick = false;
      lastStopUseTime = System.currentTimeMillis();
   }
}
