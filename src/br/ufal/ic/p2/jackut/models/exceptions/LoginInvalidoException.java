package br.ufal.ic.p2.jackut.models.exceptions;

/**
 * Exce��o lan�ada quando o campo login � invalido
 */
public class LoginInvalidoException extends UsuarioException {

    /**
     * Construtor padr�o que define a mensagem de erro.
     */
    public LoginInvalidoException() {
        super("Login inv�lido.");
    }
}
