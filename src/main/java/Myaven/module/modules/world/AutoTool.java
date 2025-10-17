package Myaven.module.modules.world;

import Myaven.events.PlayerUpdateEvent;
import Myaven.module.Category;
import Myaven.module.Module;
import Myaven.module.modules.player.AutoWeapon;
import Myaven.setting.Setting;
import Myaven.setting.settings.BooleanSetting;
import Myaven.setting.settings.SliderSetting;
import Myaven.util.ItemUtil;
import Myaven.util.TimerUtil;
import net.minecraft.block.Block;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MovingObjectPosition.MovingObjectType;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.input.Mouse;

public class AutoTool extends Module {
   public static SliderSetting delay;
   public static BooleanSetting H;
   public static BooleanSetting m;
   public static BooleanSetting T;
   private TimerUtil s = new TimerUtil();
   private boolean x = false;
   private int M = -1;
   private boolean r = false;

   public AutoTool() {
      super("AutoTool", false, Category.World, true, "Switch to the right tools when you are mining");
      this.addSettings(new Setting[]{delay, H, m, T});
   }

   @Override
   public String getSuffix() {
      return String.valueOf((int)delay.getRoundedValue());
   }

   @SubscribeEvent
   public void B(PlayerUpdateEvent event) {
      if (Mouse.isButtonDown(0)) {
         if (this.mc.objectMouseOver != null && this.mc.objectMouseOver.typeOfHit == MovingObjectType.BLOCK && !this.r) {
            this.r = true;
            this.s.reset();
         }

         if (this.s.hasTimePassed((long)delay.getRoundedValue(), true) && this.r && (H.getState() && this.mc.thePlayer.isSneaking() || !H.getState())) {
            if (!this.r) {
               return;
            }

            BlockPos pos = this.mc.objectMouseOver.getBlockPos();
            if (pos == null) {
               return;
            }

            Block block = this.mc.theWorld.getBlockState(pos).getBlock();
            if (block == null || block == Blocks.air) {
               return;
            }

            if (this.d(block) == -1) {
               return;
            }

            if (!this.x) {
               this.M = this.mc.thePlayer.inventory.currentItem;
            }

            this.mc.thePlayer.inventory.currentItem = this.d(this.mc.theWorld.getBlockState(this.mc.objectMouseOver.getBlockPos()).getBlock());
            this.x = true;
            this.r = false;
         }
      } else {
         this.r = false;
         if (T.getState() && this.x) {
            if (AutoWeapon.getBestHotbarWeaponSlot() != -1) {
               this.mc.thePlayer.inventory.currentItem = AutoWeapon.getBestHotbarWeaponSlot();
            } else if (this.M != -1) {
               this.mc.thePlayer.inventory.currentItem = this.M;
            }

            this.x = false;
            this.M = -1;
         } else if (m.getState() && this.x && this.M != -1) {
            this.mc.thePlayer.inventory.currentItem = this.M;
            this.x = false;
            this.M = -1;
         }
      }
   }

   @Override
   public void onDisable() {
      this.r = false;
      this.M = -1;
   }

   public int d(Block block) {
      float n = 1.0F;
      int n2 = -1;

      for (int i = 0; i < InventoryPlayer.getHotbarSize(); i++) {
         ItemStack getStackInSlot = this.mc.thePlayer.inventory.getStackInSlot(i);
         if (getStackInSlot != null) {
            float a = ItemUtil.getBlockBreakingSpeed(getStackInSlot, block);
            if (a > n) {
               n = a;
               n2 = i;
            }
         }
      }

      return n2;
   }

   // $VF: Irreducible bytecode was duplicated to produce valid code
   static {
      delay = new SliderSetting("Delay", 0.0, 0.0, 1000.0, 1.0);
      H = new BooleanSetting("Require-sneak", false);
      m = new BooleanSetting("Switch-back", false);
      T = new BooleanSetting("Switch-back-to-sword", false);
   }
}
