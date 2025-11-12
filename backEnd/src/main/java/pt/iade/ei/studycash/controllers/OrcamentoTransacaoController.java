package pt.iade.ei.studycash.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import pt.iade.ei.studycash.models.OrcamentoTransacao;
import pt.iade.ei.studycash.models.repositories.OrcamentoTransacaoRepository;

import java.util.Optional;

@RestController
@RequestMapping(path = "/api/orcamentotransacoes")
public class OrcamentoTransacaoController {

    @Autowired
    private OrcamentoTransacaoRepository orcamentoTransacaoRepository;

    @GetMapping(path = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public Iterable<OrcamentoTransacao> getAll() {
        return orcamentoTransacaoRepository.findAll();
    }

    @GetMapping(path = "/{id:[0-9]+}", produces = MediaType.APPLICATION_JSON_VALUE)
    public OrcamentoTransacao getById(@PathVariable int id) {
        Optional<OrcamentoTransacao> item = orcamentoTransacaoRepository.findById(id);
        return item.orElseThrow(() -> new RuntimeException("Associação não encontrada"));
    }

    @PostMapping(path = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public OrcamentoTransacao save(@RequestBody OrcamentoTransacao item) {
        return orcamentoTransacaoRepository.save(item);
    }

    @DeleteMapping(path = "/{id:[0-9]+}", produces = MediaType.APPLICATION_JSON_VALUE)
    public String delete(@PathVariable int id) {
        orcamentoTransacaoRepository.deleteById(id);
        return "Associação eliminada com id " + id;
    }
}
