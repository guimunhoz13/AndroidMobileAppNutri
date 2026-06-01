package com.nutricionista.app.modelos;

import java.io.Serializable;

public class Consulta implements Serializable {
    private String id;
    private String idPaciente; // referência ao documento do paciente (1 para N)
    private String dataConsulta;
    private double peso;
    private double altura;
    private String observacoes;

    public Consulta() {}

    public Consulta(String idPaciente, String dataConsulta, double peso, double altura, String observacoes) {
        this.idPaciente = idPaciente;
        this.dataConsulta = dataConsulta;
        this.peso = peso;
        this.altura = altura;
        this.observacoes = observacoes;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getIdPaciente() { return idPaciente; }
    public void setIdPaciente(String idPaciente) { this.idPaciente = idPaciente; }
    public String getDataConsulta() { return dataConsulta; }
    public void setDataConsulta(String d) { this.dataConsulta = d; }
    public double getPeso() { return peso; }
    public void setPeso(double p) { this.peso = p; }
    public double getAltura() { return altura; }
    public void setAltura(double a) { this.altura = a; }
    public String getObservacoes() { return observacoes; }
    public void setObservacoes(String o) { this.observacoes = o; }

    public double calcularIMC() {
        if (altura <= 0) return 0;
        return peso / (altura * altura);
    }
}
