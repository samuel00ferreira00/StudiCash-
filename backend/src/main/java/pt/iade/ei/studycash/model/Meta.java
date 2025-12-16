package pt.iade.ei.studycash.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "meta")
public class Meta {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_meta")
    private Long idMeta;

    private String nome;
    private double valorAtual;
    private double valorObjetivo;
    private LocalDate dataInicio;
    private LocalDate dataFim;

    @ManyToOne
    @JoinColumn(name = "id_user")
    private User user;

    public Meta() {}

    public Meta(Long idMeta, String nome, double valorAtual, double valorObjetivo, LocalDate dataInicio, LocalDate dataFim, User user) {
        this.idMeta = idMeta; this.nome = nome; this.valorAtual = valorAtual; this.valorObjetivo = valorObjetivo; this.dataInicio = dataInicio; this.dataFim = dataFim; this.user = user;
    }

    public Long getIdMeta() { return idMeta; }
    public void setIdMeta(Long idMeta) { this.idMeta = idMeta; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public double getValorAtual() { return valorAtual; }
    public void setValorAtual(double valorAtual) { this.valorAtual = valorAtual; }

    public double getValorObjetivo() { return valorObjetivo; }
    public void setValorObjetivo(double valorObjetivo) { this.valorObjetivo = valorObjetivo; }

    public LocalDate getDataInicio() { return dataInicio; }
    public void setDataInicio(LocalDate dataInicio) { this.dataInicio = dataInicio; }

    public LocalDate getDataFim() { return dataFim; }
    public void setDataFim(LocalDate dataFim) { this.dataFim = dataFim; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    @Override public String toString(){ return "Meta{idMeta="+idMeta+",nome='"+nome+"'}"; }
}