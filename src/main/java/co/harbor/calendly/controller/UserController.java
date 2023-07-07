package co.harbor.calendly.controller;

import co.harbor.calendly.model.Response;
import co.harbor.calendly.entity.User;
import co.harbor.calendly.repository.IUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotEmpty;
import java.util.List;
import java.util.Optional;

import static co.harbor.calendly.constants.Constants.*;
import static co.harbor.calendly.controller.helper.ErrorHelper.handleDataIntegrityViolationException;

/**
 * This is a controller class for Users. It has APIs to
 * **   1. Add users
 * **   2. Get all users
 * **   3. Get user by email
 */
@RestController
public class UserController {
    @Autowired
    private IUserRepository userRepository;

    private static final String DEFAULT_PAGE_NUMBER = "0";
    private static final String DEFAULT_PAGE_SIZE = "10";

    /**
     * Adds new users
     * @param users list of users
     * @return {@link Response}
     */
    @PostMapping("/v1/users")
    public ResponseEntity<Response> addUsers(
            @RequestBody
            @NotEmpty
            List<User> users) {
        try {
            userRepository.saveAll(users);
            Response response = new Response(ADD_USERS_API_RESPONSE_SUCCESS_MESSAGE, SUCCESS);
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (DataIntegrityViolationException e) {
            return handleDataIntegrityViolationException(e, ADD_USERS_UNIQUE_CONSTRAINT_VIOLATION_ERROR_MSG);
        }
    }

    /**
     * Gets all users
     * @param page starting page number for paginating the results
     * @param size size of each page for paginating the results
     * @return {@link Page<User>}
     */
    @GetMapping("/v1/users")
    public ResponseEntity<Page<User>> getUsers(
            @RequestParam(defaultValue = DEFAULT_PAGE_NUMBER) int page,
            @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<User> users = userRepository.findAll(pageable);
        return ResponseEntity.ok(users);
    }

    /**
     * Gets user by email id
     * @param email user email
     * @return {@link User}
     */
    @GetMapping("/v1/user")
    public ResponseEntity<User> getUserByEmail(
            @RequestParam String email
    ) {
        return new ResponseEntity<>(userRepository.findByEmailId(email), HttpStatus.OK);
    }

    /**
     * Gets user by user id.
     * @param userId user's id
     * @return {@link User}
     */
    @GetMapping("/v1/user/{id}")
    public ResponseEntity<Optional<User>> getUserById(
            @PathVariable("id") Integer userId
    ) {
        Optional<User> user = userRepository.findById(userId);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

}
