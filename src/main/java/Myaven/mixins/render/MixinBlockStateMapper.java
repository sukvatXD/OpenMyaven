package Myaven.mixins.render;

import java.util.Set;
import net.minecraft.client.renderer.block.statemap.BlockStateMapper;
import net.minecraft.init.Blocks;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin({BlockStateMapper.class})
public abstract class MixinBlockStateMapper {
   @Redirect(
      method = {"putAllStateModelLocations"},
      at = @At(
         value = "INVOKE",
         target = "Ljava/util/Set;contains(Ljava/lang/Object;)Z"
      )
   )
   public boolean Z(Set instance, Object o) {
      return o != Blocks.barrier && instance.contains(o);
   }
}
