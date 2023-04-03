package org.board.logic;

import org.board.entities.Option;
import org.board.entities.Player;

import java.sql.Array;
import java.util.ArrayList;

public class Agent {
    static final public String NAME = "Rupert";
    private Player player;

    public void setPlayer(Player player) {
        this.player = player;
    }
    public Player getPlayer() {
        return player;
    }

    public void play(State state) throws Exception {
        System.out.println("Agent is performing is actions");

        for (int i = 1; i <= 4; i++) {
            var options = getRankedBestOptions(state);
            var bestOption = options.get(0);
            state.performAction(bestOption, i);
        }
    }

    public ArrayList<Option> getRankedBestOptions(State state) throws Exception {
        var newState = state.deepClone();

        return new ArrayList<>();
    }
}
