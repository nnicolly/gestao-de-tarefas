package projeto_estagio.gestaoDeTarefas.controller;

import projeto_estagio.gestaoDeTarefas.model.Tarefa;
import projeto_estagio.gestaoDeTarefas.model.Responsavel;
import projeto_estagio.gestaoDeTarefas.service.TarefaService;
import projeto_estagio.gestaoDeTarefas.service.ResponsavelService;
import projeto_estagio.gestaoDeTarefas.enums.Status;
import projeto_estagio.gestaoDeTarefas.enums.Prioridade;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import java.io.Serializable;
import java.util.List;
import java.util.ArrayList;
import java.util.Date;

@ManagedBean(name = "tarefaListaBean")
@ViewScoped
public class TarefaListaBean implements Serializable {

    private static final long serialVersionUID = 1L;

    // Services
    private TarefaService tarefaService;
    private ResponsavelService responsavelService;

    // Listas
    private List<Tarefa> tarefas;
    private List<Tarefa> tarefasFiltradas;
    private List<Responsavel> responsaveis;

    // Filtros
    private Long filtroId;
    private String filtroTexto; // Para título/descrição
    private Long filtroResponsavelId; // Mudança: usar ID em vez do objeto completo
    private Status filtroStatus;

    // Tarefa selecionada
    private Tarefa tarefaSelecionada;

    // ID para edição
    private Long tarefaId;

    // ID do responsável selecionado para evitar problemas de converter
    private Long responsavelSelecionadoId;

    public TarefaListaBean() {
        init();
    }

    public void init() {
        tarefaService = new TarefaService();
        responsavelService = new ResponsavelService();
        carregarDados();
    }

    public void carregarDados() {
        try {
            tarefas = tarefaService.listarTodasTarefas();
            aplicarFiltros(); // Aplica filtros automaticamente após carregar
            responsaveis = responsavelService.listarTodosResponsaveis();
        } catch (Exception e) {
            addMessage(FacesMessage.SEVERITY_ERROR, "Erro ao carregar dados: " + e.getMessage());
        }
    }

    public void aplicarFiltros() {
        if (tarefas == null) {
            tarefasFiltradas = new ArrayList<>();
            return;
        }

        tarefasFiltradas = new ArrayList<>(tarefas);

        // Filtro por ID - com tratamento de erro para números inválidos
        if (filtroId != null) {
            tarefasFiltradas.removeIf(tarefa -> !tarefa.getId().equals(filtroId));
        }

        // Filtro por texto (título ou descrição)
        if (filtroTexto != null && !filtroTexto.trim().isEmpty()) {
            String textoLower = filtroTexto.toLowerCase().trim();
            int tamanhoAntes = tarefasFiltradas.size();
            tarefasFiltradas.removeIf(tarefa -> {
                boolean tituloMatch = tarefa.getTitulo() != null &&
                    tarefa.getTitulo().toLowerCase().contains(textoLower);
                boolean descricaoMatch = tarefa.getDescricao() != null &&
                    tarefa.getDescricao().toLowerCase().contains(textoLower);
                return !tituloMatch && !descricaoMatch;
            });
        }

        // Filtro por responsável - melhor validação
        if (filtroResponsavelId != null && filtroResponsavelId > 0) {
            int tamanhoAntes = tarefasFiltradas.size();
            tarefasFiltradas.removeIf(tarefa ->
                tarefa.getResponsavel() == null ||
                !tarefa.getResponsavel().getId().equals(filtroResponsavelId)
            );
        }

        // Filtro por status
        if (filtroStatus != null) {
            int tamanhoAntes = tarefasFiltradas.size();
            tarefasFiltradas.removeIf(tarefa -> !tarefa.getStatus().equals(filtroStatus));
        }
    }


