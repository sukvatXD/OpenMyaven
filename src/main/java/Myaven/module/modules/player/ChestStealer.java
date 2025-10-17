package Myaven.module.modules.player;

import Myaven.events.PlayerUpdateEvent;
import Myaven.module.Category;
import Myaven.module.Module;
import Myaven.setting.Setting;
import Myaven.setting.settings.BooleanSetting;
import Myaven.setting.settings.SliderSetting;
import Myaven.util.ClientUtil;
import Myaven.util.ItemUtil;
import Myaven.util.TimerUtil;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.resources.I18n;
import net.minecraft.init.Items;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemEgg;
import net.minecraft.item.ItemEnderPearl;
import net.minecraft.item.ItemFishingRod;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemPickaxe;
import net.minecraft.item.ItemPotion;
import net.minecraft.item.ItemSnowball;
import net.minecraft.item.ItemSpade;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.item.ItemTool;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.jetbrains.annotations.NotNull;

public class ChestStealer extends Module {
   public static SliderSetting startDelay;
   public static SliderSetting minClickDelay;
   public static SliderSetting maxClickDelay;
   public static BooleanSetting autoClose;
   public static BooleanSetting ignoreTrash;
   public static BooleanSetting takeArmor;
   public static BooleanSetting takeBlocks;
   public static BooleanSetting takeBow;
   public static BooleanSetting takeFood;
   public static BooleanSetting takePotions;
   public static BooleanSetting takeProjectiles;
   public static BooleanSetting takeSword;
   public static BooleanSetting takeTools;
   private List<Integer> slotsToSteal;
   private boolean isStealing = false;
   private TimerUtil timer;

   public ChestStealer() {
      super("ChestStealer", false, Category.Player, true, "Steal items in the chest automatically");
      this.timer = new TimerUtil();
      this.addSettings(
         new Setting[]{
            startDelay,
            minClickDelay,
            maxClickDelay,
            autoClose,
            ignoreTrash,
            takeArmor,
            takeBlocks,
            takeBow,
            takeFood,
            takePotions,
            takeProjectiles,
            takeSword,
            takeTools
         }
      );
   }

   @Override
   public String getSuffix() {
      return minClickDelay.getRoundedValue() == maxClickDelay.getRoundedValue()
         ? String.valueOf((int)minClickDelay.getRoundedValue())
         : (int)minClickDelay.getRoundedValue() + "-" + (int)maxClickDelay.getRoundedValue();
   }

   @SubscribeEvent
   public void onUpdate(PlayerUpdateEvent event) {
      if (this.mc.thePlayer.openContainer instanceof ContainerChest) {
         if (!this.isStealing) {
            this.isStealing = true;
            if (this.timer.hasTimePassed(10L, true)) {
               String inventoryName = ((ContainerChest)this.mc.thePlayer.openContainer).getLowerChestInventory().getName();
               if (!inventoryName.equals(I18n.format("container.chest", new Object[0]))
                  && !inventoryName.equals(I18n.format("container.chestDouble", new Object[0]))) {
                  return;
               }

               this.slotsToSteal = this.collectStealableSlots();
               if (this.slotsToSteal.isEmpty() || ItemUtil.hasItems()) {
                  if (autoClose.getState()) {
                     this.mc.thePlayer.closeScreen();
                  }

                  this.isStealing = false;
                  return;
               }

               new Thread(() -> {
                   try {
                       Thread.sleep((long)startDelay.getRoundedValue());
                   } catch (InterruptedException e) {
                       throw new RuntimeException(e);
                   }

                   for (Integer integer : this.slotsToSteal) {
                       try {
                           Thread.sleep(ClientUtil.getRandomDoubleInMillis(minClickDelay.getRoundedValue(), maxClickDelay.getRoundedValue()));
                       } catch (InterruptedException e) {
                           throw new RuntimeException(e);
                       }
                       if (this.shouldAbort()) {
                        return;
                     }

                     this.mc.playerController.windowClick(this.mc.thePlayer.openContainer.windowId, integer, 0, 1, this.mc.thePlayer);
                  }

                  this.isStealing = false;
               }).start();
            } else {
               this.isStealing = false;
            }
         }
      } else {
         this.isStealing = false;
      }
   }

