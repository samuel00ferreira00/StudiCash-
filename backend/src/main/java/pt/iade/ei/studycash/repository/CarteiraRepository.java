package pt.iade.ei.studycash.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pt.iade.ei.studycash.model.Carteira;
import java.util.List;
import java.util.Optional;

public interface CarteiraRepository extends JpaRepository<Carteira, Long> {
    List<Carteira> findByUserIdUser(Long userId);
    Optional<Carteira> findFirstByUserIdUser(Long userId);
}