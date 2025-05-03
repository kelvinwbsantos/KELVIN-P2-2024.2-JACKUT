package br.ufal.ic.p2.jackut.models.exceptions;

/**
 * Exce��o lan�ada quando n�o h� recados
 */
public class NaoHaMensagensException extends UsuarioException {

    /**
     * Construtor padr�o que define a mensagem de erro.
     */
    public NaoHaMensagensException() {
        super("N�o h� mensagens.");
    }
}
