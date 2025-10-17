package Myaven.ui.font;

import Myaven.Myaven;
import Myaven.enums.FontDirection;
import org.jetbrains.annotations.NotNull;

public class VanillaFontRenderer implements IFontRenderer {
	public static VanillaFontRenderer instance = new VanillaFontRenderer();

	@Override
	public void drawString(String text, double x, double y, int color, boolean dropShadow) {
		Myaven.mc.fontRendererObj.drawString(text, (float)x, (float)y, color, dropShadow);
	}

	public static VanillaFontRenderer getInstance() {
		return instance;
	}

	@Override
	public void drawString(String text, double x, double y, int color) {
		this.drawString(text, x, y, color, false);
	}

	@Override
	public double measureTextWidth(String text) {
		return (double)Myaven.mc.fontRendererObj.getStringWidth(text);
	}

	@Override
	public void S(String text, double x, double y, int color) {
		this.drawString(text, x - (double)((int)this.measureTextWidth(text) >> 1), y, color, false);
	}

	@Override
	public double R() {
		return (double)Myaven.mc.fontRendererObj.FONT_HEIGHT;
	}

	@Override
	public void drawStringAligned(String text, double x, double y,
								  @NotNull FontDirection centerMode,
								  boolean dropShadow, int color) {

		double offsetX = 0;
		double offsetY = 0;

		switch (centerMode) {
			case X:
				offsetX = this.measureTextWidth(text) / 2.0;
				break;
			case Y:
				offsetY = this.R() / 2.0;
				break;
			case XY:
				offsetX = this.measureTextWidth(text) / 2.0;
				offsetY = this.R() / 2.0;
				break;
			case NONE:
			default:
				break;
		}

		x -= offsetX;
		y -= offsetY;

		this.drawString(text, x, y, color);
	}


	@Override
	public boolean isAscii(String text) {
		return true;
	}

	private static RuntimeException a(RuntimeException runtimeException) {
		return runtimeException;
	}
}
