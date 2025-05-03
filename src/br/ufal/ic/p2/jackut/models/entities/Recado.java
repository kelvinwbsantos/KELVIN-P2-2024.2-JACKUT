package br.ufal.ic.p2.jackut.models.entities;

import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;

public class Recado implements Serializable {
    private Queue<String> recados = new LinkedList<>();

    // Métodos para manipular os recados (exemplo):
    public void adicionarRecado(String recado) {
        recados.add(recado);
    }


    public String lerRecado() {
        return recados.poll(); // Retorna e remove o primeiro recado
    }


}
