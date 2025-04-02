package br.ufal.ic.p2.jackut.models.exceptions;

/**
 * Exceção lançada quando tenta enviar convite de amizade para alguem que já está na lista de amigos
 */

public class SaoAmigosException extends UsuarioException {

    /**
     * Construtor padrão que define a mensagem de erro.
     */
    public SaoAmigosException() {
        super("Já são amigos.");
    }
}
