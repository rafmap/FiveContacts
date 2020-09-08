package com.example.fivecontacts.main.model;

import java.io.Serializable;

public class User implements Serializable {
    String nome;
    String login;
    String senha;
    String email;

    public User(String nome, String login, String password, String email) {
        this.nome = nome;
        this.login = login;
        this.senha = password;
        this.email = email;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

}
