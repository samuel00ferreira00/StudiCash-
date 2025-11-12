package pt.iade.ei.studycash.models;

import jakarta.persistence.*;

@Entity
@Table(name = "contas")
public class Conta {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "nome_conta")
    private String nomeConta;

    private double saldo;

    @Column(name = "data_criacao")
    private java.sql.Date dataCriacao;

    @ManyToOne
    @JoinColumn(name = "id_utilizador")
    private Utilizador utilizador;

    @ManyToOne
    @JoinColumn(name = "id_tipo_conta")
    private TipoConta tipoConta;

    public Conta() {}

    public int getId() { return id; }
    public String getNomeConta() { return nomeConta; }
    public double getSaldo() { return saldo; }
    public java.sql.Date getDataCriacao() { return dataCriacao; }
    public Utilizador getUtilizador() { return utilizador; }
    public TipoConta getTipoConta() { return tipoConta; }

    public void setNomeConta(String nomeConta) { this.nomeConta = nomeConta; }
    public void setSaldo(double saldo) { this.saldo = saldo; }
    public void setDataCriacao(java.sql.Date dataCriacao) { this.dataCriacao = dataCriacao; }
    public void setUtilizador(Utilizador utilizador) { this.utilizador = utilizador; }
    public void setTipoConta(TipoConta tipoConta) { this.tipoConta = tipoConta; }
}
