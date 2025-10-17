package Myaven.module.modules.combat;

import Myaven.events.AttackEvent;
import Myaven.events.KnockbackEvent;
import Myaven.events.PacketReceiveEvent;
import Myaven.module.Category;
import Myaven.module.Module;
import Myaven.setting.Setting;
import Myaven.setting.settings.BooleanSetting;
import Myaven.setting.settings.DescriptionSetting;
import Myaven.setting.settings.ModeSetting;
import Myaven.setting.settings.PercentageSetting;
import Myaven.setting.settings.SliderSetting;
import Myaven.util.ClientUtil;
import Myaven.util.PacketUtil;
import Myaven.util.PlayerUtil;
import Myaven.util.RaytraceUtil;
import Myaven.util.TargetUtil;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.Packet;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.network.play.client.C02PacketUseEntity.Action;
import net.minecraft.network.play.server.S12PacketEntityVelocity;
import net.minecraft.network.play.server.S32PacketConfirmTransaction;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import org.jetbrains.annotations.NotNull;

public class Velocity extends Module {
   public static DescriptionSetting modeDescription;
   public static ModeSetting mode;
   public static PercentageSetting horizontalPercent;
   public static PercentageSetting verticalPercent;
   public static BooleanSetting requireMoving;
   public static SliderSetting minDelayTicks;
   public static SliderSetting maxDelayTicks;
   public static PercentageSetting activationChance;
   private boolean shouldCancelAttack = false;
   private static CopyOnWriteArrayList<Packet<INetHandlerPlayClient>> queuedPackets;
   private static long delayStartTime;
   private static boolean delayActive;

   public Velocity() {
      super("Velocity", false, Category.Combat, true, "Modify the velocity received");
      this.addSettings(new Setting[]{modeDescription, mode, horizontalPercent, verticalPercent, requireMoving, minDelayTicks, maxDelayTicks, activationChance});
   }

   @Override
   public String getSuffix() {
      String var2 = mode.getCurrent();
      switch (var2) {
         case "VANILLA":
            if (horizontalPercent.getPercentage() != verticalPercent.getPercentage()) {
               return Math.round((float)horizontalPercent.getPercentage()) + "% " + Math.round((float)verticalPercent.getPercentage()) + "%";
            }

            return Math.round((float)horizontalPercent.getPercentage()) + "%";
         case "REVERSE":
            return "§m" + Math.round((float)horizontalPercent.getPercentage()) + "%§r " + Math.round((float)verticalPercent.getPercentage()) + "%";
         default:
            return mode.getCurrent();
      }
   }

   @SubscribeEvent
   public void onKnockback(KnockbackEvent event) {
      
      if (requireMoving.getState() && PlayerUtil.isMoving() || !requireMoving.getState()) {
         String var3 = mode.getCurrent();
         switch (var3) {
            case "VANILLA":
               if (ClientUtil.getRandomDoubleInMillis(0.0, 99.0) < (long)activationChance.getPercentage() || activationChance.getPercentage() == 100) {
                  if (horizontalPercent.getPercentage() != 100) {
                     event.setMotionX(event.getMotionX() * (float)horizontalPercent.getPercentage() / 100.0F);
                     event.setMotionZ(event.getMotionZ() * (float)horizontalPercent.getPercentage() / 100.0F);
                  }

                  if (verticalPercent.getPercentage() != 100) {
                     event.setMotionY(event.getMotionY() * (float)horizontalPercent.getPercentage() / 100.0F);
                  }
               }
               break;
            case "REVERSE":
               if (!TargetUtil.filterEntities(TargetUtil.getLivingEntitiesInRange(5.0), true, false, false, true, true).isEmpty()
                  && !TargetUtil.isInFov(
                     (Entity)TargetUtil.filterEntities(TargetUtil.getLivingEntitiesInRange(5.0), true, false, false, true, true).get(0), 180.0
                  )
                  && (ClientUtil.getRandomDoubleInMillis(0.0, 99.0) < (long)activationChance.getPercentage() || activationChance.getPercentage() == 100)) {
                  event.setMotionX(-(event.getMotionX() * (float)horizontalPercent.getPercentage() / 100.0F));
                  event.setMotionZ(-(event.getMotionZ() * (float)horizontalPercent.getPercentage() / 100.0F));
               }
               break;
            case "REDUCE":
               if ((
                     !KillAura.isAutoBlockActive
                        ? !this.mc.thePlayer.isUsingItem() && System.currentTimeMillis() - KillAura.lastStopUseTime >= 10L
                        : !KillAura.isBlocking
                  )
                  && PlayerUtil.isMoving()) {
                  List<EntityLivingBase> raycastEntity = RaytraceUtil.getEntitiesInRay(3.0);
                  if (!raycastEntity.isEmpty()
                     && raycastEntity.get(0) instanceof EntityPlayer
                     && (ClientUtil.getRandomDoubleInMillis(0.0, 99.0) < (long)activationChance.getPercentage() || activationChance.getPercentage() == 100)) {
                     this.mc.thePlayer.swingItem();
                     PacketUtil.sendPacket(new C02PacketUseEntity((Entity)raycastEntity.get(0), Action.ATTACK));
                     event.setMotionX(event.getMotionX() * 0.6F);
                     event.setMotionZ(event.getMotionZ() * 0.6F);
                     this.mc.thePlayer.setSprinting(false);
                     this.shouldCancelAttack = true;
                  }
               }
         }
      }
   }

