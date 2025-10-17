package Myaven.mixins.entity;

import Myaven.Myaven;
import Myaven.events.AttackEvent;
import Myaven.events.FinishedUseEvent;
import Myaven.events.PostAttackEvent;
import Myaven.module.modules.combat.KeepSprint;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.IEntityMultiPart;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.boss.EntityDragonPart;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.server.S12PacketEntityVelocity;
import net.minecraft.potion.Potion;
import net.minecraft.stats.AchievementList;
import net.minecraft.stats.StatList;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.MinecraftForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({EntityPlayer.class})
public abstract class MixinEntityPlayer extends EntityLivingBase {

   public MixinEntityPlayer(World p_i1594_1_) {
      super(p_i1594_1_);
   }

   @Inject(
      method = {"onItemUseFinish"},
      at = {@At("TAIL")}
   )
   protected void k(CallbackInfo ci) {
      MinecraftForge.EVENT_BUS.post(new FinishedUseEvent());
   }

   @Inject(
      method = {"attackTargetEntityWithCurrentItem"},
      at = {@At("HEAD")},
      cancellable = true
   )
   public void l(Entity p_attackTargetEntityWithCurrentItem_1_, CallbackInfo ci) {
      AttackEvent event = new AttackEvent();
      MinecraftForge.EVENT_BUS.post(event);
      if (event.isCanceled()) {
         ci.cancel();
      }

      if (ForgeHooks.onPlayerAttackTarget((EntityPlayer)((Object)this), p_attackTargetEntityWithCurrentItem_1_)
         && p_attackTargetEntityWithCurrentItem_1_.canAttackWithItem()
         && !p_attackTargetEntityWithCurrentItem_1_.hitByEntity(this)) {
         float f = (float)this.getEntityAttribute(SharedMonsterAttributes.attackDamage).getAttributeValue();
         int i = 0;
         float f1;
         if (p_attackTargetEntityWithCurrentItem_1_ instanceof EntityLivingBase) {
            f1 = EnchantmentHelper.getModifierForCreature(this.getHeldItem(), ((EntityLivingBase)p_attackTargetEntityWithCurrentItem_1_).getCreatureAttribute());
         } else {
            f1 = EnchantmentHelper.getModifierForCreature(this.getHeldItem(), EnumCreatureAttribute.UNDEFINED);
         }

         i = 0 + EnchantmentHelper.getKnockbackModifier(this);
         if (this.isSprinting()) {
            i++;
         }

         if (f > 0.0F || f1 > 0.0F) {
            boolean flag = this.fallDistance > 0.0F
               && !this.onGround
               && !this.isOnLadder()
               && !this.isInWater()
               && !this.isPotionActive(Potion.blindness)
               && this.ridingEntity == null
               && p_attackTargetEntityWithCurrentItem_1_ instanceof EntityLivingBase;
            if (flag && f > 0.0F) {
               f *= 1.5F;
            }

            f += f1;
            boolean flag1 = false;
            int j = EnchantmentHelper.getFireAspectModifier(this);
            if (p_attackTargetEntityWithCurrentItem_1_ instanceof EntityLivingBase && j > 0 && !p_attackTargetEntityWithCurrentItem_1_.isBurning()) {
               flag1 = true;
               p_attackTargetEntityWithCurrentItem_1_.setFire(1);
            }

            double d0 = p_attackTargetEntityWithCurrentItem_1_.motionX;
            double d1 = p_attackTargetEntityWithCurrentItem_1_.motionY;
            double d2 = p_attackTargetEntityWithCurrentItem_1_.motionZ;
            boolean flag2 = p_attackTargetEntityWithCurrentItem_1_.attackEntityFrom(DamageSource.causePlayerDamage((EntityPlayer)((Object)this)), f);
            if (flag2) {
               if (i > 0) {
                  p_attackTargetEntityWithCurrentItem_1_.addVelocity(
                     (double)(-MathHelper.sin(this.rotationYaw * (float) Math.PI / 180.0F) * (float)i * 0.5F),
                     0.1,
                     (double)(MathHelper.cos(this.rotationYaw * (float) Math.PI / 180.0F) * (float)i * 0.5F)
                  );
                  if (Myaven.moduleManager.getModule("keepsprint").isEnabled()) {
                     KeepSprint.executeKeepSprint();
                     this.setSprinting(false);
                  } else {
                     this.motionX *= 0.6;
                     this.motionZ *= 0.6;
                     this.setSprinting(false);
                  }
               }

               if (p_attackTargetEntityWithCurrentItem_1_ instanceof EntityPlayerMP && p_attackTargetEntityWithCurrentItem_1_.velocityChanged) {
                  ((EntityPlayerMP)p_attackTargetEntityWithCurrentItem_1_)
                     .playerNetServerHandler
                     .sendPacket(new S12PacketEntityVelocity(p_attackTargetEntityWithCurrentItem_1_));
                  p_attackTargetEntityWithCurrentItem_1_.velocityChanged = false;
                  p_attackTargetEntityWithCurrentItem_1_.motionX = d0;
                  p_attackTargetEntityWithCurrentItem_1_.motionY = d1;
                  p_attackTargetEntityWithCurrentItem_1_.motionZ = d2;
               }

               if (f >= 18.0F) {
                  Myaven.mc.thePlayer.triggerAchievement(AchievementList.overkill);
               }

               this.setLastAttacker(p_attackTargetEntityWithCurrentItem_1_);
               if (p_attackTargetEntityWithCurrentItem_1_ instanceof EntityLivingBase) {
                  EnchantmentHelper.applyThornEnchantments((EntityLivingBase)p_attackTargetEntityWithCurrentItem_1_, this);
               }

               EnchantmentHelper.applyArthropodEnchantments(this, p_attackTargetEntityWithCurrentItem_1_);
               ItemStack itemstack = Myaven.mc.thePlayer.getCurrentEquippedItem();
               Entity entity1 = p_attackTargetEntityWithCurrentItem_1_;
               if (p_attackTargetEntityWithCurrentItem_1_ instanceof EntityDragonPart) {
                  IEntityMultiPart ientitymultipart = ((EntityDragonPart)p_attackTargetEntityWithCurrentItem_1_).entityDragonObj;
                  if (ientitymultipart instanceof EntityLivingBase) {
                     entity1 = (EntityLivingBase)ientitymultipart;
                  }
               }

               if (itemstack != null && entity1 instanceof EntityLivingBase) {
                  itemstack.hitEntity((EntityLivingBase)entity1, (EntityPlayer)((Object)this));
                  if (itemstack.stackSize <= 0) {
                     Myaven.mc.thePlayer.destroyCurrentEquippedItem();
                  }
               }

               if (p_attackTargetEntityWithCurrentItem_1_ instanceof EntityLivingBase) {
                  Myaven.mc.thePlayer.addStat(StatList.damageDealtStat, Math.round(f * 10.0F));
                  if (j > 0) {
                     p_attackTargetEntityWithCurrentItem_1_.setFire(j * 4);
                  }
               }

               Myaven.mc.thePlayer.addExhaustion(0.3F);
            } else if (flag1) {
               p_attackTargetEntityWithCurrentItem_1_.extinguish();
            }
         }

         MinecraftForge.EVENT_BUS.post(new PostAttackEvent(p_attackTargetEntityWithCurrentItem_1_));
      }

      ci.cancel();
   }
}
