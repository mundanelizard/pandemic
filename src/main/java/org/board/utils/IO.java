package org.board.utils;

import org.board.entities.Player;
import org.board.enumerables.Choice;
import org.board.entities.Option;

import java.util.ArrayList;
import java.util.Scanner;

/**
 * IO - handles IO for getting user inputs in the game.
 */
public class IO {
    static final Scanner shell = new Scanner(System.in);

    /**
     * Gets player choice
     * @param player the player
     * @return the player choice
     */
    public static Choice getPlayerChoice(Player player) {
        int max = Choice.values().length;
        int choice;

        do {
            displayPlayerChoice(player);
            choice = shell.nextInt();
        } while(choice > max || choice < 0);


        return Choice.values()[choice];
    }

    /**
     * Displays player choices
     * @param player the player
     */
    public static void displayPlayerChoice(Player player) {
        System.out.println("--");
        System.out.println("Hello " + player.getName() + "! It's your turn.");
        System.out.println("Here are your choices: ");

        for (var choice : Choice.values()) {
            System.out.println(choice.ordinal() + ". " + choice);
        }

        System.out.print("> ");
    }

    /**
     * Gets the player action choice
     * @param options the options to choose from
     * @param player the player
     * @param actionCount the action index.
     * @return option
     */
    public static Option getPlayerActionChoice(ArrayList<Option> options, Player player, int actionCount) {
        int choice;

        do {
            displayPlayerOptions(options, player, actionCount);
            choice = shell.nextInt();
        } while (choice < 0 || choice > options.size());

        return options.get(choice);
    }

    /**
     * Displays all the player options.
     * @param options the options available to the players
     * @param player the players
     * @param actionCount the action count
     */
    private static void displayPlayerOptions(ArrayList<Option> options, Player player, int actionCount) {
        System.out.println("--");
        System.out.println("Hello " + player.getName() + "!");
        System.out.println("Here are the possible moves for the current action (" + actionCount + "/4)");

        for (var i = 0; i < options.size(); i++) {
            System.out.println(i + ". " + options.get(i).getName());
        }

        System.out.print("> ");
    }

    /**
     * Gets the user preferred user count.
     * @return the total number players.
     */
    public static int getPlayerCount() {
        var players = -1;

        do {
            System.out.println("How many players do you want (1 - 3)? The agent is always a player in each game.");
            System.out.print("> ");

            players = IO.shell.nextInt();
        } while(players < 1 || players > 3);

        return players;
    }

    /**
     * Gets player from the user via shell.
     * @param index the index of the player.
     * @return name of the user
     */
    public static String getPlayerName(int index) {
        System.out.println("What's player " + (index + 1) + " name?");
        System.out.print("> ");
        return IO.shell.next();
    }
}
