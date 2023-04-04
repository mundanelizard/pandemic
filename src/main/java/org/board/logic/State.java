package org.board.logic;

import org.board.entities.*;
import org.board.enumerables.Card;
import org.board.enumerables.Colour;
import org.board.enumerables.OptionType;
import org.board.utils.Loader;
import org.board.utils.Utils;

import java.util.ArrayList;
import java.util.Arrays;

public class State {
    /* Indexes for board state */
    static final int BOARD_STATE_DISEASE_CUBE_INDEX = 1;
    static final int BOARD_STATE_PAWN_CUBE_INDEX = 2;
    static final int BOARD_STATE_STATION_CUBE_INDEX = 0;

    /**
     * Stores all items on the board. It represents the board state.
     * x - is the city
     * state[x][0] = station
     * state[x][1] = pawn
     * state[x][2] = disease cube
     */
    private int[][][] boardState;

    /* Marker for the outbreak state from 0 to 8. It is greater than 8, the game ends */
    private int outbreakMarkerState = 0;

    public boolean debug = true;

    private int epidemics = 0;
    /**
     * 0 - inactive
     * 1 - cure found
     * 2 - eradicated
     */
    private int[] cureIndicatorState = new int[4];

    /**
     * Infection Rate Marker 0 - 7; this is below the infection deck on the board.
     * ----------------------
     * 0. infection rate is 2
     * 1. infection rate is 2
     * 2. infection rate is 2
     * 3. infection rate is 3
     * 4. infection rate is 3
     * 5. infection rate is 4
     * 6. infection rate is 4
     */
    private int infectionRateMarkerState = 0;

    /* Represents all the player cards on the board */
    private ArrayList<PlayerCard> playerCards = new ArrayList<>();

    /* The index in the player cards deck, we shift the marker instead of removing cards. */
    private int playerCardIndex = 0;

    /* Represents all the infection card on the board */
    private ArrayList<InfectionCard> infectionCards = new ArrayList<>();

    /* The index in the infections cards deck, we shift the marker instead of removing cards. */
    private int infectionCardIndex = 0;

    /* Players in the game - the last player is always an agent (maximum of 4 players) */
    private ArrayList<Player> players = new ArrayList<>();

    /* Keeps track of the current player */
    private int turn = 0;

    /* All the cities in the game (and on the board) */
    private ArrayList<City> cities = new ArrayList<>();

    /* All the cubes in the game (note: not on the board) */
    private ArrayList<Cube> cubes = new ArrayList<>();

    /* All the stations in the game (note: not on the board) */
    private ArrayList<Station> stations = new ArrayList<>();

    /* Game End States*/

    /* Keeps track of if the game is running */
    private boolean running = true;

    /* Keeps track of the game ended because of the game edge case (see documentation) */
    private boolean failed = false;

    /* Reason of exist if it was a failed state (game over) */
    private String status = "";

    /**
     * Create a new game state and gives you the option to choose to get IO input.
     * @param init chose if you want it to initialise or not (default is yes - see other constructor)
     * @throws Exception an exception in the case of an IO error;
     */
    State(boolean init) throws Exception {
        // you can choose if you want to initialise state at creation from IO;
        if (!init) return;
        initialise();
    }

    /**
     * Creates a new game state and asks the user for the IO input.
     * @throws Exception an exception in the case of an IO error.
     */
    State() throws Exception {
        this(true);
    }

    /* Initializers - initialises member variables */

    /**
     * Initialises the game state from IO.
     * @throws Exception IO error
     */
    private void initialise() throws Exception {
        cities = Loader.loadCityGraph();
        infectionCards = Loader.loadInfectionCards();
        playerCards = Loader.loadPlayerCards();
        boardState = Loader.loadEmptyBoardState();
        cubes = Loader.loadCubes();
        stations = Loader.loadStations();

        // places a research station in Atlanta
        initialiseStation();
        outbreakMarkerState = 0;
        Arrays.fill(cureIndicatorState, 0);

        players = Loader.loadPlayers();
        initialisePawns();

        dealPlayersCardsToPlayer();
        dealInfectionCardAndInfectionCities();

        running = true;
    }

    /**
     * Initialise pawns to their default locations on the board.
     */
    private void initialisePawns() {
        var atlanta = City.getCityByName(cities, "Atlanta");

        if (atlanta == null) {
           setGameOver("Atlanta couldn't be found.");
           return;
        }

        // placing all the pawn on atlanta
        for (var player : players) {
            placePawn(player, atlanta.getId());
        }
    }

