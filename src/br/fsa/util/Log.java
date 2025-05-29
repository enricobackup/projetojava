package br.fsa.util;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Classe para registrar logs de execução do programa em um arquivo.
 */
public class Log {

    private String logFilePath;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    /**
     * Construtor que define o nome do arquivo de log.
     *
     * @param nomeArquivoLog O caminho completo ou relativo para o arquivo de log.
     */
    public Log(String nomeArquivoLog) {
        this.logFilePath = nomeArquivoLog;
        // Opcional: Criar o arquivo ou diretório se não existir?
    }

    /**
     * Registra uma mensagem informativa no arquivo de log.
     *
     * @param mensagem A mensagem a ser registrada.
     */
    public void logInfo(String mensagem) {
        escreverLog("INFO", mensagem);
    }

    /**
     * Registra uma mensagem de erro no arquivo de log.
     *
     * @param mensagem A mensagem de erro a ser registrada.
     */
    public void logErro(String mensagem) {
        escreverLog("ERRO", mensagem);
    }

    /**
     * Registra uma mensagem de erro junto com a stack trace da exceção.
     *
     * @param mensagem A mensagem de erro.
     * @param e A exceção que causou o erro.
     */
    public void logErro(String mensagem, Throwable e) {
        escreverLog("ERRO", mensagem);
        // Log a stack trace para depuração detalhada
        try (FileWriter fw = new FileWriter(logFilePath, true);
             PrintWriter pw = new PrintWriter(fw)) {
            e.printStackTrace(pw);
        } catch (IOException ioex) {
            System.err.println("Falha CRÍTICA ao escrever stack trace no log: " + ioex.getMessage());
        }
    }

    /**
     * Método privado para escrever a mensagem formatada no arquivo.
     *
     * @param tipo     O tipo de log ("INFO" ou "ERRO").
     * @param mensagem A mensagem a ser escrita.
     */
    private void escreverLog(String tipo, String mensagem) {
        try (FileWriter fw = new FileWriter(logFilePath, true); // true para append
             PrintWriter pw = new PrintWriter(fw)) {
            String timestamp = dateFormat.format(new Date());
            // Formato: TIPO - mensagem (conforme instrução, adicionando timestamp)
            pw.println(timestamp + " - " + tipo + " - " + mensagem);
        } catch (IOException e) {
            // Em caso de falha ao logar, imprime no console para não perder a informação
            System.err.println("Falha ao escrever no arquivo de log ('" + logFilePath + "'): " + e.getMessage());
            System.err.println("Mensagem original: [" + tipo + "] " + mensagem);
        }
    }
}

