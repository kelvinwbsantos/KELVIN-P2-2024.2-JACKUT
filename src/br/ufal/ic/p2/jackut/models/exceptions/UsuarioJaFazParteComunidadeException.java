package br.ufal.ic.p2.jackut.models.exceptions;

public class UsuarioJaFazParteComunidadeException extends Exception {
    public UsuarioJaFazParteComunidadeException() {
        super("Usuario já faz parte dessa comunidade.");
    }
}
