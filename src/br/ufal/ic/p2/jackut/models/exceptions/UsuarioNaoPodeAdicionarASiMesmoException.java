package br.ufal.ic.p2.jackut.models.exceptions;

/**
 * Exceção lançada quando um usuário tenta se adicionar como amigo.
 */
public class UsuarioNaoPodeAdicionarASiMesmoException extends UsuarioException {

    /**
     * Construtor padrão que define a mensagem de erro.
     */
    public UsuarioNaoPodeAdicionarASiMesmoException() {
        super("Usuário não pode adicionar a si mesmo como amigo.");
    }
}
