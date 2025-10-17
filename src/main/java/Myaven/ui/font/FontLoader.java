package Myaven.ui.font;

import Myaven.Myaven;

import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;

public class FontLoader {
	public static Font d(String location, int size) throws IOException, FontFormatException {
		ScaledResolution sr = new ScaledResolution(Myaven.mc);
		size = (int)((double)size * ((double)sr.getScaleFactor() / 2.0));
		InputStream is = Myaven.mc.getResourceManager().getResource(new ResourceLocation("Myaven:fonts/" + location)).getInputStream();
		return Font.createFont(0, is).deriveFont(0, (float)size);
	}
}
