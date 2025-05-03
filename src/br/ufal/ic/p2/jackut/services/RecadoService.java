package br.ufal.ic.p2.jackut.services;

import br.ufal.ic.p2.jackut.models.entities.Recado;
import br.ufal.ic.p2.jackut.models.entities.Sessao;
import br.ufal.ic.p2.jackut.models.entities.Usuario;
import br.ufal.ic.p2.jackut.models.exceptions.NaoHaRecadosException;
import br.ufal.ic.p2.jackut.models.exceptions.RelacionamentoExceptions.UsuarioEhSeuInimigoException;
import br.ufal.ic.p2.jackut.models.exceptions.UsuarioException;
import br.ufal.ic.p2.jackut.models.exceptions.UsuarioNaoCadastradoException;
import br.ufal.ic.p2.jackut.models.exceptions.UsuarioNaoPodeEnviarRecadoParaSiMesmoException;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;

public class RecadoService {
    private Map<String, Recado> recados = new HashMap<>(); // Usando Map para associar login à Recado
    private UsuarioService usuarioService;
    private SessaoService sessaoService;
    private RelacionamentoService relacionamentoService;

    public RecadoService(UsuarioService usuarioService, SessaoService sessaoService) {
        this.usuarioService = usuarioService;
        this.sessaoService = sessaoService;
        this.recados = loadRecadosFromFile();
    }

    // em RecadoService
    public void setRelacionamentoService(RelacionamentoService relacionamentoService) {
        this.relacionamentoService = relacionamentoService;
    }

    public void removerRecado(String login) {
        recados.clear();
        saveRecadosToFile();
    }


    public void enviarRecado(String idSessao, String destinatario, String recado) throws UsuarioException, UsuarioEhSeuInimigoException {
        Usuario remetente = sessaoService.getUsuarioPorSessao(idSessao);
        Usuario recebedor = usuarioService.getUsuario(destinatario); // Deve lançar UsuarioNaoCadastradoException se não existir

        if (relacionamentoService.ehInimigo(idSessao, destinatario))
        {
            throw new UsuarioEhSeuInimigoException(usuarioService.getAtributoUsuario(destinatario, "nome"));
        }

        if (remetente == recebedor)
        {
            throw new UsuarioNaoPodeEnviarRecadoParaSiMesmoException();
        }

        Recado inboxDestinatario = recados.get(destinatario);
        if (inboxDestinatario == null) {
            inboxDestinatario = new Recado();
            recados.put(destinatario, inboxDestinatario);
        }

        inboxDestinatario.adicionarRecado(recado);
        saveRecadosToFile();
    }

    public void enviarRecadoJackut(String destinatario, String recado) throws UsuarioNaoCadastradoException {
        Usuario recebedor = usuarioService.getUsuario(destinatario); // Deve lançar UsuarioNaoCadastradoException se não existir

        Recado inboxDestinatario = recados.get(destinatario);
        if (inboxDestinatario == null) {
            inboxDestinatario = new Recado();
            recados.put(destinatario, inboxDestinatario);
        }

        inboxDestinatario.adicionarRecado(recado);
        saveRecadosToFile();
    }

    public String lerRecado(String idSessao) throws UsuarioException {
        Usuario usuario = sessaoService.getUsuarioPorSessao(idSessao);
        String login = usuario.getLogin();

        Recado inbox = recados.get(login);

        if (inbox == null) {
            throw new NaoHaRecadosException();
        }

        String mensagem = inbox.lerRecado();

        if (mensagem == null) {
            throw new NaoHaRecadosException();
        }

        return mensagem;
    }

    public void saveRecadosToFile() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("recados.ser"))) {
            oos.writeObject(recados); // Salva o mapa inteiro
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("unchecked")
    private Map<String, Recado> loadRecadosFromFile() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream("recados.ser"))) {
            return (Map<String, Recado>) ois.readObject(); // Carrega o mapa de comunidades
        } catch (Exception e) {
            return new HashMap<>(); // Retorna um mapa vazio se não houver dados
        }
    }

    public void zerar() {
        recados.clear();
        saveRecadosToFile();
    }


}
