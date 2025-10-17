package Myaven.mixins.render;

import Myaven.Myaven;
import Myaven.module.modules.visual.CaveXray;
import Myaven.util.ColorUtil;
import java.nio.ByteOrder;
import java.nio.IntBuffer;
import net.minecraft.client.renderer.WorldRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin({WorldRenderer.class})
public abstract class MixinWorldRenderer {
   @Shadow
   private boolean noColor;
   @Shadow
   public IntBuffer rawIntBuffer;

   @Shadow
   protected abstract int getColorIndex(int integer);

   /**
    * @author
    * @reason
    */
   @Overwrite
   public void putColorMultiplier(float red, float green, float blue, int p_178978_4_) {
      int i = this.getColorIndex(p_178978_4_);
      int j = -1;
      if (!this.noColor) {
         j = this.rawIntBuffer.get(i);
         if (ByteOrder.nativeOrder() == ByteOrder.LITTLE_ENDIAN) {
            int k = (int)((float)(j & 0xFF) * red);
            int l = (int)((float)(j >> 8 & 0xFF) * green);
            int i1 = (int)((float)(j >> 16 & 0xFF) * blue);
            j &= -16777216;
            j = j | i1 << 16 | l << 8 | k;
            if (Myaven.moduleManager.getModule("cavexray").isEnabled()) {
               j = ColorUtil.toARGB(k, l, i1, CaveXray.computeAlphaFromOpacity());
            }
         } else {
            int j1 = (int)((float)(j >> 24 & 0xFF) * red);
            int k1 = (int)((float)(j >> 16 & 0xFF) * green);
            int l1 = (int)((float)(j >> 8 & 0xFF) * blue);
            j &= 255;
            j = j | j1 << 24 | k1 << 16 | l1 << 8;
            if (Myaven.moduleManager.getModule("cavexray").isEnabled()) {
               j = ColorUtil.toARGB(j1, k1, l1, CaveXray.computeAlphaFromOpacity());
            }
         }
      }

      this.rawIntBuffer.put(i, j);
   }
}
