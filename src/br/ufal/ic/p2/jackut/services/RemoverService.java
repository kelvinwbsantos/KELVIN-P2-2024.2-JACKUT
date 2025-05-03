package br.ufal.ic.p2.jackut.services;

import br.ufal.ic.p2.jackut.models.entities.Usuario;
import br.ufal.ic.p2.jackut.models.exceptions.ComunidadeNaoExisteException;
import br.ufal.ic.p2.jackut.models.exceptions.UsuarioException;
import br.ufal.ic.p2.jackut.models.exceptions.UsuarioNaoCadastradoException;

/**
 * Servi�o respons�vel por remover um usu�rio do sistema, incluindo suas associa��es com comunidades, amizades e recados.
 */
public class RemoverService {

    private UsuarioService usuarioService;
    private AmizadeService amizadeService;
    private RecadoService recadoService;
    private ComunidadeService comunidadeService;
    private RelacionamentoService relacionamentoService;
    private MensagemService mensagemService;
    private SessaoService sessaoService;

    /**
     * Construtor do servi�o de remo��o de usu�rio.
     *
     * @param usuarioService Servi�o respons�vel pelo gerenciamento de usu�rios.
     * @param amizadeService Servi�o respons�vel pela gest�o de amizades.
     * @param recadoService Servi�o respons�vel pelo envio e remo��o de recados.
     * @param comunidadeService Servi�o respons�vel pela gest�o das comunidades.
     * @param relacionamentoService Servi�o respons�vel pelos relacionamentos (�dolos, f�s, paqueras, inimigos).
     * @param mensagemService Servi�o respons�vel pelo gerenciamento de mensagens.
     * @param sessaoService Servi�o respons�vel pela gest�o de sess�es de usu�rios.
     */
    public RemoverService(UsuarioService usuarioService, AmizadeService amizadeService, RecadoService recadoService,
                          ComunidadeService comunidadeService, RelacionamentoService relacionamentoService,
                          MensagemService mensagemService, SessaoService sessaoService) {
        this.usuarioService = usuarioService;
        this.amizadeService = amizadeService;
        this.recadoService = recadoService;
        this.comunidadeService = comunidadeService;
        this.relacionamentoService = relacionamentoService;
        this.mensagemService = mensagemService;
        this.sessaoService = sessaoService;
    }

    /**
     * Remove um usu�rio do sistema, incluindo a remo��o de suas associa��es com comunidades, amizades e recados.
     *
     * @param idSessao Identificador da sess�o do usu�rio logado.
     * @throws UsuarioException Se ocorrer algum erro relacionado ao usu�rio durante o processo de remo��o.
     * @throws ComunidadeNaoExisteException Se a comunidade associada ao usu�rio n�o existir.
     */
    public void removerUsuario(String idSessao) throws UsuarioException, ComunidadeNaoExisteException {
        // Obt�m o usu�rio a partir da sess�o ativa.
        Usuario usuario = sessaoService.getUsuarioPorSessao(idSessao);
        String login = usuario.getLogin();

        // Remove o usu�rio da comunidade associada (se existir).
        comunidadeService.removerComunidade(login);

        // Remove o usu�rio do servi�o de usu�rios.
        usuarioService.removerUsuario(login);

        // Remove os recados relacionados ao usu�rio.
        recadoService.removerRecado(login);
    }
}
