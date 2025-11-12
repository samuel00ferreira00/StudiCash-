package pt.iade.ei.studycash.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import pt.iade.ei.studycash.models.TipoTransacao;
import pt.iade.ei.studycash.models.repositories.TipoTransacaoRepository;

import java.util.Optional;

@RestController
@RequestMapping(path = "/api/tipostransacao")
public class TipoTransacaoController {

    private Logger logger = LoggerFactory.getLogger(TipoTransacaoController.class);

    @Autowired
    private TipoTransacaoRepository tipoTransacaoRepository;

    @GetMapping(path = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public Iterable<TipoTransacao> getTiposTransacao() {
        logger.info("A enviar todos os tipos de transação");
        return tipoTransacaoRepository.findAll();
    }

    @GetMapping(path = "/{id:[0-9]+}", produces = MediaType.APPLICATION_JSON_VALUE)
    public TipoTransacao getTipoTransacao(@PathVariable int id) {
        logger.info("A enviar tipo de transação com id " + id);
        Optional<TipoTransacao> tipo = tipoTransacaoRepository.findById(id);
        return tipo.orElseThrow(() -> new RuntimeException("Tipo de transação não encontrado"));
    }

    @PostMapping(path = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public TipoTransacao saveTipoTransacao(@RequestBody TipoTransacao tipoTransacao) {
        TipoTransacao saved = tipoTransacaoRepository.save(tipoTransacao);
        logger.info("Tipo de transação guardado com id " + saved.getId());
        return saved;
    }

    @DeleteMapping(path = "/{id:[0-9]+}", produces = MediaType.APPLICATION_JSON_VALUE)
    public String deleteTipoTransacao(@PathVariable int id) {
        logger.info("A eliminar tipo de transação " + id);
        tipoTransacaoRepository.deleteById(id);
        return "Tipo de transação eliminado com id " + id;
    }
}
