package Myaven.util;

import Myaven.Myaven;
import Myaven.mixins.accessor.AccessorGuiScreen;
import akka.japi.Pair;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import net.minecraft.block.Block;
import net.minecraft.block.BlockFalling;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.init.Items;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemBlock;
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
import net.minecraft.util.DamageSource;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.input.Mouse;

public class ItemUtil {

   public static int getArmorValue(ItemStack itemStack) {
      if (itemStack == null) {
         return 0;
      } else {
         int level = 0;
         Item item = itemStack.getItem();
         if (item == Items.diamond_helmet || item == Items.diamond_chestplate || item == Items.diamond_leggings || item == Items.diamond_boots) {
            level += 15;
         } else if (item == Items.iron_helmet || item == Items.iron_chestplate || item == Items.iron_leggings || item == Items.iron_boots) {
            level += 10;
         } else if (item == Items.golden_helmet || item == Items.golden_chestplate || item == Items.golden_leggings || item == Items.golden_boots) {
            level += 5;
         } else if (item == Items.chainmail_helmet || item == Items.chainmail_chestplate || item == Items.chainmail_leggings || item == Items.chainmail_boots) {
            level += 5;
         }

         return level + getDamageReduction(itemStack);
      }
   }

   public static int getDamageReduction(@NotNull ItemStack itemStack) {
      return ((ItemArmor)itemStack.getItem()).damageReduceAmount
         + EnchantmentHelper.getEnchantmentModifierDamage(new ItemStack[]{itemStack}, DamageSource.generic);
   }

   public static double getWeaponDamage(ItemStack itemStack) {
      if (itemStack == null) {
         return 0.0;
      } else {
         double getAmount = 0.0;

         for (Entry<String, AttributeModifier> entry : itemStack.getAttributeModifiers().entries()) {
            if (entry.getKey().equals("generic.attackDamage")) {
               getAmount = entry.getValue().getAmount();
               break;
            }
         }

         getAmount += (double)EnchantmentHelper.getEnchantmentLevel(Enchantment.fireAspect.effectId, itemStack) * 1.25;
         return getAmount + (double)EnchantmentHelper.getEnchantmentLevel(Enchantment.sharpness.effectId, itemStack) * 1.25;
      }
   }

   public static float getBlockBreakingSpeed(ItemStack itemStack, Block block) {
      if (itemStack == null) {
         return 0.0F;
      } else {
         float getStrVsBlock = itemStack.getStrVsBlock(block);
         if (getStrVsBlock > 1.0F) {
            int getEnchantmentLevel = EnchantmentHelper.getEnchantmentLevel(Enchantment.efficiency.effectId, itemStack);
            if (getEnchantmentLevel > 0) {
               getStrVsBlock += (float)(getEnchantmentLevel * getEnchantmentLevel + 1);
            }
         }

         return getStrVsBlock;
      }
   }

   public static float getToolEfficiency(ItemStack stack) {
      if (stack == null) {
         return 0.0F;
      } else {
         Item item = stack.getItem();
         int level = EnchantmentHelper.getEnchantmentLevel(Enchantment.efficiency.effectId, stack);
         switch (level) {
            case 1: {
               int n = 30;
               break;
            }
            case 2: {
               int n = 69;
               break;
            }
            case 3: {
               int n = 120;
               break;
            }
            case 4: {
               int n = 186;
               break;
            }
            case 5: {
               int n = 271;
               break;
            }
            default:
               level = 0;
               boolean axe = false;
         }

         if (item instanceof ItemPickaxe) {
            ItemPickaxe pickaxe = (ItemPickaxe)item;
            return pickaxe.getToolMaterial().getEfficiencyOnProperMaterial() + (float)level;
         } else if (item instanceof ItemSpade) {
            ItemSpade shovel = (ItemSpade)item;
            return shovel.getToolMaterial().getEfficiencyOnProperMaterial() + (float)level;
         } else if (item instanceof ItemAxe) {
            ItemAxe axe = (ItemAxe)item;
            return axe.getToolMaterial().getEfficiencyOnProperMaterial() + (float)level;
         } else {
            return 0.0F;
         }
      }
   }

