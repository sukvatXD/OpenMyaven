package Myaven.mixins.client;

import Myaven.Myaven;
import java.io.IOException;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import org.lwjgl.input.Keyboard;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({GuiScreen.class})
public class MixinGuiScreen {
   @Shadow
   public Minecraft mc;

   @Shadow
   protected void keyTyped(char typedChar, int keyCode) throws IOException {
   }

   @Inject(
      method = {"handleKeyboardInput"},
      at = {@At("HEAD")},
      cancellable = true
   )
   public void M(CallbackInfo ci) throws IOException {
      if (Myaven.moduleManager.getModule("InputFix").isEnabled()) {
         char eventChar = Keyboard.getEventCharacter();
         int eventKey = Keyboard.getEventKey();
         if (Keyboard.getEventKeyState() || eventChar >= (int)7415153241454805024L && eventKey == 0) {
            this.keyTyped(Keyboard.getEventCharacter(), Keyboard.getEventKey());
         }

         this.mc.dispatchKeypresses();
         ci.cancel();
      }
   }

   private static IOException a(IOException iOException) {
      return iOException;
   }
}
