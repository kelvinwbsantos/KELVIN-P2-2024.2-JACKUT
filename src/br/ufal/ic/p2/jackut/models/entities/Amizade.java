package br.ufal.ic.p2.jackut.models.entities;

import java.io.Serializable;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Representa o relacionamento de amizade entre usu�rios.
 * Armazena os amigos, os convites de amizade enviados e recebidos.
 */
public class Amizade implements Serializable {

    /** Conjunto de usu�rios que s�o amigos. */
    private Set<String> amigos;

    /** Conjunto de nomes de usu�rios para os quais convites de amizade foram enviados. */
    private Set<String> convitesEnviados;

    /** Conjunto de nomes de usu�rios dos quais convites de amizade foram recebidos. */
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
     * Retorna o conjunto de amigos do usu�rio.
     *
     * @return um {@link Set} contendo os nomes dos amigos.
     */
    public Set<String> getAmigos() {
        return amigos;
    }

    /**
     * Retorna o conjunto de convites de amizade enviados pelo usu�rio.
     *
     * @return um {@link Set} contendo os nomes dos usu�rios para os quais convites foram enviados.
     */
    public Set<String> getConvitesEnviados() {
        return convitesEnviados;
    }

    /**
     * Retorna o conjunto de convites de amizade recebidos pelo usu�rio.
     *
     * @return um {@link Set} contendo os nomes dos usu�rios que enviaram convites de amizade.
     */
    public Set<String> getConvitesRecebidos() {
        return convitesRecebidos;
    }
}
