package br.fsa.pessoas;

import br.fsa.faculdade.Materia;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Professor extends Pessoa {
    private List<Materia> listaMaterias; 
    private List<String> codigosMaterias; 
    private String codigoUnico;


    //construtores malucos
    public Professor() {
        super();
        this.listaMaterias = new ArrayList<>();
        this.codigosMaterias = new ArrayList<>();
    }

    public Professor(String nome, String dataNascimento, String rua, String numero, String cidade, String estado, String cep, String telefone, String genero, String rg, String cpf, List<Materia> listaMaterias, String codigoUnico) {
        super(nome, dataNascimento, rua, numero, cidade, estado, cep, telefone, genero, rg, cpf);
        this.listaMaterias = (listaMaterias != null) ? new ArrayList<>(listaMaterias) : new ArrayList<>();
        this.codigosMaterias = new ArrayList<>();
        if (this.listaMaterias != null) {
            for (Materia m : this.listaMaterias) {
                this.codigosMaterias.add(m.getCodigoUnico());
            }
        }
        this.codigoUnico = codigoUnico;
    }

    //Getters e Setters

    public List<Materia> getListaMaterias() {
        return listaMaterias;
    }

    public void setListaMaterias(List<Materia> listaMaterias) {
        this.listaMaterias = listaMaterias;
        this.codigosMaterias = new ArrayList<>();
        if (this.listaMaterias != null) {
            for (Materia m : this.listaMaterias) {
                this.codigosMaterias.add(m.getCodigoUnico());
            }
        }
    }

    public List<String> getCodigosMaterias() {
        return codigosMaterias;
    }

    public void setCodigosMaterias(List<String> codigosMaterias) {
        this.codigosMaterias = codigosMaterias;

    }

    public void adicionarMateria(Materia materia) {
        if (this.listaMaterias == null) {
            this.listaMaterias = new ArrayList<>();
        }
        if (this.codigosMaterias == null) {
            this.codigosMaterias = new ArrayList<>();
        }
        if (materia != null && !this.codigosMaterias.contains(materia.getCodigoUnico())) {
            this.listaMaterias.add(materia);
            this.codigosMaterias.add(materia.getCodigoUnico());
        }
    }

    public void removerMateria(Materia materia) {
        if (this.listaMaterias != null && materia != null) {
            this.listaMaterias.remove(materia);
            if (this.codigosMaterias != null) {
                this.codigosMaterias.remove(materia.getCodigoUnico());
            }
        }
    }

    public String getCodigoUnico() {
        return codigoUnico;
    }

    public void setCodigoUnico(String codigoUnico) {
        this.codigoUnico = codigoUnico;
    }

    @Override
    public String toString() {
        String materiasStr = "Nenhuma";
        if (listaMaterias != null && !listaMaterias.isEmpty()) {
            materiasStr = listaMaterias.stream().map(m -> m.getNome() + " (" + m.getCodigoUnico() + ")").collect(Collectors.joining(", "));
        } else if (codigosMaterias != null && !codigosMaterias.isEmpty()) {
            materiasStr = "Códigos: " + String.join(", ", codigosMaterias) + " (não carregadas)";
        }
        return "Professor [Código: " + codigoUnico + ", Nome: " + getNome() + ", CPF: " + getCpf() + ", Matérias: " + materiasStr + "]";
    }

}

