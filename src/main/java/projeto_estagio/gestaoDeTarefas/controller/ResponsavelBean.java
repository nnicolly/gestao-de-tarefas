package projeto_estagio.gestaoDeTarefas.controller;

import projeto_estagio.gestaoDeTarefas.model.Responsavel;
import projeto_estagio.gestaoDeTarefas.service.ResponsavelService;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import java.io.Serializable;
import java.util.List;

@ManagedBean(name = "responsavelBean")
@ViewScoped
public class ResponsavelBean implements Serializable {

    private static final long serialVersionUID = 1L;

    // Service
    private ResponsavelService responsavelService;

    // Listas
    private List<Responsavel> responsaveis;

    // Objeto para cadastro/edição
    private Responsavel responsavel;

    // ID para edição
    private Long responsavelId;

    // ID temporário para navegação
    private Long idParaEdicao;

    public ResponsavelBean() {
        init();
    }

    public void init() {
        responsavelService = new ResponsavelService();
        responsavel = new Responsavel();
        carregarDados();
    }

    public void carregarDados() {
        try {
            responsaveis = responsavelService.listarTodosResponsaveis();
        } catch (Exception e) {
            addMessage(FacesMessage.SEVERITY_ERROR, "Erro ao carregar dados: " + e.getMessage());
        }
    }

    // Método chamado ao entrar na página de formulário para edição
    public void carregarParaEdicao() {
        if (responsavelId != null) {
            try {
                responsavel = responsavelService.buscarResponsavelPorId(responsavelId);
                if (responsavel == null) {
                    responsavel = new Responsavel();
                    addMessage(FacesMessage.SEVERITY_ERROR, "Responsável não encontrado.");
                }
            } catch (Exception e) {
                responsavel = new Responsavel();
                addMessage(FacesMessage.SEVERITY_ERROR, "Erro ao carregar responsável: " + e.getMessage());
            }
        } else {
            responsavel = new Responsavel(); // Novo cadastro
        }
    }

    public String salvarResponsavel() {
        try {
            if (responsavel.getId() == null) {
                // Criação
                responsavelService.criarResponsavel(responsavel);
                addMessage(FacesMessage.SEVERITY_INFO, "Responsável cadastrado com sucesso!");
            } else {
                // Edição
                responsavelService.atualizarResponsavel(responsavel);
                addMessage(FacesMessage.SEVERITY_INFO, "Responsável atualizado com sucesso!");
            }

            // Redireciona de volta para a listagem
            return "responsaveis-lista?faces-redirect=true";

        } catch (Exception e) {
            addMessage(FacesMessage.SEVERITY_ERROR, "Erro ao salvar responsável: " + e.getMessage());
            return null; // Permanece na página atual
        }
    }

    public String excluirResponsavel(Long id) {
        try {
            responsavelService.deletarResponsavel(id);
            addMessage(FacesMessage.SEVERITY_INFO, "Responsável excluído com sucesso!");
            carregarDados(); // Recarrega a lista
            return null; // Permanece na página atual
        } catch (Exception e) {
            addMessage(FacesMessage.SEVERITY_ERROR, "Erro ao excluir responsável: " + e.getMessage());
            return null;
        }
    }

    public String cancelar() {
        return "responsaveis-lista?faces-redirect=true";
    }

    public String editarResponsavel(Long id) {
        return "responsaveis-form?faces-redirect=true&id=" + id;
    }

    public String novoResponsavel() {
        return "responsaveis-form?faces-redirect=true";
    }

    private void addMessage(FacesMessage.Severity severity, String message) {
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(severity, message, null));
    }

    // Getters e Setters
    public List<Responsavel> getResponsaveis() {
        return responsaveis;
    }

    public void setResponsaveis(List<Responsavel> responsaveis) {
        this.responsaveis = responsaveis;
    }

    public Responsavel getResponsavel() {
        return responsavel;
    }

    public void setResponsavel(Responsavel responsavel) {
        this.responsavel = responsavel;
    }

    public Long getResponsavelId() {
        return responsavelId;
    }

    public void setResponsavelId(Long responsavelId) {
        this.responsavelId = responsavelId;
    }

    public Long getIdParaEdicao() {
        return idParaEdicao;
    }

    public void setIdParaEdicao(Long idParaEdicao) {
        this.idParaEdicao = idParaEdicao;
    }

    public boolean isEdicao() {
        return responsavel != null && responsavel.getId() != null;
    }
}
