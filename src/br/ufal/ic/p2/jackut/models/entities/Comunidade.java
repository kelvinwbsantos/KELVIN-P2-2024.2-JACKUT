package br.ufal.ic.p2.jackut.models.entities;

import java.io.Serializable;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

public class Comunidade implements Serializable {
    private String nome;
    private String descricao;
    private String loginDono;
    public Set<String> membros = new LinkedHashSet<>();

    public Comunidade(String loginDono, String nome, String descricao) {
        this.nome = nome;
        this.descricao = descricao;
        this.loginDono = loginDono;
        membros.add(loginDono);
    }

    public String getDescricao() {
        return descricao;
    }

    public String getloginDono() {
        return loginDono;
    }

    public Set<String> getMembrosComunidade() {
        return membros;
    }

    public void adicionarMembro(String membro) {
        membros.add(membro);
    }
}
