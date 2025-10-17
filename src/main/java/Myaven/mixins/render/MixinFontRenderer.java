package Myaven.mixins.render;

import Myaven.Myaven;
import Myaven.module.modules.misc.AntiObfuscation;
import Myaven.module.modules.misc.NameHider;
import Myaven.util.ClientUtil;
import net.minecraft.client.gui.FontRenderer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@SideOnly(Side.CLIENT)
@Mixin({FontRenderer.class})
public class MixinFontRenderer {
   private static String[] a;
   private static String[] b;

   @ModifyVariable(
      method = {"renderString"},
      at = @At("HEAD"),
      require = 1,
      ordinal = 0,
      argsOnly = true
   )
   private String T(String string) {
      if (ClientUtil.isWorldLoaded() && Myaven.moduleManager.getModule("NameHider").isEnabled()) {
         string = NameHider.replaceName(string);
      }

      if (ClientUtil.isWorldLoaded() && Myaven.moduleManager.getModule("NoObfuscation").isEnabled()) {
         string = AntiObfuscation.stripObfuscation(string);
      }

      return string;
   }

   @ModifyVariable(
      method = {"getStringWidth"},
      at = @At("HEAD"),
      require = 1,
      ordinal = 0,
      argsOnly = true
   )
   private String x(String string) {
      if (ClientUtil.isWorldLoaded() && Myaven.moduleManager.getModule("NameHider").isEnabled()) {
         string = NameHider.replaceName(string);
      }

      if (ClientUtil.isWorldLoaded() && Myaven.moduleManager.getModule("NoObfuscation").isEnabled()) {
         string = AntiObfuscation.stripObfuscation(string);
      }

      return string;
   }
}
