package Myaven.module.modules.player;

import Myaven.Myaven;
import Myaven.events.PlayerUpdateEvent;
import Myaven.events.Render2DEvent;
import Myaven.module.Category;
import Myaven.module.Module;
import Myaven.setting.Setting;
import Myaven.setting.settings.BooleanSetting;
import Myaven.setting.settings.DescriptionSetting;
import Myaven.setting.settings.ModeSetting;
import Myaven.setting.settings.SliderSetting;
import Myaven.util.PacketUtil;
import Myaven.util.TimerUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class Blink extends Module {
   public static DescriptionSetting modeDescription;
   public static ModeSetting mode;
   public static SliderSetting pulseTicks;
   public static BooleanSetting showDelay;
   public static BooleanSetting autoDisable;
   public static SliderSetting autoDisableTicks;
   private TimerUtil pulseTimer = new TimerUtil();
   private TimerUtil overlayStartTimer = new TimerUtil();
   private long overlayStartTime = System.currentTimeMillis();
   private boolean blinkingActive = false;
   private boolean overlayWaiting = true;
   private long lastToggleTime = System.currentTimeMillis();
   private boolean enabledFlag = false;
   private static String[] a;
   private static String[] j;
   private static long[] k;
   private static Integer[] n;
   private static long[] o;
   private static Long[] p;

   public Blink() {
      super("Blink", false, Category.Player, true, "Stop outgoing packet and release them at one time");
      this.addSettings(new Setting[]{modeDescription, mode, pulseTicks, showDelay, autoDisable, autoDisableTicks});
   }

   @Override
   public String getSuffix() {
      return mode.getCurrent();
   }

   @Override
   public void onEnable() {
      if (!this.enabledFlag) {
         this.lastToggleTime = System.currentTimeMillis();
      }

      this.enabledFlag = true;
   }

   @SubscribeEvent
   public void onUpdate(PlayerUpdateEvent event) {
      if (autoDisable.getState() && (double)(System.currentTimeMillis() - this.lastToggleTime) > autoDisableTicks.getRoundedValue() * 50.0) {
         Myaven.moduleManager.getModule("Blink").toggle();
      }

      String var3 = mode.getCurrent();
      switch (var3) {
         case "NORMAL":
            PacketUtil.setCancelOutgoing(true);
            this.blinkingActive = true;
            break;
         case "PULSE":
            if (!this.blinkingActive) {
               PacketUtil.setCancelOutgoing(true);
               this.blinkingActive = true;
            }

            if (this.pulseTimer.hasTimePassed((long)pulseTicks.getRoundedValue() * 50L, true)) {
               PacketUtil.flushPendingPackets();
               PacketUtil.setCancelOutgoing(false);
               this.blinkingActive = false;
            }
      }
   }

   @SubscribeEvent
   public void onRender(Render2DEvent event) {
      if (showDelay.getState() && this.blinkingActive) {
         if (this.overlayWaiting && this.overlayStartTimer.hasTimePassed(10L, true)) {
            this.overlayStartTime = System.currentTimeMillis();
            this.overlayWaiting = false;
         }

         ScaledResolution scaledResolution = new ScaledResolution(Minecraft.getMinecraft());
         FontRenderer font = Minecraft.getMinecraft().fontRendererObj;
         String show = String.format("%.1f", (double)(System.currentTimeMillis() - this.overlayStartTime) / 1000.0);
         font.drawStringWithShadow(
            show,
            (float)(scaledResolution.getScaledWidth() / 2 - font.getStringWidth(show) / 2),
            (float)(scaledResolution.getScaledHeight() / 2 + 75),
            16777215
         );
      } else {
         this.overlayWaiting = true;
      }
   }

   @Override
   public void onDisable() {
      this.overlayWaiting = true;
      if (this.blinkingActive) {
         PacketUtil.flushPendingPackets();
         PacketUtil.setCancelOutgoing(false);
         this.blinkingActive = false;
      }

      if (this.enabledFlag) {
         this.lastToggleTime = System.currentTimeMillis();
         PacketUtil.flushPendingPackets();
         PacketUtil.setCancelOutgoing(false);
         this.blinkingActive = false;
      }

      this.enabledFlag = false;
   }

   // $VF: Irreducible bytecode was duplicated to produce valid code
   static {
      modeDescription = new DescriptionSetting("NORMAL, PULSE");
      mode = new ModeSetting("Mode", "NORMAL", "PULSE");
      pulseTicks = new SliderSetting("Pulse-ticks", 20.0, 1.0, 100.0, 1.0);
      showDelay = new BooleanSetting("Show-delay", true);
      autoDisable = new BooleanSetting("Auto-disable", true);
      autoDisableTicks = new SliderSetting("Auto-disable-ticks", 20.0, 1.0, 100.0, 1.0);
   }
}
