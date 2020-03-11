package Group3;

import java.util.ArrayList;
import Interop.Geometry.Point;
import Interop.Percept.Sound.SoundPerceptType;


public class SoundStorage {

	// quartet contains: type, coordinates, time left, radius
	private ArrayList<Quartet<SoundPerceptType, Point, Integer, Double>> sounds = new ArrayList<>();
	
	public ArrayList<Quartet<SoundPerceptType, Point, Integer, Double>> getSounds() {
		return sounds;
	}

	public void setSounds(ArrayList<Quartet<SoundPerceptType, Point, Integer, Double>> sounds) {
		this.sounds = sounds;
	}
	
	public void addSound(SoundPerceptType type, Point point, Integer timeLeft, Double radius) {
			Quartet<SoundPerceptType, Point, Integer, Double> quartet = new Quartet<SoundPerceptType, Point, Integer, Double>(type, point, timeLeft, radius);
			sounds.add(quartet);
	}
	
	public void updateSounds() {
		ArrayList<Quartet<SoundPerceptType, Point, Integer, Double>> remove = new ArrayList<>();
		
		for (int i = 0; i < sounds.size(); i++) {
			sounds.get(i).setContent3(sounds.get(i).getContent3()-1);
			if (sounds.get(i).getContent3() <= 0) {
				remove.add(sounds.get(i));
			}
		}
		
		sounds.removeAll(remove);
	}
}
