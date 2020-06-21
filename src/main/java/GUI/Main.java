package GUI;

import GUI.map.parser.Parser;
import Group3.TestAutomation;


public class Main {

    private static Game game;
    private static String mapPath = "./src/main/java/Group3/MapLevels/complexity8.map";

    public static void main(String[] args) {

        game = new Game(Parser.parseFile(mapPath), new Group3.IAgentsFactoryGroup3(), false);
        game.run();
        TestAutomation.setGame(game);
        System.out.printf("The winner is: %s\n", game.getWinner());

    }

    public Game getGame() {
        return game;
    }
    public static String getMapPath() { return mapPath; }

}
