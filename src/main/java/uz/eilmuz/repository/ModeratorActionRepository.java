package uz.eilmuz.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uz.eilmuz.model.ModeratorAction;
import uz.eilmuz.model.User;
import java.util.List;

public interface ModeratorActionRepository extends JpaRepository<ModeratorAction, Long> {
    List<ModeratorAction> findTop10ByOrderByActionAtDesc();
    long countByModerator(User moderator);
}
