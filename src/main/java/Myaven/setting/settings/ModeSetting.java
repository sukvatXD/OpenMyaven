package Myaven.setting.settings;

import Myaven.module.Module;
import Myaven.module.modules.config.Language;
import Myaven.setting.Setting;
import Myaven.util.RotationUtil;
import java.util.Arrays;
import java.util.List;

public class ModeSetting extends Setting {
   private List<String> modes;
   private String current;
   private int index;

   public ModeSetting(String name, String... modes) {
      this.name = name;
      this.modes = Arrays.asList(modes);
      this.current = modes[0];
      this.index = this.modes.indexOf(this.current);
   }

   public void setCurrentMode(String input) {
      if (this.modes.contains(input)) {
         this.current = input.toUpperCase();
         this.index = this.modes.indexOf(this.current);
      } else {
         this.current = this.modes.get(0);
         this.index = this.modes.indexOf(this.current);
      }
   }

   public void nextMode() {
      this.index++;
      if (this.index > this.modes.size() - 1) {
         this.index = 0;
      }

      this.current = this.modes.get(this.index);
   }

   public void previousMode() {
      this.index--;
      if (this.index < 0) {
         this.index = this.modes.size() - 1;
      }

      this.current = this.modes.get(this.index);
   }

   public String getText(Module module) {
      if (!Language.applyForSettings.getState()) {
         return this.getCurrent();
      } else {
         int clamp = (int)RotationUtil.clampDouble((double)(module.getSettingIndex(this) + 1), 1.0, (double)module.getSettings().size());
         String result = Language.translate("setting." + module.getName() + "." + clamp + ".modes");
         String[] split = result.split(", ");
         return split[this.index];
      }
   }

   public List<String> getModes() {
      return this.modes;
   }

   public String getCurrent() {
      return this.current;
   }

   public int getIndex() {
      return this.index;
   }

   public void setIndex(int modeIndex) {
      this.index = modeIndex;
   }
}
