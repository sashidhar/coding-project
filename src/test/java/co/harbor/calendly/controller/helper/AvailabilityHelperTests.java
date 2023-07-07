package co.harbor.calendly.controller.helper;

import co.harbor.calendly.model.Interval;
import co.harbor.calendly.model.RecurringUserAvailability;
import co.harbor.calendly.entity.UserAvailability;
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

        UserAvailability existingAvailability = new UserAvailability();
        // existingAvailability.set_date(DATE_FORMAT.parse("2023-07-05"));
        existingAvailability.set_date(Date.valueOf("2023-07-05"));
        existingAvailability.set_start(Time.valueOf("10:00:00"));
        existingAvailability.set_end(Time.valueOf("11:00:00"));
        existingAvailability.setUserid(1);

        UserAvailability availabilityDeletion = new UserAvailability();
        // availabilityDeletion.set_date(DATE_FORMAT.parse("2023-07-05"));
        availabilityDeletion.set_date(Date.valueOf("2023-07-05"));
        availabilityDeletion.set_start(Time.valueOf("10:15:00"));
        availabilityDeletion.set_end(Time.valueOf("10:45:00"));
        availabilityDeletion.setUserid(1);

        List<UserAvailability> userAvailabilities = availabilityHelper.computeUpdatedAvailability(existingAvailability, availabilityDeletion);

        List<UserAvailability> expectedAvailabilities = new ArrayList<>();
        expectedAvailabilities.add(availabilityHelper.newAvailability(
                Date.valueOf("2023-07-05"),
                //DATE_FORMAT.parse("2023-07-05"),
                Time.valueOf("10:00:00"),
                Time.valueOf("10:15:00"),
                1
        ));
        expectedAvailabilities.add(availabilityHelper.newAvailability(
                //DATE_FORMAT.parse("2023-07-05"),
                Date.valueOf("2023-07-05"),
                Time.valueOf("10:45:00"),
                Time.valueOf("11:00:00"),
                1
        ));

        assertThat(userAvailabilities).isNotEmpty();
        assertThat(userAvailabilities).isEqualTo(expectedAvailabilities);
    }

    @Test
    public void testComputeUpdatedAvailabilityForPartialOverlap1() throws ParseException {

        UserAvailability existingAvailability = new UserAvailability();
        //existingAvailability.set_date(DATE_FORMAT.parse("2023-07-05"));
        existingAvailability.set_date(Date.valueOf("2023-07-05"));
        existingAvailability.set_start(Time.valueOf("10:00:00"));
        existingAvailability.set_end(Time.valueOf("11:00:00"));
        existingAvailability.setUserid(1);

        UserAvailability availabilityDeletion = new UserAvailability();
        // availabilityDeletion.set_date(DATE_FORMAT.parse("2023-07-05"));
        availabilityDeletion.set_date(Date.valueOf("2023-07-05"));
        availabilityDeletion.set_start(Time.valueOf("10:30:00"));
        availabilityDeletion.set_end(Time.valueOf("11:30:00"));
        availabilityDeletion.setUserid(1);

        List<UserAvailability> userAvailabilities = availabilityHelper.computeUpdatedAvailability(existingAvailability, availabilityDeletion);

        List<UserAvailability> expectedAvailabilities = new ArrayList<>();
        expectedAvailabilities.add(availabilityHelper.newAvailability(
                // DATE_FORMAT.parse("2023-07-05"),
                Date.valueOf("2023-07-05"),
                Time.valueOf("10:00:00"),
                Time.valueOf("10:30:00"),
                1
        ));

        assertThat(userAvailabilities).isNotEmpty();
        assertThat(userAvailabilities).isEqualTo(expectedAvailabilities);
    }

    @Test
    public void testComputeUpdatedAvailabilityForPartialOverlap2() throws ParseException {

        UserAvailability existingAvailability = new UserAvailability();
        // existingAvailability.set_date(DATE_FORMAT.parse("2023-07-05"));
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
        expectedAvailabilities.add(availabilityHelper.newAvailability(
                // DATE_FORMAT.parse("2023-07-05"),
                Date.valueOf("2023-07-05"),
                Time.valueOf("10:30:00"),
                Time.valueOf("11:00:00"),
                1
        ));

        assertThat(userAvailabilities).isNotEmpty();
        assertThat(userAvailabilities).isEqualTo(expectedAvailabilities);
    }

    @Test
    public void testAddRecurringAvailability() throws ParseException {

        RecurringUserAvailability recurringAvailability = new RecurringUserAvailability();
       //  recurringAvailability.setStartdate(DATE_FORMAT.parse("2023-07-06"));
        recurringAvailability.setStartdate(Date.valueOf("2023-07-06"));
        recurringAvailability.set_start(Time.valueOf("10:00:00"));
        recurringAvailability.set_end(Time.valueOf("11:00:00"));
        recurringAvailability.setInterval(Interval.WEEKLY.name());
        recurringAvailability.setOccurrences(3);
        recurringAvailability.setUserid(100);

        List<UserAvailability> actualRecurringAvailability = availabilityHelper.computeRecurringAvailability(recurringAvailability);

        List<UserAvailability> expectedRecurringAvailability = new ArrayList<>();
        expectedRecurringAvailability.add(availabilityHelper.newAvailability(
                //DATE_FORMAT.parse("2023-07-06"),
                Date.valueOf("2023-07-06"),
                Time.valueOf("10:00:00"),
                Time.valueOf("11:00:00"),
                100));

        expectedRecurringAvailability.add(availabilityHelper.newAvailability(
               // DATE_FORMAT.parse("2023-07-13"),
                Date.valueOf("2023-07-13"),
                Time.valueOf("10:00:00"),
                Time.valueOf("11:00:00"),
                100));

        expectedRecurringAvailability.add(availabilityHelper.newAvailability(
                // DATE_FORMAT.parse("2023-07-20"),
                Date.valueOf("2023-07-20"),
                Time.valueOf("10:00:00"),
                Time.valueOf("11:00:00"),
                100));

        assertThat(actualRecurringAvailability).isEqualTo(expectedRecurringAvailability);
    }

}
