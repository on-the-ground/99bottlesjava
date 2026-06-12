static final class BottlesUtils {

    private static final int MAX_BOTTLES = 99;

    static String verse(int number) {
        if (number == 0) {
            return "No more bottles of beer on the wall, no more bottles of beer.\n"
                    + "Go to the store and buy some more, 99 bottles of beer on the wall.\n";
        }

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

    public static void song() {
        for (int i = MAX_BOTTLES; i >= 0; i--) {
            System.out.println(verse(i));
        }
    }
}

void main() {
    BottlesUtils.song();
}
