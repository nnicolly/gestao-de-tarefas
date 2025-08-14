package projeto_estagio.gestaoDeTarefas.service;

import projeto_estagio.gestaoDeTarefas.dao.TarefaDAO;
import projeto_estagio.gestaoDeTarefas.model.Tarefa;
import projeto_estagio.gestaoDeTarefas.model.Responsavel;
import projeto_estagio.gestaoDeTarefas.enums.Status;
import projeto_estagio.gestaoDeTarefas.enums.Prioridade;

import java.util.List;

public class TarefaService {

    private TarefaDAO tarefaDAO;

    public TarefaService() {
        this.tarefaDAO = new TarefaDAO();
    }

    public void criarTarefa(Tarefa tarefa) {
        if (tarefa.getTitulo() == null || tarefa.getTitulo().trim().isEmpty()) {
            throw new IllegalArgumentException("O título da tarefa é obrigatório");
        }

        if (tarefa.getStatus() == null) {
            tarefa.setStatus(Status.EM_ANDAMENTO);
        }

        tarefaDAO.salvar(tarefa);
    }

    public void atualizarTarefa(Tarefa tarefa) {
        if (tarefa.getId() == null) {
            throw new IllegalArgumentException("ID da tarefa é obrigatório para atualização");
        }

        if (tarefa.getTitulo() == null || tarefa.getTitulo().trim().isEmpty()) {
            throw new IllegalArgumentException("O título da tarefa é obrigatório");
        }

        tarefaDAO.atualizar(tarefa);
    }

    public void deletarTarefa(Long id) {
        Tarefa tarefa = tarefaDAO.buscarPorId(id);
        if (tarefa != null) {
            tarefaDAO.deletar(tarefa);
        } else {
            throw new IllegalArgumentException("Tarefa não encontrada");
        }
    }

    public Tarefa buscarTarefaPorId(Long id) {
        return tarefaDAO.buscarPorId(id);
    }

    public List<Tarefa> listarTodasTarefas() {
        return tarefaDAO.buscarTodos();
    }

    public List<Tarefa> buscarTarefasPorResponsavel(Responsavel responsavel) {
        return tarefaDAO.buscarPorResponsavel(responsavel);
    }

    public List<Tarefa> buscarTarefasPorStatus(Status status) {
        return tarefaDAO.buscarPorStatus(status);
    }

    public List<Tarefa> buscarTarefasPorPrioridade(Prioridade prioridade) {
        return tarefaDAO.buscarPorPrioridade(prioridade);
    }

    public List<Tarefa> buscarTarefasPorTitulo(String titulo) {
        return tarefaDAO.buscarPorTitulo(titulo);
    }

    public List<Tarefa> buscarTarefasVencidas() {
        return tarefaDAO.buscarTarefasVencidas();
    }

    public void concluirTarefa(Long id) {
        Tarefa tarefa = tarefaDAO.buscarPorId(id);
        if (tarefa != null) {
            tarefa.setStatus(Status.CONCLUIDO);
            tarefaDAO.atualizar(tarefa);
        } else {
            throw new IllegalArgumentException("Tarefa não encontrada");
        }
    }

    public void atribuirResponsavel(Long tarefaId, Responsavel responsavel) {
        Tarefa tarefa = tarefaDAO.buscarPorId(tarefaId);
        if (tarefa != null) {
            tarefa.setResponsavel(responsavel);
            tarefaDAO.atualizar(tarefa);
        } else {
            throw new IllegalArgumentException("Tarefa não encontrada");
        }
    }

    public void fecharConexao() {
        tarefaDAO.fecharConexao();
    }

    public List<Tarefa> buscarComFiltros(Long id, String texto, Responsavel responsavel, Status status) {
        // Se nenhum filtro foi aplicado, retorna todas
        if (id == null && (texto == null || texto.trim().isEmpty()) &&
            responsavel == null && status == null) {
            return listarTodasTarefas();
        }

        // Aplica filtros específicos
        List<Tarefa> resultado = listarTodasTarefas();

        if (id != null) {
            resultado.removeIf(tarefa -> !tarefa.getId().equals(id));
        }

        if (texto != null && !texto.trim().isEmpty()) {
            String textoLower = texto.toLowerCase();
            resultado.removeIf(tarefa -> {
                boolean tituloMatch = tarefa.getTitulo() != null &&
                    tarefa.getTitulo().toLowerCase().contains(textoLower);
                boolean descricaoMatch = tarefa.getDescricao() != null &&
                    tarefa.getDescricao().toLowerCase().contains(textoLower);
                return !tituloMatch && !descricaoMatch;
            });
        }

        if (responsavel != null && responsavel.getId() != null) {
            resultado.removeIf(tarefa ->
                tarefa.getResponsavel() == null ||
                !tarefa.getResponsavel().getId().equals(responsavel.getId())
            );
        }

        if (status != null) {
            resultado.removeIf(tarefa -> !tarefa.getStatus().equals(status));
        }

        return resultado;
    }
}
