package Myaven.events;


import net.minecraft.network.Packet;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraftforge.fml.common.eventhandler.Cancelable;
import net.minecraftforge.fml.common.eventhandler.Event;

@Cancelable
public class PacketReceiveEvent extends Event {
   private Packet<INetHandlerPlayClient> packet;

   public PacketReceiveEvent(Packet<INetHandlerPlayClient> packet) {
      this.packet = packet;
   }

   
   public Packet<INetHandlerPlayClient> getPacket() {
      return this.packet;
   }
}
