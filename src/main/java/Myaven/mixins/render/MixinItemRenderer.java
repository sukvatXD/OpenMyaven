package Myaven.mixins.render;

import Myaven.Myaven;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemMap;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({ItemRenderer.class})
public class MixinItemRenderer {
   @Shadow
   @Final
   private Minecraft mc;
   @Shadow
   private float equippedProgress;
   @Shadow
   private float prevEquippedProgress;
   @Shadow
   private ItemStack itemToRender;
   private static String a;

   @Shadow
   public void renderItem(EntityLivingBase entityIn, ItemStack heldStack, TransformType transform) {
   }

   @Shadow
   private void renderItemMap(AbstractClientPlayer clientPlayer, float p_178097_2_, float p_178097_3_, float p_178097_4_) {
   }

   @Shadow
   private void rotateArroundXAndY(float angle, float p_178101_2_) {
   }

   @Shadow
   private void setLightMapFromPlayer(AbstractClientPlayer clientPlayer) {
   }

   @Shadow
   private void rotateWithPlayerRotations(EntityPlayerSP entityplayerspIn, float partialTicks) {
   }

   @Shadow
   private void renderPlayerArm(AbstractClientPlayer clientPlayer, float p_178095_2_, float p_178095_3_) {
   }

   @Shadow
   private void doItemUsedTransformations(float p_178105_1_) {
   }

   @Shadow
   private void performDrinking(AbstractClientPlayer clientPlayer, float p_178104_2_) {
   }

   @Shadow
   private void transformFirstPersonItem(float equipProgress, float swingProgress) {
   }

   @Shadow
   private void doBowTransformations(float p_178098_1_, AbstractClientPlayer clientPlayer) {
   }

   @Shadow
   private void doBlockTransformations() {
   }

   @Inject(
      method = {"renderItemInFirstPerson"},
      at = {@At("HEAD")},
      cancellable = true
   )
   public void Y(float partialTicks, CallbackInfo ci) {
      if (Myaven.moduleManager.getModule(a).isEnabled()) {
         float f = 1.0F - (this.prevEquippedProgress + (this.equippedProgress - this.prevEquippedProgress) * partialTicks);
         AbstractClientPlayer abstractclientplayer = this.mc.thePlayer;
         float f1 = abstractclientplayer.getSwingProgress(partialTicks);
         float f2 = abstractclientplayer.prevRotationPitch + (abstractclientplayer.rotationPitch - abstractclientplayer.prevRotationPitch) * partialTicks;
         float f3 = abstractclientplayer.prevRotationYaw + (abstractclientplayer.rotationYaw - abstractclientplayer.prevRotationYaw) * partialTicks;
         this.rotateArroundXAndY(f2, f3);
         this.setLightMapFromPlayer(abstractclientplayer);
         this.rotateWithPlayerRotations((EntityPlayerSP)abstractclientplayer, partialTicks);
         GlStateManager.enableRescaleNormal();
         GlStateManager.pushMatrix();
         if (this.itemToRender != null) {
            if (this.itemToRender.getItem() instanceof ItemMap) {
               this.renderItemMap(abstractclientplayer, f2, f, f1);
            } else if (abstractclientplayer.getItemInUseCount() > 0) {
               EnumAction enumaction = this.itemToRender.getItemUseAction();
               switch (enumaction) {
                  case NONE:
                     this.transformFirstPersonItem(f, 0.0F);
                     break;
                  case EAT:
                  case DRINK:
                     this.performDrinking(abstractclientplayer, partialTicks);
                     this.transformFirstPersonItem(0.2F, f1);
                     GlStateManager.translate(0.0F, 0.3F, 0.0F);
                     break;
                  case BLOCK:
                     GlStateManager.translate(0.56F, -0.52F, -0.71999997F);
                     GlStateManager.translate(0.0F, -0.05F, 0.0F);
                     GlStateManager.rotate(45.0F, 0.0F, 1.0F, 0.0F);
                     float b = MathHelper.sin(f1 * f1 * (float) Math.PI);
                     float b1 = MathHelper.sin(MathHelper.sqrt_float(f1) * (float) Math.PI);
                     GlStateManager.rotate(b * -20.0F, 0.0F, 1.0F, 0.0F);
                     GlStateManager.rotate(b1 * -20.0F, 0.0F, 0.0F, 1.0F);
                     GlStateManager.rotate(b1 * -80.0F, 1.0F, 0.0F, 0.0F);
                     GlStateManager.scale(0.35F, 0.35F, 0.35F);
                     this.doBlockTransformations();
                     GlStateManager.translate(-0.5F, 0.2F, 0.0F);
                     break;
                  case BOW:
                     this.transformFirstPersonItem(0.2F, f1);
                     this.doBowTransformations(partialTicks, abstractclientplayer);
                     GlStateManager.translate(0.0F, 0.2F, 0.0F);
               }
            } else {
               this.doItemUsedTransformations(f1);
               this.transformFirstPersonItem(f, f1);
            }

            this.renderItem(abstractclientplayer, this.itemToRender, TransformType.FIRST_PERSON);
         } else if (!abstractclientplayer.isInvisible()) {
            this.renderPlayerArm(abstractclientplayer, f, f1);
         }

         GlStateManager.popMatrix();
         GlStateManager.disableRescaleNormal();
         RenderHelper.disableStandardItemLighting();
         ci.cancel();
      }
   }
}