    /**
     * Initialises the research station at Atlanta
     * @throws Exception when "Atlanta" doesn't exist.
     */
    private void initialiseStation() throws Exception {
        // getting the city from the list of cities
        var atlanta = City.getCityByName(cities, "Atlanta");

        if (atlanta == null) {
            setGameOver("Atlanta couldn't be found.");
            return;
        }

        // placing the station on atlanta
        placeStation(atlanta.getId());
    }

    /* Getters - get variables and perform filtering */

    public boolean isFailed() {
        return failed;
    }

    public boolean isRunning() {
        return running;
    }

    public Player getCurrentPlayer() {
        return players.get(turn);
    }

    public int getOutbreakMarkerState() {
        return outbreakMarkerState;
    }

    public ArrayList<Player> getPlayers() {
        return players;
    }

    public City getCurrentPlayerCity() {
        return cities.get(getCurrentPlayer().getCity());
    }

    public String getStatus() {
        return status;
    }

    public int getEpidemics() {
        return epidemics;
    }

    public int getTurn() {
        return turn;
    }

    public int[] getCureIndicatorState() {
        return cureIndicatorState;
    }

    /**
     * Gets all the suits for a players hand that is curable based on available cards.
     * @param cards the list of cards available to the player.
     * @return the suit index of each curable disease.
     */
    private ArrayList<Integer> getCurableDiseasesSuits(ArrayList<PlayerCard> cards) {
        var cures = new ArrayList<Integer>();

        // if the cards are less than five, you can't cure a disease.
        if (cards.size() < 5) {
            return cures;
        }

        // an int map to help keep track of available cards and their suits.
        var map = new int[Colour.values().length];

        for (var card : cards) {
            var colour = card.getColour();

            // skipping over non colour based cards (epidemics);
            if (colour == Colour.Invalid) continue;

            // counting the number of cards of the same colour.
            map[colour.ordinal()] += 1;
        }

        for (var i = 0; i < map.length; i++) {
            // filter colour that are up to five.
            if (map[i] < 5) continue;

            // add only suits that are greater than 5.
            cures.add(i);
        }

        return cures;
    }

    public int getInfectionRateMarkerState() {
        return infectionRateMarkerState;
    }

    /**
     * Gets all the counts of all the research station on board.
     * @return the total research station on board.
     */
    public int getResearchStationsCount() {
        var count = 0;

        for(var station : stations) {
            if (station.empty()) continue;
            count += 1;
        }

        return count;
    }

    /**
     * Generates a list of all the possible actions for a players turn taking into a count their position on the board.
     * @return the possible actions for the current player turn.
     */
    public ArrayList<Option> getAllPossibleActions() {
        var actions = new ArrayList<Option>();

        loadTreatAndEradicateDiseaseOptions(actions);

        loadFerryOptions(actions);

        loadBuildAResearchStationAndDirectFlightOptions(actions);

        // checking if there is a research station in the current player location.
        var  isStationInCity = Utils.getItemsOnBoard(boardState, State.BOARD_STATE_STATION_CUBE_INDEX, stations, getCurrentPlayerCity().getId()).size() > 0;

        if (!isStationInCity) {
            return actions;
        }

        // load options to discover a cure and shuttle a flight only if there is a research station in user location.
        loadDiscoverCureOptions(actions);

        loadShuttleFlightOptions(actions);

        return actions;
    }

    /**
     * Gets the total count of free cubes on the board.
     * @return count of free cube son the board
     */
    public int getFreeCubesCount() {
        int count = 0;

        for (var cube : cubes) {
            if (cube.getCity() == -1) continue;
            count += 1;
        }

        return count;
    }

    /**
     * Get count of all the cubes on the board.
     * @return cubes on board count.
     */
    public int getCubesOnBoardCount() {
        return cubes.size() - getFreeCubesCount();
    }

    /* Setters - updates state variables */

    /**
     * Sets game over state by setting the status string, running to false and failed to true
     * @param status the reason why the game is over.
     */
    private void setGameOver(String status) {
        this.status = status;
        this.running = false;
        this.failed = true;
    }

    /**
     * Increase the current turn by 1
     */
    private void increaseTurn() {
        turn = (turn + 1) % players.size();
    }

    /* Handling Actions */

