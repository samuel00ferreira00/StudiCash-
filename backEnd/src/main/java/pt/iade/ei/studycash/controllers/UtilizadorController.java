package pt.iade.ei.studycash.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import pt.iade.ei.studycash.models.Utilizador;
import pt.iade.ei.studycash.models.repositories.UtilizadorRepository;

import java.util.Optional;

@RestController
@RequestMapping(path = "/api/utilizadores")
public class UtilizadorController {
    private Logger logger = LoggerFactory.getLogger(UtilizadorController.class);

    @Autowired
    private UtilizadorRepository utilizadorRepository;

    @GetMapping(path = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public Iterable<Utilizador> getUtilizadores() {
        logger.info("A enviar todos os utilizadores");
        return utilizadorRepository.findAll();
    }

    @GetMapping(path = "/{id:[0-9]+}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Utilizador getUtilizador(@PathVariable int id) {
        logger.info("A enviar utilizador com id " + id);
        Optional<Utilizador> _user = utilizadorRepository.findById(id);
        return _user.orElseThrow(() -> new RuntimeException("Utilizador não encontrado"));
    }

    @PostMapping(path = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public Utilizador saveUtilizador(@RequestBody Utilizador utilizador) {
        Utilizador saved = utilizadorRepository.save(utilizador);
        logger.info("Utilizador guardado com id " + saved.getId());
        return saved;
    }

    @DeleteMapping(path = "/{id:[0-9]+}", produces = MediaType.APPLICATION_JSON_VALUE)
    public String deleteUtilizador(@PathVariable int id) {
        logger.info("A eliminar utilizador " + id);
        utilizadorRepository.deleteById(id);
        return "Utilizador eliminado com id " + id;
    }
}
