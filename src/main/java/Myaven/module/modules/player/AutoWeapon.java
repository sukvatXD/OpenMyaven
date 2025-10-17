package Myaven.module.modules.player;

import Myaven.events.PlayerUpdateEvent;
import Myaven.module.Category;
import Myaven.module.Module;
import Myaven.setting.Setting;
import Myaven.setting.settings.BooleanSetting;
import Myaven.util.ItemUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.input.Mouse;

public class AutoWeapon extends Module {
   public static BooleanSetting allowAxeAsWeapon;
   public static BooleanSetting allowStickAsWeapon;
   public static BooleanSetting allowRodAsWeapon;
   private static String[] R;
   private static String[] a;
   private static String[] d;

   public AutoWeapon() {
      super("AutoWeapon", false, Category.Player, true, "Switch to the best weapon in hotbar during combat");
      this.addSettings(new Setting[]{allowAxeAsWeapon, allowStickAsWeapon, allowRodAsWeapon});
   }

   @SubscribeEvent
   public void onPlayerUpdate(PlayerUpdateEvent event) {
      if (this.mc.objectMouseOver.entityHit != null && Mouse.isButtonDown(0)) {
         this.mc.thePlayer.inventory.currentItem = getBestHotbarWeaponSlot();
      }
   }

   public static int getBestHotbarWeaponSlot() {
      Minecraft mc = Minecraft.getMinecraft();
      int weaponIndex = 0;
      double highestPriority = 0.0;

      for (int i = 0; i < InventoryPlayer.getHotbarSize(); i++) {
         ItemStack item = mc.thePlayer.inventory.getStackInSlot(i);
         if (item != null) {
            double priority = 0.0;
            double typePriority = 0.0;
            if (item.getItem() instanceof ItemSword) {
               priority = ItemUtil.getWeaponDamage(item);
               typePriority = 0.4;
            } else if (allowAxeAsWeapon.getState() && item.getItem() instanceof ItemAxe) {
               priority = ItemUtil.getWeaponDamage(item);
               typePriority = 0.3;
            } else if (allowStickAsWeapon.getState() && item.getItem() == Items.stick) {
               priority = ItemUtil.getWeaponDamage(item);
               typePriority = 0.2;
            } else if (allowRodAsWeapon.getState() && item.getItem() == Items.fishing_rod) {
               priority = ItemUtil.getWeaponDamage(item);
               typePriority = 0.1;
            }

            priority += typePriority;
            if (priority > highestPriority) {
               highestPriority = priority;
               weaponIndex = i;
            }
         }
      }

      return weaponIndex;
   }

   // $VF: Irreducible bytecode was duplicated to produce valid code
   static {
      allowAxeAsWeapon = new BooleanSetting("Axe-is-weapon", false);
      allowStickAsWeapon = new BooleanSetting("Stick-is-weapon", false);
      allowRodAsWeapon = new BooleanSetting("FishingRod-is-weapon", false);
   }
}