    /**
     * Performs the action given on the current player and deals card if it is the last action.
     * @param choice the choice of action to take.
     * @param actionCount the number of actions that has been taken.
     * @throws Exception if an invalid action was taken.
     */
    public void performAction(Option choice, int actionCount) throws Exception {
        if (debug) {
            System.out.println("---");
            System.out.println("- - Performing action " + choice.getName());
        }

        // skips performing the action if the game has ended.
        if (!running) {
            return;
        }

        var player = getCurrentPlayer();

        switch (choice.getType()) {
            case TransferCard:
                handleTransferCard(player, players.get(choice.getEndPlayer()), choice.getDisposeCard());
                break;
            case DriveOrFerry:
                handleDriveOrFerry(player, choice.getEndCity());
                break;
            case DirectFlight:
                handleDirectFlight(player, choice.getDisposeCard(), choice.getEndCity());
                break;
            case CharterFlight:
                handleCharterFlight(player, choice.getDisposeCard(), choice.getEndCity());
                break;
            case BuildResearchStation:
                handleBuildAResearchStation(player, choice.getDisposeCard());
                break;
            case TreatDiseaseRemoveOneCube:
                handleTreatDiseaseRemoveOneCube( player,  choice.getSuit());
                break;
            case TreatDiseaseRemoveAll:
                handleTreatDiseaseRemoveAll(player, choice.getSuit());
                break;
            case DiscoverACure:
                handleDiscoverACure(player, choice.getSuit());
                break;
            case ShuttleFlight:
                handleShuttleFlight(player, choice.getEndCity());
                break;
            case Invalid:
            default:
                setGameOver("Invalid player action choice " + choice.getType());
        }

        // check if the action perform lead to a win.
        checkWin();

        // skips dealing card and other process if the game isn't running
        if (!running) {
            return;
        }

        // skip the action if the count is not 4
        if (actionCount != 4) return;

        // deals a new card to the player, infect cities and increase the players turn.
        dealNPlayerCardsToPlayer(2);
        dealInfectionCardAndInfectCityForTurn();
        increaseTurn();

        if (debug)
            System.out.println("Player " + player.getName() + " has completed his turn");
    }

    /* Handlers - handles game actions */

    /**
     * Handles direct shuttle flight from player location to location x. This assumes that the player is in a city with a research station,
     * moving to another city with a research station.
     * @param player the player that wants to take a direct shuttle.
     * @param endCity the city the player wants to end up in.
     */
    private void handleShuttleFlight(Player player, int endCity) {
        placePawn(player, endCity);
        if (debug)
            System.out.println("! Player " + player.getName() + " took a shuttle to #" + endCity);
    }

    /**
     * Handles discovering a cure for a disease of a particular suit. It assumes that the player is at a research station
     * and has the right amount of curds(5) to create a cure for the disease. It doesn't take into account player roles.
     * @param player the player that has the cards to cure the disease
     * @param suit the suit of card to cure disease.
     * @throws Exception when the suit of card is invalid or user doesn't have sufficient card.
     */
    private void handleDiscoverACure(Player player, int suit) throws Exception {
        // removes 5 cards of a particular suit from the player hand
        player.removeNCardsOfSuit(Colour.values()[suit], 5);

        // removing one from the suit because the Colour enum has an extra invalid colour
        var suitIndexInIndicator = suit - 1;
        // updating the cure state according the coding defined during creation.
        cureIndicatorState[suitIndexInIndicator] = 1;

        if (debug)
            System.out.println("! Player " + player.getName() + " discovered cure for " + Colour.values()[suit]);
    }

    /**
     * Treat a disease by remove only one cube from the board in the player's location.
     * @param player the player that is treat the disease
     * @param suit the suit of cube to remove.
     */
    private void handleTreatDiseaseRemoveOneCube(Player player, int suit) {
        // getting all the cubes on the board at that city
        var cubesOnBoard = Utils.getItemsOnBoard(boardState, State.BOARD_STATE_DISEASE_CUBE_INDEX, cubes, player.getCity());

        for (var cube : cubesOnBoard) {
            // check the colour of the suit
            if (cube.getColour().ordinal() != suit) continue;

            // removing only the cubes that matches the suit.
            removeCube(cube);
            break;
        }

        // check if by removing the cube, it eradicated the disease.
        resolveEradication(suit);

        if (debug)
            System.out.println("! Player " + player.getName() + " removed a disease cube from #" + player.getCity());
    }

