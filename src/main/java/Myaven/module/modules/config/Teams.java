package Myaven.module.modules.config;

import Myaven.module.Category;
import Myaven.module.Module;
import Myaven.setting.Setting;
import Myaven.setting.settings.DescriptionSetting;
import Myaven.setting.settings.ModeSetting;
import Myaven.setting.settings.TextSetting;
import Myaven.util.ColorUtil;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Items;
import net.minecraft.item.ItemArmor;
import net.minecraft.util.EnumChatFormatting;

public class Teams extends Module {
   public static DescriptionSetting modesDescriptionPart1;
   public static DescriptionSetting modesDescriptionPart2;
   public static ModeSetting mode;
   public static TextSetting customPatternRegex;
   public static List<String> allyNames;
   public static List<String> enemyNames;
   public static String warningPrefixYellow;
   public static String warningPrefixDarkRed;
   public static String warningPrefixLightPurple;
   public static String specialPrefixS;

   public Teams() {
      super("Teams", true, Category.Configuration, false, "Configure the teaming system", false, true);
      this.addSettings(new Setting[]{modesDescriptionPart1, modesDescriptionPart2, mode, customPatternRegex});
   }

   public static boolean isTeammate(EntityLivingBase entity) {
      if (entity == null) {
         return false;
      } else if (allyNames.contains(entity.getName())) {
         return true;
      } else if (enemyNames.contains(entity.getName())) {
         return false;
      } else {
         Minecraft mc = Minecraft.getMinecraft();
         if (mode.getCurrent().equalsIgnoreCase("NONE")) {
            return false;
         } else {
            String var3 = mode.getCurrent();
            switch (var3) {
               case "PATTERN":
                  Pattern pattern = Pattern.compile(customPatternRegex.getCustomText());
                  Matcher matcher = pattern.matcher(entity.getDisplayName().getFormattedText());
                  if (matcher.find()) {
                     Matcher matcher2 = pattern.matcher(mc.thePlayer.getDisplayName().getFormattedText());
                     if (matcher2.find()) {
                        if (matcher2.group().equalsIgnoreCase(matcher.group())) {
                           return true;
                        }

                        return false;
                     }

                     return false;
                  }

                  return false;
               case "NAME_COLOR":
                  if (mc.thePlayer.getDisplayName() != null && entity.getDisplayName() != null) {
                     String targetName = entity.getDisplayName().getFormattedText();
                     String clientName = mc.thePlayer.getDisplayName().getFormattedText();
                     targetName = targetName.replace(warningPrefixYellow, "");
                     clientName = clientName.replace(warningPrefixYellow, "");
                     targetName = targetName.replace(warningPrefixDarkRed, "");
                     clientName = clientName.replace(warningPrefixDarkRed, "");
                     targetName = targetName.replace(warningPrefixLightPurple, "");
                     clientName = clientName.replace(warningPrefixLightPurple, "");
                     targetName = targetName.replace(specialPrefixS, "");
                     clientName = clientName.replace(specialPrefixS, "");
                     targetName = targetName.replace("§r", "");
                     clientName = clientName.replace("§r", "");
                     targetName = targetName.replace("§k", "");
                     clientName = clientName.replace("§k", "");
                     targetName = targetName.replace("§l", "");
                     clientName = clientName.replace("§l", "");
                     Pattern pattern2 = Pattern.compile("§[A-Za-z0-9]");
                     Matcher tN = pattern2.matcher(targetName);
                     Matcher cN = pattern2.matcher(clientName);
                     if (tN.find() && cN.find() && tN.group().equalsIgnoreCase(cN.group())) {
                        return true;
                     }
                  }

                  return false;
               case "ARMOR_COLOR":
                  if (mc.thePlayer.getEquipmentInSlot(4) != null
                     && mc.thePlayer.getEquipmentInSlot(4).getItem() == Items.leather_helmet
                     && entity.getEquipmentInSlot(4) != null
                     && entity.getEquipmentInSlot(4).getItem() == Items.leather_helmet
                     && ((ItemArmor)entity.getEquipmentInSlot(4).getItem()).getColor(entity.getEquipmentInSlot(4))
                        == ((ItemArmor)entity.getEquipmentInSlot(4).getItem()).getColor(mc.thePlayer.getEquipmentInSlot(4))) {
                     return true;
                  }

                  return false;
               default:
                  return entity.isOnSameTeam(mc.thePlayer);
            }
         }
      }
   }

