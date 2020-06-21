package Group3_Game_Controller;

import java.util.ArrayList;
import Interop.Geometry.Point;
import Interop.Percept.Sound.SoundPerceptType;
import javafx.scene.layout.BorderPane;


public class SoundStorage {

	private ArrayList<Sound> sounds = new ArrayList<>();
	private BorderPane mapPane;
	private double scalingFactor = 1;
	public SoundStorage() {
		this.mapPane = null;
	}

	public SoundStorage(BorderPane mapPane, double scalingFactor) {
		this.mapPane = mapPane;
		this.scalingFactor = scalingFactor;
	}
	
	public ArrayList<Sound> getSounds() {
		return sounds;
	}

	public void setSounds(ArrayList<Sound> sounds) {
		this.sounds = sounds;
	}
	
	public void addSound(SoundPerceptType type, Point point, Integer timeLeft, Double radius) {
			Sound sound = new Sound(type, point, timeLeft, radius, scalingFactor);
			sounds.add(sound);
	}
	
	public void updateSounds() {
		ArrayList<Sound> remove = new ArrayList<>();
		
		for (int i = 0; i < sounds.size(); i++) {
			sounds.get(i).setTurnsLeft(sounds.get(i).getTurnsLeft()-1);
			//sounds.get(i).updateShape();
			
			
			if (sounds.get(i).getTurnsLeft() <= 0) {
				remove.add(sounds.get(i));
				//remove the ellipse
				if(mapPane != null) {
					mapPane.getChildren().remove(sounds.get(i).getShape());
				}
			}

		}
		sounds.removeAll(remove);
	}
}
