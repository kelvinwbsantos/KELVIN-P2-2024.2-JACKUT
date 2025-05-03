package br.ufal.ic.p2.jackut.services;

import java.util.HashMap;
import java.util.Map;
import java.io.*;

import br.ufal.ic.p2.jackut.models.entities.Usuario;
import br.ufal.ic.p2.jackut.models.exceptions.*;

public class UsuarioService {
    public Map<String, Usuario> usuarios = new HashMap<>();

    public UsuarioService() {
        usuarios = loadUsuariosFromFile();
    }

    public void removerUsuario(String login) {
        usuarios.remove(login);
        saveUsuariosToFile();
    }

    public void saveUsuariosToFile() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("usuarios.ser"))) {
            oos.writeObject(usuarios); // Salva o mapa inteiro
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("unchecked")
    private Map<String, Usuario> loadUsuariosFromFile() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream("usuarios.ser"))) {
            return (Map<String, Usuario>) ois.readObject(); // Carrega o mapa de comunidades
        } catch (Exception e) {
            return new HashMap<>(); // Retorna um mapa vazio se não houver dados
        }
    }

    public void zerar() {
        usuarios.clear();
        saveUsuariosToFile();
    }

    public String getAtributoUsuario(String login, String atributo) throws UsuarioException {
        Usuario usuario = usuarios.get(login);

        if (usuario == null) {
            throw new UsuarioNaoCadastradoException();
        }

        switch (atributo) {
            case "login":
                return usuario.getLogin();
            case "nome":
                return usuario.getNome();
            case "senha":
                return usuario.getSenha();
            default:
                return usuario.getAtributo(atributo);
        }
    }

    public void criarUsuario(String login, String senha, String nome) throws UsuarioException {
        if (login == null || login.trim().isEmpty()) {
            throw new LoginInvalidoException();
        }
        if (senha == null || senha.trim().isEmpty()) {
            throw new SenhaInvalidaException();
        }

        // Verifica se o login já existe no mapa
        if (usuarios.containsKey(login)) {
            throw new ContaJaExisteNomeException();
        }

        Usuario novoUsuario = new Usuario(login, senha, nome);

        // Depois adiciona no Map, usando o login como chave
        usuarios.put(login, novoUsuario);

        // E salva no arquivo para persistir
        saveUsuariosToFile();
    }

    // Método para obter um usuário pelo login
    public Usuario getUsuario(String login) throws UsuarioNaoCadastradoException {
        if (usuarios.get(login) == null) {
            throw new UsuarioNaoCadastradoException();
        }

        return usuarios.get(login);
    }




}


