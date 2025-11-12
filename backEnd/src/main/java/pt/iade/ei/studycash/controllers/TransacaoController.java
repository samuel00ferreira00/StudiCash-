package pt.iade.ei.studycash.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import pt.iade.ei.studycash.models.Transacao;
import pt.iade.ei.studycash.models.repositories.TransacaoRepository;

import java.util.Optional;

@RestController
@RequestMapping(path = "/api/transacoes")
public class TransacaoController {

    @Autowired
    private TransacaoRepository transacaoRepository;

    @GetMapping(path = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public Iterable<Transacao> getTransacoes() {
        return transacaoRepository.findAll();
    }

    @GetMapping(path = "/{id:[0-9]+}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Transacao getTransacao(@PathVariable int id) {
        Optional<Transacao> transacao = transacaoRepository.findById(id);
        return transacao.orElseThrow(() -> new RuntimeException("Transação não encontrada"));
    }

    @PostMapping(path = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public Transacao saveTransacao(@RequestBody Transacao transacao) {
        return transacaoRepository.save(transacao);
    }

    @DeleteMapping(path = "/{id:[0-9]+}", produces = MediaType.APPLICATION_JSON_VALUE)
    public String deleteTransacao(@PathVariable int id) {
        transacaoRepository.deleteById(id);
        return "Transação eliminada com id " + id;
    }
}
