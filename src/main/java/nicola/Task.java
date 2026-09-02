package nicola;

/**
 * a task with description and completion status.
 */
public abstract class Task {
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
