package br.ufal.ic.p2.jackut.services;

import br.ufal.ic.p2.jackut.models.entities.Comunidade;
import br.ufal.ic.p2.jackut.models.entities.Usuario;
import br.ufal.ic.p2.jackut.models.exceptions.*;

import java.io.*;
import java.util.*;

/**
 * Serviço responsável pela criação, gerenciamento e persistência de comunidades no sistema Jackut.
 */
public class ComunidadeService {

    /** Serviço responsável pela manipulação de usuários. */
    private UsuarioService usuarioService;

    /** Serviço responsável pela manipulação de sessões de usuários. */
    private SessaoService sessaoService;

    /** Mapa que associa o nome da comunidade ao objeto {@link Comunidade}. */
    public Map<String, Comunidade> comunidades = new LinkedHashMap<>();

    /**
     * Construtor da classe {@code ComunidadeService}.
     *
     * @param usuarioService Serviço de usuários.
     * @param sessaoService Serviço de sessões.
     */
    public ComunidadeService(UsuarioService usuarioService, SessaoService sessaoService) {
        this.usuarioService = usuarioService;
        this.sessaoService = sessaoService;
        this.comunidades = loadComunidadesFromFile();
    }

    /**
     * Remove todas as comunidades criadas por um determinado usuário.
     * Remove também a comunidade da lista de todos os membros.
     *
     * @param login Login do usuário dono das comunidades.
     * @throws UsuarioNaoCadastradoException Se o usuário não estiver cadastrado.
     */
    public void removerComunidade(String login) throws UsuarioNaoCadastradoException {
        Usuario usuario = usuarioService.getUsuario(login);
        if (usuario == null) throw new UsuarioNaoCadastradoException();

        List<String> comunidadesUsuario = new ArrayList<>(usuario.comunidades);

        for (String nomeComunidade : comunidadesUsuario) {
            Comunidade comunidade = comunidades.get(nomeComunidade);

            if (comunidade != null && comunidade.getloginDono().equals(login)) {
                for (String participante : new HashSet<>(comunidade.getMembrosComunidade())) {
                    Usuario user = usuarioService.usuarios.get(participante);
                    if (user != null) {
                        user.comunidades.remove(nomeComunidade);
                    }
                }

                comunidades.remove(nomeComunidade);
                usuario.comunidades.remove(nomeComunidade);
            }
        }

        saveComunidadesToFile();
        usuarioService.saveUsuariosToFile();
    }

    /**
     * Cria uma nova comunidade no sistema.
     *
     * @param idSessao ID da sessão do usuário que criará a comunidade.
     * @param nome Nome da comunidade.
     * @param descricao Descrição da comunidade.
     * @throws UsuarioException Se o usuário não estiver autenticado.
     * @throws ComunidadeNomeExisteException Se já existir uma comunidade com esse nome.
     */
    public void criarComunidade(String idSessao, String nome, String descricao)
            throws UsuarioException, ComunidadeNomeExisteException {
        Usuario dono = sessaoService.getUsuarioPorSessao(idSessao);
        String loginDono = dono.getLogin();

        if (!comunidades.containsKey(nome)) {
            Comunidade comunidade = new Comunidade(loginDono, nome, descricao);
            comunidades.put(nome, comunidade);
            dono.comunidades.add(nome);
            saveComunidadesToFile();
        } else {
            throw new ComunidadeNomeExisteException();
        }
    }

    /**
     * Retorna a descrição de uma comunidade.
     *
     * @param nome Nome da comunidade.
     * @return A descrição da comunidade.
     * @throws ComunidadeNaoExisteException Se a comunidade não existir.
     */
    public String getDescricaoComunidade(String nome) throws ComunidadeNaoExisteException {
        Comunidade comunidade = comunidades.get(nome);
        if (comunidade == null) throw new ComunidadeNaoExisteException();
        return comunidade.getDescricao();
    }

    /**
     * Retorna o login do dono de uma comunidade.
     *
     * @param nome Nome da comunidade.
     * @return Login do dono da comunidade.
     * @throws ComunidadeNaoExisteException Se a comunidade não existir.
     */
    public String getDonoComunidade(String nome) throws ComunidadeNaoExisteException {
        Comunidade comunidade = comunidades.get(nome);
        if (comunidade == null) throw new ComunidadeNaoExisteException();
        return comunidade.getloginDono();
    }

    /**
     * Retorna os membros de uma comunidade.
     *
     * @param nome Nome da comunidade.
     * @return Conjunto com os logins dos membros da comunidade.
     * @throws ComunidadeNaoExisteException Se a comunidade não existir.
     */
    public Set<String> getMembrosComunidade(String nome) throws ComunidadeNaoExisteException {
        Comunidade comunidade = comunidades.get(nome);
        if (comunidade == null) throw new ComunidadeNaoExisteException();
        return comunidade.getMembrosComunidade();
    }

    /**
     * Salva o estado atual das comunidades no arquivo "comunidades.ser".
     */
    public void saveComunidadesToFile() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("comunidades.ser"))) {
            oos.writeObject(comunidades);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Carrega as comunidades a partir do arquivo "comunidades.ser".
     *
     * @return Mapa com os dados das comunidades.
     */
    @SuppressWarnings("unchecked")
    private Map<String, Comunidade> loadComunidadesFromFile() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream("comunidades.ser"))) {
            return (Map<String, Comunidade>) ois.readObject();
        } catch (Exception e) {
            return new LinkedHashMap<>();
        }
    }

    /**
     * Remove todas as comunidades do sistema (reseta o serviço).
     */
    public void zerar() {
        comunidades.clear();
        saveComunidadesToFile();
    }

    /**
     * Retorna a lista de nomes de comunidades das quais o usuário participa.
     *
     * @param login Login do usuário.
     * @return Lista de nomes das comunidades.
     * @throws UsuarioNaoCadastradoException Se o usuário não existir.
     */
    public List<String> getComunidades(String login) throws UsuarioNaoCadastradoException {
        Usuario usuario = usuarioService.getUsuario(login);
        if (usuario == null) throw new UsuarioNaoCadastradoException();
        return usuario.comunidades;
    }

    /**
     * Adiciona um usuário autenticado a uma comunidade existente.
     *
     * @param idSessao ID da sessão do usuário.
     * @param nomeComunidade Nome da comunidade.
     * @throws UsuarioException Se o usuário não estiver autenticado.
     * @throws ComunidadeNaoExisteException Se a comunidade não existir.
     * @throws UsuarioJaFazParteComunidadeException Se o usuário já participa da comunidade.
     */
    public void adicionarComunidade(String idSessao, String nomeComunidade)
            throws UsuarioException, ComunidadeNaoExisteException, UsuarioJaFazParteComunidadeException {
        Usuario usuario = sessaoService.getUsuarioPorSessao(idSessao);
        String login = usuario.getLogin();

        Comunidade comunidade = comunidades.get(nomeComunidade);
        if (comunidade == null) {
            throw new ComunidadeNaoExisteException();
        }

        if (comunidade.getMembrosComunidade().contains(login)) {
            throw new UsuarioJaFazParteComunidadeException();
        }

        comunidade.adicionarMembro(login);
        usuario.comunidades.add(nomeComunidade);
        saveComunidadesToFile();
    }
}
