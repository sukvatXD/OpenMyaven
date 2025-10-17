package Myaven.module.modules.combat;

import Myaven.Myaven;
import Myaven.events.LivingUpdateEvent;
import Myaven.events.PlayerUpdateEvent;
import Myaven.management.RotationManager;
import Myaven.module.Category;
import Myaven.module.Module;
import Myaven.setting.Setting;
import Myaven.setting.settings.BooleanSetting;
import Myaven.setting.settings.DescriptionSetting;
import Myaven.setting.settings.ModeSetting;
import Myaven.setting.settings.SliderSetting;
import Myaven.util.ClientUtil;
import Myaven.util.PacketUtil;
import Myaven.util.RotationUtil;
import Myaven.util.TargetUtil;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.EntityFireball;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.network.play.client.C0APacketAnimation;
import net.minecraft.network.play.client.C02PacketUseEntity.Action;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class AntiFireball extends Module {
   public static SliderSetting range;
   public static SliderSetting fov;
   public static BooleanSetting swing;
   public static DescriptionSetting moveFixDescription;
   public static ModeSetting moveFix;
   private ArrayList<EntityFireball> fireballsInRange = new ArrayList<>();
   private ArrayList<EntityFireball> fireballsToAttack = new ArrayList<>();
   private EntityFireball targetFireball = null;
   private boolean N = false;

   public AntiFireball() {
      super("AntiFireball", false, Category.Combat, true, "Hit fireballs back");
      this.addSettings(new Setting[]{range, fov, moveFixDescription, moveFix});
   }

   private boolean isFireballInRange(EntityFireball entityFireball) {
      return TargetUtil.isInRange(entityFireball, range.getRoundedValue() + 3.0) && this.calculateAngle(entityFireball) <= (float)fov.getRoundedValue();
   }

   private float calculateAngle(Entity entity) {
      Vec3 eyePos = this.mc.thePlayer.getPositionEyes(ClientUtil.getRenderPartialTicks());
      float borderSize = entity.getCollisionBorderSize();
      AxisAlignedBB boundingBox = entity.getEntityBoundingBox().expand((double)borderSize, (double)borderSize, (double)borderSize);
      if (boundingBox.isVecInside(eyePos)) {
         return 0.0F;
      } else {
         double deltaX = entity.posX - eyePos.xCoord;
         double deltaZ = entity.posZ - eyePos.zCoord;
         return Math.abs(MathHelper.wrapAngleTo180_float((float)(Math.atan2(deltaZ, deltaX) * 180.0 / Math.PI) - 90.0F - this.mc.thePlayer.rotationYaw)) * 2.0F;
      }
   }

   private void swing() {
      if (swing.getState()) {
         this.mc.thePlayer.swingItem();
      } else {
         PacketUtil.sendPacket(new C0APacketAnimation());
      }
   }

   @SubscribeEvent
   public void on(LivingUpdateEvent event) {
      
      if (ClientUtil.isWorldLoaded()) {
         List<EntityFireball> fireballs = this.mc
            .theWorld
            .loadedEntityList
            .stream()
            .filter(entity -> entity instanceof EntityFireball)
            .map(entity -> (EntityFireball)entity)
            .collect(Collectors.toList());
         this.fireballsInRange.removeIf(entityFireball -> !fireballs.contains(entityFireball));
         this.fireballsToAttack.removeIf(entityFireball -> !fireballs.contains(entityFireball));

         for (EntityFireball fireball : fireballs) {
            if (!this.fireballsInRange.contains(fireball) && !this.fireballsToAttack.contains(fireball)) {
               if (TargetUtil.getDistanceToEntityHitbox(fireball) > 3.0) {
                  this.fireballsInRange.add(fireball);
               } else {
                  this.fireballsToAttack.add(fireball);
               }
            }
         }

         if (this.mc.thePlayer.capabilities.allowFlying) {
            this.targetFireball = null;
         } else {
            this.targetFireball = this.fireballsInRange
               .stream()
               .filter(this::isFireballInRange)
               .min(Comparator.comparingDouble(TargetUtil::getDistanceToEntityHitbox))
               .orElse(null);
         }
      }
   }

   @SubscribeEvent
   public void on(PlayerUpdateEvent event) {
      
      if (ClientUtil.isWorldLoaded()) {
         if (!Myaven.moduleManager.getModule("Scaffold").isEnabled() && !KillAura.combatTick) {
            String rotationsMode = moveFix.getCurrent();
            switch (rotationsMode) {
               case "SILENT":
                  RotationManager.setMovementMode(RotationManager.MovementMode.SILENT);
                  break;
               case "STRICT":
                  RotationManager.setMovementMode(RotationManager.MovementMode.STRICT);
                  break;
               case "NONE":
                  RotationManager.setMovementMode(RotationManager.MovementMode.NONE);
            }

            if (this.targetFireball != null) {
               if (this.targetFireball.getEntityBoundingBox() != null) {
                  float[] rotations = RotationUtil.getRotationToEntity(this.targetFireball);
                  RotationManager.setRotation(rotations[0], rotations[1]);
                  this.N = true;
                  this.swing();
                  if (TargetUtil.getDistanceToEntityHitbox(this.targetFireball) <= range.getRoundedValue()) {
                     PacketUtil.sendPacket(new C02PacketUseEntity(this.targetFireball, Action.ATTACK));
                     this.mc.playerController.attackEntity(this.mc.thePlayer, this.targetFireball);
                  }
               }
            }
         } else {
            this.C();
         }
      }
   }

   @Override
   public void onDisable() {
      this.fireballsInRange.clear();
      this.fireballsToAttack.clear();
      this.C();
   }

   private void C() {
      if (this.N) {
         RotationManager.syncRotationWithPlayer();
         this.N = false;
      }
   }

   @SubscribeEvent
   public void on(EntityJoinWorldEvent event) {
      if (ClientUtil.isWorldLoaded()) {
         if (event.entity == this.mc.thePlayer) {
            this.fireballsInRange.clear();
            this.fireballsToAttack.clear();
         }
      }
   }

   // $VF: Irreducible bytecode was duplicated to produce valid code
   static {
      range = new SliderSetting("Range", 5.0, 0.0, 10.0, 0.1);
      fov = new SliderSetting("FOV", 180.0, 0.0, 360.0, 1.0);
      swing = new BooleanSetting("Swing", true);
      moveFixDescription = new DescriptionSetting("SILENT, STRICT, NONE");
      moveFix = new ModeSetting("Move-fix", "SILENT", "STRICT", "NONE");
   }
}
