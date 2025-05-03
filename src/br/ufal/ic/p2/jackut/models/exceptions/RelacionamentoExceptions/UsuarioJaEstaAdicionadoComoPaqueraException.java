package br.ufal.ic.p2.jackut.models.exceptions.RelacionamentoExceptions;

public class UsuarioJaEstaAdicionadoComoPaqueraException extends Exception {
    public UsuarioJaEstaAdicionadoComoPaqueraException() {
        super("Usuário já está adicionado como paquera.");
    }
}
