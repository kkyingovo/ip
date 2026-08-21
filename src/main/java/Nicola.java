import java.util.Scanner;

/**
 * A simple chatbot that greets the user, echoes commands, and exits on "bye".
 */
public class Nicola {
    private static final String LINE = "_______^_^___________________________________________________";

    public static void main(String[] args) {
        System.out.println("Hello, this is Nicola.");
        System.out.println("How can I help you?");
        System.out.println(LINE);

        Scanner scanner = new Scanner(System.in);
        String input = scanner.nextLine();

        while (!input.equals("bye")) {
            System.out.println(LINE);
            System.out.println(" " + input);
            System.out.println(LINE);
            input = scanner.nextLine();
        }

        System.out.println(LINE);
        System.out.println(" Bye. Hope to see you again soon!");
        System.out.println(LINE);
    }
}
