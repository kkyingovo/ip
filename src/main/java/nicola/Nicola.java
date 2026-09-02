package nicola;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * A simple chatbot that greets the user, echoes commands, and exits on "bye".
 */
public class Nicola {
    private static final Ui ui = new Ui();
    private static final Storage storage = new Storage("data/nicola.txt");
    private static final Parser parser = new Parser();

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

        TaskList tasks = new TaskList(storage.loadTasks());

        while(ui.hasNextCommand()){
            String input = ui.readCommand();
            String command = parser.getCommandWord(input);
            String details = parser.getDetails(input);

            if (command.equals("bye")) {
                break;
            } else if (command.equals("list")) {
                listTasks(tasks);
            } else if (command.equals("todo")) {
                if(details.isBlank()){
                    System.out.println("Darling, the todo cannot be empty.");
                }else{
                    addTodo(tasks, details);
                }
            } else if (command.equals("deadline")) {
                if(details.isBlank()){
                    System.out.println("Darling, the deadline cannot be empty.");
                }else {
                    addDeadline(tasks, details);
                }
            } else if (command.equals("event")) {
                if(details.isBlank()) {
                    System.out.println("Darling, the event cannot be empty.");
                }else {
                    addEvent(tasks, details);
                }
            } else if (command.equals("mark")) {
                if(details.isBlank()){
                    System.out.println("Please give me a task number, dear.");
                } else {
                    markTask(tasks, details, true);
                }
            } else if (command.equals("unmark")) {
                if(details.isBlank()) {
                    System.out.println("Please give me a task number, dear.");
                } else {
                    markTask(tasks, details, false);
                }
            } else if (command.equals("delete")) {
                if(details.isBlank()) {
                    System.out.println("Please give me a task number, dear.");
                } else {
                    deleteTask(tasks, details);
                }
            } else {
                System.out.println("Sorry darling, I don't understand that.");
            }

            ui.showLine();
            storage.saveTasks(tasks.getTasks());
        }

        ui.showLine();
        ui.showGoodbye();
    }

    private static void listTasks(TaskList tasks){
        System.out.println("Here are your tasks babe.");
        for(int i = 0; i < tasks.size(); i++){
            System.out.println(" " + (i + 1) + "." + tasks.get(i).formatForList());
        }
    }

    private static void addTodo(TaskList tasks, String description){
        if(description.isBlank()){
            System.out.println("Darling, the todo cannot be empty.");
            return;
        }
        if(tasks.isFull()){
            System.out.println("Darling, your task list is full.");
            return;
        }

        tasks.add(new TodoTask(description.trim()));
        System.out.println("Sure dear. I've added this todo for you");
        System.out.println("  [T][ ] " + description.trim());
        System.out.println("Now you have " + tasks.size() + " tasks in your list.");
    }

    private static void addDeadline(TaskList tasks, String payload){
        if (tasks.isFull()) {
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


    private static void addEvent(TaskList tasks, String payload){
        if (tasks.isFull()) {
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

    private static void markTask(TaskList tasks, String text, boolean done){
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

    private static void deleteTask(TaskList tasks, String text){
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


