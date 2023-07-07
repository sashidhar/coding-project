package co.harbor.calendly.model;

import lombok.Data;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.Max;
import java.sql.Date;
import java.sql.Time;

/**
 * Represents user availability entity in the database.
 */
@Validated
@Data
public class RecurringUserAvailability {
    private Integer id;
    private Date startdate;
    private Time _start;
    private Time _end;
    private Integer userid;
    private String interval;

    @Max(100)
    private Integer occurrences = 30;
}

