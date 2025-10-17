package Myaven.mixins.client;

import Myaven.Myaven;
import Myaven.events.StopUseEvent;
import Myaven.module.modules.combat.KillAura;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.MinecraftForge;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({PlayerControllerMP.class})
public class MixinPlayerControllerMP {
   @Shadow
   @Final
   private Minecraft mc;

   @Inject(
      method = {"onPlayerDamageBlock"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/client/multiplayer/PlayerControllerMP;syncCurrentPlayItem()V",
         shift = At.Shift.AFTER
      )},
      cancellable = true
   )
   public void A(CallbackInfoReturnable<Boolean> cir) {
      if (this.mc.thePlayer.isUsingItem() && Myaven.moduleManager.getModule("Animations").isEnabled()) {
         cir.setReturnValue(true);
      }
   }

   @Inject(
      method = {"onStoppedUsingItem"},
      at = {@At("RETURN")}
   )
   public void F(CallbackInfo ci) {
      MinecraftForge.EVENT_BUS.post(new StopUseEvent());
   }

   @Inject(
      method = {"clickBlock"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void Z(BlockPos pos, EnumFacing face, CallbackInfoReturnable<Boolean> cir) {
      if (KillAura.combatTick) {
         cir.setReturnValue(false);
         cir.cancel();
      }
   }

   @Inject(
      method = {"onPlayerDamageBlock"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void L(BlockPos pos, EnumFacing side, CallbackInfoReturnable<Boolean> cir) {
      if (KillAura.combatTick) {
         cir.setReturnValue(false);
         cir.cancel();
      }
   }

   @Inject(
      method = {"onPlayerDestroyBlock"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void x(BlockPos pos, EnumFacing side, CallbackInfoReturnable<Boolean> cir) {
      if (KillAura.combatTick) {
         cir.setReturnValue(false);
         cir.cancel();
      }
   }
}
