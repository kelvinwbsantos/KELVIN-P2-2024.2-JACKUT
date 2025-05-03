package br.ufal.ic.p2.jackut.models.entities;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.Queue;

/**
 * Representa uma fila de recados (mensagens) enviados a um usu�rio.
 * Os recados s�o armazenados em ordem de chegada.
 */
public class Recado implements Serializable {

    /** Fila de recados do usu�rio, mantida em ordem de inser��o (FIFO). */
    private Queue<String> recados = new LinkedList<>();

    /**
     * Adiciona um novo recado � fila.
     *
     * @param recado O conte�do textual do recado a ser adicionado.
     */
    public void adicionarRecado(String recado) {
        recados.add(recado);
    }

    /**
     * L� e remove o recado mais antigo da fila.
     *
     * @return O conte�do do recado, ou {@code null} se a fila estiver vazia.
     */
    public String lerRecado() {
        return recados.poll(); // Retorna e remove o primeiro recado
    }
}
