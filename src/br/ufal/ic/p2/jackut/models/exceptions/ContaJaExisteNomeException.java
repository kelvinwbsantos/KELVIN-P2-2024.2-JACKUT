package br.ufal.ic.p2.jackut.models.exceptions;

/**
 * Exceção lançada quando uma conta já existe com o nome informado.
 */
public class ContaJaExisteNomeException extends UsuarioException {

    /**
     * Construtor padrão que define a mensagem de erro.
     */
    public ContaJaExisteNomeException() {
        super("Conta com esse nome já existe.");
    }
}
