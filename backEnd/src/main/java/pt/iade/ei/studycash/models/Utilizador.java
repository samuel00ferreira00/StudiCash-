package pt.iade.ei.studycash.models;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "utilizadores")
public class Utilizador {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "nome_do_utilizador")
    private String nomeDoUtilizador;

    private String email;
    @JsonIgnore
    private String senha;

    public Utilizador() {}

    public int getId() { return id; }
    public String getNomeDoUtilizador() { return nomeDoUtilizador; }
    public String getEmail() { return email; }
    public String getSenha() { return senha; }

    public void setNomeDoUtilizador(String nomeDoUtilizador) { this.nomeDoUtilizador = nomeDoUtilizador; }
    public void setEmail(String email) { this.email = email; }
    public void setSenha(String senha) { this.senha = senha; }
}
