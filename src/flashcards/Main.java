package flashcards;

import java.io.*;
import java.util.*;

public class Main {
    private static LinkedHashMap<String, String> map = new LinkedHashMap<>();
    private static LinkedHashMap<String, String> invertedMap = new LinkedHashMap<>();

    private static HashMap<String, Integer> errors = new HashMap<>();

    private static String exportTo = null;

    public static void main(String[] args) {
        String importFrom = null;

        DefaultArgParser argParser = new DefaultArgParser(args);

        try {
            importFrom = argParser.getImport();
        } catch (Exception ignored){ }

        try {
            exportTo = argParser.getExport();
        } catch (Exception ignored){ }

        if (importFrom != null) {
            importFile(importFrom);
            IOHandler.println("");
        }

        executeMenu();
    }

    private static void askCard(String cardName) {
        IOHandler.println("Print the definition of \"" + cardName + "\":");

        String answer = IOHandler.nextLine();

        if (answer.equals(map.get(cardName))) {
            IOHandler.println("Correct answer.");
        } else if (map.containsValue(answer)) {
            IOHandler.println("Wrong answer. The correct one is \"" + map.get(cardName) + "\", you've just" +
                    " written the definition of \"" + invertedMap.get(answer) + "\"");

            increaseCardErrors(cardName);
        } else {
            IOHandler.println("Wrong answer. The correct one is \"" + map.get(cardName) + "\".");

            increaseCardErrors(cardName);
        }
    }

    private static void increaseCardErrors(String cardName) {
        if (errors.containsKey(cardName)) {
            errors.replace(cardName, errors.get(cardName) + 1);
        } else {
            errors.put(cardName, 1);
        }
    }

    private static void executeMenu() {
        IOHandler.println("Input the action (add, remove, import, export, ask, exit, log, hardest card, reset stats):");

        String command = IOHandler.nextLine();

        if ("exit".equals(command)) {
            IOHandler.println("Bye bye!");

            if (exportTo != null) {
                exportToFile(exportTo);
            }

            return;
        }

        switch (command) {
            case "add":
                executeAdd();
                break;
            case "remove":
                executeRemove();
                break;
            case "import":
                executeImport();
                break;
            case "export":
                executeExport();
                break;
            case "ask":
                executeAsk();
                break;
            case "hardest card":
                executePrintHardest();
                break;
            case "reset stats":
                executeResetStats();
                break;
            case "log":
                executeLog();
                break;
        }

        IOHandler.println("");

        executeMenu();
    }

    private static void executeAdd() {
        String cardName = "";

        // get term
        IOHandler.println("The card:");

        cardName = IOHandler.nextLine();

        if (map.containsKey(cardName)) {
            IOHandler.println("The card \"" + cardName + "\" already exists.");
            return;
        }

        String definition = "";

        // get definition
        IOHandler.println("The definition of the card:");

        definition = IOHandler.nextLine();

        if (map.containsValue(definition)) {
            IOHandler.println("The definition \"" + definition + "\" already exists.");
            return;
        }

        map.put(cardName, definition);
        invertedMap.put(definition, cardName);

        IOHandler.println("The pair (\"" + cardName + "\":\"" + definition + "\") has been added.");
    }

    private static void executeRemove() {
        IOHandler.println("The card:");

        String card = IOHandler.nextLine();

        errors.remove(card);

        if (map.containsKey(card)) {
            map.remove(card);
            IOHandler.println("The card has been removed.");
        } else {
            IOHandler.println("Can't remove \"" + card + "\": the is no such card.");
        }
    }

    private static void executeImport() {
        IOHandler.println("File name:");

        String fileName = IOHandler.nextLine();

        importFile(fileName);
    }

    private static void importFile(String fileName) {
        File file = new File(fileName);

        if (!file.exists()) {
            IOHandler.println("File not found.");
            return;
        }

        int imported = 0;

        try (Scanner fileScanner = new Scanner(file)) {
            while(fileScanner.hasNextLine()) {
                String cardName = fileScanner.nextLine();
                String definition = fileScanner.nextLine();
                int mistakes = Integer.parseInt(fileScanner.nextLine());

                if (map.containsKey(cardName)) {
                    String oldDefinition = map.get(cardName);

                    invertedMap.remove(oldDefinition);

                    map.replace(cardName, definition);
                    invertedMap.put(definition, cardName);
                } else {
                    map.put(cardName, definition);
                    invertedMap.put(definition, cardName);
                }

                if (errors.containsKey(cardName)) {
                    errors.replace(cardName, mistakes);
                } else {
                    errors.put(cardName, mistakes);
                }

                imported++;
            }
        } catch (Exception e) {
            // should not get here anyway
            e.printStackTrace();
        }

        IOHandler.println(imported + " cards have been loaded.");
    }

    private static void executeExport() {
        IOHandler.println("File name:");

        String fileName = IOHandler.nextLine();

        exportToFile(fileName);
    }

    private static void exportToFile(String fileName) {
        try (PrintWriter writer = new PrintWriter(fileName)) {
            map.forEach((key, value) -> {
                writer.println(key);
                writer.println(value);

                if (errors.containsKey(key)) {
                    writer.println(errors.get(key));
                } else {
                    writer.println("0");
                }
            });
        } catch (IOException e) {
            IOHandler.println("Error exporting cards.");
        }

        IOHandler.println(map.size() + " cards have been saved.");
    }

    private static void executeAsk() {
        IOHandler.println("How many times to ask?");

        int times = Integer.parseInt(IOHandler.nextLine());

        Random random = new Random();

        for (int i = 0; i < times; i++) {
            int r = random.nextInt(map.size());
            String key = (String)map.keySet().toArray()[r];
            askCard(key);
        }
    }

    private static void executeLog() {
        IOHandler.println("File name:");

        String fileName = IOHandler.nextLine();

        try (PrintWriter writer = new PrintWriter(fileName)) {
            String[] log = IOHandler.getLog();

            for(String line: log) {
                writer.println(line);
            }
        } catch (IOException e) {
            IOHandler.println("Error exporting log.");
        }

        IOHandler.println("The log has been saved.");
    }

    private static void executePrintHardest() {
        int lookingFor = 0;

        for (Integer value: errors.values()) {
            if (value > lookingFor) {
                lookingFor = value;
            }
        }

        final int max = lookingFor; // weird

        ArrayList<String> cardNames = new ArrayList<>();

        errors.forEach((k, v) -> {
            if (v.equals(max)) {
                cardNames.add(k);
            }
        });

        if (cardNames.size() == 0) {
            IOHandler.println("There are no cards with errors.");
        } else if(cardNames.size() == 1) {
            IOHandler.println("The hardest card is \"" + cardNames.get(0) + "\". You have " + max + " errors answering it.");
        } else {
            String cards = cardNames.toString().replace("[", "\"").replace(", ", "\", \"").replace("]", "\"");
            IOHandler.println("The hardest cards are " + cards + ". You have " + max + " errors answering them.");
        }
    }

    private static void executeResetStats() {
        errors = new HashMap<>();
        IOHandler.println("Card statistics has been reset.");
    }
}
