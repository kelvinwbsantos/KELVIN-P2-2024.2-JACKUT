package br.ufal.ic.p2.jackut.services;

import br.ufal.ic.p2.jackut.models.entities.Usuario;
import br.ufal.ic.p2.jackut.models.exceptions.ComunidadeNaoExisteException;
import br.ufal.ic.p2.jackut.models.exceptions.UsuarioException;
import br.ufal.ic.p2.jackut.models.exceptions.UsuarioNaoCadastradoException;

public class RemoverService {
    private UsuarioService usuarioService;
    private AmizadeService amizadeService;
    private RecadoService recadoService;
    private ComunidadeService comunidadeService;
    private RelacionamentoService relacionamentoService;
    private MensagemService mensagemService;
    private SessaoService sessaoService;

    public RemoverService(UsuarioService usuarioService, AmizadeService amizadeService, RecadoService recadoService, ComunidadeService comunidadeService, RelacionamentoService relacionamentoService, MensagemService mensagemService, SessaoService sessaoService) {
        this.usuarioService = usuarioService;
        this.amizadeService = amizadeService;
        this.recadoService = recadoService;
        this.comunidadeService = comunidadeService;
        this.relacionamentoService = relacionamentoService;
        this.mensagemService = mensagemService;
        this.sessaoService = sessaoService;
    }

    public void removerUsuario(String idSessao) throws UsuarioException, ComunidadeNaoExisteException {
        Usuario usuario = sessaoService.getUsuarioPorSessao(idSessao);
        String login = usuario.getLogin();


        comunidadeService.removerComunidade(login);
        usuarioService.removerUsuario(login);
        recadoService.removerRecado(login);
    }

}
