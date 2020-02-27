package Group3;

import java.io.*;
import java.util.ArrayList;

public class MapReader {

	private Storage gameStorage = new Storage();
	private ArrayList<StaticObject> staticObjects = new ArrayList<StaticObject>();
	private TargetArea target;
	private SpawnAreaIntruder spawnIntr;
	private SpawnAreaGuards spawnGuards;
	private ShadedArea shaded;
	private Wall wall;
	private Teleport teleport;
	private Window window;
	private SentryTower sentry;
	private Door door;
	private String split[];
	private String doubleSplit[];
	
	public MapReader(String path) {
		
		//basic reader stuff
		Reader reader = null;
		try {
			reader = new FileReader(path);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		BufferedReader buffReader = new BufferedReader(reader);
		
		//read the first line, so it is possible to enter the loop
		String inputLine = null;
		try {
			inputLine = buffReader.readLine();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
		//here, call the storage class while needed and store the objects as we want to
		while (inputLine != null) {
				try {
					split = inputLine.split("=");
					for (int i = 0; i < split.length; i++) {
						split[i] = split[i].trim();
					}
					
					switch(split[0]) {
					case "gameMode": 
						gameStorage.setGameMode(Integer.parseInt(split[1]));
						break;
						
					case "height":
						gameStorage.setHeight(Double.parseDouble(split[1]));
						break;
						
					case "width":
						gameStorage.setWidth(Double.parseDouble(split[1]));
						break;
					
					case "numGuards":
						gameStorage.setNumGuards(Integer.parseInt(split[1]));
						break;
					
					case "numIntruders":
						gameStorage.setNumIntruders(Integer.parseInt(split[1]));
						break;
					
					case "captureDistance":
						gameStorage.setCaptureDistance(Double.parseDouble(split[1]));
						break;
					
					case "winConditionIntruderRounds":
						gameStorage.setWinConditionIntruderRounds(Integer.parseInt(split[1]));
						break;
						
					case "maxRotationAngle":
						gameStorage.setMaxRotationAngle(Double.parseDouble(split[1]));
						break;
						
					case "maxMoveDistanceIntruder":
						gameStorage.setMaxMoveDistanceIntruder(Double.parseDouble(split[1]));
						break;
						
					case "maxSprintDistanceIntruder":
						gameStorage.setMaxSprintDistanceIntruder(Double.parseDouble(split[1]));
						break;
						
					case "maxMoveDistanceGuard":
						gameStorage.setMaxMoveDistanceGuard(Double.parseDouble(split[1]));
						break;
						
					case "sprintCoolDown":
						gameStorage.setSprintCoolDown(Integer.parseInt(split[1]));
						break;
					
					case "pheromoneCooldown":
						gameStorage.setPheromoneCoolDown(Integer.parseInt(split[1]));
						break;
						
					case "radiusPheromone":
						gameStorage.setRadiusPheromone(Double.parseDouble(split[1]));
						break;
						
					case "slowDownModifierWindow":
						gameStorage.setSlowDownModifierWindow(Double.parseDouble(split[1]));
						break;
					
					case "slowDownModifierDoor":
						gameStorage.setSlowDownModifierDoor(Double.parseDouble(split[1]));
						break;
						
					case "slowDownModifierSentryTower":
						gameStorage.setSlowDownModifierSentryTower(Double.parseDouble(split[1]));
						break;
						
					case "viewAngle":
						gameStorage.setViewAngle(Double.parseDouble(split[1]));
						break;
						
					case "viewRays":
						gameStorage.setViewRays(Integer.parseInt(split[1]));
						break;
						
					case "viewRangeIntruderNormal":
						gameStorage.setViewRangeIntruderNormal(Double.parseDouble(split[1]));
						break;
						
					case "viewRangeIntruderShaded":
						gameStorage.setViewRangeGuardShaded(Double.parseDouble(split[1]));
						break;
						
					case "viewRangeGuardNormal":
						gameStorage.setViewRangeGuardNormal(Double.parseDouble(split[1]));
						break;
						
					case "viewRangeGuardShaded":
						gameStorage.setViewRangeGuardShaded(Double.parseDouble(split[1]));
						break;
						
					case "viewRangeSentry":
						doubleSplit = split[1].split(",");
						for (int i = 0; i < doubleSplit.length; i++) {
							doubleSplit[i].trim();
						}
						gameStorage.setViewRangeSentryShort(Double.parseDouble(doubleSplit[0]));
						gameStorage.setViewRangeSentryLong(Double.parseDouble(doubleSplit[1]));
						break;
						
					case "yellSoundRadius":
						gameStorage.setYellSoundRadius(Double.parseDouble(split[1]));
						break;
						
					case "maxMoveSoundRadius":
						gameStorage.setMaxMoveSoundRadius(Double.parseDouble(split[1]));
						break;
						
					case "windowSoundRadius":
						gameStorage.setWindowSoundRadius(Double.parseDouble(split[1]));
						break;
						
					case "doorSoundRadius":
						gameStorage.setDoorSoundRadius(Double.parseDouble(split[1]));
						break;
						
					case "targetArea":
						doubleSplit = split[1].split(",");
						for (int i = 0; i < doubleSplit.length; i++) {
							doubleSplit[i] = doubleSplit[i].trim();
						}
						target = new TargetArea(Integer.parseInt(doubleSplit[0]), Integer.parseInt(doubleSplit[1]), Integer.parseInt(doubleSplit[2]), Integer.parseInt(doubleSplit[3]), Integer.parseInt(doubleSplit[4]), Integer.parseInt(doubleSplit[5]), Integer.parseInt(doubleSplit[6]), Integer.parseInt(doubleSplit[7]));
						staticObjects.add(target);
						break;
						
					case "spawnAreaIntruders":
						doubleSplit = split[1].split(",");
						for (int i = 0; i < doubleSplit.length; i++) {
							doubleSplit[i] = doubleSplit[i].trim();
						}
						spawnIntr = new SpawnAreaIntruder(Integer.parseInt(doubleSplit[0]), Integer.parseInt(doubleSplit[1]), Integer.parseInt(doubleSplit[2]), Integer.parseInt(doubleSplit[3]), Integer.parseInt(doubleSplit[4]), Integer.parseInt(doubleSplit[5]), Integer.parseInt(doubleSplit[6]), Integer.parseInt(doubleSplit[7]));
						staticObjects.add(spawnIntr);
						break;
						
					case "spawnAreaGuards":
						doubleSplit = split[1].split(",");
						for (int i = 0; i < doubleSplit.length; i++) {
							doubleSplit[i] = doubleSplit[i].trim();
						}
						spawnGuards = new SpawnAreaGuards(Integer.parseInt(doubleSplit[0]), Integer.parseInt(doubleSplit[1]), Integer.parseInt(doubleSplit[2]), Integer.parseInt(doubleSplit[3]), Integer.parseInt(doubleSplit[4]), Integer.parseInt(doubleSplit[5]), Integer.parseInt(doubleSplit[6]), Integer.parseInt(doubleSplit[7]));
						staticObjects.add(spawnGuards);
						break;
					
					case "wall":
						doubleSplit = split[1].split(",");
						for (int i = 0; i < doubleSplit.length; i++) {
							doubleSplit[i] = doubleSplit[i].trim();
						}
						wall = new Wall(Integer.parseInt(doubleSplit[0]), Integer.parseInt(doubleSplit[1]), Integer.parseInt(doubleSplit[2]), Integer.parseInt(doubleSplit[3]), Integer.parseInt(doubleSplit[4]), Integer.parseInt(doubleSplit[5]), Integer.parseInt(doubleSplit[6]), Integer.parseInt(doubleSplit[7]));
						staticObjects.add(wall);
						break;
						
					case "teleport":
						doubleSplit = split[1].split(",");
						for (int i = 0; i < doubleSplit.length; i++) {
							doubleSplit[i] = doubleSplit[i].trim();
						}
						teleport = new Teleport(Integer.parseInt(doubleSplit[0]), Integer.parseInt(doubleSplit[1]), Integer.parseInt(doubleSplit[2]), Integer.parseInt(doubleSplit[3]), Integer.parseInt(doubleSplit[4]), Integer.parseInt(doubleSplit[5]), Integer.parseInt(doubleSplit[6]), Integer.parseInt(doubleSplit[7]), Integer.parseInt(doubleSplit[8]), Integer.parseInt(doubleSplit[9]));
						staticObjects.add(teleport);
						break;
						
					case "shaded":
						doubleSplit = split[1].split(",");
						for (int i = 0; i < doubleSplit.length; i++) {
							doubleSplit[i] = doubleSplit[i].trim();
						}
						shaded = new ShadedArea(Integer.parseInt(doubleSplit[0]), Integer.parseInt(doubleSplit[1]), Integer.parseInt(doubleSplit[2]), Integer.parseInt(doubleSplit[3]), Integer.parseInt(doubleSplit[4]), Integer.parseInt(doubleSplit[5]), Integer.parseInt(doubleSplit[6]), Integer.parseInt(doubleSplit[7]));
						staticObjects.add(shaded);
						break;
						
					case "door":
						doubleSplit = split[1].split(",");
						for (int i = 0; i < doubleSplit.length; i++) {
							doubleSplit[i] = doubleSplit[i].trim();
						}
						door = new Door(Integer.parseInt(doubleSplit[0]), Integer.parseInt(doubleSplit[1]), Integer.parseInt(doubleSplit[2]), Integer.parseInt(doubleSplit[3]), Integer.parseInt(doubleSplit[4]), Integer.parseInt(doubleSplit[5]), Integer.parseInt(doubleSplit[6]), Integer.parseInt(doubleSplit[7]));
						staticObjects.add(door);
						break;
						
					case "window":
						doubleSplit = split[1].split(",");
						for (int i = 0; i < doubleSplit.length; i++) {
							doubleSplit[i] = doubleSplit[i].trim();
						}
						window = new Window(Integer.parseInt(doubleSplit[0]), Integer.parseInt(doubleSplit[1]), Integer.parseInt(doubleSplit[2]), Integer.parseInt(doubleSplit[3]), Integer.parseInt(doubleSplit[4]), Integer.parseInt(doubleSplit[5]), Integer.parseInt(doubleSplit[6]), Integer.parseInt(doubleSplit[7]));
						staticObjects.add(window);
						break;
						
					case "sentry":
						doubleSplit = split[1].split(",");
						for (int i = 0; i < doubleSplit.length; i++) {
							doubleSplit[i] = doubleSplit[i].trim();
						}
						sentry = new SentryTower(Integer.parseInt(doubleSplit[0]), Integer.parseInt(doubleSplit[1]), Integer.parseInt(doubleSplit[2]), Integer.parseInt(doubleSplit[3]), Integer.parseInt(doubleSplit[4]), Integer.parseInt(doubleSplit[5]), Integer.parseInt(doubleSplit[6]), Integer.parseInt(doubleSplit[7]));
						staticObjects.add(sentry);
						break;
						
					default:
						System.out.println(split[0] + " has no been read correctly.");
						break;
					}
					
					
					inputLine = buffReader.readLine();
				} 
				catch (IOException e) {
						e.printStackTrace();
				}
		}
		
		try {
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public Storage getStorage() {
		return gameStorage;
	}

	public ArrayList<StaticObject> getStaticObjects() {
		return staticObjects;
	}
}
