package br.ufal.ic.p2.jackut;

import br.ufal.ic.p2.jackut.models.exceptions.*;
import br.ufal.ic.p2.jackut.models.entities.*;

import java.io.*;
import java.util.*;

/**
 * Classe Facade que gerencia usu�rios, sess�es, amizades e mensagens no sistema Jackut.
 */
public class Facade implements Serializable {

    private static final long serialVersionUID = 1L;
    private static final String DATA_FILE = "jackut_data.ser";

    /**
     * Mapa que armazena os usu�rios cadastrados, associando o login ao objeto Usuario.
     */
    Map<String, Usuario> usuarios = new HashMap<>();

    /**
     * Mapa que gerencia as sess�es ativas, associando um identificador de sess�o ao objeto Usuario.
     */
    Map<String, Usuario> sessoes = new HashMap<>();

    /**
     * Construtor da Facade, que carrega os dados do sistema.
     */
    public Facade() {
        carregarEstado();
    }

    /**
     * Reseta o sistema, apagando todos os usu�rios cadastrados.
     */
    public void zerarSistema() {
        usuarios.clear();
        sessoes.clear();
        salvarEstado();
    }
    /**
     * Salva o estado atual do sistema em um arquivo.
     */
    public void salvarEstado() {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(DATA_FILE))) {
            out.writeObject(usuarios);
            out.writeObject(sessoes);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Carrega o estado salvo do sistema, se existir.
     */
    @SuppressWarnings("unchecked")
    private void carregarEstado() {
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(DATA_FILE))) {
            usuarios = (Map<String, Usuario>) in.readObject();
            sessoes = (Map<String, Usuario>) in.readObject();
        } catch (IOException | ClassNotFoundException e) {
            // Se o arquivo n�o existir ou houver erro, inicializa os mapas vazios
            usuarios = new HashMap<>();
            sessoes = new HashMap<>();
        }
    }

    /**
     * Encerra o sistema e persiste os dados.
     */
    public void encerrarSistema() {
        salvarEstado();
    }

    /**
     * Retorna um atributo espec�fico do usu�rio.
     *
     * @param login    O login do usu�rio.
     * @param atributo O atributo desejado (ex: "nome", "senha").
     * @return O valor do atributo solicitado.
     * @throws UsuarioException Se o usu�rio n�o for encontrado ou o atributo n�o estiver preenchido.
     */
    public String getAtributoUsuario(String login, String atributo) throws UsuarioException {
        Usuario usuario = usuarios.get(login);
        if (usuario == null) {
            throw new UsuarioNaoCadastradoException();
        }

        switch (atributo) {
            case "nome":
                return usuario.getNome();
            case "senha":
                return usuario.getSenha();
            default:
                return usuario.getAtributo(atributo);
        }
    }

    /**
     * Cria um novo usu�rio no sistema.
     *
     * @param login O login do usu�rio.
     * @param senha A senha do usu�rio.
     * @param nome  O nome do usu�rio.
     * @throws UsuarioException Se o login j� estiver cadastrado.
     */
    public void criarUsuario(String login, String senha, String nome) throws UsuarioException {
        if (usuarios.containsKey(login)) {
            throw new ContaJaExisteNomeException();
        }
        usuarios.put(login, new Usuario(login, senha, nome));
    }

    /**
     * Abre uma sess�o para um usu�rio autenticado.
     *
     * @param login O login do usu�rio.
     * @param senha A senha do usu�rio.
     * @return O identificador da sess�o.
     * @throws UsuarioException Se o login ou senha forem inv�lidos.
     */
    public String abrirSessao(String login, String senha) throws UsuarioException {
        Usuario usuario = usuarios.get(login);
        if (usuario == null || !usuario.getSenha().equals(senha)) {
            throw new LoginSenhaInvalidosException();
        }

        String idSessao = UUID.randomUUID().toString(); // Melhor seguran�a
        sessoes.put(idSessao, usuario);
        return idSessao;
    }

    /**
     * Edita o perfil de um usu�rio.
     *
     * @param idSessao Identificador da sess�o do usu�rio.
     * @param atributo Nome do atributo a ser modificado.
     * @param valor    Novo valor do atributo.
     * @throws UsuarioException Se o usu�rio n�o for encontrado.
     */
    public void editarPerfil(String idSessao, String atributo, String valor) throws UsuarioException {
        Usuario usuario = sessoes.get(idSessao);
        if (usuario == null) {
            throw new UsuarioNaoCadastradoException();
        }
        usuario.setAtributo(atributo, valor);
    }

    /**
     * Verifica se dois usu�rios s�o amigos.
     *
     * @param login      Login do primeiro usu�rio.
     * @param loginAmigo Login do poss�vel amigo.
     * @return true se forem amigos, false caso contr�rio.
     */
    public boolean ehAmigo(String login, String loginAmigo) {
        Usuario usuario = usuarios.get(login);
        return usuario != null && usuario.getAmigos().contains(loginAmigo);
    }

    /**
     * Adiciona um amigo para um usu�rio.
     *
     * @param idSessao   Identificador da sess�o do usu�rio.
     * @param loginAmigo Login do amigo a ser adicionado.
     * @throws UsuarioException Se o usu�rio ou amigo n�o existirem, ou se j� forem amigos.
     */
    public void adicionarAmigo(String idSessao, String loginAmigo) throws UsuarioException {
        Usuario usuario = sessoes.get(idSessao);
        Usuario amigo = usuarios.get(loginAmigo);

        if (usuario == null || amigo == null) {
            throw new UsuarioNaoCadastradoException();
        }

        if (loginAmigo.equals(usuario.getLogin())) {
            throw new UsuarioNaoPodeAdicionarASiMesmoException();
        }

        if (usuario.getAmigos().contains(loginAmigo)) {
            throw new UsuarioJahehAmigoException();
        }

        usuario.enviarConvite(loginAmigo);
        amigo.receberConvite(usuario.getLogin());
    }

    /**
     * Retorna a lista de amigos de um usu�rio.
     *
     * @param login Login do usu�rio.
     * @return String formatada com a lista de amigos.
     */
    public String getAmigos(String login) {
        Usuario usuario = usuarios.get(login);
        return usuario == null ? "{}" : "{" + String.join(",", usuario.getAmigos()) + "}";
    }

    /**
     * Envia um recado para outro usu�rio.
     *
     * @param idSessao         Identificador da sess�o do remetente.
     * @param loginDestinatario Login do destinat�rio.
     * @param mensagem         Mensagem a ser enviada.
     * @throws UsuarioException Se o remetente ou destinat�rio n�o existirem.
     */
    public void enviarRecado(String idSessao, String loginDestinatario, String mensagem) throws UsuarioException {
        Usuario remetente = sessoes.get(idSessao);
        Usuario destinatario = usuarios.get(loginDestinatario);

        if (remetente == null || destinatario == null) {
            throw new UsuarioNaoCadastradoException();
        }

        if (remetente.getLogin().equals(loginDestinatario)) {
            throw new UsuarioNaoPodeEnviarRecadoParaSiMesmoException();
        }

        destinatario.receberMensagem(remetente.getLogin(), mensagem);
    }

    /**
     * L� os recados de um usu�rio.
     *
     * @param idSessao Identificador da sess�o do usu�rio.
     * @return Recados do usu�rio.
     * @throws NaoHaRecadosException Se o usu�rio n�o tiver recados.
     */
    public String lerRecado(String idSessao) throws UsuarioException {
        Usuario usuario = sessoes.get(idSessao);
        if (usuario == null) {
            throw new NaoHaRecadosException();
        }
        return usuario.getRecados();
    }
}
