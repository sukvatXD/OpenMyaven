package Myaven.module.modules.combat;

import Myaven.Myaven;
import Myaven.events.AttackEvent;
import Myaven.events.PlayerUpdateEvent;
import Myaven.module.Category;
import Myaven.module.Module;
import Myaven.module.modules.misc.AntiBot;
import Myaven.setting.Setting;
import Myaven.setting.settings.BooleanSetting;
import Myaven.setting.settings.DescriptionSetting;
import Myaven.setting.settings.ModeSetting;
import Myaven.setting.settings.SliderSetting;
import Myaven.util.ClientUtil;
import Myaven.util.PacketUtil;
import Myaven.util.RotationUtil;
import Myaven.util.TargetUtil;
import Myaven.util.TimerUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemEgg;
import net.minecraft.item.ItemFishingRod;
import net.minecraft.item.ItemSnowball;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.client.C09PacketHeldItemChange;
import net.minecraft.util.MovingObjectPosition.MovingObjectType;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class AutoProjectiles extends Module {
   public static DescriptionSetting modeDescription;
   public static ModeSetting mode;
   public static SliderSetting disableRange;
   public static SliderSetting attackRange;
   public static SliderSetting holdItemDelay;
   public static SliderSetting throwInterval;
   public static SliderSetting throwAmounts;
   public static BooleanSetting allowAutoBlock;
   public static BooleanSetting onlyUsePacketWhileAutoBlocking;
   private static Minecraft mc;
   public static int currentItemIndex;
   public static TimerUtil actionTimer;
   public static TimerUtil throwTimer;
   public static boolean isThrowing;
   public static boolean isEnabled;
   public static boolean hasThrown;
   public static boolean isThrowingSequence;
   public static long lastThrowTime;
   public static boolean isSwitchingItems;
   public static int lastItemIndex;
   private static String[] g;
   private static String[] k;
   private static long[] m;
   private static Integer[] p;
   private static long[] r;
   private static Long[] t;

   public AutoProjectiles() {
      super("AutoProjectiles", false, Category.Combat, true, "Automatically throw projectiles to the enemy");
      this.addSettings(
         new Setting[]{
            modeDescription, mode, disableRange, attackRange, holdItemDelay, throwInterval, throwAmounts, allowAutoBlock, onlyUsePacketWhileAutoBlocking
         }
      );
   }

   @Override
   public String getSuffix() {
      return mode.getCurrent();
   }

   @SubscribeEvent
   public void on(PlayerUpdateEvent event) {
      
      if (Myaven.moduleManager.getModule("Scaffold").isEnabled()) {
         this.reset();
      } else if (!this.hasAvailableProjectiles()) {
         this.reset();
      } else if (!allowAutoBlock.getState() && KillAura.isAutoBlockActive) {
         this.reset();
      } else {
         if (TargetUtil.getLivingEntitiesInRange(attackRange.getRoundedValue())
            .stream()
            .anyMatch(
               o -> {
                  return RotationUtil.getEntityHitVec(o).distanceTo(ClientUtil.getPlayerEyesPosition()) >= disableRange.getRoundedValue()
                     && TargetUtil.isInFov(o, 160.0)
                     && o instanceof EntityPlayer
                     && !AntiBot.isBot(o);
               }
            )) {
            if (isEnabled && mc.objectMouseOver.typeOfHit != MovingObjectType.BLOCK && throwTimer.hasTimePassed((long)throwInterval.getRoundedValue(), true)) {
               currentItemIndex = mc.thePlayer.inventory.currentItem;
               isThrowing = true;
               isEnabled = false;
               hasThrown = false;
               isThrowingSequence = false;
               actionTimer.reset();
            }

            if (isThrowing) {
               if (!hasThrown && !KillAura.isAutoBlockActive && actionTimer.hasTimePassed(10L)) {
                  hasThrown = true;
                  boolean flag = mode.getCurrent().equalsIgnoreCase("PACKET")
                     && (
                        onlyUsePacketWhileAutoBlocking.getState() && KillAura.isAutoBlockActive
                           || !onlyUsePacketWhileAutoBlocking.getState() && !KillAura.isAutoBlockActive
                     );
                  switchToProjectile(flag);

                  for (int i = 0; (double)i < throwAmounts.getRoundedValue(); i++) {
                     performThrow(flag);
                  }

                  isThrowingSequence = true;
                  lastThrowTime = System.currentTimeMillis();
               }

               if (isThrowingSequence && actionTimer.hasTimePassed((long)holdItemDelay.getRoundedValue() - 20L)) {
                  boolean flag = mode.getCurrent().equalsIgnoreCase("PACKET")
                     && (
                        onlyUsePacketWhileAutoBlocking.getState() && KillAura.isAutoBlockActive
                           || !onlyUsePacketWhileAutoBlocking.getState() && !KillAura.isAutoBlockActive
                     );
                  isThrowingSequence = false;
                  this.restorePreviousItem(flag);
               }

               if (actionTimer.hasTimePassed((long)holdItemDelay.getRoundedValue())) {
                  isThrowing = false;
                  isEnabled = true;
                  throwTimer.reset();
               }
            }
         } else {
            this.reset();
         }
      }
   }

   @SubscribeEvent
   public void onAttack(AttackEvent event) {
      if (isThrowing && !KillAura.isAutoBlockActive) {
         event.setCanceled(true);
      }
   }

   @Override
   public void onDisable() {
      this.reset();
   }

   private void reset() {
      isSwitchingItems = false;
      isEnabled = true;
      isThrowing = false;
      hasThrown = false;
      if (isThrowingSequence && System.currentTimeMillis() - lastThrowTime >= 9L) {
         isThrowingSequence = false;
         this.restorePreviousItem(false);
      }
   }

   private boolean hasAvailableProjectiles() {

      for (int i = 0; i < 9; i++) {
         ItemStack stack = mc.thePlayer.inventory.mainInventory[i];
         if (stack != null && (stack.getItem() instanceof ItemSnowball || stack.getItem() instanceof ItemEgg || stack.getItem() instanceof ItemFishingRod)) {
            return true;
         }
      }

      return false;
   }

   public static void switchToProjectile(boolean packet) {
      for (int i = 0; i < 9; i++) {
         ItemStack stack = mc.thePlayer.inventory.mainInventory[i];
         if (stack != null
            && (stack.getItem() instanceof ItemSnowball || stack.getItem() instanceof ItemEgg || stack.getItem() instanceof ItemFishingRod)
            && i != mc.thePlayer.inventory.currentItem) {
            isSwitchingItems = true;
            if (packet) {
               PacketUtil.sendPacket(new C09PacketHeldItemChange(i));
            } else {
               mc.thePlayer.inventory.currentItem = i;
            }

            lastItemIndex = i;
            return;
         }
      }
   }

   private void restorePreviousItem(boolean packet) {
      if (isSwitchingItems) {
         isSwitchingItems = false;
         if (packet) {
            PacketUtil.sendPacket(new C09PacketHeldItemChange(currentItemIndex));
         } else {
            mc.thePlayer.inventory.currentItem = currentItemIndex;
         }
      }
   }

   public static void performThrow(boolean packet) {
      if (packet) {
         PacketUtil.sendPacket(new C08PacketPlayerBlockPlacement(mc.thePlayer.inventory.mainInventory[lastItemIndex]));
      } else {
         KeyBinding.setKeyBindState(mc.gameSettings.keyBindUseItem.getKeyCode(), true);
         KeyBinding.onTick(mc.gameSettings.keyBindUseItem.getKeyCode());
         KeyBinding.setKeyBindState(mc.gameSettings.keyBindUseItem.getKeyCode(), false);
      }
   }

   // $VF: Irreducible bytecode was duplicated to produce valid code
   static {
      modeDescription = new DescriptionSetting("PACKET, LEGIT");
      mode = new ModeSetting("Mode", "PACKET", "LEGIT");
      disableRange = new SliderSetting("Disable-range", 3.0, 0.0, 12.0, 0.1);
      attackRange = new SliderSetting("Range", 5.0, 0.0, 12.0, 0.1);
      holdItemDelay = new SliderSetting("Hold-item-delay", 100.0, 30.0, 1000.0, 1.0);
      throwInterval = new SliderSetting("Throw-interval", 400.0, 100.0, 2000.0, 1.0);
      throwAmounts = new SliderSetting("Throw-amounts", 1.0, 1.0, 5.0, 1.0);
      allowAutoBlock = new BooleanSetting("Allow-autoblock", true);
      onlyUsePacketWhileAutoBlocking = new BooleanSetting("Only-use-packet-while-autoblocking", true);
      mc = Minecraft.getMinecraft();
      currentItemIndex = 0;
      actionTimer = new TimerUtil();
      throwTimer = new TimerUtil();
      isThrowing = false;
      isEnabled = false;
      hasThrown = false;
      isThrowingSequence = false;
      lastThrowTime = System.currentTimeMillis();
      isSwitchingItems = false;
      lastItemIndex = 0;
   }
}
