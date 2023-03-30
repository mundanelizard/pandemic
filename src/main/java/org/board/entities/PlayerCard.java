package org.board.entities;

import org.board.enumerables.Card;

import java.util.ArrayList;

public class PlayerCard {
    private static ArrayList<PlayerCard> cards = new ArrayList<>();

    final private int city;
    final private Card type;

    private PlayerCard(int city, Card type) {
        this.city = city;
        this.type = type;
    }

    public int getCity() {
        return city;
    }

    public Card getType() {
        return type;
    }

    /* Static Methods */

    public static void addCard(int city, Card type) {
        cards.add(new PlayerCard(city, type));
    }

    public static ArrayList<PlayerCard> getCards() {
        var newCards = new ArrayList<PlayerCard>();

        for (var card : cards) {
            var newCard = new PlayerCard(card.city, card.type);
            newCards.add(newCard);
        }

        return newCards;
    }
}
