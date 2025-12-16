package pt.iade.ei.studycash.controller;

import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import java.util.*;
import pt.iade.ei.studycash.model.Carteira;
import pt.iade.ei.studycash.repository.CarteiraRepository;

@RestController
@RequestMapping("/api/carteiras")
@CrossOrigin(origins = "*")
public class CarteiraController {
    @Autowired
    private CarteiraRepository repo;

    @GetMapping
    public List<Carteira> all() {
        return repo.findAll();
    }

    @GetMapping("/user/{userId}")
    public List<Carteira> byUser(@PathVariable Long userId) {
        return repo.findByUserIdUser(userId);
    }

    @GetMapping("/user/{userId}/first")
    public ResponseEntity<Carteira> firstByUser(@PathVariable Long userId) {
        Optional<Carteira> carteira = repo.findFirstByUserIdUser(userId);
        return carteira.map(ResponseEntity::ok)
                       .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Carteira create(@RequestBody Carteira c) {
        return repo.save(c);
    }

    @PutMapping("/{id}")
    public Carteira update(@PathVariable Long id, @RequestBody Carteira c) {
        c.setIdCarteira(id);
        return repo.save(c);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        repo.deleteById(id);
    }
}