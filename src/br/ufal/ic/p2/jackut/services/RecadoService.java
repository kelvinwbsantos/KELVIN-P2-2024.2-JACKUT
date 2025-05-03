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
 * Servi�o respons�vel pelo envio, leitura, remo��o e persist�ncia de recados privados entre usu�rios.
 */
public class RecadoService {

    /** Mapa que associa logins de usu�rios �s suas caixas de recados. */
    private Map<String, Recado> recados = new HashMap<>();

    private UsuarioService usuarioService;
    private SessaoService sessaoService;
    private RelacionamentoService relacionamentoService;

    /**
     * Construtor do servi�o de recados.
     *
     * @param usuarioService Servi�o de gerenciamento de usu�rios.
     * @param sessaoService Servi�o de gerenciamento de sess�es.
     */
    public RecadoService(UsuarioService usuarioService, SessaoService sessaoService) {
        this.usuarioService = usuarioService;
        this.sessaoService = sessaoService;
        this.recados = loadRecadosFromFile();
    }

    /**
     * Define o servi�o de relacionamentos para verifica��o de inimigos.
     *
     * @param relacionamentoService Servi�o de relacionamento.
     */
    public void setRelacionamentoService(RelacionamentoService relacionamentoService) {
        this.relacionamentoService = relacionamentoService;
    }

    /**
     * Remove todos os recados de um usu�rio espec�fico.
     *
     * @param login Login do usu�rio.
     */
    public void removerRecado(String login) {
        recados.clear();
        saveRecadosToFile();
    }

    /**
     * Envia um recado de um usu�rio logado para outro usu�rio.
     *
     * @param idSessao Sess�o do remetente.
     * @param destinatario Login do destinat�rio.
     * @param recado Texto do recado.
     * @throws UsuarioException Se a sess�o for inv�lida.
     * @throws UsuarioEhSeuInimigoException Se o destinat�rio for inimigo do remetente.
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
     * Envia um recado do sistema Jackut para um usu�rio (sem sess�o).
     *
     * @param destinatario Login do destinat�rio.
     * @param recado Texto do recado.
     * @throws UsuarioNaoCadastradoException Se o destinat�rio n�o existir.
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
     * L� o pr�ximo recado dispon�vel para o usu�rio logado.
     *
     * @param idSessao Sess�o do usu�rio.
     * @return Texto do recado.
     * @throws UsuarioException Se a sess�o for inv�lida.
     * @throws NaoHaRecadosException Se n�o houver recados dispon�veis.
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