    public void limparFiltros() {
        System.out.println("=== LIMPAR FILTROS INICIADO ===");
        System.out.println("Valores antes de limpar:");
        System.out.println("filtroId: " + filtroId);
        System.out.println("filtroTexto: " + filtroTexto);
        System.out.println("filtroResponsavel: " + filtroResponsavelId);
        System.out.println("filtroStatus: " + filtroStatus);

        filtroId = null;
        filtroTexto = null;
        filtroResponsavelId = null;
        filtroStatus = null;

        System.out.println("Valores após limpar - todos devem ser null:");
        System.out.println("filtroId: " + filtroId);
        System.out.println("filtroTexto: " + filtroTexto);
        System.out.println("filtroResponsavel: " + filtroResponsavelId);
        System.out.println("filtroStatus: " + filtroStatus);

        aplicarFiltros();
        System.out.println("=== LIMPAR FILTROS FINALIZADO ===");
    }

    public String novaTarefa() {
        return "tarefas-form?faces-redirect=true";
    }

    public String editarTarefa(Long id) {
        return "tarefas-form?faces-redirect=true&id=" + id;
    }

    // Método chamado ao entrar na página de formulário para edição
    public void carregarParaEdicao() {
        if (tarefaId != null) {
            try {
                tarefaSelecionada = tarefaService.buscarTarefaPorId(tarefaId);
                if (tarefaSelecionada == null) {
                    tarefaSelecionada = new Tarefa();
                    addMessage(FacesMessage.SEVERITY_ERROR, "Tarefa não encontrada.");
                } else {
                    // Carrega o ID do responsável se existir
                    if (tarefaSelecionada.getResponsavel() != null) {
                        responsavelSelecionadoId = tarefaSelecionada.getResponsavel().getId();
                    }
                }
            } catch (Exception e) {
                tarefaSelecionada = new Tarefa();
                addMessage(FacesMessage.SEVERITY_ERROR, "Erro ao carregar tarefa: " + e.getMessage());
            }
        } else {
            tarefaSelecionada = new Tarefa(); // Nova tarefa
            responsavelSelecionadoId = null;
        }
    }

    public String salvarTarefa() {
        try {
            // Se um responsável foi selecionado, busca o objeto completo
            if (responsavelSelecionadoId != null && responsavelSelecionadoId > 0) {
                Responsavel responsavel = responsavelService.buscarResponsavelPorId(responsavelSelecionadoId);
                tarefaSelecionada.setResponsavel(responsavel);
            } else {
                tarefaSelecionada.setResponsavel(null);
            }

            if (tarefaSelecionada.getId() == null) {
                // Criação
                tarefaService.criarTarefa(tarefaSelecionada);
                addMessage(FacesMessage.SEVERITY_INFO, "Tarefa cadastrada com sucesso!");
            } else {
                // Edição
                tarefaService.atualizarTarefa(tarefaSelecionada);
                addMessage(FacesMessage.SEVERITY_INFO, "Tarefa atualizada com sucesso!");
            }

            // Redireciona de volta para a listagem
            return "tarefas-lista?faces-redirect=true";

        } catch (Exception e) {
            addMessage(FacesMessage.SEVERITY_ERROR, "Erro ao salvar tarefa: " + e.getMessage());
            return null; // Permanece na página atual
        }
    }

    public String cancelarTarefa() {
        return "tarefas-lista?faces-redirect=true";
    }

    public void excluirTarefa(Tarefa tarefa) {
        try {
            tarefaService.deletarTarefa(tarefa.getId());
            carregarDados();
            addMessage(FacesMessage.SEVERITY_INFO, "Tarefa excluída com sucesso!");
        } catch (Exception e) {
            addMessage(FacesMessage.SEVERITY_ERROR, "Erro ao excluir tarefa: " + e.getMessage());
        }
    }

    public void concluirTarefa(Tarefa tarefa) {
        try {
            tarefaService.concluirTarefa(tarefa.getId());
            carregarDados();
            aplicarFiltros();
            addMessage(FacesMessage.SEVERITY_INFO, "Tarefa concluída com sucesso!");
        } catch (Exception e) {
            addMessage(FacesMessage.SEVERITY_ERROR, "Erro ao concluir tarefa: " + e.getMessage());
        }
    }

