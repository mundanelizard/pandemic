package org.board.entities;

import org.board.enumerables.OptionType;


/**
 * Represents a possible action by a player at any given point in the game.
 * It also contains required data to perform the action.
 */
public class Option {
    /* the card to dispose after action */
    private int disposeCard = -1;

    /* the city to end on after an action */
    private int endCity = -1;

    /* the player to end on after an action */
    private int endPlayer = -1;

    /* the suit represents colour */
    private int suit = -1;

    /* type of action to perform */
    private OptionType type = OptionType.Invalid;

    /* the name of the option (auto generated) */
    final String name;

    public Option(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    /**
     * Gets the type of action to perform
     * @return action type
     */
    public OptionType getType() {
        return type;
    }

    /**
     * Gets the card to dispose or use to action.
     * @return the id of the card
     */
    public int getDisposeCard() {
        return disposeCard;
    }

    /**
     * Gets the city to end on after a sequence of actions.
     * @return the id of the city to end on.
     */
    public int getEndCity() {
        return endCity;
    }

    /**
     * Gets the end player (mainly for card transfers).
     * @return the id of the player in the player list.
     */
    public int getEndPlayer() {
        return endPlayer;
    }

    /**
     * Gets the card colour suit to work on.
     * @return the colour suit
     */
    public int getSuit() {
        return suit;
    }

    /**
     * Sets the card to dispose
     * @param disposeCard card to dispose
     */
    public void setDisposeCard(int disposeCard) {
        this.disposeCard = disposeCard;
    }

    /**
     * Sets the city to end on.
     * @param endCity the city to end on
     */
    public void setEndCity(int endCity) {
        this.endCity = endCity;
    }

    /**
     * Sets the end player
     * @param endPlayer the end player
     */
    public void setEndPlayer(int endPlayer) {
        this.endPlayer = endPlayer;
    }

    /**
     * Sets the suit of the action
     * @param suit the action suit
     */
    public void setSuit(int suit) {
        this.suit = suit;
    }

    /**
     * Sets the type of the action
     * @param type the type of action.
     */
    public void setType(OptionType type) {
        this.type = type;
    }
}
