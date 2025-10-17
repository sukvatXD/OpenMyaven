package Myaven.module.modules.visual;

import Myaven.events.PacketReceiveEvent;
import Myaven.events.PreMotionEvent;
import Myaven.events.Render2DEvent;
import Myaven.module.Category;
import Myaven.module.Module;
import Myaven.setting.Setting;
import Myaven.setting.settings.DescriptionSetting;
import Myaven.setting.settings.ModeSetting;
import Myaven.setting.settings.SliderSetting;
import net.minecraft.network.play.server.S03PacketTimeUpdate;
import net.minecraft.network.play.server.S2BPacketChangeGameState;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.jetbrains.annotations.NotNull;

public class Abmience extends Module {
   public static DescriptionSetting modeDescription;
   public static ModeSetting mode;
   public static SliderSetting time;
   public static SliderSetting speed;
   public Abmience() {
      super("Ambience", false, Category.Visual, false, "Change the environment rendering");
      this.addSettings(new Setting[]{modeDescription, mode, time, speed});
   }

   @Override
   public void onDisable() {
      this.reset();
   }

   private void reset() {
      this.mc.theWorld.setRainStrength(0.0F);
      this.mc.theWorld.getWorldInfo().setCleanWeatherTime(Integer.MAX_VALUE);
      this.mc.theWorld.getWorldInfo().setRainTime(0);
      this.mc.theWorld.getWorldInfo().setThunderTime(0);
      this.mc.theWorld.getWorldInfo().setRaining(false);
      this.mc.theWorld.getWorldInfo().setThundering(false);
   }

   @SubscribeEvent
   public void onUpdate(PreMotionEvent event) {
      if (this.mc.thePlayer.ticksExisted % 20 == 0) {
         String var3 = mode.getCurrent();
         switch (var3) {
            case "CLEAR":
               this.reset();
               break;
            case "RAIN":
               this.mc.theWorld.setRainStrength(1.0F);
               this.mc.theWorld.getWorldInfo().setCleanWeatherTime(0);
               this.mc.theWorld.getWorldInfo().setRainTime(Integer.MAX_VALUE);
               this.mc.theWorld.getWorldInfo().setThunderTime(Integer.MAX_VALUE);
               this.mc.theWorld.getWorldInfo().setRaining(true);
               this.mc.theWorld.getWorldInfo().setThundering(false);
         }
      }
   }

   @SubscribeEvent
   public void onPacket(@NotNull PacketReceiveEvent event) {
      if (event.getPacket() instanceof S03PacketTimeUpdate) {
         event.setCanceled(true);
      } else if (event.getPacket() instanceof S2BPacketChangeGameState && !mode.getCurrent().equalsIgnoreCase("NONE")) {
         S2BPacketChangeGameState s2b = (S2BPacketChangeGameState)event.getPacket();
         if (s2b.getGameState() == 1 || s2b.getGameState() == 2) {
            event.setCanceled(true);
         }
      }
   }

   @SubscribeEvent
   public void onRender(Render2DEvent event) {
      this.mc.theWorld.setWorldTime((long)(time.getRoundedValue() + (double)System.currentTimeMillis() * speed.getRoundedValue()));
   }

   // $VF: Irreducible bytecode was duplicated to produce valid code
   static {
      modeDescription = new DescriptionSetting("NONE, RAIN, CLEAR");
      mode = new ModeSetting("Mode", "NONE", "RAIN", "CLEAR");
      time = new SliderSetting("Time", 0.0, 0.0, 24000.0, 10.0);
      speed = new SliderSetting("Speed", 0.0, 0.0, 100.0, 1.0);
   }
}
