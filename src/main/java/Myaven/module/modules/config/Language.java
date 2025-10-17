package Myaven.module.modules.config;

import Myaven.module.Category;
import Myaven.module.Module;
import Myaven.setting.Setting;
import Myaven.setting.settings.BooleanSetting;
import Myaven.setting.settings.DescriptionSetting;
import Myaven.setting.settings.ModeSetting;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;
import java.util.ResourceBundle.Control;

public class Language extends Module {
   public static DescriptionSetting languagesDescriptionPart1;
   public static DescriptionSetting languagesDescriptionPart2;
   public static ModeSetting languageMode;
   public static DescriptionSetting clickGuiOptionsDescription;
   public static BooleanSetting applyForCategory;
   public static BooleanSetting applyForDescriptions;
   public static BooleanSetting applyForName;
   public static BooleanSetting applyForSettings;
   public static DescriptionSetting arrayListOptionsDescription;
   public static BooleanSetting applyForArrayList;
   private static Locale localeEnglishUS;
   private static Locale localeChineseSimplified;
   private static Locale localeChineseTraditional;
   public static ResourceBundle bundleEnglish;
   public static ResourceBundle bundleChineseSimplified;
   public static ResourceBundle bundleChineseTraditional;

   public Language() {
      super("Language", true, Category.Configuration, false, "Configuration of language", false, true);
      this.addSettings(
         new Setting[]{
            languagesDescriptionPart1,
            languagesDescriptionPart2,
            languageMode,
            clickGuiOptionsDescription,
            applyForCategory,
            applyForDescriptions,
            applyForName,
            applyForSettings,
            arrayListOptionsDescription,
            applyForArrayList
         }
      );
   }

   public static int getSelectedLanguageIndex() {
      if (languageMode.getCurrent().equalsIgnoreCase("CHINESE_SIMPLIFIED")) {
         return 1;
      } else {
         return languageMode.getCurrent().equalsIgnoreCase("CHINESE_TRADITIONAL") ? 2 : 0;
      }
   }

   public static String translate(String input) {
      switch (getSelectedLanguageIndex()) {
         case 1:
            return bundleChineseSimplified.getString(input);
         case 2:
            return bundleChineseTraditional.getString(input);
         default:
            return bundleEnglish.getString(input);
      }
   }

   public static void setLanguage(String languageName) {
      languageName = languageName.toUpperCase();
      languageMode.setCurrentMode(languageName);
   }

   public static String translateModuleName(String CASE_SENSITIVE_NAME) {
      return translate("module." + CASE_SENSITIVE_NAME + ".name");
   }

   public static String getModuleDescription(String CASE_SENSITIVE_NAME) {
      return !applyForDescriptions.getState() ? CASE_SENSITIVE_NAME : translate("module." + CASE_SENSITIVE_NAME + ".description");
   }

   public static String getCategoryName(String CASE_SENSITIVE_NAME) {
      return !applyForCategory.getState() ? CASE_SENSITIVE_NAME : translate("category." + CASE_SENSITIVE_NAME);
   }

   // $VF: Irreducible bytecode was duplicated to produce valid code
   static {
      languagesDescriptionPart1 = new DescriptionSetting("ENGLISH, CHINESE_SIMPLIFIED, ");
      languagesDescriptionPart2 = new DescriptionSetting("CHINESE_TRADITIONAL");
      languageMode = new ModeSetting("Language", "ENGLISH", "CHINESE_SIMPLIFIED", "CHINESE_TRADITIONAL");
      clickGuiOptionsDescription = new DescriptionSetting("These options only affect ClickGui");
      applyForCategory = new BooleanSetting("Apply-for-category", false);
      applyForDescriptions = new BooleanSetting("Apply-for-descriptions", false);
      applyForName = new BooleanSetting("Apply-for-name", false);
      applyForSettings = new BooleanSetting("Apply-for-settings", false);
      arrayListOptionsDescription = new DescriptionSetting("This options only affects ArrayList");
      applyForArrayList = new BooleanSetting("Apply-for-arraylist", false);
      localeEnglishUS = new Locale("en", "US");
      localeChineseSimplified = new Locale("zh", "CN");
      localeChineseTraditional = new Locale("zh", "TW");
      bundleEnglish = ResourceBundle.getBundle("lang", localeEnglishUS, new d());
      bundleChineseSimplified = ResourceBundle.getBundle("lang", localeChineseSimplified, new d());
      bundleChineseTraditional = ResourceBundle.getBundle("lang", localeChineseTraditional, new d());
   }

   public static class d extends Control {
      @Override
      public ResourceBundle newBundle(String baseName, Locale locale, String format, ClassLoader loader, boolean reload) throws IllegalAccessException, InstantiationException, IOException {
         String bundleName = this.toBundleName(baseName, locale);
         String resourceName = this.toResourceName(bundleName, "txt");
         InputStream stream = loader.getResourceAsStream(resourceName);
         Object var10 = null;
         if (stream != null) {
            InputStreamReader reader = new InputStreamReader(stream, StandardCharsets.UTF_8);
            Object var12 = null;
            return new PropertyResourceBundle(reader);
         } else {
            if (stream != null) {
               stream.close();
            }

            return null;
         }
      }
   }
}
