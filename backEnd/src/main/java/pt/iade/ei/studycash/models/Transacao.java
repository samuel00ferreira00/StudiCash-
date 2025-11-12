package pt.iade.ei.studycash.models;

import jakarta.persistence.*;

@Entity
@Table(name = "transacoes")
public class Transacao {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String descricao;
    private double valor;

    @Column(name = "data_transacao")
    private java.sql.Date dataTransacao;

    @ManyToOne
    @JoinColumn(name = "id_conta")
    private Conta conta;

    @ManyToOne
    @JoinColumn(name = "id_tipo_transacao")
    private TipoTransacao tipoTransacao;

    public Transacao() {}

    public int getId() { return id; }
    public String getDescricao() { return descricao; }
    public double getValor() { return valor; }
    public java.sql.Date getDataTransacao() { return dataTransacao; }
    public Conta getConta() { return conta; }
    public TipoTransacao getTipoTransacao() { return tipoTransacao; }

    public void setDescricao(String descricao) { this.descricao = descricao; }
    public void setValor(double valor) { this.valor = valor; }
    public void setDataTransacao(java.sql.Date dataTransacao) { this.dataTransacao = dataTransacao; }
    public void setConta(Conta conta) { this.conta = conta; }
    public void setTipoTransacao(TipoTransacao tipoTransacao) { this.tipoTransacao = tipoTransacao; }
}
