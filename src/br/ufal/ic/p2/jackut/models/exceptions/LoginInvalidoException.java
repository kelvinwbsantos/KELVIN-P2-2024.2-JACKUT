package br.ufal.ic.p2.jackut.models.exceptions;

/**
 * Exceção lançada quando o campo login é invalido
 */
public class LoginInvalidoException extends UsuarioException {

    /**
     * Construtor padrão que define a mensagem de erro.
     */
    public LoginInvalidoException() {
        super("Login inválido.");
    }
}