    private void addMessage(FacesMessage.Severity severity, String message) {
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(severity, message, null));
    }

    // Métodos auxiliares para a interface
    public Status[] getStatusValues() {
        return Status.values();
    }

    public Prioridade[] getPrioridadeValues() {
        return Prioridade.values();
    }

    public String getStatusLabel(Status status) {
        return status != null ? status.getDescricao() : "";
    }

    public String getPrioridadeLabel(Prioridade prioridade) {
        return prioridade != null ? prioridade.getDescricao() : "";
    }

    // Getters e Setters
    public List<Tarefa> getTarefas() {
        return tarefas;
    }

    public void setTarefas(List<Tarefa> tarefas) {
        this.tarefas = tarefas;
    }

    public List<Tarefa> getTarefasFiltradas() {
        return tarefasFiltradas;
    }

    public void setTarefasFiltradas(List<Tarefa> tarefasFiltradas) {
        this.tarefasFiltradas = tarefasFiltradas;
    }

    public List<Responsavel> getResponsaveis() {
        return responsaveis;
    }

    public void setResponsaveis(List<Responsavel> responsaveis) {
        this.responsaveis = responsaveis;
    }

    public Long getFiltroId() {
        return filtroId;
    }

    public void setFiltroId(Long filtroId) {
        System.out.println("SETTER filtroId chamado: " + filtroId);
        this.filtroId = filtroId;
    }

    public String getFiltroTexto() {
        return filtroTexto;
    }

    public void setFiltroTexto(String filtroTexto) {
        System.out.println("SETTER filtroTexto chamado: " + filtroTexto);
        this.filtroTexto = filtroTexto;
    }

    public Long getFiltroResponsavelId() {
        return filtroResponsavelId;
    }

    public void setFiltroResponsavelId(Long filtroResponsavelId) {
        System.out.println("SETTER filtroResponsavel chamado: " + filtroResponsavelId);
        this.filtroResponsavelId = filtroResponsavelId;
    }

    public Status getFiltroStatus() {
        return filtroStatus;
    }

    public void setFiltroStatus(Status filtroStatus) {
        System.out.println("SETTER filtroStatus chamado: " + filtroStatus);
        this.filtroStatus = filtroStatus;
    }

    public Tarefa getTarefaSelecionada() {
        return tarefaSelecionada;
    }

    public void setTarefaSelecionada(Tarefa tarefaSelecionada) {
        this.tarefaSelecionada = tarefaSelecionada;
    }

    public Long getTarefaId() {
        return tarefaId;
    }

    public void setTarefaId(Long tarefaId) {
        this.tarefaId = tarefaId;
    }

    public Long getResponsavelSelecionadoId() {
        return responsavelSelecionadoId;
    }

    public void setResponsavelSelecionadoId(Long responsavelSelecionadoId) {
        this.responsavelSelecionadoId = responsavelSelecionadoId;
    }

    public boolean isEdicaoTarefa() {
        return tarefaSelecionada != null && tarefaSelecionada.getId() != null;
    }

    public Date getDataAtual() {
        return new Date();
    }

    // Método para limpar um campo de filtro específico
    public void limparFiltroId() {
        this.filtroId = null;
        aplicarFiltros();
    }

    public void limparFiltroTexto() {
        this.filtroTexto = null;
        aplicarFiltros();
    }

    public void limparFiltroResponsavel() {
        this.filtroResponsavelId = null;
        aplicarFiltros();
    }

    public void limparFiltroStatus() {
        this.filtroStatus = null;
        aplicarFiltros();
    }

    // Método para verificar se há filtros ativos
    public boolean hasFiltrosAtivos() {
        return filtroId != null ||
               (filtroTexto != null && !filtroTexto.trim().isEmpty()) ||
               filtroResponsavelId != null ||
               filtroStatus != null;
    }

    // Método para obter contagem total de tarefas
    public int getTotalTarefas() {
        return tarefas != null ? tarefas.size() : 0;
    }

    // Método para obter contagem de tarefas filtradas
    public int getTotalTarefasFiltradas() {
        return tarefasFiltradas != null ? tarefasFiltradas.size() : 0;
    }
}
