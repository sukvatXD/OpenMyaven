package Myaven.module.modules.player;

import Myaven.events.PreMotionEvent;
import Myaven.mixins.accessor.AccessorTimer;
import Myaven.module.Category;
import Myaven.module.Module;
import Myaven.setting.Setting;
import Myaven.setting.settings.BooleanSetting;
import Myaven.setting.settings.DescriptionSetting;
import Myaven.setting.settings.ModeSetting;
import Myaven.setting.settings.SliderSetting;
import Myaven.util.ClientUtil;
import Myaven.util.TimerUtil;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class NoFall extends Module {
   public static DescriptionSetting modeDescription1;
   public static DescriptionSetting modeDescription2;
   public static ModeSetting mode;
   public static SliderSetting fallDistanceThreshold;
   public static SliderSetting timerSpeed;
   public static SliderSetting groundSpoofTicks;
   public static BooleanSetting alwaysGroundSpoof;
   private TimerUtil spoofTimer = new TimerUtil();
   private boolean isGroundSpoofing = false;
   private boolean preventDoubleSpoof = false;
   private boolean wasOnGround = false;
   private boolean justLanded = false;
   private boolean hasJumped = false;
   private float originalTimerSpeed;
   private boolean isTimerSpeedSaved = false;

   public NoFall() {
      super("NoFall", false, Category.Player, true, "Decrease fall damage", false, false);
      this.addSettings(new Setting[]{modeDescription1, modeDescription2, mode, fallDistanceThreshold, timerSpeed, groundSpoofTicks, alwaysGroundSpoof});
   }

   @SubscribeEvent
   public void onMotion(PreMotionEvent event) {
      if (event.isOnGround() && !this.wasOnGround) {
         this.justLanded = true;
      } else {
         this.justLanded = false;
      }

      String var3 = mode.getCurrent();
      switch (var3) {
         case "NO_GROUND":
            if (alwaysGroundSpoof.getState()) {
               event.setGround(false);
            } else {
               if (this.justLanded && ClientUtil.getFallDistance(this.mc.thePlayer) >= fallDistanceThreshold.getRoundedValue() && !this.isGroundSpoofing) {
                  this.spoofTimer.reset();
                  this.isGroundSpoofing = true;
               } else {
                  this.isGroundSpoofing = false;
               }

               if (this.isGroundSpoofing) {
                  if (!this.spoofTimer.hasTimePassed((long)groundSpoofTicks.getRoundedValue() * 50L)) {
                     event.setGround(false);
                  } else {
                     this.isGroundSpoofing = false;
                  }
               }
            }
            break;
         case "ON_GROUND":
            if (alwaysGroundSpoof.getState()) {
               event.setGround(true);
            } else {
               if (!event.isOnGround()
                  && ClientUtil.getFallDistance(this.mc.thePlayer) >= fallDistanceThreshold.getRoundedValue()
                  && !this.isGroundSpoofing
                  && !this.preventDoubleSpoof) {
                  event.setGround(true);
                  this.spoofTimer.reset();
                  this.isGroundSpoofing = true;
               } else {
                  this.isGroundSpoofing = false;
                  this.preventDoubleSpoof = false;
               }

               if (this.isGroundSpoofing) {
                  if (!this.spoofTimer.hasTimePassed((long)groundSpoofTicks.getRoundedValue() * 50L)) {
                     event.setGround(true);
                  } else {
                     this.isGroundSpoofing = false;
                  }
               }
            }
            break;
         case "JUMP":
            if (ClientUtil.getFallDistance(this.mc.thePlayer) >= fallDistanceThreshold.getRoundedValue() && !event.isOnGround()) {
               if (this.justLanded && !this.hasJumped) {
                  event.setGround(false);
                  this.mc.thePlayer.jump();
                  event.setGround(true);
                  this.spoofTimer.reset();
                  this.hasJumped = true;
               } else {
                  event.setGround(true);
               }
            }

            if (event.isOnGround() && !this.justLanded) {
               this.hasJumped = false;
            }
            break;
         case "TIMER":
            if (!this.isTimerSpeedSaved) {
               this.isTimerSpeedSaved = true;
               this.originalTimerSpeed = ((AccessorTimer)this.mc).getTimer().timerSpeed;
            }

            if (ClientUtil.getFallDistance(this.mc.thePlayer) >= fallDistanceThreshold.getRoundedValue() && !event.isOnGround()) {
               ((AccessorTimer)this.mc).getTimer().timerSpeed = (float)timerSpeed.getRoundedValue();
               event.setGround(true);
            } else {
               this.isTimerSpeedSaved = false;
               ((AccessorTimer)this.mc).getTimer().timerSpeed = this.originalTimerSpeed;
            }
      }

      this.wasOnGround = event.isOnGround();
   }

   @Override
   public void onDisable() {
      if (this.isTimerSpeedSaved) {
         ((AccessorTimer)this.mc).getTimer().timerSpeed = this.originalTimerSpeed;
         this.isTimerSpeedSaved = false;
      }

      this.preventDoubleSpoof = false;
      this.isGroundSpoofing = false;
   }

   // $VF: Irreducible bytecode was duplicated to produce valid code
   static {
      modeDescription1 = new DescriptionSetting("NO_GROUND, ON_GROUND,");
      modeDescription2 = new DescriptionSetting("JUMP, TIMER");
      mode = new ModeSetting("Mode", "NO_GROUND", "ON_GROUND", "JUMP", "TIMER");
      fallDistanceThreshold = new SliderSetting("Fall-distance", 3.0, 0.0, 20.0, 1.0);
      timerSpeed = new SliderSetting("Timer-speed", 0.7, 0.1, 3.0, 0.1);
      groundSpoofTicks = new SliderSetting("Ground-spoof-ticks", 0.0, 0.0, 100.0, 1.0);
      alwaysGroundSpoof = new BooleanSetting("Always-ground-spoof", true);
   }

}
