package br.ufal.ic.p2.jackut.models.entities;

/**
 * Representa uma sess�o de usu�rio autenticado na plataforma.
 * Armazena o identificador da sess�o e o login do usu�rio associado.
 */
public class Sessao {

    /** Identificador �nico da sess�o. */
    public String idSessao;

    /** Login do usu�rio associado � sess�o. */
    private String login;

    /**
     * Construtor da classe {@code Sessao}.
     *
     * @param idSessao Identificador �nico da sess�o.
     * @param login Login do usu�rio autenticado.
     */
    public Sessao(String idSessao, String login) {
        this.idSessao = idSessao;
        this.login = login;
    }

    /**
     * Retorna o identificador da sess�o.
     *
     * @return uma {@link String} com o ID da sess�o.
     */
    public String getIdSessao() {
        return idSessao;
    }

    /**
     * Retorna o login do usu�rio associado � sess�o.
     *
     * @return uma {@link String} com o login do usu�rio.
     */
    public String getLogin() {
        return login;
    }
}
