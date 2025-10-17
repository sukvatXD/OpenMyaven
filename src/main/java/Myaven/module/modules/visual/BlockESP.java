package Myaven.module.modules.visual;

import Myaven.events.PlayerUpdateEvent;
import Myaven.mixins.accessor.AccessorEntityRenderer;
import Myaven.module.Category;
import Myaven.module.Module;
import Myaven.setting.Setting;
import Myaven.setting.settings.BooleanSetting;
import Myaven.setting.settings.SliderSetting;
import Myaven.util.BlockUtil;
import Myaven.util.ClientUtil;
import Myaven.util.RenderUtil;
import Myaven.util.TimerUtil;
import java.awt.Color;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.opengl.GL11;

public class BlockESP extends Module {
   public static SliderSetting range;
   public static BooleanSetting outline;
   public static BooleanSetting shade;
   public static BooleanSetting tracers;
   public static BooleanSetting diamond;
   public static BooleanSetting obsidian;
   public static BooleanSetting spawner;
   public static BooleanSetting gold;
   public static BooleanSetting iron;
   public static BooleanSetting coal;
   public static BooleanSetting lapis;
   public static BooleanSetting emerald;
   public static BooleanSetting redstone;
   private Set<BlockPos> trackedBlockPositions = ConcurrentHashMap.newKeySet();
   private TimerUtil scanTimer = new TimerUtil();
   private boolean scanning = false;

   public BlockESP() {
      super("BlocksESP", false, Category.Visual, true, "Highlight some blocks");
      this.addSettings(new Setting[]{range, outline, shade, tracers, diamond, obsidian, spawner, gold, iron, coal, lapis, emerald, redstone});
   }

   @Override
   public void onDisable() {
      this.trackedBlockPositions.clear();
   }

   @SubscribeEvent
   public void onUpdate(PlayerUpdateEvent event) {
      if (!this.scanning && this.scanTimer.hasTimePassed(500, true)) {
         this.scanning = true;
         new Thread(
               () -> {

                  int i;
                  for (int n = i = (int)range.getRoundedValue(); i >= -n; i--) {
                     for (int j = -n; j <= n; j++) {
                        for (int k = -n; k <= n; k++) {
                           BlockPos blockPos = new BlockPos(
                              this.mc.thePlayer.posX + (double)j, this.mc.thePlayer.posY + (double)i, this.mc.thePlayer.posZ + (double)k
                           );
                           if (!this.trackedBlockPositions.contains(blockPos)) {
                              Block blockState = BlockUtil.getBlock(blockPos);
                              if (blockState != null && this.shouldHighlightBlock(blockState)) {
                                 this.trackedBlockPositions.add(blockPos);
                              }
                           }
                        }
                     }
                  }

                  this.scanning = false;
               }
            )
            .start();
      }
   }

   @SubscribeEvent
   public void onJoin(EntityJoinWorldEvent e) {
      if (e.entity == this.mc.thePlayer) {
         this.trackedBlockPositions.clear();
      }
   }

   // $VF: Could not create synchronized statement, marking monitor enters and exits
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @SubscribeEvent
   public void onRender(RenderWorldLastEvent ev) {
      if (ClientUtil.isWorldLoaded()) {
         Set var3 = this.trackedBlockPositions;
         synchronized (this.trackedBlockPositions){} // $VF: monitorenter 
         if (!this.trackedBlockPositions.isEmpty()) {
            Iterator<BlockPos> iterator = this.trackedBlockPositions.iterator();

            while (iterator.hasNext()) {
               BlockPos blockPos = iterator.next();
               Block block = BlockUtil.getBlock(blockPos);
               if (block != null && this.shouldHighlightBlock(block)) {
                  this.renderBlockEsp(blockPos);
               } else {
                  iterator.remove();
               }
            }
         }

         // $VF: monitorexit
      }
   }

   private void renderBlockEsp(BlockPos p) {
      if (p != null) {
         int[] rgb = this.getBlockColor(BlockUtil.getBlock(p));
         if (rgb[0] + rgb[1] + rgb[2] != 0) {
            RenderUtil.drawBlockBox(p, new Color(rgb[0], rgb[1], rgb[2]).getRGB(), outline.getState(), shade.getState());
            if (tracers.getState()) {
               this.drawTracerToBlock(p, new Color(rgb[0], rgb[1], rgb[2]).getRGB());
            }
         }
      }
   }