   private boolean shouldAbort() {
      if (this.isEnabled() && this.mc.thePlayer.openContainer instanceof ContainerChest) {
         return false;
      } else {
         this.isStealing = false;
         return true;
      }
   }

   private List<Integer> collectStealableSlots() {
      ArrayList items = new ArrayList();
      ContainerChest chest = (ContainerChest)this.mc.thePlayer.openContainer;
      IInventory chestInv = chest.getLowerChestInventory();
      List<Integer> slots = new ArrayList<>();

      for (int i = 0; i < chestInv.getSizeInventory(); i++) {
         ItemStack is = chestInv.getStackInSlot(i);
         if (is != null && is.getItem() != null && !items.contains(i)) {
            Item item = is.getItem();
            boolean trash = this.isTrashItem(is, i, chestInv);
            if (ignoreTrash.getState() && !trash || !ignoreTrash.getState()) {
               if (takeArmor.getState() && item instanceof ItemArmor) {
                  items.add(i);
               } else if (takeBlocks.getState() && ItemUtil.isPlaceableBlock(is)) {
                  items.add(i);
               } else if (!takeBow.getState() || !(item instanceof ItemBow) && item != Items.arrow) {
                  if (takeFood.getState() && item instanceof ItemFood) {
                     items.add(i);
                  } else if (takePotions.getState() && item instanceof ItemPotion) {
                     items.add(i);
                  } else if (!takeProjectiles.getState()
                     || !(item instanceof ItemEnderPearl) && !(item instanceof ItemEgg) && !(item instanceof ItemSnowball) && !(item instanceof ItemFishingRod)
                     )
                   {
                     if (takeSword.getState() && item instanceof ItemSword) {
                        items.add(i);
                     } else if (!takeTools.getState() || !(item instanceof ItemPickaxe) && !(item instanceof ItemAxe) && !(item instanceof ItemSpade)) {
                        if (!ignoreTrash.getState()) {
                           items.add(i);
                        }
                     } else {
                        items.add(i);
                     }
                  } else {
                     items.add(i);
                  }
               } else {
                  items.add(i);
               }

               slots.add(i);
            }
         }
      }

      return slots.isEmpty() ? items : items;
   }

