package flashcards.helpers;

public abstract class ArgParser {
    protected static int getArgIndex(String arg, String... args) {
        for (int i = 0; i < args.length; i++) {
            if (args[i].equals(arg)) {
                return i + 1;
            }
        }

        return -1;
    }
}