   public static int getEntityTeamColor(EntityLivingBase entity) {
      if (allyNames.contains(entity.getName())) {
         return ColorUtil.getColorByCode("2").getRGB();
      } else if (enemyNames.contains(entity.getName())) {
         return ColorUtil.getColorByCode("4").getRGB();
      } else {
         String var2 = mode.getCurrent();
         byte var3 = -1;
         switch (var2.hashCode()) {
            case 1280995363:
               if (var2.equals("ARMOR_COLOR")) {
                  var3 = 0;
               }
            default:
               switch (var3) {
                  case 0:
                     if (entity.getEquipmentInSlot(4) != null
                        && entity.getEquipmentInSlot(4).getItem() == Items.leather_helmet
                        && Items.leather_helmet.hasColor(entity.getEquipmentInSlot(4))) {
                        return Items.leather_helmet.getColor(entity.getEquipmentInSlot(4));
                     }
                  default:
                     if (entity.getDisplayName() != null) {
                        String targetName = entity.getDisplayName().getFormattedText();
                        targetName = targetName.replace(warningPrefixYellow, "");
                        targetName = targetName.replace(warningPrefixDarkRed, "");
                        targetName = targetName.replace(warningPrefixLightPurple, "");
                        targetName = targetName.replace(specialPrefixS, "");
                        targetName = targetName.replace("§r", "");
                        targetName = targetName.replace("§k", "");
                        targetName = targetName.replace("§l", "");
                        Pattern pattern2 = Pattern.compile("§[A-Za-z0-9]");
                        Matcher tN = pattern2.matcher(targetName);
                        return tN.find() ? ColorUtil.getColorByChar(tN.group().charAt(1)).getRGB() : 16777215;
                     } else {
                        return 16777215;
                     }
               }
         }
      }
   }

   public static boolean isManualAlly(EntityLivingBase entity) {
      return entity == null ? false : allyNames.contains(entity.getName());
   }

   public static boolean isManualEnemy(EntityLivingBase entity) {
      return entity == null ? false : enemyNames.contains(entity.getName());
   }

   public static void addAlly(String name) {
      if (name != null) {
         if (!enemyNames.contains(name)) {
            allyNames.add(name);
         }
      }
   }

   public static void addEnemy(String name) {
      if (name != null) {
         if (!allyNames.contains(name)) {
            enemyNames.add(name);
         }
      }
   }

   public static void clearAllies() {
      allyNames.clear();
   }

   public static void clearEnemies() {
      enemyNames.clear();
   }

   // $VF: Irreducible bytecode was duplicated to produce valid code
   static {
      modesDescriptionPart1 = new DescriptionSetting("NAME_COLOR, PATTERN,");
      modesDescriptionPart2 = new DescriptionSetting("ARMOR_COLOR, VANILLA, NONE");
      mode = new ModeSetting("Sort-mode", "NAME_COLOR", "PATTERN", "ARMOR_COLOR", "VANILLA", "NONE");
      customPatternRegex = new TextSetting("Custom-pattern-regex", "\\[[A-Z]\\]");
      allyNames = new ArrayList<>();
      enemyNames = new ArrayList<>();
      warningPrefixYellow = EnumChatFormatting.YELLOW.toString() + EnumChatFormatting.BOLD + "⚠ " + EnumChatFormatting.RESET;
      warningPrefixDarkRed = EnumChatFormatting.DARK_RED.toString() + EnumChatFormatting.BOLD + "⚠ " + EnumChatFormatting.RESET;
      warningPrefixLightPurple = EnumChatFormatting.LIGHT_PURPLE.toString() + EnumChatFormatting.BOLD + "⚠ " + EnumChatFormatting.RESET;
      specialPrefixS = EnumChatFormatting.GOLD
              + "["
              + EnumChatFormatting.DARK_GREEN
              + "S"
              + EnumChatFormatting.GOLD
              + "] "
              + EnumChatFormatting.RESET;
   }
}
