package br.ufal.ic.p2.jackut.services;

import br.ufal.ic.p2.jackut.models.entities.Usuario;
import br.ufal.ic.p2.jackut.models.exceptions.RelacionamentoExceptions.*;
import br.ufal.ic.p2.jackut.models.exceptions.UsuarioException;
import br.ufal.ic.p2.jackut.models.exceptions.UsuarioNaoCadastradoException;

import java.io.*;
import java.util.*;

public class RelacionamentoService {
    private Map<String, Set<String>> idolos = new HashMap<>();
    private Map<String, Set<String>> fas = new HashMap<>(); // idolo -> fãs

    private Map<String, Set<String>> paqueras = new HashMap<>();
    private Map<String, Set<String>> inimigos = new HashMap<>();

    private UsuarioService usuarioService;
    private SessaoService sessaoService;
    private RecadoService recadoService;

    public RelacionamentoService(UsuarioService usuarioService, SessaoService sessaoService) {
        this.usuarioService = usuarioService;
        this.sessaoService = sessaoService;
        carregarRelacionamentos();
    }

    // em RelacionamentoService
    public void setRecadoService(RecadoService recadoService) {
        this.recadoService = recadoService;
    }



    public void saveRelacionamentoToFile() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream( "relacionamentos.ser"))) {
            oos.writeObject(idolos);
            oos.writeObject(fas);
            oos.writeObject(paqueras);
            oos.writeObject(inimigos);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Método para carregar os relacionamentos do arquivo
    @SuppressWarnings("unchecked")
    private void carregarRelacionamentos() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream( "relacionamentos.ser"))) {
            idolos = (Map<String, Set<String>>) ois.readObject();
            fas = (Map<String, Set<String>>) ois.readObject();
            paqueras = (Map<String, Set<String>>) ois.readObject();
            inimigos = (Map<String, Set<String>>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void zerar() {
        // Limpar todos os relacionamentos
        idolos.clear();
        fas.clear();
        paqueras.clear();
        inimigos.clear();

        // Salvar o estado vazio
        saveRelacionamentoToFile();
    }


    public boolean ehFa(String login, String idolo) {
        if (idolos.containsKey(login)) {
            return idolos.get(login).contains(idolo);
        }
        return false;
    }

    public void adicionarIdolo(String idSessao, String idolo) throws UsuarioException, UsuarioEhSeuInimigoException {
        Usuario usuario = sessaoService.getUsuarioPorSessao(idSessao);
        String login = usuario.getLogin();

        if (this.ehInimigo(idSessao, idolo))
        {
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



    public Set<String> getFas(String login) {
        return fas.getOrDefault(login, new HashSet<>());
    }

    public boolean ehPaquera(String idSessao, String paquera) throws UsuarioException {
        Usuario usuario = sessaoService.getUsuarioPorSessao(idSessao);
        String login = usuario.getLogin();

        if (paqueras.containsKey(login)) {
            return paqueras.get(login).contains(paquera);
        }
        return false;
    }

    public void adicionarPaquera(String idSessao, String paquera) throws UsuarioException, UsuarioJaEstaAdicionadoComoPaqueraException, UsuarioNaoPodeSerPaqueraDeSiMesmoException, UsuarioEhSeuInimigoException {
        Usuario usuario = sessaoService.getUsuarioPorSessao(idSessao);
        String login = usuario.getLogin();

        if (this.ehInimigo(idSessao, paquera))
        {
            throw new UsuarioEhSeuInimigoException(usuarioService.getAtributoUsuario(paquera, "nome"));
        }

        if (paquera.equals(login))
        {
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
            recadoService.enviarRecadoJackut(paquera, usuarioService.getAtributoUsuario(login, "nome")+" é seu paquera - Recado do Jackut.");
            recadoService.enviarRecadoJackut(login, usuarioService.getAtributoUsuario(paquera, "nome")+" é seu paquera - Recado do Jackut.");
        }
    }

    public Set<String> getPaqueras(String idSessao) throws UsuarioException {
        Usuario usuario = sessaoService.getUsuarioPorSessao(idSessao);
        String login = usuario.getLogin();

        return paqueras.getOrDefault(login, new HashSet<>());
    }

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

        if (ehInimigo(idSessao, inimigo))
        {
            throw new UsuarioJaEstaAdicionadoComoInimigoException();
        }



        if (inimigo.equals(login)) {
            throw new UsuarioNaoPodeSerInimigoDeSiMesmoException();
        }

        if (!listaInimizade.contains(inimigo) || !listaInimizadeInimigo.contains(login)) {
            listaInimizade.add(inimigo);
            listaInimizadeInimigo.add(login);
        }
    }

    public boolean ehInimigo(String idSessao, String inimigo) throws UsuarioException {
        Usuario usuario = sessaoService.getUsuarioPorSessao(idSessao);
        String login = usuario.getLogin();

        if (inimigos.containsKey(login)) {
            return inimigos.get(login).contains(inimigo);
        }
        return false;
    }



}
