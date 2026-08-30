import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Scanner;
/**
 * A simple chatbot that greets the user, echoes commands, and exits on "bye".
 */
public class Nicola {
    private static final String LINE = "_______^_^___________________________________________________";
    private static final Path DATA_FILE = Paths.get("data", "nicola.txt");

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

        taskCount = loadTasks(tasks, isDone, taskTypes);

        while (!input.equals("bye")) {
            if (input.equals("list")){
                System.out.println("Here are your tasks babe.");
                for(int i = 0; i < taskCount; i++){
                    String status = isDone[i] ? "X" : " ";
                    String type = taskTypes[i];
                    System.out.println(" " + (i+1) + ".[" + type + "][" + status + "] " +tasks[i]);
                }
            }else if(input.equals("deadline")){
                System.out.println("Darling, the deadline cannot be empty.");
            }else if (input.startsWith("deadline ")){
                if (taskCount >= tasks.length) {
                    System.out.println("Darling, your task list is full.");
                    System.out.println(LINE);
                    input = scanner.nextLine();
                    continue;
                }

                String task = input.substring(9);

                String[] parts = task.split(" /by ", 2);

                if(parts.length < 2 || parts[0].isEmpty() || parts[1].isEmpty()){
                    System.out.println("Darling, the deadline needs a description and /by time.");
                    }else{

                String taskDescription = parts[0];
                String ddl = parts[1];

                taskTypes[taskCount] = "D";
                tasks[taskCount] = taskDescription + " (by: " + ddl + ")";
                isDone[taskCount] = false;
                taskCount++;
                saveTasks(tasks, isDone, taskTypes, taskCount);

                System.out.println("Sure dear. I've added this deadline for you");
                System.out.println("  [D][ ] " + tasks[taskCount - 1]);
                System.out.println(" Now you have " + taskCount + " tasks in your list.");

            }}else if(input.equals("event")){
                System.out.println("Darling, the event cannot be empty.");
            }else if (input.startsWith("event ")){
                if (taskCount >= tasks.length) {
                    System.out.println("Darling, your task list is full.");
                    System.out.println(LINE);
                    input = scanner.nextLine();
                    continue;
                }

                String task = input.substring(6);
                if(task.isEmpty()){
                    System.out.println("Darling, the event cannot be empty.");
                }else{
                String[] parts = task.split(" /from ", 2);
                if(parts.length < 2 || parts[0].isEmpty()){
                    System.out.println("Darling, the event needs a description, /from time, and /to time.");
                }else{
                String des = parts[0];

                String[] parts2 = parts[1].split(" /to ", 2);
                if(parts2.length < 2 || parts2[0].isEmpty() || parts2[1].isEmpty()){
                    System.out.println("Darling, the event needs a description, /from time, and /to time.");
                }else {
                    String from = parts2[0];
                    String to = parts2[1];

                    taskTypes[taskCount] = "E";
                    tasks[taskCount] = des + " (from: " + from + " to: " + to + ")";
                    isDone[taskCount] = false;
                    taskCount++;
                    saveTasks(tasks, isDone, taskTypes, taskCount);

                    System.out.println("Sure dear. I've added this event for you");
                    System.out.println("  [E][ ] " + tasks[taskCount - 1]);
                    System.out.println(" Now you have " + taskCount + " tasks in your list.");

                }}}}else if(input.equals("todo")){
                System.out.println("Darling, the todo cannot be empty.");
            }else if (input.startsWith("todo ")){
                if (taskCount >= tasks.length) {
                    System.out.println("Darling, your task list is full.");
                    System.out.println(LINE);
                    input = scanner.nextLine();
                    continue;
                }

                String task = input.substring(5);
                if(task.isEmpty()){
                    System.out.println("Darling, the todo cannot be empty.");
                }else{
                taskTypes[taskCount] = "T";
                tasks[taskCount] = task;
                isDone[taskCount] = false;
                taskCount++;
                saveTasks(tasks, isDone, taskTypes, taskCount);

                System.out.println("Sure dear. I've added this todo");
                System.out.println("  [T][ ] " + tasks[taskCount - 1]);
                System.out.println(" Now you have " + taskCount + " tasks in your list.");

            }}else if(input.equals("mark")){
                System.out.println("Please give me a task number, dear.");
            }else if (input.startsWith("mark ")){
                try{
                int index = Integer.parseInt(input.substring(5));

                if(index < 1 || index > taskCount){
                    System.out.println("The number has yet to be assigned a task.");
                }else{
                isDone[index-1] = true;
                saveTasks(tasks, isDone, taskTypes, taskCount);

                System.out.println("Good job babe, I'm proud of you.");
                System.out.println("  [" + taskTypes[index-1] + "][X] " + tasks[index-1]);

            }}catch(NumberFormatException e){
                    System.out.println("Please give me a valid number.");
                }
            }else if(input.equals("unmark")){
                System.out.println("Please give me a task number, dear.");
            } else if (input.startsWith("unmark ")){
                try{
                int index = Integer.parseInt(input.substring(7));
                if(index < 1 || index > taskCount){
                        System.out.println("The number has yet to be assigned a task.");
                }else{
                isDone[index-1] = false;
                saveTasks(tasks, isDone, taskTypes, taskCount);

                System.out.println("Yes babe, I've corrected the mistake.");
                System.out.println("  [" + taskTypes[index-1] + "][ ] " + tasks[index-1]);
            }}catch(NumberFormatException e){
                    System.out.println("Please input a valid number.");
                }
            }else if(input.equals("delete")){
                System.out.println("Please give me a task number, dear.");

            }else if(input.startsWith("delete ")){
                try{
                    int index = Integer.parseInt(input.substring(7));

                    if (index < 1 || index > taskCount) {
                        System.out.println("The number has yet to be assigned a task.");

                    } else {
                        String deletedTask = tasks[index-1];
                        String deletedType = taskTypes[index-1];
                        boolean deletedDone = isDone[index-1];

                        for (int i = index - 1; i < taskCount - 1; i++) {
                            tasks[i] = tasks[i + 1];
                            taskTypes[i] = taskTypes[i + 1];
                            isDone[i] = isDone[i + 1];
                        }

                        taskCount--;
                        saveTasks(tasks, isDone, taskTypes, taskCount);

                        String status = deletedDone ? "X" : " ";
                        System.out.println("Babe, I've deleted the task.");
                        System.out.println("  [" + deletedType + "][" + status + "] " + deletedTask);
                        System.out.println("Now you have " + taskCount + " tasks in your list.");
                }}catch(NumberFormatException e){
                    System.out.println("Please give me a valid task number, dear.");
                    }

            }else{
                System.out.println(" Sorry darling, I don't understand that.");
            }

            System.out.println(LINE);
            input = scanner.nextLine();
        }

