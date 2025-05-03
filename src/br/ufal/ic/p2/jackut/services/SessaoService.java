package br.ufal.ic.p2.jackut.services;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import br.ufal.ic.p2.jackut.models.entities.*;
import br.ufal.ic.p2.jackut.models.exceptions.*;

/**
 * Serviço responsável pelo gerenciamento das sessões dos usuários no sistema.
 * Isso inclui abrir novas sessões, editar perfis e obter informações sobre o usuário logado.
 */
public class SessaoService {
    private Map<String, Sessao> sessoes = new HashMap<>(); // Mapeia ID da sessão para a Sessão
    private UsuarioService usuarioService; // Serviço para acessar informações de usuários

    /**
     * Construtor do serviço de sessões.
     *
     * @param usuarioService Serviço responsável pela manipulação de usuários.
     */
    public SessaoService(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    /**
     * Abre uma nova sessão para um usuário, verificando o login e senha.
     *
     * @param login O login do usuário que deseja abrir a sessão.
     * @param senha A senha do usuário.
     * @return O ID da nova sessão criada.
     * @throws LoginSenhaInvalidosException Se o login ou a senha estiverem incorretos.
     */
    public String abrirSessao(String login, String senha) throws LoginSenhaInvalidosException {
        // Valida se o login e a senha são válidos
        try {
            Usuario usuario = usuarioService.getUsuario(login);

            // Se o usuário não existir ou a senha não corresponder
            if (usuario == null || !usuario.getSenha().equals(senha)) {
                throw new LoginSenhaInvalidosException();
            }

        } catch (UsuarioNaoCadastradoException e) {
            throw new LoginSenhaInvalidosException();
        }

        // Caso o login e senha sejam válidos, cria a nova sessão
        String idSessao = UUID.randomUUID().toString(); // Gerar ID único para a sessão
        Sessao novaSessao = new Sessao(idSessao, login);
        sessoes.put(idSessao, novaSessao); // Armazena a sessão no mapa de sessões

        return idSessao; // Retorna o ID da sessão criada
    }

    /**
     * Edita um atributo do perfil do usuário logado.
     *
     * @param idSessao O ID da sessão do usuário logado.
     * @param atributo O atributo do perfil que se deseja editar (por exemplo, nome, email).
     * @param valor O novo valor para o atributo.
     * @throws UsuarioException Se ocorrer algum erro relacionado ao usuário ou sessão.
     */
    public void editarPerfil(String idSessao, String atributo, String valor) throws UsuarioException {
        Sessao sessao = sessoes.get(idSessao);

        if (sessao == null) {
            throw new UsuarioNaoCadastradoException();
        }

        String login = sessao.getLogin(); // Obtém o login do usuário a partir da sessão

        Usuario usuario = usuarioService.getUsuario(login); // Obtém o objeto do usuário a partir do login

        usuario.setAtributo(atributo, valor); // Edita o atributo do usuário
    }

    /**
     * Obtém o usuário associado a uma sessão, caso a sessão seja válida.
     *
     * @param idSessao O ID da sessão ativa.
     * @return O usuário associado a essa sessão.
     * @throws UsuarioException Se a sessão for inválida ou o usuário não for encontrado.
     */
    public Usuario getUsuarioPorSessao(String idSessao) throws UsuarioException {
        Sessao sessao = sessoes.get(idSessao); // Obtém a sessão através do ID

        if (sessao == null) {
            throw new UsuarioNaoCadastradoException(); // Lança exceção se a sessão não for válida
        }

        return usuarioService.getUsuario(sessao.getLogin()); // Retorna o usuário associado a essa sessão
    }
}
