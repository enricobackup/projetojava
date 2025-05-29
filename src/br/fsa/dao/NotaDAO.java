package br.fsa.dao;

import br.fsa.faculdade.Nota;
import br.fsa.util.Log;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO para Notas, responsável por ler e escrever dados de notas em arquivo CSV.
 * Versão Simplificada: Não tenta resolver referências a Alunos ou Matérias durante a leitura.
 */
public class NotaDAO implements LeitorArquivo<Nota> {

    private String arquivoCsvNotas;
    private static final String SEPARADOR = ";";
    private Log logger;

    //Construtor simplificado
    public NotaDAO(Log logger) {
        this.logger = logger;
        this.arquivoCsvNotas = "notas.csv";
    }

    @Override
    public void setArquivo(String nomeArquivo) {
        this.arquivoCsvNotas = nomeArquivo;
    }

    @Override
    public void escreveArquivo(List<Nota> notas) throws IOException {
        logger.logInfo("Iniciando escrita do arquivo: " + arquivoCsvNotas);
        try (PrintWriter writer = new PrintWriter(new FileWriter(arquivoCsvNotas))) {
            //Cabeçalho
            writer.println("CodigoAluno;CodigoMateria;TipoAvaliacao;ValorNota");

            for (Nota nota : notas) {
                String linha = formatarParaCsv(nota);
                writer.println(linha);
            }
            logger.logInfo("Arquivo " + arquivoCsvNotas + " escrito com sucesso com " + notas.size() + " registros.");
        } catch (IOException e) {
            logger.logErro("Erro ao escrever o arquivo de notas: " + arquivoCsvNotas, e);
            throw e;
        }
    }

    @Override
    public List<Nota> leArquivo() throws IOException {
        logger.logInfo("Iniciando leitura do arquivo: " + arquivoCsvNotas);
        List<Nota> notas = new ArrayList<>();
        File file = new File(arquivoCsvNotas);

        if (!file.exists()) {
            logger.logInfo("Arquivo " + arquivoCsvNotas + " não encontrado. Retornando lista vazia.");
            return notas;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(arquivoCsvNotas))) {
            String linha = reader.readLine(); //Pula cabeçalho

            while ((linha = reader.readLine()) != null) {
                if (linha.trim().isEmpty()) continue;

                Nota nota = parseCsvParaNota(linha);
                if (nota != null) {
                    notas.add(nota);
                }
            }
            logger.logInfo("Arquivo " + arquivoCsvNotas + " lido com sucesso. " + notas.size() + " registros carregados.");
        } catch (FileNotFoundException e) {
            logger.logInfo("Arquivo " + arquivoCsvNotas + " não encontrado ao tentar ler. Retornando lista vazia.");
        } catch (IOException e) {
            logger.logErro("Erro ao ler o arquivo de notas: " + arquivoCsvNotas, e);
            throw e;
        }
        return notas;
    }

    //Formata Nota para CSV
    private String formatarParaCsv(Nota nota) {
        String alunoCodigo = nota.getCodigoAluno();
        String materiaCodigo = nota.getCodigoMateria();

        return String.join(SEPARADOR,
                alunoCodigo != null ? alunoCodigo : "",
                materiaCodigo != null ? materiaCodigo : "",
                nota.getTipoAvaliacao() != null ? nota.getTipoAvaliacao() : "",
                String.valueOf(nota.getValorNota()) //Converte double para String
        );
    }

    //Parseia linha CSV para Nota
    private Nota parseCsvParaNota(String csvLine) {
        String[] data = csvLine.split(SEPARADOR, -1);
        if (data.length < 4) {
            logger.logErro("Linha CSV inválida para Nota (poucos campos: " + data.length + "): " + csvLine);
            return null;
        }

        try {
            String codigoAluno = data[0];
            String codigoMateria = data[1];
            String tipoAvaliacao = data[2];
            double valorNota = Double.parseDouble(data[3]);

            Nota nota = new Nota();
            nota.setCodigoAluno(codigoAluno); //Armazena código
            nota.setCodigoMateria(codigoMateria); //Armazena código
            nota.setTipoAvaliacao(tipoAvaliacao);
            nota.setValorNota(valorNota);
            //As referências nota.setAluno() e nota.setMateria() serão feitas depois em GerenciaFaculdade

            return nota;
        } catch (NumberFormatException e) {
             logger.logErro("Erro ao parsear valor da nota na linha CSV: " + csvLine, e);
             return null;
        } catch (Exception e) {
            logger.logErro("Erro ao parsear linha CSV para Nota: " + csvLine, e);
            return null;
        }
    }
}

