package Group3;

import Interop.Agent.Guard;
import Interop.Agent.Intruder;

import java.util.ArrayList;
import java.util.List;

import GUI.agent.factories.IAgentFactory;

/**
 * This class provides common way to build agents for the competition.
 *
 * Sharing knowledge between agents is NOT ALLOWED.
 *
 * For example:
 * Agents must not hold ANY references to common objects or references to each other.
 */
public class AgentsFactory{
    public static List<Intruder> createIntruders(int number) {
        
    	List<Intruder> intruders = new ArrayList<Intruder>();
    	
    	for(int i = 0; i < number; i++) {
    		Group3.Intruder.Intruder intruder =  new Group3.Intruder.Intruder();
    		intruders.add(intruder);
    	}
    	
    	return intruders;
    }
    public static List<Guard> createGuards(int number) {
    	
    	List<Guard> guards = new ArrayList<Guard>();
    	for(int i = 0; i < number; i++) {
    		Group3.Guard1.Guard guard =  new Group3.Guard1.Guard();
    		//Group3.ExplorationAgent agent = new Group3.ExplorationAgent();
    		guards.add(guard);
    		//guards.add(agent);
    	}
    	return guards;
    }
}