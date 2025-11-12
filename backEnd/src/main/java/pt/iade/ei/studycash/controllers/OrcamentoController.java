package pt.iade.ei.studycash.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import pt.iade.ei.studycash.models.Orcamento;
import pt.iade.ei.studycash.models.repositories.OrcamentoRepository;

import java.util.Optional;

@RestController
@RequestMapping(path = "/api/orcamentos")
public class OrcamentoController {

    @Autowired
    private OrcamentoRepository orcamentoRepository;

    @GetMapping(path = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public Iterable<Orcamento> getOrcamentos() {
        return orcamentoRepository.findAll();
    }

    @GetMapping(path = "/{id:[0-9]+}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Orcamento getOrcamento(@PathVariable int id) {
        Optional<Orcamento> orc = orcamentoRepository.findById(id);
        return orc.orElseThrow(() -> new RuntimeException("Orçamento não encontrado"));
    }

    @PostMapping(path = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public Orcamento saveOrcamento(@RequestBody Orcamento orcamento) {
        return orcamentoRepository.save(orcamento);
    }

    @DeleteMapping(path = "/{id:[0-9]+}", produces = MediaType.APPLICATION_JSON_VALUE)
    public String deleteOrcamento(@PathVariable int id) {
        orcamentoRepository.deleteById(id);
        return "Orçamento eliminado com id " + id;
    }
}
