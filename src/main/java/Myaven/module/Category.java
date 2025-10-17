package Myaven.module;

import Myaven.module.modules.config.Language;

public enum Category {
   Combat,
   Movement,
   Visual,
   Player,
   World,
   Misc,
   Configuration;

   public String b() {
      return Language.getCategoryName(this.name());
   }
}