        System.out.println(LINE);
        System.out.println(" Bye. I'll miss you. ");
        System.out.println(LINE);
    }

    /**
     * load tasks from the saved arrays and return taskCount.
     */
    private static int loadTasks(String[] tasks, boolean[] isDone, String[] taskTypes){
        if(!Files.exists(DATA_FILE)){
            return 0;
        }

        try{
            List<String> lines = Files.readAllLines(DATA_FILE,  StandardCharsets.UTF_8);
            int count = 0;

            for(String line : lines){
                String[] parts = line.split("\\|", -1);
                if(parts.length < 3){
                    continue;
                }

                String type = parts[0].trim();
                String done = parts[1].trim();

                taskTypes[count] = type;
                isDone[count] = done.equals("1");

                if(type.equals("T")){
                    tasks[count] = parts[2].trim();
                }else if(type.equals("D") && parts.length >= 4){
                    tasks[count] = parts[2].trim() + " (by: " + parts[3].trim() + ")";
                }else if(type.equals("E") && parts.length >= 5){
                    tasks[count] = parts[2].trim() + " (from: " + parts[3].trim() + " to: " + parts[4].trim() + ")";
                }else{
                    continue;
                }

                count++;
            }

            return count;
        }catch (IOException e){
            System.out.println("Could not load saved tasks.");
            return 0;
        }
    }

    /**
     * save tasks to disk
     */
    private static void saveTasks(String[] tasks, boolean[] isDone, String[] taskTypes, int taskCount) {
        try {
            Path parent = DATA_FILE.getParent();
            if (parent != null) {
                Files.createDirectories(parent);
            }

            StringBuilder content = new StringBuilder();
            for (int i = 0; i < taskCount; i++) {
                content.append(taskTypes[i]).append(" | ")
                        .append(isDone[i] ? "1" : "0")
                        .append(" | ");

                if (taskTypes[i].equals("T")) {
                    content.append(tasks[i]);
                } else if (taskTypes[i].equals("D")) {
                    content.append(extractDeadlineDescription(tasks[i])).append(" | ")
                            .append(extractDeadlineDate(tasks[i]));
                } else if (taskTypes[i].equals("E")) {
                    content.append(extractEventDescription(tasks[i])).append(" | ")
                            .append(extractEventFrom(tasks[i])).append(" | ")
                            .append(extractEventTo(tasks[i]));
                }

                content.append(System.lineSeparator());
            }

            Files.write(DATA_FILE, content.toString().getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            System.out.println("Could not save tasks.");
        }
    }

    /**
     * extract ddl text
     */
    private static String extractDeadlineDescription(String task){
        int index = task.indexOf(" (by: ");
        return index == -1 ? task : task.substring(0, index);
    }

    /**
     * extract ddl date
     */
    private static String extractDeadlineDate(String task){
        int start = task.indexOf(" (by: ");
        if(start == -1){
            return "";
        }
        return task.substring(start + 6, task.length() - 1);
    }

    /**
     * extract event text
     */
    private static String extractEventDescription (String task){
        int index = task.indexOf(" (from: ");
        return index == -1 ? task : task.substring(0, index);
    }

    /**
     * extract event start time
     */
    private static String extractEventFrom (String task){
        int start = task.indexOf(" (from: ");
        int middle = task.indexOf(" to: ");
        if (start == -1 || middle == -1) {
            return "";
        }
        return task.substring(start + 8, middle);
    }

    /**
     * extract event end time
     */
    private static String extractEventTo(String task){
        int middle = task.indexOf(" to: ");
        if (middle == -1) {
            return "";
        }
        return task.substring(middle + 5, task.length() - 1);
    }
}
