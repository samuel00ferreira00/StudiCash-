package pt.iade.ei.studycash.models;

import jakarta.persistence.*;

@Entity
@Table(name = "metas")
public class Meta {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String titulo;
    private String descricao;
    private String categoria;

    @Column(name = "valor_objetivo")
    private double valorObjetivo;

    @Column(name = "valor_atual")
    private double valorAtual;

    private double progresso;

    @Column(name = "data_criacao")
    private java.sql.Date dataCriacao;

    @Column(name = "data_limite")
    private java.sql.Date dataLimite;

    @ManyToOne
    @JoinColumn(name = "id_utilizador")
    private Utilizador utilizador;

    public Meta() {}

    public int getId() { return id; }
    public String getTitulo() { return titulo; }
    public String getDescricao() { return descricao; }
    public String getCategoria() { return categoria; }
    public double getValorObjetivo() { return valorObjetivo; }
    public double getValorAtual() { return valorAtual; }
    public double getProgresso() { return progresso; }
    public java.sql.Date getDataCriacao() { return dataCriacao; }
    public java.sql.Date getDataLimite() { return dataLimite; }
    public Utilizador getUtilizador() { return utilizador; }

    public void setTitulo(String titulo) { this.titulo = titulo; }
    public void setDescricao(String descricao) { this.descricao = descricao; }
    public void setCategoria(String categoria) { this.categoria = categoria; }
    public void setValorObjetivo(double valorObjetivo) { this.valorObjetivo = valorObjetivo; }
    public void setValorAtual(double valorAtual) { this.valorAtual = valorAtual; }
    public void setProgresso(double progresso) { this.progresso = progresso; }
    public void setDataCriacao(java.sql.Date dataCriacao) { this.dataCriacao = dataCriacao; }
    public void setDataLimite(java.sql.Date dataLimite) { this.dataLimite = dataLimite; }
    public void setUtilizador(Utilizador utilizador) { this.utilizador = utilizador; }
}
