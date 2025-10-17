package Myaven.module.modules.visual;

import Myaven.module.Category;
import Myaven.module.Module;
import Myaven.module.modules.config.Teams;
import Myaven.module.modules.misc.AntiBot;
import Myaven.setting.Setting;
import Myaven.setting.settings.PercentageSetting;
import Myaven.setting.settings.SliderSetting;
import Myaven.util.TargetUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;

public class TeamInvisible extends Module {
   public static SliderSetting rangeSetting;
   public static PercentageSetting opacitySetting;

   public TeamInvisible() {
      super("TeamInvisible", false, Category.Visual, true, "Let your teammates be \"Invisible\"");
      this.addSettings(new Setting[]{rangeSetting, opacitySetting});
   }

   @Override
   public String getSuffix() {
      return opacitySetting.getPercentage() + "%";
   }

   public static void applyTransparency(EntityLivingBase entity) {
      Minecraft mc = Minecraft.getMinecraft();
      if (entity.getUniqueID() != mc.thePlayer.getUniqueID()) {
         if (entity instanceof EntityPlayer
            && TargetUtil.isInRange(entity, rangeSetting.getRoundedValue())
            && !AntiBot.isBot(entity)
            && (Teams.isTeammate(entity) || Teams.isManualAlly(entity))) {
            GlStateManager.color(1.0F, 1.0F, 1.0F, (float)opacitySetting.getPercentage() / 100.0F);
            GlStateManager.depthMask(false);
            GlStateManager.enableBlend();
            GlStateManager.blendFunc(770, 771);
            GlStateManager.alphaFunc(516, 0.003921569F);
         }
      }
   }

   public static void resetTransparency(EntityLivingBase entity) {
      Minecraft mc = Minecraft.getMinecraft();
      if (entity.getUniqueID() != mc.thePlayer.getUniqueID()) {
         if (entity instanceof EntityPlayer
            && TargetUtil.isInRange(entity, rangeSetting.getRoundedValue())
            && !AntiBot.isBot(entity)
            && (Teams.isTeammate(entity) || Teams.isManualAlly(entity))) {
            GlStateManager.disableBlend();
            GlStateManager.alphaFunc(516, 0.1F);
            GlStateManager.depthMask(true);
         }
      }
   }

   // $VF: Irreducible bytecode was duplicated to produce valid code
   static {
      rangeSetting = new SliderSetting("Range", 20.0, 1.0, 64.0, 1.0);
      opacitySetting = new PercentageSetting("Opacity", 20);
   }
}
