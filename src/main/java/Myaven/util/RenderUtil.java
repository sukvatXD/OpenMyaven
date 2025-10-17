package Myaven.util;

import Myaven.Myaven;
import Myaven.mixins.accessor.AccessorTimer;
import Myaven.setting.Setting;
import Myaven.ui.ClickGUI;
import Myaven.ui.font.IFontRenderer;
import java.awt.Color;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.model.IBakedModel;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Vec3;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.GLU;

public class RenderUtil {
   private static int defaultBackgroundColor;
   private static int defaultTextColor;
   protected static float renderDepthOffset;
   private static FloatBuffer projectedBuffer;
   private static IntBuffer viewportBuffer;
   private static FloatBuffer modelViewBuffer;
   private static FloatBuffer projectionBuffer;
   private static float[] projectedCoords;
   private static Frustum cameraFrustum;
   private static FloatBuffer modelMatrixBuffer;
   private static FloatBuffer projectionMatrixBuffer;
   private static IntBuffer viewportIntBuffer;
   private static FloatBuffer projectedVecBuffer;

   public static void setup2DRenderState() {
      GlStateManager.enableBlend();
      GlStateManager.blendFunc(770, 771);
      GlStateManager.disableTexture2D();
      GlStateManager.disableCull();
      GlStateManager.disableAlpha();
      GlStateManager.disableDepth();
   }

   public static void restore3DRenderState() {
      GlStateManager.enableDepth();
      GlStateManager.enableAlpha();
      GlStateManager.enableCull();
      GlStateManager.enableTexture2D();
      GlStateManager.disableBlend();
   }

   public static void drawRect(double left, double top, double right, double bottom, int color) {
      float f3 = (float)(color >> 24 & 0xFF) / 255.0F;
      float f = (float)(color >> 16 & 0xFF) / 255.0F;
      float f1 = (float)(color >> 8 & 0xFF) / 255.0F;
      float f2 = (float)(color & 0xFF) / 255.0F;
      GlStateManager.pushMatrix();
      Tessellator tessellator = Tessellator.getInstance();
      WorldRenderer worldrenderer = tessellator.getWorldRenderer();
      GlStateManager.enableBlend();
      GlStateManager.disableTexture2D();
      GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
      GlStateManager.color(f, f1, f2, f3);
      worldrenderer.begin(7, DefaultVertexFormats.POSITION);
      worldrenderer.pos(left, bottom, 0.0).endVertex();
      worldrenderer.pos(right, bottom, 0.0).endVertex();
      worldrenderer.pos(right, top, 0.0).endVertex();
      worldrenderer.pos(left, top, 0.0).endVertex();
      tessellator.draw();
      GlStateManager.enableTexture2D();
      GlStateManager.disableBlend();
      GlStateManager.popMatrix();
   }

   public static void drawEntityBox(Vec3 pos, int color) {
      GlStateManager.pushMatrix();
      double x = pos.xCoord - Myaven.mc.getRenderManager().viewerPosX;
      double y = pos.yCoord - Myaven.mc.getRenderManager().viewerPosY;
      double z = pos.zCoord - Myaven.mc.getRenderManager().viewerPosZ;
      AxisAlignedBB bbox = Myaven.mc.thePlayer.getEntityBoundingBox().expand(0.1, 0.1, 0.1);
      AxisAlignedBB axis = new AxisAlignedBB(
         bbox.minX - Myaven.mc.thePlayer.posX + x,
         bbox.minY - Myaven.mc.thePlayer.posY + y,
         bbox.minZ - Myaven.mc.thePlayer.posZ + z,
         bbox.maxX - Myaven.mc.thePlayer.posX + x,
         bbox.maxY - Myaven.mc.thePlayer.posY + y,
         bbox.maxZ - Myaven.mc.thePlayer.posZ + z
      );
      float a = (float)(color >> 24 & 0xFF) / 255.0F;
      float r = (float)(color >> 16 & 0xFF) / 255.0F;
      float g = (float)(color >> 8 & 0xFF) / 255.0F;
      float b = (float)(color & 0xFF) / 255.0F;
      GL11.glBlendFunc(770, 771);
      GL11.glEnable(3042);
      GL11.glDisable(3553);
      GL11.glDisable(2929);
      GL11.glDepthMask(false);
      GL11.glLineWidth(2.0F);
      GL11.glColor4f(r, g, b, a);
      renderFilledBox(axis, r, g, b, a);
      GL11.glEnable(3553);
      GL11.glEnable(2929);
      GL11.glDepthMask(true);
      GL11.glDisable(3042);
      GlStateManager.popMatrix();
   }

   public static void drawOutlinedRect(float x, float y, float x2, float y2, float lineWidth, int color) {
      float f5 = (float)(color >> 24 & 0xFF) / 255.0F;
      float f6 = (float)(color >> 16 & 0xFF) / 255.0F;
      float f7 = (float)(color >> 8 & 0xFF) / 255.0F;
      float f8 = (float)(color & 0xFF) / 255.0F;
      GL11.glEnable(3042);
      GL11.glDisable(3553);
      GL11.glBlendFunc(770, 771);
      GL11.glEnable(2848);
      GL11.glPushMatrix();
      GL11.glColor4f(f6, f7, f8, f5);
      GL11.glLineWidth(lineWidth);
      GL11.glBegin(1);
      GL11.glVertex2d((double)x, (double)y);
      GL11.glVertex2d((double)x, (double)y2);
      GL11.glVertex2d((double)x2, (double)y2);
      GL11.glVertex2d((double)x2, (double)y);
      GL11.glVertex2d((double)x, (double)y);
      GL11.glVertex2d((double)x2, (double)y);
      GL11.glVertex2d((double)x, (double)y2);
      GL11.glVertex2d((double)x2, (double)y2);
      GL11.glEnd();
      GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
      GL11.glPopMatrix();
      GL11.glEnable(3553);
      GL11.glDisable(3042);
      GL11.glDisable(2848);
   }

   public static void drawBoundingBox(double x, double y, double z, double x2, double y2, double z2, int color, boolean outline, boolean shade) {
      
      double xPos = x - Myaven.mc.getRenderManager().viewerPosX;
      double yPos = y - Myaven.mc.getRenderManager().viewerPosY;
      double zPos = z - Myaven.mc.getRenderManager().viewerPosZ;
      GL11.glPushMatrix();
      GL11.glBlendFunc(770, 771);
      GL11.glEnable(3042);
      GL11.glLineWidth(2.0F);
      GL11.glDisable(3553);
      GL11.glDisable(2929);
      GL11.glDepthMask(false);
      float n8 = (float)(color >> 24 & 0xFF) / 255.0F;
      float n9 = (float)(color >> 16 & 0xFF) / 255.0F;
      float n10 = (float)(color >> 8 & 0xFF) / 255.0F;
      float n11 = (float)(color & 0xFF) / 255.0F;
      GL11.glColor4f(n9, n10, n11, n8);
      AxisAlignedBB axisAlignedBB = new AxisAlignedBB(xPos, yPos, zPos, xPos + x2, yPos + y2, zPos + z2);
      if (outline) {
         RenderGlobal.drawSelectionBoundingBox(axisAlignedBB);
      }

      if (shade) {
         fillBoundingBox(axisAlignedBB, n9, n10, n11);
      }

      GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
      GL11.glEnable(3553);
      GL11.glEnable(2929);
      GL11.glDepthMask(true);
      GL11.glDisable(3042);
      GL11.glPopMatrix();
   }

