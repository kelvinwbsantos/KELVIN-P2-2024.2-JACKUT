package br.ufal.ic.p2.jackut.models.exceptions;

/**
 * Exce��o lan�ada quando um atributo do usu�rio n�o foi preenchido.
 */
public class AtributoNaoPreenchidoException extends UsuarioException {

    /**
     * Construtor padr�o que define a mensagem de erro.
     */
    public AtributoNaoPreenchidoException() {
        super("Atributo n�o preenchido.");
    }
}
