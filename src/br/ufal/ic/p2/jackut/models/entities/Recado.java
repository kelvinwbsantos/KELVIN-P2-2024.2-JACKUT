package br.ufal.ic.p2.jackut.models.entities;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.Queue;

/**
 * Representa uma fila de recados (mensagens) enviados a um usuário.
 * Os recados são armazenados em ordem de chegada.
 */
public class Recado implements Serializable {

    /** Fila de recados do usuário, mantida em ordem de inserção (FIFO). */
    private Queue<String> recados = new LinkedList<>();

    /**
     * Adiciona um novo recado à fila.
     *
     * @param recado O conteúdo textual do recado a ser adicionado.
     */
    public void adicionarRecado(String recado) {
        recados.add(recado);
    }

    /**
     * Lê e remove o recado mais antigo da fila.
     *
     * @return O conteúdo do recado, ou {@code null} se a fila estiver vazia.
     */
    public String lerRecado() {
        return recados.poll(); // Retorna e remove o primeiro recado
    }
}
