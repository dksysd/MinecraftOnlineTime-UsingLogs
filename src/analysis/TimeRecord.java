package analysis;

import java.time.Duration;
import java.time.LocalTime;

public record TimeRecord(LocalTime start, LocalTime end) {
    public long betweenSeconds() {
        return Duration.between(start, end).getSeconds();
    }
}
