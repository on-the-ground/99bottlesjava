import main.java.com.bottles.stage1.FileOut;
import main.java.com.bottles.stage1.Output;
import main.java.com.bottles.stage1.Stdout;

static final class NinetyNineBottles {

    private static final int MAX_BOTTLES = 99;
    private static final int INIT_STORE_STOCK = 101;
    private int storeStock;

    public NinetyNineBottles() {
        storeStock = INIT_STORE_STOCK;
    }

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

    private int orderBottles() {
        int res = Math.min(storeStock, MAX_BOTTLES);
        storeStock -= res;
        return res;
    }


    private int diffByRule(int curBottles) {
        final int maxUnload = 2;
        final int multiUnloadThreshold = 10;
        if (curBottles == 0) return orderBottles();
        if (curBottles < maxUnload) return -curBottles;
        if (curBottles <= multiUnloadThreshold) return -maxUnload;
        return -1;
    }

    public void song(Output output) {
        int curBottles = MAX_BOTTLES;
        int diff;
        do {
            diff = diffByRule( curBottles);
            output.out(verse(curBottles, diff));
            curBottles += diff;
        } while (diff != 0);
    }
}

void main() throws IOException {
    new NinetyNineBottles().song(Stdout.INSTANCE);
    try (FileOut file = new FileOut("99bottles.output")) {
        new NinetyNineBottles().song(file);
    }
}
