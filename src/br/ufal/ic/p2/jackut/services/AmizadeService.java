package br.ufal.ic.p2.jackut.services;

import br.ufal.ic.p2.jackut.models.entities.Amizade;
import br.ufal.ic.p2.jackut.models.entities.Usuario;
import br.ufal.ic.p2.jackut.models.exceptions.*;
import br.ufal.ic.p2.jackut.models.exceptions.RelacionamentoExceptions.UsuarioEhSeuInimigoException;

import java.io.*;
import java.util.*;

/**
 * Serviço responsável pela lógica de amizades no sistema Jackut.
 * Permite adicionar amigos, verificar amizades e persistir dados.
 */
public class AmizadeService {

    /** Serviço de usuários utilizado para acessar dados dos usuários. */
    private UsuarioService usuarioService;

    /** Serviço de sessão utilizado para autenticar ações com base na sessão. */
    private SessaoService sessaoService;

    /** Serviço de relacionamentos utilizado para verificar inimigos. */
    private RelacionamentoService relacionamentoService;

    /** Mapa que armazena as amizades associadas a cada usuário. */
    public Map<String, Amizade> amizades = new LinkedHashMap<>();

    /**
     * Construtor da classe {@code AmizadeService}.
     *
     * @param usuarioService Serviço de usuários.
     * @param sessaoService Serviço de sessões.
     * @param relacionamentoService Serviço de relacionamentos (amizades/inimizades).
     */
    public AmizadeService(UsuarioService usuarioService, SessaoService sessaoService, RelacionamentoService relacionamentoService) {
        this.usuarioService = usuarioService;
        this.sessaoService = sessaoService;
        this.relacionamentoService = relacionamentoService;
        this.amizades = loadAmizadesFromFile();
    }

    /**
     * Tenta adicionar um amigo. Caso haja convite pendente de outro usuário, a amizade é confirmada.
     *
     * @param idSessao ID da sessão do usuário solicitante.
     * @param amigoLogin Login do usuário a ser adicionado como amigo.
     * @throws UsuarioException Se houver problemas com os usuários.
     * @throws UsuarioEhSeuInimigoException Se o usuário alvo for inimigo do solicitante.
     */
    public void adicionarAmigo(String idSessao, String amigoLogin) throws UsuarioException, UsuarioEhSeuInimigoException {
        Usuario usuario = sessaoService.getUsuarioPorSessao(idSessao);
        Usuario amigo = usuarioService.getUsuario(amigoLogin);

        if (relacionamentoService.ehInimigo(idSessao, amigoLogin)) {
            throw new UsuarioEhSeuInimigoException(usuarioService.getAtributoUsuario(amigoLogin, "nome"));
        }

        if (amigo == null) {
            throw new UsuarioNaoCadastradoException();
        }

        if (usuario.getLogin().equals(amigoLogin)) {
            throw new UsuarioNaoPodeAdicionarASiMesmoException();
        }

        amizades.putIfAbsent(usuario.getLogin(), new Amizade());
        amizades.putIfAbsent(amigoLogin, new Amizade());

        Amizade amizadeUsuario = amizades.get(usuario.getLogin());
        Amizade amizadeAmigo = amizades.get(amigoLogin);

        if (amizadeUsuario.getAmigos().contains(amigoLogin)) {
            throw new UsuarioJahehAmigoException();
        }

        if (amizadeUsuario.getConvitesEnviados().contains(amigoLogin)) {
            throw new EsperandoAceitacaoException();
        }

        if (amizadeUsuario.getConvitesRecebidos().contains(amigoLogin)) {
            // Aceita amizade
            amizadeUsuario.getConvitesRecebidos().remove(amigoLogin);
            amizadeAmigo.getConvitesEnviados().remove(usuario.getLogin());
            amizadeUsuario.getAmigos().add(amigoLogin);
            amizadeAmigo.getAmigos().add(usuario.getLogin());
        } else {
            // Envia convite
            amizadeUsuario.getConvitesEnviados().add(amigoLogin);
            amizadeAmigo.getConvitesRecebidos().add(usuario.getLogin());
        }
    }

    /**
     * Verifica se dois usuários são amigos.
     *
     * @param login Login do usuário.
     * @param amigoLogin Login do possível amigo.
     * @return {@code true} se forem amigos, {@code false} caso contrário.
     * @throws UsuarioException Se o usuário não existir.
     */
    public boolean ehAmigo(String login, String amigoLogin) throws UsuarioException {
        Usuario usuario = usuarioService.getUsuario(login);
        amizades.putIfAbsent(usuario.getLogin(), new Amizade());
        return amizades.get(usuario.getLogin()).getAmigos().contains(amigoLogin);
    }

    /**
     * Retorna o conjunto de logins dos amigos do usuário.
     *
     * @param login Login do usuário.
     * @return Um {@link Set} contendo os logins dos amigos.
     * @throws UsuarioException Se o usuário não existir.
     */
    public Set<String> getAmigos(String login) throws UsuarioException {
        Usuario usuario = usuarioService.getUsuario(login);
        amizades.putIfAbsent(usuario.getLogin(), new Amizade());
        return amizades.get(usuario.getLogin()).getAmigos();
    }

    /**
     * Salva o estado atual das amizades no arquivo "amizades.ser".
     */
    public void saveAmizadesToFile() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("amizades.ser"))) {
            oos.writeObject(amizades);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Carrega o estado das amizades a partir do arquivo "amizades.ser".
     *
     * @return Um {@link Map} com os dados de amizade carregados, ou um mapa vazio em caso de falha.
     */
    @SuppressWarnings("unchecked")
    private Map<String, Amizade> loadAmizadesFromFile() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream("amizades.ser"))) {
            return (Map<String, Amizade>) ois.readObject();
        } catch (Exception e) {
            return new LinkedHashMap<>();
        }
    }

    /**
     * Limpa todas as amizades registradas e salva o estado vazio no arquivo.
     */
    public void zerar() {
        amizades.clear();
        saveAmizadesToFile();
    }
}
