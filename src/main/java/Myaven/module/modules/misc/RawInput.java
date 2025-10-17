package Myaven.module.modules.misc;

import Myaven.Myaven;
import Myaven.hooks.MouseHelperHook;
import Myaven.module.Category;
import Myaven.module.Module;

public class RawInput extends Module {
   private boolean hookActive = false;
   private MouseHelperHook mouseHelperHook;

   public RawInput() {
      super("RawInput", false, Category.Misc, false, "Fix your mouse input");
   }

   @Override
   public void onEnable() {
      if (!this.hookActive) {
         this.mouseHelperHook = new MouseHelperHook();
         this.mouseHelperHook.Y();
         this.hookActive = true;
      }
   }

   @Override
   public void onDisable() {
      if (this.hookActive) {
         this.mouseHelperHook.e();
         this.mouseHelperHook = null;
         this.mc.mouseHelper = Myaven.mouseHelper;
         this.hookActive = false;
      }
   }
}
