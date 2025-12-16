package pt.iade.ei.studycash.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import pt.iade.ei.studycash.model.User;
import pt.iade.ei.studycash.model.Carteira;
import pt.iade.ei.studycash.repository.UserRepository;
import pt.iade.ei.studycash.repository.CarteiraRepository;
import java.util.List;
import java.util.Optional;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*")
public class UserController {
    @Autowired private UserRepository repo;
    @Autowired private CarteiraRepository carteiraRepo;

    @GetMapping
    public List<User> all() {
        return repo.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id) {
        Optional<User> user = repo.findById(id);
        if (user.isPresent()) {
            return ResponseEntity.ok(user.get());
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody User u) {
        if (u.getEmail() == null || u.getPassword() == null) {
            return ResponseEntity.badRequest().body("email e password são obrigatórios");
        }
        if (repo.existsByEmail(u.getEmail())) {
            return ResponseEntity.status(409).body("email já existe");
        }
        User savedUser = repo.save(u);
        
        // Criar carteira automaticamente para o novo utilizador
        Carteira carteira = new Carteira();
        carteira.setSaldo(0.0);
        carteira.setUser(savedUser);
        carteiraRepo.save(carteira);
        
        return ResponseEntity.ok(savedUser);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> credentials) {
        String email = credentials.get("email");
        String password = credentials.get("password");
        
        if (email == null || password == null) {
            return ResponseEntity.badRequest().body("email e password são obrigatórios");
        }
        
        Optional<User> user = repo.findByEmailAndPassword(email, password);
        if (user.isPresent()) {
            return ResponseEntity.ok(user.get());
        }
        return ResponseEntity.status(401).body("Credenciais inválidas");
    }

    @PutMapping("/{id}")
    public User update(@PathVariable Long id, @RequestBody User u) {
        u.setIdUser(id);
        return repo.save(u);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        repo.deleteById(id);
    }
}