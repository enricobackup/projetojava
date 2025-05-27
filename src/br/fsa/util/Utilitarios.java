package br.fsa.util;

import br.fsa.faculdade.Curso;
import br.fsa.faculdade.Materia;
import br.fsa.pessoas.Aluno;
import br.fsa.pessoas.Professor;

import java.util.InputMismatchException;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Classe utilitária com métodos estáticos auxiliares para validações e outras operações comuns.
 */
public class Utilitarios {

    /**
     * Valida um CPF brasileiro usando o algoritmo dos dígitos verificadores.
     *
     * @param cpf O CPF a ser validado (deve conter apenas números).
     * @return true se o CPF for válido, false caso contrário.
     */
    public static boolean verificaCPF(String cpf) {
        // Remove caracteres não numéricos
        cpf = cpf.replaceAll("[^0-9]", "");

        // Considera-se erro CPF's formados por uma sequência de números iguais
        if (cpf == null || cpf.length() != 11 || cpf.matches("(\\d)\\1{10}"))
            return false;

        char dig10, dig11;
        int sm, i, r, num, peso;

        try {
            // Calculo do 1º. Digito Verificador
            sm = 0;
            peso = 10;
            for (i = 0; i < 9; i++) {
                num = (int) (cpf.charAt(i) - 48); // (48 é a posição de '0' na tabela ASCII)
                sm = sm + (num * peso);
                peso = peso - 1;
            }

            r = 11 - (sm % 11);
            if ((r == 10) || (r == 11))
                dig10 = '0';
            else
                dig10 = (char) (r + 48); // Converte no respectivo caractere numérico

            // Calculo do 2º. Digito Verificador
            sm = 0;
            peso = 11;
            for (i = 0; i < 10; i++) {
                num = (int) (cpf.charAt(i) - 48);
                sm = sm + (num * peso);
                peso = peso - 1;
            }

            r = 11 - (sm % 11);
            if ((r == 10) || (r == 11))
                dig11 = '0';
            else
                dig11 = (char) (r + 48);

            // Verifica se os dígitos calculados conferem com os dígitos informados.
            return (dig10 == cpf.charAt(9)) && (dig11 == cpf.charAt(10));
        } catch (InputMismatchException erro) {
            return false;
        }
    }

    /**
     * Verifica se um código único já existe em uma lista de Alunos.
     *
     * @param codigo O código a ser verificado.
     * @param alunos A lista de alunos existentes.
     * @throws ErroCadastro Se o código já existir na lista.
     */
    public static void verificaCodigoUnicoAluno(String codigo, List<Aluno> alunos) throws ErroCadastro {
        if (alunos == null) return;
        for (Aluno a : alunos) {
            if (a.getCodigoUnico() != null && a.getCodigoUnico().equals(codigo)) {
                throw new ErroCadastro("Código de aluno já cadastrado: " + codigo);
            }
        }
    }

    /**
     * Verifica se um código único já existe em uma lista de Professores.
     *
     * @param codigo O código a ser verificado.
     * @param professores A lista de professores existentes.
     * @throws ErroCadastro Se o código já existir na lista.
     */
    public static void verificaCodigoUnicoProfessor(String codigo, List<Professor> professores) throws ErroCadastro {
        if (professores == null) return;
        for (Professor p : professores) {
            if (p.getCodigoUnico() != null && p.getCodigoUnico().equals(codigo)) {
                throw new ErroCadastro("Código de professor já cadastrado: " + codigo);
            }
        }
    }

    /**
     * Verifica se um código único já existe em uma lista de Cursos.
     *
     * @param codigo O código a ser verificado.
     * @param cursos A lista de cursos existentes.
     * @throws ErroCadastro Se o código já existir na lista.
     */
    public static void verificaCodigoUnicoCurso(String codigo, List<Curso> cursos) throws ErroCadastro {
        if (cursos == null) return;
        for (Curso c : cursos) {
            if (c.getCodigoUnico() != null && c.getCodigoUnico().equals(codigo)) {
                throw new ErroCadastro("Código de curso já cadastrado: " + codigo);
            }
        }
    }

    /**
     * Verifica se um código único já existe em uma lista de Matérias.
     *
     * @param codigo O código a ser verificado.
     * @param materias A lista de matérias existentes.
     * @throws ErroCadastro Se o código já existir na lista.
     */
    public static void verificaCodigoUnicoMateria(String codigo, List<Materia> materias) throws ErroCadastro {
        if (materias == null) return;
        for (Materia m : materias) {
            if (m.getCodigoUnico() != null && m.getCodigoUnico().equals(codigo)) {
                throw new ErroCadastro("Código de matéria já cadastrado: " + codigo);
            }
        }
    }

    /**
     * Valida se um campo de texto obrigatório foi preenchido.
     *
     * @param nomeCampo O nome do campo (para a mensagem de erro).
     * @param valor O valor do campo a ser validado.
     * @throws ErroCadastro Se o valor for nulo ou vazio (após remover espaços em branco).
     */
    public static void validaCampoObrigatorio(String nomeCampo, String valor) throws ErroCadastro {
        if (valor == null || valor.trim().isEmpty()) {
            throw new ErroCadastro("Campo obrigatório '" + nomeCampo + "' não foi preenchido.");
        }
    }

    /**
     * Valida se uma nota está dentro do intervalo permitido (ex: 0 a 10).
     *
     * @param nota O valor da nota.
     * @param min O valor mínimo permitido.
     * @param max O valor máximo permitido.
     * @throws ErroCadastro Se a nota estiver fora do intervalo.
     */
    public static void validaNota(double nota, double min, double max) throws ErroCadastro {
        if (nota < min || nota > max) {
            throw new ErroCadastro("Nota inválida: " + nota + ". Deve estar entre " + min + " e " + max + ".");
        }
    }

    // Outros validadores podem ser adicionados aqui conforme a necessidade:
    // - Validador de formato de data (ex: dd/MM/yyyy)
    // - Validador de formato de telefone
    // - Validador de formato de CEP
    // - Validador de formato de código único (se houver um padrão)
}

