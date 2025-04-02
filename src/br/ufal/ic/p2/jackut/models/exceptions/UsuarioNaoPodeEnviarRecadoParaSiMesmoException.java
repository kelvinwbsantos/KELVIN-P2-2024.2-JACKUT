package br.ufal.ic.p2.jackut.models.exceptions;

/**
 * Exce��o lan�ada quando um usu�rio tenta enviar um recado para si mesmo.
 */
public class UsuarioNaoPodeEnviarRecadoParaSiMesmoException extends UsuarioException {

    /**
     * Construtor padr�o que define a mensagem de erro.
     */
    public UsuarioNaoPodeEnviarRecadoParaSiMesmoException() {
        super("Usu�rio n�o pode enviar recado para si mesmo.");
    }
}
