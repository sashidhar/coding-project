package co.harbor.calendly.repository;

import co.harbor.calendly.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository for {@link User} entity. Provides custom CRUD operations for user.
 */
public interface IUserRepository extends JpaRepository<User, Integer> {

    User findByEmailId(String email);

}
