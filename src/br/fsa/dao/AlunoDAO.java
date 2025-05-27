package br.fsa.dao;

import br.fsa.pessoas.Aluno;
import br.fsa.util.Log;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO para Alunos, responsável por ler e escrever dados de alunos em arquivo CSV.
 * Versão Simplificada: Não tenta resolver referências a Cursos durante a leitura.
 */
public class AlunoDAO implements LeitorArquivo<Aluno> {

    private String arquivoCsvAlunos;
    private static final String SEPARADOR = ";";
    private Log logger; // Instância do Log

    // Construtor simplificado, recebe apenas o Logger
    public AlunoDAO(Log logger) {
        this.logger = logger;
        // Define um nome padrão ou exige via setArquivo
        this.arquivoCsvAlunos = "alunos.csv";
    }

    @Override
    public void setArquivo(String nomeArquivo) {
        this.arquivoCsvAlunos = nomeArquivo;
    }

    @Override
    public void escreveArquivo(List<Aluno> alunos) throws IOException {
        logger.logInfo("Iniciando escrita do arquivo: " + arquivoCsvAlunos);
        try (PrintWriter writer = new PrintWriter(new FileWriter(arquivoCsvAlunos))) {
            // Escreve o cabeçalho
            writer.println("Nome;DataNascimento;Rua;Numero;Cidade;Estado;CEP;Telefone;Genero;RG;CPF;CursoCodigo;CodigoUnico");

            for (Aluno aluno : alunos) {
                String linha = formatarParaCsv(aluno);
                writer.println(linha);
            }
            logger.logInfo("Arquivo " + arquivoCsvAlunos + " escrito com sucesso com " + alunos.size() + " registros.");
        } catch (IOException e) {
            logger.logErro("Erro ao escrever o arquivo de alunos: " + arquivoCsvAlunos, e);
            throw e;
        }
    }

    @Override
    public List<Aluno> leArquivo() throws IOException {
        logger.logInfo("Iniciando leitura do arquivo: " + arquivoCsvAlunos);
        List<Aluno> alunos = new ArrayList<>();
        File file = new File(arquivoCsvAlunos);

        if (!file.exists()) {
            logger.logInfo("Arquivo " + arquivoCsvAlunos + " não encontrado. Retornando lista vazia.");
            return alunos;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(arquivoCsvAlunos))) {
            String linha = reader.readLine(); // Pula o cabeçalho

            while ((linha = reader.readLine()) != null) {
                if (linha.trim().isEmpty()) continue;

                Aluno aluno = parseCsvParaAluno(linha);
                if (aluno != null) {
                    alunos.add(aluno);
                }
            }
            logger.logInfo("Arquivo " + arquivoCsvAlunos + " lido com sucesso. " + alunos.size() + " registros carregados.");
        } catch (FileNotFoundException e) {
             logger.logInfo("Arquivo " + arquivoCsvAlunos + " não encontrado ao tentar ler. Retornando lista vazia.");
        } catch (IOException e) {
            logger.logErro("Erro ao ler o arquivo de alunos: " + arquivoCsvAlunos, e);
            throw e;
        }
        return alunos;
    }

    // Formata Aluno para CSV
    private String formatarParaCsv(Aluno aluno) {
        // Pega o código do curso diretamente do aluno (pode ser null se não associado)
        String cursoCodigo = aluno.getCodigoCurso();
        return String.join(SEPARADOR,
                aluno.getNome() != null ? aluno.getNome() : "",
                aluno.getDataNascimento() != null ? aluno.getDataNascimento() : "",
                aluno.getRua() != null ? aluno.getRua() : "",
                aluno.getNumero() != null ? aluno.getNumero() : "",
                aluno.getCidade() != null ? aluno.getCidade() : "",
                aluno.getEstado() != null ? aluno.getEstado() : "",
                aluno.getCep() != null ? aluno.getCep() : "",
                aluno.getTelefone() != null ? aluno.getTelefone() : "",
                aluno.getGenero() != null ? aluno.getGenero() : "",
                aluno.getRg() != null ? aluno.getRg() : "",
                aluno.getCpf() != null ? aluno.getCpf() : "",
                cursoCodigo != null ? cursoCodigo : "", // Salva o código do curso
                aluno.getCodigoUnico() != null ? aluno.getCodigoUnico() : ""
        );
    }

    // Parseia linha CSV para Aluno (sem resolver referência de Curso)
    private Aluno parseCsvParaAluno(String csvLine) {
        String[] data = csvLine.split(SEPARADOR, -1);
        if (data.length < 13) {
            logger.logErro("Linha CSV inválida para Aluno (poucos campos: " + data.length + "): " + csvLine);
            return null;
        }

        try {
            Aluno aluno = new Aluno();
            aluno.setNome(data[0]);
            aluno.setDataNascimento(data[1]);
            aluno.setRua(data[2]);
            aluno.setNumero(data[3]);
            aluno.setCidade(data[4]);
            aluno.setEstado(data[5]);
            aluno.setCep(data[6]);
            aluno.setTelefone(data[7]);
            aluno.setGenero(data[8]);
            aluno.setRg(data[9]);
            aluno.setCpf(data[10]);
            aluno.setCodigoCurso(data[11]); // Apenas armazena o código do curso
            aluno.setCodigoUnico(data[12]);
            // A referência aluno.setCurso() será feita depois em GerenciaFaculdade

            return aluno;
        } catch (Exception e) {
            logger.logErro("Erro ao parsear linha CSV para Aluno: " + csvLine, e);
            return null;
        }
    }
}

