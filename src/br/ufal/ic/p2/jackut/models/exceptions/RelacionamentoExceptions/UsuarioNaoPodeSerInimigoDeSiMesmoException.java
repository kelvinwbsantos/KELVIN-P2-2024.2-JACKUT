package br.ufal.ic.p2.jackut.models.exceptions.RelacionamentoExceptions;

public class UsuarioNaoPodeSerInimigoDeSiMesmoException extends Exception {
    public UsuarioNaoPodeSerInimigoDeSiMesmoException() {
        super("Usuário não pode ser inimigo de si mesmo.");
    }
}
