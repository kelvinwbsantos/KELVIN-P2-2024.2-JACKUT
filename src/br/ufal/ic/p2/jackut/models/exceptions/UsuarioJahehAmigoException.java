package br.ufal.ic.p2.jackut.models.exceptions;

/**
 * Exceção lançada quando um usuário tenta adicionar outro usuário que já é seu amigo.
 */
public class UsuarioJahehAmigoException extends UsuarioException {

    /**
     * Construtor padrão que define a mensagem de erro.
     */
    public UsuarioJahehAmigoException() {
        super("Usuário já está adicionado como amigo.");
    }
}
