package Myaven.module.modules.config;

import Myaven.events.Render2DEvent;
import Myaven.module.Category;
import Myaven.module.Module;
import Myaven.setting.Setting;
import Myaven.setting.settings.BooleanSetting;
import Myaven.setting.settings.ColorSetting;
import Myaven.setting.settings.DescriptionSetting;
import Myaven.setting.settings.ModeSetting;
import Myaven.setting.settings.SliderSetting;
import Myaven.util.ClientUtil;
import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class Notifications extends Module {
   public static DescriptionSetting graphicModeDescription;
   public static ModeSetting graphicMode;
   public static DescriptionSetting soundModeDescription1;
   public static DescriptionSetting soundModeDescription2;
   public static ModeSetting soundMode;
   public static ColorSetting customBackgroundColor;
   public static DescriptionSetting stripColorDescription;
   public static ModeSetting stripColorMode;
   public static ColorSetting customStripColor;
   public static BooleanSetting textShadow;
   public static SliderSetting stayTime;
   public static SliderSetting leaveTime;
   public static SliderSetting offsetX;
   public static SliderSetting offsetY;
   private static List<Notification> activeNotifications;
   private static int a;
   private static int A;
   private static int k;
   private static int u;
   private static String[] b;
   private static String[] d;
   private static long[] g;
   private static Integer[] i;

   public Notifications() {
      super("Notifications", true, Category.Configuration, false, "Module toggle notifications settings", false, true);
      this.addSettings(
              graphicModeDescription,
              graphicMode,
              soundModeDescription1,
              soundModeDescription2,
              soundMode,
              customBackgroundColor,
              stripColorDescription,
              stripColorMode,
              customStripColor,
              textShadow,
              stayTime,
              leaveTime,
              offsetX,
              offsetY);
   }

   public static void sendNotification(String text, boolean doEnable) {
      Minecraft mc = Minecraft.getMinecraft();
      String var4 = graphicMode.getCurrent();
      switch (var4) {
         case "CHAT":
            ClientUtil.sendPrefixedChat(text);
            break;
         case "LEFT":
            activeNotifications.add(
               new Notification(text, System.currentTimeMillis(), (int)offsetX.getRoundedValue(), computeNextNotificationY())
            );
            break;
         case "RIGHT":
            ScaledResolution sr = new ScaledResolution(mc);
            activeNotifications.add(
               new Notification(
                  text,
                  System.currentTimeMillis(),
                  (int)((double)(sr.getScaledWidth() - mc.fontRendererObj.getStringWidth(text) - 6) - offsetX.getRoundedValue()),
                  computeNextNotificationY()
               )
            );
      }

      var4 = soundMode.getCurrent();
      switch (var4) {
         case "BUTTON":
            ClientUtil.playSound("gui.button.press");
            break;
         case "PLATE":
            if (doEnable) {
               mc.getSoundHandler().playSound(PositionedSoundRecord.create(new ResourceLocation("random.click"), 0.6F));
            } else {
               mc.getSoundHandler().playSound(PositionedSoundRecord.create(new ResourceLocation("random.click"), 0.5F));
            }
            break;
         case "SIGMA":
            if (doEnable) {
               mc.getSoundHandler().playSound(PositionedSoundRecord.create(new ResourceLocation("myaven", "sigma.enable"), 1.0F));
            } else {
               mc.getSoundHandler().playSound(PositionedSoundRecord.create(new ResourceLocation("myaven", "sigma.disable"), 1.0F));
            }
            break;
         case "RISE":
            if (doEnable) {
               mc.getSoundHandler().playSound(PositionedSoundRecord.create(new ResourceLocation("myaven", "rise.enable"), 1.0F));
            } else {
               mc.getSoundHandler().playSound(PositionedSoundRecord.create(new ResourceLocation("myaven", "rise.disable"), 1.0F));
            }
            break;
         case "QUICKMACRO":
            if (doEnable) {
               mc.getSoundHandler().playSound(PositionedSoundRecord.create(new ResourceLocation("myaven", "quickmacro.enable"), 1.0F));
            } else {
               mc.getSoundHandler().playSound(PositionedSoundRecord.create(new ResourceLocation("myaven", "quickmacro.disable"), 1.0F));
            }
      }
   }

   private static int computeNextNotificationY() {
      Minecraft mc = Minecraft.getMinecraft();
      ScaledResolution sr = new ScaledResolution(mc);
      if (!activeNotifications.isEmpty()) {
         int y = (int)((double)sr.getScaledHeight() - offsetY.getRoundedValue());

         for (Notification notification : activeNotifications) {
            if (notification.posY - 5 - mc.fontRendererObj.FONT_HEIGHT < y) {
               y = notification.posY - 5 - mc.fontRendererObj.FONT_HEIGHT;
            }
         }

         return y;
      } else {
         return (int)((double)sr.getScaledHeight() - offsetY.getRoundedValue());
      }
   }

   private static int getNotificationStackSpacing() {
      return getNotificationHeight() + 5;
   }

   private static int getNotificationHeight() {
      Minecraft mc = Minecraft.getMinecraft();
      return mc.fontRendererObj.FONT_HEIGHT + 4;
   }

   @SubscribeEvent
   public void onRender2D(Render2DEvent event) {
      if (graphicMode.getCurrent().equalsIgnoreCase("LEFT") || graphicMode.getCurrent().equalsIgnoreCase("RIGHT")) {
         new ScaledResolution(this.mc);
         String var5 = stripColorMode.getCurrent();
         int color;
         switch (var5) {
            case "THEME":
               color = Theme.computeThemeColor(0.0);
               break;
            case "THEME_CUSTOM":
               color = Theme.computeCustomThemeColor(0.0);
               break;
            default:
               color = customStripColor.F();
         }

         for (Notification notification : activeNotifications) {
            String var7 = graphicMode.getCurrent();
            switch (var7) {
               case "LEFT":
                  Gui.drawRect(
                     notification.currentX,
                     notification.posY,
                     notification.currentX + 6 + this.mc.fontRendererObj.getStringWidth(notification.message),
                     notification.posY + getNotificationHeight(),
                     customBackgroundColor.F()
                  );
                  Gui.drawRect(
                     notification.currentX + 6 + this.mc.fontRendererObj.getStringWidth(notification.message),
                     notification.posY,
                     notification.currentX + 6 + this.mc.fontRendererObj.getStringWidth(notification.message) + 3,
                     notification.posY + getNotificationHeight(),
                     color
                  );
                  this.mc
                     .fontRendererObj
                     .drawString(notification.message, (float)(notification.currentX + 3), (float)(notification.posY + 2), -1, textShadow.getState());
                  if ((double)System.currentTimeMillis() - stayTime.getRoundedValue() >= (double)notification.startTime) {
                     long passedTime = (long)((double)System.currentTimeMillis() - ((double)notification.startTime + stayTime.getRoundedValue()));
                     float var15 = (float)passedTime / (float)leaveTime.getRoundedValue();
                     notification.currentX = notification.startX - (int)(var15 * (float)(this.mc.fontRendererObj.getStringWidth(notification.message) + 6));
                  }
                  break;
               case "RIGHT":
                  Gui.drawRect(
                     notification.currentX - 3,
                     notification.posY,
                     notification.currentX + 6 + this.mc.fontRendererObj.getStringWidth(notification.message),
                     notification.posY + getNotificationHeight(),
                     customBackgroundColor.F()
                  );
                  Gui.drawRect(notification.currentX - 3, notification.posY, notification.currentX, notification.posY + getNotificationHeight(), color);
                  this.mc
                     .fontRendererObj
                     .drawString(notification.message, (float)(notification.currentX + 3), (float)(notification.posY + 2), -1, textShadow.getState());
                  if ((double)System.currentTimeMillis() - stayTime.getRoundedValue() >= (double)notification.startTime) {
                     long passedTime = (long)((double)System.currentTimeMillis() - ((double)notification.startTime + stayTime.getRoundedValue()));
                     float percentagePassed = (float)passedTime / (float)leaveTime.getRoundedValue();
                     notification.currentX = notification.startX
                        + (int)(percentagePassed * (float)(this.mc.fontRendererObj.getStringWidth(notification.message) + 6));
                  }
            }
         }

         activeNotifications.removeIf(s -> (double)(System.currentTimeMillis() - s.startTime) >= leaveTime.getRoundedValue() + stayTime.getRoundedValue());
      }
   }

   // $VF: Irreducible bytecode was duplicated to produce valid code
   static {
      a = 3;
      A = 2;
      u = 3;
      k = 5;
      graphicModeDescription = new DescriptionSetting("RIGHT, LEFT, CHAT, DISABLE");
      graphicMode = new ModeSetting("Graphic", "RIGHT", "LEFT", "CHAT", "DISABLE");
      soundModeDescription1 = new DescriptionSetting("BUTTON, PLATE, SIGMA,");
      soundModeDescription2 = new DescriptionSetting("RISE, QUICKMACRO, DISABLE");
      soundMode = new ModeSetting("Sound", "BUTTON", "PLATE", "SIGMA", "RISE", "QUICKMACRO", "DISABLE");
      customBackgroundColor = new ColorSetting("Custom-background-color", "000000");
      stripColorDescription = new DescriptionSetting("THEME, THEME_CUSTOM, CUSTOM");
      stripColorMode = new ModeSetting("Strip-color", "THEME", "THEME_CUSTOM", "CUSTOM");
      customStripColor = new ColorSetting("Customstrip-color", "FFFFFF");
      textShadow = new BooleanSetting("Text-shadow", true);
      stayTime = new SliderSetting("Stay-time", 300.0, 100.0, 2000.0, 1.0);
      leaveTime = new SliderSetting("Leave-time", 500.0, 100.0, 2000.0, 1.0);
      offsetX = new SliderSetting("Offset-X", 0.0, 0.0, 100.0, 1.0);
      offsetY = new SliderSetting("Offset-Y", 50.0, 0.0, 200.0, 1.0);
      activeNotifications = new ArrayList<>();
   }

   public static class Notification {
      private String message;
      private int progress = 0;
      private long startTime;
      private int startX;
      private int currentX;
      private int posY;

      public Notification(String message, long StartTime, int startX, int startY) {
         this.message = message;
         this.startTime = StartTime;
         this.startX = startX;
         this.currentX = startX;
         this.posY = startY;
      }

      
      public String getMessage() {
         return this.message;
      }

      
      public int getProgress() {
         return this.progress;
      }

      
      public long getStartTime() {
         return this.startTime;
      }

      
      public int getStartX() {
         return this.startX;
      }

      
      public int getCurrentX() {
         return this.currentX;
      }

      
      public int getPosY() {
         return this.posY;
      }
   }
}
