package br.ufal.ic.p2.jackut.models.exceptions;

/**
 * Exce��o lan�ada quando uma conta j� existe com o nome informado.
 */
public class ContaJaExisteNomeException extends UsuarioException {

    /**
     * Construtor padr�o que define a mensagem de erro.
     */
    public ContaJaExisteNomeException() {
        super("Conta com esse nome j� existe.");
    }
}
