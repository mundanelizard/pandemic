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

    static final int BOARD_STATE_DISEASE_CUBE_INDEX = 1;
    static final int BOARD_STATE_PAWN_CUBE_INDEX = 2;
    static final int BOARD_STATE_STATION_CUBE_INDEX = 0;
    /**
     * Coding
     * x - is the city
     * state[x][0] = station
     * state[x][1] = pawn
     * state[x][2] = disease cube
     */
    private int[][][] boardState;
    private int outbreakMarkerState = 0;
    /**
     * 0 - inactive
     * 1 - cure found
     * 2 - eradicated
     */
    private int[] cureIndicatorState = new int[4];
    /**
     * Infection Rate Marker 0 - 7;
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

    private ArrayList<PlayerCard> playerCards = new ArrayList<>();
    private int playerCardIndex = 0;

    private ArrayList<InfectionCard> infectionCards = new ArrayList<>();
    private int infectionCardIndex = 0;

    private ArrayList<Player> players = new ArrayList<>();
    private int turn = 0;

    private ArrayList<City> cities = new ArrayList<>();
    private ArrayList<Cube> cubes = new ArrayList<>();
    private ArrayList<Station> stations = new ArrayList<>();

    private boolean running = true;
    private boolean failed = false;
    private String status = "";

    State(boolean init) throws Exception {
        if (!init) return;
        initialise();
    }

    State() throws Exception {
        this(true);
    }

    public State deepClone() throws Exception {
        var state = new State();

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
        state.stations = Station.getStations();

        state.running = running;
        state.failed = failed;
        state.status = status;

        return state;
    }

    private void initialise() throws Exception {
        cities = Loader.loadCityGraph();
        infectionCards = Loader.loadInfectionCards();
        playerCards = Loader.loadPlayerCards();
        boardState = Loader.loadEmptyBoardState();
        cubes = Loader.loadCubes();
        stations = Loader.loadStations();

        // Places a research station in Atlanta
        initialiseStation();
        outbreakMarkerState = 0;
        Arrays.fill(cureIndicatorState, 0);

        players = Loader.loadPlayers();
        initialisePawns();

        Utils.shuffle(playerCards);
        Utils.shuffle(infectionCards);

        dealPlayersCardsToPlayer();
        dealInfectionCardAndInfectionCities();

        running = true;
    }

    private void dealInfectionCardAndInfectionCities() throws Exception {
        System.out.println();
        System.out.println("* Initial infection deal");

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

    private void initialisePawns() throws Exception {
        var atlanta = City.getCityByName("Atlanta");

        if (atlanta == null) {
            throw new Exception("Atlanta couldn't be found.");
        }

        for (var player : players) {
            placePawn(player, atlanta.getId());
        }
    }

    private void initialiseStation() throws Exception {
        var atlanta = City.getCityByName("Atlanta");

        if (atlanta == null) {
            throw new Exception("Atlanta couldn't be found.");
        }

        placeStation(atlanta.getId());
    }

    Player getCurrentPlayer() {
        return players.get(turn);
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

    private void setGameOver(String status) {
        this.status = status;
        this.running = false;
        this.failed = true;
    }

    public boolean isFailed() {
        return failed;
    }

    public void setRunning(boolean running) {
        this.running = running;
    }

    public boolean isRunning() {
        return running;
    }

    void increaseTurn() {
        turn = (turn + 1) % players.size();
    }

    /* Handling Actions */
    public void performAction(Option choice, int n) throws Exception {
        System.out.println("---");
        System.out.println("!Performing action " + choice.getName());

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
                handleBuildAResearchStation(player, choice.getSuit());
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
                // todo => set exit reason --
                setGameOver("Invalid player action choice " + choice.getType());
        }

        // todo => check if they won.

        if (!running) {
            return;
        }

        dealNPlayerCardsToPlayer(2);
        dealInfectionCardAndInfectCityForTurn();

        if (n != 4) return;

        increaseTurn();
        System.out.println("Player " + player.getName() + " has completed his turn");
    }

    private void handleShuttleFlight(Player player, int endCity) {
        placePawn(player, endCity);
        System.out.println("! Player " + player.getName() + " took a shuttle to #" + endCity);
    }

    private void handleDiscoverACure(Player player, int suit) throws Exception {
        player.removeNCardsOfSuit(Colour.values()[suit], 5);
        placeStation(player.getCity());
        cureIndicatorState[suit] = 1;
        System.out.println("! Player " + player.getName() + " discovered cure for " + Colour.values()[suit]);
    }

    private void handleTreatDiseaseRemoveOneCube(Player player, int suit) throws Exception {
        var cubesOnBoard = Utils.getItemsOnBoard(boardState, State.BOARD_STATE_DISEASE_CUBE_INDEX, cubes, player.getCity());

        for (var cube : cubesOnBoard) {
            if (cube.getColour().ordinal() != suit) continue;

            removeCube(cube, player.getCity());
            break;
        }

        resolveEradication(suit);
        System.out.println("! Player " + player.getName() + " removed a disease cube from #" + player.getCity());
    }

    private void resolveEradication(int suit) {
        int count = 0;
        for (int i = 0; i < boardState.length; i++) {
            count += Utils.getItemsOnBoard(boardState, State.BOARD_STATE_DISEASE_CUBE_INDEX, cubes, i).size();
        }

        if (count != 0) return;

        System.out.println("** Yay! Eradicated disease " + Colour.values()[suit]);
        // mark the disease as eradicated.
        cureIndicatorState[suit] = 2;
    }

    private void handleTreatDiseaseRemoveAll(Player player, int suit) throws Exception {
        var cubesOnBoard = Utils.getItemsOnBoard(boardState, State.BOARD_STATE_DISEASE_CUBE_INDEX, cubes, player.getCity());

        for (var cube : cubesOnBoard) {
            if (cube.getColour().ordinal() != suit) continue;

            removeCube(cube, player.getCity());
        }

        resolveEradication(suit);
        System.out.println("! Player " + player.getName() + " removed all disease cubes in #" + player.getCity());
    }

    private void handleBuildAResearchStation(Player player, int suit) throws Exception {
        var cards = player.getHand();

        int count = 0;
        for (var card : cards) {
            if (count == 5) break;
            if (card.getColour() != Colour.values()[suit])
                continue;

            cards.remove(card);
            count ++;
        }

        if (count != 5){
            running = false;
            setGameOver("Card count not enough to create research station you have " + count + " cards");
        }

        placeStation(player.getCity());
        System.out.println("! Player " + player.getName() + " built a research station in #" + player.getCity());
    }

    private void handleCharterFlight(Player player, int cardIndex, int endCity) {
        var card = player.removeCard(cardIndex);

        if (card.getCity() != player.getCity()) {
            running = false;
            setGameOver("Can't charter flight to city " + player.getCity() + " with card #" + card.getCity());
            return;
        }

        placePawn(player, endCity);
        System.out.println("! Player " + player.getName() + " chartered flight to #" + endCity);
    }

    private void handleDirectFlight(Player player, int cardIndex, int endCity) {
        var card = player.removeCard(cardIndex);

        if (card.getCity() != endCity) {
            running = false;
            setGameOver("Can't direct fly with card " + card.getCity() + " to city " + endCity);
            return;
        }

        placePawn(player, endCity);

        System.out.println("! Player " + player.getName() + " flew directly #" + endCity);
    }

    private void handleDriveOrFerry(Player player, int city) {
        placePawn(player, city);
    }

    private void handleTransferCard(Player player, Player endPlayer, int cardIndex) {
        var card = player.removeCard(cardIndex);
        endPlayer.addCard(card);
        System.out.println("! Transferred card [" + card.getCity() + ", " +  card.getType() + "] to " + endPlayer.getName());
    }

    public void dealPlayersCardsToPlayer() {
        for (var player : players) {
            var dealCount = 2;

            if (players.size() == 2) {
                dealCount = 4;
            } else if (players.size() == 3) {
                dealCount = 3;
            }

            System.out.println();
            System.out.println("* Dealing " + dealCount + " cards to " + player.getName());
            for (int i = 0; i < dealCount; i++) {
                player.addCard(dealPlayerCard());
            }
        }
    }

    public void dealNPlayerCardsToPlayer(int dealCount) throws Exception {
        var player = getCurrentPlayer();
        System.out.println();
        System.out.println("* Dealing " + dealCount + " cards to " + player.getName());

        for (int i = 0; i < dealCount; i++) {
            var card = dealPlayerCard();

            if (card.getType() != Card.Epidemic) {
                player.addCard(card);
                continue;
            }

            System.out.println("An got dealt an epidemic card. Resolving epidemic...");
            resolveEpidemic();
            System.out.println("Epidemic Resolved");

            if (!running) {
                return;
            }
        }
    }

    private void resolveEpidemic() throws Exception {
        // todo => increase the infection rate marker
        infectionRateMarkerState += 1;

        dealInfectionCardAndInfectCity(3);

        if (!running) {
            return;
        }

        System.out.println();
        System.out.println("**Reshuffling infection cards deck.");
        Utils.shuffle(infectionCards, infectionCardIndex);
        infectionCardIndex = 0;
    }

    public PlayerCard dealPlayerCard() {
        var playerCard = playerCards.get(playerCardIndex);
        System.out.println("** Dealing: dealing card #" + playerCard.getCity() + " of type " + playerCard.getType() + " of colour " + playerCard.getColour() );
        playerCardIndex += 1;
        return playerCard;
    }

    public void dealInfectionCardAndInfectCityForTurn()  throws Exception {
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

    public void dealInfectionCardAndInfectCity(int n) throws Exception {
        var card = dealInfectionCard();
        var city = cities.get(card.getId());
        var suit = city.getColour();

        placeNCubesInCity(city, suit, n);
    }

    private void resolveOutbreak(City city, Colour suit) throws Exception {
        System.out.println();
        System.out.println("* An outbreak occurred of disease " + suit + "  Resolving outbreak in city " + city.getName() + " #" + city.getId());
        outbreakMarkerState += 1;

        if (outbreakMarkerState >= 8) {
            running = false;
            setGameOver("Exceeded maximum numbers of outbreaks allowed.");
            return;
        }

        for (var neighbour : city.getNeighbours()) {
            placeNCubesInCity(cities.get(neighbour), suit, 1);

            if (!running) {
                return;
            }
        }
    }


    public void placeNCubesInCity(City city, Colour suit, int n) throws Exception {
        System.out.println("** Infecting: added " + n + " " + suit + " cubes on " + city.getName() + " #" + city.getId() );
        var cubesOfSuitOnBoard = 0;


        for(var cube : cubes) {
            if (cube.getCity() != city.getId()) continue;
            if (cube.getColour() != suit) continue;
            cubesOfSuitOnBoard += 1;
        }

        // fulling infections rules
        int numberOfCubesToAdd = n - cubesOfSuitOnBoard;

        for (int i = 0; i < numberOfCubesToAdd; i++) {
            var cube = Cube.getEmptyCube(cubes, suit);

            if (cube == null) {
               running = false;
                setGameOver("Out of cubes of the colour " + suit);
               return;
            }

            placeCube(cube, city.getId());
        }

        var totalCubes = cubesOfSuitOnBoard + n;
        if (totalCubes > 3) {
            resolveOutbreak(city, suit);
        }
    }

    public void removeCube(Cube cube, int cityId) throws Exception {
        cube.setCity(-1);
        Utils.remove(boardState, cityId, State.BOARD_STATE_DISEASE_CUBE_INDEX, cube.getId());
    }

    public void placeCube(Cube cube, int cityId) throws Exception {
        cube.setCity(cityId);
        Utils.insert(boardState, cityId, State.BOARD_STATE_DISEASE_CUBE_INDEX, cube.getId());
    }

    public void placePawn(Player player, int city) {
        player.setCity(city);
        Utils.insert(boardState, city, State.BOARD_STATE_PAWN_CUBE_INDEX, player.getPawn());
    }

    public void placeStation(int city) throws Exception {
        var station = Station.getEmptyStation(stations);

        if (station == null) {
            throw new Exception("Out of stations>");
        }

        station.setCity(city);
        Utils.insert(boardState, city, State.BOARD_STATE_STATION_CUBE_INDEX, station.getId());
    }

    public InfectionCard dealInfectionCard() {
        var infectionCard = infectionCards.get(infectionCardIndex);
        infectionCardIndex += 1;
        return infectionCard;
    }


    /* Building Options */

    public ArrayList<Option> getAllPossibleActions() {
        var actions = new ArrayList<Option>();

        loadTreatAndEradicateDiseaseOptions(actions);

        loadFerryOptions(actions);

        loadBuildAResearchStationAndDirectFlightOptions(actions);

        var  isStationInCity = Utils.getItemsOnBoard(boardState, State.BOARD_STATE_STATION_CUBE_INDEX, stations, getCurrentPlayerCity().getId()).size() > 0;

        if (!isStationInCity) {
            return actions;
        }

        // only load this when there is a research station in the city.
        loadDiscoverCureOptions(actions);

        loadShuttleFlightOptions(actions);

        return actions;
    }

    private void loadShuttleFlightOptions(ArrayList<Option> actions) {
        var cityId = getCurrentPlayerCity().getId();

        for (var station : stations) {
            if (station.getCity() == cityId || station.getCity() == -1) continue;
            var cityName = cities.get(station.getCity()).getName();
            var option = new Option("Take a shuttle flight to " + cityName + " research station");
            option.setEndCity(station.getCity());
            option.setType(OptionType.ShuttleFlight);
            actions.add(option);
        }
    }

    private void loadDiscoverCureOptions(ArrayList<Option> actions) {
        // check if you can discover a cure if the players i greater than 20
        var cures = getDiseaseCures(getCurrentPlayer().getHand());

        for (var cure : cures) {
            // don't display the disease if it has been cured
            if (cureIndicatorState[cure] == 1) continue;

            var option = new Option("Cure disease of colour " + Colour.values()[cure]);
            option.setSuit( cure);
            option.setType(OptionType.DiscoverACure);
            actions.add(option);
        }
    }

    private void loadTreatAndEradicateDiseaseOptions(ArrayList<Option> actions) {
        var city = getCurrentPlayerCity();
        var cubesInCity = Utils.getItemsOnBoard(boardState, State.BOARD_STATE_DISEASE_CUBE_INDEX, cubes, city.getId());
        var insertedCubeSuits = new boolean[6];

        for (var cube : cubesInCity) {
            var colour = cube.getColour().ordinal();
            if (insertedCubeSuits[colour]) continue;

            insertedCubeSuits[colour] = true;
            var curred = cureIndicatorState[city.getColour().ordinal()];

            // you can remove a disease cube from the board or all if it has been cured.
            // if it is the last cube of a curred disease it is eradicated

            Option option;
            if (curred == 1) {
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

    private void loadFerryOptions(ArrayList<Option> actions) {
        var neighbours = getCurrentPlayerCity().getNeighbours();

        for (var city : neighbours) {
            // you can move via ferry to any city you are connected to
            var name = cities.get(city).getName();
            var option = new Option("Move by ferry to " + name);
            option.setEndCity(city);
            option.setType(OptionType.DriveOrFerry);
            actions.add(option);
        }
    }

    private   void loadBuildAResearchStationAndDirectFlightOptions(ArrayList<Option> actions) {
        var player = getCurrentPlayer();

        for (var card : player.getHand()) {
            if (card.getCity() == -1) continue;

            var city = cities.get(card.getCity());
            var cityName = city.getName();

            var cardIndex = playerCards.indexOf(card);

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
                var option = new Option("Dispose [" + card.getCity() + ", " + cityName + ", " + card.getColour() + "] to teleport to " + cityName);
                option.setType(OptionType.DirectFlight);
                option.setDisposeCard(cardIndex);
                option.setEndCity(card.getCity());
                actions.add(option);
            }

            // you can transfer card to other players.
            buildOptionsToTransferCardToPlayers(actions, players, cardIndex, player.getPawn());
        }
    }

    private void buildOptionsToTransferCardToPlayers(ArrayList<Option> actions, ArrayList<Player> players, int cardIndex, int pawn) {
        for(var player : players) {
            if (player.getPawn() == pawn) continue;

            var option = new Option("Transfer card to player [" + player.getName() + "]");
            option.setType(OptionType.TransferCard);
            option.setEndPlayer(player.getPawn());
            option.setDisposeCard(cardIndex);
            actions.add(option);
        }
    }


    private ArrayList<Integer> getDiseaseCures(ArrayList<PlayerCard> cards) {
        var cures = new ArrayList<Integer>();

        if (cards.size() < 5) {
            return cures;
        }

        var map = new int[Colour.values().length];

        for (var card : cards) {
            // counting the number of cards of the same colour
            var colour = card.getColour();
            map[colour.ordinal()] += 1;
        }

        for (var i = 0; i < map.length; i++) {
            // filter colour that are up to five.
            if (map[i] < 5) continue;
            cures.add(i);
        }

        return cures;
    }

    private void buildOptionsToFlyToAllCities(ArrayList<Option> actions, PlayerCard card, int cardIndex, int currentCity) {
        var cardCity = cities.get(card.getCity());
        var cardCityName = cardCity.getName();

        for (var city: cities) {
            if (city.getId() == currentCity) continue;

            var option = new Option("Dispose [" + card.getCity() + ", " + cardCityName + ", " + card.getColour() + "] to teleport to " + city.getName());
            option.setDisposeCard(cardIndex);
            option.setEndCity(city.getId());
            option.setType(OptionType.CharterFlight);
            actions.add(option);
        }
    }

}
