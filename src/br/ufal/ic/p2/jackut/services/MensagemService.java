package br.ufal.ic.p2.jackut.services;

import br.ufal.ic.p2.jackut.models.entities.Comunidade;
import br.ufal.ic.p2.jackut.models.entities.Mensagem;
import br.ufal.ic.p2.jackut.models.entities.Usuario;
import br.ufal.ic.p2.jackut.models.exceptions.ComunidadeNaoExisteException;
import br.ufal.ic.p2.jackut.models.exceptions.NaoHaMensagensException;
import br.ufal.ic.p2.jackut.models.exceptions.UsuarioException;

import java.io.*;
import java.util.*;

/**
 * Servi�o respons�vel pelo envio, leitura e persist�ncia de mensagens trocadas entre membros de comunidades.
 */
public class MensagemService {

    /** Servi�o que gerencia comunidades e seus membros. */
    private ComunidadeService comunidadeService;

    /** Servi�o que gerencia sess�es e autentica��o de usu�rios. */
    private SessaoService sessaoService;

    /** Mapa que associa cada usu�rio a uma fila de mensagens recebidas. */
    private Map<String, Queue<Mensagem>> mensagens = new LinkedHashMap<>();

    /**
     * Construtor da classe {@code MensagemService}.
     *
     * @param comunidadeService Servi�o de gerenciamento de comunidades.
     * @param sessaoService Servi�o de gerenciamento de sess�es.
     */
    public MensagemService(ComunidadeService comunidadeService, SessaoService sessaoService) {
        this.comunidadeService = comunidadeService;
        this.sessaoService = sessaoService;
        this.mensagens = loadMensagensFromFile();
    }

    /**
     * L� a pr�xima mensagem dispon�vel para o usu�rio autenticado.
     * A mensagem � removida da fila ap�s leitura.
     *
     * @param idSessao ID da sess�o do usu�rio.
     * @return O texto da mensagem lida.
     * @throws UsuarioException Se a sess�o for inv�lida.
     * @throws NaoHaMensagensException Se n�o houver mensagens para o usu�rio.
     */
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

    /**
     * Envia uma mensagem para todos os membros de uma comunidade.
     *
     * @param idSessao ID da sess�o do remetente.
     * @param comunidade Nome da comunidade de destino.
     * @param texto Conte�do da mensagem.
     * @throws UsuarioException Se a sess�o for inv�lida.
     * @throws ComunidadeNaoExisteException Se a comunidade n�o existir.
     */
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

    /**
     * Salva o mapa de mensagens no arquivo "mensagens.ser".
     */
    public void saveMensagensToFile() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("mensagens.ser"))) {
            oos.writeObject(mensagens);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Carrega as mensagens a partir do arquivo "mensagens.ser".
     *
     * @return Mapa com as filas de mensagens por usu�rio.
     */
    @SuppressWarnings("unchecked")
    private Map<String, Queue<Mensagem>> loadMensagensFromFile() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream("mensagens.ser"))) {
            return (Map<String, Queue<Mensagem>>) ois.readObject();
        } catch (Exception e) {
            return new LinkedHashMap<>();
        }
    }

    /**
     * Remove todas as mensagens do sistema e salva o estado limpo.
     */
    public void zerar() {
        mensagens.clear();
        saveMensagensToFile();
    }
}
