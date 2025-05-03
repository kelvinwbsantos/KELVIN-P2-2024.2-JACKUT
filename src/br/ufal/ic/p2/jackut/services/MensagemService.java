package br.ufal.ic.p2.jackut.services;

import br.ufal.ic.p2.jackut.models.entities.Comunidade;
import br.ufal.ic.p2.jackut.models.entities.Mensagem;
import br.ufal.ic.p2.jackut.models.entities.Usuario;
import br.ufal.ic.p2.jackut.models.exceptions.ComunidadeNaoExisteException;
import br.ufal.ic.p2.jackut.models.exceptions.NaoHaMensagensException;
import br.ufal.ic.p2.jackut.models.exceptions.UsuarioException;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.*;

public class MensagemService {
    private ComunidadeService comunidadeService;
    private SessaoService sessaoService;

    private Map<String, Queue<Mensagem>> mensagens = new LinkedHashMap<>(); // login mensagem na comunidade

    public MensagemService(ComunidadeService comunidadeService, SessaoService sessaoService) {
        this.comunidadeService = comunidadeService;
        this.sessaoService = sessaoService;
        this.mensagens = loadMensagensFromFile();
    }

    public String lerMensagem(String idSessao) throws UsuarioException {
        Usuario usuario = sessaoService.getUsuarioPorSessao(idSessao);
        Queue<Mensagem> mensagensUsuario = mensagens.get(usuario.getLogin());

        if (mensagensUsuario == null || mensagensUsuario.isEmpty()) {
            throw new NaoHaMensagensException();
        }

        Mensagem mensagem = mensagensUsuario.poll();
        saveMensagensToFile();

        return mensagem.getMensagem();
    }

    public void enviarMensagem(String idSessao, String comunidade, String texto)
            throws UsuarioException, ComunidadeNaoExisteException {
        Usuario remetente = sessaoService.getUsuarioPorSessao(idSessao);
        Set<String> membros = comunidadeService.getMembrosComunidade(comunidade);

        Mensagem mensagem = new Mensagem(texto, remetente.getLogin(), comunidade);

        for (String membro : membros) {
            mensagens.computeIfAbsent(membro, k -> new LinkedList<>()).add(mensagem);
        }

        saveMensagensToFile();
    }

    public void saveMensagensToFile() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("mensagens.ser"))) {
            oos.writeObject(mensagens); // Salva o mapa inteiro
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("unchecked")
    private Map<String, Queue<Mensagem>> loadMensagensFromFile() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream("mensagens.ser"))) {
            return (Map<String, Queue<Mensagem>>) ois.readObject(); // Carrega o mapa de comunidades
        } catch (Exception e) {
            return new LinkedHashMap<>(); // Retorna um mapa vazio se não houver dados
        }
    }

    public void zerar() {
        mensagens.clear();
        saveMensagensToFile();
    }
}
