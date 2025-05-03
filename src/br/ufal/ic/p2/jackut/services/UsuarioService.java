package br.ufal.ic.p2.jackut.services;

import java.util.HashMap;
import java.util.Map;
import java.io.*;

import br.ufal.ic.p2.jackut.models.entities.Usuario;
import br.ufal.ic.p2.jackut.models.exceptions.*;

/**
 * Servi�o respons�vel pela manipula��o e persist�ncia de usu�rios no sistema.
 * Isso inclui a cria��o de usu�rios, remo��o, obten��o de atributos e persist�ncia em arquivo.
 */
public class UsuarioService {
    public Map<String, Usuario> usuarios = new HashMap<>(); // Mapeia login para o objeto Usuario

    /**
     * Construtor que carrega os usu�rios do arquivo persistido.
     */
    public UsuarioService() {
        usuarios = loadUsuariosFromFile();
    }

    /**
     * Remove um usu�rio do sistema com base no login.
     *
     * @param login O login do usu�rio a ser removido.
     */
    public void removerUsuario(String login) {
        usuarios.remove(login); // Remove o usu�rio do mapa
        saveUsuariosToFile(); // Salva o estado ap�s a remo��o
    }

    /**
     * Salva todos os usu�rios persistidos em um arquivo bin�rio.
     */
    public void saveUsuariosToFile() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("usuarios.ser"))) {
            oos.writeObject(usuarios); // Salva o mapa de usu�rios no arquivo
        } catch (Exception e) {
            e.printStackTrace(); // Em caso de erro, imprime a stack trace
        }
    }

    /**
     * Carrega os usu�rios de um arquivo bin�rio.
     *
     * @return Um mapa de usu�rios carregado do arquivo ou um mapa vazio caso n�o existam dados.
     */
    @SuppressWarnings("unchecked")
    private Map<String, Usuario> loadUsuariosFromFile() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream("usuarios.ser"))) {
            return (Map<String, Usuario>) ois.readObject(); // Retorna o mapa de usu�rios carregado
        } catch (Exception e) {
            return new HashMap<>(); // Caso n�o seja poss�vel carregar, retorna um mapa vazio
        }
    }

    /**
     * Limpa todos os usu�rios e salva o estado vazio no arquivo.
     */
    public void zerar() {
        usuarios.clear(); // Limpa todos os usu�rios do mapa
        saveUsuariosToFile(); // Salva o estado vazio no arquivo
    }

    /**
     * Obt�m o valor de um atributo de um usu�rio.
     *
     * @param login O login do usu�rio.
     * @param atributo O nome do atributo a ser recuperado (ex: "login", "nome", "senha").
     * @return O valor do atributo requisitado.
     * @throws UsuarioException Se o usu�rio n�o for encontrado ou o atributo n�o existir.
     */
    public String getAtributoUsuario(String login, String atributo) throws UsuarioException {
        Usuario usuario = usuarios.get(login);

        if (usuario == null) {
            throw new UsuarioNaoCadastradoException(); // Lan�a exce��o se o usu�rio n�o for encontrado
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
                return usuario.getAtributo(atributo); // Obt�m outros atributos gen�ricos
        }
    }

    /**
     * Cria um novo usu�rio no sistema.
     *
     * @param login O login do novo usu�rio.
     * @param senha A senha do novo usu�rio.
     * @param nome O nome do novo usu�rio.
     * @throws UsuarioException Se o login ou senha forem inv�lidos, ou se o usu�rio j� existir.
     */
    public void criarUsuario(String login, String senha, String nome) throws UsuarioException {
        if (login == null || login.trim().isEmpty()) {
            throw new LoginInvalidoException(); // Lan�a exce��o se o login for inv�lido
        }
        if (senha == null || senha.trim().isEmpty()) {
            throw new SenhaInvalidaException(); // Lan�a exce��o se a senha for inv�lida
        }

        // Verifica se o login j� existe
        if (usuarios.containsKey(login)) {
            throw new ContaJaExisteNomeException(); // Lan�a exce��o se a conta j� existir
        }

        // Cria o novo usu�rio e adiciona ao mapa
        Usuario novoUsuario = new Usuario(login, senha, nome);
        usuarios.put(login, novoUsuario);

        // Persiste os dados do novo usu�rio
        saveUsuariosToFile();
    }

    /**
     * Obt�m um usu�rio a partir do login.
     *
     * @param login O login do usu�rio.
     * @return O objeto Usuario correspondente ao login.
     * @throws UsuarioNaoCadastradoException Se o usu�rio n�o for encontrado.
     */
    public Usuario getUsuario(String login) throws UsuarioNaoCadastradoException {
        Usuario usuario = usuarios.get(login);

        if (usuario == null) {
            throw new UsuarioNaoCadastradoException(); // Lan�a exce��o se o usu�rio n�o for encontrado
        }

        return usuario; // Retorna o objeto Usuario
    }
}
