package uz.eilmuz.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uz.eilmuz.model.Role;
import uz.eilmuz.model.User;
import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
    List<User> findByRole(Role role);
    long countByRole(Role role);
}
