import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Question4 {
    static Output objOutput;
    static int commandCounter = 0, saveIndex;
    static boolean hasAppend = false, hasInsert = false, cat = false;
    static HashMap<String, String> files = new HashMap<>();
    static String insertingText = "", catOutput = "";

    static class Output {
        String command;
        String subCommand = "";
        String options = "";
        ArrayList<String> flags = new ArrayList<>();
        ArrayList<String> arguments = new ArrayList<>();
        HashMap<Integer, String> optionArg = new HashMap<>();
        int optionArgCounter = 0;

        int flagCounter = 0, argumentCounter = 0;

    }

    public static void main(String[] args) {

        ArrayList<String> orders;
        ArrayList<String> regexes = new ArrayList<>();
        ArrayList<Pattern> patterns = new ArrayList<>();
        StringBuilder order = new StringBuilder();

        Matcher matcher;
        Scanner scanner = new Scanner(System.in);

        addRegexes(regexes);

        for (String regex : regexes)
            patterns.add(Pattern.compile(regex));

        while (scanner.hasNextLine()) {
            order.append(scanner.nextLine()).append(" ");
        }
        orders = orderSeparator(order.toString(), ';');
        orders.replaceAll(String::trim);

        for (String seperatedOrder : orders) {
            if (seperatedOrder.equals("exit")) {
                break;
            }

            int pipeCounter = 0;
            Matcher pipeMatcher = Pattern.compile("\\|").matcher(seperatedOrder);
            Matcher appendMatcher = Pattern.compile(">>").matcher(seperatedOrder);
            Matcher insertMatcher = Pattern.compile(">").matcher(seperatedOrder);
            while (appendMatcher.find()) if (!isBetweenQuotation(appendMatcher.start(), seperatedOrder)) {
                hasAppend = true;
                saveIndex = appendMatcher.start();
            }
            while (insertMatcher.find()) {
                if (!isBetweenQuotation(insertMatcher.start(), seperatedOrder)) {
                    hasInsert = true;
                    saveIndex = insertMatcher.start();
                }
            }
            while (pipeMatcher.find()) if (!isBetweenQuotation(pipeMatcher.start(), seperatedOrder)) pipeCounter++;
            if (pipeCounter == 0) {
                objOutput = new Output();
                for (int i = 0; i < 5; i++) {
                    matcher = patterns.get(i).matcher(seperatedOrder);
                    orderFinder(matcher, seperatedOrder, i);
                }
                printOutput(true);
                insertingText = "";
                catOutput = "";
            } else {
                int i = 0;
                ArrayList<String> pipedOrders = orderSeparator(seperatedOrder, '|');
                pipedOrders.replaceAll(String::trim);
                for (String pipedOrder : pipedOrders) {
                    objOutput = new Output();
                    for (int j = 0; j < 5; j++) {
                        matcher = patterns.get(j).matcher(pipedOrder);
                        orderFinder(matcher, pipedOrder, j);
                    }
                    printOutput(i++, true);
                    insertingText = "";
                    catOutput = "";
                }
            }
            hasAppend = false;
            hasInsert = false;
            cat = false;
        }
    }

    private static void orderFinder(Matcher matcher, String seperatedOrder, int i) {
        while (matcher.find()) {
            switch (i) {
                case 0 -> addCommand(matcher);
                case 1 -> addSubCommand(matcher);
                case 2 -> {
                    if (!isBetweenQuotation(matcher.start("option"), seperatedOrder)) addOption(matcher);
                }
                case 3 -> {
                    if (!isBetweenQuotation(matcher.start("flag"), seperatedOrder)) addFlag(matcher);
                }
                case 4 -> addArgument(matcher, seperatedOrder);
            }
        }
    }

    public static void printOutput(boolean print) {
        method();
        if (print) System.out.println(insertingText);
    }

    private static void method() {
        insertingText += "command " + commandCounter + "\ncommand: " + objOutput.command + "\n";
        if (!objOutput.subCommand.isEmpty()) insertingText += "subcommand: " + objOutput.subCommand + "\n";

        for (int i = 0; i < objOutput.options.length(); i++) {
            if (objOutput.optionArg.containsKey(i))
                insertingText += "option " + (i + 1) + ": " + objOutput.options.charAt(i) + " = " + objOutput.optionArg.get(i) + "\n";
            else insertingText += "option " + (i + 1) + ": " + objOutput.options.charAt(i) + "\n";
        }

        for (int i = 0; i < objOutput.flags.size(); i++)
            insertingText += "flag " + (i + 1) + ": " + objOutput.flags.get(i) + "\n";

        for (int i = 0; i < objOutput.arguments.size(); i++)
            insertingText += "argument " + (i + 1) + ": " + objOutput.arguments.get(i) + "\n";
        if (!catOutput.isEmpty()) insertingText += "\n" + catOutput + "\n";
    }

    public static void printOutput(int k, boolean print) {
        method();

        for (int i = 0; i < k; i++)
            insertingText += "input " + (i + 1) + ": command " + (commandCounter - k + i) + "\n";
        if (print) System.out.println(insertingText);
    }

    public static void addRegexes(ArrayList<String> regexes) {
        regexes.add("(?<command>^[\\w.]+)");
        regexes.add("(?<command>^[\\w.]+)\\s+(?<subCommand>[\\w.]+)");
        regexes.add("\\s+-(?<option>[\\w.]+)");
        regexes.add("\\s+--(?<flag>[\\w.]+)");
        regexes.add("\\s+(?<argument>(\"[^\"]+\")|([\\w.]+))");
    }

    public static ArrayList<String> orderSeparator(String order, char regexChar) {
        ArrayList<String> seperated = new ArrayList<>();
        int j = 0;
        for (int i = 0; i < order.length(); i++) {
            if (order.charAt(i) == regexChar) if (!isBetweenQuotation(i, order)) {
                seperated.add(order.substring(j, i));
                j = ++i;
            }
        }
        if (j != order.length()) {
            seperated.add(order.substring(j));
        }
        return seperated;
    }

    public static void addCommand(Matcher matcher) {
        commandCounter++;
        objOutput.command = matcher.group("command");
        if (matcher.group("command").equals("cat")) cat = true;
    }

    public static void addSubCommand(Matcher matcher) {
        objOutput.subCommand = matcher.group("subCommand");
    }

    public static void addOption(Matcher matcher) {
        for (int i = 0; i < matcher.group("option").length(); i++)
            objOutput.options += (matcher.group("option").charAt(i));
    }

    public static void addFlag(Matcher matcher) {
        objOutput.flagCounter++;
        objOutput.flags.add(matcher.group("flag"));
    }

    public static void addArgument(Matcher matcher, String input) {
        if (isValidArg(matcher.group("argument"))) {
            if (isFileName(input, matcher)) {
                if (!files.containsKey(matcher.group("argument"))) files.put(matcher.group("argument"), "");

                printOutput(false);
                if (hasAppend) {
                    files.put(matcher.group("argument"), files.get(matcher.group("argument")) + insertingText);
                } else if (hasInsert) {
                    files.put(matcher.group("argument"), "");
                    files.put(matcher.group("argument"), insertingText);
                }
                if (cat) {
                    catOutput = files.get(matcher.group("argument"));
                    objOutput.argumentCounter++;
                    objOutput.arguments.add((matcher.group("argument")));
                    objOutput.optionArg.put(objOutput.argumentCounter - 1, matcher.group("argument"));
                }
                insertingText = "";
            } else {
                if (!argIsAfterSave(input,matcher.start("argument"))) {
                    objOutput.argumentCounter++;
                    objOutput.arguments.add((matcher.group("argument")));
                    if (argIsAfterOpt(input, matcher.start("argument")))
                        objOutput.optionArg.put(objOutput.optionArgCounter++, matcher.group("argument"));
                }
            }
        }
    }

    public static boolean isBetweenQuotation(int index, String command) {
        int counter = 0;
        for (int i = 0; i < index; i++) {
            if (command.charAt(i) == '"') counter++;
        }
        return counter % 2 == 1;
    }

    public static boolean isValidArg(String arg) {
        if (objOutput.options.contains(arg)) return false;
        if (objOutput.flags.contains(arg)) return false;
        if (objOutput.command.equals(arg)) return false;
        if (objOutput.subCommand.equals(arg)) return false;
        return true;
    }

    public static boolean isFileName(String order, Matcher matcher) {
        if (cat) return true;
        int fileIndex = matcher.start("argument");
        for (int i = saveIndex + 1; i < fileIndex; i++)
            if (order.charAt(i) != ' ' && order.charAt(i) != '"') return false;
        if (fileIndex < saveIndex) return false;
        return true;
    }

    public static boolean argIsAfterOpt(String order, int index) {
        int i = index - 1;
        while (order.charAt(i--) == ' ')
            if (i == 0) return false;
        while (order.charAt(i--) != ' ')
            if (i == 0) return false;
        if (order.charAt(i + 2) == '-' && order.charAt(i + 3) != '-') return !isBetweenQuotation(i, order);
        return false;
    }

    public static boolean argIsAfterSave(String order, int index) {
        int i = index - 1;
        while (order.charAt(i--) == ' ')
            if (i == 0) return false;
        if (order.charAt(i) == '>')
            return !isBetweenQuotation(i, order);
        return false;
    }
}