   private int[] getBlockColor(Block b) {
      short red = 0;
      int green = 0;
      int blue = 0;
      if (b.equals(Blocks.iron_ore)) {
         red = 255;
         green = 255;
         blue = 255;
      } else if (b.equals(Blocks.gold_ore)) {
         red = 255;
         green = 255;
      } else if (b.equals(Blocks.diamond_ore)) {
         green = 220;
         blue = 255;
      } else if (b.equals(Blocks.emerald_ore)) {
         red = 35;
         green = 255;
      } else if (b.equals(Blocks.lapis_ore)) {
         green = 50;
         blue = 255;
      } else if (b.equals(Blocks.redstone_ore)) {
         red = 255;
      } else if (b.equals(Blocks.mob_spawner)) {
         red = 30;
         blue = 135;
      }

      return new int[]{red, green, blue};
   }

   public boolean shouldHighlightBlock(Block block) {
      return iron.getState() && block.equals(Blocks.iron_ore)
         || gold.getState() && block.equals(Blocks.gold_ore)
         || diamond.getState() && block.equals(Blocks.diamond_ore)
         || emerald.getState() && block.equals(Blocks.emerald_ore)
         || lapis.getState() && block.equals(Blocks.lapis_ore)
         || redstone.getState() && block.equals(Blocks.redstone_ore)
         || coal.getState() && block.equals(Blocks.coal_ore)
         || spawner.getState() && block.equals(Blocks.mob_spawner)
         || obsidian.getState() && block.equals(Blocks.obsidian);
   }

   private void drawTracerToBlock(BlockPos var0, int var1) {
      double renderPosXDelta = (double)var0.getX() - this.mc.getRenderManager().viewerPosX + 0.5;
      double renderPosYDelta = (double)var0.getY() - this.mc.getRenderManager().viewerPosY + 0.5;
      double renderPosZDelta = (double)var0.getZ() - this.mc.getRenderManager().viewerPosZ + 0.5;
      GL11.glPushMatrix();
      GL11.glEnable(3042);
      GL11.glEnable(2848);
      GL11.glDisable(2929);
      GL11.glDisable(3553);
      GL11.glBlendFunc(770, 771);
      GL11.glLineWidth(1.0F);
      float blockPos9 = (float)(Minecraft.getMinecraft().thePlayer.posX - (double)var0.getX());
      float blockPos7 = (float)(Minecraft.getMinecraft().thePlayer.posY - (double)var0.getY());
      float f = (float)(var1 >> 16 & 0xFF) / 255.0F;
      float f2 = (float)(var1 >> 8 & 0xFF) / 255.0F;
      float f3 = (float)(var1 & 0xFF) / 255.0F;
      float f4 = (float)(var1 >> 24 & 0xFF) / 255.0F;
      GL11.glColor4f(f, f2, f3, f4);
      GL11.glLoadIdentity();
      boolean previousState = this.mc.gameSettings.viewBobbing;
      this.mc.gameSettings.viewBobbing = false;
      ((AccessorEntityRenderer)this.mc.entityRenderer).callOrientCamera(ClientUtil.getRenderPartialTicks());
      GL11.glBegin(3);
      GL11.glVertex3d(0.0, (double)Minecraft.getMinecraft().thePlayer.getEyeHeight(), 0.0);
      GL11.glVertex3d(renderPosXDelta, renderPosYDelta, renderPosZDelta);
      GL11.glVertex3d(renderPosXDelta, renderPosYDelta, renderPosZDelta);
      GL11.glEnd();
      this.mc.gameSettings.viewBobbing = previousState;
      GL11.glEnable(3553);
      GL11.glEnable(2929);
      GL11.glDisable(2848);
      GL11.glDisable(3042);
      GL11.glPopMatrix();
   }

   // $VF: Irreducible bytecode was duplicated to produce valid code
   static {
      range = new SliderSetting("Range", 64.0, 0.0, 128.0, 1.0);
      outline = new BooleanSetting("Outline", true);
      shade = new BooleanSetting("Shade", true);
      tracers = new BooleanSetting("Tracers", false);
      diamond = new BooleanSetting("Diamond", true);
      obsidian = new BooleanSetting("Obsidian", true);
      spawner = new BooleanSetting("Spawner", false);
      gold = new BooleanSetting("Gold", false);
      iron = new BooleanSetting("Iron", false);
      coal = new BooleanSetting("Coal", false);
      lapis = new BooleanSetting("Lapis", false);
      emerald = new BooleanSetting("Emerald", false);
      redstone = new BooleanSetting("Redstone", false);
   }

}
