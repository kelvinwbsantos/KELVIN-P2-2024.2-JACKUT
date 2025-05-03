package br.ufal.ic.p2.jackut.services;

import br.ufal.ic.p2.jackut.models.entities.Usuario;
import br.ufal.ic.p2.jackut.models.exceptions.RelacionamentoExceptions.*;
import br.ufal.ic.p2.jackut.models.exceptions.UsuarioException;
import br.ufal.ic.p2.jackut.models.exceptions.UsuarioNaoCadastradoException;

import java.io.*;
import java.util.*;

/**
 * Servi�o respons�vel pela gest�o dos relacionamentos entre usu�rios: �dolos, f�s, paqueras e inimigos.
 */
public class RelacionamentoService {

    /** Mapa que associa cada usu�rio aos seus �dolos. */
    private Map<String, Set<String>> idolos = new HashMap<>();

    /** Mapa que associa cada �dolo aos seus f�s. */
    private Map<String, Set<String>> fas = new HashMap<>();

    /** Mapa que associa cada usu�rio �s suas paqueras. */
    private Map<String, Set<String>> paqueras = new HashMap<>();

    /** Mapa que associa cada usu�rio aos seus inimigos. */
    private Map<String, Set<String>> inimigos = new HashMap<>();

    private UsuarioService usuarioService;
    private SessaoService sessaoService;
    private RecadoService recadoService;

    /**
     * Construtor do servi�o de relacionamentos.
     *
     * @param usuarioService Servi�o de gerenciamento de usu�rios.
     * @param sessaoService Servi�o de gerenciamento de sess�es.
     */
    public RelacionamentoService(UsuarioService usuarioService, SessaoService sessaoService) {
        this.usuarioService = usuarioService;
        this.sessaoService = sessaoService;
        carregarRelacionamentos();
    }

    /**
     * Define o servi�o de recados para envio de mensagens entre usu�rios.
     *
     * @param recadoService Servi�o de gerenciamento de recados.
     */
    public void setRecadoService(RecadoService recadoService) {
        this.recadoService = recadoService;
    }

