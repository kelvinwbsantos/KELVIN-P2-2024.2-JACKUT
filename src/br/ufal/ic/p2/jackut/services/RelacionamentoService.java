package br.ufal.ic.p2.jackut.services;

import br.ufal.ic.p2.jackut.models.entities.Usuario;
import br.ufal.ic.p2.jackut.models.exceptions.RelacionamentoExceptions.*;
import br.ufal.ic.p2.jackut.models.exceptions.UsuarioException;
import br.ufal.ic.p2.jackut.models.exceptions.UsuarioNaoCadastradoException;

import java.io.*;
import java.util.*;

/**
 * Serviço responsável pela gestão dos relacionamentos entre usuários: ídolos, fãs, paqueras e inimigos.
 */
public class RelacionamentoService {

    /** Mapa que associa cada usuário aos seus ídolos. */
    private Map<String, Set<String>> idolos = new HashMap<>();

    /** Mapa que associa cada ídolo aos seus fãs. */
    private Map<String, Set<String>> fas = new HashMap<>();

    /** Mapa que associa cada usuário às suas paqueras. */
    private Map<String, Set<String>> paqueras = new HashMap<>();

    /** Mapa que associa cada usuário aos seus inimigos. */
    private Map<String, Set<String>> inimigos = new HashMap<>();

    private UsuarioService usuarioService;
    private SessaoService sessaoService;
    private RecadoService recadoService;

    /**
     * Construtor do serviço de relacionamentos.
     *
     * @param usuarioService Serviço de gerenciamento de usuários.
     * @param sessaoService Serviço de gerenciamento de sessões.
     */
    public RelacionamentoService(UsuarioService usuarioService, SessaoService sessaoService) {
        this.usuarioService = usuarioService;
        this.sessaoService = sessaoService;
        carregarRelacionamentos();
    }

    /**
     * Define o serviço de recados para envio de mensagens entre usuários.
     *
     * @param recadoService Serviço de gerenciamento de recados.
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
     * Carrega os relacionamentos dos usuários a partir do arquivo "relacionamentos.ser".
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
     * Verifica se o usuário é fã de um ídolo.
     *
     * @param login Login do usuário.
     * @param idolo Login do ídolo.
     * @return true se o usuário for fã do ídolo, caso contrário, false.
     */
    public boolean ehFa(String login, String idolo) {
        return idolos.containsKey(login) && idolos.get(login).contains(idolo);
    }

    /**
     * Adiciona um ídolo para o usuário logado.
     *
     * @param idSessao Sessão do usuário logado.
     * @param idolo Login do ídolo a ser adicionado.
     * @throws UsuarioException Se houver algum erro relacionado ao usuário, como tentar ser fã de si mesmo.
     * @throws UsuarioEhSeuInimigoException Se o usuário tentar adicionar um inimigo como ídolo.
     */
    public void adicionarIdolo(String idSessao, String idolo) throws UsuarioException, UsuarioEhSeuInimigoException {
        Usuario usuario = sessaoService.getUsuarioPorSessao(idSessao);
        String login = usuario.getLogin();

        if (this.ehInimigo(idSessao, idolo)) {
            throw new UsuarioEhSeuInimigoException(usuarioService.getAtributoUsuario(idolo, "nome"));
        }

        if (login.equals(idolo)) {
            throw new UsuarioException("Usuário não pode ser fã de si mesmo.");
        }

        if (usuarioService.getUsuario(idolo) == null) {
            throw new UsuarioException("Usuário não cadastrado.");
        }

        // Evitar duplicação
        idolos.putIfAbsent(login, new HashSet<>());
        if (!idolos.get(login).add(idolo)) {
            throw new UsuarioException("Usuário já está adicionado como ídolo.");
        }

        // Adiciona o fã na lista de fãs do ídolo
        fas.putIfAbsent(idolo, new HashSet<>());
        fas.get(idolo).add(login);
    }

    /**
     * Retorna os fãs de um ídolo.
     *
     * @param login Login do ídolo.
     * @return Conjunto de fãs do ídolo.
     */
    public Set<String> getFas(String login) {
        return fas.getOrDefault(login, new HashSet<>());
    }

    /**
     * Verifica se dois usuários estão na categoria de "paquera".
     *
     * @param idSessao Sessão do usuário logado.
     * @param paquera Login do usuário que o logado está interessado.
     * @return true se o usuário está na lista de paqueras, caso contrário, false.
     * @throws UsuarioException Se houver um erro no usuário logado.
     */
    public boolean ehPaquera(String idSessao, String paquera) throws UsuarioException {
        Usuario usuario = sessaoService.getUsuarioPorSessao(idSessao);
        String login = usuario.getLogin();

        return paqueras.containsKey(login) && paqueras.get(login).contains(paquera);
    }

    /**
     * Adiciona um usuário à lista de paqueras de outro usuário.
     *
     * @param idSessao Sessão do usuário logado.
     * @param paquera Login do usuário a ser adicionado como paquera.
     * @throws UsuarioException Se houver erro relacionado ao usuário, como ser paquera de si mesmo.
     * @throws UsuarioJaEstaAdicionadoComoPaqueraException Se o usuário já estiver na lista de paqueras.
     * @throws UsuarioNaoPodeSerPaqueraDeSiMesmoException Se o usuário tentar ser paquera de si mesmo.
     * @throws UsuarioEhSeuInimigoException Se o usuário tentar adicionar um inimigo como paquera.
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
            recadoService.enviarRecadoJackut(paquera, usuarioService.getAtributoUsuario(login, "nome") + " é seu paquera - Recado do Jackut.");
            recadoService.enviarRecadoJackut(login, usuarioService.getAtributoUsuario(paquera, "nome") + " é seu paquera - Recado do Jackut.");
        }
    }

    /**
     * Retorna a lista de paqueras de um usuário.
     *
     * @param idSessao Sessão do usuário logado.
     * @return Conjunto de paqueras do usuário.
     * @throws UsuarioException Se houver erro com o usuário logado.
     */
    public Set<String> getPaqueras(String idSessao) throws UsuarioException {
        Usuario usuario = sessaoService.getUsuarioPorSessao(idSessao);
        String login = usuario.getLogin();

        return paqueras.getOrDefault(login, new HashSet<>());
    }

    /**
     * Adiciona um inimigo a um usuário.
     *
     * @param idSessao Sessão do usuário logado.
     * @param inimigo Login do inimigo a ser adicionado.
     * @throws UsuarioException Se houver erro relacionado ao usuário, como adicionar inimigo de si mesmo.
     * @throws UsuarioJaEstaAdicionadoComoInimigoException Se o usuário já é inimigo.
     * @throws UsuarioNaoPodeSerInimigoDeSiMesmoException Se o usuário tentar adicionar a si mesmo como inimigo.
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
     * Verifica se dois usuários são inimigos.
     *
     * @param idSessao Sessão do usuário logado.
     * @param inimigo Login do possível inimigo.
     * @return true se forem inimigos, caso contrário, false.
     * @throws UsuarioException Se houver erro com a sessão do usuário.
     */
    public boolean ehInimigo(String idSessao, String inimigo) throws UsuarioException {
        Usuario usuario = sessaoService.getUsuarioPorSessao(idSessao);
        String login = usuario.getLogin();

        return inimigos.containsKey(login) && inimigos.get(login).contains(inimigo);
    }
}
