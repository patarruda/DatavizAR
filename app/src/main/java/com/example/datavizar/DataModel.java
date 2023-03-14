package com.example.datavizar;

public class DataModel {
    private String nome;
    private float valor;
    private float escala;

    public DataModel(String nome, float valor, float escala) {
        this.nome = nome;
        this.valor = valor;
        this.escala = escala;
    }

    public String getNome() {
        return nome;
    }

    public float getValor() {
        return valor;
    }

    public float getEscala() {
        return escala;
    }
}
