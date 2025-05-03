package br.ufal.ic.p2.jackut.models.entities;

public class Sessao {
    public String idSessao;
    private String login;

    public Sessao(String idSessao, String login) {
        this.idSessao = idSessao;
        this.login = login;
    }

    public String getIdSessao() {
        return idSessao;
    }

    public String getLogin() {
        return login;
    }
}
