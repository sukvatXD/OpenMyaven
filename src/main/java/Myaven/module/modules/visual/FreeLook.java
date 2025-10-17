package Myaven.module.modules.visual;

import Myaven.module.Category;
import Myaven.module.Module;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;

public class FreeLook extends Module {
   private boolean needsInit = true;
   private boolean restore = false;
   private int previousThirdPersonView;
   private float yaw;
   private float pitch;
   public static boolean isActive;
   public static float freeLookYaw;
   public static float freeLookPitch;

   public FreeLook() {
      super("Freelook", false, Category.Visual, true, "Allows you to move your camera without moving your head");
   }

   @SubscribeEvent
   public void m(ClientTickEvent event) {
      if (this.needsInit) {
         this.yaw = this.mc.thePlayer.rotationYaw;
         this.pitch = this.mc.thePlayer.rotationPitch;
         this.previousThirdPersonView = this.mc.gameSettings.thirdPersonView;
         this.needsInit = false;
      }

      this.mc.gameSettings.thirdPersonView = 1;
      freeLookYaw = this.yaw;
      freeLookPitch = this.pitch;
      isActive = true;
      this.restore = true;
   }

   @Override
   public void onDisable() {
      isActive = false;
      if (this.restore) {
         this.mc.gameSettings.thirdPersonView = this.previousThirdPersonView;
         this.mc.thePlayer.rotationYaw = this.yaw;
         this.mc.thePlayer.rotationPitch = this.pitch;
         this.restore = false;
         this.needsInit = true;
      }
   }

   // $VF: Irreducible bytecode was duplicated to produce valid code
   static {
      isActive = false;
   }
}
