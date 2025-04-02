package br.ufal.ic.p2.jackut.models.exceptions;

/**
 * Exceção lançada quando uma conta já enviou um convite de amizade e quer enviar novamente
 */

public class EsperandoAceitacaoException extends UsuarioException {

    /**
     * Construtor padrão que define a mensagem de erro.
     */
    public EsperandoAceitacaoException() {
        super("Usuário já está adicionado como amigo, esperando aceitação do convite.");
    }
}
