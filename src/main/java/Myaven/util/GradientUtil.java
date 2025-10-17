package Myaven.util;

import Myaven.Myaven;
import Myaven.setting.Setting;
import java.awt.Color;
import java.nio.Buffer;
import java.nio.IntBuffer;
import net.minecraft.client.renderer.texture.TextureUtil;
import org.jetbrains.annotations.Range;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;

public class GradientUtil {
   private GradientDirection v;
   private int startRed;
   private int startGreen;
   private int startBlue;
   private int endRed;
   private int endGreen;
   private int endBlue;
   private int prevStartRed;
   private int prevStartGreen;
   private int prevStartBlue;
   private int prevEndRed;
   private int prevEndGreen;
   private int prevEndBlue;
   private int interpolatedStartRed;
   private int interpolatedStartGreen;
   private int interpolatedStartBlue;
   private int interpolatedEndRed;
   private int interpolatedEndGreen;
   private int interpolatedEndBlue;
   private boolean c = false;
   private int[] T;
   private static long[] b;
   private static Integer[] e;

   public GradientUtil(GradientDirection type) {
      this.v = type;
   }

   public void drawGradient(float x, float y, float width, float height) {
      this.drawGradient((int)x, (int)y, (int)width, (int)height);
   }

   public void drawGradient(int x, int y, int width, int height) {
      this.T = this.readScreenPixels(x, y, width, height);
      int leftTop = this.T[0];
      int rightTop = this.T[width - 1];
      int leftBottom = this.T[(height - 1) * width - 1];
      int rightBottom = this.T[height * width - 1];
      Color color1;
      Color color2;
      switch (this.v) {
         case LR:
            color1 = RenderUtil.blendColorsRGB(leftTop, leftBottom);
            color2 = RenderUtil.blendColorsRGB(rightTop, rightBottom);
            break;
         case TB:
         default:
            color1 = RenderUtil.blendColorsRGB(leftTop, rightTop);
            color2 = RenderUtil.blendColorsRGB(leftBottom, rightBottom);
      }

      this.startRed = color1.getRed();
      this.startGreen = color1.getGreen();
      this.startBlue = color1.getBlue();
      this.endRed = color2.getRed();
      this.endGreen = color2.getGreen();
      this.endBlue = color2.getBlue();
      if (!this.c) {
         this.prevStartRed = this.startRed;
         this.prevStartGreen = this.startBlue;
         this.prevStartBlue = this.startGreen;
         this.interpolatedStartRed = this.endRed;
         this.interpolatedStartGreen = this.endBlue;
         this.interpolatedStartBlue = this.endGreen;
         this.c = true;
      }

      this.prevEndRed = this.prevStartRed;
      this.prevEndGreen = this.prevStartGreen;
      this.prevEndBlue = this.prevStartBlue;
      this.interpolatedEndRed = this.interpolatedStartRed;
      this.interpolatedEndGreen = this.interpolatedStartGreen;
      this.interpolatedEndBlue = this.interpolatedStartBlue;
      this.prevStartRed = this.smoothTransition((double)this.prevStartRed, (double)this.startRed);
      this.prevStartGreen = this.smoothTransition((double)this.prevStartGreen, (double)this.startGreen);
      this.prevStartBlue = this.smoothTransition((double)this.prevStartBlue, (double)this.startBlue);
      this.interpolatedStartRed = this.smoothTransition((double)this.interpolatedStartRed, (double)this.endRed);
      this.interpolatedStartGreen = this.smoothTransition((double)this.interpolatedStartGreen, (double)this.endGreen);
      this.interpolatedStartBlue = this.smoothTransition((double)this.interpolatedStartBlue, (double)this.endBlue);
      this.prevStartRed = Math.min(this.prevStartRed, 255);
      this.prevStartGreen = Math.min(this.prevStartGreen, 255);
      this.prevStartBlue = Math.min(this.prevStartBlue, 255);
      this.prevStartRed = Math.max(this.prevStartRed, 0);
      this.prevStartGreen = Math.max(this.prevStartGreen, 0);
      this.prevStartBlue = Math.max(this.prevStartBlue, 0);
      this.interpolatedStartRed = Math.min(this.interpolatedStartRed, 255);
      this.interpolatedStartGreen = Math.min(this.interpolatedStartGreen, 255);
      this.interpolatedStartBlue = Math.min(this.interpolatedStartBlue, 255);
      this.interpolatedStartRed = Math.max(this.interpolatedStartRed, 0);
      this.interpolatedStartGreen = Math.max(this.interpolatedStartGreen, 0);
      this.interpolatedStartBlue = Math.max(this.interpolatedStartBlue, 0);
   }

