package Myaven.setting;

import Myaven.module.Module;
import Myaven.module.modules.config.Language;
import Myaven.util.RotationUtil;


public class Setting {
   public String name;

   public String getDisplayName(Module module) {
      if (!Language.applyForSettings.getState()) {
         return this.name;
      } else {
         int clamp = (int)RotationUtil.clampDouble((double)(module.getSettingIndex(this) + 1), 1.0, (double)module.getSettings().size());
         return Language.translate("setting." + module.getName() + "." + clamp);
      }
   }

   
   public String getName() {
      return this.name;
   }

   
   public void setName(String name) {
      this.name = name;
   }
}