    /**
     * Treat disease and remove all from the current city. Only works for cured diseases. It assumes all the conditions
     * are met.
     * @param player the player that wants to remove the disease
     * @param suit the suit of disease to remove.
     */
    private void handleTreatDiseaseRemoveAll(Player player, int suit) {
        // all the cubes on the board at that city
        var cubesOnBoard = Utils.getItemsOnBoard(boardState, State.BOARD_STATE_DISEASE_CUBE_INDEX, cubes, player.getCity());

        for (var cube : cubesOnBoard) {
            if (cube.getColour().ordinal() != suit) continue;

            // removing only cubes on the specified suit.
            removeCube(cube);
        }

        // check if by remove the cube, it eradicated the disease.
        resolveEradication(suit);

        if (debug)
            System.out.println("! Player " + player.getName() + " removed all disease cubes in #" + player.getCity());
    }

    /**
     * Builds a research station at the players location by discarding a card. It assumes the card you are discarding
     * matches the current player location, and it does valid the player city against the card.
     * @param player the player with the card
     * @param cardIndex the index of the card in the player hand
     * @throws Exception when the card city doesn't match the player city.
     */
    private void handleBuildAResearchStation(Player player, int cardIndex) throws Exception {
        // removes card from player hand
        var card = player.removeCard(cardIndex);

        // checks if the card and player city are the same.
        if (card.getCity() != player.getCity()) {
            setGameOver("Player city #" + player.getCity() + " doesn't match card city #" + card.getCity());
            return;
        }

        // places a station at the player city.
        placeStation(player.getCity());

        if (debug)
            System.out.println("! Player " + player.getName() + " built a research station in #" + player.getCity());
    }

    /**
     * Handles chartered flight from one city to another. It assumes the required edges are met and also validates it by
     * checking the card city and the player city are a match. It ends the game if flight isn't possible.
     * @param player the player with the card
     * @param cardIndex the index of th card in the players hand
     * @param endCity the city the player wants to end on.
     */
    private void handleCharterFlight(Player player, int cardIndex, int endCity) {
        // removing card from player hand
        var card = player.removeCard(cardIndex);

        if (card.getCity() != player.getCity()) {
            setGameOver("Can't charter flight to city " + player.getCity() + " with card #" + card.getCity());
            return;
        }

        // teleporting the player to the new location.
        placePawn(player, endCity);

        if(debug)
            System.out.println("! Player " + player.getName() + " chartered flight to #" + endCity);
    }

    /**
     * Handles direct flight by disposing a card that matches the end city. It assumes all the edge cases are met, and
     * it validates the edge case of the card city and the end city matching. It ends the game if they do not match.
     * @param player the player with the card
     * @param cardIndex the index of the card in the player's hand
     * @param endCity the city the player wants to end up in (ideally matching the card city).
     */
    private void handleDirectFlight(Player player, int cardIndex, int endCity) {
        // disposing card form the player hand
        var card = player.removeCard(cardIndex);

        // validating the end city and card city constraint.
        if (card.getCity() != endCity) {
            setGameOver("Can't direct fly with card " + card.getCity() + " to city " + endCity);
            return;
        }

        // placing the player at the new location.
        placePawn(player, endCity);

        if (debug)
            System.out.println("! Player " + player.getName() + " flew directly #" + endCity);
    }

    /**
     * Handles drive or ferry to nearby cities. It assumes all the edge cases are met, and it performs no further
     * validations on the player or end city.
     * @param player the player who want to move
     * @param city the city the player wants to end up.
     */
    private void handleDriveOrFerry(Player player, int city) {
        placePawn(player, city);
    }

    /**
     * Transfers card from player one to player two. There's no condition for this action.
     * @param player the player who want to transfer card.
     * @param endPlayer the player who is receiving the card.
     * @param cardIndex the index of the card in the initial player hand.
     */
    private void handleTransferCard(Player player, Player endPlayer, int cardIndex) {
        // removing the card from initial player
        var card = player.removeCard(cardIndex);
        // adding the card to the end player
        endPlayer.addCard(card);

        if (debug)
            System.out.println("! Transferred card [" + card.getCity() + ", " +  card.getType() + "] to " + endPlayer.getName());
    }


    /* Resolvers - resolves edge cases */

    /**
     * Checks if a disease of a particular cube has been eradicated and set the cure marker to 2
     * @param suit the disease cube to resolve eradication
     */
    private void resolveEradication(int suit) {
        // doesn't eradicate disease that hasn't been cured.
        if (cureIndicatorState[suit - 1] == 0)
            return;

        int count = 0;

        // gets the count of all the cubes of tha colour across all cities on the board.
        for (int i = 0; i < boardState.length; i++) {
            count += Utils.getItemsOnBoard(boardState, State.BOARD_STATE_DISEASE_CUBE_INDEX, cubes, i).size();
        }

        // doesn't eradicate disease if it is still on the board.
        if (count != 0) return;

        // mark the disease as eradicated.
        cureIndicatorState[suit - 1] = 2;

        if (debug)
            System.out.println("** Yay! Eradicated disease " + Colour.values()[suit]);

    }

