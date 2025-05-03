package br.ufal.ic.p2.jackut.models.entities;

import java.io.Serializable;

public class Mensagem implements Serializable {
    private String mensagem;

    public Mensagem(String mensagem, String login, String comunidade) {
        this.mensagem = mensagem;
    }

    public String getMensagem() {
        return mensagem;
    }


}
