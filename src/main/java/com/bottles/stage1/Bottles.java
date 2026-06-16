import main.java.com.bottles.stage1.FileOut;
import main.java.com.bottles.stage1.Output;
import main.java.com.bottles.stage1.Stdout;

static final class BottlesUtils {

    private static final int MAX_BOTTLES = 99;
    private static final int INIT_STORE_STOCK = 101;
    private static int storeStock = INIT_STORE_STOCK;

    private static String verse(int number) {
        if (number == 1) {
            return "1 bottle of beer on the wall, 1 bottle of beer.\n"
                    + "Take it down and pass it around, no more bottles of beer on the wall.\n";
        }

        if (number == 2) {
            return "2 bottles of beer on the wall, 2 bottles of beer.\n"
                    + "Take one down and pass it around, 1 bottle of beer on the wall.\n";
        }

        return number + " bottles of beer on the wall, "
                + number + " bottles of beer.\n"
                + "Take one down and pass it around, "
                + (number - 1)
                + " bottles of beer on the wall.\n";
    }

    private static String verse0(int newStock) {
        if (newStock > 0) return "No more bottles of beer on the wall, no more bottles of beer.\n" +
                "Go to the store and buy some more, " + newStock + " bottles of beer on the wall.\n\n";
        return "No more bottles of beer on the wall, no more bottles of beer.\n" +
                "Even the store has no more bottles. Time to say goodbye, my dear.\n\n";
    }

    private static int orderBottles() {
        int res = Math.min(storeStock, MAX_BOTTLES);
        storeStock -= res;
        return res;
    }

    private static int iteration(Output output, int initBottles) {
        for (int i = initBottles; i >= 1; i--) {
            output.out(verse(i));
        }
        int newStock = orderBottles();
        output.out(verse0(newStock));
        return newStock;
    }

    public static void song(Output output) {
        int newStock = MAX_BOTTLES;
        while (newStock > 0) {
            newStock = iteration(output, newStock);
        }
    }
}

void main() throws IOException {
    BottlesUtils.song(Stdout.INSTANCE);
    try (FileOut file = new FileOut("99bottles.output")) {
        BottlesUtils.song(file);
    }
}