    /**
     * Resolve epidemic by shuffling deck and resetting deck index counter.
     * @throws Exception when there aren't any infection card left.
     */
    private void resolveEpidemic() throws Exception {
        // increase epidemics count and infection rate marker.
        epidemics += 1;
        infectionRateMarkerState += 1;

        // deal card and infect cities skipping over cured disease cards.
        dealInfectionCardAndInfectCity(3, true);

        // return if the game ended in the process
        if (!running) {
            return;
        }

        if (debug) {
            System.out.println();
            System.out.println("**Reshuffling infection cards deck.");
        }

        // shuffle card and reset index.
        Utils.shuffle(infectionCards, infectionCardIndex);
        infectionCardIndex = 0;
    }

    /**
     * Resolves an outbreak based on the outbreak resolving rules (see documentation)
     * @param city the city to resolve outbreak from.
     * @param suit the colour of disease to resolve outbreak.
     * @throws Exception when we run out of cubes.
     */
    private void resolveOutbreak(City city, Colour suit) throws Exception {
        if (debug) {
            System.out.println();
            System.out.println("* An outbreak occurred of disease " + suit + "  Resolving outbreak in city " + city.getName() + " #" + city.getId());
        }

        outbreakMarkerState += 1;

        // check if outbreak mark is equal to 8
        if (outbreakMarkerState >= 8) {
            setGameOver("Exceeded maximum numbers of outbreaks allowed.");
            return;
        }

        for (var neighbour : city.getNeighbours()) {
            // placing cubes in surrounding cities.
            placeNCubesInCity(cities.get(neighbour), suit, 1);

            // checking if the game in still active.
            if (!running) {
                return;
            }
        }
    }

    /**
     * Checks if all the disease has been cured. If they've been cured it sate the running state to false.
     */
    private void checkWin() {
        int cures = 0;

        for (var cure : cureIndicatorState) {
            if (cure == 0) continue;
            cures += 1;
        }

        if (cures == cureIndicatorState.length) {
            running = false;
        }
    }


    /* Removing items from board */

    /**
     * Removes a cube from the board.
     * @param cube cube to remove from board.
     */
    public void removeCube(Cube cube) {
        remove(cube.getCity(), State.BOARD_STATE_DISEASE_CUBE_INDEX, cube.getId());
        cube.remove();
    }


    /* Dealers - Placing items on board */

    /**
     * Deals card to players according the number of players in the game.
     */
    public void dealPlayersCardsToPlayer() {
        for (var player : players) {
            var dealCount = 2;

            if (players.size() == 2) {
                dealCount = 4;
            } else if (players.size() == 3) {
                dealCount = 3;
            }

            if (debug) {
                System.out.println();
                System.out.println("* Dealing " + dealCount + " cards to " + player.getName());
            }

            for (int i = 0; i < dealCount; i++) {
                player.addCard(dealPlayerCard());
            }
        }
    }

    /**
     * Deals an infection card and increase the infection index.
     * @return a new infection card.
     */
    public InfectionCard dealInfectionCard() {
        var infectionCard = infectionCards.get(infectionCardIndex);
        infectionCardIndex += 1;
        return infectionCard;
    }

    /**
     * Deals n number of cards to the current player (this is mainly used at the end of a turn).
     * @param dealCount the number of cards to deal to player.
     * @throws Exception if we run out of player cards.
     */
    public void dealNPlayerCardsToPlayer(int dealCount) throws Exception {
        if (playerCardIndex >= playerCards.size()) {
            setGameOver("Ran out of player cards");
            return;
        }

        var player = getCurrentPlayer();
        if (debug) {
            System.out.println();
            System.out.println("* Dealing " + dealCount + " cards to " + player.getName());
        }

        for (int i = 0; i < dealCount; i++) {
            var card = dealPlayerCard();

            // only adds non-epidemic cards to the user
            if (card.getType() != Card.Epidemic) {
                player.addCard(card);
                continue;
            }

            // resolving epidemic card immediately

            if (debug)
                System.out.println("!!! An got dealt an epidemic card. Resolving epidemic...");

            resolveEpidemic();

            if (debug)
                System.out.println("!!! Epidemic Resolved");

            if (!running) {
                return;
            }
        }
    }

