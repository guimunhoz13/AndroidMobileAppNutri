package com.nutricionista.app.modelos;

import java.io.Serializable;

public class AlimentoConsulta implements Serializable {

    private String id;
    private String idConsulta;
    private String idAlimento;
    private String quantidade;

    public AlimentoConsulta() {
    }

    public AlimentoConsulta(String idConsulta, String idAlimento, String quantidade) {
        this.idConsulta = idConsulta;
        this.idAlimento = idAlimento;
        this.quantidade = quantidade;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIdConsulta() {
        return idConsulta;
    }

    public void setIdConsulta(String idConsulta) {
        this.idConsulta = idConsulta;
    }

    public String getIdAlimento() {
        return idAlimento;
    }

    public void setIdAlimento(String idAlimento) {
        this.idAlimento = idAlimento;
    }

    public String getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(String quantidade) {
        this.quantidade = quantidade;
    }
}