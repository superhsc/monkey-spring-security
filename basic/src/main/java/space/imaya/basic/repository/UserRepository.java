package space.imaya.basic.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import space.imaya.basic.domain.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {

    Optional<User> findUserByUsername(String username);
}
