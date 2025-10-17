package Myaven.setting.settings;

import Myaven.setting.Setting;

public class BooleanSetting extends Setting {
   private boolean state;
   private static String m;

   public BooleanSetting(String name, boolean state) {
      this.name = name;
      this.state = state;
   }

   private boolean getStateInternal() {
      return this.state;
   }

   public void toggle() {
      this.setState(!this.getStateInternal());
   }

   public boolean getState() {
      return this.state;
   }

   public void setState(boolean state) {
      this.state = state;
   }
}
