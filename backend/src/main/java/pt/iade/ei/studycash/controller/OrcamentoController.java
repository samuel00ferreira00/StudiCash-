package pt.iade.ei.studycash.controller;

import org.springframework.web.bind.annotation.*; import org.springframework.beans.factory.annotation.Autowired; import java.util.*;
import pt.iade.ei.studycash.model.Orcamento; import pt.iade.ei.studycash.repository.OrcamentoRepository;

@RestController @RequestMapping("/api/orcamentos") @CrossOrigin(origins="*")
public class OrcamentoController {
 @Autowired private OrcamentoRepository repo;
 @GetMapping public List<Orcamento> all(){ return repo.findAll(); }
 @PostMapping public Orcamento create(@RequestBody Orcamento o){ return repo.save(o); }
 @PutMapping("/{id}") public Orcamento update(@PathVariable Long id, @RequestBody Orcamento o){ o.setIdOrcamento(id); return repo.save(o); }
 @DeleteMapping("/{id}") public void delete(@PathVariable Long id){ repo.deleteById(id); }
}