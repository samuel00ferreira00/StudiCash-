package pt.iade.ei.studycash.controller;

import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.*;
import pt.iade.ei.studycash.model.Meta;
import pt.iade.ei.studycash.repository.MetaRepository;

@RestController
@RequestMapping("/api/metas")
@CrossOrigin(origins = "*")
public class MetaController {
    @Autowired
    private MetaRepository repo;

    @GetMapping
    public List<Meta> all() {
        return repo.findAll();
    }

    @GetMapping("/user/{userId}")
    public List<Meta> byUser(@PathVariable Long userId) {
        return repo.findByUserIdUser(userId);
    }

    @PostMapping
    public Meta create(@RequestBody Meta m) {
        return repo.save(m);
    }

    @PutMapping("/{id}")
    public Meta update(@PathVariable Long id, @RequestBody Meta m) {
        m.setIdMeta(id);
        return repo.save(m);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        repo.deleteById(id);
    }
}