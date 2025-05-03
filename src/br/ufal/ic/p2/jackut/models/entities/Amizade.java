package br.ufal.ic.p2.jackut.models.entities;

import java.io.Serializable;
import java.util.LinkedHashSet;
import java.util.Set;

public class Amizade implements Serializable {
    private Set<String> amigos;
    private Set<String> convitesEnviados;
    private Set<String> convitesRecebidos;

    public Amizade() {
        this.amigos = new LinkedHashSet<>();
        this.convitesEnviados = new LinkedHashSet<>();
        this.convitesRecebidos = new LinkedHashSet<>();
    }

    public Set<String> getAmigos() {
        return amigos;
    }

    public Set<String> getConvitesEnviados() {
        return convitesEnviados;
    }

    public Set<String> getConvitesRecebidos() {
        return convitesRecebidos;
    }
}
