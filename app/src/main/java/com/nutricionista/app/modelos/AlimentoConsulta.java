package com.nutricionista.app.modelos;

import java.io.Serializable;

public class AlimentoConsulta implements Serializable {
    private String id;
    private String idConsulta; // referência ao documento da consulta (1 para N)
    private String nomeAlimento;
    private String quantidade;
    private double calorias;

    public AlimentoConsulta() {}

    public AlimentoConsulta(String idConsulta, String nomeAlimento, String quantidade, double calorias) {
        this.idConsulta = idConsulta;
        this.nomeAlimento = nomeAlimento;
        this.quantidade = quantidade;
        this.calorias = calorias;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getIdConsulta() { return idConsulta; }
    public void setIdConsulta(String i) { this.idConsulta = i; }
    public String getNomeAlimento() { return nomeAlimento; }
    public void setNomeAlimento(String n) { this.nomeAlimento = n; }
    public String getQuantidade() { return quantidade; }
    public void setQuantidade(String q) { this.quantidade = q; }
    public double getCalorias() { return calorias; }
    public void setCalorias(double c) { this.calorias = c; }
}
