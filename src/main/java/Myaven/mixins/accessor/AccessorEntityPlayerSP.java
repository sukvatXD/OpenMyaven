package Myaven.mixins.accessor;

import net.minecraft.client.entity.EntityPlayerSP;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin({EntityPlayerSP.class})
public interface AccessorEntityPlayerSP {
   @Accessor("serverSprintState")
   boolean isServerSprint();

   @Accessor("serverSprintState")
   void setServerSprint(boolean boolean1);

   @Accessor("positionUpdateTicks")
   int getPositionUpdateTicks();

   @Accessor("lastReportedYaw")
   float getLastReportedYaw();

   @Accessor("lastReportedPitch")
   float getLastReportedPitch();
}
