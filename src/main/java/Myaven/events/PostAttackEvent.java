package Myaven.events;


import net.minecraft.entity.Entity;
import net.minecraftforge.fml.common.eventhandler.Cancelable;
import net.minecraftforge.fml.common.eventhandler.Event;

@Cancelable
public class PostAttackEvent extends Event {
   private Entity target;

   public PostAttackEvent(Entity entity) {
      this.target = entity;
   }

   
   public Entity getTarget() {
      return this.target;
   }
}
