package pt.iade.ei.studycash.model;

import jakarta.persistence.*;

@Entity
@Table(name = "categoria")
public class Categoria {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_categoria")
    private Long idCategoria;

    private String nome;
    private String tipo; // entrada | saida

    public Categoria() {}
    public Categoria(Long idCategoria, String nome, String tipo) {
        this.idCategoria = idCategoria; this.nome = nome; this.tipo = tipo;
    }

    public Long getIdCategoria() { return idCategoria; }
    public void setIdCategoria(Long idCategoria) { this.idCategoria = idCategoria; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }

    @Override public String toString(){ return "Categoria{idCategoria="+idCategoria+",nome='"+nome+"',tipo='"+tipo+"'}"; }
}