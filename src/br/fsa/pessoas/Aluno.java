package br.fsa.pessoas;

import br.fsa.faculdade.Curso;

public class Aluno extends Pessoa {
    private Curso curso; 
    private String codigoCurso;
    private String codigoUnico; 


    //construtores 
    public Aluno() {
        super();
    }

    public Aluno(String nome, String dataNascimento, String rua, String numero, String cidade, String estado, String cep, String telefone, String genero, String rg, String cpf, Curso curso, String codigoUnico) {
        super(nome, dataNascimento, rua, numero, cidade, estado, cep, telefone, genero, rg, cpf);
        this.curso = curso;
        this.codigoCurso = (curso != null) ? curso.getCodigoUnico() : null;
        this.codigoUnico = codigoUnico;
    }

    //Getters e Setters

    public Curso getCurso() {
        return curso;
    }

    public void setCurso(Curso curso) {
        this.curso = curso;
        this.codigoCurso = (curso != null) ? curso.getCodigoUnico() : null;
    }

    public String getCodigoCurso() {
        return codigoCurso;
    }

    public void setCodigoCurso(String codigoCurso) {
        this.codigoCurso = codigoCurso;
    }

    public String getCodigoUnico() {
        return codigoUnico;
    }

    public void setCodigoUnico(String codigoUnico) {
        this.codigoUnico = codigoUnico;
    }

    @Override
    public String toString() {
        String infoCurso = "Nenhum";
        if (curso != null) {
            infoCurso = curso.getNome() + " (" + curso.getCodigoUnico() + ")";
        } else if (codigoCurso != null && !codigoCurso.isEmpty()){
             infoCurso = "Código: " + codigoCurso + " (não carregado)";
        }
        return "Aluno [Código: " + codigoUnico + ", Nome: " + getNome() + ", CPF: " + getCpf() + ", Curso: " + infoCurso + "]";
    }
}

