package Myaven.module;

import Myaven.config.ConfigManager;
import Myaven.module.modules.config.Language;
import Myaven.module.modules.config.Notifications;
import Myaven.setting.Setting;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraftforge.common.MinecraftForge;

public class Module {
   private Category category;
   public Minecraft mc = Minecraft.getMinecraft();
   private String name;
   private boolean enabled;
   private boolean visible;
   private String description;
   private int key;
   private List<Setting> settings = new ArrayList<>();
   protected boolean canBeEnabled = true;
   protected boolean isConfiguration = false;

   public Module(String name, Boolean enabled, Category category, Boolean visible, String description) {
      this.name = name;
      this.enabled = enabled;
      this.category = category;
      this.visible = visible;
      this.description = description;
   }

   public Module(String name, Boolean enabled, Category category, Boolean visible, String description, boolean canBeEnabled, boolean isConfiguration) {
      this.name = name;
      this.enabled = enabled;
      this.category = category;
      this.visible = visible;
      this.description = description;
      this.canBeEnabled = canBeEnabled;
      this.isConfiguration = isConfiguration;
   }

   public int getSettingIndex(Setting setting) {
      return this.settings.indexOf(setting);
   }

   public String getText() {
      return Language.translateModuleName(this.name);
   }

   public String l(boolean arraylist) {
      return !Language.applyForArrayList.getState() ? this.getName() : Language.translateModuleName(this.name);
   }

   public String T() {
      return !Language.applyForDescriptions.getState() ? this.setDescription() : Language.getModuleDescription(this.name);
   }

   public void toggle() {
      if (this.canBeEnabled) {
         if (!this.isConfiguration) {
            if (this.enabled) {
               Notifications.sendNotification("§l" + this.getName() + " §r§l(§c§lOFF§r§l)", false);
               this.enabled = false;
               MinecraftForge.EVENT_BUS.unregister(this);
               onDisable();
            } else {
               Notifications.sendNotification("§l" + this.getName() + " §r§l(§a§lON§r§l)", true);
               this.enabled = true;
               MinecraftForge.EVENT_BUS.register(this);
               onEnable();
            }

            ConfigManager.save();
         }
      }
   }

   public String getSuffix() {
      return null;
   }

   protected void addSettings(Setting... setting) {
      this.settings.addAll(Arrays.asList(setting));
   }

   public List<Setting> getSettings() {
      return this.settings;
   }

   public void onEnable() {
   }

   public void onDisable() {
   }

   public void Y(boolean visible) {
      if (this.isConfiguration) {
         this.visible = false;
      } else {
         this.visible = visible;
      }
   }

   
   public Category getCategory() {
      return this.category;
   }

   
   public String getName() {
      return this.name;
   }

   
   public boolean isEnabled() {
      return this.enabled;
   }

   
   public void setEnabled(boolean enabled) {
      this.enabled = enabled;
   }

   
   public boolean setVisible() {
      return this.visible;
   }

   
   public String setDescription() {
      return this.description;
   }

   
   public int getKey() {
      return this.key;
   }

   
   public void setKey(int bind) {
      this.key = bind;
   }

   
   public List<Setting> getSettings2() {
      return this.settings;
   }

   
   public boolean canBeEnabled() {
      return this.canBeEnabled;
   }

   
   public boolean isConfig() {
      return this.isConfiguration;
   }
}
