package GUI;

import GUI.agent.factories.DefaultAgentFactory;
import GUI.map.parser.Parser;


public class Main {

    public static void main(String[] args) {

        Game game = new Game(Parser.parseFile("./src/main/java/GUI/map/maps/test_2.map"), new DefaultAgentFactory(), false);
        game.run();
        System.out.printf("The winner is: %s\n", game.getWinner());

    }


}
