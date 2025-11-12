package pt.iade.ei.studycash.models;

import jakarta.persistence.*;

@Entity
@Table(name = "orcamento_transacao")
public class OrcamentoTransacao {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "id_orcamento")
    private Orcamento orcamento;

    @ManyToOne
    @JoinColumn(name = "id_transacao")
    private Transacao transacao;

    @Column(name = "data_associacao")
    private java.sql.Date dataAssociacao;

    public OrcamentoTransacao() {}

    public int getId() { return id; }
    public Orcamento getOrcamento() { return orcamento; }
    public Transacao getTransacao() { return transacao; }
    public java.sql.Date getDataAssociacao() { return dataAssociacao; }

    public void setOrcamento(Orcamento orcamento) { this.orcamento = orcamento; }
    public void setTransacao(Transacao transacao) { this.transacao = transacao; }
    public void setDataAssociacao(java.sql.Date dataAssociacao) { this.dataAssociacao = dataAssociacao; }
}
