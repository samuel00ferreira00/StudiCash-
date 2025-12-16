package pt.iade.ei.studycash.controller;

import org.springframework.web.bind.annotation.*; import org.springframework.beans.factory.annotation.Autowired; import java.util.*;
import pt.iade.ei.studycash.model.Categoria; import pt.iade.ei.studycash.repository.CategoriaRepository;

@RestController @RequestMapping("/api/categorias") @CrossOrigin(origins="*")
public class CategoriaController {
 @Autowired private CategoriaRepository repo;
 @GetMapping public List<Categoria> all(){ return repo.findAll(); }
 @PostMapping public Categoria create(@RequestBody Categoria c){ return repo.save(c); }
 @PutMapping("/{id}") public Categoria update(@PathVariable Long id, @RequestBody Categoria c){ c.setIdCategoria(id); return repo.save(c); }
 @DeleteMapping("/{id}") public void delete(@PathVariable Long id){ repo.deleteById(id); }
}