   public void renderGradient(float x, float y, float width, float height, @Range(from = 0L,to = 1L) float partialTicks, @Range(from = 0L,to = 1L) float alpha) {
      Color color1 = new Color(
         this.interpolate((double)this.prevStartRed, (double)this.prevEndRed, partialTicks),
         this.interpolate((double)this.prevStartGreen, (double)this.prevEndGreen, partialTicks),
         this.interpolate((double)this.prevStartBlue, (double)this.prevEndBlue, partialTicks)
      );
      Color color2 = new Color(
         this.interpolate((double)this.interpolatedStartRed, (double)this.interpolatedEndRed, partialTicks),
         this.interpolate((double)this.interpolatedStartGreen, (double)this.interpolatedEndGreen, partialTicks),
         this.interpolate((double)this.interpolatedStartBlue, (double)this.interpolatedEndBlue, partialTicks)
      );
      color1 = RenderUtil.withAlpha(color1, alpha);
      color2 = RenderUtil.withAlpha(color2, alpha);
      switch (this.v) {
         case LR:
         case TB:
            RenderUtil.drawVerticalGradient((double)x, (double)y, (double)(x + width), (double)(y + height), color1.getRGB(), color2.getRGB());
      }
   }

   private int smoothTransition(double current, double target) {
      return (int)(current + (target - current) / 10.0);
   }

   private int interpolate(double current, double last, float partialTicks) {
      return (int)(current * (double)partialTicks + last * (double)(1.0F - partialTicks));
   }

   private int[] readScreenPixels(int x, int y, int width, int height) {
      int size = width * height;
      IntBuffer pixelBuffer = (IntBuffer)((Buffer)BufferUtils.createIntBuffer(size)).clear();
      int[] pixelValues = new int[size];
      GL11.glPixelStorei(3333, 1);
      GL11.glPixelStorei(3317, 1);
      int scaleFactor = 1;
      int k = Myaven.mc.gameSettings.guiScale;
      if (k == 0) {
         k = 1000;
      }

      while (scaleFactor < k && Myaven.mc.displayWidth / (scaleFactor + 1) >= 320 && Myaven.mc.displayHeight / (scaleFactor + 1) >= 240) {
         scaleFactor++;
      }

      GL11.glReadPixels(x * scaleFactor, Myaven.mc.displayHeight - (y + 6) * scaleFactor, width, height, 32993, 33639, pixelBuffer);
      pixelBuffer.get(pixelValues);
      TextureUtil.processPixelValues(pixelValues, width, height);
      return pixelValues;
   }

   public GradientUtil.GradientDirection y() {
      return this.v;
   }

   public int T() {
      return this.startRed;
   }

   public int u() {
      return this.startGreen;
   }

   public int v() {
      return this.startBlue;
   }

   public int c() {
      return this.endRed;
   }

   public int Z() {
      return this.endGreen;
   }

   public int M() {
      return this.endBlue;
   }

   public int C() {
      return this.prevStartRed;
   }

   public int K() {
      return this.prevStartGreen;
   }

   public int J() {
      return this.prevStartBlue;
   }

   public int Q() {
      return this.prevEndRed;
   }

   public int l() {
      return this.prevEndGreen;
   }

   public int F() {
      return this.prevEndBlue;
   }

   public int j() {
      return this.interpolatedStartRed;
   }

   public int U() {
      return this.interpolatedStartBlue;
   }

   public int O() {
      return this.interpolatedEndRed;
   }

   public int N() {
      return this.interpolatedEndGreen;
   }

   public int W() {
      return this.interpolatedEndBlue;
   }

   public boolean R() {
      return this.c;
   }

   public int[] I() {
      return this.T;
   }
   public static enum GradientDirection {
      TB,
      LR;
   }
}
