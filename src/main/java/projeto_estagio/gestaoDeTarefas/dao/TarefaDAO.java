package projeto_estagio.gestaoDeTarefas.dao;

import projeto_estagio.gestaoDeTarefas.model.Tarefa;
import projeto_estagio.gestaoDeTarefas.model.Responsavel;
import projeto_estagio.gestaoDeTarefas.enums.Status;
import projeto_estagio.gestaoDeTarefas.enums.Prioridade;

import javax.persistence.TypedQuery;
import java.util.List;

public class TarefaDAO extends GenericDAO<Tarefa> {

    public TarefaDAO() {
        super(Tarefa.class);
    }

    public List<Tarefa> buscarPorResponsavel(Responsavel responsavel) {
        String jpql = "SELECT t FROM Tarefa t WHERE t.responsavel = :responsavel";
        TypedQuery<Tarefa> query = entityManager.createQuery(jpql, Tarefa.class);
        query.setParameter("responsavel", responsavel);
        return query.getResultList();
    }

    public List<Tarefa> buscarPorStatus(Status status) {
        String jpql = "SELECT t FROM Tarefa t WHERE t.status = :status";
        TypedQuery<Tarefa> query = entityManager.createQuery(jpql, Tarefa.class);
        query.setParameter("status", status);
        return query.getResultList();
    }

    public List<Tarefa> buscarPorPrioridade(Prioridade prioridade) {
        String jpql = "SELECT t FROM Tarefa t WHERE t.prioridade = :prioridade";
        TypedQuery<Tarefa> query = entityManager.createQuery(jpql, Tarefa.class);
        query.setParameter("prioridade", prioridade);
        return query.getResultList();
    }

    public List<Tarefa> buscarPorTitulo(String titulo) {
        String jpql = "SELECT t FROM Tarefa t WHERE LOWER(t.titulo) LIKE LOWER(:titulo)";
        TypedQuery<Tarefa> query = entityManager.createQuery(jpql, Tarefa.class);
        query.setParameter("titulo", "%" + titulo + "%");
        return query.getResultList();
    }

    public List<Tarefa> buscarTarefasVencidas() {
        String jpql = "SELECT t FROM Tarefa t WHERE t.deadline < CURRENT_TIMESTAMP AND t.status != :status";
        TypedQuery<Tarefa> query = entityManager.createQuery(jpql, Tarefa.class);
        query.setParameter("status", Status.CONCLUIDO);
        return query.getResultList();
    }
}
