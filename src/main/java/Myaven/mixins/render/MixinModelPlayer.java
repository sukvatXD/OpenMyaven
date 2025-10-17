package Myaven.mixins.render;

import Myaven.Myaven;
import Myaven.module.modules.visual.TeamInvisible;
import net.minecraft.client.model.ModelPlayer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({ModelPlayer.class})
public abstract class MixinModelPlayer {

   @Inject(
      method = {"render"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/client/renderer/GlStateManager;pushMatrix()V",
         shift = At.Shift.AFTER
      )}
   )
   public void Q(Entity entity, float a, float b, float c, float d, float e, float scale, CallbackInfo ci) {
      if (entity instanceof EntityLivingBase && Myaven.moduleManager.getModule("TeamInvisible").isEnabled()) {
         TeamInvisible.applyTransparency((EntityLivingBase)entity);
      }
   }

   @Inject(
      method = {"render"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/client/renderer/GlStateManager;popMatrix()V"
      )}
   )
   public void t(Entity entity, float a, float b, float c, float d, float e, float scale, CallbackInfo ci) {
      if (entity instanceof EntityLivingBase && Myaven.moduleManager.getModule("TeamInvisible").isEnabled()) {
         TeamInvisible.resetTransparency((EntityLivingBase)entity);
      }
   }
}
