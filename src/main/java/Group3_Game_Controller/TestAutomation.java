package Group3_Game_Controller;

import GUI.Main;
import GUI.agent.container.IntruderContainer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;

// number of guards - number of intruders - map name - duration of game in rounds - intruders captured - who won - type of guard

public class TestAutomation {

    private static int n = 30; // number of tests to perform
    private static String filename = "data_20_guard.csv"; // dump location
    private static GUI.Game game;

    public static void main(String[] args) {

        try (PrintWriter writer = new PrintWriter(new File(filename))) {
            StringBuilder sb = new StringBuilder();
            sb.append("Map name, # of guards, # of intruders, Duration, Intruders captured, Type of a guard, Winner \n");

            for(int i = 0; i < n; i++){
                Main.main(args);
                sb.append(
                        Main.getMapPath()).append(", ").
                        append(game.getGameMap().getGameSettings().getNumGuards()).append(", ").
                        append(game.getGameMap().getGameSettings().getNumIntruders()).append(", ").
                        append(game.getNumberOfTurns()).append(", ").
                        append(game.getIntruders().stream().filter(IntruderContainer::isCaptured).count()).append(", ").
                        append(game.getGuards().get(0).getAgent().getClass()).append(", ").append(game.getWinner()).
                        append("\n");
            }

            writer.write(sb.toString());
        } catch (FileNotFoundException e) { System.out.println(e.getMessage()); }


    }

    public static void setGame(GUI.Game game) {
        TestAutomation.game = game;
    }

}
