package pt.iade.ei.studycash.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import pt.iade.ei.studycash.models.Meta;
import pt.iade.ei.studycash.models.repositories.MetaRepository;

import java.util.Optional;

@RestController
@RequestMapping(path = "/api/metas")
public class MetaController {

    @Autowired
    private MetaRepository metaRepository;

    @GetMapping(path = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public Iterable<Meta> getMetas() {
        return metaRepository.findAll();
    }

    @GetMapping(path = "/{id:[0-9]+}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Meta getMeta(@PathVariable int id) {
        Optional<Meta> meta = metaRepository.findById(id);
        return meta.orElseThrow(() -> new RuntimeException("Meta não encontrada"));
    }

    @PostMapping(path = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public Meta saveMeta(@RequestBody Meta meta) {
        return metaRepository.save(meta);
    }

    @DeleteMapping(path = "/{id:[0-9]+}", produces = MediaType.APPLICATION_JSON_VALUE)
    public String deleteMeta(@PathVariable int id) {
        metaRepository.deleteById(id);
        return "Meta eliminada com id " + id;
    }
}
