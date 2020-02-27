package Group3;

import java.io.*;
import java.util.ArrayList;

public class MapReader {

	private Storage gameStorage = new Storage();
	private ArrayList<StaticObject> staticObjects = new ArrayList<StaticObject>();
	
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
					String[] split = inputLine.split("=");
					for (int i = 0; i < split.length; i++) {
						split[i].trim();
					}
					
					switch(split[0]) {
					case "gameMode": 
						gameStorage.setGameMode(Integer.parseInt(split[1]));
						break;
						
					case "height":
						
						break;
						
					case "width":
						
						break;
					
					case "numGuards":
						
						break;
					
					case "numIntruders":
						
						break;
					
					case "captureDistance":
						
						break;
					
					case "winConditionIntruderRounds":
						
						break;
						
					case "maxRotationAngle":
						
						break;
						
					case "maxMoveDistanceIntruder":
						
						break;
						
					case "maxSprintDistanceIntruder":
						
						break;
						
					case "maxMoveDistanceGuard":
						
						break;
						
					case "sprintCoolDown":
					
						break;
					
					case "pheromoneCooldown":
						
						break;
						
					case "radiusPheromone":
						
						break;
						
					case "slowDownModifierWindow":
						
						break;
					
					case "slowDownModifierDoor":
						
						break;
						
					case "slowDownModifierSentryTower":
						
						break;
						
					case "viewAngle":
						
						break;
						
					case "viewRays":
						
						break;
						
					case "viewRangeIntruderNormal":
						
						break;
						
					case "viewRangeIntruderShaded":
						
						break;
						
					case "viewRangeGuardNormal":
						
						break;
						
					case "viewRangeGuardShaded":
						
						break;
						
					case "viewRangeSentry":
						
						break;
						
					case "yellSoundRadius":
						
						break;
						
					case "maxMoveSoundRadius":
						
						break;
						
					case "windowSoundRadius":
						
						break;
						
					case "doorSoundRadius":
						
						break;
						
					case "targetArea":
						
						break;
						
					case "spawnAreaIntruders":
						
						break;
						
					case "spawnAreaGuards":
						
						break;
					
					case "wall":
						
						break;
						
					case "teleport":
						
						break;
						
					case "shaded":
						
						break;
						
					case "door":
						
						break;
						
					case "window":
						
						break;
						
					case "sentry":
						
						break;
						
					default:
						System.out.println(split[0] + " has no been read correctly.");
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
