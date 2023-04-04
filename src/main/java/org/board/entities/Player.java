package org.board.entities;

import org.board.enumerables.Colour;
import org.board.enumerables.Role;

import java.util.ArrayList;

/**
 * Represents a player in the game.
 */
public class Player {
    /* list of players for initialisation */
    private final static ArrayList<Player> players = new ArrayList<>();

    /* the role of the user */
    final private Role role;

    /* the user pawn (this represents the user on the board) */
    final private int pawn;

    /* the name of the player */
    final private String name;

    /* the id of the city the player is located*/
    private int city;

    /* keeps tracks of all the cards in the player hand */
    final private ArrayList<PlayerCard> cards = new ArrayList<>();

    /**
     * Creates a new player
     * @param name name of the player
     * @param pawn the pawn id for the player
     * @param role role of the user
     */
    private Player(String name, int pawn, Role role) {
        this.name = name;
        this.pawn = pawn;
        this.role = role;
    }

    /**
     * Gets the username given at creation.
     * @return name assigned to the user
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the player pawn id
     * @return the user pawn.
     */
    public int getPawn() {
        return pawn;
    }

    /**
     * Current user role
     * @return role
     */
    public Role getRole() {
        return role;
    }

    /**
     * Gets all the cards assigned to players
     * @return list of cards
     */
    public ArrayList<PlayerCard> getHand() {
        return cards;
    }

    /**
     * Gets the player city
     * @return player city
     */
    public int getCity() {
        return city;
    }

    /**
     * Gets the players created during initial creation.
     * @return list of players
     */
    public static ArrayList<Player> getPlayers() {
        return players;
    }

    /**
     * Clones an array list of players.
     * @param players list of players
     * @return a clone list of players
     */
    public static ArrayList<Player> getPlayers(ArrayList<Player> players) {
        var newPlayers = new ArrayList<Player>();

        for(var player : players) {
            var newPlayer = new Player(player.name, player.pawn, player.role);
            newPlayer.city = player.city;
            newPlayer.cards.addAll(player.cards);
            newPlayers.add(newPlayer);
        }

        return newPlayers;
    }

    /**
     * Gets all roles that hasn't been assigned to a player.
     * @return a list of available roles.
     */
    public static ArrayList<String> getAvailableRoles() {
        Role[] roles = Role.values();
        var parsedRoles = new ArrayList<String>();

        // looping through all the roles to skip used roles
        for (Role role: roles) {
            var skip = false;

            // check if the role has been assigned to a player
            for (var player : players) {
                if (player.getRole() == role) {
                    skip = true;
                    break;
                }
            }

            // skipping the role if it is assigned
            if (skip) {
                continue;
            }

            // adding roles to parsed roles
            parsedRoles.add(role.toString());
        }

        return parsedRoles;
    }

    /**
     * Adds a card to the player hand
     * @param card card to add to the player hand.
     */
    public void addCard(PlayerCard card) {
        cards.add(card);
    }

    /**
     * Removes a card from the player hand
     * @param index the index of card to remove
     * @return the card removed
     */
    public PlayerCard removeCard(int index) {
        return cards.remove(index);
    }

    /**
     * Remove n cards of a particular suit from a players hand.
     * @param suit the colour of card
     * @param numberCards the number of card to remove
     * @throws Exception if the cards doesn't reach the required numbers
     */
    public void removeNCardsOfSuit(Colour suit, int numberCards) throws Exception {
        int count = 0;

        var hand = new ArrayList<>(cards);

        for (var card : hand) {
            if (count >= numberCards) break;
            if (card.getColour() != suit) continue;

            count += 1;
            cards.remove(card);
        }

        if (count != numberCards) {
            throw new Exception("Something terribly wrong happened couldn't get suit " + suit + " of count 5 instead got " + count);
        }
    }

    /**
     * Sets the city of the player
     * @param city city of the player
     */
    public void setCity(int city) {
        this.city = city;
    }

    /**
     * Adds a players to the players list
     * @param name name of the player
     * @param pawn the pawn for the player
     * @param role the role of the player
     * @throws Exception when the constraints on pawn ond roles fails.
     */
    public static void addPlayer(String name, int pawn, Role role) throws Exception {
        if (players == null) {
            throw new Exception("Players hasn't been instantiated");
        }

        // validate the constraints on roles
        validatePlayerRoleConstraints(role);
        validatePlayerPawnConstraints(pawn);

        if (name == null) {
            name = autoGenerateName();
        }

        var player = new Player(name, pawn, role);

        players.add(player);
    }

    /**
     * Auto generates a name for the user
     * @return autogenerated name "Player + n"
     */
    private static String autoGenerateName() {
        return "Player " + (players.size() + 1);
    }

    /**
     * Checks if the role is not in use.
     * @param role the role of the user.
     * @throws Exception when the user role is in use.
     */
    private static void validatePlayerRoleConstraints(Role role) throws Exception {
        for (Player player: players) {
            if (player.getRole() == role) {
                throw new Exception("This role has already been assigned to a user.");
            }
        }
    }

    /**
     * Checks if the pawn is not in use
     * @param pawn the pawn to validate.
     * @throws Exception when the pawn is used
     */
    private static void validatePlayerPawnConstraints(int pawn) throws Exception {
        for (Player player : players) {
            if (player.getPawn() == pawn) {
                throw new Exception("This pawn has already been assigned to a user.");
            }
        }
    }
}
