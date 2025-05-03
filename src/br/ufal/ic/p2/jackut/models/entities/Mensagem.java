package br.ufal.ic.p2.jackut.models.entities;

import java.io.Serializable;

/**
 * Representa uma mensagem postada em uma comunidade.
 * Atualmente, armazena apenas o conte�do textual da mensagem.
 */
public class Mensagem implements Serializable {

    /** Conte�do textual da mensagem. */
    private String mensagem;

    /**
     * Construtor da classe {@code Mensagem}.
     *
     * @param mensagem O conte�do da mensagem.
     * @param login O login do usu�rio que postou a mensagem (atualmente ignorado).
     * @param comunidade O nome da comunidade onde a mensagem foi postada (atualmente ignorado).
     */
    public Mensagem(String mensagem, String login, String comunidade) {
        this.mensagem = mensagem;
    }

    /**
     * Retorna o conte�do da mensagem.
     *
     * @return uma {@link String} com o texto da mensagem.
     */
    public String getMensagem() {
        return mensagem;
    }
}