   @SubscribeEvent
   public void onAttack(AttackEvent evt) {
      if (this.mc.thePlayer.hurtTime > 0 && this.shouldCancelAttack) {
         evt.setCanceled(true);
         this.shouldCancelAttack = false;
      }
   }

   @Override
   public void onDisable() {
      flushDelayedPackets();
   }

   @SubscribeEvent
   public void onTick(ClientTickEvent event) {
      if (ClientUtil.isWorldLoaded()) {
         if (delayActive
            && System.currentTimeMillis() - delayStartTime
               >= ClientUtil.getRandomDoubleInMillis(minDelayTicks.getRoundedValue(), maxDelayTicks.getRoundedValue()) * 50L) {
            flushDelayedPackets();
         }
      }
   }

   @SubscribeEvent
   public void onPacket(@NotNull PacketReceiveEvent event) {
      if (ClientUtil.isWorldLoaded()) {
         if (mode.getCurrent().equalsIgnoreCase("DELAY") && !ClientUtil.isLobby()) {
            if (event.getPacket() instanceof S12PacketEntityVelocity) {
               if (((S12PacketEntityVelocity)event.getPacket()).getEntityID() != this.mc.thePlayer.getEntityId()) {
                  return;
               }

               if (delayStartTime == -1L
                  && (requireMoving.getState() && PlayerUtil.isMoving() || !requireMoving.getState())
                  && (ClientUtil.getRandomDoubleInMillis(0.0, 99.0) < (long)activationChance.getPercentage() || activationChance.getPercentage() == 100)) {
                  event.setCanceled(true);
                  queuedPackets.add(event.getPacket());
                  delayStartTime = System.currentTimeMillis();
                  delayActive = true;
               }
            } else if (event.getPacket() instanceof S32PacketConfirmTransaction && delayActive) {
               event.setCanceled(true);
               queuedPackets.add(event.getPacket());
            }
         }
      }
   }

   public static void flushDelayedPackets() {

      for (Packet<INetHandlerPlayClient> p : queuedPackets) {
         PacketUtil.receivePacketNoEvent(p);
         queuedPackets.remove(p);
      }

      delayActive = false;
      queuedPackets.clear();
      delayStartTime = -1L;
   }

   // $VF: Irreducible bytecode was duplicated to produce valid code
   static {
      modeDescription = new DescriptionSetting("VANILLA, REVERSE, REDUCE, DELAY");
      mode = new ModeSetting("Mode", "VANILLA", "REVERSE", "REDUCE", "DELAY");
      horizontalPercent = new PercentageSetting("Horizontal", 0);
      verticalPercent = new PercentageSetting("Vertical", 100);
      requireMoving = new BooleanSetting("Require-moving", false);
      minDelayTicks = new SliderSetting("Min-delay-ticks", 1.0, 0.0, 10.0, 0.1);
      maxDelayTicks = new SliderSetting("Max-delay-ticks", 2.0, 0.0, 10.0, 0.1);
      activationChance = new PercentageSetting("Chance", 100);
      queuedPackets = new CopyOnWriteArrayList<>();
      delayStartTime = -1;
      delayActive = false;
   }
}
