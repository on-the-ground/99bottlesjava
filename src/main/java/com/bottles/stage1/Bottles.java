import main.java.com.bottles.stage1.FileOut;
import main.java.com.bottles.stage1.Output;
import main.java.com.bottles.stage1.Stdout;

record Stock(int wall, int store) {
    Stock apply(StockDiff diff) {
        return new Stock(wall + diff.wall, store + diff.store);
    }
}

record StockDiff(int wall, int store) {
}

static final class NinetyNineBottles {

    private static final int MAX_BOTTLES = 99;

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

    private static StockDiff orderBottles(int storeStock) {
        int diff = Math.min(storeStock, MAX_BOTTLES);
        return new StockDiff(diff, -diff);
    }


    private static StockDiff diffByRule(Stock stock) {
        final int maxUnload = 2;
        final int multiUnloadThreshold = 10;
        if (stock.wall == 0) return orderBottles(stock.store);
        if (stock.wall < maxUnload) return new StockDiff(-stock.wall, 0);
        if (stock.wall <= multiUnloadThreshold) return new StockDiff(-maxUnload, 0);
        return new StockDiff(-1, 0);
    }

    public static void song(Output output) {
        final int INIT_STORE_STOCK = 101;
        Stock stock = new Stock(MAX_BOTTLES, INIT_STORE_STOCK);
        StockDiff diff;
        do {
            diff = diffByRule(stock);
            output.out(verse(stock.wall, diff.wall));
            stock = stock.apply(diff);
        } while (diff.wall != 0);
    }
}

void main() throws IOException {
    NinetyNineBottles.song(Stdout.INSTANCE);
    try (FileOut file = new FileOut("99bottles.output")) {
        NinetyNineBottles.song(file);
    }
}
