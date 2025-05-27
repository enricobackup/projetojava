package br.fsa.pessoas;

import br.fsa.faculdade.Curso;

public class Aluno extends Pessoa {
    private Curso curso; // Referência ao objeto Curso
    private String codigoCurso; // Código do curso (para persistência e ligação)
    private String codigoUnico; // Código único do aluno

    public Aluno() {
        super();
    }

    public Aluno(String nome, String dataNascimento, String rua, String numero, String cidade, String estado, String cep, String telefone, String genero, String rg, String cpf, Curso curso, String codigoUnico) {
        super(nome, dataNascimento, rua, numero, cidade, estado, cep, telefone, genero, rg, cpf);
        this.curso = curso;
        this.codigoCurso = (curso != null) ? curso.getCodigoUnico() : null;
        this.codigoUnico = codigoUnico;
    }

    // Getters e Setters

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

    // Métodos toCsvString e fromCsvString removidos daqui, pois a lógica está no DAO
}

