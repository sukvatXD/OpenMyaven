package Myaven.mixins.accessor;

import net.minecraft.network.play.client.C0DPacketCloseWindow;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin({C0DPacketCloseWindow.class})
public interface AccessorC0DPacketCloseWindow {
   @Accessor
   int getWindowId();
}
