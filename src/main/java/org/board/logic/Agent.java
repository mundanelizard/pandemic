package org.board.logic;

import org.board.entities.Player;

public class Agent {
    static final public String NAME = "Rupert";
    private Player player;

    public void setPlayer(Player player) {
        this.player = player;
    }
    public Player getPlayer() {
        return player;
    }

    public void play(State state) {
        System.out.println("Agent is performing is actions");
    }
}
