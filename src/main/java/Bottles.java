import com.bottles.*;
import io.effectivejava.HandlerScope;

static final class NinetyNineBottles implements Iterable<String> {

    private static final int MAX_BOTTLES = 99;
    private static final int INIT_STORE_STOCK = 101;
    private static final UUID key = UUID.randomUUID();

    NinetyNineBottles() throws Exception {
        Stock initStock = Stock.of(MAX_BOTTLES, INIT_STORE_STOCK);
        var sourceEffect = HandlerScope.find(StockSourceEffect.class);
        if (sourceEffect.get(key.toString()) == null)
            sourceEffect.put(key.toString(), initStock);
    }

    @Override
    public Iterator<String> iterator() {
        return new SongCursor();
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

    private static String verse(int cur, int diff) {
        String line1 = numBottlesCapitalized(cur) + " of beer on the wall, " + numBottles(cur) + " of beer.\n";
        if (diff < 0)
            return line1 + "Take " + (-diff) + " down and pass " + ((-diff == 1) ? "it" : "them") + " around, " + numBottles(cur + diff) + " of beer on the wall.\n";
        if (diff > 0)
            return line1 + "Go to the store and buy some more, " + numBottles(cur + diff) + " of beer on the wall.\n";
        return line1 + "Even the store has no more bottles. Time to say goodbye, my dear.\n";
    }

    private static String numBottles(int n) {
        if (n == 0) return "no more bottles";
        return n + (n == 1 ? " bottle" : " bottles");
    }

    private static String numBottlesCapitalized(int n) {
        if (n == 0) return "No more bottles";
        return n + (n == 1 ? " bottle" : " bottles");
    }

    private static final class SongCursor implements Iterator<String> {
        private boolean endOfSong = false;

        private boolean isEndOfSong(Stock s) {
            return s.wall() == 0 && s.store() == 0;
        }

        @Override
        public boolean hasNext() {
            return !endOfSong;
        }

        @Override
        public String next() {
            var sourceEffect = HandlerScope.find(StockSourceEffect.class);
            while (hasNext()) {
                try {
                    Stock curStock = sourceEffect.get(key.toString());
                    if (isEndOfSong(curStock)) {
                        endOfSong = true;
                        break;
                    }

                    StockTx tx = TxRule.tx(curStock);
                    Stock newStock = curStock.apply(tx);
                    if (sourceEffect.cas(key.toString(), curStock, newStock)) {
                        if (isEndOfSong(newStock)) endOfSong = true;
                        return verse(curStock.wall(), tx.wall());
                    }
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
            throw new java.util.NoSuchElementException("The song has completely ended.");
        }
    }
}

interface StockSourceEffect {
    Stock get(String key);
    void put(String key, Stock value);
    boolean cas(String key, Stock expect, Stock update);
}

static class StockSourceHandler implements StockSourceEffect {
    SSoT ssot;

    StockSourceHandler(SSoT ssot) {
        this.ssot = ssot;
    }

    public Stock get(String key) {
        return ssot.get(key);
    }
    public void put(String key, Stock value) {
        ssot.put(key, value);
    }
    public boolean cas(String key, Stock expect, Stock update) {
        return ssot.compareAndSet(key, expect, update);
    }
}

void main() throws Exception {
    SSoT ssot = new InMemorySSoT();
    HandlerScope.open().bind(StockSourceEffect.class, () -> new StockSourceHandler(ssot)).run(() -> {
        var song = new NinetyNineBottles();

        try (FileOut file = new FileOut( "99bottles.output")) {
            try (var scope = StructuredTaskScope.open(StructuredTaskScope.Joiner.awaitAll())) {
                scope.fork(() -> {
                    for (String verse : song) {
                        System.out.println("JVT1: " + verse);
                        file.out("JVT1: " + verse);
                    }
                    return null;
                });
                scope.fork(() -> {
                    for (String verse : song) {
                        System.out.println("JVT2: " + verse);
                        file.out("JVT2: " + verse);
                    }
                    return null;
                });
                scope.join();
            }
        }
    });

    System.out.println("All the singers have finished singing.");
}