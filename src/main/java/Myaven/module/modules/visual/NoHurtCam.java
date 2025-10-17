package Myaven.module.modules.visual;

import Myaven.module.Category;
import Myaven.module.Module;
import Myaven.setting.Setting;
import Myaven.setting.settings.PercentageSetting;

public class NoHurtCam extends Module {
   public static PercentageSetting effect;

   public NoHurtCam() {
      super("NoHurtCam", true, Category.Visual, false, "Change the hurt camera effect");
      this.addSettings(new Setting[]{effect});
   }

   // $VF: Irreducible bytecode was duplicated to produce valid code
   static {
      effect = new PercentageSetting("Effect", 0);
   }
}
