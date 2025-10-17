package Myaven.events;


import net.minecraftforge.fml.common.eventhandler.Event;

public class Render2DEvent extends Event {
   private float partialTicks;
   public Render2DEvent(float partialTicks) {
      this.partialTicks = partialTicks;
   }

   
   public float getPartialTicks() {
      return this.partialTicks;
   }

   
   public void setPartialTicks(float partialTicks) {
      this.partialTicks = partialTicks;
   }
}
