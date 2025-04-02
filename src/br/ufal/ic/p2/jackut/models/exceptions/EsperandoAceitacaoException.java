package br.ufal.ic.p2.jackut.models.exceptions;

/**
 * Exce��o lan�ada quando uma conta j� enviou um convite de amizade e quer enviar novamente
 */

public class EsperandoAceitacaoException extends UsuarioException {

    /**
     * Construtor padr�o que define a mensagem de erro.
     */
    public EsperandoAceitacaoException() {
        super("Usu�rio j� est� adicionado como amigo, esperando aceita��o do convite.");
    }
}
