package nicola;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

/**
 * Runs the chatbot Nicola and processes commands entered by the user.
 */
public class Nicola {
    private static final Ui ui = new Ui();
    private static final Storage storage = new Storage("data/nicola.txt");
    private static final Parser parser = new Parser();
    private static final DateTimeFormatter INPUT_DATE =
            DateTimeFormatter.ofPattern("uuuu-MM-dd");
    private static final DateTimeFormatter INPUT_DATE_TIME =
            DateTimeFormatter.ofPattern("uuuu-MM-dd HHmm");
    private String commandType;
    /**
     * Task list used by the JavaFX interface.
     */
    private final TaskList tasks = new TaskList(storage.loadTasks());

    public static void main(String[] args) {
        ui.showWelcome();

        TaskList tasks = new TaskList(storage.loadTasks());

        while (ui.hasNextCommand()) {
            String input = ui.readCommand();
            String command = parser.getCommandWord(input);
            String details = parser.getDetails(input);

            if (command.equals("bye")) {
                break;
            }

            executeCommand(command, details, tasks);

            ui.showLine();
            storage.saveTasks(tasks.getTasks());
        }

        ui.showLine();
        ui.showGoodbye();
    }

    private static void listTasks(TaskList tasks) {
        System.out.println("Here are your tasks.");
        for (int i = 0; i < tasks.size(); i++) {
            System.out.println(" " + (i + 1) + "." + tasks.get(i).formatForList());
        }
    }

    private static void addTodo(TaskList tasks, String description) {
        if (description.isBlank()) {
            System.out.println("The todo cannot be empty.");
            return;
        }
        if (tasks.isFull()) {
            System.out.println("Your task list is full.");
            return;
        }

        tasks.add(new TodoTask(description.trim()));
        System.out.println("Sure. I've added this todo for you");
        System.out.println("  [T][ ] " + description.trim());
        System.out.println("Now you have " + tasks.size() + " tasks in your list.");
    }

    private static void addDeadline(TaskList tasks, String payload) {
        if (tasks.isFull()) {
            System.out.println("Your task list is full.");
            return;
        }

        String[] parts = payload.split(" /by ", 2);
        if (parts.length < 2 || parts[0].isBlank() || parts[1].isBlank()) {
            System.out.println("The deadline needs a description and /by date.");
            return;
        }

        String description = parts[0].trim();
        String dateText = parts[1].trim();

        LocalDateTime dateTime = parseDateTime(dateText);
        LocalDate date = null;

        if (dateTime == null) {
            date = parseDate(dateText);
        }

        if (dateTime == null && date == null) {
            System.out.println("Please use a valid date like yyyy-MM-dd or yyyy-MM-dd HHmm.");
            return;
        }

        tasks.add(new DeadlineTask(description, date, dateTime, dateText));
        System.out.println("Sure. I've added this deadline for you");
        System.out.println("  [D][ ] " + tasks.get(tasks.size() - 1).getDisplayText());
        System.out.println("Now you have " + tasks.size() + " tasks in your list.");

    }


    private static void addEvent(TaskList tasks, String payload) {
        if (tasks.isFull()) {
            System.out.println("Your task list is full.");
            return;
        }

        String[] parts = payload.split(" /from ", 2);
        if (parts.length < 2 || parts[0].isBlank()) {
            System.out.println("The event needs a description, /from time, and /to time.");
            return;
        }

        String description = parts[0].trim();
        String[] times = parts[1].split(" /to ", 2);
        if (times.length < 2 || times[0].isBlank() || times[1].isBlank()) {
            System.out.println("The event needs a description, /from time, and /to time.");
            return;
        }

        LocalDateTime from = parseDateTime(times[0].trim());
        LocalDateTime to = parseDateTime(times[1].trim());

        if (from == null || to == null) {
            System.out.println("Please use valid date-time values like yyyy-MM-dd HHmm.");
            return;
        }

        tasks.add(new EventTask(description, from, to));
        System.out.println("Sure. I've added this event for you");
        System.out.println("  [E][ ] " + tasks.get(tasks.size() - 1).getDisplayText());
        System.out.println("Now you have " + tasks.size() + " tasks in your list.");
    }

