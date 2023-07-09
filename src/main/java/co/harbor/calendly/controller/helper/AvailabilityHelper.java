package co.harbor.calendly.controller.helper;

import co.harbor.calendly.entity.UserAvailability;
import co.harbor.calendly.model.Interval;
import co.harbor.calendly.model.OverlappingAvailability;
import co.harbor.calendly.model.RecurringUserAvailability;
import co.harbor.calendly.repository.IAvailabilityRepository;
import co.harbor.calendly.utils.TimeUtils;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.util.VisibleForTesting;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Date;
import java.sql.Time;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * This a helper class for the AvailabilityController to get overlapping availabilities.
 * There are two helper methods.
 * **   1.  Overlapping availability for two users for a given date
 * **   2.  Overlapping availability for two users for all dates
 */
@Slf4j
@Component
public class AvailabilityHelper {

    @Autowired
    private IAvailabilityRepository availabilityRepository;

    /**
     * Returns overlapping availability for given two users on a given date
     *
     * @param user1 user_id of one user
     * @param user2 user_id of another user
     * @param dateStr  date for which overlapping availability is to be fetched
     * @param pageable {@link Pageable}
     * @return {@link OverlappingAvailability}
     */
    public Page<OverlappingAvailability> findOverlappingAvailabilityForDate(Integer user1, Integer user2, String dateStr, Pageable pageable) throws ParseException {
        Date _date  = Date.valueOf(dateStr); // DATE_FORMAT.parse(dateStr);
        return availabilityRepository.findOverlappingAvailabilityForDate(user1, user2, _date, pageable);
    }

    public Page<UserAvailability> findAvailabilityForAUser(Integer userId, Pageable pageable) {
        return availabilityRepository.findByUserid(userId, pageable);
    }

    public List<UserAvailability> findAvailabilityForAUserAndDate(Integer userId, String date) throws ParseException {
        return availabilityRepository.findBy_dateAndUserid(Date.valueOf(date), userId);
    }

    @Transactional
    public List<UserAvailability> deleteAvailability(UserAvailability availabilityDeletion) {

        Integer userid = availabilityDeletion.getUserid();
        Date date = availabilityDeletion.get_date();
        List<UserAvailability> existingAvailability = getExistingAvailabilityForUser(userid, date);
        List<UserAvailability> updatedAvailabilityList = new ArrayList<>();

        // Compute new updated availability based on overlap of deletion availability with existing availabilities
        for(UserAvailability availability : existingAvailability) {
            List<UserAvailability> computeUpdatedAvailability = computeUpdatedAvailability(availability, availabilityDeletion);
            updatedAvailabilityList.addAll(computeUpdatedAvailability);
        }

        // Delete existing availability and set new updated availability
        availabilityRepository.deleteAllByIdInBatch(existingAvailability.stream().map(a -> a.getId()).collect(Collectors.toList()));
        availabilityRepository.saveAll(updatedAvailabilityList);

        return updatedAvailabilityList;
    }

    List<UserAvailability> computeUpdatedAvailability(UserAvailability existingAvailability, UserAvailability availabilityDeletion) {

        long availabilityStartTime = existingAvailability.get_start().getTime();
        long availabilityEndTime = existingAvailability.get_end().getTime();

        long deletionStartTime = availabilityDeletion.get_start().getTime();
        long deletionEndTime = availabilityDeletion.get_end().getTime();

        List<UserAvailability> updatedAvailabilityList = new ArrayList<>();

        // There are 4 cases of overlap
        // Case 1. No overlap
        // Case 2. Deletion availability is exactly the same as existing existingAvailability, do nothing, this means no new existingAvailability is added to updatedAvailabilityList
        // Case 3. Deletion availability contained within the existing existingAvailability
        // Case 4. Deletion availability partially overlapping with existing existingAvailability

        if ( (deletionStartTime > availabilityStartTime && deletionEndTime > availabilityStartTime
                && deletionStartTime > availabilityEndTime && deletionEndTime > availabilityEndTime)
                ||
             ( deletionStartTime < availabilityStartTime && deletionEndTime < availabilityStartTime
                && deletionStartTime < availabilityEndTime && deletionEndTime < availabilityEndTime)
        ) {
            updatedAvailabilityList.add(newAvailability(existingAvailability.get_date(), existingAvailability.get_start(), existingAvailability.get_end(), existingAvailability.getUserid()));
        } else if ( deletionStartTime >= availabilityStartTime && deletionEndTime <= availabilityEndTime) {
             // Case 2 and Case 3
             updatedAvailabilityList.addAll(computeUpdatedAvailabilityForContainedDeletion(existingAvailability, availabilityDeletion));
         } else if (deletionStartTime >= availabilityStartTime &&  deletionEndTime > availabilityEndTime) {
             // Case 4
             updatedAvailabilityList.addAll(computeUpdatedAvailabilityForPartialOverlap1(existingAvailability, availabilityDeletion));
         } else if (deletionStartTime < availabilityStartTime && deletionEndTime < availabilityEndTime) {
            // Case 4
            updatedAvailabilityList.addAll(computeUpdatedAvailabilityForPartialOverlap2(existingAvailability, availabilityDeletion));
        }

        return updatedAvailabilityList;
    }

