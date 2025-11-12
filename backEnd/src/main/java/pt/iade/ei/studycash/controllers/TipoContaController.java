package pt.iade.ei.studycash.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import pt.iade.ei.studycash.models.TipoConta;
import pt.iade.ei.studycash.models.repositories.TipoContaRepository;

import java.util.Optional;

@RestController
@RequestMapping(path = "/api/tiposconta")
public class TipoContaController {

    @Autowired
    private TipoContaRepository tipoContaRepository;

    @GetMapping(path = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public Iterable<TipoConta> getTiposConta() {
        return tipoContaRepository.findAll();
    }

    @GetMapping(path = "/{id:[0-9]+}", produces = MediaType.APPLICATION_JSON_VALUE)
    public TipoConta getTipoConta(@PathVariable int id) {
        Optional<TipoConta> tipo = tipoContaRepository.findById(id);
        return tipo.orElseThrow(() -> new RuntimeException("Tipo de conta não encontrado"));
    }

    @PostMapping(path = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public TipoConta saveTipoConta(@RequestBody TipoConta tipoConta) {
        return tipoContaRepository.save(tipoConta);
    }

    @DeleteMapping(path = "/{id:[0-9]+}", produces = MediaType.APPLICATION_JSON_VALUE)
    public String deleteTipoConta(@PathVariable int id) {
        tipoContaRepository.deleteById(id);
        return "Tipo de conta eliminado com id " + id;
    }
}
