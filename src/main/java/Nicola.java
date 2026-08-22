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
        String[] taskTypes = new String[100];
        int taskCount = 0;

        while (!input.equals("bye")) {
            if (input.equals("list")){
                System.out.println("Here are your tasks babe.");
                for(int i = 0; i < taskCount; i++){
                    String status = isDone[i] ? "X" : " ";
                    String type = taskTypes[i];
                    System.out.println(" " + (i+1) + ".[" + type + "][" + status + "] " +tasks[i]);
                }
            }else if (input.startsWith("deadline ")){
                String task = input.substring(9);
                String[] parts = task.split(" /by ", 2);

                String taskDescription = parts[0];
                String ddl = parts[1];

                taskTypes[taskCount] = "D";
                tasks[taskCount] = taskDescription + " (by: " + ddl + ")";
                taskCount++;

                System.out.println("Sure dear. I've added this deadline for you");
                System.out.println("  [D][ ] " + tasks[taskCount - 1]);
                System.out.println(" Now you have " + taskCount + " tasks in your list.");

            }else if (input.startsWith("event ")){

                String task = input.substring(6);
                String[] parts = task.split(" /from ", 2);
                String des = parts[0];

                String[] parts2 = parts[1].split(" /to ", 2);
                String from = parts2[0];
                String to = parts2[1];

                taskTypes[taskCount] = "E";
                tasks[taskCount] = des + " (from: " + from + " to: " + to + ")";
                taskCount++;

                System.out.println("Sure dear. I've added this event for you");
                System.out.println("  [E][ ] " + tasks[taskCount - 1]);
                System.out.println(" Now you have " + taskCount + " tasks in your list.");

            }else if (input.startsWith("todo ")){
                String task = input.substring(5);
                taskTypes[taskCount] = "T";
                tasks[taskCount] = task;
                taskCount++;

                System.out.println("Sure dear. I've added this todo");
                System.out.println("  [T][ ] " + tasks[taskCount - 1]);
                System.out.println(" Now you have " + taskCount + " tasks in your list.");

            }else if (input.startsWith("mark ")){
                int index = Integer.parseInt(input.substring(5));
                isDone[index-1] = true;
                System.out.println("Good job babe, I'm proud of you.");
                System.out.println("  [" + taskTypes[index-1] + "][X] " + tasks[index-1]);

            }else if (input.startsWith("unmark ")){
                int index = Integer.parseInt(input.substring(7));
                isDone[index-1] = false;
                System.out.println("Yes babe, I've corrected the mistake.");
                System.out.println("  [" + taskTypes[index-1] + "][ ] " + tasks[index-1]);
            }else{
                System.out.println(" Sorry darling, I don't understand that command yet.");
            }

            System.out.println(LINE);
            input = scanner.nextLine();
        }

        System.out.println(LINE);
        System.out.println(" Bye. Hope to see you again. ");
        System.out.println(LINE);
    }
}
