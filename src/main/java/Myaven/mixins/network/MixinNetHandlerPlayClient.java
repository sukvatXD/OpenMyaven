package Myaven.mixins.network;

import Myaven.Myaven;
import Myaven.events.KnockbackEvent;
import Myaven.events.PostKnockbackEvent;
import com.mojang.authlib.GameProfile;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.entity.Entity;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.PacketThreadUtil;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.network.play.server.S12PacketEntityVelocity;
import net.minecraftforge.common.MinecraftForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({NetHandlerPlayClient.class})
public abstract class MixinNetHandlerPlayClient implements INetHandlerPlayClient {
   @Shadow
   private NetworkManager netManager;
   @Shadow
   private GameProfile profile;
   @Shadow
   private GuiScreen guiScreenServer;
   @Shadow
   private Minecraft gameController;
   @Shadow
   private WorldClient clientWorldController;

   public MixinNetHandlerPlayClient(Minecraft mcIn, GuiScreen p_i46300_2_, NetworkManager p_i46300_3_, GameProfile p_i46300_4_) {
      this.gameController = mcIn;
      this.guiScreenServer = p_i46300_2_;
      this.netManager = p_i46300_3_;
      this.profile = p_i46300_4_;
   }

   @Inject(
      method = {"handleEntityVelocity"},
      at = {@At("HEAD")},
      cancellable = true
   )
   public void s(S12PacketEntityVelocity packetIn, CallbackInfo ci) {
      PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.gameController);
      Entity entity = this.clientWorldController.getEntityByID(packetIn.getEntityID());
      if (entity != null) {
         if (entity.getEntityId() == Myaven.mc.thePlayer.getEntityId()) {
            KnockbackEvent event = new KnockbackEvent((float)packetIn.getMotionX(), (float)packetIn.getMotionY(), (float)packetIn.getMotionZ());
            MinecraftForge.EVENT_BUS.post(event);
            if (event.isCanceled()) {
               return;
            }

            entity.setVelocity((double)event.getMotionX() / 8000.0, (double)event.getMotionY() / 8000.0, (double)event.getMotionZ() / 8000.0);
         } else {
            entity.setVelocity((double)packetIn.getMotionX() / 8000.0, (double)packetIn.getMotionY() / 8000.0, (double)packetIn.getMotionZ() / 8000.0);
         }
      }

      ci.cancel();
   }

   @Inject(
      method = {"handleEntityVelocity"},
      at = {@At("RETURN")},
      cancellable = true
   )
   public void H(S12PacketEntityVelocity packetIn, CallbackInfo ci) {
      if (packetIn.getEntityID() == Myaven.mc.thePlayer.getEntityId()) {
         MinecraftForge.EVENT_BUS.post(new PostKnockbackEvent());
      }
   }

   private static RuntimeException a(RuntimeException runtimeException) {
      return runtimeException;
   }
}
