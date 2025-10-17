package Myaven.mixins.render;

import Myaven.Myaven;
import Myaven.events.BlockBBEvent;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumWorldBlockLayer;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin({Block.class})
public abstract class MixinBlock {
   @Shadow
   @Final
   protected BlockState blockState;
   @Shadow
   public abstract AxisAlignedBB getCollisionBoundingBox(World world, BlockPos blockPos, IBlockState iBlockState);

   /**
    * @author
    * @reason
    */
   @SideOnly(Side.CLIENT)
   @Overwrite
   public EnumWorldBlockLayer getBlockLayer() {
      return Myaven.moduleManager.getModule("cavexray").isEnabled() ? EnumWorldBlockLayer.TRANSLUCENT : EnumWorldBlockLayer.SOLID;
   }

   /**
    * @author
    * @reason
    */
   @Overwrite
   public void addCollisionBoxesToList(World worldIn, BlockPos pos, IBlockState state, AxisAlignedBB mask, List<AxisAlignedBB> list, Entity collidingEntity) {
      AxisAlignedBB axisalignedbb = this.getCollisionBoundingBox(worldIn, pos, state);
      BlockBBEvent blockBBEvent = new BlockBBEvent(this.blockState.getBlock(), pos, axisalignedbb);
      MinecraftForge.EVENT_BUS.post(blockBBEvent);
      if (!blockBBEvent.isCanceled()) {
         axisalignedbb = blockBBEvent.getBoundingBox();
         if (axisalignedbb != null && mask.intersectsWith(axisalignedbb)) {
            list.add(axisalignedbb);
         }
      }
   }
}
