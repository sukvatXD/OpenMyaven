package Myaven.module.modules.misc;

import Myaven.Myaven;
import Myaven.events.LivingUpdateEvent;
import Myaven.events.PlayerUpdateEvent;
import Myaven.module.Category;
import Myaven.module.Module;
import Myaven.setting.Setting;
import Myaven.setting.settings.BooleanSetting;
import Myaven.setting.settings.SliderSetting;
import Myaven.util.TimerUtil;
import com.mojang.authlib.GameProfile;

import java.util.*;
import java.util.stream.Collectors;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.EntityIronGolem;
import net.minecraft.entity.monster.EntitySilverfish;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.Vec3;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class AntiBot extends Module {
   public static SliderSetting spawnDelaySetting;
   public static BooleanSetting whitelistGolems;
   public static BooleanSetting whitelistSilverfish;
   public static BooleanSetting useSpawnDelayCheck;
   public static BooleanSetting enableTablistCheck;
   private static Map<EntityPlayer, Long> playerSpawnTimeMap;
   private static Set<EntityPlayer> duplicatePlayers;
   private Map<String, EntityPlayer> nameToPlayerMap;
   private static List<String> tablistNames;
   private TimerUtil timer;
   private static boolean p;
   private static String[] d;
   private static String[] g;
   private static long[] i;
   private static Integer[] j;

   public AntiBot() {
      super("AntiBot", true, Category.Misc, false, "Detect bots");
      this.nameToPlayerMap = new HashMap<>();
      this.timer = new TimerUtil();
      this.addSettings(new Setting[]{spawnDelaySetting, whitelistGolems, whitelistSilverfish, useSpawnDelayCheck, enableTablistCheck});
   }

   public static boolean isBot(Entity entity) {
      if (!Myaven.moduleManager.getModule("antibot").isEnabled()) {
         return false;
      } else if (whitelistGolems.getState() && entity instanceof EntityIronGolem) {
         return false;
      } else if (whitelistSilverfish.getState() && entity instanceof EntitySilverfish) {
         return false;
      } else if (!(entity instanceof EntityPlayer)) {
         return true;
      } else if (useSpawnDelayCheck.getState() && !playerSpawnTimeMap.isEmpty() && playerSpawnTimeMap.containsKey((EntityPlayer)entity)) {
         return true;
      } else if (entity.isDead) {
         return true;
      } else if (entity.getName().isEmpty()) {
         return true;
      } else if (!tablistNames.contains(entity.getName()) && enableTablistCheck.getState()) {
         return true;
      } else if (((EntityPlayer)entity).getHealth() != 20.0F && entity.getName().startsWith("§c")) {
         return true;
      } else {
         if (((EntityPlayer)entity).maxHurtTime == 0) {
            if (((EntityPlayer)entity).getHealth() == 20.0F) {
               String unformattedText = entity.getDisplayName().getUnformattedText();
               if (unformattedText.length() == 10 && unformattedText.charAt(0) != 167) {
                  return true;
               }

               if (unformattedText.length() == 12 && ((EntityPlayer)entity).isPlayerSleeping() && unformattedText.charAt(0) == 167) {
                  return true;
               }

               if (unformattedText.length() >= 7 && unformattedText.charAt(2) == '[' && unformattedText.charAt(3) == 'N' && unformattedText.charAt(6) == ']') {
                  return true;
               }

               if (entity.getName().contains(" ")) {
                  return true;
               }
            } else if (entity.isInvisible()) {
               String unformattedTextx = entity.getDisplayName().getUnformattedText();
               return unformattedTextx.length() >= 3 && unformattedTextx.charAt(0) == 167 && unformattedTextx.charAt(1) == 'c';
            }
         }

         return false;
      }
   }

   @SubscribeEvent
   public void onJoin(EntityJoinWorldEvent entityJoinWorldEvent) {
      if (useSpawnDelayCheck.getState() && entityJoinWorldEvent.entity instanceof EntityPlayer && entityJoinWorldEvent.entity != this.mc.thePlayer) {
         playerSpawnTimeMap.put((EntityPlayer)entityJoinWorldEvent.entity, System.currentTimeMillis());
      }
   }

   @SubscribeEvent
   public void onLivingUpdate(LivingUpdateEvent event) {
      tablistNames = Myaven.mc
         .getNetHandler()
         .getPlayerInfoMap()
         .parallelStream()
         .<GameProfile>map(NetworkPlayerInfo::getGameProfile)
         .filter(profile -> profile.getId() != Myaven.mc.thePlayer.getUniqueID())
         .<String>map(GameProfile::getName)
         .collect(Collectors.toList());
   }

   @SubscribeEvent
   public void onUpdate(PlayerUpdateEvent event) {
      if (useSpawnDelayCheck.getState() && !playerSpawnTimeMap.isEmpty()) {
         playerSpawnTimeMap.values().removeIf(n -> (double)n.longValue() < (double)System.currentTimeMillis() - spawnDelaySetting.getRoundedValue());
      }

      this.nameToPlayerMap.clear();

      for (EntityPlayer p : this.mc.theWorld.playerEntities) {
         if (!duplicatePlayers.contains(p)) {
            String name = p.getName();
            if (this.nameToPlayerMap.containsKey(name)) {
               EntityPlayer exists = this.nameToPlayerMap.get(name);
               Vec3 thePlayer = new Vec3(this.mc.thePlayer.getPosition());
               double existsDistance = thePlayer.distanceTo(new Vec3(exists.getPosition()));
               double curDistance = thePlayer.distanceTo(new Vec3(p.getPosition()));
               if (existsDistance > curDistance) {
                  duplicatePlayers.add(p);
               } else {
                  duplicatePlayers.add(exists);
               }
               break;
            }

            this.nameToPlayerMap.put(name, p);
         }
      }
   }

   @Override
   public void onDisable() {
      playerSpawnTimeMap.clear();
      duplicatePlayers.clear();
      this.nameToPlayerMap.clear();
   }

   // $VF: Irreducible bytecode was duplicated to produce valid code
   static {
      spawnDelaySetting = new SliderSetting("Delay", 7.0, 0.0, 15.0, 1.0);
      whitelistGolems = new BooleanSetting("Whitelist-golem", true);
      whitelistSilverfish = new BooleanSetting("Whitelist-silverfish", true);
      useSpawnDelayCheck = new BooleanSetting("Entity-spawn-delay", true);
      enableTablistCheck = new BooleanSetting("Tablist-check", true);
      playerSpawnTimeMap = new HashMap<>();
      duplicatePlayers = new HashSet<>();
      tablistNames = new ArrayList<>();
   }
}
