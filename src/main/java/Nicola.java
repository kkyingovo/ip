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
        String[] tasks = new String[100];
        int taskCount = 0;

        while (!input.equals("bye")) {
            if (input.equals("list")){
                for(int i = 0; i < taskCount; i++){
                    System.out.println(" " + (i+1) + ". " + tasks[i]);
                }
            }else{
                tasks[taskCount] = input;
                taskCount++;
                System.out.println(" new task added: " + input);
            }

            System.out.println(LINE);
            input = scanner.nextLine();
        }

        System.out.println(LINE);
        System.out.println(" Bye. Hope to see you again. ");
        System.out.println(LINE);
    }
}
