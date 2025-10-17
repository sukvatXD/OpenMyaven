package Myaven.mixins.render;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockModelRenderer;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.resources.model.IBakedModel;
import net.minecraft.util.BlockPos;
import net.minecraft.world.IBlockAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin({BlockModelRenderer.class})
public abstract class MixinBlockModelRenderer {
   @Shadow
   public boolean renderModelAmbientOcclusion(
      IBlockAccess blockAccessIn, IBakedModel modelIn, Block blockIn, BlockPos blockPosIn, WorldRenderer worldRendererIn, boolean checkSides
   ) {
      return false;
   }

   @Shadow
   public boolean renderModelStandard(
      IBlockAccess blockAccessIn, IBakedModel modelIn, Block blockIn, BlockPos blockPosIn, WorldRenderer worldRendererIn, boolean checkSides
   ) {
      return checkSides;
   }

   /**
    * @author
    * @reason
    */
   @Overwrite
   public boolean renderModel(IBlockAccess world, IBakedModel model, IBlockState state, BlockPos pos, WorldRenderer renderer, boolean checkSides) {
      Block block = state.getBlock();
      Minecraft mc = Minecraft.getMinecraft();
      if (Minecraft.isAmbientOcclusionEnabled() && block.getLightValue() == 0 && model.isAmbientOcclusion()) {
         boolean var13 = true;
      } else {
         boolean var10000 = false;
      }

      return this.renderModelAmbientOcclusion(world, model, block, pos, renderer, checkSides);
   }
}
