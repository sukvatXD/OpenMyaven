package Myaven.module.modules.player;

import Myaven.events.BlockBBEvent;
import Myaven.events.PacketSendEvent;
import Myaven.events.PlayerUpdateEvent;
import Myaven.events.PreMotionEvent;
import Myaven.management.RotationManager;
import Myaven.module.Category;
import Myaven.module.Module;
import Myaven.util.PlayerUtil;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class FreeCam extends Module {
   private EntityOtherPlayerMP fakePlayer;
   public static double savedPosX;
   public static double savedPosY;
   public static double savedPosZ;
   private float savedYaw;
   private float savedPitch;
   private boolean savedOnGround;
   private boolean freecamActive = false;
   private static String[] a;
   private static String[] b;

   public FreeCam() {
      super("FreeCam", false, Category.Player, true, "Free your soul out of your body");
   }

   @SubscribeEvent
   public void onMotion(PreMotionEvent e) {
      this.mc.thePlayer.noClip = true;
      this.mc.thePlayer.motionY = this.mc.gameSettings.keyBindJump.isKeyDown() ? 1.0 : (this.mc.gameSettings.keyBindSneak.isKeyDown() ? -1.0 : 0.0);
      if (PlayerUtil.isMoving()) {
         PlayerUtil.setSpeed(5.0);
      } else {
         PlayerUtil.stopMotionXZ();
      }

      e.setGround(this.savedOnGround);
   }

   @SubscribeEvent
   public void onPacket(PacketSendEvent e) {
      e.setCanceled(true);
   }

   @SubscribeEvent
   public void onUpdate(PlayerUpdateEvent e) {
      if (this.mc.gameSettings.keyBindSneak.isKeyDown()) {
         this.mc.thePlayer.setSneaking(false);
      }
   }

   @SubscribeEvent
   public void onBB(BlockBBEvent event) {
      event.setCanceled(true);
   }

   @Override
   public void onEnable() {
      if (!this.freecamActive) {
         this.freecamActive = true;
         this.fakePlayer = new EntityOtherPlayerMP(this.mc.theWorld, this.mc.thePlayer.getGameProfile());
         this.fakePlayer
            .setPositionAndRotation(
               this.mc.thePlayer.posX, this.mc.thePlayer.posY, this.mc.thePlayer.posZ, RotationManager.getCurrentYaw(), RotationManager.getCurrentPitch()
            );
         this.fakePlayer.rotationYawHead = RotationManager.getCurrentYaw();
         this.fakePlayer.setSprinting(this.mc.thePlayer.isSprinting());
         this.fakePlayer.setInvisible(this.mc.thePlayer.isInvisible());
         this.fakePlayer.setSneaking(this.mc.thePlayer.isSneaking());
         this.mc.theWorld.addEntityToWorld(this.fakePlayer.getEntityId(), this.fakePlayer);
         this.savedPitch = RotationManager.getCurrentPitch();
         this.savedYaw = RotationManager.getCurrentYaw();
         savedPosX = this.mc.thePlayer.posX;
         savedPosY = this.mc.thePlayer.posY;
         savedPosZ = this.mc.thePlayer.posZ;
         this.savedOnGround = this.mc.thePlayer.onGround;
      }
   }

   @Override
   public void onDisable() {
      if (this.freecamActive) {
         this.freecamActive = false;
         if (this.fakePlayer != null) {
            this.mc.theWorld.removeEntityFromWorld(this.fakePlayer.getEntityId());
            this.mc.thePlayer.setPositionAndRotation(savedPosX, savedPosY, savedPosZ, this.savedYaw, this.savedPitch);
         }

         this.mc.thePlayer.noClip = false;
         this.mc.thePlayer.motionY = 0.0;
         PlayerUtil.setSpeed(0.0);
         this.mc.thePlayer.onGround = this.savedOnGround;
      }
   }
}