    /**
     * Salva os relacionamentos no arquivo "relacionamentos.ser".
     */
    public void saveRelacionamentoToFile() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("relacionamentos.ser"))) {
            oos.writeObject(idolos);
            oos.writeObject(fas);
            oos.writeObject(paqueras);
            oos.writeObject(inimigos);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Carrega os relacionamentos dos usu�rios a partir do arquivo "relacionamentos.ser".
     */
    @SuppressWarnings("unchecked")
    private void carregarRelacionamentos() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream("relacionamentos.ser"))) {
            idolos = (Map<String, Set<String>>) ois.readObject();
            fas = (Map<String, Set<String>>) ois.readObject();
            paqueras = (Map<String, Set<String>>) ois.readObject();
            inimigos = (Map<String, Set<String>>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * Limpa todos os relacionamentos e os salva no arquivo.
     */
    public void zerar() {
        idolos.clear();
        fas.clear();
        paqueras.clear();
        inimigos.clear();
        saveRelacionamentoToFile();
    }

    /**
     * Verifica se o usu�rio � f� de um �dolo.
     *
     * @param login Login do usu�rio.
     * @param idolo Login do �dolo.
     * @return true se o usu�rio for f� do �dolo, caso contr�rio, false.
     */
    public boolean ehFa(String login, String idolo) {
        return idolos.containsKey(login) && idolos.get(login).contains(idolo);
    }

    /**
     * Adiciona um �dolo para o usu�rio logado.
     *
     * @param idSessao Sess�o do usu�rio logado.
     * @param idolo Login do �dolo a ser adicionado.
     * @throws UsuarioException Se houver algum erro relacionado ao usu�rio, como tentar ser f� de si mesmo.
     * @throws UsuarioEhSeuInimigoException Se o usu�rio tentar adicionar um inimigo como �dolo.
     */
    public void adicionarIdolo(String idSessao, String idolo) throws UsuarioException, UsuarioEhSeuInimigoException {
        Usuario usuario = sessaoService.getUsuarioPorSessao(idSessao);
        String login = usuario.getLogin();

        if (this.ehInimigo(idSessao, idolo)) {
            throw new UsuarioEhSeuInimigoException(usuarioService.getAtributoUsuario(idolo, "nome"));
        }

        if (login.equals(idolo)) {
            throw new UsuarioException("Usu�rio n�o pode ser f� de si mesmo.");
        }

        if (usuarioService.getUsuario(idolo) == null) {
            throw new UsuarioException("Usu�rio n�o cadastrado.");
        }

        // Evitar duplica��o
        idolos.putIfAbsent(login, new HashSet<>());
        if (!idolos.get(login).add(idolo)) {
            throw new UsuarioException("Usu�rio j� est� adicionado como �dolo.");
        }

        // Adiciona o f� na lista de f�s do �dolo
        fas.putIfAbsent(idolo, new HashSet<>());
        fas.get(idolo).add(login);
    }

    /**
     * Retorna os f�s de um �dolo.
     *
     * @param login Login do �dolo.
     * @return Conjunto de f�s do �dolo.
     */
    public Set<String> getFas(String login) {
        return fas.getOrDefault(login, new HashSet<>());
    }

    /**
     * Verifica se dois usu�rios est�o na categoria de "paquera".
     *
     * @param idSessao Sess�o do usu�rio logado.
     * @param paquera Login do usu�rio que o logado est� interessado.
     * @return true se o usu�rio est� na lista de paqueras, caso contr�rio, false.
     * @throws UsuarioException Se houver um erro no usu�rio logado.
     */
    public boolean ehPaquera(String idSessao, String paquera) throws UsuarioException {
        Usuario usuario = sessaoService.getUsuarioPorSessao(idSessao);
        String login = usuario.getLogin();

        return paqueras.containsKey(login) && paqueras.get(login).contains(paquera);
    }

    /**
     * Adiciona um usu�rio � lista de paqueras de outro usu�rio.
     *
     * @param idSessao Sess�o do usu�rio logado.
     * @param paquera Login do usu�rio a ser adicionado como paquera.
     * @throws UsuarioException Se houver erro relacionado ao usu�rio, como ser paquera de si mesmo.
     * @throws UsuarioJaEstaAdicionadoComoPaqueraException Se o usu�rio j� estiver na lista de paqueras.
     * @throws UsuarioNaoPodeSerPaqueraDeSiMesmoException Se o usu�rio tentar ser paquera de si mesmo.
     * @throws UsuarioEhSeuInimigoException Se o usu�rio tentar adicionar um inimigo como paquera.
     */
    public void adicionarPaquera(String idSessao, String paquera) throws UsuarioException, UsuarioJaEstaAdicionadoComoPaqueraException, UsuarioNaoPodeSerPaqueraDeSiMesmoException, UsuarioEhSeuInimigoException {
        Usuario usuario = sessaoService.getUsuarioPorSessao(idSessao);
        String login = usuario.getLogin();

        if (this.ehInimigo(idSessao, paquera)) {
            throw new UsuarioEhSeuInimigoException(usuarioService.getAtributoUsuario(paquera, "nome"));
        }

        if (paquera.equals(login)) {
            throw new UsuarioNaoPodeSerPaqueraDeSiMesmoException();
        }

        paqueras.putIfAbsent(login, new HashSet<>());
        Usuario usuarioPaquera = usuarioService.getUsuario(paquera);

        if (usuarioPaquera == null) {
            throw new UsuarioNaoCadastradoException();
        }

        if (paqueras.get(login).contains(paquera)) {
            throw new UsuarioJaEstaAdicionadoComoPaqueraException();
        }

        paqueras.get(login).add(paquera);
        paqueras.putIfAbsent(paquera, new HashSet<>());

        if (paqueras.get(paquera).contains(login)) {
            recadoService.enviarRecadoJackut(paquera, usuarioService.getAtributoUsuario(login, "nome") + " � seu paquera - Recado do Jackut.");
            recadoService.enviarRecadoJackut(login, usuarioService.getAtributoUsuario(paquera, "nome") + " � seu paquera - Recado do Jackut.");
        }
    }

    /**
     * Retorna a lista de paqueras de um usu�rio.
     *
     * @param idSessao Sess�o do usu�rio logado.
     * @return Conjunto de paqueras do usu�rio.
     * @throws UsuarioException Se houver erro com o usu�rio logado.
     */
    public Set<String> getPaqueras(String idSessao) throws UsuarioException {
        Usuario usuario = sessaoService.getUsuarioPorSessao(idSessao);
        String login = usuario.getLogin();

        return paqueras.getOrDefault(login, new HashSet<>());
    }

    /**
     * Adiciona um inimigo a um usu�rio.
     *
     * @param idSessao Sess�o do usu�rio logado.
     * @param inimigo Login do inimigo a ser adicionado.
     * @throws UsuarioException Se houver erro relacionado ao usu�rio, como adicionar inimigo de si mesmo.
     * @throws UsuarioJaEstaAdicionadoComoInimigoException Se o usu�rio j� � inimigo.
     * @throws UsuarioNaoPodeSerInimigoDeSiMesmoException Se o usu�rio tentar adicionar a si mesmo como inimigo.
     */
    public void adicionarInimigo(String idSessao, String inimigo) throws UsuarioException, UsuarioJaEstaAdicionadoComoInimigoException, UsuarioNaoPodeSerInimigoDeSiMesmoException {
        Usuario usuario = sessaoService.getUsuarioPorSessao(idSessao);
        String login = usuario.getLogin();

        Usuario inimigoUsuario = usuarioService.getUsuario(inimigo);

        if (inimigoUsuario == null) {
            throw new UsuarioNaoCadastradoException();
        }

        inimigos.putIfAbsent(login, new HashSet<>());
        Set<String> listaInimizade = inimigos.get(login);

        inimigos.putIfAbsent(inimigo, new HashSet<>());
        Set<String> listaInimizadeInimigo = inimigos.get(inimigo);

        if (ehInimigo(idSessao, inimigo)) {
            throw new UsuarioJaEstaAdicionadoComoInimigoException();
        }

        if (inimigo.equals(login)) {
            throw new UsuarioNaoPodeSerInimigoDeSiMesmoException();
        }

        listaInimizade.add(inimigo);
        listaInimizadeInimigo.add(login);
    }

    /**
     * Verifica se dois usu�rios s�o inimigos.
     *
     * @param idSessao Sess�o do usu�rio logado.
     * @param inimigo Login do poss�vel inimigo.
     * @return true se forem inimigos, caso contr�rio, false.
     * @throws UsuarioException Se houver erro com a sess�o do usu�rio.
     */
    public boolean ehInimigo(String idSessao, String inimigo) throws UsuarioException {
        Usuario usuario = sessaoService.getUsuarioPorSessao(idSessao);
        String login = usuario.getLogin();

        return inimigos.containsKey(login) && inimigos.get(login).contains(inimigo);
    }
}
