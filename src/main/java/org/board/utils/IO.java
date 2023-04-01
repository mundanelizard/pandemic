package org.board.utils;

import org.board.entities.Player;
import org.board.enumerables.Choice;
import org.board.logic.Action;

import java.util.ArrayList;
import java.util.Scanner;

public class IO {
    static final Scanner shell = new Scanner(System.in);

    public static Choice getPlayerChoice(Player player) {
        int max = Choice.values().length;
        int choice;

        do {
            displayPlayerChoice(player);
            choice = shell.nextInt();
        } while(choice > max || choice < 0);


        return Choice.values()[choice];
    }

    public static void displayPlayerChoice(Player player) {
        System.out.println("--");
        System.out.println("Hello " + player.getName() + " it's your turn ");
        System.out.println("Here are your choices: ");

        for (var choice : Choice.values()) {
            System.out.println(choice.ordinal() + ". " + choice);
        }

        System.out.print("> ");
    }

    public static Action.Option getPlayerActionChoice(ArrayList<Action.Option> options, Player player, int action) {
        int choice;

        do {
            displayPlayerOptions(options, player, action);
            choice = shell.nextInt();
        } while (choice < 0 || choice > options.size());

        return options.get(choice);
    }

    private static void displayPlayerOptions(ArrayList<Action.Option> options, Player player, int action) {
        System.out.println("--");
        System.out.println("Hello " + player.getName() + "!");
        System.out.println("Here are the possible moves for the current action (" + action + "/4)");

        for (var i = 0; i < options.size(); i++) {
            System.out.println(i + ". " + options.get(i).getName());
        }

        System.out.print("> ");
    }
}
