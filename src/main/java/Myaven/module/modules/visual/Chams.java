package Myaven.module.modules.visual;

import Myaven.module.Category;
import Myaven.module.Module;
import Myaven.module.modules.config.Teams;
import Myaven.module.modules.misc.AntiBot;
import Myaven.setting.Setting;
import Myaven.setting.settings.BooleanSetting;
import java.util.HashSet;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.client.event.RenderPlayerEvent.Post;
import net.minecraftforge.client.event.RenderPlayerEvent.Pre;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.opengl.GL11;

public class Chams extends Module {
   public static BooleanSetting teammates;
   public static BooleanSetting botCheck;
   private HashSet<Entity> processedEntities = new HashSet<>();

   public Chams() {
      super("Chams", false, Category.Visual, false, "Allows you to see entities through blocks");
      this.addSettings(new Setting[]{teammates, botCheck});
   }

   @SubscribeEvent
   public void onPre(Pre e) {
      Entity entity = e.entity;
      if (entity != this.mc.thePlayer) {
         if (teammates.getState() || !Teams.isTeammate((EntityLivingBase)entity)) {
            if (entity instanceof EntityPlayer || entity instanceof EntityAnimal || entity instanceof EntityMob) {
               if (botCheck.getState()) {
                  if (AntiBot.isBot(entity)) {
                     return;
                  }

                  this.processedEntities.add(entity);
               }

               GL11.glEnable(32823);
               GL11.glPolygonOffset(1.0F, -2500000.0F);
            }
         }
      }
   }

   @SubscribeEvent
   public void onPost(Post e) {
      Entity entity = e.entity;
      if (entity != this.mc.thePlayer) {
         if (teammates.getState() || !Teams.isTeammate((EntityLivingBase)entity)) {
            if (entity instanceof EntityPlayer || entity instanceof EntityAnimal || entity instanceof EntityMob) {
               if (botCheck.getState()) {
                  if (!this.processedEntities.contains(entity)) {
                     return;
                  }

                  this.processedEntities.remove(entity);
               }

               GL11.glDisable(32823);
               GL11.glPolygonOffset(1.0F, 2500000.0F);
            }
         }
      }
   }

   // $VF: Irreducible bytecode was duplicated to produce valid code
   static {
      teammates = new BooleanSetting("Teammates", true);
      botCheck = new BooleanSetting("Bot-check", true);
   }
}
