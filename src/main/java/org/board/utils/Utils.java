package org.board.utils;

import java.util.ArrayList;
import java.util.Random;

public class Utils {
    private static Random random = new Random(1);

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

    public static<T> void shuffle(ArrayList<T> items, int start) {
        for (int currentIndex = start; currentIndex < items.size(); currentIndex++) {
            int newIndex = random.nextInt(items.size());
            var temp = items.get(currentIndex);
            items.set(currentIndex, items.get(newIndex));
            items.set(newIndex, temp);
        }
    }
}
