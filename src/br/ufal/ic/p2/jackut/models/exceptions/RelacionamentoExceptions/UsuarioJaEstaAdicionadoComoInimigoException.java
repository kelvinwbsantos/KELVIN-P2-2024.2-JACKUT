package br.ufal.ic.p2.jackut.models.exceptions.RelacionamentoExceptions;

public class UsuarioJaEstaAdicionadoComoInimigoException extends Exception {
    public UsuarioJaEstaAdicionadoComoInimigoException() {
        super("Usuário já está adicionado como inimigo.");
    }
}


