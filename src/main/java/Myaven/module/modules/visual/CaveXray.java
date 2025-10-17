package Myaven.module.modules.visual;

import Myaven.module.Category;
import Myaven.module.Module;
import Myaven.setting.Setting;
import Myaven.setting.settings.PercentageSetting;
import java.awt.Color;
import net.minecraft.util.BlockPos;

public class CaveXray extends Module {
   public static PercentageSetting opacity;
   private boolean isActive = false;

   public CaveXray() {
      super("CaveXray", false, Category.Visual, true, "Allows you to see structures underground (Only works with optifine)");
      this.addSettings(new Setting[]{opacity});
   }

   @Override
   public String getSuffix() {
      return opacity.getPercentage() + "%";
   }

   public static int computeAlphaFromOpacity() {
      return 255 * opacity.getPercentage() / 100;
   }

   public static int applyAlpha140(int j) {
      Color c = new Color(j);
      return new Color(c.getRed(), c.getGreen(), c.getBlue(), 140).getRGB();
   }

   @Override
   public void onEnable() {
      if (!this.isActive) {
         this.markRenderUpdateAroundPlayer(900);
      }

      this.isActive = true;
   }

   @Override
   public void onDisable() {
      if (this.isActive) {
         this.markRenderUpdateAroundPlayer(900);
      }

      this.isActive = false;
   }

   private void markRenderUpdateAroundPlayer(int radius) {
      BlockPos pos = new BlockPos(this.mc.thePlayer.posX, this.mc.thePlayer.posY, this.mc.thePlayer.posZ);
      this.mc
         .renderGlobal
         .markBlockRangeForRenderUpdate(
            pos.getX() - radius, pos.getY() - radius, pos.getZ() - radius, pos.getX() + radius, pos.getY() + radius, pos.getZ() + radius
         );
   }

   // $VF: Irreducible bytecode was duplicated to produce valid code
   static {
      opacity = new PercentageSetting("Opacity", 60);
   }
}
