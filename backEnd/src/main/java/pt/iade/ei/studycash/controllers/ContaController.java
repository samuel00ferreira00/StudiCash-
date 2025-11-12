package pt.iade.ei.studycash.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import pt.iade.ei.studycash.models.Conta;
import pt.iade.ei.studycash.models.repositories.ContaRepository;

import java.util.Optional;

@RestController
@RequestMapping(path = "/api/contas")
public class ContaController {
    private Logger logger = LoggerFactory.getLogger(ContaController.class);

    @Autowired
    private ContaRepository contaRepository;

    @GetMapping(path = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public Iterable<Conta> getContas() {
        logger.info("A enviar todas as contas");
        return contaRepository.findAll();
    }

    @GetMapping(path = "/{id:[0-9]+}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Conta getConta(@PathVariable int id) {
        logger.info("A enviar conta com id " + id);
        Optional<Conta> conta = contaRepository.findById(id);
        return conta.orElseThrow(() -> new RuntimeException("Conta não encontrada"));
    }

    @PostMapping(path = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public Conta saveConta(@RequestBody Conta conta) {
        Conta saved = contaRepository.save(conta);
        logger.info("Conta guardada com id " + saved.getId());
        return saved;
    }

    @DeleteMapping(path = "/{id:[0-9]+}", produces = MediaType.APPLICATION_JSON_VALUE)
    public String deleteConta(@PathVariable int id) {
        logger.info("A eliminar conta " + id);
        contaRepository.deleteById(id);
        return "Conta eliminada com id " + id;
    }
}
