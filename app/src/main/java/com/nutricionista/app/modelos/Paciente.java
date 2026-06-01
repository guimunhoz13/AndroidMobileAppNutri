package com.nutricionista.app.modelos;

import java.io.Serializable;

public class Paciente implements Serializable {
    private String id; // ID do documento Firestore
    private String nome;
    private String dataNascimento;
    private String telefone;
    private String objetivoNutricional;

    public Paciente() {} // necessário para Firestore

    public Paciente(String nome, String dataNascimento, String telefone, String objetivoNutricional) {
        this.nome = nome;
        this.dataNascimento = dataNascimento;
        this.telefone = telefone;
        this.objetivoNutricional = objetivoNutricional;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getDataNascimento() { return dataNascimento; }
    public void setDataNascimento(String d) { this.dataNascimento = d; }
    public String getTelefone() { return telefone; }
    public void setTelefone(String t) { this.telefone = t; }
    public String getObjetivoNutricional() { return objetivoNutricional; }
    public void setObjetivoNutricional(String o) { this.objetivoNutricional = o; }
}
