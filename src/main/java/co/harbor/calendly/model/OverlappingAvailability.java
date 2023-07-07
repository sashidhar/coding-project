package co.harbor.calendly.model;

import java.sql.Date;
import java.sql.Time;

/**
 * This is a custom POJO to map the result of overlapping availability SQL query response.
 */
public interface OverlappingAvailability {

    Date getDate();

    Integer getFirstUser();

    Integer getSecondUser();

    Time getOverlappingStartTime();

    Time getOverlappingEndTime();

}
