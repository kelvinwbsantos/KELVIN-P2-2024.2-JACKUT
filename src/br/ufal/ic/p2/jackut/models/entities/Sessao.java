package br.ufal.ic.p2.jackut.models.entities;

/**
 * Representa uma sessão de usuário autenticado na plataforma.
 * Armazena o identificador da sessão e o login do usuário associado.
 */
public class Sessao {

    /** Identificador único da sessão. */
    public String idSessao;

    /** Login do usuário associado à sessão. */
    private String login;

    /**
     * Construtor da classe {@code Sessao}.
     *
     * @param idSessao Identificador único da sessão.
     * @param login Login do usuário autenticado.
     */
    public Sessao(String idSessao, String login) {
        this.idSessao = idSessao;
        this.login = login;
    }

    /**
     * Retorna o identificador da sessão.
     *
     * @return uma {@link String} com o ID da sessão.
     */
    public String getIdSessao() {
        return idSessao;
    }

    /**
     * Retorna o login do usuário associado à sessão.
     *
     * @return uma {@link String} com o login do usuário.
     */
    public String getLogin() {
        return login;
    }
}
