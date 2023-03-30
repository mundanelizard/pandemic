package org.board.utils;

public class Index {
    private int index = 0;

    public void increment() {
        index += 1;
    }

    public void reset() {
        index = 0;
    }

    public int getIndex() {
        return index;
    }
}
