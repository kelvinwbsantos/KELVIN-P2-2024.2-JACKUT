package br.ufal.ic.p2.jackut;

import br.ufal.ic.p2.jackut.models.exceptions.*;
import br.ufal.ic.p2.jackut.models.exceptions.RelacionamentoExceptions.*;
import br.ufal.ic.p2.jackut.services.*;

import java.io.*;
import java.util.List;
import java.util.Set;

/**
 * Classe Facade que gerencia usuários, sessões, amizades e mensagens no sistema Jackut.
 */
public class Facade implements Serializable {

    private static final long serialVersionUID = 1L;
    private static final String DATA_FILE = "jackut_data.ser";

    private UsuarioService usuarioService;
    private SessaoService sessaoService;
    private AmizadeService amizadeService;
    private RecadoService recadoService;
    private ComunidadeService comunidadeService;
    private RelacionamentoService relacionamentoService;
    private MensagemService mensagemService;
    private RemoverService removerService;

    public Facade() {
        this.usuarioService = new UsuarioService();
        this.sessaoService = new SessaoService(usuarioService);

// primeiro crie os objetos com dependências mínimas
        this.recadoService = new RecadoService(usuarioService, sessaoService); // sem relacionamento por agora
        this.relacionamentoService = new RelacionamentoService(usuarioService, sessaoService); // idem

// depois conecte os dois manualmente
        this.recadoService.setRelacionamentoService(relacionamentoService);
        this.relacionamentoService.setRecadoService(recadoService);

// agora pode instanciar os outros
        this.comunidadeService = new ComunidadeService(usuarioService, sessaoService);
        this.amizadeService = new AmizadeService(usuarioService, sessaoService, relacionamentoService);
        this.mensagemService = new MensagemService(comunidadeService, sessaoService);

        this.removerService = new RemoverService(usuarioService, amizadeService, recadoService, comunidadeService, relacionamentoService, mensagemService, sessaoService);

    }

    /**
     * Reseta o sistema, apagando todos os usuários cadastrados.
     */
    public void zerarSistema() {
        usuarioService.zerar();
        recadoService.zerar();
        comunidadeService.zerar();
        amizadeService.zerar();
        relacionamentoService.zerar();
        mensagemService.zerar();
    }

    /**
     * Encerra o sistema e persiste os dados.
     */
    public void encerrarSistema() {
        usuarioService.saveUsuariosToFile();
        recadoService.saveRecadosToFile();
        comunidadeService.saveComunidadesToFile();
        amizadeService.saveAmizadesToFile();
        relacionamentoService.saveRelacionamentoToFile();
        mensagemService.saveMensagensToFile();
    }

    public String getAtributoUsuario(String login, String atributo) throws UsuarioException {
        return usuarioService.getAtributoUsuario(login, atributo);
    }

    public void criarUsuario(String login, String senha, String nome) throws UsuarioException {
        usuarioService.criarUsuario(login, senha, nome);
    }

    public String abrirSessao(String login, String senha) throws LoginSenhaInvalidosException, UsuarioNaoCadastradoException {
        return sessaoService.abrirSessao(login, senha);
    }

    public void editarPerfil(String idSessao, String atributo, String valor) throws UsuarioException {
        sessaoService.editarPerfil(idSessao, atributo, valor);
    }

    public void adicionarAmigo(String idSessao, String amigo) throws UsuarioException, UsuarioEhSeuInimigoException {
        amizadeService.adicionarAmigo(idSessao, amigo);
    }

    public boolean ehAmigo(String login, String amigo) throws UsuarioException {
        return amizadeService.ehAmigo(login, amigo);
    }

    public String getAmigos(String login) throws UsuarioException {
        Set<String> amigos =  amizadeService.getAmigos(login);
        return amigos.isEmpty() ? "{}" : "{" + String.join(",", amigos) + "}";
    }

    public void enviarRecado(String idSessao, String destinatario, String recado) throws UsuarioException, UsuarioEhSeuInimigoException {
        recadoService.enviarRecado(idSessao, destinatario, recado);
    }

    public String lerRecado(String idSessao) throws UsuarioException {
        return recadoService.lerRecado(idSessao);
    }

    public void criarComunidade(String idSessao, String nome, String descricao) throws UsuarioException, ComunidadeNomeExisteException {
        comunidadeService.criarComunidade(idSessao, nome, descricao);
    }

    public String getDescricaoComunidade(String nomeComunidade) throws ComunidadeNaoExisteException {
        return comunidadeService.getDescricaoComunidade(nomeComunidade);
    }

    public String getDonoComunidade(String nomeComunidade) throws ComunidadeNaoExisteException {
        return comunidadeService.getDonoComunidade(nomeComunidade);
    }

    public String getMembrosComunidade(String nomeComunidade) throws ComunidadeNaoExisteException {
        Set<String> membrosComunidade = comunidadeService.getMembrosComunidade(nomeComunidade);
        return membrosComunidade.isEmpty() ? "{}" : "{" + String.join(",", membrosComunidade) + "}";
    }

    public String getComunidades(String login) throws UsuarioNaoCadastradoException {
        List<String> comunidadesDoUsuario = comunidadeService.getComunidades(login);

        return comunidadesDoUsuario.isEmpty() ? "{}" : "{" + String.join(",", comunidadesDoUsuario) + "}";
    }

    public void adicionarComunidade(String idSessao, String nome) throws UsuarioException, ComunidadeNaoExisteException, UsuarioJaFazParteComunidadeException {
        comunidadeService.adicionarComunidade(idSessao, nome);
    }

    public String lerMensagem(String idSessao) throws UsuarioException {
        return mensagemService.lerMensagem(idSessao);
    }

    public void enviarMensagem(String idSessao, String comunidade, String mensagem) throws UsuarioException, ComunidadeNaoExisteException {
        mensagemService.enviarMensagem(idSessao, comunidade, mensagem);
    }


    public boolean ehFa(String login, String idolo) {
        return relacionamentoService.ehFa(login, idolo);
    }

    public void adicionarIdolo(String idSessao, String nome) throws UsuarioException, UsuarioEhSeuInimigoException {
        relacionamentoService.adicionarIdolo(idSessao, nome);
    }

    public String getFas(String login) {
        Set<String> fas = relacionamentoService.getFas(login);
        return fas.isEmpty() ? "{}" : "{" + String.join(",", fas) + "}";
    }

    public boolean ehPaquera(String idSessao, String paquera) throws UsuarioException {
        return relacionamentoService.ehPaquera(idSessao, paquera);
    }

    public void adicionarPaquera(String idSessao, String paquera) throws UsuarioException, UsuarioJaEstaAdicionadoComoPaqueraException, UsuarioNaoPodeSerPaqueraDeSiMesmoException, UsuarioEhSeuInimigoException {
        relacionamentoService.adicionarPaquera(idSessao, paquera);
    }

    public String getPaqueras(String idSessao) throws UsuarioException {
        Set<String> paqueras = relacionamentoService.getPaqueras(idSessao);
        return paqueras.isEmpty() ? "{}" : "{" + String.join(",", paqueras) + "}";
    }

    public void adicionarInimigo(String idSessao, String inimigo) throws UsuarioException, UsuarioJaEstaAdicionadoComoInimigoException, UsuarioNaoPodeSerInimigoDeSiMesmoException {
        relacionamentoService.adicionarInimigo(idSessao, inimigo);
    }

    public void removerUsuario(String idSessao) throws UsuarioException, ComunidadeNaoExisteException {
        removerService.removerUsuario(idSessao);
    }




}
