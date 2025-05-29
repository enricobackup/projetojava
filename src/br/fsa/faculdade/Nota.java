package br.fsa.faculdade;

import br.fsa.pessoas.Aluno;

public class Nota {
    private Aluno aluno; 
    private Materia materia; 
    private String codigoAluno; 
    private String codigoMateria; 
    private String tipoAvaliacao; //ex: "p1", "p2", "trabaio"
    private double valorNota;

    //construtor padrao
    public Nota() {
    }

    //construtor com todos os parametros
    public Nota(Aluno aluno, Materia materia, String tipoAvaliacao, double valorNota) {
        this.aluno = aluno;
        this.materia = materia;
        this.codigoAluno = (aluno != null) ? aluno.getCodigoUnico() : null;
        this.codigoMateria = (materia != null) ? materia.getCodigoUnico() : null;
        this.tipoAvaliacao = tipoAvaliacao;
        this.valorNota = valorNota;
    }

    //getters e setters
    public Aluno getAluno() {
        return aluno;
    }

    public void setAluno(Aluno aluno) {
        this.aluno = aluno;
        this.codigoAluno = (aluno != null) ? aluno.getCodigoUnico() : null;
    }

    public Materia getMateria() {
        return materia;
    }

    public void setMateria(Materia materia) {
        this.materia = materia;
        this.codigoMateria = (materia != null) ? materia.getCodigoUnico() : null;
    }

    public String getCodigoAluno() {
        return codigoAluno;
    }

    public void setCodigoAluno(String codigoAluno) {
        this.codigoAluno = codigoAluno;
    }

    public String getCodigoMateria() {
        return codigoMateria;
    }

    public void setCodigoMateria(String codigoMateria) {
        this.codigoMateria = codigoMateria;
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


    //verifica se tem aluno, materia e dps cria a ntoa
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
}

