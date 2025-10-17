package Myaven.mixins.network;

import Myaven.events.PacketReceiveEvent;
import Myaven.events.PacketSendEvent;
import Myaven.util.PacketUtil;
import io.netty.channel.ChannelHandlerContext;
import net.minecraft.network.INetHandler;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraftforge.common.MinecraftForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({NetworkManager.class})
public class MixinNetworkManager {
   @Shadow
   private INetHandler packetListener;

   @Inject(
      method = {"sendPacket(Lnet/minecraft/network/Packet;)V"},
      at = {@At("HEAD")},
      cancellable = true
   )
   public void Y(Packet<?> packet, CallbackInfo ci) {
      if (packet != null) {
         if (PacketUtil.cancelIncoming) {
            PacketUtil.noEventSend.clear();
         } else if (PacketUtil.noEventSend.contains(packet)) {
            PacketUtil.noEventSend.remove(packet);
            return;
         }
      }

      PacketSendEvent event = new PacketSendEvent(packet);
      MinecraftForge.EVENT_BUS.post(event);
      if (event.isCanceled()) {
         ci.cancel();
      }
   }

   @Inject(
      method = {"channelRead0(Lio/netty/channel/ChannelHandlerContext;Lnet/minecraft/network/Packet;)V"},
      at = {@At("HEAD")},
      cancellable = true
   )
   public void W(ChannelHandlerContext p_channelRead0_1_, Packet<?> packet, CallbackInfo ci) {
      if (packet != null && PacketUtil.noEventReceive.contains(packet)) {
         PacketUtil.noEventReceive.remove(packet);
      } else {
         PacketReceiveEvent event = new PacketReceiveEvent((Packet<INetHandlerPlayClient>)packet);
         MinecraftForge.EVENT_BUS.post(event);
         if (event.isCanceled()) {
            ci.cancel();
         }
      }
   }

   @Redirect(
      method = {"channelRead0(Lio/netty/channel/ChannelHandlerContext;Lnet/minecraft/network/Packet;)V"},
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/network/Packet;processPacket(Lnet/minecraft/network/INetHandler;)V"
      )
   )
   public void h(Packet instance, INetHandler handler) {
      instance.processPacket(this.packetListener);
   }

   private static RuntimeException a(RuntimeException runtimeException) {
      return runtimeException;
   }
}
