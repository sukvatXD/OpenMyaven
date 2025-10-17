package Myaven.hooks;

import Myaven.Myaven;
import Myaven.setting.Setting;
import com.google.common.util.concurrent.AtomicDouble;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;
import net.java.games.input.Mouse;
import net.minecraft.util.MouseHelper;

public class MouseHelperHook extends MouseHelper {
	private ScheduledExecutorService l;
	private AtomicDouble N;
	private AtomicDouble V;
	private AtomicBoolean v;
	private Set<Mouse> b;

	public MouseHelperHook() {
		this.l = Executors.newScheduledThreadPool((int)8);
		this.N = new AtomicDouble();
		this.V = new AtomicDouble();
		this.v = new AtomicBoolean(false);
		this.b = new HashSet();
	}

	public void e() {
	}

	public void mouseXYChange() {
		this.deltaX = (int)this.N.getAndSet(0.0);
		this.deltaY = (int)(-this.V.getAndSet(0.0));
	}

	public void Y() {
		Myaven.mc.mouseHelper = this;
		this.l.scheduleAtFixedRate(this::f, 0L, 1L, TimeUnit.MILLISECONDS);
		this.l.scheduleAtFixedRate(this::X, 0L, 50L, TimeUnit.MILLISECONDS);
	}

	private void f() {
		if (Myaven.mc.currentScreen == null) {
			this.b.forEach(mouse -> {
				mouse.poll();
				this.N.addAndGet((double)mouse.getX().getPollData());
				this.V.addAndGet((double)mouse.getY().getPollData());
			});
		}
	}

	private void X() {
	}
}
