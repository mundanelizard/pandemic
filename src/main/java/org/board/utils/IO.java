package org.board.utils;

import org.board.entities.Player;
import org.board.enumerables.Choice;

import java.util.Scanner;

public class IO {
    static final Scanner shell = new Scanner(System.in);

    public static Choice getPlayerChoice(Player player) {
        int max = Choice.values().length;
        int choice = -1;

        do {
            displayPlayerChoice(player);
            choice = shell.nextInt();
        } while(choice > max || choice < 0);


        return Choice.values()[choice];
    }

    public static void displayPlayerChoice(Player player) {
        System.out.println("Hello " + player.getName() + " it's your turn ");
        System.out.println("Here are your choices: ");

        for (var choice : Choice.values()) {
            System.out.println(choice.ordinal() + ". " + choice);
        }

        System.out.print("> ");
    }
}
