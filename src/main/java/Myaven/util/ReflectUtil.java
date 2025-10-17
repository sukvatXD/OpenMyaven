package Myaven.util;

import java.lang.reflect.Method;

import net.minecraft.client.particle.EffectRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MovingObjectPosition;

public class ReflectUtil {
    public static void invokeBlockHitEffects(EffectRenderer effectRenderer, BlockPos pos, MovingObjectPosition mop) {
        try {
            Method m = EffectRenderer.class.getDeclaredMethod("addBlockHitEffects", BlockPos.class, MovingObjectPosition.class);
            m.setAccessible(true);
            m.invoke(effectRenderer, pos, mop);
        } catch (Throwable t) {

        }
    }

    public static boolean invokeItemSwing(Item item, EntityLivingBase entity, ItemStack stack) {
        try {

            Method m = Item.class.getDeclaredMethod("onEntitySwing", EntityLivingBase.class, ItemStack.class);
            m.setAccessible(true);
            return (Boolean) m.invoke(item, entity, stack);
        } catch (Throwable t) {
            return false;
        }
    }

    public static boolean canRiderInteract(Entity entity) {
        try {

            Method m = Entity.class.getDeclaredMethod("canRiderInteract");
            m.setAccessible(true);
            return (Boolean) m.invoke(entity);
        } catch (Throwable t) {
            return false;
        }
    }
}
