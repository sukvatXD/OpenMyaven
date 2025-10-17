package Myaven.util;

import Myaven.Myaven;
import Myaven.events.PacketSendEvent;
import java.util.concurrent.CopyOnWriteArrayList;
import net.minecraft.network.INetHandler;
import net.minecraft.network.Packet;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.network.play.INetHandlerPlayServer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class PacketUtil {
   public static CopyOnWriteArrayList<Packet<INetHandlerPlayServer>> noEventSend = new CopyOnWriteArrayList<>();
   public static CopyOnWriteArrayList<Packet<INetHandlerPlayClient>> noEventReceive = new CopyOnWriteArrayList<>();
   public static boolean cancelOutgoing = false;
   public static boolean cancelIncoming = false;
   public static CopyOnWriteArrayList<Packet> pendingPackets = new CopyOnWriteArrayList<>();

   public PacketUtil() {
      MinecraftForge.EVENT_BUS.register(this);
   }

   public static void sendPacket(Packet<?> packet) {
      if (packet != null) {
         Packet<INetHandlerPlayServer> casted = castPacket(packet);
         Myaven.mc.thePlayer.sendQueue.addToSendQueue(casted);
      }
   }

   public static void sendPacketNoEvent(Packet<?> packet) {
      if (packet != null) {
         Packet<INetHandlerPlayServer> casted = castPacket(packet);
         noEventSend.add(casted);
         Myaven.mc.thePlayer.sendQueue.addToSendQueue(casted);
      }
   }

   public static void receivePacketNoEvent(Packet<?> packet) {
      if (packet != null) {
         Packet<INetHandlerPlayClient> casted = castPacket(packet);
         noEventReceive.add(casted);
         casted.processPacket(Myaven.mc.getNetHandler());
      }
   }

   public static void receivePacket(Packet<?> packet) {
      if (packet != null) {
         Packet<INetHandlerPlayClient> casted = castPacket(packet);
         casted.processPacket(Myaven.mc.getNetHandler());
      }
   }

   @SubscribeEvent
   public void onPacketSend(PacketSendEvent event) {
      if (!Myaven.mc.isSingleplayer()) {
         if (cancelOutgoing || cancelIncoming) {
            pendingPackets.add(event.getPacket());
            event.setCanceled(true);
         }
      }
   }

   public static void flushPendingPackets() {
      if (!Myaven.mc.isSingleplayer()) {
         if (!cancelIncoming) {
            for (Packet<?> packet : pendingPackets) {
               sendPacketNoEvent(packet);
               pendingPackets.remove(packet);
            }
         }
      }
   }

   public static void setCancelOutgoing(boolean set) {
      cancelOutgoing = set;
   }

   public static void setCancelIncoming(boolean set) {
      cancelIncoming = set;
   }

   public static void flushOnTick() {
      if (!Myaven.mc.isSingleplayer()) {
         setCancelIncoming(false);

         for (Packet<?> packet : pendingPackets) {
            sendPacketNoEvent(packet);
            pendingPackets.remove(packet);
         }
      }
   }

   public static <H extends INetHandler> Packet<H> castPacket(Packet<?> packet) {
      return (Packet<H>)packet;
   }

   private static RuntimeException a(RuntimeException runtimeException) {
      return runtimeException;
   }
}
