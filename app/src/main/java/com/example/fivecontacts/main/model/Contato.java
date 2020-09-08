package com.example.fivecontacts.main.model;

import java.io.Serializable;

public class Contato implements Serializable {
    private String nome;
    private String numero;

    public String getNome(){
        return nome;
    }
    public void setNome(String nome){
        this.nome = nome;
    }

    public String getNumero(){
        return numero;
    }
    public void setNumero(String numero){
        this.numero = numero;
    }
}
