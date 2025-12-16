package pt.iade.ei.studycash.model;

import jakarta.persistence.*;

@Entity
@Table(name = "orcamento")
public class Orcamento {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_orcamento")
    private Long idOrcamento;

    private String mes;
    private double limite;
    private double gastoAtual;

    @ManyToOne
    @JoinColumn(name = "id_carteira")
    private Carteira carteira;

    public Orcamento(){}
    public Orcamento(Long idOrcamento, String mes, double limite, double gastoAtual, Carteira carteira){
        this.idOrcamento = idOrcamento; this.mes = mes; this.limite = limite; this.gastoAtual = gastoAtual; this.carteira = carteira;
    }

    public Long getIdOrcamento() { return idOrcamento; }
    public void setIdOrcamento(Long idOrcamento) { this.idOrcamento = idOrcamento; }

    public String getMes() { return mes; }
    public void setMes(String mes) { this.mes = mes; }

    public double getLimite() { return limite; }
    public void setLimite(double limite) { this.limite = limite; }

    public double getGastoAtual() { return gastoAtual; }
    public void setGastoAtual(double gastoAtual) { this.gastoAtual = gastoAtual; }

    public Carteira getCarteira() { return carteira; }
    public void setCarteira(Carteira carteira) { this.carteira = carteira; }

    @Override public String toString(){ return "Orcamento{idOrcamento="+idOrcamento+",mes='"+mes+"'}"; }
}