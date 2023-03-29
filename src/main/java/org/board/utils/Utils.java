package org.board.utils;

public class Utils {
    public static void insert(int[][][] boardState, int cityId, int typeId, int itemId) {
        insert(boardState[cityId][typeId], itemId);
    }

    private static void insert(int[] boardState, int itemId) {
        for(int i = 0; i < boardState.length; i++) {
            if(boardState[i] != -1) continue;

            boardState[i] = itemId;
            return;
        }
    }
}
