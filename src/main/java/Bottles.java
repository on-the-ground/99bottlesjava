import com.bottles.*;
import java.util.concurrent.StructuredTaskScope.Subtask;

static final class NinetyNineBottles {

    private static final int MAX_BOTTLES = 99;
    private static final int INIT_STORE_STOCK = 101;

    public static Iterable<String> song(UUID id, SSoT source) {
        return new Song(id, source);
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

    private record Song(UUID id, SSoT source) implements Iterable<String> {

        @Override
        public Iterator<String> iterator() {
            Stock init = Stock.of(MAX_BOTTLES, INIT_STORE_STOCK);
            source.put(id.toString(), init);
            return new SongCursor();
        }

        private Stock curStock() {
            return source.get(id.toString());
        }

        private boolean cas(Stock curStockIKnow, Stock newStock) {
            return source.compareAndSet(id.toString(), curStockIKnow, newStock);
        }

        private final class SongCursor implements Iterator<String> {
            private Stock cur;
            private boolean endOfSong = false;

            private SongCursor() {
                this.cur = curStock();
            }

            private boolean isEndOfSong() {
                return cur.wall() == 0 && cur.store() == 0;
            }

            @Override
            public boolean hasNext() {
                return !endOfSong;
            }

            @Override
            public String next() {
                while (hasNext()) {
                    Stock snapshot = curStock();
                    StockTx tx = TxRule.tx(snapshot);
                    Stock newStock = snapshot.apply(tx);
                    if (cas(snapshot, newStock)) {
                        cur = newStock;
                        if (isEndOfSong()) endOfSong = true;
                        return verse(snapshot.wall(), tx.wall());
                    }
                }
                throw new java.util.NoSuchElementException("The song has completely ended.");
            }

        }
    }
}

void main() throws InterruptedException {
    SSoT source = new InMemorySSoT();
    UUID id1 = UUID.randomUUID();

    try (var scope = StructuredTaskScope.open(StructuredTaskScope.Joiner.awaitAll())) {

        Subtask<Void> task1 = scope.fork(() -> {
            try (FileOut file = new FileOut(id1 + ".out")) {
                for (String verse : NinetyNineBottles.song(id1, source)) {
                    System.out.println("JVT1: " + verse);
                    file.out(id1 + verse);
                }
            }
            return null;
        });

        Subtask<Void> task2 = scope.fork(() -> {
            try (FileOut file = new FileOut(id1 + ".out")) {
                for (String verse : NinetyNineBottles.song(id1, source)) {
                    System.out.println("JVT2: " + verse);
                    file.out(id1 + verse);
                }
            }
            return null;
        });
        scope.join();
    }
    System.out.println("All the singers have finished singing.");
}