package br.fsa.faculdade;

import br.fsa.pessoas.Aluno;

public class Nota {
    private Aluno aluno; // Referência ao objeto Aluno
    private Materia materia; // Referência ao objeto Materia
    private String codigoAluno; // Código do aluno (para persistência e ligação)
    private String codigoMateria; // Código da matéria (para persistência e ligação)
    private String tipoAvaliacao; // Ex: "P1", "P2", "Trabalho"
    private double valorNota;

    // Construtor padrão
    public Nota() {
    }

    // Construtor com todos os parâmetros
    public Nota(Aluno aluno, Materia materia, String tipoAvaliacao, double valorNota) {
        this.aluno = aluno;
        this.materia = materia;
        this.codigoAluno = (aluno != null) ? aluno.getCodigoUnico() : null;
        this.codigoMateria = (materia != null) ? materia.getCodigoUnico() : null;
        this.tipoAvaliacao = tipoAvaliacao;
        this.valorNota = valorNota;
    }

    // Getters e Setters
    public Aluno getAluno() {
        return aluno;
    }

    public void setAluno(Aluno aluno) {
        this.aluno = aluno;
        // Atualiza o código do aluno quando o objeto aluno é definido
        this.codigoAluno = (aluno != null) ? aluno.getCodigoUnico() : null;
    }

    public Materia getMateria() {
        return materia;
    }

    public void setMateria(Materia materia) {
        this.materia = materia;
        // Atualiza o código da matéria quando o objeto matéria é definido
        this.codigoMateria = (materia != null) ? materia.getCodigoUnico() : null;
    }

    public String getCodigoAluno() {
        // Retorna o código armazenado, mesmo que o objeto aluno ainda não esteja ligado
        return codigoAluno;
    }

    public void setCodigoAluno(String codigoAluno) {
        // Permite definir o código do aluno (lido do CSV)
        this.codigoAluno = codigoAluno;
        // Não tenta buscar o objeto Aluno aqui, isso é feito em GerenciaFaculdade
    }

    public String getCodigoMateria() {
        // Retorna o código armazenado, mesmo que o objeto matéria ainda não esteja ligado
        return codigoMateria;
    }

    public void setCodigoMateria(String codigoMateria) {
        // Permite definir o código da matéria (lido do CSV)
        this.codigoMateria = codigoMateria;
        // Não tenta buscar o objeto Materia aqui, isso é feito em GerenciaFaculdade
    }

    public String getTipoAvaliacao() {
        return tipoAvaliacao;
    }

    public void setTipoAvaliacao(String tipoAvaliacao) {
        this.tipoAvaliacao = tipoAvaliacao;
    }

    public double getValorNota() {
        return valorNota;
    }

    public void setValorNota(double valorNota) {
        this.valorNota = valorNota;
    }

    @Override
    public String toString() {
        String infoAluno = "N/A";
        if (aluno != null) {
            infoAluno = aluno.getNome() + " (" + aluno.getCodigoUnico() + ")";
        } else if (codigoAluno != null && !codigoAluno.isEmpty()){
             infoAluno = "Código: " + codigoAluno + " (não carregado)";
        }

        String infoMateria = "N/A";
         if (materia != null) {
            infoMateria = materia.getNome() + " (" + materia.getCodigoUnico() + ")";
        } else if (codigoMateria != null && !codigoMateria.isEmpty()){
             infoMateria = "Código: " + codigoMateria + " (não carregada)";
        }

        return "Nota [Aluno: " + infoAluno + ", Matéria: " + infoMateria + ", Tipo: " + tipoAvaliacao + ", Valor: " + valorNota + "]";
    }

    // Métodos toCsvString e fromCsvString removidos daqui, pois a lógica está no DAO
}

