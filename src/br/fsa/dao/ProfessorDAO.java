package br.fsa.dao;

import br.fsa.pessoas.Professor;
import br.fsa.util.Log;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO para Professores, responsável por ler e escrever dados de professores em arquivo CSV.
 * Versão Simplificada: Não tenta resolver referências a Matérias durante a leitura.
 */
public class ProfessorDAO implements LeitorArquivo<Professor> {

    private String arquivoCsvProfessores;
    private static final String SEPARADOR = ";";
    private static final String SEPARADOR_MATERIAS = ","; //Separador para a lista de códigos de matérias
    private Log logger;

    //Construtor simplificado
    public ProfessorDAO(Log logger) {
        this.logger = logger;
        this.arquivoCsvProfessores = "professores.csv";
    }

    @Override
    public void setArquivo(String nomeArquivo) {
        this.arquivoCsvProfessores = nomeArquivo;
    }

    @Override
    public void escreveArquivo(List<Professor> professores) throws IOException {
        logger.logInfo("Iniciando escrita do arquivo: " + arquivoCsvProfessores);
        try (PrintWriter writer = new PrintWriter(new FileWriter(arquivoCsvProfessores))) {
            //Cabeçalho
            writer.println("Nome;DataNascimento;Rua;Numero;Cidade;Estado;CEP;Telefone;Genero;RG;CPF;CodigoUnico;CodigosMaterias");

            for (Professor professor : professores) {
                String linha = formatarParaCsv(professor);
                writer.println(linha);
            }
            logger.logInfo("Arquivo " + arquivoCsvProfessores + " escrito com sucesso com " + professores.size() + " registros.");
        } catch (IOException e) {
            logger.logErro("Erro ao escrever o arquivo de professores: " + arquivoCsvProfessores, e);
            throw e;
        }
    }

    @Override
    public List<Professor> leArquivo() throws IOException {
        logger.logInfo("Iniciando leitura do arquivo: " + arquivoCsvProfessores);
        List<Professor> professores = new ArrayList<>();
        File file = new File(arquivoCsvProfessores);

        if (!file.exists()) {
            logger.logInfo("Arquivo " + arquivoCsvProfessores + " não encontrado. Retornando lista vazia.");
            return professores;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(arquivoCsvProfessores))) {
            String linha = reader.readLine(); //Pula cabeçalho

            while ((linha = reader.readLine()) != null) {
                if (linha.trim().isEmpty()) continue;

                Professor professor = parseCsvParaProfessor(linha);
                if (professor != null) {
                    professores.add(professor);
                }
            }
            logger.logInfo("Arquivo " + arquivoCsvProfessores + " lido com sucesso. " + professores.size() + " registros carregados.");
        } catch (FileNotFoundException e) {
            logger.logInfo("Arquivo " + arquivoCsvProfessores + " não encontrado ao tentar ler. Retornando lista vazia.");
        } catch (IOException e) {
            logger.logErro("Erro ao ler o arquivo de professores: " + arquivoCsvProfessores, e);
            throw e;
        }
        return professores;
    }

    //Formata Professor para CSV
    private String formatarParaCsv(Professor professor) {
        //Junta os códigos das matérias com o separador específico
        String codigosMateriasStr = "";
        if (professor.getCodigosMaterias() != null) {
            codigosMateriasStr = String.join(SEPARADOR_MATERIAS, professor.getCodigosMaterias());
        }

        return String.join(SEPARADOR,
                professor.getNome() != null ? professor.getNome() : "",
                professor.getDataNascimento() != null ? professor.getDataNascimento() : "",
                professor.getRua() != null ? professor.getRua() : "",
                professor.getNumero() != null ? professor.getNumero() : "",
                professor.getCidade() != null ? professor.getCidade() : "",
                professor.getEstado() != null ? professor.getEstado() : "",
                professor.getCep() != null ? professor.getCep() : "",
                professor.getTelefone() != null ? professor.getTelefone() : "",
                professor.getGenero() != null ? professor.getGenero() : "",
                professor.getRg() != null ? professor.getRg() : "",
                professor.getCpf() != null ? professor.getCpf() : "",
                professor.getCodigoUnico() != null ? professor.getCodigoUnico() : "",
                codigosMateriasStr //Adiciona a string de códigos de matérias
        );
    }

    //Parseia linha CSV para Professor
    private Professor parseCsvParaProfessor(String csvLine) {
        String[] data = csvLine.split(SEPARADOR, -1);
        if (data.length < 13) { //Agora são 13 campos
            logger.logErro("Linha CSV inválida para Professor (poucos campos: " + data.length + "): " + csvLine);
            return null;
        }

        try {
            Professor professor = new Professor();
            professor.setNome(data[0]);
            professor.setDataNascimento(data[1]);
            professor.setRua(data[2]);
            professor.setNumero(data[3]);
            professor.setCidade(data[4]);
            professor.setEstado(data[5]);
            professor.setCep(data[6]);
            professor.setTelefone(data[7]);
            professor.setGenero(data[8]);
            professor.setRg(data[9]);
            professor.setCpf(data[10]);
            professor.setCodigoUnico(data[11]);

            //Parseia a string de códigos de matérias
            List<String> codigosMaterias = new ArrayList<>();
            if (data.length > 12 && data[12] != null && !data[12].trim().isEmpty()) {
                String[] codigos = data[12].split(SEPARADOR_MATERIAS);
                for (String codigo : codigos) {
                    if (!codigo.trim().isEmpty()) {
                        codigosMaterias.add(codigo.trim());
                    }
                }
            }
            professor.setCodigosMaterias(codigosMaterias);
            //A referência professor.setListaMaterias() será feita depois em GerenciaFaculdade

            return professor;
        } catch (Exception e) {
            logger.logErro("Erro ao parsear linha CSV para Professor: " + csvLine, e);
            return null;
        }
    }
}