   public static void drop(int slot) {
      Myaven.mc.playerController.windowClick(Myaven.mc.thePlayer.inventoryContainer.windowId, convertSlotIndex(slot), 1, 4, Myaven.mc.thePlayer);
   }

   public static void click(int slot) {
      Myaven.mc.playerController.windowClick(Myaven.mc.thePlayer.inventoryContainer.windowId, convertSlotIndex(slot), 0, 1, Myaven.mc.thePlayer);
   }

   public static void swap(int slot, int destination) {
      Myaven.mc
         .playerController
         .windowClick(Myaven.mc.thePlayer.inventoryContainer.windowId, convertSlotIndex(slot), convertSlotIndex(destination), 2, Myaven.mc.thePlayer);
   }

   public static boolean hasItems() {
      int i = 9;

      while (i < 45) {
         if (Myaven.mc.thePlayer.inventoryContainer.getSlot(i).getStack() == null) {
            return false;
         }

         i++;
      }

      return true;
   }

   public static boolean isPlaceableBlock(ItemStack stack) {
      if (stack != null) {
         if (stack.getItem() instanceof ItemBlock
            && (
               BlockUtil.isLiquidBlock(Block.getBlockFromItem(stack.getItem()))
                  || BlockUtil.isInteractableBlock(Block.getBlockFromItem(stack.getItem()))
                  || BlockUtil.isFurnace(Block.getBlockFromItem(stack.getItem()))
            )) {
            return false;
         }

         if (stack.getItem() instanceof ItemBlock
            && ((ItemBlock)stack.getItem()).getBlock().isFullCube()
            && !(((ItemBlock)stack.getItem()).getBlock() instanceof BlockFalling)) {
            return true;
         }
      }

      return false;
   }

   public static List<Pair<ItemStack, Integer>> getBestArmorSet(IInventory inventory) {
      List<Pair<ItemStack, Integer>> armors = new ArrayList<>();
      armors.add(new Pair(null, 39));
      armors.add(new Pair(null, 38));
      armors.add(new Pair(null, 37));
      armors.add(new Pair(null, 36));
      ItemStack bestHelmet = null;
      ItemStack bestChestplate = null;
      ItemStack bestLeggings = null;
      ItemStack bestBoots = null;

      for (int i = 0; i < inventory.getSizeInventory(); i++) {
         ItemStack item = inventory.getStackInSlot(i);
         if (item != null && item.getItem() instanceof ItemArmor) {
            if (((ItemArmor)item.getItem()).armorType == 0) {
               if (getArmorValue(item) > getArmorValue(bestHelmet)) {
                  bestHelmet = item;
                  armors.set(0, new Pair(item, i));
               }
            } else if (((ItemArmor)item.getItem()).armorType == 1) {
               if (getArmorValue(item) > getArmorValue(bestChestplate)) {
                  bestChestplate = item;
                  armors.set(1, new Pair(item, i));
               }
            } else if (((ItemArmor)item.getItem()).armorType == 2) {
               if (getArmorValue(item) > getArmorValue(bestLeggings)) {
                  bestLeggings = item;
                  armors.set(2, new Pair(item, i));
               }
            } else if (((ItemArmor)item.getItem()).armorType == 3 && getArmorValue(item) > getArmorValue(bestBoots)) {
               bestBoots = item;
               armors.set(3, new Pair(item, i));
            }
         }
      }

      return armors;
   }

   public static Pair<ItemStack, Integer> getBestSword(IInventory inventory) {
      Pair<ItemStack, Integer> best = new Pair(null, null);

      for (int i = 0; i < inventory.getSizeInventory(); i++) {
         ItemStack item = inventory.getStackInSlot(i);
         if (item != null && item.getItem() instanceof ItemSword && getWeaponDamage(item) > getWeaponDamage((ItemStack)best.first())) {
            best = new Pair(item, i);
         }
      }

      return best;
   }

   public static Pair<ItemStack, Integer> getBestBow(IInventory inventory) {
      Pair best = new Pair(null, null);

      for (int i = 0; i < inventory.getSizeInventory(); i++) {
         ItemStack item = inventory.getStackInSlot(i);
         if (item != null && item.getItem() instanceof ItemBow && getUnbreakingScore(item) > getUnbreakingScore((ItemStack)best.first())) {
            best = new Pair(item, i);
         }
      }

      return best;
   }

