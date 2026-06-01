package com.nutricionista.app.modelos;

import java.io.Serializable;

public class PlanoAlimentar implements Serializable {

    private String id;
    private String idPaciente;
    private String cafeManha;
    private String lancheManha;
    private String almoco;
    private String lancheTarde;
    private String jantar;
    private String ceia;
    private String observacoes;

    public PlanoAlimentar() {
    }

    public PlanoAlimentar(String idPaciente, String cafeManha, String lancheManha, String almoco,
                          String lancheTarde, String jantar, String ceia, String observacoes) {
        this.idPaciente = idPaciente;
        this.cafeManha = cafeManha;
        this.lancheManha = lancheManha;
        this.almoco = almoco;
        this.lancheTarde = lancheTarde;
        this.jantar = jantar;
        this.ceia = ceia;
        this.observacoes = observacoes;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIdPaciente() {
        return idPaciente;
    }

    public void setIdPaciente(String idPaciente) {
        this.idPaciente = idPaciente;
    }

    public String getCafeManha() {
        return cafeManha;
    }

    public void setCafeManha(String cafeManha) {
        this.cafeManha = cafeManha;
    }

    public String getLancheManha() {
        return lancheManha;
    }

    public void setLancheManha(String lancheManha) {
        this.lancheManha = lancheManha;
    }

    public String getAlmoco() {
        return almoco;
    }

    public void setAlmoco(String almoco) {
        this.almoco = almoco;
    }

    public String getLancheTarde() {
        return lancheTarde;
    }

    public void setLancheTarde(String lancheTarde) {
        this.lancheTarde = lancheTarde;
    }

    public String getJantar() {
        return jantar;
    }

    public void setJantar(String jantar) {
        this.jantar = jantar;
    }

    public String getCeia() {
        return ceia;
    }

    public void setCeia(String ceia) {
        this.ceia = ceia;
    }

    public String getObservacoes() {
        return observacoes;
    }

    public void setObservacoes(String observacoes) {
        this.observacoes = observacoes;
    }
}