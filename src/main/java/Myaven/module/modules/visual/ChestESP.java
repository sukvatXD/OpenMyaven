package Myaven.module.modules.visual;

import Myaven.module.Category;
import Myaven.module.Module;
import Myaven.module.modules.config.Theme;
import Myaven.setting.Setting;
import Myaven.setting.settings.BooleanSetting;
import Myaven.setting.settings.ColorSetting;
import Myaven.setting.settings.DescriptionSetting;
import Myaven.setting.settings.ModeSetting;
import Myaven.util.RenderUtil;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.BlockPos;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.Action;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.jetbrains.annotations.NotNull;

public class ChestESP extends Module {
   public static DescriptionSetting colorModesDescription;
   public static ModeSetting colorMode;
   public static ColorSetting customColor;
   public static BooleanSetting showFill;
   public static BooleanSetting showOutline;
   public static BooleanSetting ignoreOpened;
   private List<BlockPos> openedChests = new ArrayList<>();

   public ChestESP() {
      super("ChestESP", false, Category.Visual, false, "ESP for chests");
      this.addSettings(colorModesDescription,colorMode,customColor,showFill,showOutline,ignoreOpened);
   }

   @SubscribeEvent
   public void onRender(RenderWorldLastEvent event) {
      String var4 = colorMode.getCurrent();
      int color;
      switch (var4) {
         case "THEME":
            color = Theme.computeThemeColor(0.0);
            break;
         case "THEME_CUSTOM":
            color = Theme.computeCustomThemeColor(0.0);
            break;
         default:
            color = customColor.F();
      }

      for (TileEntity tileEntity : this.mc.theWorld.loadedTileEntityList) {
         if (tileEntity instanceof TileEntityChest) {
            if (!this.openedChests.contains(tileEntity.getPos()) && ((TileEntityChest)tileEntity).numPlayersUsing > 0) {
               this.openedChests.add(tileEntity.getPos());
            }

            if (!this.openedChests.contains(tileEntity.getPos()) && ignoreOpened.getState() || !ignoreOpened.getState()) {
               RenderUtil.drawBlockBox(tileEntity.getPos(), color, showOutline.getState(), showFill.getState());
            }
         }
      }
   }

   @SubscribeEvent
   public void onInteract(PlayerInteractEvent event) {
      if (event.action == Action.RIGHT_CLICK_BLOCK) {
         BlockPos pos = event.pos;
         if (event.world.getBlockState(pos).getBlock() == Blocks.chest) {
            this.openedChests.add(pos);
         }
      }
   }

   @SubscribeEvent
   public void onJoin(@NotNull EntityJoinWorldEvent e) {
      if (e.entity == this.mc.thePlayer) {
         this.openedChests.clear();
      }
   }

   @Override
   public void onDisable() {
      this.openedChests.clear();
   }

   // $VF: Irreducible bytecode was duplicated to produce valid code
   static {
      colorModesDescription = new DescriptionSetting("THEME, THEME_CUSTOM, CUSTOM");
      colorMode = new ModeSetting("Color", "THEME", "THEME_CUSTOM", "CUSTOM");
      customColor = new ColorSetting("Custom-color", "FFFFFF");
      showFill = new BooleanSetting("Show-target-shade", false);
      showOutline = new BooleanSetting("Show-target-outline", true);
      ignoreOpened = new BooleanSetting("Ignore-opened", false);
   }
}
