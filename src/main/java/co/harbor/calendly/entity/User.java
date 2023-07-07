package co.harbor.calendly.entity;

import lombok.Data;

import javax.persistence.*;

/**
 * Represents user entity in the database.
 */
@Data
@Entity(name = "user")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "fname")
    private String firstName;

    @Column(name = "lname")
    private String lastName;

    @Column(name = "email", unique = true)
    private String emailId;
}
