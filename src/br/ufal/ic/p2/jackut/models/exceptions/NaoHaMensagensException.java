package br.ufal.ic.p2.jackut.models.exceptions;

/**
 * Exceção lançada quando não há recados
 */
public class NaoHaMensagensException extends UsuarioException {

    /**
     * Construtor padrão que define a mensagem de erro.
     */
    public NaoHaMensagensException() {
        super("Não há mensagens.");
    }
}
