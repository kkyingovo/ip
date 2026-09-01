import java.util.ArrayList;
import java.util.List;

public class TaskList {
    private static final int MAX_TASKS = 100;
    private final List<Task> tasks;

    public TaskList() {
        tasks = new ArrayList<>();
    }

    public TaskList(List<Task> loadedTasks) {
        tasks = new ArrayList<>(loadedTasks);
    }

    public int size(){
        return tasks.size();
    }

    public Task get(int index) {
        return tasks.get(index);
    }

    public void add(Task task) {
        if(tasks.size() < MAX_TASKS) {
            tasks.add(task);
        }
    }

    public Task remove (int index) {
        return tasks.remove(index);
    }

    public List<Task> getTasks() {
        return tasks;
    }

    public boolean isFull() {
        return tasks.size() >= MAX_TASKS;
    }
}
