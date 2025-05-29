package br.fsa.pessoas;

public abstract class Pessoa {
    protected String nome;
    protected String dataNascimento;
    protected String rua;
    protected String numero;
    protected String cidade;
    protected String estado;
    protected String cep; //CEP como String para incluir "-" e evitar problemas com zeros na esquerda
    protected String telefone;
    protected String genero;
    protected String rg;
    protected String cpf;

    //Getters e Setters para todos os atributos

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getDataNascimento() {
        return dataNascimento;
    }

    public void setDataNascimento(String dataNascimento) {
        this.dataNascimento = dataNascimento;
    }

    public String getRua() {
        return rua;
    }

    public void setRua(String rua) {
        this.rua = rua;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public String getCidade() {
        return cidade;
    }

    public void setCidade(String cidade) {
        this.cidade = cidade;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getCep() {
        return cep;
    }

    public void setCep(String cep) {
        this.cep = cep;
    }

    //Método para obter o endereço formatado
    public String getEnderecoCompleto() {
        return rua + ", " + numero + " - " + cidade + "/" + estado + " - CEP: " + cep;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public String getGenero() {
        return genero;
    }

    public void setGenero(String genero) {
        this.genero = genero;
    }

    public String getRg() {
        return rg;
    }

    public void setRg(String rg) {
        this.rg = rg;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        //A validação do CPF será feita na classe Utilitarios antes de chamar esse setter
        this.cpf = cpf;
    }

    //construtores
    public Pessoa() {}

    public Pessoa(String nome, String dataNascimento, String rua, String numero, String cidade, String estado, String cep, String telefone, String genero, String rg, String cpf) {
        this.nome = nome;
        this.dataNascimento = dataNascimento;
        this.rua = rua;
        this.numero = numero;
        this.cidade = cidade;
        this.estado = estado;
        this.cep = cep;
        this.telefone = telefone;
        this.genero = genero;
        this.rg = rg;
        this.cpf = cpf; 
    }

    @Override
    public String toString() {
        return "Nome: " + nome + ", CPF: " + cpf + ", Nascimento: " + dataNascimento + ", Endereço: " + getEnderecoCompleto();
    }
}

