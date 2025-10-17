package Myaven.mixins.accessor;

import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin({Minecraft.class})
public interface AccessorLeftClickCounter {
   @Accessor("leftClickCounter")
   void setLeftClickCounter(int integer);
}
