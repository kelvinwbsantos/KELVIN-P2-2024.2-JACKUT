package br.ufal.ic.p2.jackut.models.exceptions.RelacionamentoExceptions;

public class UsuarioNaoPodeSerInimigoDeSiMesmoException extends Exception {
    public UsuarioNaoPodeSerInimigoDeSiMesmoException() {
        super("Usu�rio n�o pode ser inimigo de si mesmo.");
    }
}
