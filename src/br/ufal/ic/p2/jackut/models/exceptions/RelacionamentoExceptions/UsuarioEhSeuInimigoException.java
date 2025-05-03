package br.ufal.ic.p2.jackut.models.exceptions.RelacionamentoExceptions;

public class UsuarioEhSeuInimigoException extends Exception {
    public UsuarioEhSeuInimigoException(String nomeUsuario) {
        super("Função inválida: " + nomeUsuario +" é seu inimigo.");
    }
}
