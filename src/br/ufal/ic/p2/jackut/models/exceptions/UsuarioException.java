package br.ufal.ic.p2.jackut.models.exceptions;

/**
 * Classe base para exceções relacionadas a operações com usuários no sistema Jackut.
 * Todas as exceções específicas de usuário devem herdar desta classe.
 */
public class UsuarioException extends Exception {

    /**
     * Construtor que permite definir uma mensagem de erro personalizada.
     *
     * @param mensagem A mensagem descritiva do erro.
     */
    public UsuarioException(String mensagem) {
        super(mensagem);
    }
}
