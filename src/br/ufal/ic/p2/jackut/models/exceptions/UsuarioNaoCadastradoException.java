package br.ufal.ic.p2.jackut.models.exceptions;

/**
 * Exce��o lan�ada quando uma opera��o � realizada com um usu�rio que n�o est� cadastrado no sistema.
 */
public class UsuarioNaoCadastradoException extends UsuarioException {

    /**
     * Construtor padr�o que define a mensagem de erro.
     */
    public UsuarioNaoCadastradoException() {
        super("Usu�rio n�o cadastrado.");
    }
}
