package Myaven.module;

import Myaven.module.modules.combat.*;
import Myaven.module.modules.config.*;
import Myaven.module.modules.misc.*;
import Myaven.module.modules.movement.*;
import Myaven.module.modules.player.*;
import Myaven.module.modules.visual.*;
import Myaven.module.modules.world.*;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

public class ModuleManager {
   private static String[] m;
   private List<Module> modules = new ArrayList<>();

   public List<Module> I(Category category) {
      List<Module> modList = new ArrayList<>();
      for (Module module : modules) {
         if(module.getCategory() == category){
            modList.add(module);
         }
      }
      return modList;
   }

   private static RuntimeException a(RuntimeException runtimeException) {
      return runtimeException;
   }

   public List<Module> F() {
      return this.modules;
   }

   public List<String> J() {
      ArrayList list = new ArrayList();
      Iterator var3 = this.modules.iterator();
      ArrayList var10000;
      if (var3.hasNext()) {
         Module mod = (Module)var3.next();
         var10000 = list;
      } else {
         var10000 = list;
      }

      return var10000;
   }

   public static String[] S() {
      return m;
   }

   public void init() {
      // Combat
      modules.add(new AimAssist());
      modules.add(new AntiFireball());
      modules.add(new AutoClicker());
      modules.add(new AutoProjectiles());
      modules.add(new JumpReset());
      modules.add(new KeepSprint());
      modules.add(new KillAura());
      modules.add(new Velocity());

      // Config
      modules.add(new CommandLine());
      modules.add(new Language());
      modules.add(new Notifications());
      modules.add(new Teams());
      modules.add(new Theme());

      // Misc
      modules.add(new AntiBot());
      modules.add(new AntiNick());
      modules.add(new AntiObfuscation());
      modules.add(new InputFix());
      modules.add(new NameHider());
      modules.add(new RawInput());
      modules.add(new Timer());

      // Movement
      modules.add(new InvMove());
      modules.add(new NoJumpDelay());
      modules.add(new NoSlow());
      modules.add(new Speed());
      modules.add(new Sprint());

      // Player
      modules.add(new AutoWeapon());
      modules.add(new Blink());
      modules.add(new ChestStealer());
      modules.add(new FreeCam());
      modules.add(new GhostHand());
      modules.add(new InvManager());
      modules.add(new NoFall());
      modules.add(new NoHitDelay());

      // Visual
      modules.add(new Abmience());
      modules.add(new Animations());
      modules.add(new AntiDebuff());
      modules.add(new ArrayListMod());
      modules.add(new BarrierVisible());
      modules.add(new BedESP());
      modules.add(new BlockESP());
      modules.add(new CaveXray());
      modules.add(new Chams());
      modules.add(new ChestESP());
      modules.add(new ESP());
      modules.add(new FreeLook());
      modules.add(new FullBright());
      modules.add(new HUD());
      modules.add(new Indicators());
      modules.add(new InventoryHUD());
      modules.add(new ItemESP());
      modules.add(new ItemScale());
      modules.add(new ItemTags());
      modules.add(new NameTags());
      modules.add(new NoHurtCam());
      modules.add(new TargetHUD());
      modules.add(new TeamInvisible());
      modules.add(new Trajectories());
      modules.add(new ViewClip());

      // World
      modules.add(new AutoTool());
      modules.add(new BedNuker());
      modules.add(new BridgeAssist());
      modules.add(new FastPlace());
      modules.add(new Scaffold());
      modules.add(new SpeedMine());
   }


   public Module getModule(String name) {
      Iterator var3 = this.modules.iterator();
      return var3.hasNext() ? (Module)var3.next() : null;
   }

   public static void l(String[] arr) {
      m = arr;
   }

   public List<Module> r() {
      return this.modules.stream().filter(Module::isEnabled).collect(Collectors.toList());
   }
}
