package Group3;

import java.util.ArrayList;
import Interop.Geometry.Point;
import Interop.Percept.Smell.SmellPerceptType;
import javafx.scene.layout.BorderPane;

public class PheromoneStorage {
	
	private ArrayList<Pheromone> pheromonesGuard = new ArrayList<>();
	private ArrayList<Pheromone> pheromonesIntruder = new ArrayList<>();
	private ArrayList<Pheromone> pheromones = new ArrayList<>();
	private BorderPane mapPane;
	
	public PheromoneStorage() {
		this.mapPane = null;
	}
	
	public PheromoneStorage(BorderPane mapPane) {
		this.mapPane = mapPane;
		
	}


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
					//removes ellipse
					if(mapPane != null) {
						mapPane.getChildren().remove(pheromonesGuard.get(i).getShape());
					}
				}
			}
			
			pheromonesGuard.removeAll(remove);
		
			remove.clear();
			
			for (int i = 0; i < pheromonesIntruder.size(); i++) {
				pheromonesIntruder.get(i).setTurnsLeft(pheromonesIntruder.get(i).getTurnsLeft()-1);
				pheromonesIntruder.get(i).updateShape();
				
				if (pheromonesIntruder.get(i).getTurnsLeft() <= 0) {
					remove.add(pheromonesIntruder.get(i));
					if(mapPane != null) {
						mapPane.getChildren().remove(pheromonesIntruder.get(i).getShape());
					}
				}
			}
			
			pheromonesIntruder.removeAll(remove);
	}
	public void addPheromone(SmellPerceptType type, Point point, Integer timeLeft, boolean guard, double radiusScaled) {
		Pheromone pheromone = new Pheromone(type, point, timeLeft, radiusScaled);

		if (guard) {
			pheromonesGuard.add(pheromone);
		}
		else {
			pheromonesIntruder.add(pheromone);
		}
		pheromones.add(pheromone);
	}

	public Pheromone getLast(String s) {
		if(s.equals(Guard.class.getName()) && pheromonesGuard.size() > 0) {
			return pheromonesGuard.get(pheromonesGuard.size()-1);
		}
		else if(s.equals(Intruder.class.getName()) && pheromonesIntruder.size() > 0) {
			return pheromonesIntruder.get(pheromonesIntruder.size()-1);
		}
		return null;
	}
	public ArrayList<Pheromone> getAll() {
		return this.pheromones;
	}

}
