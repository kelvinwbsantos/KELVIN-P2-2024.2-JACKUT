package br.ufal.ic.p2.jackut.models.entities;

import br.ufal.ic.p2.jackut.models.exceptions.*;
import java.io.Serializable;
import java.util.*;

/**
 * Classe que representa um usuário no sistema Jackut.
 * Um usuário possui login, senha, nome, atributos personalizados e pode participar de comunidades.
 */
public class Usuario implements Serializable {
    private static final long serialVersionUID = 1L;

    /** Login único do usuário. */
    private String login;

    /** Senha de acesso do usuário. */
    private String senha;

    /** Nome completo do usuário. */
    private String nome;

    /** Mapa de atributos personalizados do usuário (chave: nome do atributo, valor: conteúdo). */
    private Map<String, String> atributos = new HashMap<>();

    /** Lista com os nomes das comunidades das quais o usuário participa. */
    public List<String> comunidades = new ArrayList<>();

    /**
     * Construtor da classe {@code Usuario}.
     *
     * @param login Nome de usuário único.
     * @param senha Senha do usuário.
     * @param nome Nome completo do usuário.
     * @throws UsuarioException Se login ou senha forem inválidos.
     */
    public Usuario(String login, String senha, String nome) {
        this.login = login;
        this.senha = senha;
        this.nome = nome;
    }

    /**
     * Retorna o login do usuário.
     *
     * @return Uma {@link String} contendo o login do usuário.
     */
    public String getLogin() {
        return login;
    }

    /**
     * Retorna o nome completo do usuário.
     *
     * @return Uma {@link String} contendo o nome do usuário.
     */
    public String getNome() {
        return nome;
    }

    /**
     * Retorna a senha do usuário.
     *
     * @return Uma {@link String} contendo a senha.
     */
    public String getSenha() {
        return senha;
    }

    /**
     * Retorna o valor de um atributo específico do usuário.
     *
     * @param chave Nome do atributo a ser recuperado.
     * @return O valor associado ao atributo.
     * @throws UsuarioException Se o atributo não estiver preenchido.
     */
    public String getAtributo(String chave) throws UsuarioException {
        if (!this.atributos.containsKey(chave)) {
            throw new AtributoNaoPreenchidoException();
        }
        return this.atributos.get(chave);
    }

    /**
     * Define ou atualiza um atributo personalizado para o usuário.
     *
     * @param chave Nome do atributo.
     * @param valor Valor a ser associado ao atributo.
     */
    public void setAtributo(String chave, String valor) {
        this.atributos.put(chave, valor);
    }
}
