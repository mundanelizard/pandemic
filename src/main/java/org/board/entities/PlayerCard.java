package org.board.entities;

import org.board.enumerables.Card;
import org.board.enumerables.Colour;

import java.util.ArrayList;

public class PlayerCard {
    private static ArrayList<PlayerCard> cards = new ArrayList<>();

    final private int city;
    final private Card type;
    final private Colour colour;

    private PlayerCard(int city, Card type, Colour colour) {
        this.city = city;
        this.type = type;
        this.colour = colour;
    }

    public int getCity() {
        return city;
    }

    public Card getType() {
        return type;
    }

    public Colour getColour() {
        return colour;
    }

    /* Static Methods */

    public static void addCard(int city, Card type, Colour colour) {
        cards.add(new PlayerCard(city, type, colour));
    }

    public static ArrayList<PlayerCard> getCards() {
        return getCards(cards);
    }

    public static ArrayList<PlayerCard> getCards(ArrayList<PlayerCard> cards) {
        var newCards = new ArrayList<PlayerCard>();

        for (var card : cards) {
            var newCard = new PlayerCard(card.city, card.type, card.colour);
            newCards.add(newCard);
        }

        return newCards;
    }
}
