package Myaven.module.modules.misc;

import Myaven.module.Category;
import Myaven.module.Module;
import Myaven.util.ClientUtil;

public class AntiObfuscation extends Module {
   public AntiObfuscation() {
      super("NoObfuscation", false, Category.Misc, false, "Remove the obfuscation minecraft chat code");
   }

   public static String stripObfuscation(String s) {
      return ClientUtil.isWorldLoaded() && s != null ? s.replace("§k", "") : s;
   }
}
