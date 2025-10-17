package Myaven.module.modules.visual;

import Myaven.module.Category;
import Myaven.module.Module;
import Myaven.setting.Setting;
import Myaven.setting.settings.BooleanSetting;
import Myaven.setting.settings.SliderSetting;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.util.StringUtils;

public class ItemScale extends Module {
   public static SliderSetting scale;
   public static BooleanSetting megaWalls;
   public static BooleanSetting bedwars;
   public static BooleanSetting swordAndBows;
   public static BooleanSetting gApple;
   public static BooleanSetting all;
   public static BooleanSetting nbtOnly;

   public ItemScale() {
      super("ItemScale", false, Category.Visual, false, "Scale the dropped items");
      this.addSettings(new Setting[]{scale, megaWalls, bedwars, swordAndBows, gApple, all, nbtOnly});
   }

   @Override
   public String getSuffix() {
      return String.valueOf(scale.getRoundedValue());
   }

   public static boolean onScale(ItemStack itemStack) {
      Item item = itemStack.getItem();
      String itemName = itemStack.getDisplayName();
      String stripItemName = StringUtils.stripControlCodes(itemName);
      if (all.getState()) {
         return nbtOnly.getState() && itemStack.hasTagCompound() || !nbtOnly.getState();
      } else if (nbtOnly.getState()) {
         return itemStack.hasTagCompound();
      } else if (!megaWalls.getState()
         || !stripItemName.startsWith("Phoenix's Tears of Regen")
            && !stripItemName.startsWith("Squid's Absorption")
            && !stripItemName.startsWith("Matey")
            && !stripItemName.startsWith("Regen-Ade")
            && !stripItemName.startsWith("Ultra Pasteurized Milk Bucket")
            && !stripItemName.startsWith("Junk Apple")
            && item != Items.pumpkin_pie
            && item != Items.golden_apple
            && item != Items.diamond
            && item != Items.diamond_sword
            && !ItemTags.isDiamond(item)) {
         if (!bedwars.getState() || item != Items.diamond && item != Items.gold_ingot && item != Items.iron_ingot && item != Items.emerald) {
            return !swordAndBows.getState() || !(item instanceof ItemSword) && !(item instanceof ItemBow)
               ? gApple.getState() && item == Items.golden_apple
               : true;
         } else {
            return true;
         }
      } else {
         return true;
      }
   }

   // $VF: Irreducible bytecode was duplicated to produce valid code
   static {
      scale = new SliderSetting("Scale", 1.0, 0.01, 5.0, 0.01);
      megaWalls = new BooleanSetting("Megawalls-items", false);
      bedwars = new BooleanSetting("Bedwars-resources", true);
      swordAndBows = new BooleanSetting("Render-swords-and-bows", false);
      gApple = new BooleanSetting("Render-golden-apples", true);
      all = new BooleanSetting("Render-ALL", false);
      nbtOnly = new BooleanSetting("NBT-only", false);
   }
}
