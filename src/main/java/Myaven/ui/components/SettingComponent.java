package Myaven.ui.components;

import Myaven.module.modules.config.Language;
import Myaven.setting.Setting;
import Myaven.setting.settings.BooleanSetting;
import Myaven.setting.settings.ColorSetting;
import Myaven.setting.settings.DescriptionSetting;
import Myaven.setting.settings.ModeSetting;
import Myaven.setting.settings.PercentageSetting;
import Myaven.setting.settings.SliderSetting;
import Myaven.setting.settings.TextSetting;
import Myaven.ui.Component;
import java.awt.Color;
import java.text.MessageFormat;
import org.jetbrains.annotations.NotNull;

public abstract class SettingComponent implements Component {
	public static int P;
	public static int x;
	public static int d;
	public static int q;
	public static int D;
	public static int T;
	public static int R;
	public static int u;
	protected ModuleComponent parentModule;
	protected int backgroundColor;
	protected int textColor;
	protected int Q;
	protected int a;
	protected int width;

	public SettingComponent(ModuleComponent parent) {
		super();
		this.backgroundColor = P;
		this.textColor = d;
		this.parentModule = parent;
	}

	@Override
	public final void render(int x, int y) {
		boolean hover = this.isHovered(x, y);
		this.backgroundColor = hover ? SettingComponent.x : P;
		this.textColor = hover ? q : d;
		this.drawScreen(x, y);
	}

	@NotNull
	@Override
	public ModuleComponent getParent() {
		return this.parentModule;
	}

	public boolean isHovered(int x, int y) {
		return x > this.a && x < this.a + this.getParent().categoryComponent.getWidth() && y > this.width && y < this.width + 8;
	}

	public static SettingComponent create(@NotNull Setting setting, ModuleComponent component, int y) {
		if (setting instanceof DescriptionSetting) {
			return new DescriptionComponent((DescriptionSetting)setting, component, y);
		} else if (setting instanceof ModeSetting) {
			return new ModeComponent((ModeSetting)setting, component, y);
		} else if (setting instanceof SliderSetting) {
			return new SliderComponent((SliderSetting)setting, component, y);
		} else if (setting instanceof PercentageSetting) {
			return new SliderComponent((PercentageSetting)setting, component, y);
		} else if (setting instanceof BooleanSetting) {
			return new ButtonComponent(component.module, (BooleanSetting)setting, component, y);
		} else if (setting instanceof ColorSetting) {
			return new DescriptionComponent(
				new DescriptionSetting("ignore"),
				component,
				y,
				Language.applyForDescriptions.getState()
					? MessageFormat.format(Language.translate("clickgui.description.edit"), setting.getName())
					: "Edit \"" + setting.getName() + "\" using command"
			);
		} else {
			return setting instanceof TextSetting
				? new DescriptionComponent(
					new DescriptionSetting("ignore"),
					component,
					y,
					Language.applyForDescriptions.getState()
						? MessageFormat.format(Language.translate("clickgui.description.edit"), setting.getName())
						: "Edit \"" + setting.getName() + "\" using command"
				)
				: null;
		}
	}

	// $VF: Irreducible bytecode was duplicated to produce valid code
	static {
		P = new Color(255, 255, 255).getRGB();
		x = new Color(162, 162, 162).getRGB();
		d = new Color(20, 255, 0).getRGB();
		q = new Color(20, 162, 0).getRGB();
		D = new Color(0, 0, 0, 255).getRGB();
		T = new Color(96, 96, 96).getRGB();
		R = new Color(8, 136, 231).getRGB();
		u = new Color(5, 90, 152).getRGB();
	}
}
