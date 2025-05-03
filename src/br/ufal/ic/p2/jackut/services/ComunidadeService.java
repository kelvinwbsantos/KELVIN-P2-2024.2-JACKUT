package br.ufal.ic.p2.jackut.services;

import br.ufal.ic.p2.jackut.models.entities.Comunidade;
import br.ufal.ic.p2.jackut.models.entities.Usuario;
import br.ufal.ic.p2.jackut.models.exceptions.*;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.*;

public class ComunidadeService {
    private UsuarioService usuarioService;
    private SessaoService sessaoService;
    public Map<String, Comunidade> comunidades = new LinkedHashMap<>(); // Usando Map para associar nome à comunidade

    public ComunidadeService(UsuarioService usuarioService, SessaoService sessaoService) {
        this.usuarioService = usuarioService;
        this.sessaoService = sessaoService;
        this.comunidades = loadComunidadesFromFile();
    }

    public void removerComunidade(String login) throws UsuarioNaoCadastradoException {
        // Get the user to verify existence
        Usuario usuario = usuarioService.getUsuario(login);
        if (usuario == null) {
            throw new UsuarioNaoCadastradoException();
        }

        // Get all communities the user is part of
        List<String> comunidadesUsuario = new ArrayList<>(usuario.comunidades);

        for (String nomeComunidade : comunidadesUsuario) {
            Comunidade comunidade = comunidades.get(nomeComunidade);

            // Only process if community exists and user is the owner
            if (comunidade != null && comunidade.getloginDono().equals(login)) {
                // Remove community from all members' profiles
                for (String participante : new HashSet<>(comunidade.getMembrosComunidade())) {
                    Usuario user = usuarioService.usuarios.get(participante);
                    if (user != null) {
                        user.comunidades.remove(nomeComunidade);
                    }
                }

                // Remove the community itself
                comunidades.remove(nomeComunidade);

                // Remove from owner's list too
                usuario.comunidades.remove(nomeComunidade);
            }
        }

        // Save changes
        saveComunidadesToFile();
        usuarioService.saveUsuariosToFile();
    }

    public void criarComunidade(String idSessao, String nome, String descricao) throws UsuarioException, ComunidadeNomeExisteException {
        Usuario dono = sessaoService.getUsuarioPorSessao(idSessao);
        String loginDono = dono.getLogin();

        if (!comunidades.containsKey(nome)) {
            Comunidade comunidade = new Comunidade(loginDono, nome, descricao);
            comunidades.put(nome, comunidade);
            dono.comunidades.add(nome);
            saveComunidadesToFile();
        }
        else {
            throw new ComunidadeNomeExisteException();
        }
    }

    public String getDescricaoComunidade(String nome) throws ComunidadeNaoExisteException {
        Comunidade comunidade = comunidades.get(nome);

        if (comunidade == null) {
            throw new ComunidadeNaoExisteException();
        }

        return comunidade.getDescricao();
    }

    public String getDonoComunidade(String nome) throws ComunidadeNaoExisteException {
        Comunidade comunidade = comunidades.get(nome);

        if (comunidade == null) {
            throw new ComunidadeNaoExisteException();
        }

        return comunidade.getloginDono();
    }

    public Set<String> getMembrosComunidade(String nome) throws ComunidadeNaoExisteException {
        Comunidade comunidade = comunidades.get(nome);

        if (comunidade == null) {
            throw new ComunidadeNaoExisteException();
        }

        Set<String> membros = comunidade.getMembrosComunidade();

        return membros;
    }

    public void saveComunidadesToFile() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("comunidades.ser"))) {
            oos.writeObject(comunidades); // Salva o mapa inteiro
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("unchecked")
    private Map<String, Comunidade> loadComunidadesFromFile() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream("comunidades.ser"))) {
            return (Map<String, Comunidade>) ois.readObject(); // Carrega o mapa de comunidades
        } catch (Exception e) {
            return new LinkedHashMap<>(); // Retorna um mapa vazio se não houver dados
        }
    }

    public void zerar() {
        comunidades.clear();
        saveComunidadesToFile();
    }

    public List<String> getComunidades(String login) throws UsuarioNaoCadastradoException {
        Usuario usuario = usuarioService.getUsuario(login);

        if (usuario == null) {
            throw new UsuarioNaoCadastradoException();
        }

        return usuario.comunidades; // mudar
    }




    public void adicionarComunidade(String idSessao, String nomeComunidade) throws UsuarioException, ComunidadeNaoExisteException, UsuarioJaFazParteComunidadeException {
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

        saveComunidadesToFile(); // Salva alteração
    }


}
