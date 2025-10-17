package Myaven.mixins.render;

import Myaven.Myaven;
import net.minecraft.block.BlockGrass;
import net.minecraft.util.EnumWorldBlockLayer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@SideOnly(Side.CLIENT)
@Mixin({BlockGrass.class})
public abstract class MixinBlockGrass {
   /**
    * @author
    * @reason
    */
   @Overwrite
   public EnumWorldBlockLayer getBlockLayer() {
      return Myaven.moduleManager.getModule("cavexray").isEnabled() ? EnumWorldBlockLayer.TRANSLUCENT : EnumWorldBlockLayer.CUTOUT_MIPPED;
   }
}
