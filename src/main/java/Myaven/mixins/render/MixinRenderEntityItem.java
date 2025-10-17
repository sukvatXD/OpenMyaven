package Myaven.mixins.render;

import Myaven.Myaven;
import Myaven.module.modules.visual.ItemScale;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.entity.RenderEntityItem;
import net.minecraft.client.resources.model.IBakedModel;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({RenderEntityItem.class})
public class MixinRenderEntityItem {

   @Shadow
   protected int func_177078_a(ItemStack stack) {
      int i = 1;
      if (stack.stackSize > 48) {
         i = 5;
      } else if (stack.stackSize > 32) {
         i = 4;
      } else if (stack.stackSize > 16) {
         i = 3;
      } else if (stack.stackSize > 1) {
         i = 2;
      }

      return i;
   }

   @Inject(
      method = {"func_177077_a"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void b(
      EntityItem itemIn,
      double p_177077_2_,
      double p_177077_4_,
      double p_177077_6_,
      float p_177077_8_,
      IBakedModel p_177077_9_,
      CallbackInfoReturnable<Integer> cir
   ) {
      ItemStack itemstack = itemIn.getEntityItem();
      Item item = itemstack.getItem();
      Block block = Block.getBlockFromItem(item);
      if (item == null) {
         cir.setReturnValue(0);
         cir.cancel();
      } else {
         boolean flag = p_177077_9_.isGui3d();
         int i = this.func_177078_a(itemstack);
         float f1 = MathHelper.sin(((float)itemIn.getAge() + p_177077_8_) / 10.0F + itemIn.hoverStart) * 0.1F + 0.1F;
         float f2 = p_177077_9_.getItemCameraTransforms().getTransform(TransformType.GROUND).scale.y;
         GlStateManager.translate((float)p_177077_2_, (float)p_177077_4_ + f1 + 0.25F * f2, (float)p_177077_6_);
         if (flag || Minecraft.getMinecraft().getRenderManager().options != null) {
            if (p_177077_9_.isGui3d()) {
               float f3 = (((float)itemIn.getAge() + p_177077_8_) / 20.0F + itemIn.hoverStart) * (180.0F / (float)Math.PI);
               GlStateManager.rotate(f3, 0.0F, 1.0F, 0.0F);
            } else {
               GlStateManager.rotate(180.0F - Minecraft.getMinecraft().getRenderManager().playerViewY, 0.0F, 1.0F, 0.0F);
               GlStateManager.rotate(
                  Minecraft.getMinecraft().gameSettings.thirdPersonView == 2
                     ? Minecraft.getMinecraft().getRenderManager().playerViewX
                     : -Minecraft.getMinecraft().getRenderManager().playerViewX,
                  1.0F,
                  0.0F,
                  0.0F
               );
            }
         }

         if (!flag) {
            float f6 = -0.0F * (float)(i - 1) * 0.5F;
            float f4 = -0.0F * (float)(i - 1) * 0.5F;
            float f5 = -0.046875F * (float)(i - 1) * 0.5F;
            GlStateManager.translate(f6, f4, f5);
         }

         float f6 = -0.0F * (float)(i - 1) * 0.5F;
         float f4 = -0.0F * (float)(i - 1) * 0.5F;
         float f5 = -0.046875F * (float)(i - 1) * 0.5F;
         if (Myaven.moduleManager.getModule("ItemScale").isEnabled() && ItemScale.onScale(itemstack)) {
            GlStateManager.translate((double)f6, (double)f4 + ItemScale.scale.getRoundedValue() / 8.0, (double)f5);
            GlStateManager.scale(ItemScale.scale.getRoundedValue(), ItemScale.scale.getRoundedValue(), ItemScale.scale.getRoundedValue());
         }

         GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
         cir.setReturnValue(i);
         cir.cancel();
      }
   }
}