   public static void drawEntityESP(Entity e, int type, double expand, double shift, int color, boolean damage) {
      if (e instanceof EntityLivingBase) {
         float partialTicks = ((AccessorTimer)Myaven.mc).getTimer().renderPartialTicks;
         double x = e.lastTickPosX + (e.posX - e.lastTickPosX) * (double)partialTicks - Myaven.mc.getRenderManager().viewerPosX;
         double y = e.lastTickPosY + (e.posY - e.lastTickPosY) * (double)partialTicks - Myaven.mc.getRenderManager().viewerPosY;
         double z = e.lastTickPosZ + (e.posZ - e.lastTickPosZ) * (double)partialTicks - Myaven.mc.getRenderManager().viewerPosZ;
         float d = (float)expand / 40.0F;
         if (e instanceof EntityPlayer && damage && ((EntityPlayer)e).hurtTime != 0) {
            color = Color.RED.getRGB();
         }

         GlStateManager.pushMatrix();
         if (type == 3) {
            GL11.glTranslated(x, y - 0.2, z);
            GL11.glRotated((double)(-Myaven.mc.getRenderManager().playerViewY), 0.0, 1.0, 0.0);
            GlStateManager.disableDepth();
            GL11.glScalef(0.03F + d, 0.03F + d, 0.03F + d);
            int outline = Color.black.getRGB();
            Gui.drawRect(-18, -1, -21, 74, outline);
            Gui.drawRect(18, -1, 21, 74, outline);
            Gui.drawRect(-18, -1, 21, 2, outline);
            Gui.drawRect(-18, 71, 21, 74, outline);
            Gui.drawRect(-19, 0, -20, 73, color);
            Gui.drawRect(19, 0, 20, 73, color);
            Gui.drawRect(-19, 0, 20, 1, color);
            Gui.drawRect(-19, 72, 20, 73, color);
            GlStateManager.enableDepth();
         } else if (type == 4) {
            EntityLivingBase en = (EntityLivingBase)e;
            double r = (double)(en.getHealth() / en.getMaxHealth());
            int b = (int)(73.0 * r);
            int hc = r < 0.3 ? Color.red.getRGB() : (r < 0.5 ? Color.orange.getRGB() : (r < 0.7 ? Color.yellow.getRGB() : Color.green.getRGB()));
            GL11.glTranslated(x, y - 0.2, z);
            GL11.glRotated((double)(-Myaven.mc.getRenderManager().playerViewY), 0.0, 1.0, 0.0);
            GlStateManager.disableDepth();
            GL11.glScalef(0.03F + d, 0.03F + d, 0.03F + d);
            int i = (int)(21.0 + shift * 2.0);
            Gui.drawRect(i, -1, i + 4, 74, Color.black.getRGB());
            Gui.drawRect(i + 1, b, i + 3, 73, Color.darkGray.getRGB());
            Gui.drawRect(i + 1, 0, i + 3, b, hc);
            GlStateManager.enableDepth();
         } else if (type == 7) {
            EntityLivingBase en = (EntityLivingBase)e;
            double r = (double)(en.getHealth() / en.getMaxHealth());
            int b = (int)(73.0 * r);
            int hc = r < 0.3 ? Color.red.getRGB() : (r < 0.5 ? Color.orange.getRGB() : (r < 0.7 ? Color.yellow.getRGB() : Color.green.getRGB()));
            GL11.glTranslated(x, y - 0.2, z);
            GL11.glRotated((double)(-Myaven.mc.getRenderManager().playerViewY), 0.0, 1.0, 0.0);
            GlStateManager.disableDepth();
            GL11.glScalef(0.03F + d, 0.03F + d, 0.03F + d);
            int i = (int)(23.0 + shift * 2.0);
            Gui.drawRect(i + 1, -1, i + 8, 74, Color.black.getRGB());
            Gui.drawRect(i + 2, b, i + 7, 73, Color.darkGray.getRGB());
            Gui.drawRect(i + 2, 0, i + 7, b, hc);
            GlStateManager.enableDepth();
         } else if (type == 6) {
            drawCircle3D(x, y, z, 0.7F, 45, 1.5F, color, color == 0);
         } else {
            if (color == 0) {
               color = getChromaColor(2L, 0L);
            }

            float ax = (float)(color >> 24 & 0xFF) / 255.0F;
            float r = (float)(color >> 16 & 0xFF) / 255.0F;
            float g = (float)(color >> 8 & 0xFF) / 255.0F;
            float b = (float)(color & 0xFF) / 255.0F;
            AxisAlignedBB bbox = e.getEntityBoundingBox().expand(0.1 + expand, 0.1 + expand, 0.1 + expand);
            AxisAlignedBB axis = new AxisAlignedBB(
               bbox.minX - e.posX + x, bbox.minY - e.posY + y, bbox.minZ - e.posZ + z, bbox.maxX - e.posX + x, bbox.maxY - e.posY + y, bbox.maxZ - e.posZ + z
            );
            GL11.glBlendFunc(770, 771);
            GL11.glEnable(3042);
            GL11.glDisable(3553);
            GL11.glDisable(2929);
            GL11.glDepthMask(false);
            GL11.glLineWidth(2.0F);
            GL11.glColor4f(r, g, b, ax);
            if (type == 1) {
               RenderGlobal.drawSelectionBoundingBox(axis);
            } else if (type == 2) {
               fillBoundingBox(axis, r, g, b);
            } else if (type == 8) {
               AxisAlignedBB box = e.getEntityBoundingBox();
               AxisAlignedBB axis2 = new AxisAlignedBB(
                  box.minX - e.posX + x, box.minY - e.posY + y, box.minZ - e.posZ + z, box.maxX - e.posX + x, box.maxY - e.posY + y, box.maxZ - e.posZ + z
               );
               RenderGlobal.drawSelectionBoundingBox(axis2);
            }

            GL11.glEnable(3553);
            GL11.glEnable(2929);
            GL11.glDepthMask(true);
            GL11.glDisable(3042);
         }

         GlStateManager.popMatrix();
      }
   }

