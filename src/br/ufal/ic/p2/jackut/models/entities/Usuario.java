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
    private Set<String> amigos = new LinkedHashSet<>();
    private Set<String> convitesEnviados = new LinkedHashSet<>();
    private Set<String> convitesRecebidos = new LinkedHashSet<>();
    private Set<String> mensagensEnviadas = new LinkedHashSet<>();
    private Queue<String> recados = new LinkedList<>();

    /**
     * Construtor da classe Usuario.
     *
     * @param login Nome de usu�rio �nico.
     * @param senha Senha do usu�rio.
     * @param nome  Nome completo do usu�rio.
     * @throws UsuarioException Se login ou senha forem inv�lidos.
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

    /**
     * Define um atributo personalizado para o usu�rio.
     *
     * @param chave Nome do atributo.
     * @param valor Valor do atributo.
     */
    public void setAtributo(String chave, String valor) {
        this.atributos.put(chave, valor);
    }

    /**
     * Obt�m o valor de um atributo do usu�rio.
     *
     * @param chave Nome do atributo desejado.
     * @return O valor do atributo.
     * @throws UsuarioException Se o atributo n�o estiver preenchido.
     */
    public String getAtributo(String chave) throws UsuarioException {
        if (!this.atributos.containsKey(chave)) {
            throw new AtributoNaoPreenchidoException();
        }
        return this.atributos.get(chave);
    }

    /**
     * Envia um convite de amizade para outro usu�rio.
     *
     * @param loginAmigo Login do usu�rio que receber� o convite.
     * @throws EsperandoAceitacaoException Se o convite j� foi enviado e est� pendente de aceita��o.
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
     * Obt�m a lista de amigos do usu�rio.
     *
     * @return Um conjunto contendo os logins dos amigos.
     */
    public Set<String> getAmigos() {
        return amigos;
    }

    /**
     * Recebe um convite de amizade de outro usu�rio.
     *
     * @param loginAmigo Login do usu�rio que enviou o convite.
     * @throws SaoAmigosException Se os usu�rios j� forem amigos.
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
     * Recebe uma mensagem de outro usu�rio.
     *
     * @param loginRemetente Login do remetente.
     * @param mensagem       Conte�do da mensagem.
     */
    public void receberMensagem(String loginRemetente, String mensagem) {
        this.recados.add(mensagem);
    }

    /**
     * L� um recado da caixa de entrada do usu�rio.
     *
     * @return O recado mais antigo.
     * @throws NaoHaRecadosException Se n�o houver recados dispon�veis.
     */
    public String getRecados() throws NaoHaRecadosException {
        if (this.recados.isEmpty()) {
            throw new NaoHaRecadosException();
        }
        return this.recados.poll(); // Retorna e remove o primeiro recado da fila
    }
}
