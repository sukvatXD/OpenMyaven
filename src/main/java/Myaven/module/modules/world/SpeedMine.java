package Myaven.module.modules.world;

import Myaven.mixins.accessor.AccessorPlayerController;
import Myaven.module.Category;
import Myaven.module.Module;
import Myaven.setting.Setting;
import Myaven.setting.settings.DescriptionSetting;
import Myaven.setting.settings.ModeSetting;
import Myaven.setting.settings.PercentageSetting;
import Myaven.setting.settings.SliderSetting;
import Myaven.util.BlockUtil;
import Myaven.util.ClientUtil;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import org.lwjgl.input.Mouse;

public class SpeedMine extends Module {
   public static DescriptionSetting modeDescription;
   public static ModeSetting mode;
   public static SliderSetting delay;
   public static PercentageSetting speed;
   private float progress;

   public SpeedMine() {
      super("SpeedMine", false, Category.World, true, "Increase your mining speed");
      this.addSettings(new Setting[]{modeDescription, mode, delay, speed});
   }

   @Override
   public String getSuffix() {
      if (speed.getPercentage() != 0) {
         return speed.getPercentage() + 100 + "%";
      } else {
         return delay.getRoundedValue() != 5.0 ? (int)delay.getRoundedValue() + " delay" : "";
      }
   }

   @SubscribeEvent
   public void onTick(ClientTickEvent event) {
      if (event.phase == Phase.END) {
         if (this.mc.inGameHasFocus && ClientUtil.isWorldLoaded()) {
            int delay = (int)SpeedMine.delay.getRoundedValue();
            if ((double)delay < 5.0) {
               if ((double)delay == 0.0) {
                  ((AccessorPlayerController)this.mc.playerController).setBlockHitDelay(0);
               } else if (((AccessorPlayerController)this.mc.playerController).getBlockHitDelay() > delay) {
                  ((AccessorPlayerController)this.mc.playerController).setBlockHitDelay(delay);
               }
            }

            double c = 1.0 + (double)speed.getPercentage() / 100.0;
            if (c > 1.0) {
               if (!this.mc.thePlayer.capabilities.isCreativeMode && Mouse.isButtonDown(0)) {
                  float float1 = ((AccessorPlayerController)this.mc.playerController).getCurBlockDamageMP();
                  String var7 = mode.getCurrent();
                  switch (var7) {
                     case "PRE":
                        float n = (float)(1.0 - 1.0 / c);
                        if (float1 > 0.0F && float1 < n) {
                           ((AccessorPlayerController)this.mc.playerController).setCurBlockDamageMP(n);
                           break;
                        }
                     case "POST":
                        double n2 = 1.0 / c;
                        if (float1 < 1.0F && (double)float1 >= n2) {
                           ((AccessorPlayerController)this.mc.playerController).setCurBlockDamageMP(1.0F);
                        }
                        break;
                     case "INCREASE":
                        float n3 = -1.0F;
                        if (float1 < 1.0F) {
                           if (this.mc.objectMouseOver != null && float1 > this.progress) {
                              n3 = (float)(
                                 (double)this.progress
                                    + (double)BlockUtil.getBlockBreakingSpeed(
                                          this.mc.theWorld.getBlockState(this.mc.objectMouseOver.getBlockPos()).getBlock(),
                                          this.mc.thePlayer.inventory.getStackInSlot(this.mc.thePlayer.inventory.currentItem),
                                          false,
                                          false
                                       )
                                       * (c - 0.2152857 * (c - 1.0))
                              );
                           }

                           if (n3 != -1.0F && float1 > 0.0F) {
                              ((AccessorPlayerController)this.mc.playerController).setCurBlockDamageMP(n3);
                           }
                        }

                        this.progress = float1;
                  }
               } else if (mode.getCurrent().equals("INCREASE")) {
                  this.progress = 0.0F;
               }
            }
         }
      }
   }

   // $VF: Irreducible bytecode was duplicated to produce valid code
   static {
      modeDescription = new DescriptionSetting("POST, PRE, INCREASE");
      mode = new ModeSetting("Mode", "POST", "PRE", "INCREASE");
      delay = new SliderSetting("Delay", 0.0, 0.0, 5.0, 1.0);
      speed = new PercentageSetting("Increase-speed", 10);
   }
}