   public static void drawLine2D(float x1, float y1, float x2, float y2, float width, float r, float g, float b, float a) {
      GlStateManager.pushMatrix();
      GlStateManager.disableTexture2D();
      GlStateManager.enableBlend();
      GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
      GL11.glLineWidth(width);
      GL11.glColor4f(r, g, b, a);
      GL11.glBegin(1);
      GL11.glVertex2f(x1, y1);
      GL11.glVertex2f(x2, y2);
      GL11.glEnd();
      GlStateManager.disableBlend();
      GlStateManager.enableTexture2D();
      GlStateManager.popMatrix();
   }

   public static void drawCircle3D(double x, double y, double z, double radius, int sides, float lineWidth, int color, boolean chroma) {
      float a = (float)(color >> 24 & 0xFF) / 255.0F;
      float r = (float)(color >> 16 & 0xFF) / 255.0F;
      float g = (float)(color >> 8 & 0xFF) / 255.0F;
      float b = (float)(color & 0xFF) / 255.0F;
      Myaven.mc.entityRenderer.disableLightmap();
      
      GL11.glDisable(3553);
      GL11.glEnable(3042);
      GL11.glBlendFunc(770, 771);
      GL11.glDisable(2929);
      GL11.glEnable(2848);
      GL11.glDepthMask(false);
      GL11.glLineWidth(lineWidth);
      if (!chroma) {
         GL11.glColor4f(r, g, b, a);
      }

      GL11.glBegin(1);
      long d = 0L;
      long ed = 15000L / (long)sides;
      long hed = ed / 2L;

      for (int i = 0; i < sides * 2; i++) {
         if (chroma) {
            if (i % 2 != 0) {
               if (i == 47) {
                  d = hed;
               }

               d += ed;
            }

            int c = getChromaColor(2L, d);
            float r2 = (float)(c >> 16 & 0xFF) / 255.0F;
            float g2 = (float)(c >> 8 & 0xFF) / 255.0F;
            float b2 = (float)(c & 0xFF) / 255.0F;
            GL11.glColor3f(r2, g2, b2);
         }

         double angle = (Math.PI * 2) * (double)i / (double)sides + Math.toRadians(180.0);
         GL11.glVertex3d(x + Math.cos(angle) * radius, y, z + Math.sin(angle) * radius);
      }

      GL11.glEnd();
      GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
      GL11.glDepthMask(true);
      GL11.glDisable(2848);
      GL11.glEnable(2929);
      GL11.glDisable(3042);
      GL11.glEnable(3553);
      Myaven.mc.entityRenderer.enableLightmap();
   }

   public static void drawFilledCircle(double n, double n2, double n3, int n4, int n5) {
      if (n4 >= 3) {
         float n6 = (float)(n5 >> 24 & 0xFF) / 255.0F;
         float n7 = (float)(n5 >> 16 & 0xFF) / 255.0F;
         float n8 = (float)(n5 >> 8 & 0xFF) / 255.0F;
         float n9 = (float)(n5 & 0xFF) / 255.0F;
         Tessellator getInstance = Tessellator.getInstance();
         WorldRenderer getWorldRenderer = getInstance.getWorldRenderer();
         GlStateManager.enableBlend();
         GlStateManager.disableTexture2D();
         GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
         GL11.glColor4f(n7, n8, n9, n6);
         getWorldRenderer.begin(6, DefaultVertexFormats.POSITION);

         for (int i = 0; i < n4; i++) {
            double n10 = (Math.PI * 2) * (double)i / (double)n4 + Math.toRadians(180.0);
            getWorldRenderer.pos(n + Math.sin(n10) * n3, n2 + Math.cos(n10) * n3, 0.0).endVertex();
         }

         getInstance.draw();
         GlStateManager.enableTexture2D();
         GlStateManager.disableBlend();
      }
   }

   public static void fillBoundingBox(AxisAlignedBB abb, float r, float g, float b) {
      renderFilledBox(abb, r, g, b, 0.25F);
   }

