package Myaven.module.modules.visual;

import Myaven.events.Render2DEvent;
import Myaven.management.RotationManager;
import Myaven.module.Category;
import Myaven.module.Module;
import Myaven.setting.Setting;
import Myaven.setting.settings.BooleanSetting;
import Myaven.setting.settings.SliderSetting;
import Myaven.util.ColorUtil;
import Myaven.util.RenderUtil;
import java.awt.Color;
import java.util.Iterator;
import java.util.stream.Collectors;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderPearl;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.entity.projectile.EntityFireball;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.MathHelper;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.opengl.GL11;

public class Indicators extends Module {
   public static SliderSetting scale;
   public SliderSetting offset = new SliderSetting("Offset", 50.0, 0.0, 255.0, 1.0);
   public BooleanSetting directionCheck = new BooleanSetting("Direction-check", true);
   public BooleanSetting showFireballs = new BooleanSetting("Fireballs", true);
   public BooleanSetting showPearls = new BooleanSetting("Pearls", true);
   public BooleanSetting showArrows = new BooleanSetting("Arrows", true);

   public Indicators() {
      super("Indicators", false, Category.Visual, false, "Show projectiles that is going to hit you on screen");
      this.addSettings(new Setting[]{scale, this.offset, this.directionCheck, this.showFireballs, this.showPearls, this.showArrows});
   }

   private boolean shouldDisplayProjectile(Entity entity) {
      double d = (entity.posX - entity.lastTickPosX) * (this.mc.thePlayer.posX - entity.posX)
         + (entity.posY - entity.lastTickPosY)
            * (this.mc.thePlayer.posY + (double)this.mc.thePlayer.getEyeHeight() - entity.posY - (double)entity.height / 2.0)
         + (entity.posZ - entity.lastTickPosZ) * (this.mc.thePlayer.posZ - entity.posZ);
      if (d == 0.0) {
         return false;
      } else if (d < 0.0 && this.directionCheck.getState()) {
         return false;
      } else if (this.showFireballs.getState() && entity instanceof EntityFireball) {
         return true;
      } else if (this.showPearls.getState() && entity instanceof EntityEnderPearl) {
         return true;
      } else {
         return !this.showArrows.getState() ? false : entity instanceof EntityArrow;
      }
   }

   private Item getProjectileItem(Entity entity) {
      if (entity instanceof EntityFireball) {
         return Items.fire_charge;
      } else if (entity instanceof EntityEnderPearl) {
         return Items.ender_pearl;
      } else {
         return entity instanceof EntityArrow ? Items.arrow : new Item();
      }
   }

   private Color getProjectileColor(Entity entity) {
      if (entity instanceof EntityFireball) {
         return new Color(12676363);
      } else if (entity instanceof EntityEnderPearl) {
         return new Color(2458740);
      } else {
         return entity instanceof EntityArrow ? new Color(9868950) : new Color(-1);
      }
   }

