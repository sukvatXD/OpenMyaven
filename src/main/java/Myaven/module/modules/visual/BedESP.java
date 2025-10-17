package Myaven.module.modules.visual;

import Myaven.events.PlayerUpdateEvent;
import Myaven.events.Render3DEvent;
import Myaven.module.Category;
import Myaven.module.Module;
import Myaven.module.modules.config.Theme;
import Myaven.setting.Setting;
import Myaven.setting.settings.ColorSetting;
import Myaven.setting.settings.DescriptionSetting;
import Myaven.setting.settings.ModeSetting;
import Myaven.setting.settings.PercentageSetting;
import Myaven.setting.settings.SliderSetting;
import Myaven.util.BlockUtil;
import Myaven.util.ClientUtil;
import Myaven.util.RenderUtil;
import Myaven.util.TimerUtil;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import net.minecraft.block.BlockBed;
import net.minecraft.block.BlockBed.EnumPartType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.opengl.GL11;

public class BedESP extends Module {
   public static SliderSetting range;
   public static PercentageSetting opacity;
   public static DescriptionSetting themeDescription;
   public static ModeSetting color;
   public static ColorSetting customColor;
   private CopyOnWriteArrayList<BlockPos[]> beds = new CopyOnWriteArrayList<>();
   private TimerUtil timer = new TimerUtil();
   private boolean scan = false;

   public BedESP() {
      super("BedESP", false, Category.Visual, false, "Show ESP on beds");
      this.addSettings(new Setting[]{range, opacity, themeDescription, color, customColor});
   }

   @SubscribeEvent
   public void onJoin(@NotNull EntityJoinWorldEvent e) {
      if (e.entity != this.mc.thePlayer) {
         return;
      }

      this.scan = false;
      this.beds.clear();
   }

   @SubscribeEvent
   public void onUpdate(PlayerUpdateEvent event) {
      if (!this.scan && this.timer.hasTimePassed(500, true)) {
         this.scan = true;
         CopyOnWriteArrayList<BlockPos[]> temp = new CopyOnWriteArrayList<>(this.beds);
         new Thread(
               () -> {

                  int i;
                  label56:
                  for (int n = i = (int)range.getRoundedValue(); i >= -n; i--) {
                     for (int j = -n; j <= n; j++) {
                        for (int k = -n; k <= n; k++) {
                           BlockPos blockPos = new BlockPos(
                              this.mc.thePlayer.posX + (double)j, this.mc.thePlayer.posY + (double)i, this.mc.thePlayer.posZ + (double)k
                           );
                           IBlockState getBlockState = this.mc.theWorld.getBlockState(blockPos);
                           if (getBlockState.getBlock() == Blocks.bed && getBlockState.getValue(BlockBed.PART) == EnumPartType.FOOT) {
                              for (int l = 0; l < temp.size(); l++) {
                                 if (BlockUtil.isReplaceable(blockPos, temp.get(l)[0])) {
                                    continue label56;
                                 }
                              }

                              temp.add(new BlockPos[]{blockPos, blockPos.offset((EnumFacing)getBlockState.getValue(BlockBed.FACING))});
                           }
                        }
                     }
                  }

                  this.beds = new CopyOnWriteArrayList<>(temp);
                  this.scan = false;
               }
            )
            .start();
      }
   }

   @SubscribeEvent
   public void onRender(Render3DEvent event) {
      if (ClientUtil.isWorldLoaded()) {
         for (BlockPos[] blockPos : this.beds) {
            if (!(this.mc.theWorld.getBlockState(blockPos[0]).getBlock() instanceof BlockBed)) {
               this.beds.remove(blockPos);
            } else {
               this.drawBedESP(blockPos);
            }
         }
      }
   }

   @Override
   public void onDisable() {
      this.scan = false;
      this.beds.clear();
   }

   private void drawBedESP(BlockPos[] array) {
      double n = (double)array[0].getX() - this.mc.getRenderManager().viewerPosX;
      double n2 = (double)array[0].getY() - this.mc.getRenderManager().viewerPosY;
      double n3 = (double)array[0].getZ() - this.mc.getRenderManager().viewerPosZ;
      GL11.glBlendFunc(770, 771);
      GL11.glEnable(3042);
      GL11.glLineWidth(2.0F);
      GL11.glDisable(3553);
      GL11.glDisable(2929);
      GL11.glDepthMask(false);
      String r = BedESP.color.getCurrent();
      int color;
      switch (r) {
         case "THEME":
            color = Theme.computeThemeColor(0.0);
            break;
         case "THEME_CUSTOM":
            color = Theme.computeCustomThemeColor(0.0);
            break;
         default:
            color = customColor.F();
      }

      float r1 = (float)(color >> 16 & 0xFF) / 255.0F;
      float g = (float)(color >> 8 & 0xFF) / 255.0F;
      float b = (float)(color & 0xFF) / 255.0F;
      AxisAlignedBB axisAlignedBB;
      if (array[0].getX() != array[1].getX()) {
         if (array[0].getX() > array[1].getX()) {
            axisAlignedBB = new AxisAlignedBB(n - 1.0, n2, n3, n + 1.0, n2 + 0.5625, n3 + 1.0);
         } else {
            axisAlignedBB = new AxisAlignedBB(n, n2, n3, n + 2.0, n2 + 0.5625, n3 + 1.0);
         }
      } else if (array[0].getZ() > array[1].getZ()) {
         axisAlignedBB = new AxisAlignedBB(n, n2, n3 - 1.0, n + 1.0, n2 + 0.5625, n3 + 1.0);
      } else {
         axisAlignedBB = new AxisAlignedBB(n, n2, n3, n + 1.0, n2 + 0.5625, n3 + 2.0);
      }

      RenderUtil.renderFilledBox(axisAlignedBB, r1, g, b, (float)opacity.getPercentage() / 100.0F);
      GL11.glEnable(3553);
      GL11.glEnable(2929);
      GL11.glDepthMask(true);
      GL11.glDisable(3042);
   }

   private static boolean positionsEqual(BlockPos[] a, BlockPos[] b) {
      if (a.length != b.length) {
         return false;
      } else {
         for (int i = 0; i < a.length; i++) {
            if (!a[i].equals(b[i])) {
               return false;
            }
         }

         return true;
      }
   }

   public static void stripBeds(CopyOnWriteArrayList<BlockPos[]> beds) {
      List<BlockPos[]> beds2 = new ArrayList<>(beds);
      ArrayList toRemove = new ArrayList();

      for (int i = 0; i < beds2.size(); i++) {
         BlockPos[] a = beds2.get(i);

         for (int j = i + 1; j < beds2.size(); j++) {
            BlockPos[] b = beds2.get(j);
            if (positionsEqual(a, b)) {
               toRemove.add(b);
            }
         }
      }

      beds.removeAll(toRemove);
   }

   // $VF: Irreducible bytecode was duplicated to produce valid code
   static {
      range = new SliderSetting("Range", 64.0, 0.0, 128.0, 1.0);
      opacity = new PercentageSetting("Background-opacity", 40);
      themeDescription = new DescriptionSetting("THEME, THEME_CUSTOM, CUSTOM");
      color = new ModeSetting("Color", "THEME", "THEME_CUSTOM", "CUSTOM");
      customColor = new ColorSetting("Custom-color", "FF0000");
   }
}