   public static void renderFilledBox(AxisAlignedBB abb, float r, float g, float b, float a) {
      Tessellator ts = Tessellator.getInstance();
      WorldRenderer vb = ts.getWorldRenderer();
      vb.begin(7, DefaultVertexFormats.POSITION_COLOR);
      vb.pos(abb.minX, abb.minY, abb.minZ).color(r, g, b, a).endVertex();
      vb.pos(abb.minX, abb.maxY, abb.minZ).color(r, g, b, a).endVertex();
      vb.pos(abb.maxX, abb.minY, abb.minZ).color(r, g, b, a).endVertex();
      vb.pos(abb.maxX, abb.maxY, abb.minZ).color(r, g, b, a).endVertex();
      vb.pos(abb.maxX, abb.minY, abb.maxZ).color(r, g, b, a).endVertex();
      vb.pos(abb.maxX, abb.maxY, abb.maxZ).color(r, g, b, a).endVertex();
      vb.pos(abb.minX, abb.minY, abb.maxZ).color(r, g, b, a).endVertex();
      vb.pos(abb.minX, abb.maxY, abb.maxZ).color(r, g, b, a).endVertex();
      ts.draw();
      vb.begin(7, DefaultVertexFormats.POSITION_COLOR);
      vb.pos(abb.maxX, abb.maxY, abb.minZ).color(r, g, b, a).endVertex();
      vb.pos(abb.maxX, abb.minY, abb.minZ).color(r, g, b, a).endVertex();
      vb.pos(abb.minX, abb.maxY, abb.minZ).color(r, g, b, a).endVertex();
      vb.pos(abb.minX, abb.minY, abb.minZ).color(r, g, b, a).endVertex();
      vb.pos(abb.minX, abb.maxY, abb.maxZ).color(r, g, b, a).endVertex();
      vb.pos(abb.minX, abb.minY, abb.maxZ).color(r, g, b, a).endVertex();
      vb.pos(abb.maxX, abb.maxY, abb.maxZ).color(r, g, b, a).endVertex();
      vb.pos(abb.maxX, abb.minY, abb.maxZ).color(r, g, b, a).endVertex();
      ts.draw();
      vb.begin(7, DefaultVertexFormats.POSITION_COLOR);
      vb.pos(abb.minX, abb.maxY, abb.minZ).color(r, g, b, a).endVertex();
      vb.pos(abb.maxX, abb.maxY, abb.minZ).color(r, g, b, a).endVertex();
      vb.pos(abb.maxX, abb.maxY, abb.maxZ).color(r, g, b, a).endVertex();
      vb.pos(abb.minX, abb.maxY, abb.maxZ).color(r, g, b, a).endVertex();
      vb.pos(abb.minX, abb.maxY, abb.minZ).color(r, g, b, a).endVertex();
      vb.pos(abb.minX, abb.maxY, abb.maxZ).color(r, g, b, a).endVertex();
      vb.pos(abb.maxX, abb.maxY, abb.maxZ).color(r, g, b, a).endVertex();
      vb.pos(abb.maxX, abb.maxY, abb.minZ).color(r, g, b, a).endVertex();
      ts.draw();
      vb.begin(7, DefaultVertexFormats.POSITION_COLOR);
      vb.pos(abb.minX, abb.minY, abb.minZ).color(r, g, b, a).endVertex();
      vb.pos(abb.maxX, abb.minY, abb.minZ).color(r, g, b, a).endVertex();
      vb.pos(abb.maxX, abb.minY, abb.maxZ).color(r, g, b, a).endVertex();
      vb.pos(abb.minX, abb.minY, abb.maxZ).color(r, g, b, a).endVertex();
      vb.pos(abb.minX, abb.minY, abb.minZ).color(r, g, b, a).endVertex();
      vb.pos(abb.minX, abb.minY, abb.maxZ).color(r, g, b, a).endVertex();
      vb.pos(abb.maxX, abb.minY, abb.maxZ).color(r, g, b, a).endVertex();
      vb.pos(abb.maxX, abb.minY, abb.minZ).color(r, g, b, a).endVertex();
      ts.draw();
      vb.begin(7, DefaultVertexFormats.POSITION_COLOR);
      vb.pos(abb.minX, abb.minY, abb.minZ).color(r, g, b, a).endVertex();
      vb.pos(abb.minX, abb.maxY, abb.minZ).color(r, g, b, a).endVertex();
      vb.pos(abb.minX, abb.minY, abb.maxZ).color(r, g, b, a).endVertex();
      vb.pos(abb.minX, abb.maxY, abb.maxZ).color(r, g, b, a).endVertex();
      vb.pos(abb.maxX, abb.minY, abb.maxZ).color(r, g, b, a).endVertex();
      vb.pos(abb.maxX, abb.maxY, abb.maxZ).color(r, g, b, a).endVertex();
      vb.pos(abb.maxX, abb.minY, abb.minZ).color(r, g, b, a).endVertex();
      vb.pos(abb.maxX, abb.maxY, abb.minZ).color(r, g, b, a).endVertex();
      ts.draw();
      vb.begin(7, DefaultVertexFormats.POSITION_COLOR);
      vb.pos(abb.minX, abb.maxY, abb.maxZ).color(r, g, b, a).endVertex();
      vb.pos(abb.minX, abb.minY, abb.maxZ).color(r, g, b, a).endVertex();
      vb.pos(abb.minX, abb.maxY, abb.minZ).color(r, g, b, a).endVertex();
      vb.pos(abb.minX, abb.minY, abb.minZ).color(r, g, b, a).endVertex();
      vb.pos(abb.maxX, abb.maxY, abb.minZ).color(r, g, b, a).endVertex();
      vb.pos(abb.maxX, abb.minY, abb.minZ).color(r, g, b, a).endVertex();
      vb.pos(abb.maxX, abb.maxY, abb.maxZ).color(r, g, b, a).endVertex();
      vb.pos(abb.maxX, abb.minY, abb.maxZ).color(r, g, b, a).endVertex();
      ts.draw();
   }

   public static void renderBlockModel(IBlockState blockState, double x, double y, double z, int color) {
      Minecraft mc = Minecraft.getMinecraft();
      BlockRendererDispatcher dispatcher = mc.getBlockRendererDispatcher();
      IBakedModel model = dispatcher.getModelFromBlockState(blockState, mc.theWorld, new BlockPos(x, y, z));
      double xPos = x - mc.getRenderManager().viewerPosX;
      double yPos = y - mc.getRenderManager().viewerPosY;
      double zPos = z - mc.getRenderManager().viewerPosZ;
      float a = (float)(color >> 24 & 0xFF) / 255.0F;
      float r = (float)(color >> 16 & 0xFF) / 255.0F;
      float g = (float)(color >> 8 & 0xFF) / 255.0F;
      float b = (float)(color & 0xFF) / 255.0F;
      GlStateManager.pushMatrix();
      GlStateManager.translate(xPos, yPos, zPos);
      GlStateManager.enableBlend();
      GlStateManager.blendFunc(770, 771);
      GlStateManager.disableTexture2D();
      GlStateManager.disableCull();
      GlStateManager.disableDepth();
      GlStateManager.depthMask(false);
      GlStateManager.color(r, g, b, a);
      renderModelQuads(model, r, g, b, a);
      GlStateManager.depthMask(true);
      GlStateManager.enableDepth();
      GlStateManager.enableTexture2D();
      GlStateManager.enableCull();
      GlStateManager.disableBlend();
      GlStateManager.popMatrix();
   }

   private static void renderModelQuads(IBakedModel model, float r, float g, float b, float a) {
      Tessellator tessellator = Tessellator.getInstance();
      WorldRenderer wr = tessellator.getWorldRenderer();
      EnumFacing[] var8 = EnumFacing.values();
      

      for (EnumFacing face : var8) {
         for (BakedQuad quad : model.getFaceQuads(face)) {
            drawBakedQuad(wr, quad, r, g, b, a, tessellator);
         }
      }

      for (BakedQuad quad : model.getGeneralQuads()) {
         drawBakedQuad(wr, quad, r, g, b, a, tessellator);
      }
   }

   private static void drawBakedQuad(WorldRenderer wr, BakedQuad quad, float r, float g, float b, float a, Tessellator tessellator) {
      int[] vertexData = quad.getVertexData();
      byte vertexCount = 4;
      int intsPerVertex = vertexData.length / 4;
      wr.begin(7, DefaultVertexFormats.POSITION_COLOR);
      int i = 0;

       int baseIndex = i * intsPerVertex;
       float vx = Float.intBitsToFloat(vertexData[baseIndex]);
       float vy = Float.intBitsToFloat(vertexData[baseIndex + 1]);
       float vz = Float.intBitsToFloat(vertexData[baseIndex + 2]);
       wr.pos((double)vx, (double)vy, (double)vz).color(r, g, b, a).endVertex();
       i++;

       tessellator.draw();
   }

