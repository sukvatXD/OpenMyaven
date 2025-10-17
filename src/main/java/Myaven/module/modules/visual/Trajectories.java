package Myaven.module.modules.visual;

import Myaven.events.Render3DEvent;
import Myaven.mixins.accessor.AccessorRenderManager;
import Myaven.module.Category;
import Myaven.module.Module;
import Myaven.module.modules.visual.TeamInvisible;
import Myaven.setting.settings.BooleanSetting;
import Myaven.util.RenderUtil;
import java.awt.Color;
import java.util.ArrayList;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemEgg;
import net.minecraft.item.ItemEnderPearl;
import net.minecraft.item.ItemFishingRod;
import net.minecraft.item.ItemSnowball;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.opengl.GL11;

public class Trajectories
        extends Module {
   public static BooleanSetting requirePullBow;
   private static String[] a;
   private static String[] b;
   private static long[] d;
   private static Integer[] g;

   public Trajectories() {
      super("Trajectories", false, Category.Visual, false, "Show trajectories of projectiles");
      this.addSettings(requirePullBow);
   }

   @SubscribeEvent
   public void onRender(Render3DEvent event) {
      if (this.isEnabled() && this.mc.thePlayer.getHeldItem() != null && this.mc.gameSettings.thirdPersonView == 0) {
         float hitboxExpand;
         float gravity;
         Item item = this.mc.thePlayer.getHeldItem().getItem();
         RenderManager renderManager = this.mc.getRenderManager();
         boolean isBow = false;
         float velocityMultiplier = 1.5f;
         float drag = 0.99f;
         if (item instanceof ItemBow) {
            isBow = true;
            gravity = 0.05f;
            hitboxExpand = 0.3f;
            float charge = (float)this.mc.thePlayer.getItemInUseDuration() / 20.0f;
            if ((charge = (charge * charge + charge * 2.0f) / 3.0f) < 0.1f && requirePullBow.getState()) {
               return;
            }
            if (charge > 1.0f) {
               charge = 1.0f;
            }
            velocityMultiplier = charge * 3.0f;
         } else if (item instanceof ItemFishingRod) {
            gravity = 0.04f;
            hitboxExpand = 0.25f;
            drag = 0.92f;
         } else if (item instanceof ItemSnowball || item instanceof ItemEgg) {
            gravity = 0.03f;
            hitboxExpand = 0.25f;
         } else {
            if (!(item instanceof ItemEnderPearl)) {
               return;
            }
            gravity = 0.03f;
            hitboxExpand = 0.25f;
         }
         float yaw = this.mc.thePlayer.rotationYaw;
         float pitch = this.mc.thePlayer.rotationPitch;
         double x = ((AccessorRenderManager)renderManager).getRenderPosX() - (double)MathHelper.cos((float)(yaw / 180.0f * (float)Math.PI)) * 0.16;
         double y = ((AccessorRenderManager)renderManager).getRenderPosY() + (double)this.mc.thePlayer.getEyeHeight() - (double)0.1f;
         double z = ((AccessorRenderManager)renderManager).getRenderPosZ() - (double)MathHelper.sin((float)(yaw / 180.0f * (float)Math.PI)) * 0.16;
         double mx = (double)(MathHelper.sin((float)(yaw / 180.0f * (float)Math.PI)) * MathHelper.cos((float)(pitch / 180.0f * (float)Math.PI))) * 1.0 * -1.0;
         double my = (double)MathHelper.sin((float)(pitch / 180.0f * (float)Math.PI)) * 1.0 * -1.0;
         double mz = (double)(MathHelper.cos((float)(yaw / 180.0f * (float)Math.PI)) * MathHelper.cos((float)(pitch / 180.0f * (float)Math.PI))) * 1.0;
         float mag = MathHelper.sqrt_double((double)(mx * mx + my * my + mz * mz));
         mx /= (double)mag;
         my /= (double)mag;
         mz /= (double)mag;
         mx *= (double)velocityMultiplier;
         my *= (double)velocityMultiplier;
         mz *= (double)velocityMultiplier;
         MovingObjectPosition mop = null;
         boolean hasHitBlock = false;
         boolean hasHitEntity = false;
         WorldRenderer worldRenderer = Tessellator.getInstance().getWorldRenderer();
         ArrayList<Vec3> trajectoryPoints = new ArrayList<Vec3>();
         while (!hasHitBlock && y > 0.0) {
            Vec3 start = new Vec3(x, y, z);
            Vec3 end = new Vec3(x + mx, y + my, z + mz);
            mop = this.mc.theWorld.rayTraceBlocks(start, end, false, true, false);
            start = new Vec3(x, y, z);
            end = new Vec3(x + mx, y + my, z + mz);
            if (mop != null) {
               hasHitBlock = true;
               end = new Vec3(mop.hitVec.xCoord, mop.hitVec.yCoord, mop.hitVec.zCoord);
            }
            AxisAlignedBB aabb = new AxisAlignedBB(x - (double)hitboxExpand, y - (double)hitboxExpand, z - (double)hitboxExpand, x + (double)hitboxExpand, y + (double)hitboxExpand, z + (double)hitboxExpand).addCoord(mx, my, mz).expand(1.0, 1.0, 1.0);
            int minChunkX = MathHelper.floor_double((double)((aabb.minX - 2.0) / 16.0));
            int maxChunkX = MathHelper.floor_double((double)((aabb.maxX + 2.0) / 16.0));
            int minChunkZ = MathHelper.floor_double((double)((aabb.minZ - 2.0) / 16.0));
            int maxChunkZ = MathHelper.floor_double((double)((aabb.maxZ + 2.0) / 16.0));
            ArrayList<Entity> possibleEntities = new ArrayList<>();
            for (int x1 = minChunkX; x1 <= maxChunkX; ++x1) {
               for (int z1 = minChunkZ; z1 <= maxChunkZ; ++z1) {
                  this.mc.theWorld.getChunkFromChunkCoords(x1, z1).getEntitiesWithinAABBForEntity((Entity)this.mc.thePlayer, aabb, possibleEntities, null);
               }
            }
            for (Entity entity : possibleEntities) {
               AxisAlignedBB entityBox;
               MovingObjectPosition intercept;
               if (!entity.canBeCollidedWith() || entity == this.mc.thePlayer || (intercept = (entityBox = entity.getEntityBoundingBox().expand((double)hitboxExpand, (double)hitboxExpand, (double)hitboxExpand)).calculateIntercept(start, end)) == null) continue;
               RenderUtil.drawEntityESP(entity, 8, 0.0, 0.0, Color.RED.getRGB(), false);
               hasHitEntity = true;
               hasHitBlock = true;
               mop = intercept;
            }
            if (this.mc.theWorld.getBlockState(new BlockPos(x += mx, y += my, z += mz)).getBlock().getMaterial() == Material.water) {
               mx *= 0.6;
               my *= 0.6;
               mz *= 0.6;
            } else {
               mx *= (double)drag;
               my *= (double)drag;
               mz *= (double)drag;
            }
            my -= (double)gravity;
            trajectoryPoints.add(new Vec3(x - ((AccessorRenderManager)renderManager).getRenderPosX(), y - ((AccessorRenderManager)renderManager).getRenderPosY(), z - ((AccessorRenderManager)renderManager).getRenderPosZ()));
         }
         if (trajectoryPoints.size() > 1) {
            RenderUtil.setup2DRenderState();
            RenderUtil.setGLColor(new Color(85, 255, 85, 255).getRGB());
            GL11.glLineWidth((float)1.5f);
            GL11.glEnable((int)2848);
            GL11.glHint((int)3154, (int)4354);
            worldRenderer.begin(3, DefaultVertexFormats.POSITION);
            trajectoryPoints.forEach(vec3 -> worldRenderer.pos(vec3.xCoord, vec3.yCoord, vec3.zCoord).endVertex());
            Tessellator.getInstance().draw();
            GlStateManager.pushMatrix();
            GlStateManager.translate((double)(x - ((AccessorRenderManager)renderManager).getRenderPosX()), (double)(y - ((AccessorRenderManager)renderManager).getRenderPosY()), (double)(z - ((AccessorRenderManager)renderManager).getRenderPosZ()));
            if (mop != null) {
               switch (mop.sideHit.getAxis().ordinal()) {
                  case 0: {
                     GlStateManager.rotate((float)90.0f, (float)0.0f, (float)1.0f, (float)0.0f);
                     break;
                  }
                  case 1: {
                     GlStateManager.rotate((float)90.0f, (float)1.0f, (float)0.0f, (float)0.0f);
                     break;
                  }
               }
               this.t(-0.25f, -0.25f, 0.25f, 0.25f, 1.5f, new Color(Color.RED.getRed(), 255, Color.RED.getGreen(), 255).getRGB());
               this.t(-0.25f, 0.25f, 0.25f, -0.25f, 1.5f, new Color(Color.RED.getRed(), 255, Color.RED.getGreen(), 255).getRGB());
            }
            GlStateManager.popMatrix();
            GL11.glDisable((int)2848);
            GL11.glLineWidth((float)2.0f);
            GlStateManager.resetColor();
            RenderUtil.restore3DRenderState();
         }
      }
   }

   private void t(float x1, float y1, float x2, float y2, float lineWidth, int color) {
      RenderUtil.setGLColor(color);
      GL11.glLineWidth((float)lineWidth);
      GL11.glEnable((int)2848);
      GL11.glHint((int)3154, (int)4354);
      GL11.glBegin((int)1);
      GL11.glVertex2f((float)x1, (float)y1);
      GL11.glVertex2f((float)x2, (float)y2);
      GL11.glEnd();
      GL11.glDisable((int)2848);
      GL11.glLineWidth((float)2.0f);
      GlStateManager.resetColor();
   }

   static {
      Trajectories.requirePullBow = new BooleanSetting("Require-pull-bow", false);
   }
}
