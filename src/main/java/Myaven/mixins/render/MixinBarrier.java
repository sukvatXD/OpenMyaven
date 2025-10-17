package Myaven.mixins.render;

import Myaven.Myaven;
import net.minecraft.block.Block;
import net.minecraft.block.BlockBarrier;
import net.minecraft.block.material.Material;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumWorldBlockLayer;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({BlockBarrier.class})
public abstract class MixinBarrier extends Block {

   public MixinBarrier(Material materialIn) {
      super(materialIn);
   }

   @Inject(
      method = {"getRenderType"},
      at = {@At("HEAD")},
      cancellable = true
   )
   public void l(CallbackInfoReturnable<Integer> cir) {
      if (Myaven.moduleManager.getModule("barrierVisible").isEnabled()) {
         cir.setReturnValue(3);
      }
   }

   @SideOnly(Side.CLIENT)
   public EnumWorldBlockLayer getBlockLayer() {
      return EnumWorldBlockLayer.CUTOUT;
   }

   @SideOnly(Side.CLIENT)
   public boolean shouldSideBeRendered(IBlockAccess worldIn, BlockPos pos, EnumFacing side) {
      return worldIn.getBlockState(pos).getBlock() != this && super.shouldSideBeRendered(worldIn, pos, side);
   }
}
