package Myaven.ui;

import Myaven.setting.Setting;
import Myaven.ui.components.ModuleComponent;
import Myaven.ui.font.IFontRenderer;
import javax.annotation.Nullable;
import org.jetbrains.annotations.NotNull;

public interface Component {
	@Nullable
	default Setting getSetting() {
		return null;
	}

	default void render() {
	}

	@NotNull
	ModuleComponent getParent();

	@NotNull
	default IFontRenderer getFontRenderer() {
		return ClickGUI.getFont();
	}

	default void drawScreen(int x, int y) {
	}

	default void onClick(int x, int y, int b) {
	}

	default void mouseReleased(int x, int y, int m) {
	}

	default void keyTyped(char t, int k) {
	}

	default void updateHeight(int n) {
	}

	default int getHeight() {
		return 0;
	}

	default void onGuiClosed() {
	}

	default void render(int x, int y) {
		this.drawScreen(x, y);
	}
}
