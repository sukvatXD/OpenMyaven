package Myaven.mixins.accessor;

import net.minecraft.client.settings.KeyBinding;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin({KeyBinding.class})
public interface AccessorKeybinding {
   @Accessor("pressed")
   void setPressed(boolean boolean1);
}
