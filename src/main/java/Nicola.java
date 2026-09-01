import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
/**
 * A simple chatbot that greets the user, echoes commands, and exits on "bye".
 */
public class Nicola {
    private static final int MAX_TASKS = 100;
    private static final Ui ui = new Ui();
    private static final Storage storage = new Storage("data/micola.txt");

    private static final DateTimeFormatter INPUT_DATE =
            DateTimeFormatter.ofPattern("uuuu-MM-dd");
    private static final DateTimeFormatter INPUT_DATE_TIME =
            DateTimeFormatter.ofPattern("uuuu-MM-dd HHmm");
    private static final DateTimeFormatter DISPLAY_DATE =
            DateTimeFormatter.ofPattern("MMM dd uuuu");
    private static final DateTimeFormatter DISPLAY_DATE_TIME =
            DateTimeFormatter.ofPattern("MMM dd uuuu HHmm");

    public static void main(String[] args) {
        ui.showWelcome();

        List<Task> tasks = storage.loadTasks();

        while(ui.hasNextCommand()){
            String input = ui.readCommand();

            if (input.equals("bye")) {
                break;
            } else if (input.equals("list")) {
                listTasks(tasks);
            } else if (input.equals("todo")) {
                System.out.println("Darling, the todo cannot be empty.");
            } else if (input.startsWith("todo ")) {
                addTodo(tasks, input.substring(5));
            } else if (input.equals("deadline")) {
                System.out.println("Darling, the deadline cannot be empty.");
            } else if (input.startsWith("deadline ")) {
                addDeadline(tasks, input.substring(9));
            } else if (input.equals("event")) {
                System.out.println("Darling, the event cannot be empty.");
            } else if (input.startsWith("event ")) {
                addEvent(tasks, input.substring(6));
            } else if (input.equals("mark")) {
                System.out.println("Please give me a task number, dear.");
            } else if (input.startsWith("mark ")) {
                markTask(tasks, input.substring(5), true);
            } else if (input.equals("unmark")) {
                System.out.println("Please give me a task number, dear.");
            } else if (input.startsWith("unmark ")) {
                markTask(tasks, input.substring(7), false);
            } else if (input.equals("delete")) {
                System.out.println("Please give me a task number, dear.");
            } else if (input.startsWith("delete ")) {
                deleteTask(tasks, input.substring(7));
            } else {
                System.out.println("Sorry darling, I don't understand that.");
            }

            ui.showLine();
            storage.saveTasks(tasks);
        }

        ui.showLine();
        ui.showGoodbye();
    }

    private static void listTasks(List<Task> tasks){
        System.out.println("Here are your tasks babe.");
        for(int i = 0; i < tasks.size(); i++){
            System.out.println(" " + (i + 1) + "." + tasks.get(i).formatForList());
        }
    }

    private static void addTodo(List<Task> tasks, String description){
        if(description.isBlank()){
            System.out.println("Darling, the todo cannot be empty.");
            return;
        }
        if(tasks.size() >= MAX_TASKS){
            System.out.println("Darling, your task list is full.");
            return;
        }

        tasks.add(new TodoTask(description.trim()));
        System.out.println("Sure dear. I've added this todo for you");
        System.out.println("  [T][ ] " + description.trim());
        System.out.println("Now you have " + tasks.size() + " tasks in your list.");
    }

    private static void addDeadline(List<Task> tasks, String payload){
        if (tasks.size() >= MAX_TASKS) {
            System.out.println("Darling, your task list is full.");
            return;
        }

        String[] parts = payload.split(" /by ", 2);
        if (parts.length < 2 || parts[0].isBlank() || parts[1].isBlank()) {
            System.out.println("Darling, the deadline needs a description and /by date.");
            return;
        }

        String description = parts[0].trim();
        String dateText = parts[1].trim();

        LocalDateTime dateTime = parseDateTime(dateText);
        LocalDate date = null;

        if(dateTime == null){
            date = parseData(dateText);
        }

        if(dateTime == null && date == null){
            System.out.println("Darling, please use a valid date like yyyy-MM-dd or yyyy-MM-dd HHmm.");
            return;
            }

        tasks.add(new DeadlineTask(description, date, dateTime, dateText));
        System.out.println("Sure dear. I've added this deadline for you");
        System.out.println("  [D][ ] " + tasks.get(tasks.size() - 1).getDisplayText());
        System.out.println("Now you have " + tasks.size() + " tasks in your list.");

        }


    private static void addEvent(List<Task> tasks, String payload){
        if (tasks.size() >= MAX_TASKS) {
            System.out.println("Darling, your task list is full.");
            return;
        }

        String[] parts = payload.split(" /from ", 2);
        if (parts.length < 2 || parts[0].isBlank()) {
            System.out.println("Darling, the event needs a description, /from time, and /to time.");
            return;
        }

        String description = parts[0].trim();
        String[] times = parts[1].split(" /to ", 2);
        if (times.length < 2 || times[0].isBlank() || times[1].isBlank()) {
            System.out.println("Darling, the event needs a description, /from time, and /to time.");
            return;
        }

        LocalDateTime from = parseDateTime(times[0].trim());
        LocalDateTime to = parseDateTime(times[1].trim());

        if (from == null || to == null) {
            System.out.println("Darling, please use valid date-time values like yyyy-MM-dd HHmm.");
            return;
        }

        tasks.add(new EventTask(description, from, to));
        System.out.println("Sure dear. I've added this event for you");
        System.out.println("  [E][ ] " + tasks.get(tasks.size() - 1).getDisplayText());
        System.out.println("Now you have " + tasks.size() + " tasks in your list.");
    }

    private static void markTask(List<Task> tasks, String text, boolean done){
        try {
            int index = Integer.parseInt(text.trim()) - 1;
            if (index < 0 || index >= tasks.size()) {
                System.out.println("The number has yet to be assigned a task.");
                return;
            }

            tasks.get(index).setDone(done);
            if (done) {
                System.out.println("Good job babe, I'm proud of you.");
            } else {
                System.out.println("Yes babe, I've corrected the mistake.");
            }
            System.out.println("  " + tasks.get(index).formatForList());
        } catch (NumberFormatException e) {
            System.out.println("Please give me a valid number.");
        }
    }

    private static void deleteTask(List<Task> tasks, String text){
        try {
            int index = Integer.parseInt(text.trim()) - 1;
            if (index < 0 || index >= tasks.size()) {
                System.out.println("The number has yet to be assigned a task.");
                return;
            }

            Task removed = tasks.remove(index);
            System.out.println("Babe, I've deleted the task.");
            System.out.println("  " + removed.formatForList());
            System.out.println("Now you have " + tasks.size() + " tasks in your list.");
        } catch (NumberFormatException e) {
            System.out.println("Please give me a valid task number, dear.");
        }
    }

    private static LocalDate parseData(String text){
        try {
            return LocalDate.parse(text, INPUT_DATE);
        } catch (DateTimeParseException e) {
            return null;
        }
    }

    private static LocalDateTime parseDateTime(String text) {
        try {
            return LocalDateTime.parse(text, INPUT_DATE_TIME);
        } catch (DateTimeParseException e) {
            return null;
        }
    }

}


