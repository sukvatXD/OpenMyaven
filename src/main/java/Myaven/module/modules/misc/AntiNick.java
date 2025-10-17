package Myaven.module.modules.misc;

import Myaven.events.LivingUpdateEvent;
import Myaven.module.Category;
import Myaven.module.Module;
import Myaven.setting.Setting;
import Myaven.setting.settings.TextSetting;
import Myaven.util.ClientUtil;
import java.util.UUID;
import net.minecraft.client.Minecraft;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class AntiNick extends Module {
   public static TextSetting suffixText;

   public AntiNick() {
      super("AntiNick", false, Category.Misc, false, "Allows you to see if any players is nicked");
      this.addSettings(new Setting[]{suffixText});
   }

   private static boolean isUUIDVersion1(UUID uuid) {
      return uuid.version() == 1;
   }

   @SubscribeEvent
   public void onUpdate(LivingUpdateEvent event) {
      if (ClientUtil.isWorldLoaded()) {
         for (EntityPlayer player : Minecraft.getMinecraft().theWorld.playerEntities) {
            if (player != this.mc.thePlayer) {
               for (NetworkPlayerInfo info : this.mc.getNetHandler().getPlayerInfoMap()) {
                  if (info.getGameProfile().getName() != null
                     && info.getGameProfile().getName().equalsIgnoreCase(player.getGameProfile().getName())
                     && isUUIDVersion1(info.getGameProfile().getId())
                     && player.getName() != null) {
                     String playerName = player.getName();
                     ScorePlayerTeam team = this.mc.theWorld.getScoreboard().getPlayersTeam(playerName);
                     if (team != null) {
                        team.setNameSuffix(" " + suffixText.getCustomText());
                        break;
                     }
                  }
               }
            }
         }
      }
   }

   // $VF: Irreducible bytecode was duplicated to produce valid code
   static {
      suffixText = new TextSetting("Suffix", "§l§e[§l§eNICK§r§l§e]§r");
   }
}
