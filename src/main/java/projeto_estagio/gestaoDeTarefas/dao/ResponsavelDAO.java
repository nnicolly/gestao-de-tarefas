package projeto_estagio.gestaoDeTarefas.dao;

import projeto_estagio.gestaoDeTarefas.model.Responsavel;

import javax.persistence.TypedQuery;
import java.util.List;

public class ResponsavelDAO extends GenericDAO<Responsavel> {

    public ResponsavelDAO() {
        super(Responsavel.class);
    }

    public Responsavel buscarPorCpf(String cpf) {
        String jpql = "SELECT r FROM Responsavel r WHERE r.cpf = :cpf";
        TypedQuery<Responsavel> query = entityManager.createQuery(jpql, Responsavel.class);
        query.setParameter("cpf", cpf);
        try {
            return query.getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }

    public List<Responsavel> buscarPorNome(String nome) {
        String jpql = "SELECT r FROM Responsavel r WHERE LOWER(r.nome) LIKE LOWER(:nome)";
        TypedQuery<Responsavel> query = entityManager.createQuery(jpql, Responsavel.class);
        query.setParameter("nome", "%" + nome + "%");
        return query.getResultList();
    }

    public boolean existePorCpf(String cpf) {
        String jpql = "SELECT COUNT(r) FROM Responsavel r WHERE r.cpf = :cpf";
        TypedQuery<Long> query = entityManager.createQuery(jpql, Long.class);
        query.setParameter("cpf", cpf);
        return query.getSingleResult() > 0;
    }
}
