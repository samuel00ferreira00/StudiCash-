package pt.iade.ei.studycash.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import pt.iade.ei.studycash.models.Transferencia;
import pt.iade.ei.studycash.models.repositories.TransferenciaRepository;

import java.util.Optional;

@RestController
@RequestMapping(path = "/api/transferencias")
public class TransferenciaController {

    @Autowired
    private TransferenciaRepository transferenciaRepository;

    @GetMapping(path = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public Iterable<Transferencia> getTransferencias() {
        return transferenciaRepository.findAll();
    }

    @GetMapping(path = "/{id:[0-9]+}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Transferencia getTransferencia(@PathVariable int id) {
        Optional<Transferencia> trans = transferenciaRepository.findById(id);
        return trans.orElseThrow(() -> new RuntimeException("Transferência não encontrada"));
    }

    @PostMapping(path = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public Transferencia saveTransferencia(@RequestBody Transferencia transferencia) {
        return transferenciaRepository.save(transferencia);
    }

    @DeleteMapping(path = "/{id:[0-9]+}", produces = MediaType.APPLICATION_JSON_VALUE)
    public String deleteTransferencia(@PathVariable int id) {
        transferenciaRepository.deleteById(id);
        return "Transferência eliminada com id " + id;
    }
}
