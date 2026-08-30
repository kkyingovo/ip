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
    private static final String LINE = "_______^_^___________________________________________________";
    private static final Path DATA_FILE = Paths.get("data", "nicola.txt");
    private static final int MAX_TASKS = 100;

    private static final DateTimeFormatter INPUT_DATE =
            DateTimeFormatter.ofPattern("uuuu-MM-dd");
    private static final DateTimeFormatter INPUT_DATE_TIME =
            DateTimeFormatter.ofPattern("uuuu-MM-dd HHmm");
    private static final DateTimeFormatter DISPLAY_DATE =
            DateTimeFormatter.ofPattern("MMM dd uuuu");
    private static final DateTimeFormatter DISPLAY_DATE_TIME =
            DateTimeFormatter.ofPattern("MMM dd uuuu HHmm");

    public static void main(String[] args) {
        System.out.println("Hello, this is Nicola.");
        System.out.println("How can I help you?");
        System.out.println(LINE);

        Scanner scanner = new Scanner(System.in);
        List<Task> tasks = loadTasks();

        while(scanner.hasNextLine()){
            String input = scanner.nextLine().trim();

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

            System.out.println(LINE);
            saveTasks(tasks);
        }

        System.out.println(LINE);
        System.out.println("Bye. I'll miss you.");
        System.out.println(LINE);
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

    private static List<Task> loadTasks(){
        List<Task> tasks = new ArrayList<>();

        if (!Files.exists(DATA_FILE)) {
            return tasks;
        }

        try {
            List<String> lines = Files.readAllLines(DATA_FILE, StandardCharsets.UTF_8);

            for (String line : lines) {
                String[] parts = line.split("\\|", -1);
                if (parts.length < 3) {
                    continue;
                }

                String type = parts[0].trim();
                boolean done = parts[1].trim().equals("1");

                if (type.equals("T")) {
                    TodoTask task = new TodoTask(parts[2].trim());
                    task.setDone(done);
                    tasks.add(task);
                } else if (type.equals("D")) {
                    String description = parts[2].trim();

                    if (parts.length >= 6 && parts[4].trim().equals("1")) {
                        LocalDateTime dateTime = LocalDateTime.parse(parts[5].trim());
                        DeadlineTask task = new DeadlineTask(description, null, dateTime, parts[5].trim());
                        task.setDone(done);
                        tasks.add(task);
                    } else if (parts.length >= 4) {
                        LocalDate date = LocalDate.parse(parts[3].trim());
                        DeadlineTask task = new DeadlineTask(description, date, null, parts[3].trim());
                        task.setDone(done);
                        tasks.add(task);
                    }
                } else if (type.equals("E")) {
                    if (parts.length >= 5) {
                        String description = parts[2].trim();
                        LocalDateTime from = LocalDateTime.parse(parts[3].trim());
                        LocalDateTime to = LocalDateTime.parse(parts[4].trim());
                        EventTask task = new EventTask(description, from, to);
                        task.setDone(done);
                        tasks.add(task);
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("Could not load saved tasks.");
        }

        return tasks;
    }

    private static void saveTasks(List<Task> tasks){
        try {
            Path parent = DATA_FILE.getParent();
            if (parent != null) {
                Files.createDirectories(parent);
            }

            StringBuilder content = new StringBuilder();
            for (Task task : tasks) {
                content.append(task.serialize()).append(System.lineSeparator());
            }

            Files.write(DATA_FILE, content.toString().getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            System.out.println("Could not save tasks.");
        }
    }

    private abstract static class Task{
        private final String description;
        private boolean done;

        Task(String description) {
            this.description = description;
        }

        void setDone(boolean done) {
            this.done = done;
        }

        boolean isDone() {
            return done;
        }

        String getDescription() {
            return description;
        }

        String formatForList() {
            return "[" + getType() + "][" + (done ? "X" : " ") + "] " + getDisplayText();
        }

        abstract String getType();

        abstract String getDisplayText();

        abstract String serialize();
    }

    private static class TodoTask extends Task{
        TodoTask(String description) {
            super(description);
        }

        @Override
        String getType() {
            return "T";
        }

        @Override
        String getDisplayText() {
            return getDescription();
        }

        @Override
        String serialize() {
            return "T | " + (isDone() ? "1" : "0") + " | " + getDescription();
        }
    }
    private static class DeadlineTask extends Task{
        private final LocalDate byDate;
        private final LocalDateTime byDateTime;
        private final String rawInput;

        DeadlineTask(String description, LocalDate byDate, LocalDateTime byDateTime, String rawInput) {
            super(description);
            this.byDate = byDate;
            this.byDateTime = byDateTime;
            this.rawInput = rawInput;
        }

        @Override
        String getType() {
            return "D";
        }

        @Override
        String getDisplayText() {
            if (byDateTime != null) {
                return getDescription() + " (by: " + byDateTime.format(DISPLAY_DATE_TIME) + ")";
            }
            if (byDate != null) {
                return getDescription() + " (by: " + byDate.format(DISPLAY_DATE) + ")";
            }
            return getDescription() + " (by: " + rawInput + ")";
        }

        @Override
        String serialize() {
            if (byDateTime != null) {
                return "D | " + (isDone() ? "1" : "0") + " | " + getDescription()
                        + " |  | 1 | " + byDateTime;
            }
            return "D | " + (isDone() ? "1" : "0") + " | " + getDescription()
                    + " | " + byDate + " | 0 | ";
        }
    }

    private static class EventTask extends Task{
        private final LocalDateTime from;
        private final LocalDateTime to;

        EventTask(String description, LocalDateTime from, LocalDateTime to) {
            super(description);
            this.from = from;
            this.to = to;
        }

        @Override
        String getType() {
            return "E";
        }

        @Override
        String getDisplayText() {
            return getDescription() + " (from: " + from.format(DISPLAY_DATE_TIME)
                    + " to: " + to.format(DISPLAY_DATE_TIME) + ")";
        }

        @Override
        String serialize() {
            return "E | " + (isDone() ? "1" : "0") + " | " + getDescription()
                    + " | " + from + " | " + to;
        }
    }
}


