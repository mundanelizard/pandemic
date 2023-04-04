package org.board.utils;

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

    public static<T> void shuffle(ArrayList<T> items) {
        shuffle(items, items.size());
    }

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

    public static int[][][] copy3dArray(int[][][] array) {
        int[][][] newArray = new int[array.length][][];

        for (int i = 0; i < array.length; i++) {
            newArray[i] = new int[array[i].length][];
            for (int j = 0; j < array[i].length; j++) {
                newArray[i][j] = new int[array[i][j].length];
                for (int k = 0; k < array[i][j].length; k++) {
                    newArray[i][j][k] = newArray[i][j][k];
                }
            }
        }

        return newArray;
    }

}
