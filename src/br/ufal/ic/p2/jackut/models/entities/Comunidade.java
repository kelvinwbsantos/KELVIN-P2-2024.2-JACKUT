package br.ufal.ic.p2.jackut.models.entities;

import java.io.Serializable;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Representa uma comunidade dentro da plataforma.
 * Cada comunidade possui um nome, descri��o, dono e uma lista de membros.
 */
public class Comunidade implements Serializable {

    /** Nome da comunidade. */
    private String nome;

    /** Descri��o da comunidade. */
    private String descricao;

    /** Login do usu�rio que � dono da comunidade. */
    private String loginDono;

    /** Conjunto de logins dos membros da comunidade. */
    public Set<String> membros = new LinkedHashSet<>();

    /**
     * Construtor da classe {@code Comunidade}.
     *
     * @param loginDono Login do usu�rio criador (dono) da comunidade.
     * @param nome Nome da comunidade.
     * @param descricao Descri��o da comunidade.
     */
    public Comunidade(String loginDono, String nome, String descricao) {
        this.nome = nome;
        this.descricao = descricao;
        this.loginDono = loginDono;
        membros.add(loginDono);
    }

    /**
     * Retorna a descri��o da comunidade.
     *
     * @return uma {@link String} com a descri��o.
     */
    public String getDescricao() {
        return descricao;
    }

    /**
     * Retorna o login do dono da comunidade.
     *
     * @return uma {@link String} com o login do dono.
     */
    public String getloginDono() {
        return loginDono;
    }

    /**
     * Retorna o conjunto de membros da comunidade.
     *
     * @return um {@link Set} contendo os logins dos membros.
     */
    public Set<String> getMembrosComunidade() {
        return membros;
    }

    /**
     * Adiciona um novo membro � comunidade.
     *
     * @param membro Login do usu�rio a ser adicionado como membro.
     */
    public void adicionarMembro(String membro) {
        membros.add(membro);
    }
}