    /**
     * Dealing player card and increasing index.
     * @return a new player card.
     */
    public PlayerCard dealPlayerCard() {
        var playerCard = playerCards.get(playerCardIndex);

        if (debug)
            System.out.println("** Dealing: dealing card #" + playerCard.getCity() + " of type " + playerCard.getType() + " of colour " + playerCard.getColour() );

        playerCardIndex += 1;
        return playerCard;
    }

    /**
     * Dealing infection card and infecting city for the current layer turn.
     * @throws Exception if there isn't enough infection cards.
     */
    public void dealInfectionCardAndInfectCityForTurn()  throws Exception {
        // setting the number of infection cards to deal according to the infection marker state.
        var numberOfInfectionCards = 2;
        if (infectionRateMarkerState > 3) {
            numberOfInfectionCards = 3;
        }

        for (int i = 0; i < numberOfInfectionCards; i++) {
            dealInfectionCardAndInfectCity(1);
            if (!running) 
                return;
        }
    }

    /**
     * Deals initial infection cards.
     * @throws Exception if there isn't enough infection cards.
     */
    private void dealInfectionCardAndInfectionCities() throws Exception {
        if (debug) {
            System.out.println();
            System.out.println("* Initial infection deal");
        }

        dealInfectionCardAndInfectCity( 3);
        dealInfectionCardAndInfectCity(3);
        dealInfectionCardAndInfectCity(3);

        dealInfectionCardAndInfectCity(2);
        dealInfectionCardAndInfectCity(2);
        dealInfectionCardAndInfectCity(2);

        dealInfectionCardAndInfectCity(1);
        dealInfectionCardAndInfectCity(1);
        dealInfectionCardAndInfectCity(1);
    }

    /**
     * Deals a cards and infect the city n time without skipping cured diseases.
     * @param n the number of times to draw infection card.
     * @throws Exception when there isn't enough infection cards.
     */
    public void dealInfectionCardAndInfectCity(int n) throws Exception {
        dealInfectionCardAndInfectCity(n, false);
    }

    /**
     * Deals a card and infection cities n times with the option of skipping cured diseases.
     * @param n the number of time to draw infection card
     * @param skipCured option to skip curred cities.
     * @throws Exception when there isn't enough infection cards.
     */
    public void dealInfectionCardAndInfectCity(int n, boolean skipCured) throws Exception {
        if (infectionCardIndex >= infectionCards.size()) {
            setGameOver("Ran out of infection cards");
            return;
        }

        var card = dealInfectionCard();
        var city = cities.get(card.getId());
        var suit = city.getColour();

        // doesn't infect cities with cures
        if (skipCured && cureIndicatorState[suit.ordinal() -1] >= 1)
            return;

        // places n cubes of the card suit on the current city.
        placeNCubesInCity(city, suit, n);
    }

    /**
     * Places numberOfCubes cubes on in a city of a particular colour suit.
     * @param city city to place disease cubes
     * @param suit the suit of the disease
     * @param numberOfCubes the number of cubes to place
     * @throws Exception when there isn't any disease cube left.
     */
    public void placeNCubesInCity(City city, Colour suit, int numberOfCubes) throws Exception {
        if (debug)
            System.out.println("** Infecting: added " + numberOfCubes + " " + suit + " cubes on " + city.getName() + " #" + city.getId() );

        var cubesOfSuitOnBoard = 0;

        // counting the number of cubes of a particular suit on the board in that city.
        for(var cube : cubes) {
            if (cube.getCity() != city.getId()) continue;
            if (cube.getColour() != suit) continue;
            cubesOfSuitOnBoard += 1;
        }

        // fulling infections rules
        int numberOfCubesToAdd = numberOfCubes - cubesOfSuitOnBoard;

        for (int i = 0; i < numberOfCubesToAdd; i++) {
            // gets and empty cube
            var cube = Cube.getEmptyCube(cubes, suit);

            if (cube == null) {
                setGameOver("Out of cubes of the colour " + suit);
                return;
            }

            // places a cube on the board in the current city.
            placeCube(cube, city.getId());
        }

        // check if it more than 3 in total and create outbreak if it is.
        var totalCubes = cubesOfSuitOnBoard + numberOfCubes;
        if (totalCubes > 3) {
            resolveOutbreak(city, suit);
        }
    }

    /**
     * Places cube in a specific city.
     * @param cube cube to place on the board.
     * @param cityId the current city to place the cube.
     * @throws Exception fi there isn't enough cubes.
     */
    public void placeCube(Cube cube, int cityId) throws Exception {
        cube.setCity(cityId);
        insert(cityId, State.BOARD_STATE_DISEASE_CUBE_INDEX, cube.getId());
    }

