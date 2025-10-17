package Myaven.module.modules.config;

import Myaven.events.Render2DEvent;
import Myaven.mixins.accessor.AccessorGuiChat;
import Myaven.module.Category;
import Myaven.module.Module;
import Myaven.module.modules.misc.AntiBot;
import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.opengl.GL11;

public class CommandLine extends Module {
   private String input = "";
   private int selectionIndex = 0;
   private int pageIndex = 1;
   private List<String> suggestions = new ArrayList<>();
   private String commandName = "";
   private boolean isCommandMode = false;

   public CommandLine() {
      super("CommandLine", true, Category.Misc, false, "Configure the client setting by typing command in chat");
   }

   @SubscribeEvent
   public void onRender(Render2DEvent event) {
      if (this.mc.currentScreen instanceof GuiChat) {
         GuiTextField textField = ((AccessorGuiChat)this.mc.currentScreen).getInputField();
         if (textField == null) {
            return;
         }

         String text = textField.getText();
         if (!text.isEmpty() && text.charAt(0) == '.') {
            drawOutlineRect(
               2.0F,
               (float)(this.mc.currentScreen.height - 14),
               (float)(this.mc.currentScreen.width - 2),
               (float)(this.mc.currentScreen.height - 2),
               2.5F,
               new Color(Theme.computeThemeColor(0.0))
            );
         }
      } else {
         this.selectionIndex = 0;
         this.pageIndex = 1;
         this.suggestions.clear();
         this.commandName = "";
         this.isCommandMode = false;
      }
   }

   public static void drawOutlineRect(float x1, float y1, float x2, float y2, float lineWidth, Color color) {
      GlStateManager.pushMatrix();
      GlStateManager.enableBlend();
      GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
      GlStateManager.disableTexture2D();
      GlStateManager.disableDepth();
      GL11.glLineWidth(lineWidth);
      GL11.glColor4f((float)color.getRed() / 255.0F, (float)color.getGreen() / 255.0F, (float)color.getBlue() / 255.0F, (float)color.getAlpha() / 255.0F);
      GL11.glBegin(2);
      GL11.glVertex2f(x1, y1);
      GL11.glVertex2f(x2, y1);
      GL11.glVertex2f(x2, y2);
      GL11.glVertex2f(x1, y2);
      GL11.glEnd();
      GlStateManager.enableTexture2D();
      GlStateManager.enableDepth();
      GlStateManager.disableBlend();
      GlStateManager.popMatrix();
   }
}
