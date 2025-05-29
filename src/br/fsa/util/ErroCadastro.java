package br.fsa.util;

/**
 * Exceção personalizada para erros relacionados ao cadastro ou validação de dados
 * no sistema da faculdade.
 */
public class ErroCadastro extends Exception {

    /**
     * Construtor que aceita uma mensagem de erro.
     * @param mensagem A mensagem detalhando o erro ocorrido.
     */
    public ErroCadastro(String mensagem) {
        super(mensagem);
    }

    /**
     * Construtor que aceita uma mensagem de erro e a causa original.
     * @param mensagem A mensagem detalhando o erro ocorrido.
     * @param causa A exceção original que causou este erro.
     */
    public ErroCadastro(String mensagem, Throwable causa) {
        super(mensagem, causa);
    }
}

