package br.ufal.ic.p2.jackut.models.exceptions;

/**
 * Exce��o lan�ada quando ao iniciar sess�o, existe login ou senha incorretos.
 */
public class LoginSenhaInvalidosException extends UsuarioException {

    /**
     * Construtor padr�o que define a mensagem de erro.
     */
    public LoginSenhaInvalidosException() {
        super("Login ou senha inv�lidos.");
    }
}
