package Myaven.mixins.entity;

import Myaven.Myaven;
import Myaven.events.LivingUpdateEvent;
import Myaven.events.PlayerUpdateEvent;
import Myaven.events.PostMotionEvent;
import Myaven.events.PostPlayerUpdateEvent;
import Myaven.events.PreMotionEvent;
import Myaven.events.SlowdownEvent;
import Myaven.util.RotationUtil;
import com.mojang.authlib.GameProfile;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.network.play.client.C01PacketChatMessage;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C0BPacketEntityAction;
import net.minecraft.network.play.client.C03PacketPlayer.C04PacketPlayerPosition;
import net.minecraft.network.play.client.C03PacketPlayer.C05PacketPlayerLook;
import net.minecraft.network.play.client.C03PacketPlayer.C06PacketPlayerPosLook;
import net.minecraft.network.play.client.C0BPacketEntityAction.Action;
import net.minecraft.potion.Potion;
import net.minecraft.util.MovementInput;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({EntityPlayerSP.class})
public abstract class MixinEntityPlayerSP extends AbstractClientPlayer {
   @Shadow
   @Final
   public NetHandlerPlayClient sendQueue;
   @Shadow
   private double lastReportedPosX;
   @Shadow
   private double lastReportedPosY;
   @Shadow
   private double lastReportedPosZ;
   @Shadow
   private float lastReportedYaw;
   @Shadow
   private float lastReportedPitch;
   @Shadow
   private boolean serverSneakState;
   @Shadow
   private boolean serverSprintState;
   @Shadow
   private int positionUpdateTicks;
   @Shadow
   public MovementInput movementInput;
   @Shadow
   protected Minecraft mc;
   @Shadow
   protected int sprintToggleTimer;
   @Shadow
   public int sprintingTicksLeft;
   @Shadow
   private int horseJumpPowerCounter;
   @Shadow
   private float horseJumpPower;
   @Shadow
   public float timeInPortal;
   @Shadow
   public float prevTimeInPortal;

   @Shadow
   protected abstract boolean isCurrentViewEntity();

   @Shadow
   public abstract boolean isRidingHorse();

   @Shadow
   protected abstract void sendHorseJump();

   public MixinEntityPlayerSP(World p_i45074_1_, GameProfile p_i45074_2_) {
      super(p_i45074_1_, p_i45074_2_);
   }