   @SubscribeEvent
   public void onRender(Render2DEvent render2DEvent) {
      Iterator var3 = this.mc.theWorld.loadedEntityList.stream().filter(this::shouldDisplayProjectile).collect(Collectors.toList()).iterator();

      while (var3.hasNext()) {
         Entity entity = (Entity)var3.next();
         float offset2 = (float)(10.0 + this.offset.getRoundedValue());
         float yawBetween = computeYawBetweenPoints(
            lerp(this.mc.thePlayer.posX, this.mc.thePlayer.prevPosX, (double)render2DEvent.getPartialTicks()),
            lerp(this.mc.thePlayer.posZ, this.mc.thePlayer.prevPosZ, (double)render2DEvent.getPartialTicks()),
            lerp(entity.posX, entity.prevPosX, (double)render2DEvent.getPartialTicks()),
            lerp(entity.posZ, entity.prevPosZ, (double)render2DEvent.getPartialTicks())
         );
         if (this.mc.gameSettings.thirdPersonView == 2) {
            yawBetween += 180.0F;
         }

         float x = (float)Math.sin(Math.toRadians((double)yawBetween));
         float z = (float)Math.cos(Math.toRadians((double)yawBetween)) * -1.0F;
         GlStateManager.pushMatrix();
         GlStateManager.disableDepth();
         GlStateManager.scale(scale.getRoundedValue(), scale.getRoundedValue(), 0.0);
         GlStateManager.translate(
            (double)((float)new ScaledResolution(this.mc).getScaledWidth() / 2.0F) / scale.getRoundedValue(),
            (double)((float)new ScaledResolution(this.mc).getScaledHeight() / 2.0F) / scale.getRoundedValue(),
            0.0
         );
         GlStateManager.pushMatrix();
         GlStateManager.translate((offset2 + 0.0F) * x - 8.0F, (offset2 + 0.0F) * z - 8.0F, -300.0F);
         this.mc.getRenderItem().renderItemAndEffectIntoGUI(new ItemStack(this.getProjectileItem(entity)), 0, 0);
         GlStateManager.popMatrix();
         String string = String.format("%dm", (int)this.mc.thePlayer.getDistanceToEntity(entity));
         GlStateManager.pushMatrix();
         GlStateManager.translate(
            (offset2 + 0.0F) * x - (float)this.mc.fontRendererObj.getStringWidth(string) / 2.0F + 1.0F, (offset2 + 0.0F) * z + 1.0F, -100.0F
         );
         this.mc
            .fontRendererObj
            .drawStringWithShadow(string, 0.0F, 0.0F, ColorUtil.getColorByFormatting(EnumChatFormatting.GRAY).getRGB() & 16777215 | -1090519040);
         GlStateManager.popMatrix();
         GlStateManager.pushMatrix();
         GlStateManager.translate((offset2 + 15.0F) * x + 1.0F, (offset2 + 15.0F) * z + 1.0F, -100.0F);
         RenderUtil.setup2DRenderState();
         drawDirectionIndicator(0.0F, 0.0F, (float)(Math.atan2((double)z, (double)x) + Math.PI), 7.5F, 1.5F, this.getProjectileColor(entity).getRGB());
         RenderUtil.restore3DRenderState();
         GlStateManager.popMatrix();
         GlStateManager.enableDepth();
         GlStateManager.popMatrix();
      }
   }

   public static float computeYawBetweenPoints(double x1, double z1, double x2, double z2) {
      return MathHelper.wrapAngleTo180_float((float)(Math.atan2(z2 - z1, x2 - x1) * 180.0 / Math.PI) - 90.0F - RotationManager.getCurrentYaw());
   }

   public static double lerp(double current, double previous, double t) {
      return previous + (current - previous) * t;
   }

   public static void drawDirectionIndicator(float centerX, float centerY, float angle, float length, float lineWidth, int color) {
      float f6 = angle + (float)Math.toRadians(45.0);
      float f7 = angle - (float)Math.toRadians(45.0);
      RenderUtil.setGLColor(color);
      GL11.glLineWidth(lineWidth);
      GL11.glEnable(2848);
      GL11.glHint(3154, 4354);
      GL11.glBegin(1);
      GL11.glVertex2f(centerX, centerY);
      GL11.glVertex2f(centerX + length * (float)Math.cos((double)f6), centerY + length * (float)Math.sin((double)f6));
      GL11.glVertex2f(centerX, centerY);
      GL11.glVertex2f(centerX + length * (float)Math.cos((double)f7), centerY + length * (float)Math.sin((double)f7));
      GL11.glEnd();
      GL11.glDisable(2848);
      GL11.glLineWidth(2.0F);
      GlStateManager.resetColor();
   }

   // $VF: Irreducible bytecode was duplicated to produce valid code
   static {
      scale = new SliderSetting("Scale", 1.0, 0.0, 5.0, 0.1);
   }
}
