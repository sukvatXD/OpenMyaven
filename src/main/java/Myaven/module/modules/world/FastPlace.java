package Myaven.module.modules.world;

import Myaven.events.PlayerUpdateEvent;
import Myaven.events.RightClickEvent;
import Myaven.mixins.accessor.AccessorRightClickDelayTimer;
import Myaven.module.Category;
import Myaven.module.Module;
import Myaven.setting.Setting;
import Myaven.setting.settings.SliderSetting;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemEgg;
import net.minecraft.item.ItemSnowball;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class FastPlace extends Module {
   public static SliderSetting blockDelay;
   public static SliderSetting projectilesDelay;
   private static String[] a;
   private static String[] b;

   public FastPlace() {
      super("FastPlace", false, Category.World, false, "Change the block placing delay when holding RMB");
      this.addSettings(new Setting[]{blockDelay, projectilesDelay});
   }

   @Override
   public String getSuffix() {
      return blockDelay.getRoundedValue() != projectilesDelay.getRoundedValue()
         ? (int)blockDelay.getRoundedValue() + ", " + (int)projectilesDelay.getRoundedValue()
         : String.valueOf((int)blockDelay.getRoundedValue());
   }

   @SubscribeEvent
   public void onUpdate(PlayerUpdateEvent event) {
      if (this.mc.inGameHasFocus) {
         if (Minecraft.getMinecraft().thePlayer.getHeldItem() != null && Minecraft.getMinecraft().thePlayer.getHeldItem().getItem() instanceof ItemBlock) {
            int c = (int)blockDelay.getRoundedValue();
            if (c == 0) {
               ((AccessorRightClickDelayTimer)this.mc).setRightClickDelayTimer(0);
            } else {
               if (c == 4) {
                  return;
               }

               int d = ((AccessorRightClickDelayTimer)this.mc).getRightClickDelayTimer();
               if (d == 4) {
                  ((AccessorRightClickDelayTimer)this.mc).setRightClickDelayTimer(c);
               }
            }
         } else if (Minecraft.getMinecraft().thePlayer.getHeldItem() != null
            && (
               Minecraft.getMinecraft().thePlayer.getHeldItem().getItem() instanceof ItemSnowball
                  || Minecraft.getMinecraft().thePlayer.getHeldItem().getItem() instanceof ItemEgg
            )) {
            int c = (int)projectilesDelay.getRoundedValue();
            if (c == 0) {
               ((AccessorRightClickDelayTimer)this.mc).setRightClickDelayTimer(0);
            } else {
               if (c == 4) {
                  return;
               }

               int d = ((AccessorRightClickDelayTimer)this.mc).getRightClickDelayTimer();
               if (d == 4) {
                  ((AccessorRightClickDelayTimer)this.mc).setRightClickDelayTimer(c);
               }
            }
         }
      }
   }

   @SubscribeEvent
   public void onClick(RightClickEvent event) {
      if (Minecraft.getMinecraft().thePlayer.getHeldItem() != null && Minecraft.getMinecraft().thePlayer.getHeldItem().getItem() instanceof ItemBlock) {
         int c = (int)blockDelay.getRoundedValue();
         if (c == 0) {
            ((AccessorRightClickDelayTimer)this.mc).setRightClickDelayTimer(0);
         }
      } else if (Minecraft.getMinecraft().thePlayer.getHeldItem() != null
         && (
            Minecraft.getMinecraft().thePlayer.getHeldItem().getItem() instanceof ItemSnowball
               || Minecraft.getMinecraft().thePlayer.getHeldItem().getItem() instanceof ItemEgg
         )) {
         int c = (int)projectilesDelay.getRoundedValue();
         if (c == 0) {
            ((AccessorRightClickDelayTimer)this.mc).setRightClickDelayTimer(0);
         }
      }
   }

   // $VF: Irreducible bytecode was duplicated to produce valid code
   static {
      blockDelay = new SliderSetting("Block-Delay", 1.0, 0.0, 4.0, 1.0);
      projectilesDelay = new SliderSetting("Projectiles-Delay", 1.0, 0.0, 4.0, 1.0);
   }
}
