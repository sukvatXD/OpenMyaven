package Myaven.module.modules.player;

import Myaven.events.PlayerUpdateEvent;
import Myaven.module.Category;
import Myaven.module.Module;
import Myaven.setting.Setting;
import Myaven.setting.settings.BooleanSetting;
import Myaven.setting.settings.DescriptionSetting;
import Myaven.setting.settings.SliderSetting;
import Myaven.util.ClientUtil;
import Myaven.util.ItemUtil;
import Myaven.util.TimerUtil;
import akka.japi.Pair;
import java.util.List;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.init.Items;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemEgg;
import net.minecraft.item.ItemEnderPearl;
import net.minecraft.item.ItemFireball;
import net.minecraft.item.ItemFishingRod;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemPickaxe;
import net.minecraft.item.ItemPotion;
import net.minecraft.item.ItemShears;
import net.minecraft.item.ItemSnowball;
import net.minecraft.item.ItemSpade;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.item.ItemTool;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class InvManager extends Module {
   public static DescriptionSetting generalSettings;
   public static SliderSetting startDelayMs;
   public static SliderSetting minDelayMs;
   public static SliderSetting maxDelayMs;
   public static BooleanSetting autoArmor;
   public static BooleanSetting autoThrowTrash;
   public static DescriptionSetting trashSettings;
   public static BooleanSetting onlyConfiguredTrash;
   public static BooleanSetting projectilesTrash;
   public static BooleanSetting discardNormalFood;
   public static BooleanSetting bowTrash;
   public static BooleanSetting potionTrash;
   public static BooleanSetting toolsTrash;
   public static DescriptionSetting slotSettings;
   public static SliderSetting swordSlot;
   public static SliderSetting projectilesSlot;
   public static SliderSetting blockSlot;
   public static SliderSetting foodSlot;
   public static SliderSetting bowSlot;
   public static SliderSetting pickaxeSlot;
   public static SliderSetting axeSlot;
   public static SliderSetting shovelSlot;
   public static SliderSetting potionSlot;
   public static SliderSetting fireballSlot;
   public static SliderSetting enderPearlSlot;
   public static SliderSetting shearsSlot;
   public static DescriptionSetting maxItemSlotsSettings;
   public static SliderSetting maxBlockSlots;
   public static SliderSetting maxArrowSlots;
   public static SliderSetting maxTrashThrows;
   private boolean isProcessing;
   private TimerUtil timer;

   public InvManager() {
      super("InvManager", false, Category.Player, true, "Clean and manage your inventory");
      this.isProcessing = false;
      this.timer = new TimerUtil();
      this.addSettings(
         new Setting[]{
            generalSettings,
            startDelayMs,
            minDelayMs,
            maxDelayMs,
            autoArmor,
            autoThrowTrash,
            trashSettings,
            onlyConfiguredTrash,
            projectilesTrash,
            discardNormalFood,
            bowTrash,
            potionTrash,
            toolsTrash,
            slotSettings,
            swordSlot,
            projectilesSlot,
            blockSlot,
            foodSlot,
            bowSlot,
            pickaxeSlot,
            axeSlot,
            shovelSlot,
            potionSlot,
            fireballSlot,
            enderPearlSlot,
            shearsSlot,
            maxItemSlotsSettings,
            maxBlockSlots,
            maxArrowSlots,
            maxTrashThrows
         }
      );
   }

   @Override
   public String getSuffix() {
      return minDelayMs.getRoundedValue() == maxDelayMs.getRoundedValue()
         ? String.valueOf((int)minDelayMs.getRoundedValue())
         : (int)minDelayMs.getRoundedValue() + "-" + (int)maxDelayMs.getRoundedValue();
   }

   @SubscribeEvent
   public void onUpdate(PlayerUpdateEvent event) {
      if (this.mc.currentScreen instanceof GuiInventory) {
         if (!this.isProcessing) {
            this.isProcessing = true;
            new Thread(
                  () -> {
                     try {

                        if (this.timer.hasTimePassed(10L, true)) {
                           Thread.sleep((long)startDelayMs.getRoundedValue());
                           if (autoArmor.getState()) {
                              List<Pair<ItemStack, Integer>> armors = ItemUtil.getBestArmorSet(this.mc.thePlayer.inventory);

                              for (int ax = 0; ax < 4; ax++) {
                                 switch (ax) {
                                    case 0:
                                       if (this.shouldAbort()) {
                                          return;
                                       }

                                       if (armors.get(0).second() != null
                                               && (Integer)armors.get(0).second() != 39
                                               && ItemUtil.getArmorValue((ItemStack)armors.get(0).first())
                                               > ItemUtil.getArmorValue(this.mc.thePlayer.inventory.getStackInSlot(39))) {
                                          if (this.mc.thePlayer.getEquipmentInSlot(4) != null) {
                                             ItemUtil.drop(39);
                                          }

                                          Thread.sleep(ClientUtil.getRandomDoubleInMillis(minDelayMs.getRoundedValue(), maxDelayMs.getRoundedValue()));
                                          if (this.shouldAbort()) {
                                             return;
                                          }

                                          ItemUtil.click((Integer)armors.get(0).second());
                                          Thread.sleep(ClientUtil.getRandomDoubleInMillis(minDelayMs.getRoundedValue(), maxDelayMs.getRoundedValue()));
                                       }
                                       break;
                                    case 1:
                                       if (this.shouldAbort()) {
                                          return;
                                       }

                                       if (armors.get(1).second() != null
                                               && (Integer)armors.get(1).second() != 38
                                               && ItemUtil.getArmorValue((ItemStack)armors.get(1).first())
                                               > ItemUtil.getArmorValue(this.mc.thePlayer.inventory.getStackInSlot(38))) {
                                          if (this.mc.thePlayer.getEquipmentInSlot(3) != null) {
                                             ItemUtil.drop(38);
                                          }

                                          Thread.sleep(ClientUtil.getRandomDoubleInMillis(minDelayMs.getRoundedValue(), maxDelayMs.getRoundedValue()));
                                          if (this.shouldAbort()) {
                                             return;
                                          }

                                          ItemUtil.click((Integer)armors.get(1).second());
                                          Thread.sleep(ClientUtil.getRandomDoubleInMillis(minDelayMs.getRoundedValue(), maxDelayMs.getRoundedValue()));
                                       }
                                       break;
                                    case 2:
                                       if (this.shouldAbort()) {
                                          return;
                                       }

                                       if (armors.get(2).second() != null
                                               && (Integer)armors.get(2).second() != 37
                                               && ItemUtil.getArmorValue((ItemStack)armors.get(2).first())
                                               > ItemUtil.getArmorValue(this.mc.thePlayer.inventory.getStackInSlot(37))) {
                                          if (this.mc.thePlayer.getEquipmentInSlot(2) != null) {
                                             ItemUtil.drop(37);
                                          }

                                          Thread.sleep(ClientUtil.getRandomDoubleInMillis(minDelayMs.getRoundedValue(), maxDelayMs.getRoundedValue()));
                                          if (this.shouldAbort()) {
                                             return;
                                          }

                                          ItemUtil.click((Integer)armors.get(2).second());
                                          Thread.sleep(ClientUtil.getRandomDoubleInMillis(minDelayMs.getRoundedValue(), maxDelayMs.getRoundedValue()));
                                       }
                                       break;
                                    case 3:
                                       if (this.shouldAbort()) {
                                          return;
                                       }

                                       if (armors.get(3).second() != null
                                               && (Integer)armors.get(3).second() != 36
                                               && ItemUtil.getArmorValue((ItemStack)armors.get(3).first())
                                               > ItemUtil.getArmorValue(this.mc.thePlayer.inventory.getStackInSlot(36))) {
                                          if (this.mc.thePlayer.getEquipmentInSlot(1) != null) {
                                             ItemUtil.drop(36);
                                          }

                                          Thread.sleep(ClientUtil.getRandomDoubleInMillis(minDelayMs.getRoundedValue(), maxDelayMs.getRoundedValue()));
                                          if (this.shouldAbort()) {
                                             return;
                                          }

                                          ItemUtil.click((Integer)armors.get(3).second());
                                          Thread.sleep(ClientUtil.getRandomDoubleInMillis(minDelayMs.getRoundedValue(), maxDelayMs.getRoundedValue()));
                                       }
                                 }
                              }
                           }

                           if (this.shouldAbort()) {
                              return;
                           }

                           if (swordSlot.getRoundedValue() != 0.0
                                   && ItemUtil.getBestSword(this.mc.thePlayer.inventory).second() != null
                                   && (int)swordSlot.getRoundedValue() - 1 != (Integer)ItemUtil.getBestSword(this.mc.thePlayer.inventory).second()) {
                              ItemUtil.swap((Integer)ItemUtil.getBestSword(this.mc.thePlayer.inventory).second(), (int)swordSlot.getRoundedValue() - 37);
                              Thread.sleep(ClientUtil.getRandomDoubleInMillis(minDelayMs.getRoundedValue(), maxDelayMs.getRoundedValue()));
                           }

                           if (this.shouldAbort()) {
                              return;
                           }

                           if (projectilesSlot.getRoundedValue() != 0.0
                                   && !projectilesTrash.getState()
                                   && ItemUtil.getBestPickaxe(this.mc.thePlayer.inventory).second() != null
                                   && (int)projectilesSlot.getRoundedValue() - 1 != (Integer)ItemUtil.getBestPickaxe(this.mc.thePlayer.inventory).second()) {
                              ItemUtil.swap((Integer)ItemUtil.getBestPickaxe(this.mc.thePlayer.inventory).second(), (int)projectilesSlot.getRoundedValue() - 37);
                              Thread.sleep(ClientUtil.getRandomDoubleInMillis(minDelayMs.getRoundedValue(), maxDelayMs.getRoundedValue()));
                           }

                           if (this.shouldAbort()) {
                              return;
                           }

                           if (blockSlot.getRoundedValue() != 0.0
                                   && ItemUtil.getBestBuildingBlock(this.mc.thePlayer.inventory).second() != null
                                   && (int)blockSlot.getRoundedValue() - 1 != (Integer)ItemUtil.getBestBuildingBlock(this.mc.thePlayer.inventory).second()) {
                              ItemUtil.swap((Integer)ItemUtil.getBestBuildingBlock(this.mc.thePlayer.inventory).second(), (int)blockSlot.getRoundedValue() - 37);
                              Thread.sleep(ClientUtil.getRandomDoubleInMillis(minDelayMs.getRoundedValue(), maxDelayMs.getRoundedValue()));
                           }

                           if (this.shouldAbort()) {
                              return;
                           }

                           if (bowSlot.getRoundedValue() != 0.0
                                   && !bowTrash.getState()
                                   && ItemUtil.getBestBow(this.mc.thePlayer.inventory).second() != null
                                   && (int)bowSlot.getRoundedValue() - 1 != (Integer)ItemUtil.getBestBow(this.mc.thePlayer.inventory).second()) {
                              ItemUtil.swap((Integer)ItemUtil.getBestBow(this.mc.thePlayer.inventory).second(), (int)bowSlot.getRoundedValue() - 37);
                              Thread.sleep(ClientUtil.getRandomDoubleInMillis(minDelayMs.getRoundedValue(), maxDelayMs.getRoundedValue()));
                           }

                           if (!toolsTrash.getState()) {
                              List<Pair<ItemStack, Integer>> tools = ItemUtil.getBestTools(this.mc.thePlayer.inventory);

                              for (int st = 0; st < 3; st++) {
                                 switch (st) {
                                    case 0:
                                       if (this.shouldAbort()) {
                                          return;
                                       }

                                       if (pickaxeSlot.getRoundedValue() != 0.0
                                               && tools.get(0).second() != null
                                               && (int)pickaxeSlot.getRoundedValue() - 1 != (Integer)tools.get(0).second()) {
                                          ItemUtil.swap((Integer)tools.get(0).second(), (int)pickaxeSlot.getRoundedValue() - 37);
                                          Thread.sleep(ClientUtil.getRandomDoubleInMillis(minDelayMs.getRoundedValue(), maxDelayMs.getRoundedValue()));
                                       }
                                       break;
                                    case 1:
                                       if (this.shouldAbort()) {
                                          return;
                                       }

                                       if (axeSlot.getRoundedValue() != 0.0
                                               && tools.get(1).second() != null
                                               && (int)axeSlot.getRoundedValue() - 1 != (Integer)tools.get(1).second()) {
                                          ItemUtil.swap((Integer)tools.get(1).second(), (int)axeSlot.getRoundedValue() - 37);
                                          Thread.sleep(ClientUtil.getRandomDoubleInMillis(minDelayMs.getRoundedValue(), maxDelayMs.getRoundedValue()));
                                       }
                                       break;
                                    case 2:
                                       if (this.shouldAbort()) {
                                          return;
                                       }

                                       if (shovelSlot.getRoundedValue() != 0.0
                                               && tools.get(2).second() != null
                                               && (int)shovelSlot.getRoundedValue() - 1 != (Integer)tools.get(2).second()) {
                                          ItemUtil.swap((Integer)tools.get(2).second(), (int)shovelSlot.getRoundedValue() - 37);
                                          Thread.sleep(ClientUtil.getRandomDoubleInMillis(minDelayMs.getRoundedValue(), maxDelayMs.getRoundedValue()));
                                       }
                                 }
                              }
                           }

                           if (this.shouldAbort()) {
                              return;
                           }

                           if (foodSlot.getRoundedValue() != 0.0
                                   && ItemUtil.getBestFood(this.mc.thePlayer.inventory).second() != null
                                   && (int)foodSlot.getRoundedValue() - 1 != (Integer)ItemUtil.getBestFood(this.mc.thePlayer.inventory).second()
                                   && (
                                   discardNormalFood.getState()
                                           && ((ItemStack)ItemUtil.getBestFood(this.mc.thePlayer.inventory).first()).getItem() == Items.golden_apple
                                           || !discardNormalFood.getState()
                           )) {
                              ItemUtil.swap((Integer)ItemUtil.getBestFood(this.mc.thePlayer.inventory).second(), (int)foodSlot.getRoundedValue() - 37);
                              Thread.sleep(ClientUtil.getRandomDoubleInMillis(minDelayMs.getRoundedValue(), maxDelayMs.getRoundedValue()));
                           }

                           if (this.shouldAbort()) {
                              return;
                           }

                           if (potionSlot.getRoundedValue() != 0.0
                                   && !potionTrash.getState()
                                   && ItemUtil.getPotion(this.mc.thePlayer.inventory).second() != null
                                   && (int)potionSlot.getRoundedValue() - 1 != (Integer)ItemUtil.getPotion(this.mc.thePlayer.inventory).second()
                                   && (
                                   this.mc.thePlayer.inventory.getStackInSlot((int)potionSlot.getRoundedValue() - 1) == null
                                           || !(this.mc.thePlayer.inventory.getStackInSlot((int)potionSlot.getRoundedValue() - 1).getItem() instanceof ItemPotion)
                           )) {
                              ItemUtil.swap((Integer)ItemUtil.getPotion(this.mc.thePlayer.inventory).second(), (int)potionSlot.getRoundedValue() - 37);
                              Thread.sleep(ClientUtil.getRandomDoubleInMillis(minDelayMs.getRoundedValue(), maxDelayMs.getRoundedValue()));
                           }

                           if (this.shouldAbort()) {
                              return;
                           }

                           if (fireballSlot.getRoundedValue() != 0.0
                                   && ItemUtil.getFireball(this.mc.thePlayer.inventory).second() != null
                                   && (int)fireballSlot.getRoundedValue() - 1 != (Integer)ItemUtil.getFireball(this.mc.thePlayer.inventory).second()
                                   && (
                                   this.mc.thePlayer.inventory.getStackInSlot((int)fireballSlot.getRoundedValue() - 1) == null
                                           || !(this.mc.thePlayer.inventory.getStackInSlot((int)fireballSlot.getRoundedValue() - 1).getItem() instanceof ItemFireball)
                           )) {
                              ItemUtil.swap((Integer)ItemUtil.getPotion(this.mc.thePlayer.inventory).second(), (int)fireballSlot.getRoundedValue() - 37);
                              Thread.sleep(ClientUtil.getRandomDoubleInMillis(minDelayMs.getRoundedValue(), maxDelayMs.getRoundedValue()));
                           }

                           if (this.shouldAbort()) {
                              return;
                           }

                           if (enderPearlSlot.getRoundedValue() != 0.0
                                   && ItemUtil.getEnderPearl(this.mc.thePlayer.inventory).second() != null
                                   && (int)enderPearlSlot.getRoundedValue() - 1 != (Integer)ItemUtil.getEnderPearl(this.mc.thePlayer.inventory).second()
                                   && (
                                   this.mc.thePlayer.inventory.getStackInSlot((int)enderPearlSlot.getRoundedValue() - 1) == null
                                           || !(this.mc.thePlayer.inventory.getStackInSlot((int)enderPearlSlot.getRoundedValue() - 1).getItem() instanceof ItemEnderPearl)
                           )) {
                              ItemUtil.swap((Integer)ItemUtil.getPotion(this.mc.thePlayer.inventory).second(), (int)enderPearlSlot.getRoundedValue() - 37);
                              Thread.sleep(ClientUtil.getRandomDoubleInMillis(minDelayMs.getRoundedValue(), maxDelayMs.getRoundedValue()));
                           }

                           if (this.shouldAbort()) {
                              return;
                           }

                           if (shearsSlot.getRoundedValue() != 0.0
                                   && ItemUtil.getShears(this.mc.thePlayer.inventory).second() != null
                                   && (int)shearsSlot.getRoundedValue() - 1 != (Integer)ItemUtil.getShears(this.mc.thePlayer.inventory).second()
                                   && (
                                   this.mc.thePlayer.inventory.getStackInSlot((int)shearsSlot.getRoundedValue() - 1) == null
                                           || !(this.mc.thePlayer.inventory.getStackInSlot((int)shearsSlot.getRoundedValue() - 1).getItem() instanceof ItemShears)
                           )) {
                              ItemUtil.swap((Integer)ItemUtil.getPotion(this.mc.thePlayer.inventory).second(), (int)shearsSlot.getRoundedValue() - 37);
                              Thread.sleep(ClientUtil.getRandomDoubleInMillis(minDelayMs.getRoundedValue(), maxDelayMs.getRoundedValue()));
                           }

                           if (autoThrowTrash.getState()) {
                              int throwed = 0;
                              int blocks = 0;
                              int arrows = 0;

                              for (int i = 0; i < this.mc.thePlayer.inventory.getSizeInventory(); i++) {
                                 ItemStack item = this.mc.thePlayer.inventory.getStackInSlot(i);
                                 if (item != null) {
                                    List<Pair<ItemStack, Integer>> armors = ItemUtil.getBestArmorSet(this.mc.thePlayer.inventory);
                                    if (autoArmor.getState()) {
                                       for (int ax = 0; ax < 4; ax++) {
                                          switch (ax) {
                                             case 0:
                                                if (this.shouldAbort()) {
                                                   return;
                                                }

                                                if (armors.get(0).second() != null
                                                        && (Integer)armors.get(0).second() != 39
                                                        && ItemUtil.getArmorValue((ItemStack)armors.get(0).first())
                                                        > ItemUtil.getArmorValue(this.mc.thePlayer.inventory.getStackInSlot(39))) {
                                                   if (this.mc.thePlayer.getEquipmentInSlot(4) != null) {
                                                      ItemUtil.drop(39);
                                                   }

                                                   Thread.sleep(ClientUtil.getRandomDoubleInMillis(minDelayMs.getRoundedValue(), maxDelayMs.getRoundedValue()));
                                                   if (this.shouldAbort()) {
                                                      return;
                                                   }

                                                   ItemUtil.click((Integer)armors.get(0).second());
                                                   Thread.sleep(ClientUtil.getRandomDoubleInMillis(minDelayMs.getRoundedValue(), maxDelayMs.getRoundedValue()));
                                                }
                                                break;
                                             case 1:
                                                if (this.shouldAbort()) {
                                                   return;
                                                }

                                                if (armors.get(1).second() != null
                                                        && (Integer)armors.get(1).second() != 38
                                                        && ItemUtil.getArmorValue((ItemStack)armors.get(1).first())
                                                        > ItemUtil.getArmorValue(this.mc.thePlayer.inventory.getStackInSlot(38))) {
                                                   if (this.mc.thePlayer.getEquipmentInSlot(3) != null) {
                                                      ItemUtil.drop(38);
                                                   }

                                                   Thread.sleep(ClientUtil.getRandomDoubleInMillis(minDelayMs.getRoundedValue(), maxDelayMs.getRoundedValue()));
                                                   if (this.shouldAbort()) {
                                                      return;
                                                   }

                                                   ItemUtil.click((Integer)armors.get(1).second());
                                                   Thread.sleep(ClientUtil.getRandomDoubleInMillis(minDelayMs.getRoundedValue(), maxDelayMs.getRoundedValue()));
                                                }
                                                break;
                                             case 2:
                                                if (this.shouldAbort()) {
                                                   return;
                                                }

                                                if (armors.get(2).second() != null
                                                        && (Integer)armors.get(2).second() != 37
                                                        && ItemUtil.getArmorValue((ItemStack)armors.get(2).first())
                                                        > ItemUtil.getArmorValue(this.mc.thePlayer.inventory.getStackInSlot(37))) {
                                                   if (this.mc.thePlayer.getEquipmentInSlot(2) != null) {
                                                      ItemUtil.drop(37);
                                                   }

                                                   Thread.sleep(ClientUtil.getRandomDoubleInMillis(minDelayMs.getRoundedValue(), maxDelayMs.getRoundedValue()));
                                                   if (this.shouldAbort()) {
                                                      return;
                                                   }

                                                   ItemUtil.click((Integer)armors.get(2).second());
                                                   Thread.sleep(ClientUtil.getRandomDoubleInMillis(minDelayMs.getRoundedValue(), maxDelayMs.getRoundedValue()));
                                                }
                                                break;
                                             case 3:
                                                if (this.shouldAbort()) {
                                                   return;
                                                }

                                                if (armors.get(3).second() != null
                                                        && (Integer)armors.get(3).second() != 36
                                                        && ItemUtil.getArmorValue((ItemStack)armors.get(3).first())
                                                        > ItemUtil.getArmorValue(this.mc.thePlayer.inventory.getStackInSlot(36))) {
                                                   if (this.mc.thePlayer.getEquipmentInSlot(1) != null) {
                                                      ItemUtil.drop(36);
                                                   }

                                                   Thread.sleep(ClientUtil.getRandomDoubleInMillis(minDelayMs.getRoundedValue(), maxDelayMs.getRoundedValue()));
                                                   if (this.shouldAbort()) {
                                                      return;
                                                   }

                                                   ItemUtil.click((Integer)armors.get(3).second());
                                                   Thread.sleep(ClientUtil.getRandomDoubleInMillis(minDelayMs.getRoundedValue(), maxDelayMs.getRoundedValue()));
                                                }
                                          }
                                       }
                                    }

                                    if (this.shouldAbort()) {
                                       return;
                                    }

                                    if (swordSlot.getRoundedValue() != 0.0
                                            && ItemUtil.getBestSword(this.mc.thePlayer.inventory).second() != null
                                            && (int)swordSlot.getRoundedValue() - 1 != (Integer)ItemUtil.getBestSword(this.mc.thePlayer.inventory).second()) {
                                       ItemUtil.swap((Integer)ItemUtil.getBestSword(this.mc.thePlayer.inventory).second(), (int)swordSlot.getRoundedValue() - 37);
                                       Thread.sleep(ClientUtil.getRandomDoubleInMillis(minDelayMs.getRoundedValue(), maxDelayMs.getRoundedValue()));
                                    }

                                    if (this.shouldAbort()) {
                                       return;
                                    }

                                    if (projectilesSlot.getRoundedValue() != 0.0
                                            && !projectilesTrash.getState()
                                            && ItemUtil.getBestPickaxe(this.mc.thePlayer.inventory).second() != null
                                            && (int)projectilesSlot.getRoundedValue() - 1 != (Integer)ItemUtil.getBestPickaxe(this.mc.thePlayer.inventory).second()) {
                                       ItemUtil.swap(
                                               (Integer)ItemUtil.getBestPickaxe(this.mc.thePlayer.inventory).second(), (int)projectilesSlot.getRoundedValue() - 37
                                       );
                                       Thread.sleep(ClientUtil.getRandomDoubleInMillis(minDelayMs.getRoundedValue(), maxDelayMs.getRoundedValue()));
                                    }

                                    if (this.shouldAbort()) {
                                       return;
                                    }

                                    if (blockSlot.getRoundedValue() != 0.0
                                            && ItemUtil.getBestBuildingBlock(this.mc.thePlayer.inventory).second() != null
                                            && (int)blockSlot.getRoundedValue() - 1 != (Integer)ItemUtil.getBestBuildingBlock(this.mc.thePlayer.inventory).second()) {
                                       ItemUtil.swap(
                                               (Integer)ItemUtil.getBestBuildingBlock(this.mc.thePlayer.inventory).second(), (int)blockSlot.getRoundedValue() - 37
                                       );
                                       Thread.sleep(ClientUtil.getRandomDoubleInMillis(minDelayMs.getRoundedValue(), maxDelayMs.getRoundedValue()));
                                    }

                                    if (this.shouldAbort()) {
                                       return;
                                    }

                                    if (bowSlot.getRoundedValue() != 0.0
                                            && !bowTrash.getState()
                                            && ItemUtil.getBestBow(this.mc.thePlayer.inventory).second() != null
                                            && (int)bowSlot.getRoundedValue() - 1 != (Integer)ItemUtil.getBestBow(this.mc.thePlayer.inventory).second()) {
                                       ItemUtil.swap((Integer)ItemUtil.getBestBow(this.mc.thePlayer.inventory).second(), (int)bowSlot.getRoundedValue() - 37);
                                       Thread.sleep(ClientUtil.getRandomDoubleInMillis(minDelayMs.getRoundedValue(), maxDelayMs.getRoundedValue()));
                                    }

                                    if (!toolsTrash.getState()) {
                                       List<Pair<ItemStack, Integer>> tools = ItemUtil.getBestTools(this.mc.thePlayer.inventory);

                                       for (int st = 0; st < 3; st++) {
                                          switch (st) {
                                             case 0:
                                                if (this.shouldAbort()) {
                                                   return;
                                                }

                                                if (pickaxeSlot.getRoundedValue() != 0.0
                                                        && tools.get(0).second() != null
                                                        && (int)pickaxeSlot.getRoundedValue() - 1 != (Integer)tools.get(0).second()) {
                                                   ItemUtil.swap((Integer)tools.get(0).second(), (int)pickaxeSlot.getRoundedValue() - 37);
                                                   Thread.sleep(ClientUtil.getRandomDoubleInMillis(minDelayMs.getRoundedValue(), maxDelayMs.getRoundedValue()));
                                                }
                                                break;
                                             case 1:
                                                if (this.shouldAbort()) {
                                                   return;
                                                }

                                                if (axeSlot.getRoundedValue() != 0.0
                                                        && tools.get(1).second() != null
                                                        && (int)axeSlot.getRoundedValue() - 1 != (Integer)tools.get(1).second()) {
                                                   ItemUtil.swap((Integer)tools.get(1).second(), (int)axeSlot.getRoundedValue() - 37);
                                                   Thread.sleep(ClientUtil.getRandomDoubleInMillis(minDelayMs.getRoundedValue(), maxDelayMs.getRoundedValue()));
                                                }
                                                break;
                                             case 2:
                                                if (this.shouldAbort()) {
                                                   return;
                                                }

                                                if (shovelSlot.getRoundedValue() != 0.0
                                                        && tools.get(2).second() != null
                                                        && (int)shovelSlot.getRoundedValue() - 1 != (Integer)tools.get(2).second()) {
                                                   ItemUtil.swap((Integer)tools.get(2).second(), (int)shovelSlot.getRoundedValue() - 37);
                                                   Thread.sleep(ClientUtil.getRandomDoubleInMillis(minDelayMs.getRoundedValue(), maxDelayMs.getRoundedValue()));
                                                }
                                          }
                                       }
                                    }

                                    if (this.shouldAbort()) {
                                       return;
                                    }

                                    if (foodSlot.getRoundedValue() != 0.0
                                            && ItemUtil.getBestFood(this.mc.thePlayer.inventory).second() != null
                                            && (int)foodSlot.getRoundedValue() - 1 != (Integer)ItemUtil.getBestFood(this.mc.thePlayer.inventory).second()
                                            && (
                                            discardNormalFood.getState()
                                                    && ((ItemStack)ItemUtil.getBestFood(this.mc.thePlayer.inventory).first()).getItem() == Items.golden_apple
                                                    || !discardNormalFood.getState()
                                    )) {
                                       ItemUtil.swap((Integer)ItemUtil.getBestFood(this.mc.thePlayer.inventory).second(), (int)foodSlot.getRoundedValue() - 37);
                                       Thread.sleep(ClientUtil.getRandomDoubleInMillis(minDelayMs.getRoundedValue(), maxDelayMs.getRoundedValue()));
                                    }

                                    if (this.shouldAbort()) {
                                       return;
                                    }

                                    if (potionSlot.getRoundedValue() != 0.0
                                            && !potionTrash.getState()
                                            && ItemUtil.getPotion(this.mc.thePlayer.inventory).second() != null
                                            && (int)potionSlot.getRoundedValue() - 1 != (Integer)ItemUtil.getPotion(this.mc.thePlayer.inventory).second()
                                            && (
                                            this.mc.thePlayer.inventory.getStackInSlot((int)potionSlot.getRoundedValue() - 1) == null
                                                    || !(
                                                    this.mc.thePlayer.inventory.getStackInSlot((int)potionSlot.getRoundedValue() - 1).getItem() instanceof ItemPotion
                                            )
                                    )) {
                                       ItemUtil.swap((Integer)ItemUtil.getPotion(this.mc.thePlayer.inventory).second(), (int)potionSlot.getRoundedValue() - 37);
                                       Thread.sleep(ClientUtil.getRandomDoubleInMillis(minDelayMs.getRoundedValue(), maxDelayMs.getRoundedValue()));
                                    }

                                    if (this.shouldAbort()) {
                                       return;
                                    }

                                    if (fireballSlot.getRoundedValue() != 0.0
                                            && ItemUtil.getFireball(this.mc.thePlayer.inventory).second() != null
                                            && (int)fireballSlot.getRoundedValue() - 1 != (Integer)ItemUtil.getFireball(this.mc.thePlayer.inventory).second()
                                            && (
                                            this.mc.thePlayer.inventory.getStackInSlot((int)fireballSlot.getRoundedValue() - 1) == null
                                                    || !(
                                                    this.mc.thePlayer.inventory.getStackInSlot((int)fireballSlot.getRoundedValue() - 1).getItem() instanceof ItemFireball
                                            )
                                    )) {
                                       ItemUtil.swap((Integer)ItemUtil.getFireball(this.mc.thePlayer.inventory).second(), (int)fireballSlot.getRoundedValue() - 37);
                                       Thread.sleep(ClientUtil.getRandomDoubleInMillis(minDelayMs.getRoundedValue(), maxDelayMs.getRoundedValue()));
                                    }

                                    if (this.shouldAbort()) {
                                       return;
                                    }

                                    if (enderPearlSlot.getRoundedValue() != 0.0
                                            && ItemUtil.getEnderPearl(this.mc.thePlayer.inventory).second() != null
                                            && (int)enderPearlSlot.getRoundedValue() - 1 != (Integer)ItemUtil.getEnderPearl(this.mc.thePlayer.inventory).second()
                                            && (
                                            this.mc.thePlayer.inventory.getStackInSlot((int)enderPearlSlot.getRoundedValue() - 1) == null
                                                    || !(
                                                    this.mc.thePlayer.inventory.getStackInSlot((int)enderPearlSlot.getRoundedValue() - 1).getItem() instanceof ItemEnderPearl
                                            )
                                    )) {
                                       ItemUtil.swap(
                                               (Integer)ItemUtil.getEnderPearl(this.mc.thePlayer.inventory).second(), (int)enderPearlSlot.getRoundedValue() - 37
                                       );
                                       Thread.sleep(ClientUtil.getRandomDoubleInMillis(minDelayMs.getRoundedValue(), maxDelayMs.getRoundedValue()));
                                    }

                                    if (this.shouldAbort()) {
                                       return;
                                    }

                                    if (shearsSlot.getRoundedValue() != 0.0
                                            && ItemUtil.getShears(this.mc.thePlayer.inventory).second() != null
                                            && (int)shearsSlot.getRoundedValue() - 1 != (Integer)ItemUtil.getShears(this.mc.thePlayer.inventory).second()
                                            && (
                                            this.mc.thePlayer.inventory.getStackInSlot((int)shearsSlot.getRoundedValue() - 1) == null
                                                    || !(
                                                    this.mc.thePlayer.inventory.getStackInSlot((int)shearsSlot.getRoundedValue() - 1).getItem() instanceof ItemShears
                                            )
                                    )) {
                                       ItemUtil.swap((Integer)ItemUtil.getShears(this.mc.thePlayer.inventory).second(), (int)shearsSlot.getRoundedValue() - 37);
                                       Thread.sleep(ClientUtil.getRandomDoubleInMillis(minDelayMs.getRoundedValue(), maxDelayMs.getRoundedValue()));
                                    }

                                    if (this.shouldAbort()) {
                                       return;
                                    }

                                    if ((double)throwed >= maxTrashThrows.getRoundedValue()) {
                                       return;
                                    }

                                    armors = ItemUtil.getBestArmorSet(this.mc.thePlayer.inventory);
                                    if (item.getItem() instanceof ItemArmor && ((ItemArmor)item.getItem()).armorType == 0) {
                                       if (ItemUtil.getArmorValue(item) <= ItemUtil.getArmorValue((ItemStack)armors.get(0).first()) && i != 39) {
                                          ItemUtil.drop(i);
                                          throwed++;
                                          Thread.sleep(ClientUtil.getRandomDoubleInMillis(minDelayMs.getRoundedValue(), maxDelayMs.getRoundedValue()));
                                       }
                                    } else if (item.getItem() instanceof ItemArmor && ((ItemArmor)item.getItem()).armorType == 1) {
                                       if (ItemUtil.getArmorValue(item) <= ItemUtil.getArmorValue((ItemStack)armors.get(1).first()) && i != 38) {
                                          ItemUtil.drop(i);
                                          throwed++;
                                          Thread.sleep(ClientUtil.getRandomDoubleInMillis(minDelayMs.getRoundedValue(), maxDelayMs.getRoundedValue()));
                                       }
                                    } else if (item.getItem() instanceof ItemArmor && ((ItemArmor)item.getItem()).armorType == 2) {
                                       if (ItemUtil.getArmorValue(item) <= ItemUtil.getArmorValue((ItemStack)armors.get(2).first()) && i != 37) {
                                          ItemUtil.drop(i);
                                          throwed++;
                                          Thread.sleep(ClientUtil.getRandomDoubleInMillis(minDelayMs.getRoundedValue(), maxDelayMs.getRoundedValue()));
                                       }
                                    } else if (item.getItem() instanceof ItemArmor && ((ItemArmor)item.getItem()).armorType == 3) {
                                       if (ItemUtil.getArmorValue(item) <= ItemUtil.getArmorValue((ItemStack)armors.get(3).first()) && i != 36) {
                                          ItemUtil.drop(i);
                                          throwed++;
                                          Thread.sleep(ClientUtil.getRandomDoubleInMillis(minDelayMs.getRoundedValue(), maxDelayMs.getRoundedValue()));
                                       }
                                    } else if (item.getItem() instanceof ItemSword && (Integer)ItemUtil.getBestSword(this.mc.thePlayer.inventory).second() != i) {
                                       ItemUtil.drop(i);
                                       throwed++;
                                       Thread.sleep(ClientUtil.getRandomDoubleInMillis(minDelayMs.getRoundedValue(), maxDelayMs.getRoundedValue()));
                                    } else if ((
                                            item.getItem() instanceof ItemSnowball
                                                    || item.getItem() instanceof ItemEgg
                                                    || item.getItem() instanceof ItemFishingRod && item.getItem() instanceof ItemEnderPearl
                                    )
                                            && projectilesTrash.getState()) {
                                       ItemUtil.drop(i);
                                       throwed++;
                                       Thread.sleep(ClientUtil.getRandomDoubleInMillis(minDelayMs.getRoundedValue(), maxDelayMs.getRoundedValue()));
                                    } else if ((
                                            !(item.getItem() instanceof ItemBow)
                                                    || ItemUtil.getBestBow(this.mc.thePlayer.inventory).second() == null
                                                    || (Integer)ItemUtil.getBestBow(this.mc.thePlayer.inventory).second() == i
                                    )
                                            && (!bowTrash.getState() || !(item.getItem() instanceof ItemBow) && item.getItem() != Items.arrow)) {
                                       if (!(item.getItem() instanceof ItemFood)
                                               || (!discardNormalFood.getState() || item.getItem() == Items.golden_apple)
                                               && (Integer)ItemUtil.getBestFood(this.mc.thePlayer.inventory).second() == i) {
                                          if (potionTrash.getState() && item.getItem() instanceof ItemPotion) {
                                             ItemUtil.drop(i);
                                             throwed++;
                                             Thread.sleep(ClientUtil.getRandomDoubleInMillis(minDelayMs.getRoundedValue(), maxDelayMs.getRoundedValue()));
                                          } else if (item.getItem() instanceof ItemTool) {
                                             if (toolsTrash.getState()) {
                                                ItemUtil.drop(i);
                                                throwed++;
                                                Thread.sleep(ClientUtil.getRandomDoubleInMillis(minDelayMs.getRoundedValue(), maxDelayMs.getRoundedValue()));
                                             } else if (item.getItem() instanceof ItemPickaxe) {
                                                if ((Integer)ItemUtil.getBestTools(this.mc.thePlayer.inventory).get(0).second() != i) {
                                                   ItemUtil.drop(i);
                                                   throwed++;
                                                   Thread.sleep(ClientUtil.getRandomDoubleInMillis(minDelayMs.getRoundedValue(), maxDelayMs.getRoundedValue()));
                                                }
                                             } else if (item.getItem() instanceof ItemAxe) {
                                                if ((Integer)ItemUtil.getBestTools(this.mc.thePlayer.inventory).get(1).second() != i) {
                                                   ItemUtil.drop(i);
                                                   throwed++;
                                                   Thread.sleep(ClientUtil.getRandomDoubleInMillis(minDelayMs.getRoundedValue(), maxDelayMs.getRoundedValue()));
                                                }
                                             } else if (item.getItem() instanceof ItemSpade) {
                                                if ((Integer)ItemUtil.getBestTools(this.mc.thePlayer.inventory).get(2).second() != i) {
                                                   ItemUtil.drop(i);
                                                   throwed++;
                                                   Thread.sleep(ClientUtil.getRandomDoubleInMillis(minDelayMs.getRoundedValue(), maxDelayMs.getRoundedValue()));
                                                }
                                             } else if (item.getItem() instanceof ItemShears
                                                     && (Integer)ItemUtil.getShears(this.mc.thePlayer.inventory).second() != i) {
                                                ItemUtil.drop(i);
                                                throwed++;
                                                Thread.sleep(ClientUtil.getRandomDoubleInMillis(minDelayMs.getRoundedValue(), maxDelayMs.getRoundedValue()));
                                             }
                                          } else if (ItemUtil.isPlaceableBlock(item)) {
                                             if ((double)blocks >= maxBlockSlots.getRoundedValue()) {
                                                ItemUtil.drop(i);
                                                Thread.sleep(ClientUtil.getRandomDoubleInMillis(minDelayMs.getRoundedValue(), maxDelayMs.getRoundedValue()));
                                             }

                                             blocks++;
                                             throwed++;
                                          } else if (item.getItem() == Items.arrow) {
                                             if ((double)arrows >= maxArrowSlots.getRoundedValue()) {
                                                ItemUtil.drop(i);
                                                Thread.sleep(ClientUtil.getRandomDoubleInMillis(minDelayMs.getRoundedValue(), maxDelayMs.getRoundedValue()));
                                             }

                                             arrows++;
                                             throwed++;
                                          } else if (!onlyConfiguredTrash.getState()
                                                  && !(item.getItem() instanceof ItemPotion)
                                                  && item.getItem() != Items.arrow
                                                  && !(item.getItem() instanceof ItemTool)
                                                  && !(item.getItem() instanceof ItemSword)
                                                  && !(item.getItem() instanceof ItemFood)
                                                  && !(item.getItem() instanceof ItemBow)
                                                  && !(item.getItem() instanceof ItemArmor)
                                                  && !ItemUtil.isPlaceableBlock(item)
                                                  && !(item.getItem() instanceof ItemSnowball)
                                                  && !(item.getItem() instanceof ItemEgg)
                                                  && !(item.getItem() instanceof ItemEnderPearl)
                                                  && !(item.getItem() instanceof ItemShears)
                                                  && !(item.getItem() instanceof ItemFireball)
                                                  || ItemUtil.getBestPickaxe(this.mc.thePlayer.inventory).first() != null
                                                  && ((ItemStack)ItemUtil.getBestPickaxe(this.mc.thePlayer.inventory).first()).getItem() instanceof ItemFishingRod
                                                  && !(item.getItem() instanceof ItemFishingRod)) {
                                             ItemUtil.drop(i);
                                             throwed++;
                                             Thread.sleep(ClientUtil.getRandomDoubleInMillis(minDelayMs.getRoundedValue(), maxDelayMs.getRoundedValue()));
                                          }
                                       } else {
                                          ItemUtil.drop(i);
                                          throwed++;
                                          Thread.sleep(ClientUtil.getRandomDoubleInMillis(minDelayMs.getRoundedValue(), maxDelayMs.getRoundedValue()));
                                       }
                                    } else {
                                       ItemUtil.drop(i);
                                       throwed++;
                                       Thread.sleep(ClientUtil.getRandomDoubleInMillis(minDelayMs.getRoundedValue(), maxDelayMs.getRoundedValue()));
                                    }
                                 }
                              }
                           }

                           this.isProcessing = false;
                        } else {
                           this.isProcessing = false;
                        }
                     }catch (InterruptedException e){
                        e.printStackTrace();
                     }
                  }
               )
               .start();
         }
      } else {
         this.isProcessing = false;
      }
   }

   private boolean shouldAbort() {
      if (this.mc.currentScreen instanceof GuiInventory && this.isEnabled()) {
         return false;
      } else {
         this.isProcessing = false;
         return true;
      }
   }

   // $VF: Irreducible bytecode was duplicated to produce valid code
   static {
      generalSettings = new DescriptionSetting("General settings");
      startDelayMs = new SliderSetting("Start-delay", 50.0, 0.0, 1000.0, 1.0);
      minDelayMs = new SliderSetting("Min-delay", 50.0, 0.0, 1000.0, 1.0);
      maxDelayMs = new SliderSetting("Max-delay", 50.0, 0.0, 1000.0, 1.0);
      autoArmor = new BooleanSetting("Auto-armor", true);
      autoThrowTrash = new BooleanSetting("Throw-trash", true);
      trashSettings = new DescriptionSetting("Trash settings");
      onlyConfiguredTrash = new BooleanSetting("Only-items-configured-are-trash", false);
      projectilesTrash = new BooleanSetting("Projectiles-is-trash", false);
      discardNormalFood = new BooleanSetting("Normal-food-is-trash", true);
      bowTrash = new BooleanSetting("Bow-is-trash", true);
      potionTrash = new BooleanSetting("Potion-is-trash", true);
      toolsTrash = new BooleanSetting("Tools-are-trash", true);
      slotSettings = new DescriptionSetting("Slots settings (0 = no sort)");
      swordSlot = new SliderSetting("Sword-slot", 1.0, 0.0, 9.0, 1.0);
      projectilesSlot = new SliderSetting("Projectiles-slot", 3.0, 0.0, 9.0, 1.0);
      blockSlot = new SliderSetting("Block-slot", 2.0, 0.0, 9.0, 1.0);
      foodSlot = new SliderSetting("Food-slot", 9.0, 0.0, 9.0, 1.0);
      bowSlot = new SliderSetting("Bow-slot", 4.0, 0.0, 9.0, 1.0);
      pickaxeSlot = new SliderSetting("Pickaxe-slot", 5.0, 0.0, 9.0, 1.0);
      axeSlot = new SliderSetting("Axe-slot", 6.0, 0.0, 9.0, 1.0);
      shovelSlot = new SliderSetting("Shovel-slot", 7.0, 0.0, 9.0, 1.0);
      potionSlot = new SliderSetting("Potion-slot", 8.0, 0.0, 9.0, 1.0);
      fireballSlot = new SliderSetting("Fireball-slot", 0.0, 0.0, 9.0, 1.0);
      enderPearlSlot = new SliderSetting("Ender-pearl-slot", 0.0, 0.0, 9.0, 1.0);
      shearsSlot = new SliderSetting("Shears-slot", 0.0, 0.0, 9.0, 1.0);
      maxItemSlotsSettings = new DescriptionSetting("Max item slots settings");
      maxBlockSlots = new SliderSetting("Max-block-slots", 10.0, 1.0, 36.0, 1.0);
      maxArrowSlots = new SliderSetting("Max-arrow-slots", 10.0, 1.0, 36.0, 1.0);
      maxTrashThrows = new SliderSetting("Max-trash-throws", 36.0, 1.0, 36.0, 1.0);
   }
}
