package Myaven.module.modules.movement;

import Myaven.events.PlayerUpdateEvent;
import Myaven.module.Category;
import Myaven.module.Module;
import Myaven.setting.Setting;
import Myaven.setting.settings.DescriptionSetting;
import Myaven.setting.settings.ModeSetting;
import Myaven.setting.settings.SliderSetting;
import Myaven.util.PlayerUtil;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class Speed extends Module {
   public static DescriptionSetting modeDescription;
   public static ModeSetting mode;
   public static SliderSetting speed;

   public Speed() {
      super("Speed", false, Category.Movement, true, "Move faster");
      this.addSettings(new Setting[]{modeDescription, mode, speed});
   }

   @SubscribeEvent
   public void onUpdate(PlayerUpdateEvent event) {
      if (this.mc.gameSettings.keyBindForward.isKeyDown()
         || this.mc.gameSettings.keyBindLeft.isKeyDown()
         || this.mc.gameSettings.keyBindRight.isKeyDown()
         || this.mc.gameSettings.keyBindBack.isKeyDown()) {
         String var3 = mode.getCurrent();
         switch (var3) {
            case "GROUND_STRAFE":
               if (this.mc.thePlayer.onGround) {
                  PlayerUtil.setSpeed(speed.getRoundedValue());
                  this.mc.thePlayer.jump();
               }
               break;
            case "AUTO_JUMP":
               if (!this.mc.thePlayer.onGround) {
                  this.mc.thePlayer.motionX = this.mc.thePlayer.motionX * speed.getRoundedValue();
                  this.mc.thePlayer.motionZ = this.mc.thePlayer.motionZ * speed.getRoundedValue();
               } else {
                  this.mc.thePlayer.jump();
               }
               break;
            case "VANILLA":
               if (!this.mc.thePlayer.onGround) {
                  PlayerUtil.setSpeed(speed.getRoundedValue() * 4.0 / Math.PI);
               } else {
                  this.mc.thePlayer.jump();
               }
         }
      }
   }

   // $VF: Irreducible bytecode was duplicated to produce valid code
   static {
      modeDescription = new DescriptionSetting("GROUND_STRAFE, AUTO_JUMP, VANILLA");
      mode = new ModeSetting("Mode", "GROUND_STRAFE", "AUTO_JUMP", "VANILLA");
      speed = new SliderSetting("Speed", 1.0, 1.0, 10.0, 0.01);
   }
}
