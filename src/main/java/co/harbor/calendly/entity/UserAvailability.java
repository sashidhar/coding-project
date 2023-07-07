package co.harbor.calendly.entity;


import lombok.Data;

import javax.persistence.*;
import java.sql.Date;
import java.sql.Time;

/**
 * Represents user availability entity in the database.
 */
@Data
@Entity(name = "user_availability")
@Table(uniqueConstraints = {@UniqueConstraint(columnNames = { "_date", "_start", "_end", "userid" })})
public class UserAvailability {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "_date")
    private Date _date;

    @Column(name = "_start")
    private Time _start;

    @Column(name = "_end")
    private Time _end;

    @Column(name = "userid")
    private Integer userid;
}
