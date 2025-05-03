package br.ufal.ic.p2.jackut.services;

import java.util.HashMap;
import java.util.Map;
import java.util.*;


import br.ufal.ic.p2.jackut.models.entities.*;
import br.ufal.ic.p2.jackut.models.exceptions.*;

public class SessaoService {
    private Map<String, Sessao> sessoes = new HashMap<>(); // Usando Map para associar idSessao à Sessao
    private UsuarioService usuarioService; // Para acessar os usuários

    public SessaoService(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

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
        String idSessao = UUID.randomUUID().toString(); // Melhor segurança
        Sessao novaSessao = new Sessao(idSessao, login);
        sessoes.put(idSessao, novaSessao);

        return idSessao; // Retorna o ID da sessão criada
    }

    public void editarPerfil(String idSessao, String atributo, String valor) throws UsuarioException {
        Sessao sessao = sessoes.get(idSessao);

        if (sessao == null) { throw new UsuarioNaoCadastradoException(); }

        String login = sessao.getLogin();

        Usuario usuario = usuarioService.getUsuario(login);

        usuario.setAtributo(atributo, valor);
    }

    public Usuario getUsuarioPorSessao(String idSessao) throws UsuarioException {
        Sessao sessao = sessoes.get(idSessao);
        if (sessao == null) {
            throw new UsuarioNaoCadastradoException();
        }
        return usuarioService.getUsuario(sessao.getLogin());
    }


}

