package Myaven.module.modules.visual;

import Myaven.module.Category;
import Myaven.module.Module;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class FullBright extends Module {
   private boolean enabledFlag = true;
   private float oldGamma = this.mc.gameSettings.gammaSetting;
   private static String[] a;
   private static String[] b;

   public FullBright() {
      super("FullBright", true, Category.Visual, false, "Let the game always be bright");
   }

   @SubscribeEvent
   public void m(RenderWorldLastEvent event) {
      if (this.enabledFlag) {
         this.oldGamma = this.mc.gameSettings.gammaSetting;
         this.enabledFlag = false;
      }

      this.mc.gameSettings.gammaSetting = 15.0F;
   }

   @Override
   public void onDisable() {
      if (!this.enabledFlag) {
         this.mc.gameSettings.gammaSetting = this.oldGamma;
      }
   }
}
