package br.fsa.dao;

import br.fsa.faculdade.Materia;
import br.fsa.util.Log;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO para Matérias, responsável por ler e escrever dados de matérias em arquivo CSV.
 * Versão Simplificada: Não tenta resolver referências a Cursos ou Notas durante a leitura.
 */
public class MateriaDAO implements LeitorArquivo<Materia> {

    private String arquivoCsvMaterias;
    private static final String SEPARADOR = ";";
    private Log logger;

    // Construtor simplificado
    public MateriaDAO(Log logger) {
        this.logger = logger;
        this.arquivoCsvMaterias = "materias.csv";
    }

    @Override
    public void setArquivo(String nomeArquivo) {
        this.arquivoCsvMaterias = nomeArquivo;
    }

    @Override
    public void escreveArquivo(List<Materia> materias) throws IOException {
        logger.logInfo("Iniciando escrita do arquivo: " + arquivoCsvMaterias);
        try (PrintWriter writer = new PrintWriter(new FileWriter(arquivoCsvMaterias))) {
            // Cabeçalho
            writer.println("Nome;CodigoUnico;CodigoCurso");

            for (Materia materia : materias) {
                String linha = formatarParaCsv(materia);
                writer.println(linha);
            }
            logger.logInfo("Arquivo " + arquivoCsvMaterias + " escrito com sucesso com " + materias.size() + " registros.");
        } catch (IOException e) {
            logger.logErro("Erro ao escrever o arquivo de matérias: " + arquivoCsvMaterias, e);
            throw e;
        }
    }

    @Override
    public List<Materia> leArquivo() throws IOException {
        logger.logInfo("Iniciando leitura do arquivo: " + arquivoCsvMaterias);
        List<Materia> materias = new ArrayList<>();
        File file = new File(arquivoCsvMaterias);

        if (!file.exists()) {
            logger.logInfo("Arquivo " + arquivoCsvMaterias + " não encontrado. Retornando lista vazia.");
            return materias;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(arquivoCsvMaterias))) {
            String linha = reader.readLine(); // Pula cabeçalho

            while ((linha = reader.readLine()) != null) {
                if (linha.trim().isEmpty()) continue;

                Materia materia = parseCsvParaMateria(linha);
                if (materia != null) {
                    materias.add(materia);
                }
            }
            logger.logInfo("Arquivo " + arquivoCsvMaterias + " lido com sucesso. " + materias.size() + " registros carregados.");
        } catch (FileNotFoundException e) {
            logger.logInfo("Arquivo " + arquivoCsvMaterias + " não encontrado ao tentar ler. Retornando lista vazia.");
        } catch (IOException e) {
            logger.logErro("Erro ao ler o arquivo de matérias: " + arquivoCsvMaterias, e);
            throw e;
        }
        return materias;
    }

    // Formata Matéria para CSV
    private String formatarParaCsv(Materia materia) {
        String cursoCodigo = materia.getCodigoCurso();
        return String.join(SEPARADOR,
                materia.getNome() != null ? materia.getNome() : "",
                materia.getCodigoUnico() != null ? materia.getCodigoUnico() : "",
                cursoCodigo != null ? cursoCodigo : "" // Salva o código do curso
        );
    }

    // Parseia linha CSV para Matéria
    private Materia parseCsvParaMateria(String csvLine) {
        String[] data = csvLine.split(SEPARADOR, -1);
        if (data.length < 3) {
            logger.logErro("Linha CSV inválida para Matéria (poucos campos: " + data.length + "): " + csvLine);
            return null;
        }

        try {
            Materia materia = new Materia();
            materia.setNome(data[0]);
            materia.setCodigoUnico(data[1]);
            materia.setCodigoCurso(data[2]); // Apenas armazena o código do curso
            // A referência materia.setCurso() e materia.setListaNotas() será feita depois em GerenciaFaculdade

            return materia;
        } catch (Exception e) {
            logger.logErro("Erro ao parsear linha CSV para Matéria: " + csvLine, e);
            return null;
        }
    }
}