   public static void drawTracerLine(Entity e, int color, float lineWidth, float partialTicks) {
      if (e != null) {
         double x = e.lastTickPosX + (e.posX - e.lastTickPosX) * (double)partialTicks - Myaven.mc.getRenderManager().viewerPosX;
         double y = (double)e.getEyeHeight() + e.lastTickPosY + (e.posY - e.lastTickPosY) * (double)partialTicks - Myaven.mc.getRenderManager().viewerPosY;
         double z = e.lastTickPosZ + (e.posZ - e.lastTickPosZ) * (double)partialTicks - Myaven.mc.getRenderManager().viewerPosZ;
         float ax = (float)(color >> 24 & 0xFF) / 255.0F;
         float r = (float)(color >> 16 & 0xFF) / 255.0F;
         float g = (float)(color >> 8 & 0xFF) / 255.0F;
         float b = (float)(color & 0xFF) / 255.0F;
         GL11.glPushMatrix();
         GL11.glEnable(3042);
         GL11.glEnable(2848);
         GL11.glDisable(2929);
         GL11.glDisable(3553);
         GL11.glBlendFunc(770, 771);
         GL11.glEnable(3042);
         GL11.glLineWidth(lineWidth);
         GL11.glColor4f(r, g, b, ax);
         GL11.glBegin(2);
         GL11.glVertex3d(0.0, (double)Myaven.mc.thePlayer.getEyeHeight(), 0.0);
         GL11.glVertex3d(x, y, z);
         GL11.glEnd();
         GL11.glDisable(3042);
         GL11.glEnable(3553);
         GL11.glEnable(2929);
         GL11.glDisable(2848);
         GL11.glDisable(3042);
         GL11.glPopMatrix();
      }
   }

   public static void drawGradientRect(int left, int top, int right, int bottom, int startColor, int endColor) {
      if (left < right) {
         int j = left;
         left = right;
         right = j;
      }

      if (top < bottom) {
         int j = top;
         top = bottom;
         bottom = j;
      }

      float f = (float)(startColor >> 24 & 0xFF) / 255.0F;
      float f1 = (float)(startColor >> 16 & 0xFF) / 255.0F;
      float f2 = (float)(startColor >> 8 & 0xFF) / 255.0F;
      float f3 = (float)(startColor & 0xFF) / 255.0F;
      float f4 = (float)(endColor >> 24 & 0xFF) / 255.0F;
      float f5 = (float)(endColor >> 16 & 0xFF) / 255.0F;
      float f6 = (float)(endColor >> 8 & 0xFF) / 255.0F;
      float f7 = (float)(endColor & 0xFF) / 255.0F;
      GlStateManager.disableTexture2D();
      GlStateManager.enableBlend();
      GlStateManager.disableAlpha();
      GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
      GlStateManager.shadeModel(7425);
      Tessellator tessellator = Tessellator.getInstance();
      WorldRenderer worldrenderer = tessellator.getWorldRenderer();
      worldrenderer.begin(7, DefaultVertexFormats.POSITION_COLOR);
      worldrenderer.pos((double)right, (double)top, 0.0).color(f1, f2, f3, f).endVertex();
      worldrenderer.pos((double)left, (double)top, 0.0).color(f1, f2, f3, f).endVertex();
      worldrenderer.pos((double)left, (double)bottom, 0.0).color(f5, f6, f7, f4).endVertex();
      worldrenderer.pos((double)right, (double)bottom, 0.0).color(f5, f6, f7, f4).endVertex();
      tessellator.draw();
      GlStateManager.shadeModel(7424);
      GlStateManager.disableBlend();
      GlStateManager.enableAlpha();
      GlStateManager.enableTexture2D();
   }

   public static void drawScreenOverlay(int w, int h, int r) {
      int c = r == -1 ? -1089466352 : r;
      Gui.drawRect(0, 0, w, h, c);
   }

   public static void drawRoundedRect(float x, float y, float x2, float y2, float radius, int color) {
      if (!(x2 <= x)) {
         float width = x2 - x;
         if (width < 3.0F) {
            radius = Math.min(radius, width / 2.0F);
         }

         radius = Math.min(radius, 4.0F);
         x = (float)((double)x * 2.0);
         y = (float)((double)y * 2.0);
         x2 = (float)((double)x2 * 2.0);
         y2 = (float)((double)y2 * 2.0);
         GL11.glPushAttrib(0);
         GL11.glScaled(0.5, 0.5, 0.5);
         GL11.glEnable(3042);
         GL11.glDisable(3553);
         GL11.glEnable(2848);
         GL11.glBegin(9);
         setColorGL(color);

         for (int i = 0; i <= 90; i += 3) {
            double n7 = (double)((float)i * (float) (Math.PI / 180.0));
            GL11.glVertex2d((double)(x + radius) + Math.sin(n7) * (double)radius * -1.0, (double)(y + radius) + Math.cos(n7) * (double)radius * -1.0);
         }

         for (int j = 90; j <= 180; j += 3) {
            double n8 = (double)((float)j * (float) (Math.PI / 180.0));
            GL11.glVertex2d((double)(x + radius) + Math.sin(n8) * (double)radius * -1.0, (double)(y2 - radius) + Math.cos(n8) * (double)radius * -1.0);
         }

         if ((double)(x2 - x) >= 4.5) {
            for (int k = 0; k <= 90; k++) {
               double n9 = (double)((float)k * (float) (Math.PI / 180.0));
               GL11.glVertex2d((double)(x2 - radius) + Math.sin(n9) * (double)radius, (double)(y2 - radius) + Math.cos(n9) * (double)radius);
            }

            for (int l = 90; l <= 180; l++) {
               double n10 = (double)((float)l * (float) (Math.PI / 180.0));
               GL11.glVertex2d((double)(x2 - radius) + Math.sin(n10) * (double)radius, (double)(y + radius) + Math.cos(n10) * (double)radius);
            }
         }

         GL11.glEnd();
         GL11.glEnable(3553);
         GL11.glDisable(3042);
         GL11.glDisable(2848);
         GL11.glEnable(3553);
         GL11.glScaled(2.0, 2.0, 2.0);
         GL11.glPopAttrib();
         GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
      }
   }

   public static void drawRainbowText(String text, char lineSplit, int x, int y, long s, long shift, boolean rect, FontRenderer fontRenderer) {
      
      int bX = x;
      int l = 0;
      long r = 0L;

      for (int i = 0; i < text.length(); i++) {
         char c = text.charAt(i);
         if (c == lineSplit) {
            l++;
            x = bX;
            y += fontRenderer.FONT_HEIGHT + 5;
            r = shift * (long)l;
         } else {
            fontRenderer.drawString(String.valueOf(c), (float)x, (float)y, getChromaColor(s, r), rect);
            x += fontRenderer.getCharWidth(c);
            if (c != ' ') {
               r -= 90L;
            }
         }
      }
   }

