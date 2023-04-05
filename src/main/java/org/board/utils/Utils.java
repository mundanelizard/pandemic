package org.board.utils;

import java.util.ArrayList;
import java.util.Random;

public class Utils {
    // todo => remove seed when out of testing mode
    private static final Random random = new Random(1);

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

/*

0. Move by ferry to Washington
1. Move by ferry to Chicago
2. Move by ferry to Mexico City
3. Move by ferry to Miami
4. Dispose [4, Washington, Blue] to direct fly to Washington
5. Transfer card to player [Rupert]
6. Dispose [34, Kolkata, Black] to direct fly to Kolkata
7. Transfer card to player [Rupert]
8. Dispose [14, Lima, Yellow] to direct fly to Lima
9. Transfer card to player [Rupert]
10. Dispose [1, Chicago, Blue] to direct fly to Chicago
11. Transfer card to player [Rupert]
 */