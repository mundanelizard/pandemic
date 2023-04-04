package org.board.utils;

import org.board.entities.*;
import org.board.enumerables.Card;
import org.board.enumerables.Colour;
import org.board.enumerables.Role;
import org.board.logic.Agent;

import java.io.File;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

/**
 * Handles loading game initial state.
 */
public class Loader {
    static Random random = new Random(20);
    static final private String FILENAME = "map.txt";
    static private ArrayList<City> cities = new ArrayList<>();

    /**
     * Loads users from shell input
     * @return a list of players
     * @throws Exception when there is an issue in creating a player
     */
    static public ArrayList<Player> loadPlayers() throws Exception {
        var players = IO.getPlayerCount();

        int playerIndex;
        // create the users.
        for (playerIndex = 0; playerIndex < players; playerIndex++) {
            var name = IO.getPlayerName(playerIndex);
            var roles = Player.getAvailableRoles();
            var role = roles.get(random.nextInt(roles.size() - 1));

            Player.addPlayer(name, playerIndex, Role.getRole(role));
        }

        // creating the agent
        System.out.println();
        System.out.println("Your Agent name is 'Rupert'");
        Player.addPlayer(Agent.NAME, playerIndex, Role.getRole(Player.getAvailableRoles().get(0)));
        System.out.println();

        return Player.getPlayers();
    }


    /**
     * Loads all the stations for the game.
     * @return a list of stations.
     */
    static public ArrayList<Station> loadStations() {
        var stations = Station.getStations();

        if (stations.size() == 6) {
            return stations;
        }

        for (int i = 0; i < 6; i++) {
            Station.addStation(i);
        }

        return Station.getStations();
    }

    /**
     * Loads all the disease cubes 96
     * @return a list of disease cubes
     */
    static public ArrayList<Cube> loadCubes()  {
        for (var colour : Colour.values()) {
            if (colour == Colour.Invalid) continue;

            var padding = (colour.ordinal() - 1) * 24;
            for (int i = 0; i < 24; i++) {
                var id = padding + i;
                Cube.addCube(id, colour);
            }
        }

        return Cube.getCubes();
    }

    /**
     * Initialise the board state and set everything to -1 representing empty.
     * @return an empty boards state.
     */
    static public int[][][] loadEmptyBoardState() {
        int[][][] boardState = new int[48][3][24];

        for (int i = 0; i < boardState.length; i++) {
            for (int j = 0; j < boardState[i].length; j++) {
                for (int k = 0; k < boardState[i][j].length; k++) {
                    boardState[i][j][k] = -1;
                }
            }
        }

        return boardState;
    }

    /**
     * Loads all the player cards
     * @return a list of player cards
     */
    static public ArrayList<PlayerCard> loadPlayerCards() {
        for (var city : cities) {
            PlayerCard.addCard(city.getId(), Card.Regular, city.getColour());
        }

        for (int i = 0; i < 4; i++) {
            PlayerCard.addCard(-1, Card.Epidemic, Colour.Invalid);
        }

        var playerCards = PlayerCard.getCards();

        // shuffles the cards according to the rules.
        shufflePlayerCards(playerCards);

        return playerCards;
    }

    private static void shufflePlayerCards(ArrayList<PlayerCard> playerCards) {
        var startIndex = playerCards.size() - 5;
        // shuffles first part excluding epidemic cards
        Utils.shuffle(playerCards, startIndex);

        // shuffles epidemic cards into early deck starting at 15
        for (int i = startIndex; i < playerCards.size(); i++) {
            var rand = random.nextInt(15, playerCards.size());
            var temp = playerCards.get(rand);
            playerCards.set(rand, playerCards.get(i));
            playerCards.set(i, temp);
        }
    }

    /**
     * Loads the infection deck required.
     * @return the empty infection deck
     */
    static public ArrayList<InfectionCard> loadInfectionCards() {
        // 48 cards representing each city
        for (var city : cities) {
            InfectionCard.addCard(city.getId());
        }

        var infectionCards = InfectionCard.getCards();
        Utils.shuffle(infectionCards);

        // loaded infection cards
        return infectionCards;
    }

    /**
     * Loads a city graph
     * @return a list of cities
     * @throws Exception when it fails ot load city grpah
     */
    static public ArrayList<City> loadCityGraph() throws Exception {
        var handle = new File(FILENAME);
        var reader = new Scanner(handle);

        loadCities(reader);
        loadConnections(reader);

        cities = City.getCities();

        return cities;
    }

    /**
     * Loads city from file scanner.
     * @param reader scanner with access to file.
     */
    private static void loadCities(Scanner reader) {
        for(int id = 0; reader.hasNextLine(); id++) {
            var line = reader.nextLine();
            if (line.equals("--")) break;

            var segments = line.split(";");

            var name = segments[0];
            var colour = segments[1];

            City.addCity(id, name, Colour.getColour(colour));
        }
    }

    /**
     * Loads connections for the city graph
     * @param reader scanner with access to the map file.
     * @throws Exception when it fails to connect cities together.
     */
    private static void loadConnections(Scanner reader) throws Exception {
        while(reader.hasNextLine()) {
            var line = reader.nextLine();
            if (line.equals("--")) break;

            var segments = line.split(";");

            var firstCity = segments[0];
            var secondCity = segments[1];

            City.connect(firstCity, secondCity);
        }
    }
}
