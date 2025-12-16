package pt.iade.ei.studycash.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pt.iade.ei.studycash.model.User;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByEmail(String email);
    Optional<User> findByEmailAndPassword(String email, String password);
    Optional<User> findByEmail(String email);
}