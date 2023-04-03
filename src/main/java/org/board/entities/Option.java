package org.board.entities;

import org.board.enumerables.OptionType;


public class Option {
    private int disposeCard = -1;
    private int endCity = -1;
    private int endPlayer = -1;
    private int suit = -1;
    private OptionType type = OptionType.Invalid;
    final String name;

    public Option(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public OptionType getType() {
        return type;
    }

    public int getDisposeCard() {
        return disposeCard;
    }

    public int getEndCity() {
        return endCity;
    }

    public int getEndPlayer() {
        return endPlayer;
    }

    public void setDisposeCard(int disposeCard) {
        this.disposeCard = disposeCard;
    }

    public void setEndCity(int endCity) {
        this.endCity = endCity;
    }

    public void setEndPlayer(int endPlayer) {
        this.endPlayer = endPlayer;
    }

    public int getSuit() {
        return suit;
    }

    public void setSuit(int suit) {
        this.suit = suit;
    }

    public void setType(OptionType type) {
        this.type = type;
    }
}
