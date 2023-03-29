package org.board.entities;


import org.board.enumerables.Role;

public class Player {
    /* Class Variables */
    private static Player[] players;
    private static int loadedPlayers;

    // Role Cards
    final static private int TOTAL_ROLE_CARDS = 6;

    /* Member Variables */
    private Role role;
    private int pawn;
    private String name;

    private hand

    private Player(String name, int pawn, Role role) {
        this.name = name;
        this.pawn = pawn;
        this.role = role;
    }

    public int getPawn() {
        return pawn;
    }

    public Role getRole() {
        return role;
    }

    /* Static Methods */
    public static void init(int numberOfPlayers) throws Exception {
        if (players != null) {
            throw new Exception("Players has already been instantiated");
        }

        players = new Player[numberOfPlayers];
        loadedPlayers = 0;
    }

    public static Player build(String name, int pawn, Role role) throws Exception {
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

        players[loadedPlayers] = player;
        loadedPlayers += 1;

        return player;
    }

    private static String autoGenerateName() {
        return "blind-turtle";
    }

    public static boolean ready() {
        return players.length == loadedPlayers;
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
