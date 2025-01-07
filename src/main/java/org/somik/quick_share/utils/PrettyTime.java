package org.somik.quick_share.utils;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class PrettyTime {
    public static final List<Long> times = Arrays.asList(
            TimeUnit.DAYS.toMillis(365),
            TimeUnit.DAYS.toMillis(30),
            TimeUnit.DAYS.toMillis(1),
            TimeUnit.HOURS.toMillis(1),
            TimeUnit.MINUTES.toMillis(1),
            TimeUnit.SECONDS.toMillis(1));

    public static final List<String> timesString = Arrays.asList("year", "month", "day", "hour", "min", "sec");

    public static String toDuration(long duration) {
        duration = Math.abs(duration);

        String time = "now";
        for (int i = 0; i < times.size(); i++) {
            long x = duration / times.get(i);
            if (x > 0) {
                time = String.format("%d %s%s", x, timesString.get(i), x != 1 ? "s" : "");
                break;
            }
        }
        return time;
    }
}
