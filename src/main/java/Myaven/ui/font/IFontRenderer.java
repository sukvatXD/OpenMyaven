package Myaven.ui.font;

import Myaven.enums.FontDirection;

public interface IFontRenderer {
	void drawString(String string, double double2, double double3, int integer, boolean boolean5);

	void drawString(String string, double double2, double double3, int integer);

	default void drawStringWithShadow(String text, double x, double y, int color) {
		this.drawString(text, x, y, color, true);
	}

	double measureTextWidth(String string);

	void S(String string, double double2, double double3, int integer);

	default void w(String text, double x, double y, int color) {
		this.drawString(text, x - (double)((int)this.measureTextWidth(text)), y, color, false);
	}

	double R();

	void drawStringAligned(String string, double double2, double double3, FontDirection in, boolean boolean5, int integer);

	boolean isAscii(String string);
}
