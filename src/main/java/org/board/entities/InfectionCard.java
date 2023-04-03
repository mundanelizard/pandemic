package org.board.entities;

import java.util.ArrayList;

public class InfectionCard {
    private static ArrayList<InfectionCard> cards = new ArrayList<>();

    final private int id;

    private InfectionCard(int id) {
        this.id = id;
    }

    public static ArrayList<InfectionCard> getCards(ArrayList<InfectionCard> cards) {
        var newCards = new ArrayList<InfectionCard>();

        for (var card : cards) {
            var newCard = new InfectionCard(card.getId());
            newCards.add(newCard);
        }

        return newCards;
    }

    public int getId() {
        return id;
    }

    /* Static Methods */

    public static void addCard(int id) {
        cards.add(new InfectionCard(id));
    }


    public static ArrayList<InfectionCard> getCards() {
        return getCards(cards);
    }
}