   public static List<Pair<ItemStack, Integer>> getBestTools(IInventory inventory) {
      ArrayList best = new ArrayList();
      best.add(new Pair(null, null));
      best.add(new Pair(null, null));
      best.add(new Pair(null, null));
      ItemStack bestPickaxe = null;
      ItemStack bestAxe = null;
      ItemStack bestShovel = null;

      for (int i = 0; i < inventory.getSizeInventory(); i++) {
         ItemStack item = inventory.getStackInSlot(i);
         if (item != null && item.getItem() instanceof ItemTool) {
            if (item.getItem() instanceof ItemPickaxe && getToolEfficiency(bestPickaxe) < getToolEfficiency(item)) {
               bestPickaxe = item;
               best.set(0, new Pair(item, i));
            } else if (item.getItem() instanceof ItemAxe && getToolEfficiency(bestAxe) < getToolEfficiency(item)) {
               bestAxe = item;
               best.set(1, new Pair(item, i));
            } else if (item.getItem() instanceof ItemSpade && getToolEfficiency(bestShovel) < getToolEfficiency(item)) {
               bestShovel = item;
               best.set(2, new Pair(item, i));
            }
         }
      }

      return best;
   }

   public static Pair<ItemStack, Integer> getBestPickaxe(IInventory inventory) {
      Pair Best = new Pair(null, null);

      for (int i = 0; i < inventory.getSizeInventory(); i++) {
         ItemStack item = inventory.getStackInSlot(i);
         if (item != null && (item.getItem() instanceof ItemSnowball || item.getItem() instanceof ItemEgg || item.getItem() instanceof ItemFishingRod)) {
            if (Best.first() == null) {
               Best = new Pair(item, i);
            } else if (!(((ItemStack)Best.first()).getItem() instanceof ItemFishingRod)
               || !(item.getItem() instanceof ItemSnowball) && !(item.getItem() instanceof ItemEgg)) {
               if ((((ItemStack)Best.first()).getItem() instanceof ItemEgg || ((ItemStack)Best.first()).getItem() instanceof ItemEgg)
                  && (item.getItem() instanceof ItemSnowball || item.getItem() instanceof ItemEgg)
                  && item.stackSize > ((ItemStack)Best.first()).stackSize) {
                  Best = new Pair(item, i);
               }
            } else {
               Best = new Pair(item, i);
            }
         }
      }

      return Best;
   }

   public static Pair<ItemStack, Integer> getBestBuildingBlock(IInventory inventory) {
      Pair<ItemStack, Integer> Best = new Pair(null, null);

      for (int i = 0; i < inventory.getSizeInventory(); i++) {
         ItemStack item = inventory.getStackInSlot(i);
         if (item != null && isPlaceableBlock(item)) {
            if (Best.first() == null) {
               Best = new Pair(item, i);
            } else if (item.stackSize > ((ItemStack)Best.first()).stackSize) {
               Best = new Pair(item, i);
            }
         }
      }

      return Best;
   }

   public static Pair<ItemStack, Integer> getBestFood(IInventory inventory) {
      Pair Best = new Pair(null, null);

      for (int i = 0; i < inventory.getSizeInventory(); i++) {
         ItemStack item = inventory.getStackInSlot(i);
         if (item != null && item.getItem() instanceof ItemFood) {
            if (Best.first() == null) {
               Best = new Pair(item, i);
            } else if (((ItemStack)Best.first()).getItem() != Items.golden_apple && item.getItem() == Items.golden_apple) {
               Best = new Pair(item, i);
            } else if (((ItemStack)Best.first()).getItem() == Items.golden_apple
               && item.getItem() == Items.golden_apple
               && !((ItemStack)Best.first()).isItemEnchanted()
               && item.isItemEnchanted()) {
               Best = new Pair(item, i);
            } else if (((ItemStack)Best.first()).getItem() == Items.golden_apple
               && item.getItem() == Items.golden_apple
               && ((ItemStack)Best.first()).isItemEnchanted()
               && item.isItemEnchanted()
               && item.stackSize > ((ItemStack)Best.first()).stackSize) {
               Best = new Pair(item, i);
            } else if (((ItemStack)Best.first()).getItem() == Items.golden_apple
               && item.getItem() == Items.golden_apple
               && item.stackSize > ((ItemStack)Best.first()).stackSize
               && !((ItemStack)Best.first()).isItemEnchanted()
               && !item.isItemEnchanted()) {
               Best = new Pair(item, i);
            } else if (((ItemStack)Best.first()).getItem() != Items.golden_apple
               && item.getItem() != Items.golden_apple
               && item.stackSize > ((ItemStack)Best.first()).stackSize) {
               Best = new Pair(item, i);
            }
         }
      }

      return Best;
   }

