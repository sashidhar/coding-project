package co.harbor.calendly.model;

import java.sql.Date;
import java.sql.Time;

public interface Availability {

    Date getDate();

    Time getStart();

    Time getEnd();

    Integer getUserId();
}
