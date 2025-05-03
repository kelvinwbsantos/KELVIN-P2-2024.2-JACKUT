package br.ufal.ic.p2.jackut.models.entities;

import br.ufal.ic.p2.jackut.models.exceptions.*;
import java.io.Serializable;
import java.util.*;

/**
 * Classe que representa um usu�rio no sistema Jackut.
 * Um usu�rio possui login, senha, nome, atributos personalizados e pode participar de comunidades.
 */
public class Usuario implements Serializable {
    private static final long serialVersionUID = 1L;

    /** Login �nico do usu�rio. */
    private String login;

    /** Senha de acesso do usu�rio. */
    private String senha;

    /** Nome completo do usu�rio. */
    private String nome;

    /** Mapa de atributos personalizados do usu�rio (chave: nome do atributo, valor: conte�do). */
    private Map<String, String> atributos = new HashMap<>();

    /** Lista com os nomes das comunidades das quais o usu�rio participa. */
    public List<String> comunidades = new ArrayList<>();

    /**
     * Construtor da classe {@code Usuario}.
     *
     * @param login Nome de usu�rio �nico.
     * @param senha Senha do usu�rio.
     * @param nome Nome completo do usu�rio.
     * @throws UsuarioException Se login ou senha forem inv�lidos.
     */
    public Usuario(String login, String senha, String nome) {
        this.login = login;
        this.senha = senha;
        this.nome = nome;
    }

    /**
     * Retorna o login do usu�rio.
     *
     * @return Uma {@link String} contendo o login do usu�rio.
     */
    public String getLogin() {
        return login;
    }

    /**
     * Retorna o nome completo do usu�rio.
     *
     * @return Uma {@link String} contendo o nome do usu�rio.
     */
    public String getNome() {
        return nome;
    }

    /**
     * Retorna a senha do usu�rio.
     *
     * @return Uma {@link String} contendo a senha.
     */
    public String getSenha() {
        return senha;
    }

    /**
     * Retorna o valor de um atributo espec�fico do usu�rio.
     *
     * @param chave Nome do atributo a ser recuperado.
     * @return O valor associado ao atributo.
     * @throws UsuarioException Se o atributo n�o estiver preenchido.
     */
    public String getAtributo(String chave) throws UsuarioException {
        if (!this.atributos.containsKey(chave)) {
            throw new AtributoNaoPreenchidoException();
        }
        return this.atributos.get(chave);
    }

    /**
     * Define ou atualiza um atributo personalizado para o usu�rio.
     *
     * @param chave Nome do atributo.
     * @param valor Valor a ser associado ao atributo.
     */
    public void setAtributo(String chave, String valor) {
        this.atributos.put(chave, valor);
    }
}
