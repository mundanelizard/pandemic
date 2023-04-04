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
     * @param <T> any type
     */
    public static<T> void shuffle(ArrayList<T> items, int end) {
        for (int currentIndex = 0; currentIndex < end; currentIndex++) {
            int newIndex = random.nextInt(end);
            var temp = items.get(currentIndex);
            items.set(currentIndex, items.get(newIndex));
            items.set(newIndex, temp);
        }
    }
}
