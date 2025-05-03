package br.ufal.ic.p2.jackut.services;

import br.ufal.ic.p2.jackut.models.entities.Comunidade;
import br.ufal.ic.p2.jackut.models.entities.Usuario;
import br.ufal.ic.p2.jackut.models.exceptions.*;

import java.io.*;
import java.util.*;

/**
 * Servi�o respons�vel pela cria��o, gerenciamento e persist�ncia de comunidades no sistema Jackut.
 */
public class ComunidadeService {

    /** Servi�o respons�vel pela manipula��o de usu�rios. */
    private UsuarioService usuarioService;

    /** Servi�o respons�vel pela manipula��o de sess�es de usu�rios. */
    private SessaoService sessaoService;

    /** Mapa que associa o nome da comunidade ao objeto {@link Comunidade}. */
    public Map<String, Comunidade> comunidades = new LinkedHashMap<>();

    /**
     * Construtor da classe {@code ComunidadeService}.
     *
     * @param usuarioService Servi�o de usu�rios.
     * @param sessaoService Servi�o de sess�es.
     */
    public ComunidadeService(UsuarioService usuarioService, SessaoService sessaoService) {
        this.usuarioService = usuarioService;
        this.sessaoService = sessaoService;
        this.comunidades = loadComunidadesFromFile();
    }

    /**
     * Remove todas as comunidades criadas por um determinado usu�rio.
     * Remove tamb�m a comunidade da lista de todos os membros.
     *
     * @param login Login do usu�rio dono das comunidades.
     * @throws UsuarioNaoCadastradoException Se o usu�rio n�o estiver cadastrado.
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
     * @param idSessao ID da sess�o do usu�rio que criar� a comunidade.
     * @param nome Nome da comunidade.
     * @param descricao Descri��o da comunidade.
     * @throws UsuarioException Se o usu�rio n�o estiver autenticado.
     * @throws ComunidadeNomeExisteException Se j� existir uma comunidade com esse nome.
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
     * Retorna a descri��o de uma comunidade.
     *
     * @param nome Nome da comunidade.
     * @return A descri��o da comunidade.
     * @throws ComunidadeNaoExisteException Se a comunidade n�o existir.
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
     * @throws ComunidadeNaoExisteException Se a comunidade n�o existir.
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
     * @throws ComunidadeNaoExisteException Se a comunidade n�o existir.
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
     * Remove todas as comunidades do sistema (reseta o servi�o).
     */
    public void zerar() {
        comunidades.clear();
        saveComunidadesToFile();
    }

    /**
     * Retorna a lista de nomes de comunidades das quais o usu�rio participa.
     *
     * @param login Login do usu�rio.
     * @return Lista de nomes das comunidades.
     * @throws UsuarioNaoCadastradoException Se o usu�rio n�o existir.
     */
    public List<String> getComunidades(String login) throws UsuarioNaoCadastradoException {
        Usuario usuario = usuarioService.getUsuario(login);
        if (usuario == null) throw new UsuarioNaoCadastradoException();
        return usuario.comunidades;
    }

    /**
     * Adiciona um usu�rio autenticado a uma comunidade existente.
     *
     * @param idSessao ID da sess�o do usu�rio.
     * @param nomeComunidade Nome da comunidade.
     * @throws UsuarioException Se o usu�rio n�o estiver autenticado.
     * @throws ComunidadeNaoExisteException Se a comunidade n�o existir.
     * @throws UsuarioJaFazParteComunidadeException Se o usu�rio j� participa da comunidade.
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
