package flashcards;

import java.util.ArrayList;
import java.util.Scanner;

public class IOHandler {
    private static ArrayList<String> log = new ArrayList<>();
    private static Scanner scanner = new Scanner(System.in);

    public static String nextLine() {
        String line = scanner.nextLine();

        log.add(line);

        return line;
    }

    public static void println(String line) {
        System.out.println(line);

        log.add(line);
    }

    public static String[] getLog() {
        String[] arr = new String[log.size()];
        int i = 0;

        for (String s : log) {
            arr[i] = s;
            i++;
        }

        return arr;
    }
}