   private boolean isTrashItem(@NotNull ItemStack is, int slot, IInventory chestInv) {
      boolean trash = false;
      Item item = is.getItem();
      if (item instanceof ItemArmor) {
         if (((ItemArmor)item).armorType == 0) {
            if (ItemUtil.getBestArmorSet(chestInv).get(0).second() != null && (Integer)ItemUtil.getBestArmorSet(chestInv).get(0).second() != slot) {
               trash = true;
            }
         } else if (((ItemArmor)item).armorType == 1) {
            if (ItemUtil.getBestArmorSet(chestInv).get(1).second() != null && (Integer)ItemUtil.getBestArmorSet(chestInv).get(1).second() != slot) {
               trash = true;
            }
         } else if (((ItemArmor)item).armorType == 2) {
            if (ItemUtil.getBestArmorSet(chestInv).get(2).second() != null && (Integer)ItemUtil.getBestArmorSet(chestInv).get(2).second() != slot) {
               trash = true;
            }
         } else if (((ItemArmor)item).armorType == 3
            && ItemUtil.getBestArmorSet(chestInv).get(3).second() != null
            && (Integer)ItemUtil.getBestArmorSet(chestInv).get(3).second() != slot) {
            trash = true;
         }
      } else if (item instanceof ItemSword) {
         if (ItemUtil.getBestSword(chestInv).second() != null && (Integer)ItemUtil.getBestSword(chestInv).second() != slot) {
            trash = true;
         }
      } else if (item instanceof ItemFood) {
         if (((ItemStack)ItemUtil.getBestFood(chestInv).first()).getItem() == Items.golden_apple && item != Items.golden_apple) {
            trash = true;
         }
      } else if (item instanceof ItemBow) {
         if (ItemUtil.getBestBow(chestInv).second() != null && (Integer)ItemUtil.getBestBow(chestInv).second() != slot) {
            trash = true;
         }
      } else if (item instanceof ItemTool) {
         if (item instanceof ItemPickaxe) {
            if ((Integer)ItemUtil.getBestTools(chestInv).get(0).second() != slot) {
               trash = true;
            }
         } else if (item instanceof ItemAxe) {
            if ((Integer)ItemUtil.getBestTools(chestInv).get(1).second() != slot) {
               trash = true;
            }
         } else if (item instanceof ItemSpade && (Integer)ItemUtil.getBestTools(chestInv).get(2).second() != slot) {
            trash = true;
         }
      } else if (item instanceof ItemFishingRod && ItemUtil.containsItem(Items.fishing_rod, chestInv)) {
         trash = true;
      }

      for (int t = 0; t < 40; t++) {
         ItemStack trashSort = this.mc.thePlayer.inventory.getStackInSlot(t);
         if (trashSort != null) {
            if (item instanceof ItemArmor && trashSort.getItem() instanceof ItemArmor) {
               if (ItemUtil.getArmorValue(is) <= ItemUtil.getArmorValue(trashSort) && ((ItemArmor)trashSort.getItem()).armorType == ((ItemArmor)item).armorType
                  )
                {
                  trash = true;
               }
            } else if (item instanceof ItemSword && trashSort.getItem() instanceof ItemSword) {
               if (ItemUtil.getWeaponDamage(is) <= ItemUtil.getWeaponDamage(trashSort)) {
                  trash = true;
               }
            } else if (item instanceof ItemFood && trashSort.getItem() instanceof ItemFood) {
               if (ItemUtil.getBestFood(this.mc.thePlayer.inventory).second() != null
                  && ((ItemStack)ItemUtil.getBestFood(this.mc.thePlayer.inventory).first()).getItem() == Items.golden_apple
                  && item != Items.golden_apple) {
                  trash = true;
               }
            } else if (item instanceof ItemBow && trashSort.getItem() instanceof ItemBow) {
               if (ItemUtil.getUnbreakingScore(is) <= ItemUtil.getUnbreakingScore(trashSort)) {
                  trash = true;
               }
            } else if (item instanceof ItemTool && trashSort.getItem() instanceof ItemTool) {
               if (item instanceof ItemPickaxe && trashSort.getItem() instanceof ItemPickaxe) {
                  if (ItemUtil.getBestTools(this.mc.thePlayer.inventory).get(0).second() != null
                     && ItemUtil.getToolEfficiency(is) <= ItemUtil.getToolEfficiency(trashSort)) {
                     trash = true;
                  }
               } else if (item instanceof ItemAxe && trashSort.getItem() instanceof ItemAxe) {
                  if (ItemUtil.getBestTools(this.mc.thePlayer.inventory).get(1).second() != null
                     && ItemUtil.getToolEfficiency(is) <= ItemUtil.getToolEfficiency(trashSort)) {
                     trash = true;
                  }
               } else if (item instanceof ItemSpade
                  && trashSort.getItem() instanceof ItemSpade
                  && ItemUtil.getBestTools(this.mc.thePlayer.inventory).get(2).second() != null
                  && ItemUtil.getToolEfficiency(is) <= ItemUtil.getToolEfficiency(trashSort)) {
                  trash = true;
               }
            }
         }
      }

      return trash;
   }

   // $VF: Irreducible bytecode was duplicated to produce valid code
   static {
      startDelay = new SliderSetting("Start-delay", 50.0, 0.0, 1000.0, 1.0);
      minClickDelay = new SliderSetting("Min-delay", 50.0, 0.0, 1000.0, 1.0);
      maxClickDelay = new SliderSetting("Max-delay", 50.0, 0.0, 1000.0, 1.0);
      autoClose = new BooleanSetting("Auto-close", true);
      ignoreTrash = new BooleanSetting("Ignore-trash", true);
      takeArmor = new BooleanSetting("Armor", true);
      takeBlocks = new BooleanSetting("Blocks", true);
      takeBow = new BooleanSetting("Bow", false);
      takeFood = new BooleanSetting("Food", true);
      takePotions = new BooleanSetting("Potions", false);
      takeProjectiles = new BooleanSetting("Projectiles", true);
      takeSword = new BooleanSetting("Sword", true);
      takeTools = new BooleanSetting("Tools", false);
   }

}
