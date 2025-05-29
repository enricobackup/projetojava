package br.fsa.faculdade;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Curso {
    private String nome;
    private List<Materia> listaMaterias;
    private List<String> codigosMaterias; 
    private String codigoUnico;

    //construtor padrao
    public Curso() {
        this.listaMaterias = new ArrayList<>();
        this.codigosMaterias = new ArrayList<>();
    }

    //construtor com todos os parametros
    public Curso(String nome, String codigoUnico, List<Materia> listaMaterias) {
        this.nome = nome;
        this.codigoUnico = codigoUnico;
        this.listaMaterias = (listaMaterias != null) ? new ArrayList<>(listaMaterias) : new ArrayList<>();
        this.codigosMaterias = new ArrayList<>();
        if (this.listaMaterias != null) {
            for (Materia m : this.listaMaterias) {
                this.codigosMaterias.add(m.getCodigoUnico());
            }
        }
    }

    //getters e setters
    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

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

    //metodo pra adicionar uma materia ao curso (CORRIGIDO)
    public void adicionarMateria(Materia materia) {
        if (materia == null) return; 

        if (this.listaMaterias == null) {
            this.listaMaterias = new ArrayList<>();
        }
        if (this.codigosMaterias == null) {
            this.codigosMaterias = new ArrayList<>();
        }


        boolean objectAlreadyExists = false;
        for (Materia existingMateria : this.listaMaterias) {
            if (existingMateria.getCodigoUnico().equalsIgnoreCase(materia.getCodigoUnico())) { 
                objectAlreadyExists = true;
                break;
            }
        }

        if (!objectAlreadyExists) {
            this.listaMaterias.add(materia);
            boolean codeAlreadyExists = false;
            for(String code : this.codigosMaterias){
                if(code.equalsIgnoreCase(materia.getCodigoUnico())){
                    codeAlreadyExists = true;
                    break;
                }
            }
            if (!codeAlreadyExists) {
                 this.codigosMaterias.add(materia.getCodigoUnico());
            }
            materia.setCurso(this); 
        }
    }

    //metodo pra remover uma materia do curso
    public void removerMateria(Materia materia) {
        if (this.listaMaterias != null && materia != null) {
            Materia toRemove = null;
            for(Materia m : this.listaMaterias){
                if(m.getCodigoUnico().equalsIgnoreCase(materia.getCodigoUnico())){
                    toRemove = m;
                    break;
                }
            }
            if (toRemove != null && this.listaMaterias.remove(toRemove)) {
                 if (this.codigosMaterias != null) {
                    String codeToRemove = null;
                    for(String code : this.codigosMaterias){
                        if(code.equalsIgnoreCase(materia.getCodigoUnico())){
                            codeToRemove = code;
                            break;
                        }
                    }
                    if(codeToRemove != null){
                        this.codigosMaterias.remove(codeToRemove);
                    }
                }
                materia.setCurso(null); 
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
            materiasStr = "Códigos: " + String.join(", ", codigosMaterias) + " (objetos não ligados)";
        }
        return "Curso [Código: " + codigoUnico + ", Nome: " + nome + ", Matérias: " + materiasStr + "]";
    }

}

