import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Storage {
    private final Path filePath;

    public Storage(String filePath) {
        this.filePath = Paths.get(filePath);
    }

    public List<Task> loadTasks(){
        List<Task> tasks = new ArrayList<>();

        if (!Files.exists(filePath)) {
            return tasks;
        }

        try {
            List<String> lines = Files.readAllLines(filePath, StandardCharsets.UTF_8);

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
                    if (parts.length >= 5
                            && !parts[3].trim().isEmpty()
                            && !parts[4].trim().isEmpty()) {

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

    public void saveTasks(List<Task> tasks){
        try {
            Path parent = filePath.getParent();
            if (parent != null) {
                Files.createDirectories(parent);
            }

            StringBuilder content = new StringBuilder();
            for (Task task : tasks) {
                content.append(task.serialize()).append(System.lineSeparator());
            }

            Files.write(filePath, content.toString().getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            System.out.println("Could not save tasks.");
        }
    }
}
