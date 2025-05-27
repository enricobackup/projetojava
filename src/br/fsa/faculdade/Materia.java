package br.fsa.faculdade;

import br.fsa.pessoas.Aluno; // Necessário para a classe Nota
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Materia {
    private String nome;
    private Curso curso; // Referência ao objeto Curso
    private String codigoCurso; // Código do curso (para persistência e ligação)
    private String codigoUnico;
    private List<Nota> listaNotas;

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

    // Getters e Setters
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
        // Atualiza o código do curso quando o objeto curso é definido
        this.codigoCurso = (curso != null) ? curso.getCodigoUnico() : null;
    }

    public String getCodigoCurso() {
        // Retorna o código armazenado, mesmo que o objeto curso ainda não esteja ligado
        return codigoCurso;
    }

    public void setCodigoCurso(String codigoCurso) {
        // Permite definir o código do curso (lido do CSV)
        this.codigoCurso = codigoCurso;
        // Não tenta buscar o objeto Curso aqui, isso é feito em GerenciaFaculdade
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
            nota.setMateria(this); // Garante associação bidirecional
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

    // Métodos toCsvString e fromCsvString removidos daqui, pois a lógica está no DAO
}

