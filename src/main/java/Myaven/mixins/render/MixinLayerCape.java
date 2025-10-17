package Myaven.mixins.render;

import Myaven.Myaven;
import Myaven.module.modules.visual.TeamInvisible;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.layers.LayerCape;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({LayerCape.class})
public abstract class MixinLayerCape {
   @Inject(
      method = {"doRenderLayer*"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/client/renderer/GlStateManager;pushMatrix()V",
         shift = At.Shift.AFTER
      )}
   )
   public void g(AbstractClientPlayer entity, float a, float b, float c, float d, float e, float f, float g, CallbackInfo ci) {
      if (Myaven.moduleManager.getModule("TeamInvisible").isEnabled()) {
         TeamInvisible.applyTransparency(entity);
      }
   }

   @Inject(
      method = {"doRenderLayer*"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/client/renderer/GlStateManager;popMatrix()V"
      )}
   )
   public void Z(AbstractClientPlayer entity, float a, float b, float c, float d, float e, float f, float g, CallbackInfo ci) {
      if (Myaven.moduleManager.getModule("TeamInvisible").isEnabled()) {
         TeamInvisible.resetTransparency(entity);
      }
   }
}
