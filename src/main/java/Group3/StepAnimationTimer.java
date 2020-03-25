package Group3;

import javafx.animation.AnimationTimer;

public class StepAnimationTimer extends AnimationTimer {

	MapVisualization mv;
	MainControl controller;
	
	public StepAnimationTimer(MapVisualization mv, MainControl controller) {
		this.mv = mv;
		this.controller = controller;
	}

	public void handle(long now) {
		controller.doStep();
	}

}
