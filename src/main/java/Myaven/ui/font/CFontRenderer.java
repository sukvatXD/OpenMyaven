package Myaven.ui.font;

import Myaven.enums.FontDirection;
import Myaven.util.RenderUtil;
import java.awt.Font;
import java.util.ArrayList;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.opengl.GL11;

public class CFontRenderer extends CFont implements IFontRenderer {
	private int[] colorCodes = new int[32];

	public CFontRenderer(Font font) {
		super(font, true, true);
		this.initColorCodes();
		this.I();
	}

	@Override
	public void drawStringAligned(String text, double x, double y, @NotNull FontDirection centerMode, boolean dropShadow, int color) {
		switch (centerMode) {
			case X:
				if (dropShadow) {
					this.drawString(text, x - this.getWidth(text) / 2.0 + 0.5, y + 0.5, color, true);
				}

				this.drawString(text, x - this.getWidth(text) / 2.0, y, color, false);
				return;
			case Y:
				if (dropShadow) {
					this.drawString(text, x + 0.5, y - this.getHeight() / 2.0 + 0.5, color, true);
				}

				this.drawString(text, x, y - this.getHeight() / 2.0, color, false);
				return;
			case XY:
				if (dropShadow) {
					this.drawString(text, x - this.getWidth(text) / 2.0 + 0.5, y - this.getHeight() / 2.0 + 0.5, color, true);
				}

				this.drawString(text, x - this.getWidth(text) / 2.0, y - this.getHeight() / 2.0, color, false);
				return;
			case NONE:
			default:
				if (dropShadow) {
					this.drawString(text, x + 0.5, y + 0.5, color, true);
				}

				this.drawString(text, x, y, color, false);
		}
	}

	@Override
	public boolean isAscii(@NotNull String text) {
		return text.chars().noneMatch(c -> {
			return c >= 256;
		});
	}

	@Override
	public void drawString(String text, double x, double y, int color, boolean shadow) {
		ScaledResolution sr = new ScaledResolution(Minecraft.getMinecraft());
		if (text != null) {
			if (shadow) {
				this.drawString(text, x + 1.0, y + 1.0, (color & 16579836) >> 2 | color & 0xFF000000, false);
			}

			double alpha = (double)((float)(color >> 24 & 0xFF) / 255.0F);
			x = (x - 1.0) * (double)sr.getScaleFactor();
			y = (y - 3.0) * (double)sr.getScaleFactor() - 0.2;
			GL11.glPushMatrix();
			GL11.glScaled(1.0 / (double)sr.getScaleFactor(), 1.0 / (double)sr.getScaleFactor(), 1.0 / (double)sr.getScaleFactor());
			GlStateManager.enableBlend();
			GlStateManager.blendFunc(770, 771);
			RenderUtil.setGLColor(color);
			GlStateManager.enableTexture2D();
			GlStateManager.bindTexture(this.H.getGlTextureId());

			for (int index = 0; index < text.length(); index++) {
				char character = text.charAt(index);
				if (character == 167) {
					int colorIndex = 21;
					colorIndex = "0123456789abcdefklmnor".indexOf(text.charAt(index + 1));
					if (colorIndex < 16) {
						if (colorIndex < 0) {
							colorIndex = 15;
						}

						if (shadow) {
							colorIndex += 16;
						}

						RenderUtil.setGLColorWithAlpha(this.colorCodes[colorIndex], alpha);
					} else {
						RenderUtil.setGLColor(color);
					}

					index++;
				} else if (character < this.n.length) {
					GL11.glBegin(4);
					this.l(this.n, character, x, y);
					x += (double)this.n[character].T - 8.3 + 0.0;
					GL11.glEnd();
				}
			}

			GlStateManager.disableBlend();
			GL11.glHint(3155, 4352);
			GL11.glPopMatrix();
			GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		}
	}

	@Override
	public void drawString(String text, double x, double y, int color) {
		this.drawString(text, x, y, color, false);
	}

	@Override
	public double measureTextWidth(String text) {
		return this.getWidth(text);
	}

	@Override
	public void S(String text, double x, double y, int color) {
		this.drawStringAligned(text, x, y, FontDirection.X, false, color);
	}

	@Override
	public double R() {
		return this.getHeight();
	}

	public double getWidth(String text) {
		ScaledResolution sr = new ScaledResolution(Minecraft.getMinecraft());
		if (text == null) {
			return 0.0;
		} else {
			double width = 0.0;

			for (int index = 0; index < text.length(); index++) {
				char character = text.charAt(index);
				if (character == 167) {
					index++;
				} else if (character < this.n.length) {
					width += (double)((float)this.n[character].T - 8.3F + 0.0F);
				}
			}

			return width / (double)sr.getScaleFactor();
		}
	}

	public double getHeight() {
		ScaledResolution sr = new ScaledResolution(Minecraft.getMinecraft());
		return (double)(this.y - 8) / (double)sr.getScaleFactor();
	}

	@Override
	public void setFont(Font font) {
		super.setFont(font);
		this.I();
	}

	@Override
	public void setAntiAlias(boolean antiAlias) {
		super.setAntiAlias(antiAlias);
		this.I();
	}

	@Override
	public void setFractionalMetrics(boolean fractionalMetrics) {
		super.setFractionalMetrics(fractionalMetrics);
		this.I();
	}

	private void I() {
	}

	public void drawStringWrapped(@NotNull String text, double x, double y, FontDirection centerMode, boolean shadow, int color, double width) {
		ArrayList<String> lines = new ArrayList();
		String[] words = text.trim().split(" ");
		StringBuilder line = new StringBuilder();

		for (String word : words) {
			double totalWidth = this.getWidth(line + " " + word);
			if (x + totalWidth >= x + width) {
				lines.add(line.toString());
				line = new StringBuilder(word).append(" ");
			} else {
				line.append(word).append(" ");
			}
		}

		lines.add(line.toString());
		double newY = y - (centerMode != FontDirection.XY && centerMode != FontDirection.Y ? 0.0 : (double)(lines.size() - 1) * (this.getHeight() + 5.0) / 2.0);

		for (String s : lines) {
			RenderUtil.resetGLColor();
			this.drawStringAligned(s, x, newY, centerMode, shadow, color);
			newY += this.getHeight() + 5.0;
		}
	}

	private void initColorCodes() {

		for (int index = 0; index < 32; index++) {
			int noClue = (index >> 3 & 1) * 85;
			int red = (index >> 2 & 1) * 170 + noClue;
			int green = (index >> 1 & 1) * 170 + noClue;
			int blue = (index & 1) * 170 + noClue;
			if (index == 6) {
				red += 85;
			}

			if (index >= 16) {
				red /= 4;
				green /= 4;
				blue /= 4;
			}

			this.colorCodes[index] = (red & 0xFF) << 16 | (green & 0xFF) << 8 | blue & 0xFF;
		}
	}
}
