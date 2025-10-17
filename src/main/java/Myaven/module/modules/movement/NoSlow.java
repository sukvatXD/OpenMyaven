package Myaven.module.modules.movement;

import Myaven.Myaven;
import Myaven.events.SlowdownEvent;
import Myaven.module.Category;
import Myaven.module.Module;
import Myaven.module.modules.combat.KillAura;
import Myaven.setting.Setting;
import Myaven.setting.settings.BooleanSetting;
import Myaven.setting.settings.DescriptionSetting;
import Myaven.setting.settings.ModeSetting;
import Myaven.setting.settings.PercentageSetting;
import net.minecraft.item.ItemSword;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class NoSlow extends Module {
   public static DescriptionSetting swordModeDescription;
   public static ModeSetting swordMode;
   public static BooleanSetting onlyEnableWhenAutoblock;
   public static DescriptionSetting otherModeDescription;
   public static ModeSetting otherMode;
   public static PercentageSetting slowDownPercentage;

   public NoSlow() {
      super("NoSlow", false, Category.Movement, true, "Change the slowdown when blocking sword, eating and pulling bow");
      this.addSettings(swordModeDescription, swordMode, onlyEnableWhenAutoblock, otherModeDescription, otherMode, slowDownPercentage);
   }

   @SubscribeEvent
   public void onSlow(SlowdownEvent event) {
      if (swordMode.getCurrent().equalsIgnoreCase("NONE") && this.mc.thePlayer.getHeldItem().getItem() instanceof ItemSword) {
         event.setSpeed(0.2F);
      } else if (otherMode.getCurrent().equalsIgnoreCase("NONE") && !(this.mc.thePlayer.getHeldItem().getItem() instanceof ItemSword)) {
         event.setSpeed(0.2F);
      } else if (this.mc.thePlayer.getHeldItem().getItem() instanceof ItemSword) {
         if (Myaven.moduleManager.getModule("Killaura").isEnabled()) {
            String var3 = KillAura.autoblockMode.getCurrent();
            switch (var3) {
               case "NONE":
                  event.setSpeed(onlyEnableWhenAutoblock.getState() ? 0.2F : 0.2F + (float)(100 - slowDownPercentage.getPercentage()) / 100.0F * 0.8F);
                  return;
            }

            event.setSpeed(
               onlyEnableWhenAutoblock.getState() && !KillAura.isAutoBlockActive
                  ? 0.2F
                  : 0.2F + (float)(100 - slowDownPercentage.getPercentage()) / 100.0F * 0.8F
            );
         } else if (onlyEnableWhenAutoblock.getState()) {
            event.setSpeed(0.2F);
         } else {
            event.setSpeed(0.2F + (float)(100 - slowDownPercentage.getPercentage()) / 100.0F * 0.8F);
         }
      } else {
         event.setSpeed(0.2F + (float)(100 - slowDownPercentage.getPercentage()) / 100.0F * 0.8F);
      }
   }

   // $VF: Irreducible bytecode was duplicated to produce valid code
   static {
      swordModeDescription = new DescriptionSetting("VANILLA, NONE");
      swordMode = new ModeSetting("Sword-mode", "VANILLA", "NONE");
      onlyEnableWhenAutoblock = new BooleanSetting("Only-enable-when-autoblock", true);
      otherModeDescription = new DescriptionSetting("NONE, VANILLA");
      otherMode = new ModeSetting("Other-mode", "NONE", "VANILLA");
      slowDownPercentage = new PercentageSetting("Slow-down", 0);
   }
}
