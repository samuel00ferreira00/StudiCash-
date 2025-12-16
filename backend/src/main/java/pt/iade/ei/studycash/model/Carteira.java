package pt.iade.ei.studycash.model;

import jakarta.persistence.*;

@Entity
@Table(name = "carteira")
public class Carteira {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_carteira")
    private Long idCarteira;

    private double saldo;

    @ManyToOne
    @JoinColumn(name = "id_user")
    private User user;

    public Carteira() {}
    public Carteira(Long idCarteira, double saldo, User user) {
        this.idCarteira = idCarteira; this.saldo = saldo; this.user = user;
    }

    public Long getIdCarteira() { return idCarteira; }
    public void setIdCarteira(Long idCarteira) { this.idCarteira = idCarteira; }

    public double getSaldo() { return saldo; }
    public void setSaldo(double saldo) { this.saldo = saldo; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    @Override public String toString(){ return "Carteira{idCarteira="+idCarteira+",saldo="+saldo+"}"; }
}