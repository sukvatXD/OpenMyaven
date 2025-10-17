package Myaven.events;


import net.minecraftforge.fml.common.eventhandler.Event;

public class Render3DEvent extends Event {
   private float partialTicks;

   public Render3DEvent(float partialTicks) {
      this.partialTicks = partialTicks;
   }

   
   public float getPartialTicks() {
      return this.partialTicks;
   }

   
   public void setPartialTicks(float partialTicks) {
      this.partialTicks = partialTicks;
   }
}
