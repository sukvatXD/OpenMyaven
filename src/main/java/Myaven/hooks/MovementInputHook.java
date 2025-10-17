package Myaven.hooks;

import Myaven.Myaven;
import Myaven.util.ClientUtil;
import net.minecraft.util.MovementInput;
import org.lwjgl.input.Keyboard;

public class MovementInputHook extends MovementInput {
	public void updatePlayerMoveState() {
		this.moveStrafe = 0.0F;
		this.moveForward = 0.0F;
		if (Keyboard.isKeyDown(Myaven.mc.gameSettings.keyBindForward.getKeyCode())) {
			this.moveForward++;
		}

		if (Keyboard.isKeyDown(Myaven.mc.gameSettings.keyBindBack.getKeyCode())) {
			this.moveForward--;
		}

		if (Keyboard.isKeyDown(Myaven.mc.gameSettings.keyBindLeft.getKeyCode())) {
			this.moveStrafe++;
		}

		if (Keyboard.isKeyDown(Myaven.mc.gameSettings.keyBindRight.getKeyCode())) {
			this.moveStrafe--;
		}

		this.jump = Keyboard.isKeyDown(Myaven.mc.gameSettings.keyBindJump.getKeyCode());
		this.sneak = Keyboard.isKeyDown(Myaven.mc.gameSettings.keyBindSneak.getKeyCode());
		if (this.sneak) {
			this.moveStrafe = (float)((double)this.moveStrafe * 0.3);
			this.moveForward = (float)((double)this.moveForward * 0.3);
		}
	}

	private static RuntimeException a(RuntimeException runtimeException) {
		return runtimeException;
	}
}
