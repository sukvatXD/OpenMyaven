package Myaven.module.modules.misc;

import Myaven.mixins.accessor.AccessorTimer;
import Myaven.module.Category;
import Myaven.module.Module;
import Myaven.setting.Setting;
import Myaven.setting.settings.SliderSetting;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;

public class Timer extends Module {
   public static SliderSetting speed;
   private boolean hasSavedTimerSpeed = false;
   private float originalTimerSpeed;

   public Timer() {
      super("Timer", false, Category.Misc, true, "Modify the game's speed");
      this.addSettings(new Setting[]{speed});
   }

   @SubscribeEvent
   public void onTick(ClientTickEvent event) {
      if (!this.hasSavedTimerSpeed) {
         this.originalTimerSpeed = ((AccessorTimer)this.mc).getTimer().timerSpeed;
         this.hasSavedTimerSpeed = true;
      }

      ((AccessorTimer)this.mc).getTimer().timerSpeed = (float)speed.getRoundedValue();
   }

   @Override
   public void onDisable() {
      if (this.hasSavedTimerSpeed) {
         ((AccessorTimer)this.mc).getTimer().timerSpeed = this.originalTimerSpeed;
         this.hasSavedTimerSpeed = false;
      }
   }

   // $VF: Irreducible bytecode was duplicated to produce valid code
   static {
      speed = new SliderSetting("Speed", 1.0, 0.0, 5.0, 0.01);
   }
}
