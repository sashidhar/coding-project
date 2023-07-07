package co.harbor.calendly.controller;

import co.harbor.calendly.controller.helper.AvailabilityHelper;
import co.harbor.calendly.entity.UserAvailability;
import co.harbor.calendly.model.OverlappingAvailability;
import co.harbor.calendly.model.RecurringUserAvailability;
import co.harbor.calendly.model.Response;
import co.harbor.calendly.repository.IAvailabilityRepository;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.text.ParseException;
import java.util.List;

import static co.harbor.calendly.constants.Constants.*;
import static co.harbor.calendly.controller.helper.ErrorHelper.handleDataIntegrityViolationException;

/**
 * This is availability controller class. It has APIs to
 * **   1. Set availability for a user
 * **   2. Show availability for a user
 * **   3. Show overlapping availability for two users for a given date.
 * **   4. Set recurring availability for a user
 * **   5. Deleting availability for a user, the deletion availability can partially overlap
 *          with one or more existing availabilities
 *
 *    The Dates/timestamps are assumed to be in UTC. Assumption is that UI/front end layer does the conversion from
 *    other timezones to UTC.
 */
@Slf4j
@Data
@RestController
public class AvailabilityController {

    @Autowired
    private IAvailabilityRepository availabilityRepository;

    @Autowired
    private AvailabilityHelper availabilityHelper;

    private static final String DEFAULT_PAGE_NUMBER = "0";
    private static final String DEFAULT_PAGE_SIZE = "5";

    /**
     *
     * Sets the given availabilities
     *
     * @param availabilityList List of availability
     * @return {@link Response}
     */
    @PostMapping("/v1/availability")
    public ResponseEntity<Response> addAvailability(
            @RequestBody
            @NotEmpty
            List<UserAvailability> availabilityList) {
        try {
            availabilityRepository.saveAll(availabilityList);
            Response response = new Response(ADD_AVAILABILITY_API_RESPONSE_SUCCESS_MESSAGE, SUCCESS);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (DataIntegrityViolationException e) {
            return handleDataIntegrityViolationException(e, ADD_AVAILABILITY_UNIQUE_CONSTRAINT_VIOLATION_ERROR_MSG);
        }
    }

    /**
     * Shows availability for a given user.
     *
     * @param userId user id of the user
     * @param page starting page number for paginating the results
     * @param size page size for paginating the results
     * @return {@link Page<UserAvailability>}
     */
    @GetMapping("/v1/availability")
    public ResponseEntity<List<UserAvailability>> showAvailabilityForUser(
            @RequestParam(name = "user_id") @NotNull Integer userId,
            @RequestParam @NotNull String date,
            @RequestParam(defaultValue = DEFAULT_PAGE_NUMBER) int page,
            @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int size
    ) {
        try {
            return ResponseEntity.ok(availabilityHelper.findAvailabilityForAUserAndDate(userId, date));
        } catch (Exception e) {
            log.error("Exception parsing the given date {}", e.getMessage(), e);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error parsing the given date.");
        }
    }

    /**
     * Shows overlapping availability for two users. Specifying date is optional. When specified, overlapping availability is
     * show for that particular date or for all dates otherwise.
     * @param user1 user id of one user
     * @param user2 user id of another user
     * @param date optional date
     * @param page starting page number for paginating the results
     * @param size page size for paginating the results
     * @return
     */
    @GetMapping("/v1/overlap")
    public ResponseEntity<Page<OverlappingAvailability>> showOverlap(
        @RequestParam @NotNull Integer user1,
        @RequestParam @NotNull Integer user2,
        @RequestParam String date,
        @RequestParam(defaultValue = DEFAULT_PAGE_NUMBER) int page,
        @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int size
        ) {

        Pageable pageable = PageRequest.of(page, size);
        Page<OverlappingAvailability> overlappingAvailability = null;

        try {
            overlappingAvailability = availabilityHelper.findOverlappingAvailabilityForDate(user1, user2, date, pageable);
        } catch (ParseException e) {
            log.error("Exception showing overlapping schedules for users {}, {} for date {}", user1, user2, date, e);
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(overlappingAvailability);
    }

    /**
     * Sets recurring availability for a user.
     * Note: org.springframework.transaction.UnexpectedRollbackException occurs and transaction is silently rolled back if there is a unique constraint violation.
     * @param availability the recurring availability to set
     * @return {@link Response}
     */
    @Transactional
    @PostMapping("/v1/recurring")
    public ResponseEntity<Response> addRecurringAvailability(
            @RequestBody
            @NotBlank
            RecurringUserAvailability availability) {
        try {
            availabilityHelper.addRecurringAvailability(availability);
            Response response = new Response(RECURRING_AVAILABILITY_API_RESPONSE_SUCCESS_MESSAGE, SUCCESS);
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (DataIntegrityViolationException e) {
            return handleDataIntegrityViolationException(e, ADD_RECURRING_AVAILABILITY_UNIQUE_CONSTRAINT_VIOLATION_ERROR_MSG);
        }
    }

    /**
     * Deletes availability for a user
     * @param availabilityDeletion
     * @return {@link Response}
     */
    @DeleteMapping("/v1/availability")
    public ResponseEntity<Response> deleteAvailability(
            @RequestBody
            @NotBlank
            UserAvailability availabilityDeletion) {

        availabilityHelper.deleteAvailability(availabilityDeletion);

        Response response = new Response(DELETE_AVAILABILITY_API_RESPONSE_SUCCESS_MESSAGE, SUCCESS);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
