package br.ufal.ic.p2.jackut.models.exceptions;

/**
 * Exceção lançada quando um usuário tenta enviar um recado para si mesmo.
 */
public class UsuarioNaoPodeEnviarRecadoParaSiMesmoException extends UsuarioException {

    /**
     * Construtor padrão que define a mensagem de erro.
     */
    public UsuarioNaoPodeEnviarRecadoParaSiMesmoException() {
        super("Usuário não pode enviar recado para si mesmo.");
    }
}