    private static void markTask(TaskList tasks, String text, boolean done) {
        try {
            int index = Integer.parseInt(text.trim()) - 1;
            if (index < 0 || index >= tasks.size()) {
                System.out.println("The number has yet to be assigned a task.");
                return;
            }

            tasks.get(index).setDone(done);
            if (done) {
                System.out.println("Good job, I'm proud of you.");
            } else {
                System.out.println("Yes, I've corrected the mistake.");
            }
            System.out.println("  " + tasks.get(index).formatForList());
        } catch (NumberFormatException e) {
            System.out.println("Please give me a valid number.");
        }
    }

    private static void deleteTask(TaskList tasks, String text) {
        try {
            int index = Integer.parseInt(text.trim()) - 1;
            if (index < 0 || index >= tasks.size()) {
                System.out.println("The number has yet to be assigned a task.");
                return;
            }

            Task removed = tasks.remove(index);
            System.out.println("I've deleted the task.");
            System.out.println("  " + removed.formatForList());
            System.out.println("Now you have " + tasks.size() + " tasks in your list.");
        } catch (NumberFormatException e) {
            System.out.println("Please give me a valid task number.");
        }
    }

    private static LocalDate parseDate(String text) {
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

    private static void findTasks(TaskList tasks, String keyword) {
        keyword = keyword.trim().toLowerCase();

        System.out.println("Here are the matching tasks in your list:");

        int matchIndex = 1;
        for (int i = 0; i < tasks.size(); i++) {
            Task task = tasks.get(i);
            if (task.getDescription().toLowerCase().contains(keyword)) {
                System.out.println(" " + matchIndex + "." + task.formatForList());
                matchIndex++;
            }
        }
    }

    /**
     * Generates a response for a command entered through the GUI.
     *
     * @param input command entered by the user
     * @return Nicola's response
     */
    public String getResponse(String input) {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        PrintStream originalOutput = System.out;

        try (PrintStream capturedOutput =
                     new PrintStream(output, true, StandardCharsets.UTF_8)) {

            // Existing command methods print to System.out, so temporarily
            // collect that text for the JavaFX dialog box.
            System.setOut(capturedOutput);
            processCommand(input);

        } finally {
            // Always restore normal terminal output.
            System.setOut(originalOutput);
        }

        storage.saveTasks(tasks.getTasks());

        return output.toString(StandardCharsets.UTF_8).strip();
    }

    /**
     * Returns the command word most recently processed by the GUI.
     *
     * @return command word such as todo, mark, or delete
     */
    public String getCommandType() {
        return commandType;
    }

    /**
     * Identifies and executes a command entered through the GUI.
     *
     * @param input complete command entered by the user
     */
    private void processCommand(String input) {
        String command = parser.getCommandWord(input);
        String details = parser.getDetails(input);

        commandType = command;

        if (command.equals("bye")) {
            System.out.println("Bye. I'll miss you.");
            return;
        }

        executeCommand(command, details, tasks);
    }

    /**
     * Executes a parsed command using the given task list.
     *
     * @param command command word to execute
     * @param details details provided after the command word
     * @param tasks task list affected by the command
     */
    private static void executeCommand(
            String command,
            String details,
            TaskList tasks
    ) {
        switch (command) {
            case "list":
                listTasks(tasks);
                break;

            case "todo":
                if (details.isBlank()) {
                    System.out.println("The todo cannot be empty.");
                } else {
                    addTodo(tasks, details);
                }
                break;

            case "deadline":
                if (details.isBlank()) {
                    System.out.println("The deadline cannot be empty.");
                } else {
                    addDeadline(tasks, details);
                }
                break;

            case "event":
                if (details.isBlank()) {
                    System.out.println("The event cannot be empty.");
                } else {
                    addEvent(tasks, details);
                }
                break;

            case "mark":
                if (details.isBlank()) {
                    System.out.println("Please give me a task number.");
                } else {
                    markTask(tasks, details, true);
                }
                break;

            case "unmark":
                if (details.isBlank()) {
                    System.out.println("Please give me a task number.");
                } else {
                    markTask(tasks, details, false);
                }
                break;

            case "delete":
                if (details.isBlank()) {
                    System.out.println("Please give me a task number.");
                } else {
                    deleteTask(tasks, details);
                }
                break;

            case "find":
                findTasks(tasks, details);
                break;

            default:
                System.out.println("Sorry, I don't understand that.");
                break;
        }
    }

}


