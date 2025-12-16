package pt.iade.ei.studycash.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "transacao")
public class Transacao {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_transacao")
    private Long idTransacao;

    private String descricao;
    private double valor;
    private String tipo; // entrada | saida
    private LocalDate dataTransacao;

    @ManyToOne
    @JoinColumn(name = "id_carteira")
    private Carteira carteira;

    @ManyToOne
    @JoinColumn(name = "id_categoria", nullable = true)
    private Categoria categoria;

    private Double latitude;
    private Double longitude;
    private String localizacao;

    public Transacao(){}

    public Transacao(Long idTransacao, String descricao, double valor, String tipo, LocalDate dataTransacao, Carteira carteira, Categoria categoria, Double latitude, Double longitude){
        this.idTransacao = idTransacao; this.descricao = descricao; this.valor = valor; this.tipo = tipo; this.dataTransacao = dataTransacao; this.carteira = carteira; this.categoria = categoria; this.latitude = latitude; this.longitude = longitude;
    }

    public Long getIdTransacao() { return idTransacao; }
    public void setIdTransacao(Long idTransacao) { this.idTransacao = idTransacao; }

    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }

    public double getValor() { return valor; }
    public void setValor(double valor) { this.valor = valor; }

    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }

    public LocalDate getDataTransacao() { return dataTransacao; }
    public void setDataTransacao(LocalDate dataTransacao) { this.dataTransacao = dataTransacao; }

    public Carteira getCarteira() { return carteira; }
    public void setCarteira(Carteira carteira) { this.carteira = carteira; }

    public Categoria getCategoria() { return categoria; }
    public void setCategoria(Categoria categoria) { this.categoria = categoria; }

    public Double getLatitude() { return latitude; }
    public void setLatitude(Double latitude) { this.latitude = latitude; }

    public Double getLongitude() { return longitude; }
    public void setLongitude(Double longitude) { this.longitude = longitude; }

    public String getLocalizacao() { return localizacao; }
    public void setLocalizacao(String localizacao) { this.localizacao = localizacao; }

    @Override public String toString(){ return "Transacao{idTransacao="+idTransacao+",descricao='"+descricao+"'}"; }
}