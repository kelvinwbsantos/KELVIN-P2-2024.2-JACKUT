package br.ufal.ic.p2.jackut.models.exceptions;

/**
 * Exceção lançada quando ao iniciar sessão, existe login ou senha incorretos.
 */
public class LoginSenhaInvalidosException extends UsuarioException {

    /**
     * Construtor padrão que define a mensagem de erro.
     */
    public LoginSenhaInvalidosException() {
        super("Login ou senha inválidos.");
    }
}
