package Group3_Game_Controller;

import javafx.animation.AnimationTimer;

public class StepAnimationTimer extends AnimationTimer {

	MapVisualization mv;
	MainControl controller;
	private long delayNs = 0 ;
	long previousTime = 0;


	public StepAnimationTimer(MapVisualization mv, MainControl controller, long delayMs) {
		this.delayNs = delayMs * 1_000_000;
		this.mv = mv;
		this.controller = controller;
	}

	public void handle(long now) {
		if ((now - previousTime) < delayNs) {
			return;
		}
		previousTime = now;

		handle();

	}
	public void handle() {
		controller.doStep();
	}

}
