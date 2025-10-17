package Myaven.mixins.client;

import Myaven.events.MoveInputEvent;
import Myaven.events.PostMoveInputEvent;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.util.MovementInput;
import net.minecraft.util.MovementInputFromOptions;
import net.minecraftforge.common.MinecraftForge;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({MovementInputFromOptions.class})
public abstract class MixinMovementFromOptions extends MovementInput {
   @Shadow
   @Final
   private GameSettings gameSettings;

   @Inject(
      method = {"updatePlayerMoveState"},
      at = {@At("HEAD")},
      cancellable = true
   )
   public void N(CallbackInfo ci) {
      this.moveStrafe = 0.0F;
      this.moveForward = 0.0F;
      if (this.gameSettings.keyBindForward.isKeyDown()) {
         this.moveForward++;
      }

      if (this.gameSettings.keyBindBack.isKeyDown()) {
         this.moveForward--;
      }

      if (this.gameSettings.keyBindLeft.isKeyDown()) {
         this.moveStrafe++;
      }

      if (this.gameSettings.keyBindRight.isKeyDown()) {
         this.moveStrafe--;
      }

      this.jump = this.gameSettings.keyBindJump.isKeyDown();
      this.sneak = this.gameSettings.keyBindSneak.isKeyDown();
      MoveInputEvent moveInputEvent = new MoveInputEvent(this.moveForward, this.moveStrafe, this.jump, this.sneak, 0.3);
      MinecraftForge.EVENT_BUS.post(moveInputEvent);
      double sneakMultiplier = moveInputEvent.o();
      this.moveForward = moveInputEvent.getForward();
      this.moveStrafe = moveInputEvent.getStrafe();
      this.jump = moveInputEvent.getJump();
      this.sneak = moveInputEvent.getSneak();
      if (this.sneak) {
         this.moveStrafe = (float)((double)this.moveStrafe * sneakMultiplier);
         this.moveForward = (float)((double)this.moveForward * sneakMultiplier);
      }

      MinecraftForge.EVENT_BUS.post(new PostMoveInputEvent());
      ci.cancel();
   }
}
