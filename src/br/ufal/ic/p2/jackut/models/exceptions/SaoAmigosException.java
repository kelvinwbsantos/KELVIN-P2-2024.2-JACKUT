package br.ufal.ic.p2.jackut.models.exceptions;

/**
 * Exce��o lan�ada quando tenta enviar convite de amizade para alguem que j� est� na lista de amigos
 */

public class SaoAmigosException extends UsuarioException {

    /**
     * Construtor padr�o que define a mensagem de erro.
     */
    public SaoAmigosException() {
        super("J� s�o amigos.");
    }
}
