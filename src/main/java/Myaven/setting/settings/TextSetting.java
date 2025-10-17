package Myaven.setting.settings;

import Myaven.setting.Setting;

public class TextSetting extends Setting {
   private String custom;

   public TextSetting(String name, String custom) {
      this.name = name;
      this.custom = custom;
   }

   public String getCustomText() {
      return this.custom;
   }
   public void setCustomText(String customText) {
      this.custom = customText;
   }
}
