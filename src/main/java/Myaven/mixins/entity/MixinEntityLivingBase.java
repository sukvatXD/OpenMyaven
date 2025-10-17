package Myaven.mixins.entity;

import Myaven.Myaven;
import Myaven.events.JumpEvent;
import Myaven.events.MoveEntityEvent;
import Myaven.events.MoveEvent;
import Myaven.management.RotationManager;
import Myaven.util.RotationUtil;
import com.google.common.collect.Maps;
import java.util.Map;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.MinecraftForge;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({EntityLivingBase.class})
public abstract class MixinEntityLivingBase extends Entity {
   @Unique
   private Map<Integer, PotionEffect> activePotionsMap = Maps.newHashMap();

   @Shadow
   protected abstract float getJumpUpwardsMotion();

   @Unique
   public boolean T(@NotNull Potion potionIn) {
      return this.activePotionsMap.containsKey(potionIn.id);
   }

   @Unique
   public PotionEffect r(@NotNull Potion potionIn) {
      return this.activePotionsMap.get(potionIn.id);
   }

   public MixinEntityLivingBase(World worldIn) {
      super(worldIn);
   }

   @Inject(
      method = {"jump"},
      at = {@At("HEAD")},
      cancellable = true
   )
   protected void u(CallbackInfo ci) {
      JumpEvent event = new JumpEvent(this.getJumpUpwardsMotion(), RotationManager.getMovementYaw());
      MinecraftForge.EVENT_BUS.post(event);
      if (!event.isCanceled()) {
         this.motionY = (double)event.getMotionY();
         if (this.T(Potion.jump)) {
            this.motionY = this.motionY + (double)((float)(this.r(Potion.jump).getAmplifier() + 1) * 0.1F);
         }

         if (this.isSprinting()) {
            float f = event.getYaw() * (float) (Math.PI / 180.0);
            this.motionX = this.motionX - (double)(MathHelper.sin(f) * 0.2F);
            this.motionZ = this.motionZ + (double)(MathHelper.cos(f) * 0.2F);
         }

         this.isAirBorne = true;
         ForgeHooks.onLivingJump((EntityLivingBase)((Object)this));
         ci.cancel();
      }
   }

   @Inject(
      method = {"moveEntityWithHeading"},
      at = {@At("HEAD")},
      cancellable = true
   )
   public void r(float moveForward, float moveStrafing, CallbackInfo ci) {
      if (((Object)this) instanceof EntityPlayerSP) {
         MoveEntityEvent event = new MoveEntityEvent();
         MinecraftForge.EVENT_BUS.post(event);
         if (event.isCanceled()) {
            ci.cancel();
         }
      }
   }

   @Redirect(
      method = {"moveEntityWithHeading"},
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/entity/EntityLivingBase;moveEntity(DDD)V"
      )
   )
   public void k(EntityLivingBase instance, double x, double y, double z) {
      if (instance instanceof EntityPlayerSP) {
         MoveEvent event = new MoveEvent(x, y, z);
         MinecraftForge.EVENT_BUS.post(event);
         if (event.isCanceled()) {
            return;
         }

         x = event.getX();
         y = event.getY();
         z = event.getZ();
      }

      instance.moveEntity(x, y, z);
   }

   @Inject(
      method = {"updateDistance"},
      at = {@At("HEAD")},
      cancellable = true
   )
   protected void j(float p_1101461, float p_1101462, CallbackInfoReturnable<Float> cir) {
      float rotationYaw = this.rotationYaw;
      if ((EntityLivingBase)((Object)this) instanceof EntityPlayerSP) {
         if (Myaven.mc.thePlayer.swingProgress > 0.0F) {
            p_1101461 = RotationUtil.N;
         }

         rotationYaw = RotationUtil.N;
         Myaven.mc.thePlayer.rotationYawHead = RotationUtil.N;
      }

      float f = MathHelper.wrapAngleTo180_float(p_1101461 - ((EntityLivingBase)((Object)this)).renderYawOffset);
      ((EntityLivingBase)((Object)this)).renderYawOffset += f * 0.3F;
      float f1 = MathHelper.wrapAngleTo180_float(rotationYaw - ((EntityLivingBase)((Object)this)).renderYawOffset);
      boolean flag = f1 < 90.0F || f1 >= 90.0F;
      if (f1 < -75.0F) {
         f1 = -75.0F;
      }

      if (f1 >= 75.0F) {
         f1 = 75.0F;
      }

      ((EntityLivingBase)((Object)this)).renderYawOffset = rotationYaw - f1;
      if (f1 * f1 > 2500.0F) {
         ((EntityLivingBase)((Object)this)).renderYawOffset += f1 * 0.2F;
      }

      if (flag) {
         p_1101462 *= -1.0F;
      }

      cir.setReturnValue(p_1101462);
   }

   private static RuntimeException a(RuntimeException runtimeException) {
      return runtimeException;
   }
}
