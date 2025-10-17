package Myaven.module.modules.movement;

import Myaven.Myaven;
import Myaven.events.PlayerUpdateEvent;
import Myaven.module.Category;
import Myaven.module.Module;
import Myaven.util.ClientUtil;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class Sprint extends Module {
   public Sprint() {
      super("Sprint", true, Category.Movement, false, "Automatically sprint");
   }

   @SubscribeEvent
   public void onUpdate(PlayerUpdateEvent event) {
      if (this.mc.thePlayer.isUsingItem()) {
         if (this.mc.thePlayer.moveForward > 0.0F
            && (Myaven.moduleManager.getModule("NoSlow").isEnabled() || !this.mc.thePlayer.isUsingItem())
            && !this.mc.thePlayer.isSneaking()
            && !this.mc.thePlayer.isCollidedHorizontally
            && this.mc.thePlayer.getFoodStats().getFoodLevel() > 6) {
            this.mc.thePlayer.setSprinting(true);
         }
      } else {
         ClientUtil.setKeyPressed(this.mc.gameSettings.keyBindSprint, true);
      }
   }

   @Override
   public void onDisable() {
      ClientUtil.setKeyPressed(this.mc.gameSettings.keyBindSprint, this.mc.gameSettings.keyBindSprint.isPressed());
   }
}
