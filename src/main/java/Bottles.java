import com.bottles.*;

static final class NinetyNineBottles {

    private static final int MAX_BOTTLES = 99;

    public static void song(Output output) {
        final int INIT_STORE_STOCK = 101;
        Stock stock = new Stock(MAX_BOTTLES, INIT_STORE_STOCK);
        StockTx tx;
        do {
            tx = TxRule.tx(stock);
            output.out(verse(stock.wall(), tx.wall()));
            stock = stock.apply(tx);
        } while (stock.wall() > 0 || stock.store() > 0);
    }

    private static class TxRule {
        private static final int maxUnload = 2;
        private static final int multiUnloadThreshold = 10;

        static StockTx tx(Stock stock) {
            if (stock.wall() == 0) return StockTx.order(Math.min(stock.store(), MAX_BOTTLES));
            if (stock.wall() < maxUnload) return StockTx.unload(stock.wall());
            if (stock.wall() <= multiUnloadThreshold) return StockTx.unload(maxUnload);
            return StockTx.unload(1);
        }
    }

    private static String verse(int curBottles, int numDiff) {
        String currentBottles = numBottlesCapitalized(curBottles) + " of beer on the wall, " + numBottles(curBottles) + " of beer.\n";
        if (numDiff < 0) {
            return currentBottles + "Take " + (-numDiff) + " down and pass " + ((-numDiff == 1) ? "it" : "them") + " around, "
                    + numBottles(curBottles + numDiff) + " of beer on the wall.\n";
        } else if (numDiff > 0) {
            return currentBottles + "Go to the store and buy some more, " + numBottles(curBottles + numDiff) + " of beer on the wall.\n";
        }
        return currentBottles + "Even the store has no more bottles. Time to say goodbye, my dear.\n";
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
}

void main() throws IOException {
    NinetyNineBottles.song(Stdout.INSTANCE);
    try (FileOut file = new FileOut("99bottles.output")) {
        NinetyNineBottles.song(file);
    }
}
