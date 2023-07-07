package co.harbor.calendly.utils;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.util.TimeZone;

public class TimeUtils {

    public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
    public static final String DEFAULT_TIME_ZONE = "UTC";

    static {
        DATE_FORMAT.setTimeZone(TimeZone.getTimeZone(ZoneId.of(DEFAULT_TIME_ZONE)));
    }

    public static Date addDays(Date oldDate, int days) {
        if (null != oldDate) {
            return Date.valueOf(oldDate.toLocalDate().plusDays(days));
        }

        return null;
    }
}
