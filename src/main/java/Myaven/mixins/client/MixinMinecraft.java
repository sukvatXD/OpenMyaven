package Myaven.mixins.client;

import Myaven.Myaven;
import Myaven.events.RightClickEvent;
import Myaven.mixins.accessor.AccessorLeftClickCounter;
import Myaven.module.modules.combat.KillAura;
import Myaven.util.RaytraceUtil;
import Myaven.util.ReflectUtil;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.particle.EffectRenderer;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.MovingObjectPosition.MovingObjectType;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.Action;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({Minecraft.class})
public class MixinMinecraft {
   @Final
   @Shadow
   private static Logger logger;
   @Shadow
   private int leftClickCounter;
   @Shadow
   public int rightClickDelayTimer;
   @Shadow
   public EntityPlayerSP thePlayer;
   @Shadow
   public MovingObjectPosition objectMouseOver;
   @Shadow
   public WorldClient theWorld;
   @Shadow
   public EffectRenderer effectRenderer;
   @Shadow
   public PlayerControllerMP playerController;
   @Shadow
   public EntityRenderer entityRenderer;

   @Inject(
      method = {"startGame"},
      at = {@At(
         value = "FIELD",
         target = "Lnet/minecraft/client/Minecraft;ingameGUI:Lnet/minecraft/client/gui/GuiIngame;",
         shift = At.Shift.AFTER
      )}
   )
   private void g(CallbackInfo ci) {
      new Myaven();
   }

   @Inject(
      method = {"rightClickMouse"},
      at = {@At("TAIL")},
      cancellable = true
   )
   public void m(CallbackInfo ci) {
      MinecraftForge.EVENT_BUS.post(new RightClickEvent());
   }

   @Inject(
      method = {"runTick"},
      at = {@At("HEAD")}
   )
   private void r(CallbackInfo ci) {
      ((AccessorLeftClickCounter)Myaven.mc).setLeftClickCounter(-1);
   }

   @Inject(
      method = {"rightClickMouse"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void U(CallbackInfo ci) {
      if (Myaven.moduleManager.getModule("Animations").isEnabled()) {
         this.rightClickDelayTimer = 4;
         boolean flag = true;
         ItemStack itemstack = this.thePlayer.inventory.getCurrentItem();
         if (this.objectMouseOver == null) {
            logger.warn("Null returned as 'hitResult', this shouldn't happen!");
         } else {
            switch (this.objectMouseOver.typeOfHit) {
               case ENTITY:
                  if (this.playerController.isPlayerRightClickingOnEntity(this.thePlayer, this.objectMouseOver.entityHit, this.objectMouseOver)) {
                     flag = false;
                  } else if (this.playerController.interactWithEntitySendPacket(this.thePlayer, this.objectMouseOver.entityHit)) {
                     flag = false;
                  }
                  break;
               case BLOCK:
                  BlockPos blockpos = this.objectMouseOver.getBlockPos();
                  if (!this.theWorld.isAirBlock(blockpos)) {
                     int i = itemstack != null ? itemstack.stackSize : 0;
                     boolean result = !ForgeEventFactory.onPlayerInteract(
                           this.thePlayer, Action.RIGHT_CLICK_BLOCK, this.theWorld, blockpos, this.objectMouseOver.sideHit, this.objectMouseOver.hitVec
                        )
                        .isCanceled();
                     if (result
                        && this.playerController
                           .onPlayerRightClick(
                              this.thePlayer, this.theWorld, itemstack, blockpos, this.objectMouseOver.sideHit, this.objectMouseOver.hitVec
                           )) {
                        flag = false;
                        this.thePlayer.swingItem();
                     }

                     if (itemstack == null) {
                        return;
                     }

                     if (itemstack.stackSize == 0) {
                        this.thePlayer.inventory.mainInventory[this.thePlayer.inventory.currentItem] = null;
                     } else if (itemstack.stackSize != i || this.playerController.isInCreativeMode()) {
                        this.entityRenderer.itemRenderer.resetEquippedProgress();
                     }
                  }
            }
         }

         if (flag) {
            ItemStack itemstack1 = this.thePlayer.inventory.getCurrentItem();
            boolean resultx = !ForgeEventFactory.onPlayerInteract(this.thePlayer, Action.RIGHT_CLICK_AIR, this.theWorld, null, null, null)
               .isCanceled();
            if (resultx && itemstack1 != null && this.playerController.sendUseItem(this.thePlayer, this.theWorld, itemstack1)) {
               this.entityRenderer.itemRenderer.resetEquippedProgress2();
            }
         }

         ci.cancel();
      }
   }

   @Inject(
      method = {"clickMouse"},
      at = {@At("HEAD")}
   )
   private void Z(CallbackInfo callbackInfo) {
      if (Myaven.moduleManager.getModule("NoHitDelay").isEnabled()) {
         this.leftClickCounter = 0;
      }
   }

   @Inject(
      method = {"sendClickBlockToController"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void N(boolean leftClick, CallbackInfo ci) {
      if (KillAura.combatTick) {
         ci.cancel();
      } else {
         if (Myaven.moduleManager.getModule("Animations").isEnabled()) {
            if (!leftClick) {
               this.leftClickCounter = 0;
            }

            if (this.leftClickCounter <= 0) {
               if (leftClick && this.objectMouseOver != null && this.objectMouseOver.typeOfHit == MovingObjectType.BLOCK) {
                  BlockPos blockpos = this.objectMouseOver.getBlockPos();
                  if (this.theWorld.getBlockState(blockpos).getBlock().getMaterial() != Material.air
                     && this.playerController.onPlayerDamageBlock(blockpos, this.objectMouseOver.sideHit)) {
                     ReflectUtil.invokeBlockHitEffects(this.effectRenderer, blockpos, this.objectMouseOver);
                     if (Myaven.mc.thePlayer.isUsingItem()) {
                        RaytraceUtil.resetSwingProgress();
                     } else {
                        this.thePlayer.swingItem();
                     }
                  }
               } else {
                  this.playerController.resetBlockRemoving();
               }
            }

            ci.cancel();
         }
      }
   }

}
