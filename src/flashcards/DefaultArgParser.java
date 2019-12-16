package flashcards;

import flashcards.helpers.ArgParser;

import java.util.NoSuchElementException;

class DefaultArgParser extends ArgParser {
    private String[] args;

    DefaultArgParser(String[] args) {
        this.args = args;
    }


    String getImport(){
        return getArg("import");
    }

    String getExport() { return getArg("export"); }

    private String getArg(String arg) {
        int index = getArgIndex("-" + arg, args);

        if (index > args.length - 1) {
            throw new IllegalArgumentException("Argument \"" + arg + "\" is invalid.");
        }

        if (index == -1) {
            throw new NoSuchElementException("Argument \"" + arg + "\" was not found.");
        }

        return args[index];
    }
}
