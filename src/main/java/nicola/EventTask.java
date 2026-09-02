package nicola;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class EventTask extends Task {
    private final LocalDateTime from;
    private final LocalDateTime to;
    private static final DateTimeFormatter DISPLAY_DATE = DateTimeFormatter.ofPattern("MMM dd uuuu");
    private static final DateTimeFormatter DISPLAY_DATE_TIME = DateTimeFormatter.ofPattern("MMM dd uuuu HHmm");

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