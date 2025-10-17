package Myaven.events;


import net.minecraftforge.fml.common.eventhandler.Cancelable;
import net.minecraftforge.fml.common.eventhandler.Event;

@Cancelable
public class SlowdownEvent extends Event {
   float speed;

   public SlowdownEvent(float speed) {
      this.speed = speed;
   }

   
   public float getSpeed() {
      return this.speed;
   }

   
   public void setSpeed(float speed) {
      this.speed = speed;
   }
}
