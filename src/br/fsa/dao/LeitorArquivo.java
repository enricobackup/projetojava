package br.fsa.dao;

import java.io.IOException;
import java.util.List;

/**
 * Interface genérica para leitura e escrita de dados em arquivos.
 * Define os métodos básicos que as classes DAO devem implementar
 * para persistir e carregar listas de objetos.
 *
 * @param <T> O tipo do objeto que será lido/escrito (e.g., Aluno, Professor).
 */
public interface LeitorArquivo<T> {

    /**
     * Define o caminho do arquivo que será utilizado para leitura/escrita.
     *
     * @param nomeArquivo O caminho completo ou relativo do arquivo.
     */
    void setArquivo(String nomeArquivo);

    /**
     * Escreve uma lista de objetos no arquivo definido.
     * O conteúdo anterior do arquivo geralmente será sobrescrito.
     *
     * @param lista A lista de objetos do tipo T a serem escritos.
     * @throws IOException Se ocorrer um erro durante a escrita no arquivo.
     */
    void escreveArquivo(List<T> lista) throws IOException;

    /**
     * Lê os dados do arquivo definido e retorna uma lista de objetos.
     *
     * @return Uma lista contendo os objetos do tipo T lidos do arquivo.
     * @throws IOException Se ocorrer um erro durante a leitura do arquivo.
     */
    List<T> leArquivo() throws IOException;
}

