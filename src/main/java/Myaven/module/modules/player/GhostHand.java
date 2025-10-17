package Myaven.module.modules.player;

import Myaven.module.Category;
import Myaven.module.Module;
import Myaven.module.modules.config.Teams;
import Myaven.setting.Setting;
import Myaven.setting.settings.BooleanSetting;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemSword;
import net.minecraft.item.ItemTool;

public class GhostHand extends Module {
   public static BooleanSetting notWhileHoldingSword;
   public static BooleanSetting teammatesOnly;
   public static BooleanSetting playersOnly;
   public static BooleanSetting toolsOnly;

   public GhostHand() {
      super("GhostHand", false, Category.Player, true, "Allows you to interact through entity");
      this.addSettings(new Setting[]{notWhileHoldingSword, teammatesOnly, playersOnly, toolsOnly});
   }

   public static void filterEntitiesForGhostHand(List<Entity> list) {
      list.removeIf(GhostHand::shouldRemoveEntity);
   }

   private static boolean shouldRemoveEntity(Entity entity) {
      Minecraft mc = Minecraft.getMinecraft();
      return (
            notWhileHoldingSword.getState() && (mc.thePlayer.getHeldItem() == null || !(mc.thePlayer.getHeldItem().getItem() instanceof ItemSword))
               || !notWhileHoldingSword.getState()
         )
         && (teammatesOnly.getState() && entity instanceof EntityLivingBase && Teams.isTeammate((EntityLivingBase)entity) || !teammatesOnly.getState())
         && (toolsOnly.getState() && mc.thePlayer.getHeldItem() != null && mc.thePlayer.getHeldItem().getItem() instanceof ItemTool || !toolsOnly.getState())
         && (playersOnly.getState() && entity instanceof EntityPlayer || !playersOnly.getState());
   }

   // $VF: Irreducible bytecode was duplicated to produce valid code
   static {
      notWhileHoldingSword = new BooleanSetting("Not-while-holding-sword", true);
      teammatesOnly = new BooleanSetting("Teammates-only", true);
      playersOnly = new BooleanSetting("Players-only", true);
      toolsOnly = new BooleanSetting("Tools-only", true);
   }
}
