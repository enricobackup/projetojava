package br.fsa.dao;

import br.fsa.faculdade.Curso;
import br.fsa.util.Log;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO para Cursos, responsável por ler e escrever dados de cursos em arquivo CSV.
 * Versão Simplificada: Não tenta resolver referências a Matérias durante a leitura.
 */
public class CursoDAO implements LeitorArquivo<Curso> {

    private String arquivoCsvCursos;
    private static final String SEPARADOR = ";";
    private static final String SEPARADOR_MATERIAS = ","; //Separador para a lista de códigos de matérias
    private Log logger;

    //Construtor simplificado
    public CursoDAO(Log logger) {
        this.logger = logger;
        this.arquivoCsvCursos = "cursos.csv";
    }

    @Override
    public void setArquivo(String nomeArquivo) {
        this.arquivoCsvCursos = nomeArquivo;
    }

    @Override
    public void escreveArquivo(List<Curso> cursos) throws IOException {
        logger.logInfo("Iniciando escrita do arquivo: " + arquivoCsvCursos);
        try (PrintWriter writer = new PrintWriter(new FileWriter(arquivoCsvCursos))) {
            //Cabeçalho
            writer.println("Nome;CodigoUnico;CodigosMaterias");

            for (Curso curso : cursos) {
                String linha = formatarParaCsv(curso);
                writer.println(linha);
            }
            logger.logInfo("Arquivo " + arquivoCsvCursos + " escrito com sucesso com " + cursos.size() + " registros.");
        } catch (IOException e) {
            logger.logErro("Erro ao escrever o arquivo de cursos: " + arquivoCsvCursos, e);
            throw e;
        }
    }

    @Override
    public List<Curso> leArquivo() throws IOException {
        logger.logInfo("Iniciando leitura do arquivo: " + arquivoCsvCursos);
        List<Curso> cursos = new ArrayList<>();
        File file = new File(arquivoCsvCursos);

        if (!file.exists()) {
            logger.logInfo("Arquivo " + arquivoCsvCursos + " não encontrado. Retornando lista vazia.");
            return cursos;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(arquivoCsvCursos))) {
            String linha = reader.readLine(); //Pula cabeçalho

            while ((linha = reader.readLine()) != null) {
                if (linha.trim().isEmpty()) continue;

                Curso curso = parseCsvParaCurso(linha);
                if (curso != null) {
                    cursos.add(curso);
                }
            }
            logger.logInfo("Arquivo " + arquivoCsvCursos + " lido com sucesso. " + cursos.size() + " registros carregados.");
        } catch (FileNotFoundException e) {
            logger.logInfo("Arquivo " + arquivoCsvCursos + " não encontrado ao tentar ler. Retornando lista vazia.");
        } catch (IOException e) {
            logger.logErro("Erro ao ler o arquivo de cursos: " + arquivoCsvCursos, e);
            throw e;
        }
        return cursos;
    }

    //Formata Curso para CSV
    public String formatarParaCsv(Curso curso) {
        //Junta os códigos das matérias com o separador específico
        String codigosMateriasStr = "";
        if (curso.getCodigosMaterias() != null) {
             codigosMateriasStr = String.join(SEPARADOR_MATERIAS, curso.getCodigosMaterias());
        }

        return String.join(SEPARADOR,
                curso.getNome() != null ? curso.getNome() : "",
                curso.getCodigoUnico() != null ? curso.getCodigoUnico() : "",
                codigosMateriasStr //Adiciona a string de códigos de matérias
        );
    }

    //Parseia linha CSV para Curso
    private Curso parseCsvParaCurso(String csvLine) {
        String[] data = csvLine.split(SEPARADOR, -1);
        if (data.length < 3) { //Agora são 3 campos
            logger.logErro("Linha CSV inválida para Curso (poucos campos: " + data.length + "): " + csvLine);
            return null;
        }

        try {
            Curso curso = new Curso();
            curso.setNome(data[0]);
            curso.setCodigoUnico(data[1]);

            //Parseia a string de códigos de matérias
            List<String> codigosMaterias = new ArrayList<>();
            if (data.length > 2 && data[2] != null && !data[2].trim().isEmpty()) {
                String[] codigos = data[2].split(SEPARADOR_MATERIAS);
                for (String codigo : codigos) {
                    if (!codigo.trim().isEmpty()) {
                        codigosMaterias.add(codigo.trim());
                    }
                }
            }
            curso.setCodigosMaterias(codigosMaterias);
            //A referência curso.setListaMaterias() será feita depois em GerenciaFaculdade

            return curso;
        } catch (Exception e) {
            logger.logErro("Erro ao parsear linha CSV para Curso: " + csvLine, e);
            return null;
        }
    }
}

