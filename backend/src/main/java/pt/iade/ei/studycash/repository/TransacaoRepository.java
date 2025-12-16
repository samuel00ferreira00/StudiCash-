package pt.iade.ei.studycash.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pt.iade.ei.studycash.model.Transacao;
import java.util.List;

public interface TransacaoRepository extends JpaRepository<Transacao, Long> {
    List<Transacao> findByCarteiraIdCarteira(Long carteiraId);
    List<Transacao> findByCarteiraUserIdUser(Long userId);
}