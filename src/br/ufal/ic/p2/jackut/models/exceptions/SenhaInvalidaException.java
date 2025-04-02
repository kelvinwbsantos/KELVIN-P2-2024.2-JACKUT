package br.ufal.ic.p2.jackut.models.exceptions;

/**
 * Exceção lançada quando o campo senha é invalido
 */
public class SenhaInvalidaException extends UsuarioException {

    /**
     * Construtor padrão que define a mensagem de erro.
     */
    public SenhaInvalidaException() {
        super("Senha inválida.");
    }
}