    /**
     * Places a pawn on a city.
     * @param player player to move
     * @param city the city
     */
    public void placePawn(Player player, int city) {
        remove(player.getCity(), State.BOARD_STATE_PAWN_CUBE_INDEX, player.getPawn());
        player.setCity(city);
        insert(city, State.BOARD_STATE_PAWN_CUBE_INDEX, player.getPawn());
    }

    /**
     * Places a station at a particular location.
     * @param city the city.
     * @throws Exception when there isn't any stations left.
     */
    public void placeStation(int city) throws Exception {
        var station = Station.getEmptyStation(stations);

        if (station == null) {
            setGameOver("Out of stations.");
            return;
        }

        station.setCity(city);
        insert(city, State.BOARD_STATE_STATION_CUBE_INDEX, station.getId());
    }


    /* Builders - builds actions */

    /**
     * Loads a shuttle flight options.
     * @param actions list of actions.
     */
    private void loadShuttleFlightOptions(ArrayList<Option> actions) {
        var cityId = getCurrentPlayerCity().getId();

        for (var station : stations) {
            // station that matches the current city or isn't set on the board.
            if (station.getCity() == cityId || station.getCity() == -1) continue;

            // get the city name
            var cityName = cities.get(station.getCity()).getName();

            // add option to the list of actions.
            var option = new Option("Take a shuttle flight to " + cityName + " research station");
            option.setEndCity(station.getCity());
            option.setType(OptionType.ShuttleFlight);
            actions.add(option);
        }
    }

    /**
     * Loads all discover cure options.
     * @param actions a list of options
     */
    private void loadDiscoverCureOptions(ArrayList<Option> actions) {
        // check if you can discover a cure
        var suits = getCurableDiseasesSuits(getCurrentPlayer().getHand());

        for (var suit : suits) {
            // don't display the disease if it has been cured
            if (cureIndicatorState[suit - 1] == 1) continue;

            // builds new option.
            var option = new Option("Cure disease of colour " + Colour.values()[suit]);
            option.setSuit(suit);
            option.setType(OptionType.DiscoverACure);
            actions.add(option);
        }
    }

    /**
     * Loads all the treat and eradicate disease options.
     * @param actions list of actions.
     */
    private void loadTreatAndEradicateDiseaseOptions(ArrayList<Option> actions) {
        var city = getCurrentPlayerCity();
        var cubesInCity = Utils.getItemsOnBoard(boardState, State.BOARD_STATE_DISEASE_CUBE_INDEX, cubes, city.getId());
        var insertedCubeSuits = new boolean[6];

        for (var cube : cubesInCity) {
            // getting the colour of the inserted cubes.
            var colour = cube.getColour().ordinal();
            if (insertedCubeSuits[colour]) continue;

            // flag the colour as inserted to skip on the next encounter.
            insertedCubeSuits[colour] = true;
            var curred = cureIndicatorState[city.getColour().ordinal() - 1];

            // you can remove a disease cube from the board or all if it has been cured.
            // if it is the last cube of a curred disease it is eradicated

            // building the option.
            Option option;
            if (curred == 1) {
                // give the option to treat all if the disease has been cured.
                option = new Option("Cure disease (remove all cubes because it has been cured) [" + cube.getColour() + "]");
                option.setType(OptionType.TreatDiseaseRemoveAll);
            } else {
                option = new Option("Cure disease (remove one from cube) [" + cube.getColour() + "]");
                option.setType(OptionType.TreatDiseaseRemoveOneCube);
            }

            option.setSuit(colour);
            actions.add(option);
        }
    }

    /**
     * Load ferry options.
     * @param actions a list of options
     */
    private void loadFerryOptions(ArrayList<Option> actions) {
        // getting all the neighbours for hte player current city.
        var neighbours = getCurrentPlayerCity().getNeighbours();

        for (var city : neighbours) {
            // you can move via ferry to any city you are connected to
            var name = cities.get(city).getName();

            // building ferry option.
            var option = new Option("Move by ferry to " + name);
            option.setEndCity(city);
            option.setType(OptionType.DriveOrFerry);
            actions.add(option);
        }
    }

