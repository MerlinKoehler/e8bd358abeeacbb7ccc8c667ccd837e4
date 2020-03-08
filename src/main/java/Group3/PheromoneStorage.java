package Group3;

import java.util.ArrayList;

import Interop.Geometry.Point;
import Interop.Percept.Smell.SmellPerceptType;

public class PheromoneStorage {
	
	private ArrayList<Triplet<SmellPerceptType, Point, Integer>> pheromonesGuard = new ArrayList<>();
	private ArrayList<Triplet<SmellPerceptType, Point, Integer>> pheromonesIntruder = new ArrayList<>();
	
	
	public ArrayList<Triplet<SmellPerceptType, Point, Integer>> getPheromonesGuard() {
		return pheromonesGuard;
	}

	public void setPheromonesGuard(ArrayList<Triplet<SmellPerceptType, Point, Integer>> pheromones) {
		this.pheromonesGuard = pheromones;
	}
	
	public ArrayList<Triplet<SmellPerceptType, Point, Integer>> getPheromonesIntruder() {
		return pheromonesIntruder;
	}

	public void setPheromonesIntruder(ArrayList<Triplet<SmellPerceptType, Point, Integer>> pheromonesIntruder) {
		this.pheromonesIntruder = pheromonesIntruder;
	}
	
	//updates this whole list - how long a pheromone has left
	//deletes pheromones that are not smellable anymore
	public void updatePheromones() {
			ArrayList<Triplet<SmellPerceptType, Point, Integer>> remove = new ArrayList<>();
			
			for (int i = 0; i < pheromonesGuard.size(); i++) {
				pheromonesGuard.get(i).setContent3(pheromonesGuard.get(i).getContent3()-1);
				if (pheromonesGuard.get(i).getContent3() <= 0) {
					remove.add(pheromonesGuard.get(i));
				}
			}
			
			pheromonesGuard.removeAll(remove);
		
			remove.clear();
			
			for (int i = 0; i < pheromonesIntruder.size(); i++) {
				pheromonesIntruder.get(i).setContent3(pheromonesIntruder.get(i).getContent3()-1);
				if (pheromonesIntruder.get(i).getContent3() <= 0) {
					remove.add(pheromonesIntruder.get(i));
				}
			}
			
			pheromonesIntruder.removeAll(remove);
	}
	
	public void addPheromone(SmellPerceptType type, Point point, Integer timeLeft, boolean guard) {
		if (guard) {
			Triplet<SmellPerceptType, Point, Integer> triplet = new Triplet<SmellPerceptType, Point, Integer>(type, point, timeLeft);
			pheromonesGuard.add(triplet);
		}
		else {
			Triplet<SmellPerceptType, Point, Integer> triplet = new Triplet<SmellPerceptType, Point, Integer>(type, point, timeLeft);
			pheromonesIntruder.add(triplet);
		}
	}

	
	
	
}
