package antifraud.repositories;

import antifraud.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    User findUserByUsernameIgnoreCase(String username);
    boolean existsByUsernameIgnoreCase(String username);
}
