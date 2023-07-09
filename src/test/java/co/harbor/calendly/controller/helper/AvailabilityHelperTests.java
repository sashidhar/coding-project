package co.harbor.calendly.controller.helper;

import co.harbor.calendly.entity.UserAvailability;
import co.harbor.calendly.model.Interval;
import co.harbor.calendly.model.RecurringUserAvailability;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.sql.Date;
import java.sql.Time;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class AvailabilityHelperTests {

    @Autowired
    private AvailabilityHelper availabilityHelper;

    @Test
    public void testComputeUpdatedAvailabilityForContainedDeletion() throws ParseException {

        UserAvailability existingAvailability = newAvailability("2023-07-05", "10:00:00", "11:00:00", 1);

        UserAvailability availabilityDeletion = newAvailability("2023-07-05", "10:15:00", "10:45:00", 1);

        List<UserAvailability> userAvailabilities = availabilityHelper.computeUpdatedAvailability(existingAvailability, availabilityDeletion);

        List<UserAvailability> expectedAvailabilities = new ArrayList<>();
        expectedAvailabilities.add(newAvailability("2023-07-05", "10:00:00", "10:15:00", 1));
        expectedAvailabilities.add(newAvailability("2023-07-05", "10:45:00", "11:00:00", 1));

        assertThat(userAvailabilities).isNotEmpty();
        assertThat(userAvailabilities).isEqualTo(expectedAvailabilities);
    }

    @Test
    public void testComputeUpdatedAvailabilityForPartialOverlap1() throws ParseException {

        UserAvailability existingAvailability = new UserAvailability();
        existingAvailability.set_date(Date.valueOf("2023-07-05"));
        existingAvailability.set_start(Time.valueOf("10:00:00"));
        existingAvailability.set_end(Time.valueOf("11:00:00"));
        existingAvailability.setUserid(1);

        UserAvailability availabilityDeletion = new UserAvailability();
        availabilityDeletion.set_date(Date.valueOf("2023-07-05"));
        availabilityDeletion.set_start(Time.valueOf("10:30:00"));
        availabilityDeletion.set_end(Time.valueOf("11:30:00"));
        availabilityDeletion.setUserid(1);

        List<UserAvailability> userAvailabilities = availabilityHelper.computeUpdatedAvailability(existingAvailability, availabilityDeletion);

        List<UserAvailability> expectedAvailabilities = new ArrayList<>();
        expectedAvailabilities.add(newAvailability("2023-07-05", "10:00:00", "10:30:00", 1));

        assertThat(userAvailabilities).isNotEmpty();
        assertThat(userAvailabilities).isEqualTo(expectedAvailabilities);
    }

    @Test
    public void testComputeUpdatedAvailabilityForPartialOverlap2() throws ParseException {

        UserAvailability existingAvailability = new UserAvailability();
        existingAvailability.set_date(Date.valueOf("2023-07-05"));
        existingAvailability.set_start(Time.valueOf("10:00:00"));
        existingAvailability.set_end(Time.valueOf("11:00:00"));
        existingAvailability.setUserid(1);

        UserAvailability availabilityDeletion = new UserAvailability();
        // availabilityDeletion.set_date(DATE_FORMAT.parse("2023-07-05"));
        availabilityDeletion.set_date(Date.valueOf("2023-07-05"));
        availabilityDeletion.set_start(Time.valueOf("09:45:00"));
        availabilityDeletion.set_end(Time.valueOf("10:30:00"));
        availabilityDeletion.setUserid(1);

        List<UserAvailability> userAvailabilities = availabilityHelper.computeUpdatedAvailability(existingAvailability, availabilityDeletion);

        List<UserAvailability> expectedAvailabilities = new ArrayList<>();
        expectedAvailabilities.add(newAvailability("2023-07-05", "10:30:00", "11:00:00", 1));

        assertThat(userAvailabilities).isNotEmpty();
        assertThat(userAvailabilities).isEqualTo(expectedAvailabilities);
    }

    @Test
    public void testComputeUpdatedAvailabilityNoOverlap() throws ParseException {
        UserAvailability existingAvailability = new UserAvailability();
        existingAvailability.set_date(Date.valueOf("2023-07-05"));
        existingAvailability.set_start(Time.valueOf("15:30:00"));
        existingAvailability.set_end(Time.valueOf("16:30:00"));
        existingAvailability.setUserid(1);

        UserAvailability availabilityDeletion = new UserAvailability();
        availabilityDeletion.set_date(Date.valueOf("2023-07-05"));
        availabilityDeletion.set_start(Time.valueOf("20:40:00"));
        availabilityDeletion.set_end(Time.valueOf("21:00:00"));
        availabilityDeletion.setUserid(1);

        List<UserAvailability> userAvailabilities = availabilityHelper.computeUpdatedAvailability(existingAvailability, availabilityDeletion);

        List<UserAvailability> expectedAvailabilities = new ArrayList<>();
        expectedAvailabilities.add(newAvailability("2023-07-05", "15:30:00", "16:30:00", 1));

        assertThat(userAvailabilities).isNotEmpty();
        assertThat(userAvailabilities).isEqualTo(expectedAvailabilities);
    }

    @Test
    public void testAddRecurringAvailability() throws ParseException {

        RecurringUserAvailability recurringAvailability = new RecurringUserAvailability();
        recurringAvailability.setStartdate(Date.valueOf("2023-07-06"));
        recurringAvailability.set_start(Time.valueOf("10:00:00"));
        recurringAvailability.set_end(Time.valueOf("11:00:00"));
        recurringAvailability.setInterval(Interval.WEEKLY.name());
        recurringAvailability.setOccurrences(3);
        recurringAvailability.setUserid(100);

        List<UserAvailability> actualRecurringAvailability = availabilityHelper.computeRecurringAvailability(recurringAvailability);

        List<UserAvailability> expectedRecurringAvailability = new ArrayList<>();
        expectedRecurringAvailability.add(newAvailability("2023-07-06","10:00:00","11:00:00", 100));
        expectedRecurringAvailability.add(newAvailability("2023-07-13", "10:00:00", "11:00:00", 100));
        expectedRecurringAvailability.add(newAvailability("2023-07-20", "10:00:00", "11:00:00", 100));

        assertThat(actualRecurringAvailability).isEqualTo(expectedRecurringAvailability);
    }

    private UserAvailability newAvailability(String date, String start, String end, Integer userid) {
        UserAvailability newAvailability = new UserAvailability();
        newAvailability.set_date(Date.valueOf(date));
        newAvailability.set_start(Time.valueOf(start));
        newAvailability.set_end(Time.valueOf(end));
        newAvailability.setUserid(userid);

        return newAvailability;
    }

}
