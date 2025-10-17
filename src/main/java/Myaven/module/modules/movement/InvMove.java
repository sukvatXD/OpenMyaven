package Myaven.module.modules.movement;

import Myaven.Myaven;
import Myaven.events.LivingUpdateEvent;
import Myaven.events.PacketSendEvent;
import Myaven.events.PlayerUpdateEvent;
import Myaven.mixins.accessor.AccessorC0DPacketCloseWindow;
import Myaven.module.Category;
import Myaven.module.Module;
import Myaven.setting.Setting;
import Myaven.setting.settings.BooleanSetting;
import Myaven.setting.settings.DescriptionSetting;
import Myaven.setting.settings.ModeSetting;
import Myaven.ui.ClickGUI;
import Myaven.util.PacketUtil;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.gui.inventory.GuiContainerCreative;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C0DPacketCloseWindow;
import net.minecraft.network.play.client.C0EPacketClickWindow;
import net.minecraft.network.play.client.C16PacketClientStatus;
import net.minecraft.network.play.client.C16PacketClientStatus.EnumState;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

public class InvMove extends Module {
   public static DescriptionSetting description;
   public static ModeSetting mode;
   public static BooleanSetting containerMove;
   public static BooleanSetting inventoryMove;
   public static BooleanSetting clickGuiMove;
   private Queue<C0EPacketClickWindow> clickPacketQueue;
   private boolean isSimulatingMovement;
   private C16PacketClientStatus pendingOpenInventoryPacket;
   private int clickDelayTicks;
   private static String[] a;
   private static String[] b;
   private static long[] d;
   private static Integer[] g;

   public InvMove() {
      super("InvMove", false, Category.Movement, true, "Allows you to move around while opening a container");
      this.clickPacketQueue = new ConcurrentLinkedQueue<>();
      this.isSimulatingMovement = false;
      this.pendingOpenInventoryPacket = null;
      this.clickDelayTicks = 0;
      this.addSettings(new Setting[]{description, mode, containerMove, inventoryMove, clickGuiMove});
   }

   public void applyMovementInputs() {
      KeyBinding[] movementKeys = new KeyBinding[]{
         this.mc.gameSettings.keyBindForward,
         this.mc.gameSettings.keyBindBack,
         this.mc.gameSettings.keyBindLeft,
         this.mc.gameSettings.keyBindRight,
         this.mc.gameSettings.keyBindJump,
         this.mc.gameSettings.keyBindSprint
      };

      for (KeyBinding keyBinding : movementKeys) {
         this.updateKeyState(keyBinding.getKeyCode());
      }

      if (Myaven.moduleManager.getModule("Sprint").isEnabled()) {
         this.setKeyState(this.mc.gameSettings.keyBindSprint.getKeyCode(), true);
      }

      this.isSimulatingMovement = true;
   }

   private boolean shouldSimulateMovement() {
      String var2 = mode.getCurrent();
      switch (var2) {
         case "LEGIT":
            return this.pendingOpenInventoryPacket != null && this.clickPacketQueue.isEmpty() && this.isAllowedGuiOpen();
         case "HYPIXEL":
            return this.clickPacketQueue.isEmpty() && this.isAllowedGuiOpen();
         default:
            return this.isAllowedGuiOpen();
      }
   }

   private boolean isAllowedGuiOpen() {
      if ((this.mc.currentScreen instanceof GuiContainer || this.mc.currentScreen instanceof GuiContainerCreative) && containerMove.getState()) {
         return true;
      } else {
         return this.mc.currentScreen instanceof GuiInventory && inventoryMove.getState()
            ? true
            : this.mc.currentScreen instanceof ClickGUI && clickGuiMove.getState();
      }
   }

   @SubscribeEvent
   public void onLivingUpdate(LivingUpdateEvent event) {
      while (!this.clickPacketQueue.isEmpty()) {
         PacketUtil.sendPacketNoEvent((Packet<?>)this.clickPacketQueue.poll());
      }
   }

