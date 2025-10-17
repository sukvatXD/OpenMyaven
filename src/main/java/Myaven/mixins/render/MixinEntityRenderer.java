package Myaven.mixins.render;

import Myaven.Myaven;
import Myaven.events.Render2DEvent;
import Myaven.events.Render3DEvent;
import Myaven.mixins.accessor.AccessorEntityLivingBase;
import Myaven.module.modules.player.GhostHand;
import Myaven.module.modules.visual.NoHurtCam;
import Myaven.util.BlockUtil;
import Myaven.util.RotationUtil;
import java.util.List;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.init.Blocks;
import net.minecraft.potion.Potion;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.common.MinecraftForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin({EntityRenderer.class})
public abstract class MixinEntityRenderer {
   @Shadow
   private float thirdPersonDistance;
   @Shadow
   private float thirdPersonDistanceTemp;
   @Shadow
   private boolean cloudFog;
   @Shadow
   private Minecraft mc;
   @Shadow
   private Entity pointedEntity;
   private static String[] a;
   private static String[] b;

   @Shadow
   protected abstract void setupCameraTransform(float float1, int integer);

   @Redirect(
      method = {"getMouseOver"},
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/entity/Entity;getLook(F)Lnet/minecraft/util/Vec3;"
      )
   )
   private Vec3 J(Entity entity, float partialTicks) {
      return RotationUtil.getInterpolatedLookVec(partialTicks);
   }

   @Inject(
      method = {"getMouseOver"},
      at = {@At(
         value = "INVOKE",
         target = "Ljava/util/List;size()I"
      )},
      locals = LocalCapture.CAPTURE_FAILSOFT
   )
   private void t(
      float partialTicks,
      CallbackInfo ci,
      Entity entity,
      double d0,
      double d1,
      Vec3 vec3,
      boolean flag,
      int i,
      Vec3 vec31,
      Vec3 vec32,
      Vec3 vec33,
      float f,
      List<Entity> list,
      double d2,
      int j
   ) {
      if (Myaven.moduleManager.getModule("GhostHand").isEnabled()
         && BlockUtil.canReachBlock((double)this.mc.playerController.getBlockReachDistance())) {
         GhostHand.filterEntitiesForGhostHand(list);
      }
   }

   @Inject(
      method = {"updateCameraAndRender"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/client/gui/GuiIngame;renderGameOverlay(F)V",
         shift = At.Shift.AFTER
      )}
   )
   public void s(float partialTicks, long p_updateCameraAndRender_2_, CallbackInfo ci) {
      MinecraftForge.EVENT_BUS.post(new Render2DEvent(partialTicks));
   }

   @Inject(
      method = {"renderWorldPass"},
      at = {@At("RETURN")}
   )
   public void J(int pass, float partialTicks, long finishTimeNano, CallbackInfo ci) {
      this.setupCameraTransform(partialTicks, 0);
      MinecraftForge.EVENT_BUS.post(new Render3DEvent(partialTicks));
   }

   @Inject(
      method = {"hurtCameraEffect"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void q(float partialTicks, CallbackInfo ci) {
      if (this.mc.getRenderViewEntity() instanceof EntityLivingBase) {
         EntityLivingBase entitylivingbase = (EntityLivingBase)this.mc.getRenderViewEntity();
         float f = (float)entitylivingbase.hurtTime - partialTicks;
         if (entitylivingbase.getHealth() <= 0.0F) {
            float f1 = (float)entitylivingbase.deathTime + partialTicks;
            GlStateManager.rotate(40.0F - 8000.0F / (f1 + 200.0F), 0.0F, 0.0F, 1.0F);
         }

         if (f < 0.0F) {
            return;
         }

         f /= (float)entitylivingbase.maxHurtTime;
         f = MathHelper.sin(f * f * f * f * (float) Math.PI);
         float f2 = entitylivingbase.attackedAtYaw;
         if (Myaven.moduleManager.getModule("NoHurtCam").isEnabled()) {
            f = f * (float)NoHurtCam.effect.getPercentage() / 100.0F;
            f2 = f2 * (float)NoHurtCam.effect.getPercentage() / 100.0F;
         }

         GlStateManager.rotate(-f2, 0.0F, 1.0F, 0.0F);
         GlStateManager.rotate(-f * 14.0F, 0.0F, 0.0F, 1.0F);
         GlStateManager.rotate(f2, 0.0F, 1.0F, 0.0F);
      }

      ci.cancel();
   }

   @Inject(
      method = {"orientCamera"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/util/Vec3;distanceTo(Lnet/minecraft/util/Vec3;)D"
      )},
      cancellable = true
   )
   public void H(float partialTicks, CallbackInfo ci) {
      if (Myaven.moduleManager.getModule("ViewClip").isEnabled()) {
         ci.cancel();
         Entity entity = this.mc.getRenderViewEntity();
         float f = entity.getEyeHeight();
         if (entity instanceof EntityLivingBase && ((EntityLivingBase)entity).isPlayerSleeping()) {
            f = (float)((double)f + 1.0);
            GlStateManager.translate(0.0F, 0.3F, 0.0F);
            if (!this.mc.gameSettings.debugCamEnable) {
               BlockPos blockpos = new BlockPos(entity);
               IBlockState iblockstate = Blocks.air.getDefaultState();
               ForgeHooksClient.orientBedCamera(this.mc.theWorld, blockpos, iblockstate, entity);
               GlStateManager.rotate(entity.prevRotationYaw + (entity.rotationYaw - entity.prevRotationYaw) * partialTicks + 180.0F, 0.0F, -1.0F, 0.0F);
               GlStateManager.rotate(entity.prevRotationPitch + (entity.rotationPitch - entity.prevRotationPitch) * partialTicks, -1.0F, 0.0F, 0.0F);
            }
         } else if (this.mc.gameSettings.thirdPersonView > 0) {
            double d3 = (double)(this.thirdPersonDistanceTemp + (this.thirdPersonDistance - this.thirdPersonDistanceTemp) * partialTicks);
            if (this.mc.gameSettings.debugCamEnable) {
               GlStateManager.translate(0.0F, 0.0F, (float)(-d3));
            } else {
               float f1 = entity.rotationYaw;
               float f2 = entity.rotationPitch;
               if (this.mc.gameSettings.thirdPersonView == 2) {
                  f2 += 180.0F;
               }

               if (this.mc.gameSettings.thirdPersonView == 2) {
                  GlStateManager.rotate(180.0F, 0.0F, 1.0F, 0.0F);
               }

               GlStateManager.rotate(entity.rotationPitch - f2, 1.0F, 0.0F, 0.0F);
               GlStateManager.rotate(entity.rotationYaw - f1, 0.0F, 1.0F, 0.0F);
               GlStateManager.translate(0.0F, 0.0F, (float)(-d3));
               GlStateManager.rotate(f1 - entity.rotationYaw, 0.0F, 1.0F, 0.0F);
               GlStateManager.rotate(f2 - entity.rotationPitch, 1.0F, 0.0F, 0.0F);
            }
         } else {
            GlStateManager.translate(0.0F, 0.0F, -0.1F);
         }

         if (!this.mc.gameSettings.debugCamEnable) {
            float yaw = entity.prevRotationYaw + (entity.rotationYaw - entity.prevRotationYaw) * partialTicks + 180.0F;
            float pitch = entity.prevRotationPitch + (entity.rotationPitch - entity.prevRotationPitch) * partialTicks;
            float f1x = 0.0F;
            if (entity instanceof EntityAnimal) {
               EntityAnimal entityanimal = (EntityAnimal)entity;
               yaw = entityanimal.prevRotationYawHead + (entityanimal.rotationYawHead - entityanimal.prevRotationYawHead) * partialTicks + 180.0F;
            }

            GlStateManager.rotate(0.0F, 0.0F, 0.0F, 1.0F);
            GlStateManager.rotate(pitch, 1.0F, 0.0F, 0.0F);
            GlStateManager.rotate(yaw, 0.0F, 1.0F, 0.0F);
         }

         GlStateManager.translate(0.0F, -f, 0.0F);
         double d0 = entity.prevPosX + (entity.posX - entity.prevPosX) * (double)partialTicks;
         double d1 = entity.prevPosY + (entity.posY - entity.prevPosY) * (double)partialTicks + (double)f;
         double d2 = entity.prevPosZ + (entity.posZ - entity.prevPosZ) * (double)partialTicks;
         this.cloudFog = this.mc.renderGlobal.hasCloudFog(d0, d1, d2, partialTicks);
      }
   }

   @Redirect(
      method = {"updateFogColor"},
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/entity/EntityLivingBase;isPotionActive(Lnet/minecraft/potion/Potion;)Z"
      )
   )
   private boolean r(EntityLivingBase entityLivingBase, Potion potion) {
      return potion == Potion.blindness && Myaven.moduleManager.getModule("AntiDebuff").isEnabled()
         ? false
         : ((AccessorEntityLivingBase)entityLivingBase).getActivePotionsMap().containsKey(potion.id);
   }

   @Redirect(
      method = {"setupFog"},
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/entity/EntityLivingBase;isPotionActive(Lnet/minecraft/potion/Potion;)Z"
      )
   )
   private boolean B(EntityLivingBase entityLivingBase, Potion potion) {
      return potion == Potion.blindness && Myaven.moduleManager.getModule("AntiDebuff").isEnabled()
         ? false
         : ((AccessorEntityLivingBase)entityLivingBase).getActivePotionsMap().containsKey(potion.id);
   }

   @Redirect(
      method = {"setupCameraTransform"},
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/client/entity/EntityPlayerSP;isPotionActive(Lnet/minecraft/potion/Potion;)Z"
      )
   )
   private boolean R(EntityPlayerSP entityPlayerSP, Potion potion) {
      return potion == Potion.confusion && Myaven.moduleManager.getModule("AntiDebuff").isEnabled()
         ? false
         : ((AccessorEntityLivingBase)entityPlayerSP).getActivePotionsMap().containsKey(potion.id);
   }
}
