package com.example.workshop_ddm_teste;

public class Tarefa {
    public String id;
    public String nome;
    public boolean concluida;
    public String dataHora; // novo campo — ex: "10/05/2026 14:30"

    public Tarefa(String id, String nome, boolean concluida, String dataHora) {
        this.id        = id;
        this.nome      = nome;
        this.concluida = concluida;
        this.dataHora  = dataHora;
    }
}