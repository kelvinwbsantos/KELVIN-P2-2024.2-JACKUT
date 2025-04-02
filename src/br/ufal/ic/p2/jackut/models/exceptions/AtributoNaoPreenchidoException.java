package br.ufal.ic.p2.jackut.models.exceptions;

/**
 * Exceção lançada quando um atributo do usuário não foi preenchido.
 */
public class AtributoNaoPreenchidoException extends UsuarioException {

    /**
     * Construtor padrão que define a mensagem de erro.
     */
    public AtributoNaoPreenchidoException() {
        super("Atributo não preenchido.");
    }
}
