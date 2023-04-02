package org.board.utils;

import org.board.logic.Game;

import java.util.ArrayList;
import java.util.Random;

public class Utils {
    // todo => remove seed when out of testing mode
    private static final Random random = new Random(1);

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

    public static void remove(int[][][] boardState, int cityId, int typeId, int itemId) {
        remove(boardState[cityId][typeId], itemId);
    }

    private static void remove(int[] boardState, int itemId) {
        for(int i = 0; i < boardState.length; i++) {
            if (boardState[i] != itemId) return;

            boardState[i] = -1;
        }
    }

    public static<T> void shuffle(ArrayList<T> items, int start) {
        for (int currentIndex = start; currentIndex < items.size(); currentIndex++) {
            int newIndex = random.nextInt(items.size());
            var temp = items.get(currentIndex);
            items.set(currentIndex, items.get(newIndex));
            items.set(newIndex, temp);
        }
    }

    public static<T> ArrayList<T> getItemsOnBoard(int[][][] boardState, int typeId, ArrayList<T> cubes, int city) {
        var itemsOnBoard = new ArrayList<T>();
        var disease = boardState[city][typeId];

        for (int cube : disease) {
            if (cube == -1) continue;
            itemsOnBoard.add(cubes.get(cube));
        }

        return itemsOnBoard;
    }

}
