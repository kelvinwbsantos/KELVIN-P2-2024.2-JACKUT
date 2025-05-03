package br.ufal.ic.p2.jackut.services;

import br.ufal.ic.p2.jackut.models.entities.Amizade;
import br.ufal.ic.p2.jackut.models.entities.Comunidade;
import br.ufal.ic.p2.jackut.models.entities.Usuario;
import br.ufal.ic.p2.jackut.models.exceptions.*;
import br.ufal.ic.p2.jackut.models.exceptions.RelacionamentoExceptions.UsuarioEhSeuInimigoException;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.*;

public class AmizadeService {
    private UsuarioService usuarioService;
    private SessaoService sessaoService;
    private RelacionamentoService relacionamentoService;

    public Map<String, Amizade> amizades = new LinkedHashMap<>(); // Usando Map para associar nome à comunidade

    public AmizadeService(UsuarioService usuarioService, SessaoService sessaoService, RelacionamentoService relacionamentoService) {
        this.usuarioService = usuarioService;
        this.sessaoService = sessaoService;
        this.relacionamentoService = relacionamentoService;
        this.amizades = loadAmizadesFromFile();
    }


    public void adicionarAmigo(String idSessao, String amigoLogin) throws UsuarioException, UsuarioEhSeuInimigoException {
        Usuario usuario = sessaoService.getUsuarioPorSessao(idSessao);
        Usuario amigo = usuarioService.getUsuario(amigoLogin);

        if (relacionamentoService.ehInimigo(idSessao, amigoLogin))
        {
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
            amizadeUsuario.getConvitesRecebidos().remove(amigoLogin);
            amizadeAmigo.getConvitesEnviados().remove(usuario.getLogin());

            amizadeUsuario.getAmigos().add(amigoLogin);
            amizadeAmigo.getAmigos().add(usuario.getLogin());
        } else {
            amizadeUsuario.getConvitesEnviados().add(amigoLogin);
            amizadeAmigo.getConvitesRecebidos().add(usuario.getLogin());
        }
    }


    public boolean ehAmigo(String login, String amigoLogin) throws UsuarioException {
        Usuario usuario = usuarioService.getUsuario(login);

        amizades.putIfAbsent(usuario.getLogin(), new Amizade());
        Amizade amizadeUsuario = amizades.get(usuario.getLogin());

        return amizadeUsuario.getAmigos().contains(amigoLogin);
    }


    public Set<String> getAmigos(String login) throws UsuarioException {
        Usuario usuario = usuarioService.getUsuario(login);

        amizades.putIfAbsent(usuario.getLogin(), new Amizade());
        Amizade amizadeUsuario = amizades.get(usuario.getLogin());


        // Retorna diretamente o Set de amigos.

        return amizadeUsuario.getAmigos();
    }

    public void saveAmizadesToFile() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("amizades.ser"))) {
            oos.writeObject(amizades); // Salva o mapa inteiro
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("unchecked")
    private Map<String, Amizade> loadAmizadesFromFile() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream("amizades.ser"))) {
            return (Map<String, Amizade>) ois.readObject(); // Carrega o mapa de comunidades
        } catch (Exception e) {
            return new LinkedHashMap<>(); // Retorna um mapa vazio se não houver dados
        }
    }

    public void zerar() {
        amizades.clear();
        saveAmizadesToFile();
    }



}
