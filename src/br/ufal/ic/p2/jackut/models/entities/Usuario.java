package br.ufal.ic.p2.jackut.models.entities;

import br.ufal.ic.p2.jackut.models.exceptions.*;
import java.io.Serializable;
import java.util.*;

/**
 * Classe que representa um usuário no sistema Jackut.
 * Um usuário pode ter amigos, enviar e receber convites, mensagens e armazenar atributos personalizados.
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
     * @param login Nome de usuário único.
     * @param senha Senha do usuário.
     * @param nome  Nome completo do usuário.
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
     * @return O login do usuário.
     */
    public String getLogin() {
        return login;
    }

    /**
     * Retorna o nome do usuário.
     *
     * @return O nome do usuário.
     */
    public String getNome() {
        return nome;
    }

    /**
     * Retorna a senha do usuário.
     *
     * @return A senha do usuário.
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
