package pt.iade.ei.studycash.models.repositories;

import org.springframework.data.repository.CrudRepository;
import pt.iade.ei.studycash.models.Transacao;

public interface TransacaoRepository extends CrudRepository<Transacao, Integer> { }
