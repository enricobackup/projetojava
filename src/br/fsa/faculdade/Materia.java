package br.fsa.faculdade;

import br.fsa.pessoas.Aluno; //necessario pra classe Nota
import java.util.ArrayList;
import java.util.List;

public class Materia {
    private String nome;
    private Curso curso; 
    private String codigoCurso; 
    private String codigoUnico;
    private List<Nota> listaNotas;


    //construtores
    public Materia() {
        this.listaNotas = new ArrayList<>();
    }

    public Materia(String nome, Curso curso, String codigoUnico) {
        this.nome = nome;
        this.curso = curso;
        this.codigoCurso = (curso != null) ? curso.getCodigoUnico() : null;
        this.codigoUnico = codigoUnico;
        this.listaNotas = new ArrayList<>();
    }

    //getters e setters
    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

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

    public List<Nota> getListaNotas() {
        return listaNotas;
    }

    public void setListaNotas(List<Nota> listaNotas) {
        this.listaNotas = listaNotas;
    }

    public void adicionarNota(Nota nota) {
        if (this.listaNotas == null) {
            this.listaNotas = new ArrayList<>();
        }
        if (nota != null && !this.listaNotas.contains(nota)) {
            this.listaNotas.add(nota);
            nota.setMateria(this); 
        }
    }

    public void removerNota(Nota nota) {
        if (this.listaNotas != null && nota != null) {
            if (this.listaNotas.remove(nota)) {
                 nota.setMateria(null);
            }
        }
    }

    public List<Nota> getNotasPorAluno(Aluno aluno) {
        if (aluno == null || listaNotas == null) {
            return new ArrayList<>();
        }
        List<Nota> notasDoAluno = new ArrayList<>();
        for(Nota nota : listaNotas){
            if(nota.getAluno() != null && nota.getAluno().equals(aluno)){
                notasDoAluno.add(nota);
            }
        }
        return notasDoAluno;
    }

    public Nota getNotaEspecifica(Aluno aluno, String tipoAvaliacao) {
        if (aluno == null || tipoAvaliacao == null || listaNotas == null) {
            return null;
        }
        for(Nota nota : listaNotas){
            if(nota.getAluno() != null && nota.getAluno().equals(aluno) && tipoAvaliacao.equalsIgnoreCase(nota.getTipoAvaliacao())){
                return nota;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        String infoCurso = "Nenhum";
        if (curso != null) {
            infoCurso = curso.getNome() + " (" + curso.getCodigoUnico() + ")";
        } else if (codigoCurso != null && !codigoCurso.isEmpty()){
             infoCurso = "Código: " + codigoCurso + " (não carregado)";
        }
        return "Materia [Código: " + codigoUnico + ", Nome: " + nome + ", Curso: " + infoCurso + "]";
    }
}

