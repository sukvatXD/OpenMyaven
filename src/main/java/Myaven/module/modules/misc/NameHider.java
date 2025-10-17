package Myaven.module.modules.misc;

import Myaven.module.Category;
import Myaven.module.Module;
import Myaven.setting.Setting;
import Myaven.setting.settings.TextSetting;
import Myaven.util.ClientUtil;
import net.minecraft.client.Minecraft;

public class NameHider extends Module {
   public static TextSetting customName;

   public NameHider() {
      super("NameHider", false, Category.Misc, false, "Replace all string that matches your name");
      this.addSettings(customName);
   }

   public static String replaceName(String input) {
      Minecraft mc = Minecraft.getMinecraft();
      return ClientUtil.isWorldLoaded() && mc.thePlayer.getName() != null && customName.getCustomText() != null && input != null
         ? input.replace(mc.thePlayer.getName(), customName.getCustomText())
         : input;
   }

   // $VF: Irreducible bytecode was duplicated to produce valid code
   static {
      customName = new TextSetting("Name", "Myaven User");
   }
}
