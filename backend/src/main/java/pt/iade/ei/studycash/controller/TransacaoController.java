package pt.iade.ei.studycash.controller;

import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.*;
import pt.iade.ei.studycash.model.Transacao;
import pt.iade.ei.studycash.model.Carteira;
import pt.iade.ei.studycash.repository.TransacaoRepository;
import pt.iade.ei.studycash.repository.CarteiraRepository;

@RestController
@RequestMapping("/api/transacoes")
@CrossOrigin(origins = "*")
public class TransacaoController {
    @Autowired
    private TransacaoRepository repo;
    
    @Autowired
    private CarteiraRepository carteiraRepo;

    @GetMapping
    public List<Transacao> all() {
        return repo.findAll();
    }

    @GetMapping("/user/{userId}")
    public List<Transacao> byUser(@PathVariable Long userId) {
        return repo.findByCarteiraUserIdUser(userId);
    }

    @GetMapping("/carteira/{carteiraId}")
    public List<Transacao> byCarteira(@PathVariable Long carteiraId) {
        return repo.findByCarteiraIdCarteira(carteiraId);
    }

    @PostMapping
    public Transacao create(@RequestBody Transacao t) {
        return repo.save(t);
    }

    @PostMapping("/user/{userId}")
    public Transacao createForUser(@PathVariable Long userId, @RequestBody Transacao t) {
        // Buscar a carteira do utilizador
        Optional<Carteira> carteira = carteiraRepo.findFirstByUserIdUser(userId);
        if (carteira.isPresent()) {
            t.setCarteira(carteira.get());
            
            // Atualizar o saldo da carteira
            Carteira c = carteira.get();
            if ("Receita".equalsIgnoreCase(t.getTipo())) {
                c.setSaldo(c.getSaldo() + t.getValor());
            } else {
                c.setSaldo(c.getSaldo() - t.getValor());
            }
            carteiraRepo.save(c);
        }
        return repo.save(t);
    }

    @PutMapping("/{id}")
    public Transacao update(@PathVariable Long id, @RequestBody Transacao t) {
        t.setIdTransacao(id);
        return repo.save(t);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        repo.deleteById(id);
    }
}