   public static void drawCircleChroma(double x, double y, double z, double radius, int sides, float lineWidth, int color, boolean chroma) {
      float a = (float)(color >> 24 & 0xFF) / 255.0F;
      float r = (float)(color >> 16 & 0xFF) / 255.0F;
      float g = (float)(color >> 8 & 0xFF) / 255.0F;
      
      float b = (float)(color & 0xFF) / 255.0F;
      Myaven.mc.entityRenderer.disableLightmap();
      GL11.glDisable(3553);
      GL11.glEnable(3042);
      GL11.glBlendFunc(770, 771);
      GL11.glDisable(2929);
      GL11.glEnable(2848);
      GL11.glDepthMask(false);
      GL11.glLineWidth(lineWidth);
      if (!chroma) {
         GL11.glColor4f(r, g, b, a);
      }

      GL11.glBegin(1);
      long d = 0L;
      long ed = 15000L / (long)sides;
      long hed = ed / 2L;

      for (int i = 0; i < sides * 2; i++) {
         if (chroma) {
            if (i % 2 != 0) {
               if (i == 47) {
                  d = hed;
               }

               d += ed;
            }

            int c = getChromaColor(2L, d);
            float r2 = (float)(c >> 16 & 0xFF) / 255.0F;
            float g2 = (float)(c >> 8 & 0xFF) / 255.0F;
            float b2 = (float)(c & 0xFF) / 255.0F;
            GL11.glColor3f(r2, g2, b2);
         }

         double angle = (Math.PI * 2) * (double)i / (double)sides + Math.toRadians(180.0);
         GL11.glVertex3d(x + Math.cos(angle) * radius, y, z + Math.sin(angle) * radius);
      }

      GL11.glEnd();
      GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
      GL11.glDepthMask(true);
      GL11.glDisable(2848);
      GL11.glEnable(2929);
      GL11.glDisable(3042);
      GL11.glEnable(3553);
      Myaven.mc.entityRenderer.enableLightmap();
   }

   public static void drawArrowIndicator(float x, float y, int color, double width, double length) {
      GL11.glPushMatrix();
      GL11.glEnable(2848);
      GL11.glDisable(3553);
      setColorGL(color);
      GL11.glLineWidth((float)width);
      float halfWidth = (float)(width / 2.0);
      float xOffset = halfWidth / 2.0F;
      float yOffset = halfWidth / 2.0F;
      GL11.glBegin(1);
      GL11.glVertex2d((double)(x - xOffset), (double)(y + yOffset));
      GL11.glVertex2d((double)x + length - (double)xOffset, (double)y - length + (double)yOffset);
      GL11.glVertex2d((double)x + length - (double)xOffset, (double)y - length + (double)yOffset);
      GL11.glVertex2d((double)x + 2.0 * length - (double)xOffset, (double)(y + yOffset));
      GL11.glEnd();
      GL11.glEnable(3553);
      GL11.glDisable(2848);
      GL11.glPopMatrix();
   }

   public static void drawTrianglePointer(double x, double y, double size, double widthDiv, double heightDiv, int color) {
      boolean blend = GL11.glIsEnabled(3042);
      GL11.glEnable(3042);
      GL11.glDisable(3553);
      GL11.glBlendFunc(770, 771);
      GL11.glEnable(2848);
      GL11.glPushMatrix();
      setColorGL(color);
      
      GL11.glBegin(7);
      GL11.glVertex2d(x, y);
      GL11.glVertex2d(x - size / widthDiv, y + size);
      GL11.glVertex2d(x, y + size / heightDiv);
      GL11.glVertex2d(x + size / widthDiv, y + size);
      GL11.glVertex2d(x, y);
      GL11.glEnd();
      GL11.glColor4f(0.0F, 0.0F, 0.0F, 0.8F);
      GL11.glBegin(2);
      GL11.glVertex2d(x, y);
      GL11.glVertex2d(x - size / widthDiv, y + size);
      GL11.glVertex2d(x, y + size / heightDiv);
      GL11.glVertex2d(x + size / widthDiv, y + size);
      GL11.glVertex2d(x, y);
      GL11.glEnd();
      GL11.glPopMatrix();
      GL11.glEnable(3553);
      if (!blend) {
         GL11.glDisable(3042);
      }

      GL11.glDisable(2848);
   }

   public static void setColorGL(int n) {
      GL11.glColor4f((float)(n >> 16 & 0xFF) / 255.0F, (float)(n >> 8 & 0xFF) / 255.0F, (float)(n & 0xFF) / 255.0F, (float)(n >> 24 & 0xFF) / 255.0F);
   }

   public static void drawRoundedRectBordered(float x, float y, float x2, float y2, float radius, int n6, int n7, int n8) {
      x *= 2.0F;
      y *= 2.0F;
      x2 *= 2.0F;
      y2 *= 2.0F;
      
      GL11.glPushAttrib(1);
      GL11.glScaled(0.5, 0.5, 0.5);
      GL11.glEnable(3042);
      GL11.glDisable(3553);
      GL11.glEnable(2848);
      GL11.glBegin(9);
      setColorGL(n6);

      for (int i = 0; i <= 90; i += 3) {
         double n9 = (double)((float)i * (float) (Math.PI / 180.0));
         GL11.glVertex2d((double)(x + radius) + Math.sin(n9) * (double)radius * -1.0, (double)(y + radius) + Math.cos(n9) * (double)radius * -1.0);
      }

      for (int j = 90; j <= 180; j += 3) {
         double n10 = (double)((float)j * (float) (Math.PI / 180.0));
         GL11.glVertex2d((double)(x + radius) + Math.sin(n10) * (double)radius * -1.0, (double)(y2 - radius) + Math.cos(n10) * (double)radius * -1.0);
      }

      for (int k = 0; k <= 90; k += 3) {
         double n11 = (double)((float)k * (float) (Math.PI / 180.0));
         GL11.glVertex2d((double)(x2 - radius) + Math.sin(n11) * (double)radius, (double)(y2 - radius) + Math.cos(n11) * (double)radius);
      }

      for (int l = 90; l <= 180; l += 3) {
         double n12 = (double)((float)l * (float) (Math.PI / 180.0));
         GL11.glVertex2d((double)(x2 - radius) + Math.sin(n12) * (double)radius, (double)(y + radius) + Math.cos(n12) * (double)radius);
      }

      GL11.glEnd();
      GL11.glPushMatrix();
      GL11.glShadeModel(7425);
      GL11.glLineWidth(2.0F);
      GL11.glBegin(2);
      if ((long)n7 != 0L) {
         setColorGL(n7);
      }

      for (int n13 = 0; n13 <= 90; n13 += 3) {
         double n14 = (double)((float)n13 * (float) (Math.PI / 180.0));
         GL11.glVertex2d((double)(x + radius) + Math.sin(n14) * (double)radius * -1.0, (double)(y + radius) + Math.cos(n14) * (double)radius * -1.0);
      }

      for (int n15 = 90; n15 <= 180; n15 += 3) {
         double n16 = (double)((float)n15 * (float) (Math.PI / 180.0));
         GL11.glVertex2d((double)(x + radius) + Math.sin(n16) * (double)radius * -1.0, (double)(y2 - radius) + Math.cos(n16) * (double)radius * -1.0);
      }

      if (n8 != 0) {
         setColorGL(n8);
      }

      for (int n17 = 0; n17 <= 90; n17 += 3) {
         double n18 = (double)((float)n17 * (float) (Math.PI / 180.0));
         GL11.glVertex2d((double)(x2 - radius) + Math.sin(n18) * (double)radius, (double)(y2 - radius) + Math.cos(n18) * (double)radius);
      }

      for (int n19 = 90; n19 <= 180; n19 += 3) {
         double n20 = (double)((float)n19 * (float) (Math.PI / 180.0));
         GL11.glVertex2d((double)(x2 - radius) + Math.sin(n20) * (double)radius, (double)(y + radius) + Math.cos(n20) * (double)radius);
      }

      GL11.glEnd();
      GL11.glPopMatrix();
      GL11.glEnable(3553);
      GL11.glDisable(3042);
      GL11.glDisable(2848);
      GL11.glEnable(3553);
      GL11.glScaled(2.0, 2.0, 2.0);
      GL11.glPopAttrib();
      GL11.glLineWidth(1.0F);
      GL11.glShadeModel(7424);
      GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
   }

