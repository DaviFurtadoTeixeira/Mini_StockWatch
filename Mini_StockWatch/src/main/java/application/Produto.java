package application;

import java.sql.Date;

public class Produto {
	// Campos do banco de dados
    private int id;
    private String nome;
    private String fornecedor;
    private String tipo;
    private int quantidade;
    private Date validade;

    public Produto() {
    }

    public Produto(int id, String nome, String fornecedor, String tipo, int quantidade, Date validade) {
        this.id = id;
        this.nome = nome;
        this.fornecedor = fornecedor;
        this.tipo = tipo;
        this.quantidade = quantidade;
        this.validade = validade;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getFornecedor() { return fornecedor; }
    public void setFornecedor(String fornecedor) { this.fornecedor = fornecedor; }

    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }

    public int getQuantidade() { return quantidade; }
    public void setQuantidade(int quantidade) { this.quantidade = quantidade; }

    public Date getValidade() { return validade; }
    public void setValidade(Date validade) { this.validade = validade; }
}