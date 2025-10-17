package Myaven.events;


import net.minecraft.block.Block;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraftforge.fml.common.eventhandler.Cancelable;
import net.minecraftforge.fml.common.eventhandler.Event;

@Cancelable
public class BlockBBEvent extends Event {
   private Block block;
   private BlockPos pos;
   public AxisAlignedBB boundingBox;

   public BlockBBEvent(Block block, BlockPos pos, AxisAlignedBB boundingBox) {
      this.block = block;
      this.pos = pos;
      this.boundingBox = boundingBox;
   }

   public BlockPos getPos() {
      return this.pos;
   }

   
   public void setBlock(Block block) {
      this.block = block;
   }

   
   public void setPos(BlockPos blockPos) {
      this.pos = blockPos;
   }

   
   public void setBoundingBox(AxisAlignedBB boundingBox) {
      this.boundingBox = boundingBox;
   }

   
   public Block getBlock() {
      return this.block;
   }

   
   public AxisAlignedBB getBoundingBox() {
      return this.boundingBox;
   }
}
