package Myaven.setting.settings;

import Myaven.module.Module;
import Myaven.module.modules.config.Language;
import Myaven.setting.Setting;
import Myaven.util.RotationUtil;

public class DescriptionSetting extends Setting {
   public String showingText;

   public DescriptionSetting(String showingText) {
      this.showingText = showingText;
   }

   public String getText(Module module) {
      if (!Language.applyForSettings.getState()) {
         return this.showingText;
      } else {
         int clamp = (int)RotationUtil.clampDouble((double)(module.getSettingIndex(this) + 1), 1.0, (double)module.getSettings().size());
         return Language.translate("setting." + module.getName() + "." + clamp);
      }
   }

   public String q() {
      return this.showingText;
   }

   public void V(String showingText) {
      this.showingText = showingText;
   }
}
