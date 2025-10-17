package Myaven.mixins.render;

import Myaven.Myaven;
import java.util.BitSet;
import net.minecraft.client.renderer.chunk.VisGraph;
import net.minecraft.util.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin({VisGraph.class})
public abstract class MixinVisGraph {
   @Shadow
   private BitSet field_178612_d = new BitSet(4096);
   @Shadow
   private int field_178611_f = 4096;

   @Shadow
   private static int getIndex(BlockPos pos) {
      return getIndex(pos.getX() & 15, pos.getY() & 15, pos.getZ() & 15);
   }

   @Shadow
   private static int getIndex(int x, int y, int z) {
      return x << 0 | y << 8 | z << 4;
   }

   /**
    * @author
    * @reason
    */
   @Overwrite
   public void func_178606_a(BlockPos p_178606_1_) {
      if (!Myaven.moduleManager.getModule("cavexray").isEnabled()) {
         this.field_178612_d.set(getIndex(p_178606_1_), true);
         this.field_178611_f--;
      }
   }
}
