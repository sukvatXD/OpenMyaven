package Myaven.mixins.accessor;

import java.util.Map;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.potion.PotionEffect;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin({EntityLivingBase.class})
public interface AccessorEntityLivingBase {
   @Accessor
   Map<Integer, PotionEffect> getActivePotionsMap();

   @Accessor
   AttributeModifier getSprintingSpeedBoostModifier();

   @Accessor
   int getJumpTicks();

   @Accessor
   void setJumpTicks(int integer);
}