   @Inject(
      method = {"onUpdate"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/client/entity/AbstractClientPlayer;onUpdate()V"
      )},
      cancellable = true
   )
   public void v(CallbackInfo ci) {
      RotationUtil.L = RotationUtil.N;
      RotationUtil.C = RotationUtil.K;
      PlayerUpdateEvent event = new PlayerUpdateEvent();
      MinecraftForge.EVENT_BUS.post(event);
      if (event.isCanceled()) {
         ci.cancel();
      }
   }

   @Inject(
      method = {"onUpdate"},
      at = {@At("RETURN")}
   )
   public void d(CallbackInfo ci) {
      PostPlayerUpdateEvent event = new PostPlayerUpdateEvent();
      MinecraftForge.EVENT_BUS.post(event);
   }

   @Inject(
      method = {"sendChatMessage"},
      at = {@At("HEAD")},
      cancellable = true
   )
   public void a(String message, CallbackInfo ci) {
      if (Myaven.moduleManager.getModule("commandLine").isEnabled()) {
         if (message.startsWith(".")) {
            Myaven.commandManager.onMessage(message);
         } else {
            this.sendQueue.addToSendQueue(new C01PacketChatMessage(message));
         }

         ci.cancel();
      }
   }

   @Inject(
      method = {"onUpdateWalkingPlayer"},
      at = {@At("HEAD")},
      cancellable = true
   )
   public void X(CallbackInfo ci) {
      PreMotionEvent pre = new PreMotionEvent(
         this.posX, this.posY, this.posZ, this.rotationYaw, this.rotationPitch, this.onGround, this.isSprinting(), this.isSneaking(), false
      );
      MinecraftForge.EVENT_BUS.post(pre);
      if (!pre.isCanceled()) {
         boolean flag = this.isSprinting();
         if (flag != this.serverSprintState && !pre.isCancelPacket()) {
            if (flag) {
               this.sendQueue.addToSendQueue(new C0BPacketEntityAction(this, Action.START_SPRINTING));
            } else {
               this.sendQueue.addToSendQueue(new C0BPacketEntityAction(this, Action.STOP_SPRINTING));
            }

            this.serverSprintState = flag;
         }

         boolean flag1 = this.isSneaking();
         if (flag1 != this.serverSneakState && !pre.isCancelPacket()) {
            if (flag1) {
               this.sendQueue.addToSendQueue(new C0BPacketEntityAction(this, Action.START_SNEAKING));
            } else {
               this.sendQueue.addToSendQueue(new C0BPacketEntityAction(this, Action.STOP_SNEAKING));
            }

            this.serverSneakState = flag1;
         }

         if (this.isCurrentViewEntity()) {
            this.rotationYawHead = pre.getYaw();
            RotationUtil.N = pre.getYaw();
            RotationUtil.K = pre.getPitch();
            double d0 = pre.getPosX() - this.lastReportedPosX;
            double d1 = pre.getPosY() - this.lastReportedPosY;
            double d2 = pre.getPosZ() - this.lastReportedPosZ;
            double d3 = (double)(pre.getYaw() - this.lastReportedYaw);
            double d4 = (double)(pre.getPitch() - this.lastReportedPitch);
            boolean flag2 = d0 * d0 + d1 * d1 + d2 * d2 > 9.0E-4 || this.positionUpdateTicks >= 20;
            boolean flag3 = d3 != 0.0 || d4 != 0.0;
            if (this.ridingEntity != null && !pre.isCancelPacket()) {
               this.sendQueue
                  .addToSendQueue(new C06PacketPlayerPosLook(this.motionX, -999.0, this.motionZ, pre.getYaw(), pre.getPitch(), pre.isOnGround()));
               flag2 = false;
            } else if (!pre.isCancelPacket()) {
               if (flag2 && flag3) {
                  this.sendQueue
                     .addToSendQueue(
                        new C06PacketPlayerPosLook(this.posX, this.getEntityBoundingBox().minY, this.posZ, pre.getYaw(), pre.getPitch(), pre.isOnGround())
                     );
               } else if (flag2) {
                  this.sendQueue.addToSendQueue(new C04PacketPlayerPosition(this.posX, this.getEntityBoundingBox().minY, this.posZ, pre.isOnGround()));
               } else if (flag3) {
                  this.sendQueue.addToSendQueue(new C05PacketPlayerLook(pre.getYaw(), pre.getPitch(), pre.isOnGround()));
               } else {
                  this.sendQueue.addToSendQueue(new C03PacketPlayer(pre.isOnGround()));
               }
            }

            this.positionUpdateTicks++;
            if (flag2) {
               this.lastReportedPosX = pre.getPosX();
               this.lastReportedPosY = pre.getPosY();
               this.lastReportedPosZ = pre.getPosZ();
               this.positionUpdateTicks = 0;
            }

            if (flag3) {
               this.lastReportedYaw = pre.getYaw();
               this.lastReportedPitch = pre.getPitch();
            }
         }

         MinecraftForge.EVENT_BUS.post(new PostMotionEvent());
         ci.cancel();
      }
   }

   @Inject(
      method = {"onLivingUpdate"},
      at = {@At("HEAD")},
      cancellable = true
   )
   public void o(CallbackInfo ci) {
      LivingUpdateEvent event = new LivingUpdateEvent();
      MinecraftForge.EVENT_BUS.post(event);
      if (!event.isCanceled()) {
         if (this.sprintingTicksLeft > 0) {
            this.sprintingTicksLeft--;
            if (this.sprintingTicksLeft == 0) {
               this.setSprinting(false);
            }
         }

         if (this.sprintToggleTimer > 0) {
            this.sprintToggleTimer--;
         }

         this.prevTimeInPortal = this.timeInPortal;
         if (this.inPortal) {
            if (this.mc.currentScreen != null && !this.mc.currentScreen.doesGuiPauseGame()) {
               this.mc.displayGuiScreen((GuiScreen)null);
            }

            if (this.timeInPortal == 0.0F) {
               this.mc
                  .getSoundHandler()
                  .playSound(PositionedSoundRecord.create(new ResourceLocation("portal.trigger"), this.rand.nextFloat() * 0.4F + 0.8F));
            }

            this.timeInPortal += 0.0125F;
            if (this.timeInPortal >= 1.0F) {
               this.timeInPortal = 1.0F;
            }

            this.inPortal = false;
         } else if (this.isPotionActive(Potion.confusion) && this.getActivePotionEffect(Potion.confusion).getDuration() > 60) {
            this.timeInPortal += 0.006666667F;
            if (this.timeInPortal > 1.0F) {
               this.timeInPortal = 1.0F;
            }
         } else {
            if (this.timeInPortal > 0.0F) {
               this.timeInPortal -= 0.05F;
            }

            if (this.timeInPortal < 0.0F) {
               this.timeInPortal = 0.0F;
            }
         }

         if (this.timeUntilPortal > 0) {
            this.timeUntilPortal--;
         }

         boolean flag = this.movementInput.jump;
         boolean flag1 = this.movementInput.sneak;
         float f = 0.8F;
         boolean flag2 = this.movementInput.moveForward >= 0.8F;
         this.movementInput.updatePlayerMoveState();
         Myaven.movementHook.updatePlayerMoveState();
         if (this.isUsingItem() && !this.isRiding()) {
            SlowdownEvent slow = new SlowdownEvent(0.2F);
            MinecraftForge.EVENT_BUS.post(slow);
            if (!slow.isCanceled()) {
               this.movementInput.moveStrafe = this.movementInput.moveStrafe * slow.getSpeed();
               this.movementInput.moveForward = this.movementInput.moveForward * slow.getSpeed();
            }

            this.sprintToggleTimer = 0;
         }

         this.pushOutOfBlocks(this.posX - (double)this.width * 0.35, this.getEntityBoundingBox().minY + 0.5, this.posZ + (double)this.width * 0.35);
         this.pushOutOfBlocks(this.posX - (double)this.width * 0.35, this.getEntityBoundingBox().minY + 0.5, this.posZ - (double)this.width * 0.35);
         this.pushOutOfBlocks(this.posX + (double)this.width * 0.35, this.getEntityBoundingBox().minY + 0.5, this.posZ - (double)this.width * 0.35);
         this.pushOutOfBlocks(this.posX + (double)this.width * 0.35, this.getEntityBoundingBox().minY + 0.5, this.posZ + (double)this.width * 0.35);
         boolean flag3 = (float)this.getFoodStats().getFoodLevel() > 6.0F || this.capabilities.allowFlying;
         if (this.onGround
            && !flag1
            && !flag2
            && this.movementInput.moveForward >= 0.8F
            && !this.isSprinting()
            && flag3
            && !this.isUsingItem()
            && !this.isPotionActive(Potion.blindness)) {
            if (this.sprintToggleTimer <= 0 && !this.mc.gameSettings.keyBindSprint.isKeyDown()) {
               this.sprintToggleTimer = 7;
            } else {
               this.setSprinting(true);
            }
         }

         if (!this.isSprinting()
            && this.movementInput.moveForward >= 0.8F
            && flag3
            && !this.isUsingItem()
            && !this.isPotionActive(Potion.blindness)
            && this.mc.gameSettings.keyBindSprint.isKeyDown()) {
            this.setSprinting(true);
         }

         if (this.isSprinting() && (this.movementInput.moveForward < 0.8F || this.isCollidedHorizontally || !flag3)) {
            this.setSprinting(false);
         }

         if (this.capabilities.allowFlying) {
            if (this.mc.playerController.isSpectatorMode()) {
               if (!this.capabilities.isFlying) {
                  this.capabilities.isFlying = true;
                  this.sendPlayerAbilities();
               }
            } else if (!flag && this.movementInput.jump) {
               if (this.flyToggleTimer == 0) {
                  this.flyToggleTimer = 7;
               } else {
                  this.capabilities.isFlying = !this.capabilities.isFlying;
                  this.sendPlayerAbilities();
                  this.flyToggleTimer = 0;
               }
            }
         }

         if (this.capabilities.isFlying && this.isCurrentViewEntity()) {
            if (this.movementInput.sneak) {
               this.motionY = this.motionY - (double)(this.capabilities.getFlySpeed() * 3.0F);
            }

            if (this.movementInput.jump) {
               this.motionY = this.motionY + (double)(this.capabilities.getFlySpeed() * 3.0F);
            }
         }

         if (this.isRidingHorse()) {
            if (this.horseJumpPowerCounter < 0) {
               this.horseJumpPowerCounter++;
               if (this.horseJumpPowerCounter == 0) {
                  this.horseJumpPower = 0.0F;
               }
            }

            if (flag && !this.movementInput.jump) {
               this.horseJumpPowerCounter = -10;
               this.sendHorseJump();
            } else if (!flag && this.movementInput.jump) {
               this.horseJumpPowerCounter = 0;
               this.horseJumpPower = 0.0F;
            } else if (flag) {
               this.horseJumpPowerCounter++;
               if (this.horseJumpPowerCounter < 10) {
                  this.horseJumpPower = (float)this.horseJumpPowerCounter * 0.1F;
               } else {
                  this.horseJumpPower = 0.8F + 2.0F / (float)(this.horseJumpPowerCounter - 9) * 0.1F;
               }
            }
         } else {
            this.horseJumpPower = 0.0F;
         }

         super.onLivingUpdate();
         if (this.onGround && this.capabilities.isFlying && !this.mc.playerController.isSpectatorMode()) {
            this.capabilities.isFlying = false;
            this.sendPlayerAbilities();
         }

         ci.cancel();
      }
   }
}
