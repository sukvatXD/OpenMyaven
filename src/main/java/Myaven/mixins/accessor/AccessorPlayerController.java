package Myaven.mixins.accessor;

import net.minecraft.client.multiplayer.PlayerControllerMP;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin({PlayerControllerMP.class})
public interface AccessorPlayerController {
   @Accessor("blockHitDelay")
   void setBlockHitDelay(int integer);

   @Accessor("blockHitDelay")
   int getBlockHitDelay();

   @Accessor("curBlockDamageMP")
   void setCurBlockDamageMP(float float1);

   @Accessor("curBlockDamageMP")
   float getCurBlockDamageMP();
}
