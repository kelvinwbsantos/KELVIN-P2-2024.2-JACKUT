package br.ufal.ic.p2.jackut.models.exceptions.RelacionamentoExceptions;

public class UsuarioNaoPodeSerPaqueraDeSiMesmoException extends Exception {
    public UsuarioNaoPodeSerPaqueraDeSiMesmoException() {
        super("Usu�rio n�o pode ser paquera de si mesmo.");
    }
}
