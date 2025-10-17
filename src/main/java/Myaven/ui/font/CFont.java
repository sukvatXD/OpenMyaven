package Myaven.ui.font;

import Myaven.enums.FontDirection;
import Myaven.setting.Setting;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import net.minecraft.client.renderer.texture.DynamicTexture;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.opengl.GL11;

public class CFont {
	protected s[] n = new s[256];
	Font t;
	boolean g;
	boolean q;
	int y = -1;
	DynamicTexture H;

	public CFont(Font font, boolean antiAlias, boolean fractionalMetrics) {
		this.t = font;
		this.g = antiAlias;
		this.q = fractionalMetrics;
		this.H = this.n(font, antiAlias, fractionalMetrics, this.n);
	}

	protected DynamicTexture n(Font font, boolean antiAlias, boolean fractionalMetrics, s[] chars) {
		BufferedImage img = this.L(font, antiAlias, fractionalMetrics, chars);
		return new DynamicTexture(img);
	}

	protected BufferedImage L(Font font, boolean antiAlias, boolean fractionalMetrics, @NotNull s[] chars) {
		BufferedImage bufferedImage = new BufferedImage(1024, 1024, 2);
		Graphics2D graphics = (Graphics2D)bufferedImage.getGraphics();
		graphics.setFont(font);
		graphics.setColor(new Color(255, 255, 255, 0));
		graphics.fillRect(0, 0, 1024, 1024);
		graphics.setColor(Color.WHITE);
		graphics.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
		graphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		FontMetrics fontMetrics = graphics.getFontMetrics();
		int charHeight = 0;
		int positionX = 0;
		int positionY = 1;
		int index = 0;

		while (index < chars.length) {
			char c = (char)index;
			s charData = new s();
			Rectangle2D dimensions = fontMetrics.getStringBounds(String.valueOf(c), graphics);
			charData.T = dimensions.getBounds().width + 8;
			charData.P = dimensions.getBounds().height;
			if (positionX + charData.T >= 1024) {
				positionX = 0;
				positionY += charHeight;
				charHeight = 0;
			}

			if (charData.P > charHeight) {
				charHeight = charData.P;
			}

			charData.x = positionX;
			charData.Q = positionY;
			if (charData.P > this.y) {
				this.y = charData.P;
			}

			chars[index] = charData;
			graphics.drawString(String.valueOf(c), positionX + 2, positionY + fontMetrics.getAscent());
			positionX += charData.T;
			index++;
		}

		return bufferedImage;
	}

	public void l(s[] chars, char c, double x, double y) throws ArrayIndexOutOfBoundsException {
		this.A(x, y, (double)chars[c].T, (double)chars[c].P, (double)chars[c].x, (double)chars[c].Q, (double)chars[c].T, (double)chars[c].P);
	}

	protected void A(double x2, double y2, double width, double height, double srcX, double srcY, double srcWidth, double srcHeight) {
		float renderSRCX = (float)(srcX / 1024.0);
		float renderSRCY = (float)(srcY / 1024.0);
		float renderSRCWidth = (float)(srcWidth / 1024.0);
		float renderSRCHeight = (float)(srcHeight / 1024.0);
		GL11.glTexCoord2f(renderSRCX + renderSRCWidth, renderSRCY);
		GL11.glVertex2d(x2 + width, y2);
		GL11.glTexCoord2f(renderSRCX, renderSRCY);
		GL11.glVertex2d(x2, y2);
		GL11.glTexCoord2f(renderSRCX, renderSRCY + renderSRCHeight);
		GL11.glVertex2d(x2, y2 + height);
		GL11.glTexCoord2f(renderSRCX, renderSRCY + renderSRCHeight);
		GL11.glVertex2d(x2, y2 + height);
		GL11.glTexCoord2f(renderSRCX + renderSRCWidth, renderSRCY + renderSRCHeight);
		GL11.glVertex2d(x2 + width, y2 + height);
		GL11.glTexCoord2f(renderSRCX + renderSRCWidth, renderSRCY);
		GL11.glVertex2d(x2 + width, y2);
	}

	public void setAntiAlias(boolean antiAlias) {
		if (this.g != antiAlias) {
			this.g = antiAlias;
			this.H = this.n(this.t, antiAlias, this.q, this.n);
		}
	}

	public void setFractionalMetrics(boolean fractionalMetrics) {
		if (this.q != fractionalMetrics) {
			this.q = fractionalMetrics;
			this.H = this.n(this.t, this.g, fractionalMetrics, this.n);
		}
	}

	public void setFont(Font font) {
		this.t = font;
		this.H = this.n(font, this.g, this.q, this.n);
	}

	public static class s {
		public int T;
		public int P;
		public int x;
		public int Q;

		protected s() {
		}
	}
}
