package br.ufal.ic.p2.jackut.services;

import java.util.HashMap;
import java.util.Map;
import java.io.*;

import br.ufal.ic.p2.jackut.models.entities.Usuario;
import br.ufal.ic.p2.jackut.models.exceptions.*;

/**
 * Serviço responsável pela manipulação e persistência de usuários no sistema.
 * Isso inclui a criação de usuários, remoção, obtenção de atributos e persistência em arquivo.
 */
public class UsuarioService {
    public Map<String, Usuario> usuarios = new HashMap<>(); // Mapeia login para o objeto Usuario

    /**
     * Construtor que carrega os usuários do arquivo persistido.
     */
    public UsuarioService() {
        usuarios = loadUsuariosFromFile();
    }

    /**
     * Remove um usuário do sistema com base no login.
     *
     * @param login O login do usuário a ser removido.
     */
    public void removerUsuario(String login) {
        usuarios.remove(login); // Remove o usuário do mapa
        saveUsuariosToFile(); // Salva o estado após a remoção
    }

    /**
     * Salva todos os usuários persistidos em um arquivo binário.
     */
    public void saveUsuariosToFile() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("usuarios.ser"))) {
            oos.writeObject(usuarios); // Salva o mapa de usuários no arquivo
        } catch (Exception e) {
            e.printStackTrace(); // Em caso de erro, imprime a stack trace
        }
    }

    /**
     * Carrega os usuários de um arquivo binário.
     *
     * @return Um mapa de usuários carregado do arquivo ou um mapa vazio caso não existam dados.
     */
    @SuppressWarnings("unchecked")
    private Map<String, Usuario> loadUsuariosFromFile() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream("usuarios.ser"))) {
            return (Map<String, Usuario>) ois.readObject(); // Retorna o mapa de usuários carregado
        } catch (Exception e) {
            return new HashMap<>(); // Caso não seja possível carregar, retorna um mapa vazio
        }
    }

    /**
     * Limpa todos os usuários e salva o estado vazio no arquivo.
     */
    public void zerar() {
        usuarios.clear(); // Limpa todos os usuários do mapa
        saveUsuariosToFile(); // Salva o estado vazio no arquivo
    }

    /**
     * Obtém o valor de um atributo de um usuário.
     *
     * @param login O login do usuário.
     * @param atributo O nome do atributo a ser recuperado (ex: "login", "nome", "senha").
     * @return O valor do atributo requisitado.
     * @throws UsuarioException Se o usuário não for encontrado ou o atributo não existir.
     */
    public String getAtributoUsuario(String login, String atributo) throws UsuarioException {
        Usuario usuario = usuarios.get(login);

        if (usuario == null) {
            throw new UsuarioNaoCadastradoException(); // Lança exceção se o usuário não for encontrado
        }

        // Retorna o atributo especificado
        switch (atributo) {
            case "login":
                return usuario.getLogin();
            case "nome":
                return usuario.getNome();
            case "senha":
                return usuario.getSenha();
            default:
                return usuario.getAtributo(atributo); // Obtém outros atributos genéricos
        }
    }

    /**
     * Cria um novo usuário no sistema.
     *
     * @param login O login do novo usuário.
     * @param senha A senha do novo usuário.
     * @param nome O nome do novo usuário.
     * @throws UsuarioException Se o login ou senha forem inválidos, ou se o usuário já existir.
     */
    public void criarUsuario(String login, String senha, String nome) throws UsuarioException {
        if (login == null || login.trim().isEmpty()) {
            throw new LoginInvalidoException(); // Lança exceção se o login for inválido
        }
        if (senha == null || senha.trim().isEmpty()) {
            throw new SenhaInvalidaException(); // Lança exceção se a senha for inválida
        }

        // Verifica se o login já existe
        if (usuarios.containsKey(login)) {
            throw new ContaJaExisteNomeException(); // Lança exceção se a conta já existir
        }

        // Cria o novo usuário e adiciona ao mapa
        Usuario novoUsuario = new Usuario(login, senha, nome);
        usuarios.put(login, novoUsuario);

        // Persiste os dados do novo usuário
        saveUsuariosToFile();
    }

    /**
     * Obtém um usuário a partir do login.
     *
     * @param login O login do usuário.
     * @return O objeto Usuario correspondente ao login.
     * @throws UsuarioNaoCadastradoException Se o usuário não for encontrado.
     */
    public Usuario getUsuario(String login) throws UsuarioNaoCadastradoException {
        Usuario usuario = usuarios.get(login);

        if (usuario == null) {
            throw new UsuarioNaoCadastradoException(); // Lança exceção se o usuário não for encontrado
        }

        return usuario; // Retorna o objeto Usuario
    }
}
