package pt.iade.ei.studycash.models;

import jakarta.persistence.*;

@Entity
@Table(name = "tipo_conta")
public class TipoConta {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "nome_tipo")
    private String nomeTipo;

    @Column(name = "data_criacao")
    private java.sql.Date dataCriacao;

    public TipoConta() {}

    public int getId() { return id; }
    public String getNomeTipo() { return nomeTipo; }
    public java.sql.Date getDataCriacao() { return dataCriacao; }

    public void setNomeTipo(String nomeTipo) { this.nomeTipo = nomeTipo; }
    public void setDataCriacao(java.sql.Date dataCriacao) { this.dataCriacao = dataCriacao; }
}
