package br.fsa.faculdade;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Curso {
    private String nome;
    private List<Materia> listaMaterias;
    private List<String> codigosMaterias; // Lista de códigos (para persistência e ligação)
    private String codigoUnico;

    // Construtor padrão
    public Curso() {
        this.listaMaterias = new ArrayList<>();
        this.codigosMaterias = new ArrayList<>();
    }

    // Construtor com todos os parâmetros
    public Curso(String nome, String codigoUnico, List<Materia> listaMaterias) {
        this.nome = nome;
        this.codigoUnico = codigoUnico;
        this.listaMaterias = (listaMaterias != null) ? new ArrayList<>(listaMaterias) : new ArrayList<>();
        // Atualiza a lista de códigos a partir da lista de objetos
        this.codigosMaterias = new ArrayList<>();
        if (this.listaMaterias != null) {
            for (Materia m : this.listaMaterias) {
                this.codigosMaterias.add(m.getCodigoUnico());
            }
        }
    }

    // Getters e Setters
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
        // Atualiza a lista de códigos quando a lista de objetos é definida
        this.codigosMaterias = new ArrayList<>();
        if (this.listaMaterias != null) {
            for (Materia m : this.listaMaterias) {
                this.codigosMaterias.add(m.getCodigoUnico());
            }
        }
    }

    public List<String> getCodigosMaterias() {
        // Retorna a lista de códigos armazenada
        return codigosMaterias;
    }

    public void setCodigosMaterias(List<String> codigosMaterias) {
        // Permite definir a lista de códigos (lida do CSV)
        this.codigosMaterias = codigosMaterias;
        // Não tenta buscar os objetos Materia aqui, isso é feito em GerenciaFaculdade
    }

    // Método para adicionar uma matéria ao curso
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
            materia.setCurso(this); // Garante a associação bidirecional
        }
    }

    // Método para remover uma matéria do curso
    public void removerMateria(Materia materia) {
        if (this.listaMaterias != null && materia != null) {
            if (this.listaMaterias.remove(materia)) {
                 if (this.codigosMaterias != null) {
                    this.codigosMaterias.remove(materia.getCodigoUnico());
                }
                materia.setCurso(null); // Remove a associação
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
        return "Curso [Código: " + codigoUnico + ", Nome: " + nome + ", Matérias: " + materiasStr + "]";
    }

}

