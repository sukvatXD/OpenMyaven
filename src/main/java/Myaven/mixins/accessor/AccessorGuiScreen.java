package Myaven.mixins.accessor;

import net.minecraft.client.gui.GuiScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin({GuiScreen.class})
public interface AccessorGuiScreen {
   @Invoker("mouseClicked")
   void invokeMouseClicked(int integer1, int integer2, int integer3);
}