    /**
     * Loading build research station and direct flight
     * @param actions a list of options.
     */
    private   void loadBuildAResearchStationAndDirectFlightOptions(ArrayList<Option> actions) {
        var player = getCurrentPlayer();

        for (var card : player.getHand()) {
            if (card.getCity() == -1) continue;

            var city = cities.get(card.getCity());
            var cityName = city.getName();

            var cardIndex = player.getHand().indexOf(card);

            // check if the player has a card that matches his current city.
            if (card.getCity() == player.getCity()) {
                // you can discard card to move to any city.
                buildOptionsToFlyToAllCities(actions, card, cardIndex, player.getCity());
                // you can discard this card to build a research station
                var option = new Option("Dispose card [" + card.getCity() + ", " + cityName +  ", " + card.getColour() + "] to research station in current city " + cityName);
                option.setType(OptionType.BuildResearchStation);
                option.setDisposeCard(cardIndex);
                actions.add(option);
            } else if(card.getCity() != -1) {
                // you can discard card to fly directly to the city
                var option = new Option("Dispose [" + card.getCity() + ", " + cityName + ", " + card.getColour() + "] to direct fly to " + cityName);
                option.setType(OptionType.DirectFlight);
                option.setDisposeCard(cardIndex);
                option.setEndCity(card.getCity());
                actions.add(option);
            }

            // you can transfer card to other players.
            buildOptionsToTransferCardToPlayers(actions, cardIndex);
        }
    }

    /**
     * Builds all options to transfer card to other players.
     * @param actions the list of options
     * @param cardIndex the index of the cad in the player hand.
     */
    private void buildOptionsToTransferCardToPlayers(ArrayList<Option> actions, int cardIndex) {
        for(var player : players) {
            // skips current player for transfer options.
            if (player.getPawn() == getCurrentPlayer().getPawn()) continue;

            // builds option for each player
            var option = new Option("Transfer card to player [" + player.getName() + "]");
            option.setType(OptionType.TransferCard);
            option.setEndPlayer(player.getPawn());
            option.setDisposeCard(cardIndex);
            actions.add(option);
        }
    }

    /**
     * Builds the options to fly to tall cities
     * @param actions list of options
     * @param card the current card
     * @param cardIndex the index of the card in player hand
     * @param currentCity the current city.
     */
    private void buildOptionsToFlyToAllCities(ArrayList<Option> actions, PlayerCard card, int cardIndex, int currentCity) {
        var cardCity = cities.get(card.getCity());
        var cardCityName = cardCity.getName();

        for (var city: cities) {
            if (city.getId() == currentCity) continue;

            // building options of cities to fly to
            var option = new Option("Dispose [" + card.getCity() + ", " + cardCityName + ", " + card.getColour() + "] to fly to " + city.getName());
            option.setDisposeCard(cardIndex);
            option.setEndCity(city.getId());
            option.setType(OptionType.CharterFlight);
            actions.add(option);
        }
    }


    /* State Cloning */

    /**
     * Deep clones the state.
     * @return a new state.
     * @throws Exception if there isn't any infection card or disease cube.
     */
    public State deepClone() throws Exception {
        var state = new State(false);

        state.boardState = Utils.copy3dArray(boardState);

        state.outbreakMarkerState = outbreakMarkerState;
        state.cureIndicatorState = cureIndicatorState.clone();
        state.infectionRateMarkerState = infectionRateMarkerState;

        state.playerCards = PlayerCard.getCards(playerCards);
        state.playerCardIndex = playerCardIndex;

        state.infectionCards = InfectionCard.getCards(infectionCards);
        state.infectionCardIndex = infectionCardIndex;

        state.players = Player.getPlayers(players);
        state.turn = turn;

        state.cities = cities;
        state.cubes = Cube.getCubes(cubes);

        state.stations = Station.getStations(stations);

        state.running = running;
        state.failed = failed;
        state.status = status;

        state.epidemics = epidemics;

        return state;
    }

    /* Board State Manipulation */

    /**
     * Insert item into the board
     * @param cityId the id of the city
     * @param typeId the type of the item
     * @param itemId the id of the item
     */
    private void insert(int cityId, int typeId, int itemId) {
        for(int i = 0; i < boardState[cityId][typeId].length; i++) {
            if(boardState[cityId][typeId][i] != -1) continue;

            boardState[cityId][typeId][i] = itemId;
            return;
        }
    }

    /**
     * Removing item from the board
     * @param cityId current city
     * @param typeId item type
     * @param itemId the index of the item to remove.
     */
    private void remove(int cityId, int typeId, int itemId) {
        for(int i = 0; i < boardState[cityId][typeId].length; i++) {
            if (boardState[cityId][typeId][i] != itemId) continue;

            boardState[cityId][typeId][i] = -1;
        }
    }
}
