package Myaven.module.modules.combat;

import Myaven.events.PlayerUpdateEvent;
import Myaven.events.PostMotionEvent;
import Myaven.module.Category;
import Myaven.module.Module;
import Myaven.setting.Setting;
import Myaven.setting.settings.BooleanSetting;
import Myaven.setting.settings.PercentageSetting;
import Myaven.setting.settings.SliderSetting;
import Myaven.util.ClientUtil;
import Myaven.util.PlayerUtil;
import Myaven.util.TargetUtil;
import java.util.Iterator;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.input.Keyboard;

public class JumpReset extends Module {
   public static PercentageSetting chance;
   public static SliderSetting fov;
   public static SliderSetting range;
   public static BooleanSetting requireMoving;
   private boolean isJumping;
   private boolean isTargetInRange;
   private boolean wasOnGround;
   private int lastHurtTime;
   private double fallDistance;
   private static String[] b;
   private static String[] g;

   public JumpReset() {
      super("JumpReset", false, Category.Combat, true, "JumpReset and reduce knockback in combat");
      this.addSettings(new Setting[]{chance, fov, range, requireMoving});
   }

   @Override
   public String getSuffix() {
      return chance.getPercentage() + "%";
   }

   @SubscribeEvent
   public void onPlayerUpdate(PlayerUpdateEvent event) {
      int hurtTime = this.mc.thePlayer.hurtTime;
      boolean onGround = this.mc.thePlayer.onGround;
      if (onGround && this.fallDistance > 3.0 && !this.mc.thePlayer.capabilities.allowFlying) {
         this.wasOnGround = true;
      }

      if (hurtTime > this.lastHurtTime) {
         this.isTargetInRange = false;

         for (EntityLivingBase entity : TargetUtil.getLivingEntitiesInRange(range.getRoundedValue())) {
            if (TargetUtil.isInFov(entity, fov.getRoundedValue()) && TargetUtil.isInRange(entity, range.getRoundedValue())) {
               this.isTargetInRange = true;
            }
         }

         boolean aimingAt = this.isTargetInRange;
         boolean forward = PlayerUtil.isMoving() || !requireMoving.getState();
         if (!this.wasOnGround
            && !this.mc.thePlayer.isBurning()
            && onGround
            && aimingAt
            && forward
            && ClientUtil.getRandomDoubleInMillis(0.0, 100.0) < (long)chance.getPercentage()
            && !this.checkPotionEffects()) {
            KeyBinding.setKeyBindState(this.mc.gameSettings.keyBindJump.getKeyCode(), this.isJumping = true);
            KeyBinding.onTick(this.mc.gameSettings.keyBindJump.getKeyCode());
         }

         this.wasOnGround = false;
      }

      this.lastHurtTime = hurtTime;
      this.fallDistance = (double)this.mc.thePlayer.fallDistance;
   }

   @SubscribeEvent
   public void onPostMotion(PostMotionEvent e) {
      if (this.isJumping) {
         KeyBinding.setKeyBindState(this.mc.gameSettings.keyBindJump.getKeyCode(), this.isJumping = false);
         if (Keyboard.isKeyDown(this.mc.gameSettings.keyBindJump.getKeyCode())) {
            KeyBinding.setKeyBindState(this.mc.gameSettings.keyBindJump.getKeyCode(), true);
         }
      }
   }

   private boolean checkPotionEffects() {
      Iterator var2 = this.mc.thePlayer.getActivePotionEffects().iterator();
      if (!var2.hasNext()) {
         return false;
      } else {
         PotionEffect potionEffect = (PotionEffect)var2.next();
         int id = potionEffect.getPotionID();
         return id == Potion.jump.getId() || id == Potion.poison.getId() || id == Potion.wither.getId();
      }
   }

   // $VF: Irreducible bytecode was duplicated to produce valid code
   static {
      chance = new PercentageSetting("Chance", 100);
      fov = new SliderSetting("FOV", 90.0, 0.0, 360.0, 1.0);
      range = new SliderSetting("Range", 5.0, 0.0, 10.0, 0.1);
      requireMoving = new BooleanSetting("Require-moving", true);
   }
}
