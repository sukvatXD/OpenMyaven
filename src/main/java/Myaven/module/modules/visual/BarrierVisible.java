package Myaven.module.modules.visual;

import Myaven.module.Category;
import Myaven.module.Module;

public class BarrierVisible extends Module {
   private boolean enabledFlag = false;
   public BarrierVisible() {
      super("BarrierVisible", false, Category.Visual, false, "Render barriers as glasses");
   }

   @Override
   public void onEnable() {
      if (!this.enabledFlag) {
         this.mc.renderGlobal.loadRenderers();
      }

      this.enabledFlag = true;
   }

   @Override
   public void onDisable() {
      if (this.enabledFlag) {
         this.mc.renderGlobal.loadRenderers();
      }

      this.enabledFlag = false;
   }
}
