package projeto_estagio.gestaoDeTarefas.enums;

public enum Status {
    EM_ANDAMENTO("Em Andamento"),
    CONCLUIDO("Concluído");

    private final String descricao;

    Status(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }

    @Override
    public String toString() {
        return descricao;
    }
}
