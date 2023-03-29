package org.board.entities;


import org.board.enumerables.Role;

import java.util.ArrayList;

public class Player {
    private static int loadedPlayers;
    private static ArrayList<Player> players = new ArrayList();

    // Role Cards
    final static private int TOTAL_ROLE_CARDS = 6;

    /* Member Variables */
    final private Role role;
    final private int pawn;
    final private String name;

    final private ArrayList<PlayerCard> cards = new ArrayList<>();

    private Player(String name, int pawn, Role role) {
        this.name = name;
        this.pawn = pawn;
        this.role = role;
    }

    public String getName() {
        return name;
    }

    public int getPawn() {
        return pawn;
    }

    public Role getRole() {
        return role;
    }

    public void addCard(PlayerCard card) {
        cards.add(card);
    }

    public PlayerCard removeCard(int index) {
        return cards.remove(index);
    }

    public ArrayList<PlayerCard> getHand() {
        return cards;
    }



    /* Static Methods */

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

    private static String autoGenerateName() {
        return "Player " + (players.size() + 1);
    }

    private static void validatePlayerRoleConstraints(Role role) throws Exception {
        for (Player player: players) {
            if (player.getRole() == role) {
                throw new Exception("This role has already been assigned to a user.");
            }
        }
    }

    private static void validatePlayerPawnConstraints(int pawn) throws Exception {
        for (Player player : players) {
            if (player.getPawn() == pawn) {
                throw new Exception("This pawn has already been assigned to a user.");
            }
        }
    }

    public static String[] getAvailableRoles() {
        Role[] roles = Role.values();
        String[] parsedRoles = new String[roles.length];

        for (Role role: roles) {
            var skip = false;

            for (var player : players) {
                if (player.getRole() == role) {
                    skip = true;
                }
            }

            if (skip) {
                continue;
            }

            parsedRoles[role.ordinal()] =  role.toString();;
        }

        return parsedRoles;
    }
}
