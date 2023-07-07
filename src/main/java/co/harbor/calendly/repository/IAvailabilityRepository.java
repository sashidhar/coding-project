package co.harbor.calendly.repository;

import co.harbor.calendly.model.OverlappingAvailability;
import co.harbor.calendly.entity.UserAvailability;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Date;
import java.util.List;

/**
 * Repository for {@link UserAvailability} entity. Provides custom CRUD operations for user availability.
 */
@Transactional(readOnly = true)
public interface IAvailabilityRepository extends JpaRepository<UserAvailability, Integer> {

    Page<UserAvailability> findByUserid(@Param("userid") Integer userid, Pageable pageable);

    @Query(value = "SELECT id, _date, _start, _end, userid FROM user_availability WHERE userid = :userid AND _date = :date", nativeQuery = true)
    List<UserAvailability> findAvailabilityByUseridAndDate(Integer userid, Date date);

    @Query(value =
            "SELECT distinct\n" +
                    "\t\n" +
                    "\tu1._date as date,\n" +
                    "\tu1.userid as firstUser,\n" +
                    "\tu2.userid as secondUser,\n" +
                    "\t CASE WHEN u1._start > u2._start THEN u1._start ELSE u2._start END AS overlappingStartTime,\n" +
                    "\t CASE WHEN u1._end < u2._end THEN u1._end ELSE u2._end END AS overlappingEndTime\n" +
                    "\n" +
                    "FROM user_availability u1\n" +
                    "INNER JOIN user_availability u2\n" +
                    "ON u1.userid != u2.userid AND u1._date = u2._date\n" +
                    "AND (\n" +
                    "\tu1._start BETWEEN u2._start AND u2._end \n" +
                    "\tOR u1._end BETWEEN u2._start AND u2._end\n" +
                    ")\n" +
                    "WHERE u1._date = :date AND u1.userid = :user1 AND u2.userid= :user2"
    )
    Page<OverlappingAvailability> findOverlappingAvailabilityForDate(@Param("user1") Integer user1, @Param("user2") Integer user2, @Param("date") Date date, Pageable pageable);

    @Transactional
    @Override
    void deleteAllByIdInBatch(Iterable<Integer> integers);

    @Transactional
    @Override
    <S extends UserAvailability> List<S> saveAllAndFlush(Iterable<S> entities);
}
