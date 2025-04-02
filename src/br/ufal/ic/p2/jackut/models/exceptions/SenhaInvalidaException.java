package br.ufal.ic.p2.jackut.models.exceptions;

/**
 * Exce��o lan�ada quando o campo senha � invalido
 */
public class SenhaInvalidaException extends UsuarioException {

    /**
     * Construtor padr�o que define a mensagem de erro.
     */
    public SenhaInvalidaException() {
        super("Senha inv�lida.");
    }
}