   @SubscribeEvent
   public void onUpdate(PlayerUpdateEvent event) {
      if (this.shouldSimulateMovement() && this.clickDelayTicks == 0) {
         this.applyMovementInputs();
      } else {
         if (this.isSimulatingMovement) {
            if (this.mc.currentScreen != null) {
               KeyBinding.unPressAllKeys();
            }

            this.isSimulatingMovement = false;
         }

         if (this.pendingOpenInventoryPacket != null) {
            PacketUtil.sendPacketNoEvent(this.pendingOpenInventoryPacket);
            this.pendingOpenInventoryPacket = null;
         }

         if (this.clickDelayTicks > 0) {
            this.clickDelayTicks--;
         }
      }
   }

   @SubscribeEvent
   public void onPacket(PacketSendEvent event) {
      if (event.getPacket() instanceof C16PacketClientStatus) {
         if (mode.getCurrent().equals("LEGIT")) {
            C16PacketClientStatus packet = (C16PacketClientStatus)event.getPacket();
            if (packet.getStatus() == EnumState.OPEN_INVENTORY_ACHIEVEMENT) {
               event.setCanceled(true);
               this.pendingOpenInventoryPacket = packet;
            }
         }
      } else if (!(event.getPacket() instanceof C0EPacketClickWindow)) {
         if (event.getPacket() instanceof C0DPacketCloseWindow) {
            C0DPacketCloseWindow var6 = (C0DPacketCloseWindow)event.getPacket();
            if (this.pendingOpenInventoryPacket != null && ((AccessorC0DPacketCloseWindow)var6).getWindowId() == 0) {
               this.pendingOpenInventoryPacket = null;
               event.setCanceled(true);
            }
         }
      } else {
         C0EPacketClickWindow var7 = (C0EPacketClickWindow)event.getPacket();
         String var4 = mode.getCurrent();
         switch (var4) {
            case "LEGIT":
               if (var7.getWindowId() != 0) {
                  break;
               }

               if ((var7.getMode() == 3 || var7.getMode() == 4) && var7.getSlotId() == -999) {
                  event.setCanceled(true);
                  return;
               }

               if (this.pendingOpenInventoryPacket != null) {
                  KeyBinding.unPressAllKeys();
                  event.setCanceled(true);
                  this.clickPacketQueue.offer(var7);
               }
               break;
            case "HYPIXEL":
               if ((var7.getMode() == 3 || var7.getMode() == 4) && var7.getSlotId() == -999) {
                  event.setCanceled(true);
               } else {
                  KeyBinding.unPressAllKeys();
                  event.setCanceled(true);
                  this.clickPacketQueue.offer(var7);
                  this.clickDelayTicks = 8;
               }
         }

         if (this.pendingOpenInventoryPacket != null) {
            PacketUtil.sendPacketNoEvent(this.pendingOpenInventoryPacket);
            this.pendingOpenInventoryPacket = null;
         }
      }
   }

   @Override
   public void onDisable() {
      if (this.isSimulatingMovement) {
         if (this.mc.currentScreen != null) {
            KeyBinding.unPressAllKeys();
         }

         this.isSimulatingMovement = false;
      }

      if (this.pendingOpenInventoryPacket != null) {
         PacketUtil.sendPacketNoEvent(this.pendingOpenInventoryPacket);
         this.pendingOpenInventoryPacket = null;
      }

      this.clickDelayTicks = 0;
   }

   @Override
   public String getSuffix() {
      return mode.getCurrent();
   }

   private void updateKeyState(int keyCode) {
      this.setKeyState(keyCode, keyCode < 0 ? Mouse.isButtonDown(keyCode + 100) : Keyboard.isKeyDown(keyCode));
   }

   private void setKeyState(int keyCode, boolean pressed) {
      KeyBinding.setKeyBindState(keyCode, pressed);
   }

   // $VF: Irreducible bytecode was duplicated to produce valid code
   static {
      description = new DescriptionSetting("LEGIT, VANILLA, HYPIXEL");
      mode = new ModeSetting("Mode", "LEGIT", "VANILLA", "HYPIXEL");
      containerMove = new BooleanSetting("Container", true);
      inventoryMove = new BooleanSetting("Inventory", true);
      clickGuiMove = new BooleanSetting("ClickGUI", true);
   }
}
