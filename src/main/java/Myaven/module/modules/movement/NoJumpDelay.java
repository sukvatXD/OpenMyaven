package Myaven.module.modules.movement;

import Myaven.events.PlayerUpdateEvent;
import Myaven.mixins.accessor.AccessorJumpTicks;
import Myaven.module.Category;
import Myaven.module.Module;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class NoJumpDelay extends Module {

   public NoJumpDelay() {
      super("NoJumpDelay", true, Category.Movement, false, "Remove vanilla hold-space jump delay");
   }

   @SubscribeEvent
   public void onUpdate(PlayerUpdateEvent event) {
      ((AccessorJumpTicks)this.mc.thePlayer).setJumpTicks(0);
   }
}
