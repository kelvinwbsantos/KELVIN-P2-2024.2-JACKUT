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
    private Set<String> amigos = new LinkedHashSet<>();
    private Set<String> convitesEnviados = new LinkedHashSet<>();
    private Set<String> convitesRecebidos = new LinkedHashSet<>();
    private Set<String> mensagensEnviadas = new LinkedHashSet<>();
    private Queue<String> recados = new LinkedList<>();

    /**
     * Construtor da classe Usuario.
     *
     * @param login Nome de usuário único.
     * @param senha Senha do usuário.
     * @param nome  Nome completo do usuário.
     * @throws UsuarioException Se login ou senha forem inválidos.
     */
    public Usuario(String login, String senha, String nome) throws UsuarioException {
        if (login == null || login.trim().isEmpty()) {
            throw new LoginInvalidoException();
        }
        if (senha == null || senha.trim().isEmpty()) {
            throw new SenhaInvalidaException();
        }
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

    /**
     * Define um atributo personalizado para o usuário.
     *
     * @param chave Nome do atributo.
     * @param valor Valor do atributo.
     */
    public void setAtributo(String chave, String valor) {
        this.atributos.put(chave, valor);
    }

    /**
     * Obtém o valor de um atributo do usuário.
     *
     * @param chave Nome do atributo desejado.
     * @return O valor do atributo.
     * @throws UsuarioException Se o atributo não estiver preenchido.
     */
    public String getAtributo(String chave) throws UsuarioException {
        if (!this.atributos.containsKey(chave)) {
            throw new AtributoNaoPreenchidoException();
        }
        return this.atributos.get(chave);
    }

    /**
     * Envia um convite de amizade para outro usuário.
     *
     * @param loginAmigo Login do usuário que receberá o convite.
     * @throws EsperandoAceitacaoException Se o convite já foi enviado e está pendente de aceitação.
     */
    public void enviarConvite(String loginAmigo) throws EsperandoAceitacaoException {
        if (this.convitesRecebidos.contains(loginAmigo)) {
            this.convitesRecebidos.remove(loginAmigo);
            this.amigos.add(loginAmigo);
            return;
        }
        if (this.convitesEnviados.contains(loginAmigo)) {
            throw new EsperandoAceitacaoException();
        }
        this.convitesEnviados.add(loginAmigo);
    }

    /**
     * Obtém a lista de amigos do usuário.
     *
     * @return Um conjunto contendo os logins dos amigos.
     */
    public Set<String> getAmigos() {
        return amigos;
    }

    /**
     * Recebe um convite de amizade de outro usuário.
     *
     * @param loginAmigo Login do usuário que enviou o convite.
     * @throws SaoAmigosException Se os usuários já forem amigos.
     */
    public void receberConvite(String loginAmigo) throws SaoAmigosException {
        if (this.convitesEnviados.contains(loginAmigo)) {
            this.convitesEnviados.remove(loginAmigo);
            this.amigos.add(loginAmigo);
        } else {
            this.convitesRecebidos.add(loginAmigo);
        }
    }

    /**
     * Recebe uma mensagem de outro usuário.
     *
     * @param loginRemetente Login do remetente.
     * @param mensagem       Conteúdo da mensagem.
     */
    public void receberMensagem(String loginRemetente, String mensagem) {
        this.recados.add(mensagem);
    }

    /**
     * Lê um recado da caixa de entrada do usuário.
     *
     * @return O recado mais antigo.
     * @throws NaoHaRecadosException Se não houver recados disponíveis.
     */
    public String getRecados() throws NaoHaRecadosException {
        if (this.recados.isEmpty()) {
            throw new NaoHaRecadosException();
        }
        return this.recados.poll(); // Retorna e remove o primeiro recado da fila
    }
}
