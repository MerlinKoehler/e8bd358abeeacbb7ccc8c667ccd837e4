package Group3;

import java.util.ArrayList;
import Interop.Geometry.Point;
import Interop.Percept.Smell.SmellPerceptType;

public class PheromoneStorage {
	
	private ArrayList<Pheromone> pheromonesGuard = new ArrayList<>();
	private ArrayList<Pheromone> pheromonesIntruder = new ArrayList<>();
	private ArrayList<Pheromone> pheromones = new ArrayList<>();
	
	public ArrayList<Pheromone> getPheromonesGuard() {
		return pheromonesGuard;
	}

	public ArrayList<Pheromone> getPheromonesIntruder() {
		return pheromonesIntruder;
	}

	public ArrayList<Pheromone> getPheromones() { return pheromones; }
	
	//updates this whole list - how long a pheromone has left
	//deletes pheromones that are not smellable anymore
	public void updatePheromones() {
			ArrayList<Pheromone> remove = new ArrayList<>();
			
			for (int i = 0; i < pheromonesGuard.size(); i++) {
				pheromonesGuard.get(i).setTurnsLeft(pheromonesGuard.get(i).getTurnsLeft()-1);
				pheromonesGuard.get(i).updateShape();
				if (pheromonesGuard.get(i).getTurnsLeft() <= 0) {
					remove.add(pheromonesGuard.get(i));
				}
			}
			
			pheromonesGuard.removeAll(remove);
		
			remove.clear();
			
			for (int i = 0; i < pheromonesIntruder.size(); i++) {
				pheromonesIntruder.get(i).setTurnsLeft(pheromonesIntruder.get(i).getTurnsLeft()-1);
				pheromonesIntruder.get(i).updateShape();
				if (pheromonesIntruder.get(i).getTurnsLeft() <= 0) {
					remove.add(pheromonesIntruder.get(i));
				}
			}
			
			pheromonesIntruder.removeAll(remove);
	}
	
	public void addPheromone(SmellPerceptType type, Point point, Integer timeLeft, boolean guard, double scalingFactor) {
		Pheromone pheromone = new Pheromone(type, point, timeLeft, scalingFactor);
		if (guard) {
			pheromonesGuard.add(pheromone);
		}
		else {
			pheromonesIntruder.add(pheromone);
		}
		pheromones.add(pheromone);
	}
	
	public ArrayList<Pheromone> getAll() {
		return this.pheromones;
	}

}
