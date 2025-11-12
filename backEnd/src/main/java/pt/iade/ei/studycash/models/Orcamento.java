package pt.iade.ei.studycash.models;

import jakarta.persistence.*;

@Entity
@Table(name = "orcamentos")
public class Orcamento {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "nome_orcamento")
    private String nomeOrcamento;

    @Column(name = "valor_total")
    private double valorTotal;

    private double progresso;

    @Column(name = "data_criacao")
    private java.sql.Date dataCriacao;

    @ManyToOne
    @JoinColumn(name = "id_utilizador")
    private Utilizador utilizador;

    public Orcamento() {}

    public int getId() { return id; }
    public String getNomeOrcamento() { return nomeOrcamento; }
    public double getValorTotal() { return valorTotal; }
    public double getProgresso() { return progresso; }
    public java.sql.Date getDataCriacao() { return dataCriacao; }
    public Utilizador getUtilizador() { return utilizador; }

    public void setNomeOrcamento(String nomeOrcamento) { this.nomeOrcamento = nomeOrcamento; }
    public void setValorTotal(double valorTotal) { this.valorTotal = valorTotal; }
    public void setProgresso(double progresso) { this.progresso = progresso; }
    public void setDataCriacao(java.sql.Date dataCriacao) { this.dataCriacao = dataCriacao; }
    public void setUtilizador(Utilizador utilizador) { this.utilizador = utilizador; }
}