    private List<UserAvailability> computeUpdatedAvailabilityForPartialOverlap2(UserAvailability existingAvailability, UserAvailability availabilityDeletion) {
        List<UserAvailability> updatedAvailabilities = new ArrayList<>();

        Date date = existingAvailability.get_date();
        Time start = availabilityDeletion.get_end();
        Time end = existingAvailability.get_end();
        Integer userid = existingAvailability.getUserid();

        updatedAvailabilities.add(newAvailability(date, start, end, userid));

        return updatedAvailabilities;
    }

    private List<UserAvailability> computeUpdatedAvailabilityForPartialOverlap1(UserAvailability availability, UserAvailability availabilityDeletion) {

        List<UserAvailability> updatedAvailabilities = new ArrayList<>();

        Date date = availability.get_date();
        Time start = availability.get_start();
        Time end = availabilityDeletion.get_start();
        Integer userid = availability.getUserid();

        updatedAvailabilities.add(newAvailability(date, start, end, userid));

        return updatedAvailabilities;
    }

    @VisibleForTesting
    private List<UserAvailability> computeUpdatedAvailabilityForContainedDeletion(UserAvailability availability, UserAvailability availabilityDeletion) {
        List<UserAvailability> updatedAvailabilities = new ArrayList<>();
        long deletionStartTime = availabilityDeletion.get_start().getTime();
        long deletionEndTime = availabilityDeletion.get_end().getTime();

        Time availabilityStart = new Time(availability.get_start().getTime());
        Time deletionStart = new Time(deletionStartTime);
        Date date = availability.get_date();
        Integer userid = availability.getUserid();

        if (!availabilityStart.equals(deletionStart)) {
            updatedAvailabilities.add(newAvailability(date, availabilityStart, deletionStart, userid));
        }

        Time deletionEnd = new Time(deletionEndTime);
        Time availabilityEnd = new Time(availability.get_end().getTime());

        if (!deletionEnd.equals(availabilityEnd)) {
            updatedAvailabilities.add(newAvailability(date, deletionEnd, availabilityEnd, userid));
        }

        return updatedAvailabilities;
    }

    // Get existing availability for the given user and date
    private List<UserAvailability> getExistingAvailabilityForUser(Integer userid, Date date) {
        return availabilityRepository.findBy_dateAndUserid(date, userid);
    }

    public void addRecurringAvailability(RecurringUserAvailability availability) {
        List<UserAvailability> availabilities = computeRecurringAvailability(availability);

        availabilityRepository.saveAllAndFlush(availabilities);
    }

    List<UserAvailability> computeRecurringAvailability(RecurringUserAvailability availability) {
        Date startDate = availability.getStartdate();
        Integer numberOfOccurrences = availability.getOccurrences();
        Interval interval = Interval.valueOf(availability.getInterval().toUpperCase());

        List<UserAvailability> availabilities = new ArrayList<>();

        for(int i = 0; i < numberOfOccurrences; i++) {
            Date _date = TimeUtils.addDays(startDate, i * interval.getDays());

            UserAvailability userAvailability = newAvailability(_date, availability.get_start(), availability.get_end(), availability.getUserid());

            availabilities.add(userAvailability);
        }
        return availabilities;
    }

    private UserAvailability newAvailability(Date date, Time start, Time end, Integer userid) {
        UserAvailability newAvailability = new UserAvailability();
        newAvailability.set_date(date);
        newAvailability.set_start(start);
        newAvailability.set_end(end);
        newAvailability.setUserid(userid);

        return newAvailability;
    }

}
