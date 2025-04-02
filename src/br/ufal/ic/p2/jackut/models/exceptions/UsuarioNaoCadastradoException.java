package br.ufal.ic.p2.jackut.models.exceptions;

/**
 * Exceção lançada quando uma operação é realizada com um usuário que não está cadastrado no sistema.
 */
public class UsuarioNaoCadastradoException extends UsuarioException {

    /**
     * Construtor padrão que define a mensagem de erro.
     */
    public UsuarioNaoCadastradoException() {
        super("Usuário não cadastrado.");
    }
}
