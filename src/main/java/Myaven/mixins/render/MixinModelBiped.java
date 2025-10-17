package Myaven.mixins.render;

import Myaven.Myaven;
import Myaven.module.modules.visual.TeamInvisible;
import Myaven.util.ClientUtil;
import Myaven.util.RotationUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({ModelBiped.class})
public abstract class MixinModelBiped {
   @Shadow
   public ModelRenderer bipedRightArm;
   @Shadow
   public int heldItemRight;
   @Shadow
   public ModelRenderer bipedHead;

   @Inject(
      method = {"setRotationAngles"},
      at = {@At(
         value = "FIELD",
         target = "Lnet/minecraft/client/model/ModelBiped;swingProgress:F"
      )}
   )
   private void R(
      float p_setRotationAngles_1_,
      float p_setRotationAngles_2_,
      float p_setRotationAngles_3_,
      float p_setRotationAngles_4_,
      float p_setRotationAngles_5_,
      float p_setRotationAngles_6_,
      Entity p_setRotationAngles_7_,
      CallbackInfo callbackInfo
   ) {
      if (p_setRotationAngles_7_ instanceof EntityPlayer && p_setRotationAngles_7_.equals(Minecraft.getMinecraft().thePlayer)) {
         this.bipedHead.rotateAngleX = (float)Math.toRadians(
            (double)RotationUtil.interpolate(ClientUtil.getTimer().renderPartialTicks, RotationUtil.C, RotationUtil.K)
         );
      }
   }

   @Inject(
      method = {"render"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/client/renderer/GlStateManager;pushMatrix()V",
         shift = At.Shift.AFTER
      )}
   )
   public void L(Entity entity, float a, float b, float c, float d, float e, float scale, CallbackInfo ci) {
      if (entity instanceof EntityLivingBase && Myaven.moduleManager.getModule("TeamInvisible").isEnabled()) {
         TeamInvisible.applyTransparency((EntityLivingBase)entity);
      }
   }

   @Inject(
      method = {"render"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/client/renderer/GlStateManager;popMatrix()V",
         ordinal = 1
      )}
   )
   public void Y(Entity entity, float a, float b, float c, float d, float e, float scale, CallbackInfo ci) {
      if (entity instanceof EntityLivingBase && Myaven.moduleManager.getModule("TeamInvisible").isEnabled()) {
         TeamInvisible.resetTransparency((EntityLivingBase)entity);
      }
   }
}
