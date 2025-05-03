package br.ufal.ic.p2.jackut.services;

import br.ufal.ic.p2.jackut.models.entities.Recado;
import br.ufal.ic.p2.jackut.models.entities.Sessao;
import br.ufal.ic.p2.jackut.models.entities.Usuario;
import br.ufal.ic.p2.jackut.models.exceptions.*;
import br.ufal.ic.p2.jackut.models.exceptions.RelacionamentoExceptions.UsuarioEhSeuInimigoException;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Serviço responsável pelo envio, leitura, remoção e persistência de recados privados entre usuários.
 */
public class RecadoService {

    /** Mapa que associa logins de usuários às suas caixas de recados. */
    private Map<String, Recado> recados = new HashMap<>();

    private UsuarioService usuarioService;
    private SessaoService sessaoService;
    private RelacionamentoService relacionamentoService;

    /**
     * Construtor do serviço de recados.
     *
     * @param usuarioService Serviço de gerenciamento de usuários.
     * @param sessaoService Serviço de gerenciamento de sessões.
     */
    public RecadoService(UsuarioService usuarioService, SessaoService sessaoService) {
        this.usuarioService = usuarioService;
        this.sessaoService = sessaoService;
        this.recados = loadRecadosFromFile();
    }

    /**
     * Define o serviço de relacionamentos para verificação de inimigos.
     *
     * @param relacionamentoService Serviço de relacionamento.
     */
    public void setRelacionamentoService(RelacionamentoService relacionamentoService) {
        this.relacionamentoService = relacionamentoService;
    }

    /**
     * Remove todos os recados de um usuário específico.
     *
     * @param login Login do usuário.
     */
    public void removerRecado(String login) {
        recados.clear();
        saveRecadosToFile();
    }

    /**
     * Envia um recado de um usuário logado para outro usuário.
     *
     * @param idSessao Sessão do remetente.
     * @param destinatario Login do destinatário.
     * @param recado Texto do recado.
     * @throws UsuarioException Se a sessão for inválida.
     * @throws UsuarioEhSeuInimigoException Se o destinatário for inimigo do remetente.
     * @throws UsuarioNaoPodeEnviarRecadoParaSiMesmoException Se o remetente tentar enviar para si mesmo.
     */
    public void enviarRecado(String idSessao, String destinatario, String recado)
            throws UsuarioException, UsuarioEhSeuInimigoException {
        Usuario remetente = sessaoService.getUsuarioPorSessao(idSessao);
        Usuario recebedor = usuarioService.getUsuario(destinatario);

        if (relacionamentoService.ehInimigo(idSessao, destinatario)) {
            throw new UsuarioEhSeuInimigoException(usuarioService.getAtributoUsuario(destinatario, "nome"));
        }

        if (remetente == recebedor) {
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

    /**
     * Envia um recado do sistema Jackut para um usuário (sem sessão).
     *
     * @param destinatario Login do destinatário.
     * @param recado Texto do recado.
     * @throws UsuarioNaoCadastradoException Se o destinatário não existir.
     */
    public void enviarRecadoJackut(String destinatario, String recado)
            throws UsuarioNaoCadastradoException {
        Usuario recebedor = usuarioService.getUsuario(destinatario);

        Recado inboxDestinatario = recados.get(destinatario);
        if (inboxDestinatario == null) {
            inboxDestinatario = new Recado();
            recados.put(destinatario, inboxDestinatario);
        }

        inboxDestinatario.adicionarRecado(recado);
        saveRecadosToFile();
    }

    /**
     * Lê o próximo recado disponível para o usuário logado.
     *
     * @param idSessao Sessão do usuário.
     * @return Texto do recado.
     * @throws UsuarioException Se a sessão for inválida.
     * @throws NaoHaRecadosException Se não houver recados disponíveis.
     */
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

    /**
     * Salva os recados no arquivo "recados.ser".
     */
    public void saveRecadosToFile() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("recados.ser"))) {
            oos.writeObject(recados);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Carrega os recados do arquivo "recados.ser".
     *
     * @return Mapa de recados carregado do disco.
     */
    @SuppressWarnings("unchecked")
    private Map<String, Recado> loadRecadosFromFile() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream("recados.ser"))) {
            return (Map<String, Recado>) ois.readObject();
        } catch (Exception e) {
            return new HashMap<>();
        }
    }

    /**
     * Remove todos os recados do sistema e salva o estado limpo.
     */
    public void zerar() {
        recados.clear();
        saveRecadosToFile();
    }
}
