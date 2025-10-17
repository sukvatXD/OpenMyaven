package Myaven.module.modules.combat;

import Myaven.events.PlayerUpdateEvent;
import Myaven.module.Category;
import Myaven.module.Module;
import Myaven.setting.Setting;
import Myaven.setting.settings.DescriptionSetting;
import Myaven.setting.settings.ModeSetting;
import Myaven.setting.settings.PercentageSetting;
import Myaven.util.TimerUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.input.Keyboard;

public class KeepSprint extends Module {
   public static DescriptionSetting modeDescription;
   public static ModeSetting mode;
   public static PercentageSetting slowdownPercentage;
   private static TimerUtil timer;
   private static boolean isSprintReset;

   public KeepSprint() {
      super("KeepSprint", false, Category.Combat, true, "Modify the slowdown while attacking");
      this.addSettings(new Setting[]{modeDescription, mode, slowdownPercentage});
   }

   public static void executeKeepSprint() {
      Minecraft mc = Minecraft.getMinecraft();
      String var2 = mode.getCurrent();
      switch (var2) {
         case "VANILLA":
            mc.thePlayer.motionX = mc.thePlayer.motionX * (1.0 - 0.4 * (double)slowdownPercentage.getPercentage() / 100.0);
            mc.thePlayer.motionZ = mc.thePlayer.motionZ * (1.0 - 0.4 * (double)slowdownPercentage.getPercentage() / 100.0);
            break;
         case "SPRINT_RESET":
            mc.thePlayer.motionX *= 0.6;
            mc.thePlayer.motionZ *= 0.6;
            if (!isSprintReset) {
               KeyBinding.setKeyBindState(mc.gameSettings.keyBindForward.getKeyCode(), false);
               timer.reset();
               isSprintReset = true;
            }
      }
   }

   @SubscribeEvent
   public void onPlayerUpdateEvent(PlayerUpdateEvent event) {
      if (timer.hasTimePassed(50L, true) && isSprintReset && Keyboard.isKeyDown(this.mc.gameSettings.keyBindForward.getKeyCode())) {
         KeyBinding.setKeyBindState(this.mc.gameSettings.keyBindForward.getKeyCode(), true);
         isSprintReset = false;
      }
   }

   // $VF: Irreducible bytecode was duplicated to produce valid code
   static {
      modeDescription = new DescriptionSetting("VANILLA, SPRINT_RESET");
      mode = new ModeSetting("Mode", "VANILLA", "SPRINT_RESET");
      slowdownPercentage = new PercentageSetting("Slowdown", 0);
      timer = new TimerUtil();
      isSprintReset = false;
   }
}
