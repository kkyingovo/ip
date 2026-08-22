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
        boolean[] isDone = new boolean[100];
        int taskCount = 0;

        while (!input.equals("bye")) {
            if (input.equals("list")){
                System.out.println("Here are your tasks babe.");
                for(int i = 0; i < taskCount; i++){
                    String status = isDone[i] ? "X" : " ";
                    System.out.println(" " + (i+1) + ".[" + status + "] " +tasks[i]);
                }
            }else if (input.startsWith("mark ")){
                int index = Integer.parseInt(input.substring(5));
                isDone[index-1] = true;
                System.out.println("Good job babe, I'm proud of you.");
                System.out.println("  [X] " + tasks[index-1]);

            }else if (input.startsWith("unmark ")){
                int index = Integer.parseInt(input.substring(7));
                isDone[index-1] = false;
                System.out.println("Yes babe, I've corrected the mistake.");
                System.out.println("  [ ] " + tasks[index-1]);
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
