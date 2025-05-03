package br.ufal.ic.p2.jackut.models.entities;

import java.io.Serializable;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Representa o relacionamento de amizade entre usuários.
 * Armazena os amigos, os convites de amizade enviados e recebidos.
 */
public class Amizade implements Serializable {

    /** Conjunto de usuários que são amigos. */
    private Set<String> amigos;

    /** Conjunto de nomes de usuários para os quais convites de amizade foram enviados. */
    private Set<String> convitesEnviados;

    /** Conjunto de nomes de usuários dos quais convites de amizade foram recebidos. */
    private Set<String> convitesRecebidos;

    /**
     * Construtor da classe {@code Amizade}.
     * Inicializa os conjuntos de amigos, convites enviados e convites recebidos como {@link LinkedHashSet}.
     */
    public Amizade() {
        this.amigos = new LinkedHashSet<>();
        this.convitesEnviados = new LinkedHashSet<>();
        this.convitesRecebidos = new LinkedHashSet<>();
    }

    /**
     * Retorna o conjunto de amigos do usuário.
     *
     * @return um {@link Set} contendo os nomes dos amigos.
     */
    public Set<String> getAmigos() {
        return amigos;
    }

    /**
     * Retorna o conjunto de convites de amizade enviados pelo usuário.
     *
     * @return um {@link Set} contendo os nomes dos usuários para os quais convites foram enviados.
     */
    public Set<String> getConvitesEnviados() {
        return convitesEnviados;
    }

    /**
     * Retorna o conjunto de convites de amizade recebidos pelo usuário.
     *
     * @return um {@link Set} contendo os nomes dos usuários que enviaram convites de amizade.
     */
    public Set<String> getConvitesRecebidos() {
        return convitesRecebidos;
    }
}
