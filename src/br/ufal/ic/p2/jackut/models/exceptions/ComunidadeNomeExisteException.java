package br.ufal.ic.p2.jackut.models.exceptions;

public class ComunidadeNomeExisteException extends Exception {
    public ComunidadeNomeExisteException() {
        super("Comunidade com esse nome já existe.");
    }
}