package br.ufal.ic.p2.jackut.models.exceptions;

/**
 * Exce��o lan�ada quando um usu�rio tenta se adicionar como amigo.
 */
public class UsuarioNaoPodeAdicionarASiMesmoException extends UsuarioException {

    /**
     * Construtor padr�o que define a mensagem de erro.
     */
    public UsuarioNaoPodeAdicionarASiMesmoException() {
        super("Usu�rio n�o pode adicionar a si mesmo como amigo.");
    }
}