   public static void drawFilledPolygon(double x, double y, double radius, int sides, int color) {
      if (sides >= 3) {
         float ax = (float)(color >> 24 & 0xFF) / 255.0F;
         float r = (float)(color >> 16 & 0xFF) / 255.0F;
         float g = (float)(color >> 8 & 0xFF) / 255.0F;
         float b = (float)(color & 0xFF) / 255.0F;
         Tessellator tessellator = Tessellator.getInstance();
         WorldRenderer worldrenderer = tessellator.getWorldRenderer();
         GlStateManager.enableBlend();
         GlStateManager.disableTexture2D();
         GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
         GL11.glEnable(2848);
         GL11.glColor4f(r, g, b, ax);
         double rad180 = Math.toRadians(180.0);
         worldrenderer.begin(6, DefaultVertexFormats.POSITION);

         for (int i = 0; i < sides; i++) {
            double angle = (Math.PI * 2) * (double)i / (double)sides + rad180;
            worldrenderer.pos(x + Math.sin(angle) * radius, y + Math.cos(angle) * radius, 0.0).endVertex();
         }

         tessellator.draw();
         GlStateManager.enableTexture2D();
         GlStateManager.disableBlend();
      }
   }

   public static int getChromaColor(long speed, long... delay) {
      long time = System.currentTimeMillis() + (delay.length > 0 ? delay[0] : 0L);
      return Color.getHSBColor((float)(time % (15000L / speed)) / (15000.0F / (float)speed), 1.0F, 1.0F).getRGB();
   }

   public static void applyScissor(double x, double y, double width, double height) {
      
      ScaledResolution sr = new ScaledResolution(Myaven.mc);
      int scale = sr.getScaleFactor();
      int scaledX = (int)(x * (double)scale);
      int scaledY = (int)(((double)sr.getScaledHeight() - (y + height)) * (double)scale);
      int scaledWidth = (int)(width * (double)scale);
      int scaledHeight = (int)(height * (double)scale);
      if (scaledWidth >= 0 && scaledHeight >= 0) {
         GL11.glScissor(scaledX, scaledY, scaledWidth, scaledHeight);
      }
   }

   public static int applyAlphaToColor(int rgb, double alpha) {
      if (alpha < 0.0 || alpha > 1.0) {
         alpha = 0.5;
      }

      int red = rgb >> 16 & 0xFF;
      int green = rgb >> 8 & 0xFF;
      int blue = rgb & 0xFF;
      int alphaInt = (int)(alpha * 255.0);
      return alphaInt << 24 | red << 16 | green << 8 | blue;
   }

   public static void setGLColor(int argb) {
      float f = (float)(argb >> 24 & 0xFF) / 255.0F;
      float f2 = (float)(argb >> 16 & 0xFF) / 255.0F;
      float f3 = (float)(argb >> 8 & 0xFF) / 255.0F;
      float f4 = (float)(argb & 0xFF) / 255.0F;
      GlStateManager.color(f2, f3, f4, f);
   }

   public static void setGLColorWithAlpha(int color, double alpha) {
      float r = (float)(color >> 16 & 0xFF) / 255.0F;
      float g = (float)(color >> 8 & 0xFF) / 255.0F;
      float b = (float)(color & 0xFF) / 255.0F;
      GlStateManager.color(r, g, b, (float)alpha);
   }

   public static void resetGLColor() {
      GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
   }

   public static void prepareGL2D() {
      GL11.glDisable(2929);
      GL11.glEnable(3042);
      GL11.glDisable(3553);
      GL11.glBlendFunc(770, 771);
      GL11.glDepthMask(true);
      GL11.glEnable(2848);
      GL11.glHint(3154, 4354);
      GL11.glHint(3155, 4354);
   }

   public static void releaseGL2D() {
      GL11.glEnable(3553);
      GL11.glDisable(3042);
      GL11.glEnable(2929);
      GL11.glDisable(2848);
      GL11.glHint(3154, 4352);
      GL11.glHint(3155, 4352);
   }

   public static void drawTooltip(@NotNull String toolTip, int x, int y) {
      if (!toolTip.isEmpty()) {
         IFontRenderer font = ClickGUI.getFont();
         String[] split = toolTip.split("\n");
         double width = font.measureTextWidth(split[0]);
         double height = font.R();

         for (String s : split) {
            drawRect((double)(x + 5), (double)y + height - 2.0, (double)(x + 6) + width + 1.0, (double)y + height * 2.0, defaultBackgroundColor);
            font.drawString(s, (double)(x + 6), (double)y + height - 1.0, defaultTextColor);
            y += (int)Math.round(height);
         }
      }
   }

   @Contract("_, _, _ -> new")
   @NotNull
   public static Color interpolateColor(@NotNull Color color1, @NotNull Color color2, double ratio) {
      float r = (float)ratio;
      float ir = 1.0F - r;
      float[] rgb1 = new float[3];
      float[] rgb2 = new float[3];
      color1.getColorComponents(rgb1);
      color2.getColorComponents(rgb2);
      return new Color(rgb1[0] * r + rgb2[0] * ir, rgb1[1] * r + rgb2[1] * ir, rgb1[2] * r + rgb2[2] * ir);
   }

   @Contract("_, _ -> new")
   @NotNull
   public static Color blendColors(Color color1, Color color2) {
      return interpolateColor(color1, color2, 0.5);
   }

   @Contract("_, _ -> new")
   @NotNull
   public static Color blendColorsRGB(int color1, int color2) {
      return interpolateColor(toOpaqueColor(color1), toOpaqueColor(color2), 0.5);
   }

   @Contract("_ -> new")
   @NotNull
   public static Color toOpaqueColor(int color) {
      Color c = new Color(color);
      return new Color(c.getRed(), c.getGreen(), c.getBlue(), 255);
   }

