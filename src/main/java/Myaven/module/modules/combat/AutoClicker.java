package Myaven.module.modules.combat;

import Myaven.events.PlayerUpdateEvent;
import Myaven.mixins.accessor.AccessorKeybinding;
import Myaven.module.Category;
import Myaven.module.Module;
import Myaven.setting.Setting;
import Myaven.setting.settings.BooleanSetting;
import Myaven.setting.settings.SliderSetting;
import Myaven.util.ClientUtil;
import Myaven.util.ItemUtil;
import Myaven.util.TimerUtil;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.item.ItemSword;
import net.minecraft.util.MovingObjectPosition.MovingObjectType;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

public class AutoClicker extends Module {
   public static SliderSetting minCPS;
   public static SliderSetting maxCPS;
   public static SliderSetting sagBlockingTicks;
   public static SliderSetting sagUnblockDuration;
   public static BooleanSetting breakBlocks;
   public static BooleanSetting inventory;
   public static BooleanSetting sag;
   private TimerUtil clickTimer;
   private TimerUtil releaseTimer;
   private TimerUtil resetTimer;
   private boolean isAttacking;
   private long G;
   private static String[] a;
   private static String[] b;
   private static long[] i;
   private static Integer[] j;

   public AutoClicker() {
      super("AutoClicker", false, Category.Combat, true, "Automatically left click");
      this.clickTimer = new TimerUtil();
      this.releaseTimer = new TimerUtil();
      this.resetTimer = new TimerUtil();
      this.isAttacking = false;
      this.G = ClientUtil.getDelayInMillis(minCPS.getRoundedValue(), maxCPS.getRoundedValue());
      this.addSettings(new Setting[]{minCPS, maxCPS, sagBlockingTicks, sagUnblockDuration, breakBlocks, inventory, sag});
   }

   @Override
   public String getSuffix() {
      if (minCPS.getRoundedValue() != maxCPS.getRoundedValue()) {
         return (double)Math.round(minCPS.getRoundedValue()) == minCPS.getRoundedValue()
               && (double)Math.round(maxCPS.getRoundedValue()) == maxCPS.getRoundedValue()
            ? (int)Math.round(minCPS.getRoundedValue()) + "-" + (int)Math.round(maxCPS.getRoundedValue())
            : minCPS.getRoundedValue() + "-" + maxCPS.getRoundedValue();
      } else {
         return (double)Math.round(maxCPS.getRoundedValue()) == maxCPS.getRoundedValue()
            ? String.valueOf((int)Math.round(maxCPS.getRoundedValue()))
            : minCPS.getRoundedValue() + "-" + maxCPS.getRoundedValue();
      }
   }

   @Override
   public void onDisable() {
      this.isAttacking = false;
   }

   @SubscribeEvent
   public void onClientTick(ClientTickEvent event) {
      if (!KillAura.combatTick && Mouse.isButtonDown(0) && this.mc.currentScreen == null) {
         if (breakBlocks.getState()
            && this.mc.objectMouseOver != null
            && this.mc.objectMouseOver.typeOfHit == MovingObjectType.BLOCK
            && this.mc.objectMouseOver.entityHit == null) {
            KeyBinding.setKeyBindState(this.mc.gameSettings.keyBindAttack.getKeyCode(), true);
            this.isAttacking = false;
         } else if (sag.getState()
            && Mouse.isButtonDown(1)
            && this.mc.thePlayer.getHeldItem() != null
            && this.mc.thePlayer.getHeldItem().getItem() instanceof ItemSword) {
            if (this.isAttacking && this.releaseTimer.hasTimePassed(sagUnblockDuration.getRoundedValue() * 50.0)) {
               KeyBinding.setKeyBindState(this.mc.gameSettings.keyBindAttack.getKeyCode(), false);
               KeyBinding.setKeyBindState(this.mc.gameSettings.keyBindAttack.getKeyCode(), true);
               KeyBinding.onTick(this.mc.gameSettings.keyBindAttack.getKeyCode());
               KeyBinding.setKeyBindState(this.mc.gameSettings.keyBindAttack.getKeyCode(), false);
               ((AccessorKeybinding)this.mc.gameSettings.keyBindUseItem).setPressed(true);
               this.resetTimer.reset();
               this.isAttacking = false;
            } else if (!this.isAttacking && this.resetTimer.hasTimePassed(sagBlockingTicks.getRoundedValue() * 50.0)) {
               ((AccessorKeybinding)this.mc.gameSettings.keyBindUseItem).setPressed(false);
               this.releaseTimer.reset();
               this.isAttacking = true;
            }
         } else {
            this.isAttacking = false;
            if (this.clickTimer.hasTimePassed(this.G, true)) {
               this.G = ClientUtil.getDelayInMillis(minCPS.getRoundedValue(), maxCPS.getRoundedValue());
               KeyBinding.setKeyBindState(this.mc.gameSettings.keyBindAttack.getKeyCode(), false);
               KeyBinding.setKeyBindState(this.mc.gameSettings.keyBindAttack.getKeyCode(), true);
               KeyBinding.onTick(this.mc.gameSettings.keyBindAttack.getKeyCode());
               KeyBinding.setKeyBindState(this.mc.gameSettings.keyBindAttack.getKeyCode(), false);
            }
         }
      } else {
         this.isAttacking = false;
      }
   }

   @SubscribeEvent
   public void onPlayerUpdate(PlayerUpdateEvent event) {
      if (Mouse.isButtonDown(0)
         && this.mc.currentScreen instanceof GuiContainer
         && Keyboard.isKeyDown(this.mc.gameSettings.keyBindSneak.getKeyCode())
         && inventory.getState()) {
         ItemUtil.simulateClick(this.mc.currentScreen);
      }
   }

   // $VF: Irreducible bytecode was duplicated to produce valid code
   static {
      minCPS = new SliderSetting("MinCPS", 13.0, 1.0, 20.0, 0.1);
      maxCPS = new SliderSetting("MaxCPS", 15.0, 1.0, 20.0, 0.1);
      sagBlockingTicks = new SliderSetting("Sag-blocking-ticks", 2.0, 0.1, 20.0, 0.1);
      sagUnblockDuration = new SliderSetting("Sag-unblock-duration", 1.0, 0.1, 20.0, 0.1);
      breakBlocks = new BooleanSetting("Break-blocks", true);
      inventory = new BooleanSetting("Inventory", false);
      sag = new BooleanSetting("Sag", false);
   }
}
