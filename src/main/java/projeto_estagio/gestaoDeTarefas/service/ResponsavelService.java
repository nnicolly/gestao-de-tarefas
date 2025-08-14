package projeto_estagio.gestaoDeTarefas.service;

import projeto_estagio.gestaoDeTarefas.dao.ResponsavelDAO;
import projeto_estagio.gestaoDeTarefas.model.Responsavel;

import java.util.List;

public class ResponsavelService {

    private ResponsavelDAO responsavelDAO;

    public ResponsavelService() {
        this.responsavelDAO = new ResponsavelDAO();
    }

    public void criarResponsavel(Responsavel responsavel) {
        if (responsavel.getNome() == null || responsavel.getNome().trim().isEmpty()) {
            throw new IllegalArgumentException("O nome do responsável é obrigatório");
        }

        if (responsavel.getCpf() != null && !responsavel.getCpf().trim().isEmpty()) {
            if (responsavelDAO.existePorCpf(responsavel.getCpf())) {
                throw new IllegalArgumentException("Já existe um responsável com este CPF");
            }
        }

        responsavelDAO.salvar(responsavel);
    }

    public void atualizarResponsavel(Responsavel responsavel) {
        if (responsavel.getId() == null) {
            throw new IllegalArgumentException("ID do responsável é obrigatório para atualização");
        }

        if (responsavel.getNome() == null || responsavel.getNome().trim().isEmpty()) {
            throw new IllegalArgumentException("O nome do responsável é obrigatório");
        }

        if (responsavel.getCpf() != null && !responsavel.getCpf().trim().isEmpty()) {
            Responsavel responsavelExistente = responsavelDAO.buscarPorCpf(responsavel.getCpf());
            if (responsavelExistente != null && !responsavelExistente.getId().equals(responsavel.getId())) {
                throw new IllegalArgumentException("Já existe outro responsável com este CPF");
            }
        }

        responsavelDAO.atualizar(responsavel);
    }

    public void deletarResponsavel(Long id) {
        Responsavel responsavel = responsavelDAO.buscarPorId(id);
        if (responsavel != null) {
            responsavelDAO.deletar(responsavel);
        } else {
            throw new IllegalArgumentException("Responsável não encontrado");
        }
    }

    public Responsavel buscarResponsavelPorId(Long id) {
        return responsavelDAO.buscarPorId(id);
    }

    public List<Responsavel> listarTodosResponsaveis() {
        return responsavelDAO.buscarTodos();
    }

    public Responsavel buscarResponsavelPorCpf(String cpf) {
        return responsavelDAO.buscarPorCpf(cpf);
    }

    public List<Responsavel> buscarResponsaveisPorNome(String nome) {
        return responsavelDAO.buscarPorNome(nome);
    }

    public boolean existeResponsavelComCpf(String cpf) {
        return responsavelDAO.existePorCpf(cpf);
    }

    public void fecharConexao() {
        responsavelDAO.fecharConexao();
    }
}