   public static Pair<ItemStack, Integer> getPotion(IInventory inventory) {
      Pair Best = new Pair(null, null);

      for (int i = 0; i < inventory.getSizeInventory(); i++) {
         ItemStack item = inventory.getStackInSlot(i);
         if (item != null && item.getItem() instanceof ItemPotion && Best.first() == null) {
            Best = new Pair(item, i);
         }
      }

      return Best;
   }

   public static Pair<ItemStack, Integer> getFireball(IInventory inventory) {
      Pair Best = new Pair(null, null);

      for (int i = 0; i < inventory.getSizeInventory(); i++) {
         ItemStack item = inventory.getStackInSlot(i);
         if (item != null && item.getItem() instanceof ItemFireball && Best.first() == null) {
            Best = new Pair(item, i);
         }
      }

      return Best;
   }

   public static Pair<ItemStack, Integer> getEnderPearl(IInventory inventory) {
      Pair Best = new Pair(null, null);

      for (int i = 0; i < inventory.getSizeInventory(); i++) {
         ItemStack item = inventory.getStackInSlot(i);
         if (item != null && item.getItem() instanceof ItemEnderPearl && Best.first() == null) {
            Best = new Pair(item, i);
         }
      }

      return Best;
   }

   public static Pair<ItemStack, Integer> getShears(IInventory inventory) {
      Pair Best = new Pair(null, null);

      for (int i = 0; i < inventory.getSizeInventory(); i++) {
         ItemStack item = inventory.getStackInSlot(i);
         if (item != null && item.getItem() instanceof ItemShears && Best.first() == null) {
            Best = new Pair(item, i);
         }
      }

      return Best;
   }

   public static boolean containsItem(Item item, IInventory inventory) {

      for (int i = 0; i < inventory.getSizeInventory(); i++) {
         ItemStack it = inventory.getStackInSlot(i);
         if (it != null && item != null && item == it.getItem()) {
            return true;
         }
      }

      return false;
   }

   public static int convertSlotIndex(int slot) {
      if (slot >= 36) {
         return 8 - (slot - 36);
      } else {
         return slot < 9 ? slot + 36 : slot;
      }
   }

   public static float getUnbreakingScore(ItemStack stack) {
      if (stack == null) {
         return 0.0F;
      } else {
         float score = 0.0F;
         Item item = stack.getItem(); if (!(item instanceof ItemBow)) {
            return score;
         }

         score = 0.0F + (float)EnchantmentHelper.getEnchantmentLevel(Enchantment.power.effectId, stack);
         score += (float)EnchantmentHelper.getEnchantmentLevel(Enchantment.infinity.effectId, stack);
         score += (float)EnchantmentHelper.getEnchantmentLevel(Enchantment.flame.effectId, stack) * 0.5F;
         score += (float)EnchantmentHelper.getEnchantmentLevel(Enchantment.unbreaking.effectId, stack) * 0.1F;
         return score;
      }
   }

   public static void simulateClick(@NotNull GuiScreen s) {
      int x = Mouse.getX() * s.width / Myaven.mc.displayWidth;
      int y = s.height - Mouse.getY() * s.height / Myaven.mc.displayHeight - 1;
      ((AccessorGuiScreen)s).invokeMouseClicked(x, y, 0);
   }

   private static RuntimeException a(RuntimeException runtimeException) {
      return runtimeException;
   }
}
