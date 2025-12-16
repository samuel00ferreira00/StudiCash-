package pt.iade.ei.studycash.model;

import jakarta.persistence.*;

@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_user")
    private Long idUser;

    private String nome;
    private String email;
    private String password;

    @Column(columnDefinition = "boolean default true")
    private boolean notificacoes = true;

    public User() {}

    public User(Long idUser, String nome, String email, String password, boolean notificacoes) {
        this.idUser = idUser;
        this.nome = nome;
        this.email = email;
        this.password = password;
        this.notificacoes = notificacoes;
    }

    public Long getIdUser() { return idUser; }
    public void setIdUser(Long idUser) { this.idUser = idUser; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public boolean isNotificacoes() { return notificacoes; }
    public void setNotificacoes(boolean notificacoes) { this.notificacoes = notificacoes; }

    @Override
    public String toString() {
        return "User{idUser=" + idUser + ", nome='" + nome + "', email='" + email + "', notificacoes=" + notificacoes + "}";
    }
}