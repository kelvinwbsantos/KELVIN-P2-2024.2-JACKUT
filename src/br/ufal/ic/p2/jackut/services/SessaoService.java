package br.ufal.ic.p2.jackut.services;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import br.ufal.ic.p2.jackut.models.entities.*;
import br.ufal.ic.p2.jackut.models.exceptions.*;

/**
 * Servi�o respons�vel pelo gerenciamento das sess�es dos usu�rios no sistema.
 * Isso inclui abrir novas sess�es, editar perfis e obter informa��es sobre o usu�rio logado.
 */
public class SessaoService {
    private Map<String, Sessao> sessoes = new HashMap<>(); // Mapeia ID da sess�o para a Sess�o
    private UsuarioService usuarioService; // Servi�o para acessar informa��es de usu�rios

    /**
     * Construtor do servi�o de sess�es.
     *
     * @param usuarioService Servi�o respons�vel pela manipula��o de usu�rios.
     */
    public SessaoService(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    /**
     * Abre uma nova sess�o para um usu�rio, verificando o login e senha.
     *
     * @param login O login do usu�rio que deseja abrir a sess�o.
     * @param senha A senha do usu�rio.
     * @return O ID da nova sess�o criada.
     * @throws LoginSenhaInvalidosException Se o login ou a senha estiverem incorretos.
     */
    public String abrirSessao(String login, String senha) throws LoginSenhaInvalidosException {
        // Valida se o login e a senha s�o v�lidos
        try {
            Usuario usuario = usuarioService.getUsuario(login);

            // Se o usu�rio n�o existir ou a senha n�o corresponder
            if (usuario == null || !usuario.getSenha().equals(senha)) {
                throw new LoginSenhaInvalidosException();
            }

        } catch (UsuarioNaoCadastradoException e) {
            throw new LoginSenhaInvalidosException();
        }

        // Caso o login e senha sejam v�lidos, cria a nova sess�o
        String idSessao = UUID.randomUUID().toString(); // Gerar ID �nico para a sess�o
        Sessao novaSessao = new Sessao(idSessao, login);
        sessoes.put(idSessao, novaSessao); // Armazena a sess�o no mapa de sess�es

        return idSessao; // Retorna o ID da sess�o criada
    }

    /**
     * Edita um atributo do perfil do usu�rio logado.
     *
     * @param idSessao O ID da sess�o do usu�rio logado.
     * @param atributo O atributo do perfil que se deseja editar (por exemplo, nome, email).
     * @param valor O novo valor para o atributo.
     * @throws UsuarioException Se ocorrer algum erro relacionado ao usu�rio ou sess�o.
     */
    public void editarPerfil(String idSessao, String atributo, String valor) throws UsuarioException {
        Sessao sessao = sessoes.get(idSessao);

        if (sessao == null) {
            throw new UsuarioNaoCadastradoException();
        }

        String login = sessao.getLogin(); // Obt�m o login do usu�rio a partir da sess�o

        Usuario usuario = usuarioService.getUsuario(login); // Obt�m o objeto do usu�rio a partir do login

        usuario.setAtributo(atributo, valor); // Edita o atributo do usu�rio
    }

    /**
     * Obt�m o usu�rio associado a uma sess�o, caso a sess�o seja v�lida.
     *
     * @param idSessao O ID da sess�o ativa.
     * @return O usu�rio associado a essa sess�o.
     * @throws UsuarioException Se a sess�o for inv�lida ou o usu�rio n�o for encontrado.
     */
    public Usuario getUsuarioPorSessao(String idSessao) throws UsuarioException {
        Sessao sessao = sessoes.get(idSessao); // Obt�m a sess�o atrav�s do ID

        if (sessao == null) {
            throw new UsuarioNaoCadastradoException(); // Lan�a exce��o se a sess�o n�o for v�lida
        }

        return usuarioService.getUsuario(sessao.getLogin()); // Retorna o usu�rio associado a essa sess�o
    }
}
