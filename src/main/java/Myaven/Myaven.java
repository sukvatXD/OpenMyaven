package Myaven;

import Myaven.command.CommandManager;
import Myaven.hooks.MouseHelperHook;
import Myaven.hooks.MovementInputHook;
import Myaven.management.RotationManager;
import Myaven.module.Module;
import Myaven.module.ModuleManager;
import Myaven.ui.ClickGUI;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import net.minecraft.client.Minecraft;
import net.minecraft.util.MouseHelper;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import org.lwjgl.input.Keyboard;

public class Myaven {
   public static ModuleManager moduleManager;
   private static ScheduledExecutorService r;
   public static String Q;
   public static RotationManager a;
   public static CommandManager commandManager;
   public static MovementInputHook movementHook;
   public static MouseHelper mouseHelper;
   public static String username;
   public static ClickGUI clickgui;
   public static Minecraft mc;

   private void I(){
      r = Executors.newScheduledThreadPool(2);
      mc = Minecraft.getMinecraft();
      username = "60124808866";
      moduleManager = new ModuleManager();
      moduleManager.init();
      commandManager =  new CommandManager();
      mouseHelper = new MouseHelperHook();
      movementHook = new MovementInputHook();
      clickgui = new ClickGUI();
      clickgui.Q();
      MinecraftForge.EVENT_BUS.register(this);
   }

   @SubscribeEvent
   public void keyInput(InputEvent.KeyInputEvent event) {
      for(Module m : moduleManager.F()) {
         if(m.getKey() > 0){
            if(Keyboard.isKeyDown(m.getKey())) {
               m.toggle();
            }
         }
         if(Keyboard.isKeyDown(54)){
            mc.displayGuiScreen(clickgui);
         }
      }
   }

   public static ScheduledExecutorService a() {
      return r;
   }

   public Myaven() {
      this.I();
   }
}
