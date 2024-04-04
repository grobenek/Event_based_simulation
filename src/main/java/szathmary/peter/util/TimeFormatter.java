package szathmary.peter.util;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

/** Created by petos on 03/04/2024. */
public class TimeFormatter {
    private static final LocalTime STARTING_TIME_OF_SIMULATION = LocalTime.of(9, 0, 0);

    public static String getFormattedTime(double timeInSeconds) {
        if (Double.isNaN(timeInSeconds)) {
            return null;
        }

    return STARTING_TIME_OF_SIMULATION
        .plusSeconds((long) timeInSeconds)
        .format(DateTimeFormatter.ofPattern("HH:mm:ss"));
    }
}
