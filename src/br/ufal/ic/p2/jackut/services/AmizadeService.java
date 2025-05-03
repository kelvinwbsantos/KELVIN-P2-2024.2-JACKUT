package br.ufal.ic.p2.jackut.services;

import br.ufal.ic.p2.jackut.models.entities.Amizade;
import br.ufal.ic.p2.jackut.models.entities.Usuario;
import br.ufal.ic.p2.jackut.models.exceptions.*;
import br.ufal.ic.p2.jackut.models.exceptions.RelacionamentoExceptions.UsuarioEhSeuInimigoException;

import java.io.*;
import java.util.*;

/**
 * Servi�o respons�vel pela l�gica de amizades no sistema Jackut.
 * Permite adicionar amigos, verificar amizades e persistir dados.
 */
public class AmizadeService {

    /** Servi�o de usu�rios utilizado para acessar dados dos usu�rios. */
    private UsuarioService usuarioService;

    /** Servi�o de sess�o utilizado para autenticar a��es com base na sess�o. */
    private SessaoService sessaoService;

    /** Servi�o de relacionamentos utilizado para verificar inimigos. */
    private RelacionamentoService relacionamentoService;

    /** Mapa que armazena as amizades associadas a cada usu�rio. */
    public Map<String, Amizade> amizades = new LinkedHashMap<>();

    /**
     * Construtor da classe {@code AmizadeService}.
     *
     * @param usuarioService Servi�o de usu�rios.
     * @param sessaoService Servi�o de sess�es.
     * @param relacionamentoService Servi�o de relacionamentos (amizades/inimizades).
     */
    public AmizadeService(UsuarioService usuarioService, SessaoService sessaoService, RelacionamentoService relacionamentoService) {
        this.usuarioService = usuarioService;
        this.sessaoService = sessaoService;
        this.relacionamentoService = relacionamentoService;
        this.amizades = loadAmizadesFromFile();
    }

    /**
     * Tenta adicionar um amigo. Caso haja convite pendente de outro usu�rio, a amizade � confirmada.
     *
     * @param idSessao ID da sess�o do usu�rio solicitante.
     * @param amigoLogin Login do usu�rio a ser adicionado como amigo.
     * @throws UsuarioException Se houver problemas com os usu�rios.
     * @throws UsuarioEhSeuInimigoException Se o usu�rio alvo for inimigo do solicitante.
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
     * Verifica se dois usu�rios s�o amigos.
     *
     * @param login Login do usu�rio.
     * @param amigoLogin Login do poss�vel amigo.
     * @return {@code true} se forem amigos, {@code false} caso contr�rio.
     * @throws UsuarioException Se o usu�rio n�o existir.
     */
    public boolean ehAmigo(String login, String amigoLogin) throws UsuarioException {
        Usuario usuario = usuarioService.getUsuario(login);
        amizades.putIfAbsent(usuario.getLogin(), new Amizade());
        return amizades.get(usuario.getLogin()).getAmigos().contains(amigoLogin);
    }

    /**
     * Retorna o conjunto de logins dos amigos do usu�rio.
     *
     * @param login Login do usu�rio.
     * @return Um {@link Set} contendo os logins dos amigos.
     * @throws UsuarioException Se o usu�rio n�o existir.
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
