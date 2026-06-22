import main.java.com.bottles.stage1.FileOut;
import main.java.com.bottles.stage1.Output;
import main.java.com.bottles.stage1.Stdout;

record Diff(int wall, int store) {
}

static final class NinetyNineBottles {

    private static final int MAX_BOTTLES = 99;
    private static final int INIT_STORE_STOCK = 101;

    private static String numBottles(int num) {
        if (num == 0) return "no more bottles";
        if (num == 1) return num + " bottle";
        return num + " bottles";
    }

    private static String numBottlesCapitalized(int num) {
        if (num == 0) return "No more bottles";
        if (num == 1) return num + " bottle";
        return num + " bottles";
    }

    private static String verse(int curBottles, int numDiff) {
        String currentBottles = numBottlesCapitalized(curBottles) + " of beer on the wall, " + numBottles(curBottles) + " of beer.\n";
        if (numDiff < 0) { // Take bottles down
            return currentBottles + "Take " + (-numDiff) + " down and pass " + ((-numDiff == 1) ? "it" : "them") + " around, "
                    + numBottles(curBottles + numDiff) + " of beer on the wall.\n";
        } else if (numDiff > 0) { // Order more Bottles
            return currentBottles + "Go to the store and buy some more, " + numBottles(curBottles + numDiff) + " of beer on the wall.\n";
        }
        // No more bottles
        return currentBottles + "Even the store has no more bottles. Time to say goodbye, my dear.\n";
    }

    private static Diff orderBottles(int storeStock ) {
        int diff = Math.min(storeStock, MAX_BOTTLES);
        return new Diff(diff, -diff);
    }


    private static Diff diffByRule(int curBottles, int storeStock) {
        final int maxUnload = 2;
        final int multiUnloadThreshold = 10;
        if (curBottles == 0) return orderBottles(storeStock);
        if (curBottles < maxUnload) return new Diff(-curBottles, 0);
        if (curBottles <= multiUnloadThreshold) return new Diff(-maxUnload, 0);
        return new Diff(-1, 0);
    }

    public static void song(Output output) {
        int curBottles = MAX_BOTTLES;
        int storeStock = INIT_STORE_STOCK;
        Diff diff;
        do {
            diff = diffByRule(curBottles, storeStock);
            output.out(verse(curBottles, diff.wall));
            curBottles += diff.wall;
            storeStock += diff.store;
        } while (diff.wall != 0);
    }
}

void main() throws IOException {
    NinetyNineBottles.song(Stdout.INSTANCE);
    try (FileOut file = new FileOut("99bottles.output")) {
        NinetyNineBottles.song(file);
    }
}
