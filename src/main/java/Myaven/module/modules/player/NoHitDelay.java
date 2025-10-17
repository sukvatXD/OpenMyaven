package Myaven.module.modules.player;

import Myaven.module.Category;
import Myaven.module.Module;

public class NoHitDelay extends Module {
   public NoHitDelay() {
      super("NoHitDelay", true, Category.Player, false, "Remove 10 ticks hit delay");
   }
}