   @NotNull
   public static Color withAlpha(@NotNull Color color, float alpha) {
      float r = 0.003921569F * (float)color.getRed();
      float g = 0.003921569F * (float)color.getGreen();
      float b = 0.003921569F * (float)color.getBlue();
      return new Color(r, g, b, alpha);
   }

   public static void drawVerticalGradient(double left, double top, double right, double bottom, int startColor, int endColor) {
      float f = (float)(startColor >> 24 & 0xFF) / 255.0F;
      float f1 = (float)(startColor >> 16 & 0xFF) / 255.0F;
      float f2 = (float)(startColor >> 8 & 0xFF) / 255.0F;
      float f3 = (float)(startColor & 0xFF) / 255.0F;
      float f4 = (float)(endColor >> 24 & 0xFF) / 255.0F;
      float f5 = (float)(endColor >> 16 & 0xFF) / 255.0F;
      float f6 = (float)(endColor >> 8 & 0xFF) / 255.0F;
      float f7 = (float)(endColor & 0xFF) / 255.0F;
      GlStateManager.disableTexture2D();
      GlStateManager.enableBlend();
      GlStateManager.disableAlpha();
      GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
      GlStateManager.shadeModel(7425);
      Tessellator tessellator = Tessellator.getInstance();
      WorldRenderer worldrenderer = tessellator.getWorldRenderer();
      worldrenderer.begin(7, DefaultVertexFormats.POSITION_COLOR);
      worldrenderer.pos(right, top, (double)renderDepthOffset).color(f1, f2, f3, f).endVertex();
      worldrenderer.pos(left, top, (double)renderDepthOffset).color(f1, f2, f3, f).endVertex();
      worldrenderer.pos(left, bottom, (double)renderDepthOffset).color(f5, f6, f7, f4).endVertex();
      worldrenderer.pos(right, bottom, (double)renderDepthOffset).color(f5, f6, f7, f4).endVertex();
      tessellator.draw();
      GlStateManager.shadeModel(7424);
      GlStateManager.disableBlend();
      GlStateManager.enableAlpha();
      GlStateManager.enableTexture2D();
   }

   public static void setGLColorD(double red, double green, double blue, double alpha) {
      GL11.glColor4d(red, green, blue, alpha);
   }

   public static void setGLColorBytes(int color) {
      GL11.glColor4ub((byte)(color >> 16 & 0xFF), (byte)(color >> 8 & 0xFF), (byte)(color & 0xFF), (byte)(color >> 24 & 0xFF));
   }

   public static void enableBlend() {
      GlStateManager.enableBlend();
      GlStateManager.blendFunc(770, 771);
   }

   public static void disableBlend() {
      GlStateManager.disableBlend();
   }

   public static float[] worldToScreen(float x, float y, float z, int scaleFactor) {
      GL11.glGetFloat(2982, modelViewBuffer);
      GL11.glGetFloat(2983, projectionBuffer);
      GL11.glGetInteger(2978, viewportBuffer);
      if (!GLU.gluProject(x, y, z, modelViewBuffer, projectionBuffer, viewportBuffer, projectedBuffer)) {
         return null;
      }

      projectedCoords[0] = projectedBuffer.get(0) / (float)scaleFactor;
      projectedCoords[1] = ((float)Display.getHeight() - projectedBuffer.get(1)) / (float)scaleFactor;
      projectedCoords[2] = projectedBuffer.get(2);
      return projectedCoords;
   }

   public static void drawBlockBox(@NotNull BlockPos blockPos, int color, boolean outline, boolean shade) {
      drawBoundingBox((double)blockPos.getX(), (double)blockPos.getY(), (double)blockPos.getZ(), 1.0, 1.0, 1.0, color, outline, shade);
   }

   public static boolean isInFrustum(Entity entity) {
      return isBoxInFrustum(entity.getEntityBoundingBox()) || entity.ignoreFrustumCheck;
   }

   private static boolean isBoxInFrustum(AxisAlignedBB bb) {
      cameraFrustum.setPosition(Myaven.mc.getRenderViewEntity().posX, Myaven.mc.getRenderViewEntity().posY, Myaven.mc.getRenderViewEntity().posZ);
      return cameraFrustum.isBoundingBoxInFrustum(bb);
   }

   public static Vec3 worldToScreen(int scaleFactor, double x, double y, double z) {
      GL11.glGetFloat(2982, modelMatrixBuffer);
      GL11.glGetFloat(2983, projectionMatrixBuffer);
      GL11.glGetInteger(2978, viewportIntBuffer);
      boolean result = GLU.gluProject((float)x, (float)y, (float)z, modelMatrixBuffer, projectionMatrixBuffer, viewportIntBuffer, projectedVecBuffer);
      return result
         ? new Vec3(
            (double)(projectedVecBuffer.get(0) / (float)scaleFactor),
            (double)(((float)Display.getHeight() - projectedVecBuffer.get(1)) / (float)scaleFactor),
            (double)projectedVecBuffer.get(2)
         )
         : null;
   }

   public static void drawRectWithBorder(float x1, float y1, float x2, float y2, float lineWidth, int backgroundColor, int lineColor) {
      
      drawRect(0.0, 0.0, (double)x2, 27.0, backgroundColor);
      if (lineColor != 0) {
         setGLColor(lineColor);
         GL11.glLineWidth(lineWidth);
         GL11.glEnable(2848);
         GL11.glHint(3154, 4354);
         GL11.glBegin(1);
         GL11.glVertex2f(x1, y1);
         GL11.glVertex2f(x1, y2);
         GL11.glVertex2f(x2, y2);
         GL11.glVertex2f(x2, y1);
         GL11.glVertex2f(x1, y1);
         GL11.glVertex2f(x2, y1);
         GL11.glVertex2f(x1, y2);
         GL11.glVertex2f(x2, y2);
         GL11.glEnd();
         GL11.glDisable(2848);
         GL11.glLineWidth(2.0F);
         GlStateManager.resetColor();
      }
   }

   static {
      defaultBackgroundColor = new Color(0, 0, 0, 100).getRGB();
      defaultTextColor = new Color(229, 229, 229, 255).getRGB();
      projectedBuffer = GLAllocation.createDirectFloatBuffer(4);
      viewportBuffer = GLAllocation.createDirectIntBuffer(16);
      modelViewBuffer = GLAllocation.createDirectFloatBuffer(16);
      projectionBuffer = GLAllocation.createDirectFloatBuffer(16);
      projectedCoords = new float[3];
      cameraFrustum = new Frustum();
      modelMatrixBuffer = BufferUtils.createFloatBuffer(16);
      projectionMatrixBuffer = BufferUtils.createFloatBuffer(16);
      viewportIntBuffer = BufferUtils.createIntBuffer(16);
      projectedVecBuffer = BufferUtils.createFloatBuffer(3);
   }
}
