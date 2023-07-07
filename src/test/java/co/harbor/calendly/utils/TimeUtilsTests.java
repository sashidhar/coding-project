package co.harbor.calendly.utils;


import org.junit.jupiter.api.Test;

import java.sql.Date;

import static org.assertj.core.api.Assertions.assertThat;

public class TimeUtilsTests {

    @Test
    public void testAddDaysForNullDate() {
        Date actualDate = TimeUtils.addDays(null, 0);
        assertThat(actualDate).isNull();
    }

    @Test
    public void testAddDays() {
        Date actualDate = TimeUtils.addDays(Date.valueOf("2023-07-07"), 7);
        Date expectedDate = Date.valueOf("2023-07-14");
        assertThat(actualDate).isEqualTo(expectedDate);
    }




}
