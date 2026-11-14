package br.org.edu.ifrn.LojaCarro.model;

import jakarta.persistence.*;

@Entity
public class Carro {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String marca;

    @Column(nullable = false, length = 50)
    private String modelo;

    @Column(nullable = false)
    private Integer anoFabricacao;

    @Column(nullable = false)
    private Integer anoModelo;

    @Column(nullable = false, length = 30)
    private String cor;

    @Column(nullable = false)
    private Integer quilometragem;

    @Column(nullable = false, length = 30)
    private String tipoCombustivel;

    @Column(nullable = false, length = 30)
    private String tipoTransmissao;

    @Column(nullable = false)
    private Double preco;

    @Column(length = 500)
    private String descricao;

    @Column(nullable = false, length = 20)
    private String situacao;

    @Column(nullable = false)
    private String fotografiaPrincipal;

    public Carro() {
    }

    public Carro(String marca, String modelo, Integer anoFabricacao, Integer anoModelo, String cor, Integer quilometragem, String tipoCombustivel, String tipoTransmissao, Double preco, String descricao, String situacao, String fotografiaPrincipal) {
        this.marca = marca;
        this.modelo = modelo;
        this.anoFabricacao = anoFabricacao;
        this.anoModelo = anoModelo;
        this.cor = cor;
        this.quilometragem = quilometragem;
        this.tipoCombustivel = tipoCombustivel;
        this.tipoTransmissao = tipoTransmissao;
        this.preco = preco;
        this.descricao = descricao;
        this.situacao = situacao;
        this.fotografiaPrincipal = fotografiaPrincipal;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getMarca() { return marca; }
    public void setMarca(String marca) { this.marca = marca; }

    public String getModelo() { return modelo; }
    public void setModelo(String modelo) { this.modelo = modelo; }

    public Integer getAnoFabricacao() { return anoFabricacao; }
    public void setAnoFabricacao(Integer anoFabricacao) { this.anoFabricacao = anoFabricacao; }

    public Integer getAnoModelo() { return anoModelo; }
    public void setAnoModelo(Integer anoModelo) { this.anoModelo = anoModelo; }

    public String getCor() { return cor; }
    public void setCor(String cor) { this.cor = cor; }

    public Integer getQuilometragem() { return quilometragem; }
    public void setQuilometragem(Integer quilometragem) { this.quilometragem = quilometragem; }

    public String getTipoCombustivel() { return tipoCombustivel; }
    public void setTipoCombustivel(String tipoCombustivel) { this.tipoCombustivel = tipoCombustivel; }

    public String getTipoTransmissao() { return tipoTransmissao; }
    public void setTipoTransmissao(String tipoTransmissao) { this.tipoTransmissao = tipoTransmissao; }

    public Double getPreco() { return preco; }
    public void setPreco(Double preco) { this.preco = preco; }

    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }

    public String getSituacao() { return situacao; }
    public void setSituacao(String situacao) { this.situacao = situacao; }

    public String getFotografiaPrincipal() { return fotografiaPrincipal; }
    public void setFotografiaPrincipal(String fotografiaPrincipal) { this.fotografiaPrincipal = fotografiaPrincipal; }
}