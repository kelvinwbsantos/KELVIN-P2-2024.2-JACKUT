package br.ufal.ic.p2.jackut.models.exceptions.RelacionamentoExceptions;

public class UsuarioEhSeuInimigoException extends Exception {
    public UsuarioEhSeuInimigoException(String nomeUsuario) {
        super("Fun��o inv�lida: " + nomeUsuario +" � seu inimigo.");
    }
}
