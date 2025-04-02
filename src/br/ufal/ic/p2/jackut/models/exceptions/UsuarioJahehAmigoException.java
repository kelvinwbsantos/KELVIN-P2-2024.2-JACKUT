package br.ufal.ic.p2.jackut.models.exceptions;

/**
 * Exce��o lan�ada quando um usu�rio tenta adicionar outro usu�rio que j� � seu amigo.
 */
public class UsuarioJahehAmigoException extends UsuarioException {

    /**
     * Construtor padr�o que define a mensagem de erro.
     */
    public UsuarioJahehAmigoException() {
        super("Usu�rio j� est� adicionado como amigo.");
    }
}
