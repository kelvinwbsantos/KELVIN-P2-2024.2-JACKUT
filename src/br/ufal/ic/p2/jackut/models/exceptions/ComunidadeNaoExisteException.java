package br.ufal.ic.p2.jackut.models.exceptions;

public class ComunidadeNaoExisteException extends Exception {
    public ComunidadeNaoExisteException() {
        super("Comunidade n�o existe.");
    }
}