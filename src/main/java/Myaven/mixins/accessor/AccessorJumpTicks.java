package Myaven.mixins.accessor;

import net.minecraft.entity.EntityLivingBase;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin({EntityLivingBase.class})
public interface AccessorJumpTicks {
   @Accessor("jumpTicks")
   void setJumpTicks(int integer);
}
