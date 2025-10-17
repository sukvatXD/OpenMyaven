package Myaven.module.modules.world;

import Myaven.events.PlayerUpdateEvent;
import Myaven.mixins.accessor.AccessorKeybinding;
import Myaven.module.Category;
import Myaven.module.Module;
import Myaven.setting.Setting;
import Myaven.setting.settings.BooleanSetting;
import Myaven.setting.settings.SliderSetting;
import Myaven.util.ClientUtil;
import Myaven.util.PlayerUtil;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.input.Keyboard;

public class BridgeAssist extends Module {
   public static SliderSetting minDelay;
   public static SliderSetting maxDelay;
   public static BooleanSetting requireSneak;
   public static BooleanSetting requireLookDown;
   public static SliderSetting lookDownAngle;
   public static BooleanSetting autoItem;
   public static BooleanSetting requirePressingS;
   private boolean hasModifiedSneakKeyState = false;
   private int previousHotbarSlotIndex;
   private boolean hasSwappedHotbarSlot = false;
   private long lastSneakPressTimeMs = -1L;
   private long currentSneakHoldDelayMs;

   public BridgeAssist() {
      super("BridgeAssist", false, Category.World, true, "Sneak when you get close to the edge of the blocks");
      this.currentSneakHoldDelayMs = ClientUtil.getRandomDoubleInMillis(minDelay.getRoundedValue(), maxDelay.getRoundedValue());
      this.addSettings(new Setting[]{minDelay, maxDelay, requireSneak, requireLookDown, lookDownAngle, autoItem, requirePressingS});
   }

   @Override
   public String getSuffix() {
      return minDelay.getRoundedValue() == maxDelay.getRoundedValue()
         ? String.valueOf((int)minDelay.getRoundedValue())
         : (int)minDelay.getRoundedValue() + "-" + (int)maxDelay.getRoundedValue();
   }

   @SubscribeEvent
   public void onUpdate(PlayerUpdateEvent event) {
      this.hasModifiedSneakKeyState = true;
      if (autoItem.getState() && (this.mc.thePlayer.getHeldItem() == null || !(this.mc.thePlayer.getHeldItem().getItem() instanceof ItemBlock))) {
         if (!this.hasSwappedHotbarSlot) {
            this.previousHotbarSlotIndex = this.mc.thePlayer.inventory.currentItem;
            this.hasSwappedHotbarSlot = true;
         }

         boolean found = false;

         for (int i = 0; i < InventoryPlayer.getHotbarSize(); i++) {
            if (this.mc.thePlayer.inventory.getStackInSlot(i) != null && this.mc.thePlayer.inventory.getStackInSlot(i).getItem() instanceof ItemBlock && !found
               )
             {
               this.mc.thePlayer.inventory.currentItem = i;
               found = true;
            }
         }
      }

      if ((requireSneak.getState() && Keyboard.isKeyDown(this.mc.gameSettings.keyBindSneak.getKeyCode()) || !requireSneak.getState())
         && (requirePressingS.getState() && this.mc.gameSettings.keyBindBack.isKeyDown() || !requirePressingS.getState())
         && (requireLookDown.getState() && (double)this.mc.thePlayer.rotationPitch >= lookDownAngle.getRoundedValue() || !requireLookDown.getState())) {
         long currentTime = System.currentTimeMillis();
         if (!ClientUtil.isInWorld() && !ClientUtil.isPlayerOnGround() || !PlayerUtil.isMoving() && ClientUtil.isJumping()) {
            if (this.lastSneakPressTimeMs != -1L && currentTime - this.lastSneakPressTimeMs > this.currentSneakHoldDelayMs) {
               this.currentSneakHoldDelayMs = ClientUtil.getRandomDoubleInMillis(minDelay.getRoundedValue(), maxDelay.getRoundedValue());
               ((AccessorKeybinding)this.mc.gameSettings.keyBindSneak).setPressed(false);
               KeyBinding.setKeyBindState(this.mc.gameSettings.keyBindSneak.getKeyCode(), false);
               this.lastSneakPressTimeMs = -1L;
            }
         } else {
            ((AccessorKeybinding)this.mc.gameSettings.keyBindSneak).setPressed(true);
            KeyBinding.setKeyBindState(this.mc.gameSettings.keyBindSneak.getKeyCode(), true);
            this.lastSneakPressTimeMs = currentTime;
         }
      }
   }

   @Override
   public void onDisable() {
      this.lastSneakPressTimeMs = -1L;
      if (this.hasModifiedSneakKeyState) {
         KeyBinding.setKeyBindState(this.mc.gameSettings.keyBindSneak.getKeyCode(), Keyboard.isKeyDown(this.mc.gameSettings.keyBindSneak.getKeyCode()));
         ((AccessorKeybinding)this.mc.gameSettings.keyBindSneak).setPressed(Keyboard.isKeyDown(this.mc.gameSettings.keyBindSneak.getKeyCode()));
         this.hasModifiedSneakKeyState = false;
      }

      if (this.hasSwappedHotbarSlot) {
         this.mc.thePlayer.inventory.currentItem = this.previousHotbarSlotIndex;
         this.hasSwappedHotbarSlot = false;
      }
   }

   // $VF: Irreducible bytecode was duplicated to produce valid code
   static {

      minDelay = new SliderSetting("Min-delay", 100.0, 1.0, 300.0, 1.0);
      maxDelay = new SliderSetting("Max-delay", 200.0, 1.0, 300.0, 1.0);
      requireSneak = new BooleanSetting("Require-sneak", false);
      requireLookDown = new BooleanSetting("Require-looking-down", false);
      lookDownAngle = new SliderSetting("Look-down-Angle", 70.0, 0.0, 90.0, 1.0);
      autoItem = new BooleanSetting("Auto-item", false);
      requirePressingS = new BooleanSetting("Require-pressing-S", false);
   }
}
