package br.ufal.ic.p2.jackut.services;

import br.ufal.ic.p2.jackut.models.entities.Usuario;
import br.ufal.ic.p2.jackut.models.exceptions.ComunidadeNaoExisteException;
import br.ufal.ic.p2.jackut.models.exceptions.UsuarioException;
import br.ufal.ic.p2.jackut.models.exceptions.UsuarioNaoCadastradoException;

/**
 * Serviço responsável por remover um usuário do sistema, incluindo suas associações com comunidades, amizades e recados.
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
     * Construtor do serviço de remoção de usuário.
     *
     * @param usuarioService Serviço responsável pelo gerenciamento de usuários.
     * @param amizadeService Serviço responsável pela gestão de amizades.
     * @param recadoService Serviço responsável pelo envio e remoção de recados.
     * @param comunidadeService Serviço responsável pela gestão das comunidades.
     * @param relacionamentoService Serviço responsável pelos relacionamentos (ídolos, fãs, paqueras, inimigos).
     * @param mensagemService Serviço responsável pelo gerenciamento de mensagens.
     * @param sessaoService Serviço responsável pela gestão de sessões de usuários.
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
     * Remove um usuário do sistema, incluindo a remoção de suas associações com comunidades, amizades e recados.
     *
     * @param idSessao Identificador da sessão do usuário logado.
     * @throws UsuarioException Se ocorrer algum erro relacionado ao usuário durante o processo de remoção.
     * @throws ComunidadeNaoExisteException Se a comunidade associada ao usuário não existir.
     */
    public void removerUsuario(String idSessao) throws UsuarioException, ComunidadeNaoExisteException {
        // Obtém o usuário a partir da sessão ativa.
        Usuario usuario = sessaoService.getUsuarioPorSessao(idSessao);
        String login = usuario.getLogin();

        // Remove o usuário da comunidade associada (se existir).
        comunidadeService.removerComunidade(login);

        // Remove o usuário do serviço de usuários.
        usuarioService.removerUsuario(login);

        // Remove os recados relacionados ao usuário.
        recadoService.removerRecado(login);
    }
}
