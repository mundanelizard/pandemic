package org.board.utils;

import java.util.ArrayList;
import java.util.Random;

public class Utils {
    // todo => remove seed when out of testing mode
    private static final Random random = new Random();

    public static<T> void shuffle(ArrayList<T> items) {
        shuffle(items, items.size());
    }

    /**
     * Shuffles the dec and ends on specific number.
     * @param items the item has been run.
     * @param end the end of the
     * @param <T>
     */
    public static<T> void shuffle(ArrayList<T> items, int end) {
        for (int currentIndex = 0; currentIndex < end; currentIndex++) {
            int newIndex = random.nextInt(end);
            var temp = items.get(currentIndex);
            items.set(currentIndex, items.get(newIndex));
            items.set(newIndex, temp);
        }
    }

    public static<T> ArrayList<T> getItemsOnBoard(int[][][] boardState, int typeId, ArrayList<T> arr, int city) {
        var itemsOnBoard = new ArrayList<T>();
        var items = boardState[city][typeId];

        for (int item : items) {
            if (item == -1) continue;
            itemsOnBoard.add(arr.get(item));
        }

        return itemsOnBoard;
    }

    public static int[][][] copy3dArray(int[][][] boardState) {
        int[][][] newBoardState = new int[48][3][24];

        for (int i = 0; i < boardState.length; i++) {
            for (int j = 0; j < boardState[i].length; j++) {
                for (int k = 0; k < boardState[i][j].length; k++) {
                    newBoardState[i][j][k] = boardState[i][j][k];
                }
            }
        }

        return newBoardState;
    }
}
