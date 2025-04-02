package br.ufal.ic.p2.jackut.models.exceptions;

/**
 * Classe base para exce��es relacionadas a opera��es com usu�rios no sistema Jackut.
 * Todas as exce��es espec�ficas de usu�rio devem herdar desta classe.
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
