package Group3;

import java.util.ArrayList;
import Interop.Geometry.Point;
import Interop.Percept.Sound.SoundPerceptType;


public class SoundStorage {

	private ArrayList<Triplet<SoundPerceptType, Point, Integer>> sounds = new ArrayList<>();
	
	public ArrayList<Triplet<SoundPerceptType, Point, Integer>> getSounds() {
		return sounds;
	}

	public void setSounds(ArrayList<Triplet<SoundPerceptType, Point, Integer>> sounds) {
		this.sounds = sounds;
	}
	
	public void addSound(SoundPerceptType type, Point point, Integer timeLeft, boolean guard) {
			Triplet<SoundPerceptType, Point, Integer> triplet = new Triplet<SoundPerceptType, Point, Integer>(type, point, timeLeft);
			sounds.add(triplet);
	}
	
	public void updateSounds() {
		ArrayList<Triplet<SoundPerceptType, Point, Integer>> remove = new ArrayList<>();
		
		for (int i = 0; i < sounds.size(); i++) {
			sounds.get(i).setContent3(sounds.get(i).getContent3()-1);
			if (sounds.get(i).getContent3() <= 0) {
				remove.add(sounds.get(i));
			}
		}
		
		sounds.removeAll(remove);
	}
}
