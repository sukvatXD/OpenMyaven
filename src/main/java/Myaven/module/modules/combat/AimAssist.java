package Myaven.module.modules.combat;

import Myaven.module.Category;
import Myaven.module.Module;
import Myaven.setting.Setting;
import Myaven.setting.settings.BooleanSetting;
import Myaven.setting.settings.DescriptionSetting;
import Myaven.setting.settings.ModeSetting;
import Myaven.setting.settings.SliderSetting;
import Myaven.util.ClientUtil;
import Myaven.util.RaytraceUtil;
import Myaven.util.RotationUtil;
import Myaven.util.TargetUtil;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemSword;
import net.minecraft.util.Vec3;
import net.minecraft.util.MovingObjectPosition.MovingObjectType;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import org.lwjgl.input.Mouse;

public class AimAssist extends Module {
   public static DescriptionSetting sortDescription1;
   public static DescriptionSetting sortDescription2;
   public static ModeSetting sort;
   public static SliderSetting range;
   public static SliderSetting fov;
   public static SliderSetting speed;
   public static BooleanSetting breakBlocks;
   public static BooleanSetting swordOnly;
   public static BooleanSetting teamCheck;

   public AimAssist() {
      super("AimAssist", false, Category.Combat, true, "Help you aim BETTER when you click");
      this.addSettings(sortDescription1, sortDescription2, sort, range, fov, speed, breakBlocks, swordOnly, teamCheck);
   }

   @SubscribeEvent
   public void on(ClientTickEvent event) {

      if (ClientUtil.isWorldLoaded()) {
         if (event.phase == Phase.END) {
            if (this.mc.currentScreen == null) {
               if (Mouse.isButtonDown(0)) {
                  if (!breakBlocks.getState() || this.mc.objectMouseOver.typeOfHit != MovingObjectType.BLOCK) {
                     if (!swordOnly.getState() || this.mc.thePlayer.getHeldItem() != null && this.mc.thePlayer.getHeldItem().getItem() instanceof ItemSword) {
                        EntityLivingBase target = this.findTarget();
                        if (target != null) {
                           if (TargetUtil.isInFov(target, fov.getRoundedValue())) {
                              if ((int)target.posX != (int)this.mc.thePlayer.posX
                                 || (int)target.posZ != (int)this.mc.thePlayer.posZ
                                 || !RaytraceUtil.getEntitiesInRay(3.0).contains(target)) {
                                 Vec3 aimPoint = RotationUtil.getEntityHitVec(target);
                                 float targetYaw = RotationUtil.getRotationToVec(aimPoint)[0];
                                 float targetPitch = RotationUtil.getRotationToVec(aimPoint)[1];
                                 float stepYaw = (float)(
                                    (double)RotationUtil.getAngleDifference(this.mc.thePlayer.rotationYaw, targetYaw) / (20.0 / speed.getRoundedValue())
                                 );
                                 float stepPitch = (float)(
                                    (double)RotationUtil.getAngleDifference(this.mc.thePlayer.rotationPitch, targetPitch) / (20.0 / speed.getRoundedValue())
                                 );
                                 this.mc.thePlayer.rotationYaw += stepYaw;
                                 if (RaytraceUtil.raycastEntity(range.getRoundedValue()) == null
                                    || Objects.requireNonNull(RaytraceUtil.raycastEntity(range.getRoundedValue())).getUniqueID() != target.getUniqueID()) {
                                    this.mc.thePlayer.rotationPitch += stepPitch;
                                 }
                              }
                           }
                        }
                     }
                  }
               }
            }
         }
      }
   }

   private EntityLivingBase findTarget() {
      List<EntityLivingBase> entitiesWithinRange = TargetUtil.filterEntities(
         TargetUtil.getLivingEntitiesInRange(range.getRoundedValue()), true, false, false, teamCheck.getState(), false
      );
      if (entitiesWithinRange.isEmpty()) {
         return null;
      } else {
         String var3 = sort.getCurrent();
         switch (var3) {
            case "HEALTH":
               entitiesWithinRange.sort(Comparator.comparingDouble(EntityLivingBase::getHealth));
               break;
            case "DISTANCE":
               entitiesWithinRange.sort(Comparator.comparingDouble(this.mc.thePlayer::getDistanceToEntity));
               break;
            case "VIEW":
               entitiesWithinRange.sort(Comparator.comparingDouble(RotationUtil::getYawDifference));
               break;
            case "HURT_TIME":
               entitiesWithinRange.sort(Comparator.comparingInt(entity -> entity.hurtTime));
               break;
            case "ARMOR":
               entitiesWithinRange.sort(Comparator.comparingInt(EntityLivingBase::getTotalArmorValue));
         }

         return (EntityLivingBase)entitiesWithinRange.get(0);
      }
   }

   // $VF: Irreducible bytecode was duplicated to produce valid code
   static {
      sortDescription1 = new DescriptionSetting("DISTANCE, HEALTH, VIEW,");
      sortDescription2 = new DescriptionSetting("HURT_TIME, ARMOR");
      sort = new ModeSetting("Sort", "DISTANCE", "HEALTH", "VIEW", "HURT_TIME", "ARMOR");
      range = new SliderSetting("Range", 5.0, 0.1, 10.0, 0.1);
      fov = new SliderSetting("FOV", 160.0, 1.0, 360.0, 1.0);
      speed = new SliderSetting("Speed", 5.0, 1.0, 20.0, 0.1);
      breakBlocks = new BooleanSetting("Break-blocks", true);
      swordOnly = new BooleanSetting("Sword-only", true);
      teamCheck = new BooleanSetting("Team-check", true);
   }
}
