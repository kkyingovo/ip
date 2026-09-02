package nicola;

public class Parser {
    public String getCommandWord(String input) {
        if (input.contains(" ")) {
            return input.substring(0, input.indexOf(" "));
        }

        return input;
    }

    public String getDetails(String input) {
        if (input.contains(" ")) {
            return input.substring(input.indexOf(" ") + 1).trim();
        }
        return " ";
    }
}
