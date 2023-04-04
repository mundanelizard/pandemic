package org.board.entities;

import org.board.enumerables.Card;
import org.board.enumerables.Colour;

import java.util.ArrayList;

/**
 * Represents a player card
 */
public class PlayerCard {
    /* list of card for initialisation process */
    final private static ArrayList<PlayerCard> cards = new ArrayList<>();

    /* the city the card represent (-1 for non-city cards) */
    final private int city;

    /* what the card represents (epidemic or city) */
    final private Card type;

    /* the colour of the city */
    final private Colour colour;

    /**
     * Sets the city, type and colour of the city.
     * @param city id of the card city
     * @param type the card tye
     * @param colour the colour the card
     */
    private PlayerCard(int city, Card type, Colour colour) {
        this.city = city;
        this.type = type;
        this.colour = colour;
    }

    /**
     * Retrieves the card city
     * @return the city id
     */
    public int getCity() {
        return city;
    }

    /**
     * Retrieves the card type
     * @return card type
     */
    public Card getType() {
        return type;
    }

    /**
     * Retrieves the card colour
     * @return card colour (red, blue, black, yellow)
     */
    public Colour getColour() {
        return colour;
    }

    /**
     * Returns players card from initialisation process.
     * @return player cards
     */
    public static ArrayList<PlayerCard> getCards() {
        return cards;
    }

    /**
     * Clones a list of player cards.
     * @param cards player cards
     * @return newly clone player cards
     */
    public static ArrayList<PlayerCard> getCards(ArrayList<PlayerCard> cards) {
        var newCards = new ArrayList<PlayerCard>();

        for (var card : cards) {
            var newCard = new PlayerCard(card.city, card.type, card.colour);
            newCards.add(newCard);
        }

        return newCards;
    }

    /**
     * Add new cards to initialisation card list
     * @param city the id of the card.
     * @param type type of the card.
     * @param colour the colour of the card.
     */
    public static void addCard(int city, Card type, Colour colour) {
        cards.add(new PlayerCard(city, type, colour));
    }

    @Override
    public String toString() {
        return "[ #" + city + ", " + type + ", " + colour + "] ";
    }
}
