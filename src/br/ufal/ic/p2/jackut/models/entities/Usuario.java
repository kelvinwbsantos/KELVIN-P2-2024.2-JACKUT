package br.ufal.ic.p2.jackut.models.entities;

import br.ufal.ic.p2.jackut.models.exceptions.*;
import java.io.Serializable;
import java.util.*;

/**
 * Classe que representa um usu�rio no sistema Jackut.
 * Um usu�rio pode ter amigos, enviar e receber convites, mensagens e armazenar atributos personalizados.
 */
public class Usuario implements Serializable {
    private static final long serialVersionUID = 1L;

    private String login;
    private String senha;
    private String nome;
    private Map<String, String> atributos = new HashMap<>();
    public List<String> comunidades = new ArrayList<>();

    /**
     * Construtor da classe Usuario.
     *
     * @param login Nome de usu�rio �nico.
     * @param senha Senha do usu�rio.
     * @param nome  Nome completo do usu�rio.
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
     * @return O login do usu�rio.
     */
    public String getLogin() {
        return login;
    }

    /**
     * Retorna o nome do usu�rio.
     *
     * @return O nome do usu�rio.
     */
    public String getNome() {
        return nome;
    }

    /**
     * Retorna a senha do usu�rio.
     *
     * @return A senha do usu�rio.
     */
    public String getSenha() {
        return senha;
    }

    public String getAtributo(String chave) throws UsuarioException {
        if (!this.atributos.containsKey(chave)) {
            throw new AtributoNaoPreenchidoException();
        }
        return this.atributos.get(chave);
    }

    public void setAtributo(String chave, String valor) {
        this.atributos.put(chave, valor);
    }


}
