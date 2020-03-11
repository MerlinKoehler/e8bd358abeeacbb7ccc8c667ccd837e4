package Group3;

import java.util.ArrayList;
import Interop.Geometry.Point;
import Interop.Percept.Smell.SmellPerceptType;

public class PheromoneStorage {
	
	private ArrayList<Pheromone> pheromonesGuard = new ArrayList<>();
	private ArrayList<Pheromone> pheromonesIntruder = new ArrayList<>();
	
	public ArrayList<Pheromone> getPheromonesGuard() {
		return pheromonesGuard;
	}

	public ArrayList<Pheromone> getPheromonesIntruder() {
		return pheromonesIntruder;
	}

	
	
	//updates this whole list - how long a pheromone has left
	//deletes pheromones that are not smellable anymore
	public void updatePheromones() {
			ArrayList<Pheromone> remove = new ArrayList<>();
			
			for (int i = 0; i < pheromonesGuard.size(); i++) {
				pheromonesGuard.get(i).setTurnsLeft(pheromonesGuard.get(i).getTurnsLeft()-1);
				if (pheromonesGuard.get(i).getTurnsLeft() <= 0) {
					remove.add(pheromonesGuard.get(i));
				}
			}
			
			pheromonesGuard.removeAll(remove);
		
			remove.clear();
			
			for (int i = 0; i < pheromonesIntruder.size(); i++) {
				pheromonesIntruder.get(i).setTurnsLeft(pheromonesIntruder.get(i).getTurnsLeft()-1);
				if (pheromonesIntruder.get(i).getTurnsLeft() <= 0) {
					remove.add(pheromonesIntruder.get(i));
				}
			}
			
			pheromonesIntruder.removeAll(remove);
	}
	
	public void addPheromone(SmellPerceptType type, Point point, Integer timeLeft, boolean guard) {
		Pheromone pheromone = new Pheromone(type, point, timeLeft);
		if (guard) {
			pheromonesGuard.add(pheromone);
		}
		else {
			pheromonesIntruder.add(pheromone);
		}
	}

	
	
	
}
