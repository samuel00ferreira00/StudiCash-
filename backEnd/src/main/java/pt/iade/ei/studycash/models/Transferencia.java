package pt.iade.ei.studycash.models;

import jakarta.persistence.*;

@Entity
@Table(name = "transferencias")
public class Transferencia {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private double valor;

    @Column(name = "data_transferencia")
    private java.sql.Date dataTransferencia;

    private String descricao;

    @ManyToOne
    @JoinColumn(name = "id_conta_origem")
    private Conta contaOrigem;

    @ManyToOne
    @JoinColumn(name = "id_conta_destino")
    private Conta contaDestino;

    public Transferencia() {}

    public int getId() { return id; }
    public double getValor() { return valor; }
    public java.sql.Date getDataTransferencia() { return dataTransferencia; }
    public String getDescricao() { return descricao; }
    public Conta getContaOrigem() { return contaOrigem; }
    public Conta getContaDestino() { return contaDestino; }

    public void setValor(double valor) { this.valor = valor; }
    public void setDataTransferencia(java.sql.Date dataTransferencia) { this.dataTransferencia = dataTransferencia; }
    public void setDescricao(String descricao) { this.descricao = descricao; }
    public void setContaOrigem(Conta contaOrigem) { this.contaOrigem = contaOrigem; }
    public void setContaDestino(Conta contaDestino) { this.contaDestino = contaDestino; }
}
