package org.board.entities;

import java.util.ArrayList;

/**
 * Represents an infection card.
 */
public class InfectionCard {
    /* a list of infection for the building stage */
    private final static ArrayList<InfectionCard> cards = new ArrayList<>();

    /* the id of the infection card (matches the city id) */
    final private int id;

    /**
     * Sets the infection card id (this matches the city)
     * @param id the id of the infection card.
     */
    private InfectionCard(int id) {
        this.id = id;
    }

    /**
     * Gets a list oll the infection cards
     * @return array list of cards
     */
    public static ArrayList<InfectionCard> getCards() {
        return cards;
    }

    /**
     * Clones a list of infection cards.
     * @param cards infection cards list
     * @return the cloned infection cards
     */
    public static ArrayList<InfectionCard> getCards(ArrayList<InfectionCard> cards) {
        var newCards = new ArrayList<InfectionCard>();

        for (var card : cards) {
            var newCard = new InfectionCard(card.getId());
            newCards.add(newCard);
        }

        return newCards;
    }

    /**
     * Gets the id of the infection card.
     */
    public int getId() {
        return id;
    }

    /**
     * Adds a new card to the infection card builder
     * @param id the id of the city
     */
    public static void addCard(int id) {
        cards.add(new InfectionCard(id));
    }

}
