package pt.iade.ei.studycash.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pt.iade.ei.studycash.model.Meta;
import java.util.List;

public interface MetaRepository extends JpaRepository<Meta, Long> {
    List<Meta> findByUserIdUser(Long userId);
}