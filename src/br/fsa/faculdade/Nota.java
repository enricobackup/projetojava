package br.fsa.faculdade;

import br.fsa.pessoas.Aluno;

public class Nota {
    private Aluno aluno; // referencia ao objeto Aluno
    private Materia materia; // referencia ao objeto Materia
    private String codigoAluno; // codigo do aluno (pra persistencia e ligacao)
    private String codigoMateria; // codigo da materia (pra persistencia e ligacao)
    private String tipoAvaliacao; // ex: "p1", "p2", "trabalho"
    private double valorNota;

    // construtor padrao
    public Nota() {
    }

    // construtor com todos os parametros
    public Nota(Aluno aluno, Materia materia, String tipoAvaliacao, double valorNota) {
        this.aluno = aluno;
        this.materia = materia;
        this.codigoAluno = (aluno != null) ? aluno.getCodigoUnico() : null;
        this.codigoMateria = (materia != null) ? materia.getCodigoUnico() : null;
        this.tipoAvaliacao = tipoAvaliacao;
        this.valorNota = valorNota;
    }

    // getters e setters
    public Aluno getAluno() {
        return aluno;
    }

    public void setAluno(Aluno aluno) {
        this.aluno = aluno;
        // atualiza o codigo do aluno quando o objeto aluno é definido
        this.codigoAluno = (aluno != null) ? aluno.getCodigoUnico() : null;
    }

    public Materia getMateria() {
        return materia;
    }

    public void setMateria(Materia materia) {
        this.materia = materia;
        // atualiza o codigo da materia quando o objeto materia é definido
        this.codigoMateria = (materia != null) ? materia.getCodigoUnico() : null;
    }

    public String getCodigoAluno() {
        // retorna o codigo armazenado, mesmo que o objeto aluno ainda nao esteja ligado
        return codigoAluno;
    }

    public void setCodigoAluno(String codigoAluno) {
        // permite definir o codigo do aluno (lido do CSV)
        this.codigoAluno = codigoAluno;
        // nao tenta buscar o objeto Aluno aqui, isso é feito em GerenciaFaculdade
    }

    public String getCodigoMateria() {
        // retorna o codigo armazenado, mesmo que o objeto materia ainda nao esteja ligado
        return codigoMateria;
    }

    public void setCodigoMateria(String codigoMateria) {
        // permite definir o codigo da materia (lido do CSV)
        this.codigoMateria = codigoMateria;
        // nao tenta buscar o objeto Materia aqui, isso é feito em GerenciaFaculdade
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

