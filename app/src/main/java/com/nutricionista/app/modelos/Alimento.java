package com.nutricionista.app.modelos;

import java.io.Serializable;

public class Alimento implements Serializable {

    private String id;
    private String nome;

    public Alimento() {
    }

    public Alimento(String nome) {
        this.nome = nome;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }


    @Override
    public String toString() {
        return nome;
    }
}