package br.fsa;

import br.fsa.dao.*;
import br.fsa.faculdade.*;
import br.fsa.pessoas.*;
import br.fsa.util.*;

import org.apache.commons.cli.*;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;


public class GerenciaFaculdade {

    //constantes pros nomes de arquivos e separador
    private static final String ARQUIVO_LOG = "log_execucao.txt";
    private static final String ARQUIVO_CURSOS = "cursos.csv";
    private static final String ARQUIVO_MATERIAS = "materias.csv";
    private static final String ARQUIVO_ALUNOS = "alunos.csv";
    private static final String ARQUIVO_PROFESSORES = "professores.csv";
    private static final String ARQUIVO_NOTAS = "notas.csv";
    private static final String SEPARADOR_CSV = ";";

    //listas pra guardar os dados na memoria
    private static List<Curso> cursos = new ArrayList<>();
    private static List<Materia> materias = new ArrayList<>();
    private static List<Aluno> alunos = new ArrayList<>();
    private static List<Professor> professores = new ArrayList<>();
    private static List<Nota> notas = new ArrayList<>();

    //DAOs e Logger
    private static Log logger;
    private static CursoDAO cursoDAO;
    private static MateriaDAO materiaDAO;
    private static AlunoDAO alunoDAO;
    private static ProfessorDAO professorDAO;
    private static NotaDAO notaDAO;

    //scanner pra entrada do usuario (usado nos menus interativos)
    private static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        //inicializar Logger
        logger = new Log(ARQUIVO_LOG);
        logger.logInfo("=== Iniciando execução GerenciaFaculdade (Versão Simplificada) ===");

        //inicializar DAOs
        cursoDAO = new CursoDAO(logger);
        materiaDAO = new MateriaDAO(logger);
        alunoDAO = new AlunoDAO(logger);
        professorDAO = new ProfessorDAO(logger);
        notaDAO = new NotaDAO(logger);

        //carregar dados dos arquivos CSV
        carregarDados();

        //definir as opções da linha de comando
        Options options = definirOpcoesCLI();

        //processar os argumentos da linha de comando
        CommandLineParser parser = new DefaultParser();
        HelpFormatter formatter = new HelpFormatter();
        CommandLine cmd = null;

        try {
            //se nenhum argumento for passado, mostrar ajuda
            if (args.length == 0) {
                formatter.printHelp("java br.fsa.GerenciaFaculdade", options);
                logger.logInfo("Nenhum argumento fornecido, exibindo ajuda.");
                System.exit(0);
            }

            cmd = parser.parse(options, args);

            //executar a ação correspondente
            executarComando(cmd, options, formatter);

        } catch (ParseException e) {
            logger.logErro("Erro ao processar comandos CLI: " + e.getMessage());
            System.err.println("Erro ao processar comandos: " + e.getMessage());
            formatter.printHelp("java br.fsa.GerenciaFaculdade", options);
            System.exit(1);
        } catch (ErroCadastro e) { //apturar a exceção personalizada
            logger.logErro("Erro de Cadastro: " + e.getMessage());
            System.err.println("Erro de Cadastro: " + e.getMessage());
            System.exit(1);
        } catch (Exception e) { //captura genérica para outros erros inesperados
            logger.logErro("Ocorreu um erro inesperado", e);
            System.err.println("Ocorreu um erro inesperado: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        } finally {
            scanner.close(); //fechar o scanner
            logger.logInfo("=== Finalizando execução GerenciaFaculdade (Versão Simplificada) ===");
        }
    }

    private static Options definirOpcoesCLI() {
        Options options = new Options();

        //config do --h / -help
        options.addOption("h", "help", false, "Mostra esta mensagem de ajuda.");
        options.addOption(Option.builder("vd").longOpt("verificaDados").desc("Verifica a consistência dos dados salvos.").build());
        options.addOption(Option.builder("aa").longOpt("addAluno").desc("Adiciona um novo aluno (modo interativo).").build());
        options.addOption(Option.builder("la").longOpt("listaAluno").desc("Lista todos os alunos cadastrados.").build());
        options.addOption(Option.builder("ea").longOpt("editaAluno").hasArg().argName("codigo").desc("Edita o aluno com o código especificado (modo interativo).").build());
        options.addOption(Option.builder("ap").longOpt("addProfessor").desc("Adiciona um novo professor (modo interativo).").build());
        options.addOption(Option.builder("lp").longOpt("listaProfessor").desc("Lista todos os professores cadastrados.").build());
        options.addOption(Option.builder("ep").longOpt("editaProfessor").hasArg().argName("codigo").desc("Edita o professor com o código especificado (modo interativo).").build());
        options.addOption(Option.builder("ac").longOpt("addCurso").desc("Adiciona um novo curso (modo interativo).").build());
        options.addOption(Option.builder("lc").longOpt("listaCurso").desc("Lista todos os cursos cadastrados.").build());
        options.addOption(Option.builder("am").longOpt("addMateria").desc("Adiciona uma nova matéria (modo interativo).").build());
        options.addOption(Option.builder("lm").longOpt("listaMateria").hasArg().argName("codigo_curso").optionalArg(true).desc("Lista as matérias (opcionalmente filtra por código do curso).").build());
        options.addOption(Option.builder("an").longOpt("addNota").numberOfArgs(4).argName("tipo> <codAluno> <codMateria> <nota").desc("Adiciona nota: -an <tipo> <codAluno> <codMateria> <nota>").build());
        options.addOption(Option.builder("fm").longOpt("FechaMedia").hasArg().argName("codigo_aluno").desc("Calcula e exibe a média final do aluno especificado.").build());

        return options;
    }

    private static void executarComando(CommandLine cmd, Options options, HelpFormatter formatter) throws ErroCadastro, IOException, ParseException {
        //selecionar oq o usuario vai fazer
        if (cmd.hasOption("help")) {
            formatter.printHelp("java br.fsa.GerenciaFaculdade", options);
        } else if (cmd.hasOption("verificaDados")) {
            verificarDados();
        } else if (cmd.hasOption("addAluno")) {
            adicionarAlunoInterativo();
        } else if (cmd.hasOption("listaAluno")) {
            listarAlunos();
        } else if (cmd.hasOption("editaAluno")) {
            String codigoAluno = cmd.getOptionValue("editaAluno");
            editarAlunoInterativo(codigoAluno);
        } else if (cmd.hasOption("addProfessor")) {
            adicionarProfessorInterativo();
        } else if (cmd.hasOption("listaProfessor")) {
            listarProfessores();
        } else if (cmd.hasOption("editaProfessor")) {
            String codigoProfessor = cmd.getOptionValue("editaProfessor");
            editarProfessorInterativo(codigoProfessor);
        } else if (cmd.hasOption("addCurso")) {
            adicionarCursoInterativo();
        } else if (cmd.hasOption("listaCurso")) {
            listarCursos();
        } else if (cmd.hasOption("addMateria")) {
            adicionarMateriaInterativo();
        } else if (cmd.hasOption("listaMateria")) {
            String codigoCurso = cmd.getOptionValue("listaMateria"); //pode ser null se nao fornecido
            listarMaterias(codigoCurso);
        } else if (cmd.hasOption("addNota")) {
             String[] argsNota = cmd.getOptionValues("addNota");
             if (argsNota != null && argsNota.length == 4) {
                 adicionarNota(argsNota[0], argsNota[1], argsNota[2], argsNota[3]);
             } else {
                 throw new ParseException("Comando -addNota requer 4 argumentos: <tipo> <codAluno> <codMateria> <nota>");
             }
        } else if (cmd.hasOption("FechaMedia")) {
            String codigoAlunoMedia = cmd.getOptionValue("FechaMedia");
            fecharMediaAluno(codigoAlunoMedia);
        } else {
            logger.logErro("Comando não reconhecido ou incompleto.");
            System.err.println("Comando não reconhecido ou incompleto. Use -help para ver as opções.");
            formatter.printHelp("java br.fsa.GerenciaFaculdade", options);
        }
    }

    //METODOS PARA CARREGAR E SALVAR

    private static void carregarDados() {
        logger.logInfo("Iniciando carregamento de dados...");
        try {
            //1. Cursos
            cursoDAO.setArquivo(ARQUIVO_CURSOS);
            cursos = cursoDAO.leArquivo();
            logger.logInfo(cursos.size() + " cursos carregados.");

            //2. Matérias
            materiaDAO.setArquivo(ARQUIVO_MATERIAS);
            materias = materiaDAO.leArquivo();
            logger.logInfo(materias.size() + " matérias carregadas.");

            //3. Alunos
            alunoDAO.setArquivo(ARQUIVO_ALUNOS);
            alunos = alunoDAO.leArquivo();
            logger.logInfo(alunos.size() + " alunos carregados.");

            //4. Professores
            professorDAO.setArquivo(ARQUIVO_PROFESSORES);
            professores = professorDAO.leArquivo();
            logger.logInfo(professores.size() + " professores carregados.");

            //5. Notas
            notaDAO.setArquivo(ARQUIVO_NOTAS);
            notas = notaDAO.leArquivo();
            logger.logInfo(notas.size() + " notas carregadas.");

            //6. Ligar Referências (após tudo carregado)
            ligarReferencias();

            logger.logInfo("Carregamento e ligação de dados concluído.");

        } catch (IOException e) {
            logger.logErro("Falha crítica ao carregar dados dos arquivos CSV. O programa pode não funcionar corretamente.", e);
            System.err.println("ERRO CRÍTICO: Não foi possível carregar os dados iniciais. Verifique os arquivos CSV e o log.");
            //decide se continua ou encerra
            //System.exit(1);
        }
    }

    private static void ligarReferencias() {
        logger.logInfo("Iniciando ligação de referências entre objetos...");

        //conecta aluno ao curso
        for (Aluno aluno : alunos) {
            if (aluno.getCodigoCurso() != null) {
                Curso cursoEncontrado = buscarCursoPorCodigo(aluno.getCodigoCurso());
                if (cursoEncontrado != null) {
                    aluno.setCurso(cursoEncontrado);
                } else {
                    logger.logErro("Aluno " + aluno.getCodigoUnico() + " referencia curso inexistente: " + aluno.getCodigoCurso());
                }
            }
        }

        //coloca materia ao curso (e vice-versa)
        for (Materia materia : materias) {
            if (materia.getCodigoCurso() != null) {
                Curso cursoEncontrado = buscarCursoPorCodigo(materia.getCodigoCurso());
                if (cursoEncontrado != null) {
                    materia.setCurso(cursoEncontrado);
                    logger.logInfo("Ligando matéria " + materia.getCodigoUnico() + " ao curso " + cursoEncontrado.getCodigoUnico()); // Added log
                    //garante a ligação bidirecional (se ainda não existir)
                    boolean materiaJaNoCurso = false;
                    for(Materia mCurso : cursoEncontrado.getListaMaterias()){
                        if(mCurso.getCodigoUnico().equals(materia.getCodigoUnico())){
                            materiaJaNoCurso = true;
                            break;
                        }
                    }
                    if(!materiaJaNoCurso){
                        cursoEncontrado.adicionarMateria(materia);
                        // Add log here
                        logger.logInfo("--> Adicionando matéria " + materia.getCodigoUnico() + " à listaMaterias do curso " + cursoEncontrado.getCodigoUnico());
                    }
                } else {
                    logger.logErro("Matéria " + materia.getCodigoUnico() + " referencia curso inexistente: " + materia.getCodigoCurso());
                }
            }
        }

        //Ligar Professores às Materias (e vice-versa, se precisar)
        for (Professor professor : professores) {
            List<Materia> materiasDoProfessor = new ArrayList<>();
            if (professor.getCodigosMaterias() != null) {
                for (String codMateria : professor.getCodigosMaterias()) {
                    Materia materiaEncontrada = buscarMateriaPorCodigo(codMateria);
                    if (materiaEncontrada != null) {
                        materiasDoProfessor.add(materiaEncontrada);
                        //opcional: adicionar professor à materia (se a classe Materia tiver essa referencia)
                        //materiaEncontrada.setProfessor(professor);
                    } else {
                        logger.logErro("Professor " + professor.getCodigoUnico() + " referencia matéria inexistente: " + codMateria);
                    }
                }
            }
            professor.setListaMaterias(materiasDoProfessor); //define a lista de objetos Materia
        }

        //Ligar Notas aos Alunos e Materias (e vice-versa)
        for (Nota nota : notas) {
            boolean notaOk = true;
            if (nota.getCodigoAluno() != null) {
                Aluno alunoEncontrado = buscarAlunoPorCodigo(nota.getCodigoAluno());
                if (alunoEncontrado != null) {
                    nota.setAluno(alunoEncontrado);
                } else {
                    logger.logErro("Nota (Tipo: " + nota.getTipoAvaliacao() + ") referencia aluno inexistente: " + nota.getCodigoAluno());
                    notaOk = false;
                }
            } else {
                 logger.logErro("Nota (Tipo: " + nota.getTipoAvaliacao() + ") sem codigo de aluno.");
                 notaOk = false;
            }

            if (nota.getCodigoMateria() != null) {
                Materia materiaEncontrada = buscarMateriaPorCodigo(nota.getCodigoMateria());
                if (materiaEncontrada != null) {
                    nota.setMateria(materiaEncontrada);
                    //adiciona a nota à lista da materia (se ainda nao existir)
                    boolean notaJaNaMateria = false;
                    for(Nota nMat : materiaEncontrada.getListaNotas()){
                        //precisa de um equals melhor na classe Nota ou comparar atributos
                        if(nMat.getAluno().getCodigoUnico().equals(nota.getAluno().getCodigoUnico()) &&
                           nMat.getTipoAvaliacao().equals(nota.getTipoAvaliacao())){
                            notaJaNaMateria = true;
                            break;
                        }
                    }
                    if(notaOk && !notaJaNaMateria){
                         materiaEncontrada.adicionarNota(nota);
                    }
                } else {
                    logger.logErro("Nota (Tipo: " + nota.getTipoAvaliacao() + ", Aluno: "+nota.getCodigoAluno()+") referencia materia inexistente: " + nota.getCodigoMateria());
                }
            } else {
                 logger.logErro("Nota (Tipo: " + nota.getTipoAvaliacao() + ", Aluno: "+nota.getCodigoAluno()+") sem codigo de materia.");
            }
        }
        // adicionado: garantir que a lista de objetos Materia nos Cursos seja populada
        logger.logInfo("Iniciando ligação final Curso -> Lista<Materia>...");
        for (Curso curso : cursos) {
            if (curso.getCodigosMaterias() != null && !curso.getCodigosMaterias().isEmpty()) {
                // limpa a lista pra garantir que nao tenha duplicatas se ligarReferencias for chamado de novo
                // curso.setListaMaterias(new ArrayList<>()); // o metodo adicionarMateria ja lida com isso
                for (String codMateria : curso.getCodigosMaterias()) {
                    Materia materiaEncontrada = buscarMateriaPorCodigo(codMateria);
                    if (materiaEncontrada != null) {
                        // usa o metodo adicionarMateria pra garantir a logica encapsulada
                        curso.adicionarMateria(materiaEncontrada);
                        // logger.logInfo("Adicionando matéria " + materiaEncontrada.getCodigoUnico() + " à lista do curso " + curso.getCodigoUnico());
                    } else {
                        logger.logErro("Curso " + curso.getCodigoUnico() + " referencia codigo de materia inexistente: " + codMateria + " (não adicionado à lista de objetos).");
                    }
                }
            }
        }
        logger.logInfo("Ligação final Curso -> Lista<Materia> concluída.");

        logger.logInfo("Ligação de referências concluída.");
    }

    private static void salvarTodosDados() throws IOException {
        logger.logInfo("Iniciando salvamento de todos os dados...");
        try {
            cursoDAO.escreveArquivo(cursos);
            materiaDAO.escreveArquivo(materias);
            alunoDAO.escreveArquivo(alunos);
            professorDAO.escreveArquivo(professores);
            notaDAO.escreveArquivo(notas);
            logger.logInfo("Salvamento de todos os dados concluído.");
        } catch (IOException e) {
            logger.logErro("Erro ao salvar dados para os arquivos CSV.", e);
            throw e; //Relança para tratamento superior se necessário
        }
    }

    //--- Métodos de Busca (Iterando Listas) ---

    private static Curso buscarCursoPorCodigo(String codigo) {
        for (Curso curso : cursos) {
            if (curso.getCodigoUnico().equalsIgnoreCase(codigo)) {
                return curso;
            }
        }
        return null;
    }

    private static Materia buscarMateriaPorCodigo(String codigo) {
        for (Materia materia : materias) {
            if (materia.getCodigoUnico().equalsIgnoreCase(codigo)) {
                return materia;
            }
        }
        return null;
    }

    private static Aluno buscarAlunoPorCodigo(String codigo) {
        for (Aluno aluno : alunos) {
            if (aluno.getCodigoUnico().equalsIgnoreCase(codigo)) {
                return aluno;
            }
        }
        return null;
    }

    private static Professor buscarProfessorPorCodigo(String codigo) {
        for (Professor professor : professores) {
            if (professor.getCodigoUnico().equalsIgnoreCase(codigo)) {
                return professor;
            }
        }
        return null;
    }

    //--- Métodos para cada Comando CLI ---

    private static void verificarDados() {
        logger.logInfo("Executando verificação de dados...");
        System.out.println("Verificando consistência dos dados...");
        boolean inconsistencias = false;

        //1. Verificar se todos os alunos têm curso válido
        for (Aluno aluno : alunos) {
            if (aluno.getCurso() == null) {
                //Verifica se o código do curso existe mas não foi encontrado
                if(aluno.getCodigoCurso() != null && buscarCursoPorCodigo(aluno.getCodigoCurso()) == null){
                    System.err.println("Inconsistência: Aluno " + aluno.getCodigoUnico() + " está associado a um código de curso inválido/removido: " + aluno.getCodigoCurso());
                    inconsistencias = true;
                } else if (aluno.getCodigoCurso() == null) {
                    System.err.println("Inconsistência: Aluno " + aluno.getCodigoUnico() + " (" + aluno.getNome() + ") está sem código de curso associado.");
                    inconsistencias = true;
                }
            }
        }

        //2. Verificar se todas as notas têm matéria e aluno associados válidos
        for (Nota nota : notas) {
            boolean notaInconsistente = false;
            if (nota.getAluno() == null) {
                 if(nota.getCodigoAluno() != null && buscarAlunoPorCodigo(nota.getCodigoAluno()) == null){
                     System.err.println("Inconsistência: Nota (Tipo: " + nota.getTipoAvaliacao() + ", Valor: " + nota.getValorNota() + ") está associada a um código de aluno inválido/removido: " + nota.getCodigoAluno());
                     notaInconsistente = true;
                 } else if (nota.getCodigoAluno() == null){
                     System.err.println("Inconsistência: Nota (Tipo: " + nota.getTipoAvaliacao() + ", Valor: " + nota.getValorNota() + ") está sem código de aluno associado.");
                     notaInconsistente = true;
                 }
            }
            if (nota.getMateria() == null) {
                 if(nota.getCodigoMateria() != null && buscarMateriaPorCodigo(nota.getCodigoMateria()) == null){
                     System.err.println("Inconsistência: Nota (Tipo: " + nota.getTipoAvaliacao() + ", Valor: " + nota.getValorNota() + ", Aluno: " + nota.getCodigoAluno() + ") está associada a um código de matéria inválido/removido: " + nota.getCodigoMateria());
                     notaInconsistente = true;
                 } else if (nota.getCodigoMateria() == null){
                     System.err.println("Inconsistência: Nota (Tipo: " + nota.getTipoAvaliacao() + ", Valor: " + nota.getValorNota() + ", Aluno: " + nota.getCodigoAluno() + ") está sem código de matéria associado.");
                     notaInconsistente = true;
                 }
            }
            if(notaInconsistente) inconsistencias = true;
        }

        //3. Verificar se matérias estão associadas a cursos válidos
        for(Materia materia : materias) {
            if (materia.getCurso() == null) {
                 if(materia.getCodigoCurso() != null && buscarCursoPorCodigo(materia.getCodigoCurso()) == null){
                     System.err.println("Inconsistência: Matéria " + materia.getCodigoUnico() + " está associada a um código de curso inválido/removido: " + materia.getCodigoCurso());
                     inconsistencias = true;
                 } else if (materia.getCodigoCurso() == null){
                     System.err.println("Inconsistência: Matéria " + materia.getCodigoUnico() + " (" + materia.getNome() + ") está sem código de curso associado.");
                     inconsistencias = true;
                 }
            }
        }

        //4. Verificar se professores têm matérias válidas associadas
        for (Professor professor : professores) {
            if (professor.getListaMaterias() == null || professor.getListaMaterias().size() != professor.getCodigosMaterias().size()) {
                 //Verifica se algum código de matéria não foi encontrado durante a ligação
                 if(professor.getCodigosMaterias() != null){
                     for(String codMatProf : professor.getCodigosMaterias()){
                         boolean encontrado = false;
                         if(professor.getListaMaterias() != null){
                             for(Materia matProf : professor.getListaMaterias()){
                                 if(matProf.getCodigoUnico().equals(codMatProf)){
                                     encontrado = true;
                                     break;
                                 }
                             }
                         }
                         if(!encontrado){
                             System.err.println("Inconsistência: Professor " + professor.getCodigoUnico() + " está associado a um código de matéria inválido/removido: " + codMatProf);
                             inconsistencias = true;
                         }
                     }
                 }
            }
        }

        if (!inconsistencias) {
            System.out.println("Verificação concluída. Nenhum problema de consistência encontrado.");
            logger.logInfo("Verificação de dados concluída sem inconsistências.");
        } else {
            System.out.println("Verificação concluída. Foram encontradas inconsistências (ver mensagens acima).");
            logger.logErro("Verificação de dados concluída COM inconsistências.");
        }
    }

    //--- Métodos Interativos para Aluno ---

    private static void adicionarAlunoInterativo() throws ErroCadastro, IOException {
        System.out.println("--- Adicionar Novo Aluno ---");
        Aluno aluno = new Aluno();

        aluno.setNome(lerEntradaObrigatoria("Nome Completo"));
        aluno.setDataNascimento(lerEntradaObrigatoria("Data de Nascimento (dd/MM/yyyy)"));
        aluno.setRua(lerEntradaObrigatoria("Rua"));
        aluno.setNumero(lerEntradaObrigatoria("Número"));
        aluno.setCidade(lerEntradaObrigatoria("Cidade"));
        aluno.setEstado(lerEntradaObrigatoria("Estado (UF)"));
        aluno.setCep(lerEntradaObrigatoria("CEP (somente números)"));
        aluno.setTelefone(lerEntradaObrigatoria("Telefone"));
        aluno.setGenero(lerEntradaObrigatoria("Gênero"));
        aluno.setRg(lerEntradaObrigatoria("RG"));

        String cpf = lerEntradaObrigatoria("CPF (somente números)");
        while (!Utilitarios.verificaCPF(cpf)) {
            System.out.println("CPF inválido. Tente novamente.");
            cpf = lerEntradaObrigatoria("CPF (somente números)");
        }
        aluno.setCpf(cpf);

        String codigoUnico = lerEntradaObrigatoria("Código Único do Aluno");
        Utilitarios.verificaCodigoUnicoAluno(codigoUnico, alunos);
        aluno.setCodigoUnico(codigoUnico);

        listarCursosDisponiveis();
        String codigoCurso = lerEntradaObrigatoria("Código do Curso desejado");
        Curso cursoSelecionado = buscarCursoPorCodigo(codigoCurso);
        while (cursoSelecionado == null) {
            System.out.println("Código de curso inválido. Tente novamente.");
            codigoCurso = lerEntradaObrigatoria("Código do Curso desejado");
            cursoSelecionado = buscarCursoPorCodigo(codigoCurso);
        }
        aluno.setCodigoCurso(cursoSelecionado.getCodigoUnico()); //Salva o código
        aluno.setCurso(cursoSelecionado); //Define a referência

        alunos.add(aluno);
        salvarTodosDados(); //Salva após adicionar
        System.out.println("Aluno " + aluno.getNome() + " adicionado com sucesso!");
        logger.logInfo("Aluno adicionado: " + aluno.getCodigoUnico() + " - " + aluno.getNome());
    }

    private static void listarAlunos() {
        System.out.println("--- Lista de Alunos Cadastrados ---");
        if (alunos.isEmpty()) {
            System.out.println("Nenhum aluno cadastrado.");
            return;
        }
        //Ordena por nome usando um Comparator simples
        Collections.sort(alunos, new Comparator<Aluno>() {
            @Override
            public int compare(Aluno a1, Aluno a2) {
                return a1.getNome().compareToIgnoreCase(a2.getNome());
            }
        });
        for (Aluno aluno : alunos) {
            System.out.println(aluno.toString()); //Usa o toString() da classe Aluno
        }
        logger.logInfo("Listagem de alunos executada.");
    }

    private static void editarAlunoInterativo(String codigoAluno) throws ErroCadastro, IOException {
         Aluno aluno = buscarAlunoPorCodigo(codigoAluno);
         if (aluno == null) {
             throw new ErroCadastro("Aluno com código " + codigoAluno + " não encontrado.");
         }

         System.out.println("--- Editar Aluno: " + aluno.getNome() + " (" + codigoAluno + ") ---");
         System.out.println("(Deixe em branco para manter o valor atual)");

         String nome = lerEntradaOpcional("Novo Nome Completo", aluno.getNome());
         if (!nome.isEmpty()) aluno.setNome(nome);

         String dataNasc = lerEntradaOpcional("Nova Data de Nascimento", aluno.getDataNascimento());
         if (!dataNasc.isEmpty()) aluno.setDataNascimento(dataNasc);

         String rua = lerEntradaOpcional("Nova Rua", aluno.getRua());
         if (!rua.isEmpty()) aluno.setRua(rua);

         String numero = lerEntradaOpcional("Novo Número", aluno.getNumero());
         if (!numero.isEmpty()) aluno.setNumero(numero);

         String cidade = lerEntradaOpcional("Nova Cidade", aluno.getCidade());
         if (!cidade.isEmpty()) aluno.setCidade(cidade);

         String estado = lerEntradaOpcional("Novo Estado (UF)", aluno.getEstado());
         if (!estado.isEmpty()) aluno.setEstado(estado);

         String cep = lerEntradaOpcional("Novo CEP", aluno.getCep());
         if (!cep.isEmpty()) aluno.setCep(cep);

         String telefone = lerEntradaOpcional("Novo Telefone", aluno.getTelefone());
         if (!telefone.isEmpty()) aluno.setTelefone(telefone);

         String genero = lerEntradaOpcional("Novo Gênero", aluno.getGenero());
         if (!genero.isEmpty()) aluno.setGenero(genero);

         String rg = lerEntradaOpcional("Novo RG", aluno.getRg());
         if (!rg.isEmpty()) aluno.setRg(rg);

         //CPF e Código Único não são editáveis

         listarCursosDisponiveis();
         String codigoCursoAtual = (aluno.getCurso() != null) ? aluno.getCurso().getCodigoUnico() : "";
         String codigoCursoNovo = lerEntradaOpcional("Novo Código do Curso", codigoCursoAtual);
         if (!codigoCursoNovo.isEmpty() && !codigoCursoNovo.equals(codigoCursoAtual)) {
             Curso cursoSelecionado = buscarCursoPorCodigo(codigoCursoNovo);
             while (cursoSelecionado == null) {
                 System.out.println("Código de curso inválido. Tente novamente ou deixe em branco para manter ["+codigoCursoAtual+"].");
                 codigoCursoNovo = lerEntradaOpcional("Novo Código do Curso", codigoCursoAtual);
                 if (codigoCursoNovo.isEmpty() || codigoCursoNovo.equals(codigoCursoAtual)) {
                     cursoSelecionado = aluno.getCurso(); //Mantem o atual
                     break;
                 }
                 cursoSelecionado = buscarCursoPorCodigo(codigoCursoNovo);
             }
             if (cursoSelecionado != null) {
                 aluno.setCodigoCurso(cursoSelecionado.getCodigoUnico());
                 aluno.setCurso(cursoSelecionado);
             }
         }

         salvarTodosDados(); //Salva após editar
         System.out.println("Aluno " + aluno.getNome() + " atualizado com sucesso!");
         logger.logInfo("Aluno editado: " + aluno.getCodigoUnico());
    }

    //--- Métodos Interativos para Professor ---

    private static void adicionarProfessorInterativo() throws ErroCadastro, IOException {
        System.out.println("--- Adicionar Novo Professor ---");
        Professor professor = new Professor();

        professor.setNome(lerEntradaObrigatoria("Nome Completo"));
        professor.setDataNascimento(lerEntradaObrigatoria("Data de Nascimento (dd/MM/yyyy)"));
        professor.setRua(lerEntradaObrigatoria("Rua"));
        professor.setNumero(lerEntradaObrigatoria("Número"));
        professor.setCidade(lerEntradaObrigatoria("Cidade"));
        professor.setEstado(lerEntradaObrigatoria("Estado (UF)"));
        professor.setCep(lerEntradaObrigatoria("CEP (somente números)"));
        professor.setTelefone(lerEntradaObrigatoria("Telefone"));
        professor.setGenero(lerEntradaObrigatoria("Gênero"));
        professor.setRg(lerEntradaObrigatoria("RG"));

        String cpf = lerEntradaObrigatoria("CPF (somente números)");
        while (!Utilitarios.verificaCPF(cpf)) {
            System.out.println("CPF inválido. Tente novamente.");
            cpf = lerEntradaObrigatoria("CPF (somente números)");
        }
        professor.setCpf(cpf);

        String codigoUnico = lerEntradaObrigatoria("Código Único do Professor");
        Utilitarios.verificaCodigoUnicoProfessor(codigoUnico, professores);
        professor.setCodigoUnico(codigoUnico);

        //Adicionar matérias
        List<String> codigosMateriasSelecionadas = new ArrayList<>();
        List<Materia> materiasSelecionadas = new ArrayList<>();
        listarMateriasDisponiveis();
        System.out.println("Digite os códigos das matérias que o professor leciona, separados por vírgula (ex: MAT01,FIS02), ou deixe em branco:");
        String codigosMateriasInput = scanner.nextLine().trim();
        if (!codigosMateriasInput.isEmpty()) {
            String[] codigos = codigosMateriasInput.split(",");
            for (String codMateria : codigos) {
                String codTrimmed = codMateria.trim();
                Materia materia = buscarMateriaPorCodigo(codTrimmed);
                if (materia != null) {
                    codigosMateriasSelecionadas.add(codTrimmed);
                    materiasSelecionadas.add(materia);
                } else {
                    System.out.println("AVISO: Matéria com código " + codTrimmed + " não encontrada. Será ignorada.");
                    logger.logErro("Tentativa de adicionar matéria inexistente ("+codTrimmed+") ao professor " + codigoUnico);
                }
            }
        }
        professor.setCodigosMaterias(codigosMateriasSelecionadas);
        professor.setListaMaterias(materiasSelecionadas);

        professores.add(professor);
        salvarTodosDados();
        System.out.println("Professor " + professor.getNome() + " adicionado com sucesso!");
        logger.logInfo("Professor adicionado: " + professor.getCodigoUnico() + " - " + professor.getNome());
    }

    private static void listarProfessores() {
        System.out.println("--- Lista de Professores Cadastrados ---");
        if (professores.isEmpty()) {
            System.out.println("Nenhum professor cadastrado.");
            return;
        }
        //Ordena por nome
        Collections.sort(professores, new Comparator<Professor>() {
             @Override
            public int compare(Professor p1, Professor p2) {
                return p1.getNome().compareToIgnoreCase(p2.getNome());
            }
        });
        for (Professor professor : professores) {
            System.out.println(professor.toString()); //Usa o toString() da classe Professor
        }
        logger.logInfo("Listagem de professores executada.");
    }

    private static void editarProfessorInterativo(String codigoProfessor) throws ErroCadastro, IOException {
        Professor professor = buscarProfessorPorCodigo(codigoProfessor);
        if (professor == null) {
            throw new ErroCadastro("Professor com código " + codigoProfessor + " não encontrado.");
        }

        System.out.println("--- Editar Professor: " + professor.getNome() + " (" + codigoProfessor + ") ---");
        System.out.println("(Deixe em branco para manter o valor atual)");

        String nome = lerEntradaOpcional("Novo Nome Completo", professor.getNome());
        if (!nome.isEmpty()) professor.setNome(nome);

        String dataNasc = lerEntradaOpcional("Nova Data de Nascimento", professor.getDataNascimento());
        if (!dataNasc.isEmpty()) professor.setDataNascimento(dataNasc);

        //... (adicionar campos restantes: rua, numero, cidade, estado, cep, telefone, genero, rg)

        //Editar matérias
        String materiasAtuaisStr = "Nenhuma";
        if(professor.getListaMaterias() != null && !professor.getListaMaterias().isEmpty()){
            List<String> cods = new ArrayList<>();
            for(Materia m : professor.getListaMaterias()){
                cods.add(m.getCodigoUnico());
            }
            materiasAtuaisStr = String.join(", ", cods);
        }
        System.out.println("Matérias atuais: " + materiasAtuaisStr);
        listarMateriasDisponiveis();
        System.out.println("Digite a NOVA lista de códigos de matérias (separados por vírgula) ou deixe em branco para manter a atual:");
        String codigosMateriasInput = scanner.nextLine().trim();

        if (!codigosMateriasInput.isEmpty()) {
            List<String> novosCodigos = new ArrayList<>();
            List<Materia> novasMaterias = new ArrayList<>();
            String[] codigos = codigosMateriasInput.split(",");
            for (String codMateria : codigos) {
                String codTrimmed = codMateria.trim();
                Materia materia = buscarMateriaPorCodigo(codTrimmed);
                if (materia != null) {
                    novosCodigos.add(codTrimmed);
                    novasMaterias.add(materia);
                } else {
                    System.out.println("AVISO: Matéria com código " + codTrimmed + " não encontrada. Será ignorada.");
                    logger.logErro("Tentativa de editar matéria inexistente ("+codTrimmed+") para o professor " + codigoProfessor);
                }
            }
            professor.setCodigosMaterias(novosCodigos);
            professor.setListaMaterias(novasMaterias);
        }

        salvarTodosDados();
        System.out.println("Professor " + professor.getNome() + " atualizado com sucesso!");
        logger.logInfo("Professor editado: " + professor.getCodigoUnico());
    }

    //--- Métodos Interativos para Curso ---

    private static void adicionarCursoInterativo() throws ErroCadastro, IOException {
        System.out.println("--- Adicionar Novo Curso ---");
        Curso curso = new Curso();

        curso.setNome(lerEntradaObrigatoria("Nome do Curso"));
        String codigoUnico = lerEntradaObrigatoria("Código Único do Curso");
        Utilitarios.verificaCodigoUnicoCurso(codigoUnico, cursos);
        curso.setCodigoUnico(codigoUnico);

        //Adicionar matérias ao curso
        List<Materia> materiasDoCurso = new ArrayList<>();
        listarMateriasDisponiveis();
        System.out.println("Digite os códigos das matérias que pertencem a este curso (separados por vírgula), ou deixe em branco:");
        String codigosMateriasInput = scanner.nextLine().trim();
        if (!codigosMateriasInput.isEmpty()) {
            String[] codigos = codigosMateriasInput.split(",");
            for (String codMateria : codigos) {
                String codTrimmed = codMateria.trim();
                Materia materia = buscarMateriaPorCodigo(codTrimmed);
                if (materia != null) {
                    //Verifica se a matéria já pertence a outro curso
                    if (materia.getCurso() != null && !materia.getCurso().getCodigoUnico().equals(codigoUnico)) {
                         System.out.println("AVISO: Matéria " + codTrimmed + " já pertence ao curso " + materia.getCurso().getCodigoUnico() + ". Não pode ser adicionada a este curso.");
                         logger.logErro("Tentativa de adicionar matéria "+codTrimmed+" que já pertence a outro curso ao curso " + codigoUnico);
                    } else {
                        materiasDoCurso.add(materia);
                        materia.setCodigoCurso(codigoUnico); //Garante a associação bidirecional
                        materia.setCurso(curso);
                    }
                } else {
                    System.out.println("AVISO: Matéria com código " + codTrimmed + " não encontrada. Será ignorada.");
                     logger.logErro("Tentativa de adicionar matéria inexistente ("+codTrimmed+") ao curso " + codigoUnico);
                }
            }
        }
        curso.setListaMaterias(materiasDoCurso);

        cursos.add(curso);
        salvarTodosDados();
        System.out.println("Curso " + curso.getNome() + " adicionado com sucesso!");
        logger.logInfo("Curso adicionado: " + curso.getCodigoUnico() + " - " + curso.getNome());
    }

    private static void listarCursos() {
        System.out.println("--- Lista de Cursos Cadastrados ---");
        if (cursos.isEmpty()) {
            System.out.println("Nenhum curso cadastrado.");
            return;
        }
        //Ordena por nome
        Collections.sort(cursos, new Comparator<Curso>() {
            @Override
            public int compare(Curso c1, Curso c2) {
                return c1.getNome().compareToIgnoreCase(c2.getNome());
            }
        });
        for (Curso curso : cursos) {
            System.out.println(curso.toString()); //Usa o toString() da classe Curso
            //Lista matérias do curso
            if (curso.getListaMaterias() != null && !curso.getListaMaterias().isEmpty()) {
                List<String> nomesMaterias = new ArrayList<>();
                for(Materia m : curso.getListaMaterias()){
                    nomesMaterias.add(m.getCodigoUnico() + "-" + m.getNome());
                }
                System.out.println("  Matérias: " + String.join(", ", nomesMaterias));
            }
        }
        logger.logInfo("Listagem de cursos executada.");
    }

    //--- Métodos Interativos para Matéria ---

    private static void adicionarMateriaInterativo() throws ErroCadastro, IOException {
        System.out.println("--- Adicionar Nova Matéria ---");
        Materia materia = new Materia();

        materia.setNome(lerEntradaObrigatoria("Nome da Matéria"));
        String codigoUnico = lerEntradaObrigatoria("Código Único da Matéria");
        Utilitarios.verificaCodigoUnicoMateria(codigoUnico, materias);
        materia.setCodigoUnico(codigoUnico);

        listarCursosDisponiveis();
        String codigoCurso = lerEntradaObrigatoria("Código do Curso ao qual a matéria pertence");
        Curso cursoSelecionado = buscarCursoPorCodigo(codigoCurso);
        while (cursoSelecionado == null) {
            System.out.println("Código de curso inválido. Tente novamente.");
            codigoCurso = lerEntradaObrigatoria("Código do Curso ao qual a matéria pertence");
            cursoSelecionado = buscarCursoPorCodigo(codigoCurso);
        }
        materia.setCodigoCurso(cursoSelecionado.getCodigoUnico());
        materia.setCurso(cursoSelecionado);

        materias.add(materia);
        //Adiciona ao curso também (se não existir)
        boolean materiaJaNoCurso = false;
        for(Materia mCurso : cursoSelecionado.getListaMaterias()){
            if(mCurso.getCodigoUnico().equals(materia.getCodigoUnico())){
                materiaJaNoCurso = true;
                break;
            }
        }
        if(!materiaJaNoCurso){
            cursoSelecionado.adicionarMateria(materia);
        }

        salvarTodosDados();
        System.out.println("Matéria " + materia.getNome() + " adicionada com sucesso ao curso " + cursoSelecionado.getNome() + "!");
        logger.logInfo("Matéria adicionada: " + materia.getCodigoUnico() + " - " + materia.getNome() + " (Curso: " + cursoSelecionado.getCodigoUnico() + ")");
    }

    //Lista matérias, opcionalmente filtrando por curso
    private static void listarMaterias(String codigoCursoFiltro) {
        System.out.println("--- Lista de Matérias Cadastradas ---");
        List<Materia> materiasParaListar = new ArrayList<>();

        if (codigoCursoFiltro != null && !codigoCursoFiltro.trim().isEmpty()) {
            Curso cursoFiltro = buscarCursoPorCodigo(codigoCursoFiltro.trim());
            if (cursoFiltro == null) {
                System.out.println("Curso com código " + codigoCursoFiltro + " não encontrado.");
                logger.logErro("Tentativa de listar matérias de curso inexistente: " + codigoCursoFiltro);
                return;
            }
            System.out.println("Filtrando pelo curso: " + cursoFiltro.getNome() + " (" + codigoCursoFiltro + ")");
            //Pega as matérias diretamente do objeto curso
            if(cursoFiltro.getListaMaterias() != null){
                materiasParaListar.addAll(cursoFiltro.getListaMaterias());
            }
        } else {
            materiasParaListar = materias; //Lista todas
        }

        if (materiasParaListar.isEmpty()) {
            System.out.println("Nenhuma matéria encontrada" + (codigoCursoFiltro != null ? " para este curso." : "."));
            return;
        }

        //Ordena por nome
        Collections.sort(materiasParaListar, new Comparator<Materia>() {
            @Override
            public int compare(Materia m1, Materia m2) {
                return m1.getNome().compareToIgnoreCase(m2.getNome());
            }
        });
        for (Materia materia : materiasParaListar) {
            System.out.println(materia.toString()); //Usa o toString() da classe Materia
        }
        logger.logInfo("Listagem de matérias executada" + (codigoCursoFiltro != null ? " para o curso " + codigoCursoFiltro : "."));
    }


    //--- Métodos para Nota e Média ---

    private static void adicionarNota(String tipoAvaliacao, String codAluno, String codMateria, String valorNotaStr) throws ErroCadastro, IOException {
        logger.logInfo("Tentando adicionar nota: Tipo=" + tipoAvaliacao + ", Aluno=" + codAluno + ", Matéria=" + codMateria + ", Nota=" + valorNotaStr);

        Aluno aluno = buscarAlunoPorCodigo(codAluno);
        if (aluno == null) {
            throw new ErroCadastro("Aluno com código " + codAluno + " não encontrado.");
        }

        Materia materia = buscarMateriaPorCodigo(codMateria);
        if (materia == null) {
            throw new ErroCadastro("Matéria com código " + codMateria + " não encontrada.");
        }

        //Validar tipo de avaliação (opcional, mas dahora)
        List<String> tiposPadrao = Arrays.asList("P1", "P2", "Trabalho");
        boolean tipoPadrao = false;
        for(String tipo : tiposPadrao){
            if(tipo.equalsIgnoreCase(tipoAvaliacao)){
                tipoPadrao = true;
                break;
            }
        }
        if (!tipoPadrao) {
             logger.logInfo("Tipo de avaliação não padrão fornecido: " + tipoAvaliacao);
        }

        double valorNota;
        try {
            valorNota = Double.parseDouble(valorNotaStr);
            Utilitarios.validaNota(valorNota, 0, 10); //Valida se a nota está entre 0 e 10
        } catch (NumberFormatException e) {
            throw new ErroCadastro("Valor da nota inválido: " + valorNotaStr + ". Deve ser um número.");
        } catch (ErroCadastro e) {
             throw new ErroCadastro("Valor da nota inválido: " + valorNotaStr + ". " + e.getMessage());
        }

        //Verifica se já existe nota para este aluno/matéria/tipo
        Nota notaExistente = null;
        if(materia.getListaNotas() != null){
            for(Nota n : materia.getListaNotas()){
                if(n.getAluno() != null && n.getAluno().getCodigoUnico().equals(codAluno) && n.getTipoAvaliacao().equalsIgnoreCase(tipoAvaliacao)){
                    notaExistente = n;
                    break;
                }
            }
        }

        if (notaExistente != null) {
            logger.logInfo("Nota existente encontrada para " + codAluno + "/" + codMateria + "/" + tipoAvaliacao + ". Atualizando valor de " + notaExistente.getValorNota() + " para " + valorNota);
            notaExistente.setValorNota(valorNota);
        } else {
            Nota novaNota = new Nota(aluno, materia, tipoAvaliacao, valorNota);
            novaNota.setCodigoAluno(codAluno); //Garante que o código está salvo
            novaNota.setCodigoMateria(codMateria);
            notas.add(novaNota);
            materia.adicionarNota(novaNota); //Associa nota à matéria
            logger.logInfo("Nova nota adicionada.");
        }

        salvarTodosDados(); //Salva após adicionar/editar nota
        System.out.println("Nota " + tipoAvaliacao + " para aluno " + codAluno + " na matéria " + codMateria + " definida como " + valorNota + ".");
    }

    private static void fecharMediaAluno(String codigoAluno) throws ErroCadastro {
        logger.logInfo("Calculando média para o aluno: " + codigoAluno);
        Aluno aluno = buscarAlunoPorCodigo(codigoAluno);
        if (aluno == null) {
            throw new ErroCadastro("Aluno com código " + codigoAluno + " não encontrado.");
        }

        System.out.println("--- Médias Finais para Aluno: " + aluno.getNome() + " (" + codigoAluno + ") ---");

        if (aluno.getCurso() == null) {
             System.out.println("Aluno não está matriculado em nenhum curso.");
             logger.logErro("Aluno " + codigoAluno + " sem curso para cálculo de média.");
             return;
        }

        boolean encontrouMateriasComNota = false;
        //Itera sobre as matérias do curso do aluno
        if(aluno.getCurso().getListaMaterias() == null || aluno.getCurso().getListaMaterias().isEmpty()){
             System.out.println("O curso " + aluno.getCurso().getNome() + " não possui matérias cadastradas.");
             logger.logInfo("Curso " + aluno.getCurso().getCodigoUnico() + " sem matérias para cálculo de média do aluno " + codigoAluno);
             return;
        }

        for (Materia materia : aluno.getCurso().getListaMaterias()) {
            List<Nota> notasAlunoMateria = new ArrayList<>();
            //Busca notas manualmente na lista global ou na lista da matéria
            if(materia.getListaNotas() != null){
                for(Nota n : materia.getListaNotas()){
                    if(n.getAluno() != null && n.getAluno().getCodigoUnico().equals(codigoAluno)){
                        notasAlunoMateria.add(n);
                    }
                }
            }

            //Só processa se houver notas para esta matéria
            if (!notasAlunoMateria.isEmpty()) {
                encontrouMateriasComNota = true;
                double p1 = 0, p2 = 0, trabalho = 0;
                int countP1 = 0, countP2 = 0, countTrab = 0;

                for (Nota n : notasAlunoMateria) {
                    //Considera case-insensitive para os tipos padrão
                    if ("P1".equalsIgnoreCase(n.getTipoAvaliacao())) { p1 += n.getValorNota(); countP1++; }
                    else if ("P2".equalsIgnoreCase(n.getTipoAvaliacao())) { p2 += n.getValorNota(); countP2++; }
                    else if ("Trabalho".equalsIgnoreCase(n.getTipoAvaliacao())) { trabalho += n.getValorNota(); countTrab++; }
                    //Ignora outros tipos de nota para o cálculo da média padrão
                }

                //Calcula médias parciais (se houver mais de uma nota do mesmo tipo, faz a média)
                double mediaP1 = (countP1 > 0) ? p1 / countP1 : 0;
                double mediaP2 = (countP2 > 0) ? p2 / countP2 : 0;
                double mediaTrab = (countTrab > 0) ? trabalho / countTrab : 0;

                //Fórmula: (P1 + P2) * 0.8 + (T * 0.2)
                //Assumindo que se não tem P1 ou P2, a nota é 0 na média.
                double mediaFinal = (mediaP1 + mediaP2) * 0.8 + (mediaTrab * 0.2);

                String status = (mediaFinal >= 5.0) ? "APROVADO" : "Em Recuperação";

                System.out.printf("Matéria: %s (%s) | P1: %.1f | P2: %.1f | Trab: %.1f | Média Final: %.2f | Status: %s\n",
                                  materia.getNome(), materia.getCodigoUnico(), mediaP1, mediaP2, mediaTrab, mediaFinal, status);
                logger.logInfo(String.format("Média calculada para %s em %s: %.2f (%s)", codigoAluno, materia.getCodigoUnico(), mediaFinal, status));
            }
        }

        if (!encontrouMateriasComNota) {
            System.out.println("Nenhuma nota encontrada para as matérias do curso deste aluno.");
            logger.logInfo("Nenhuma nota encontrada para cálculo de média do aluno " + codigoAluno);
        }
    }

    //--- Métodos Auxiliares de Interação ---

    private static String lerEntradaObrigatoria(String prompt) throws ErroCadastro {
        System.out.print(prompt + ": ");
        String entrada = scanner.nextLine().trim();
        Utilitarios.validaCampoObrigatorio(prompt, entrada);
        return entrada;
    }

     private static String lerEntradaOpcional(String prompt, String valorAtual) {
        System.out.print(prompt + " [Atual: " + (valorAtual != null ? valorAtual : "") + "]: ");
        return scanner.nextLine().trim();
    }

    private static void listarCursosDisponiveis() {
         System.out.println("Cursos disponíveis:");
         if (cursos.isEmpty()) {
             System.out.println("  Nenhum curso cadastrado.");
         } else {
             //ordena por nome
             Collections.sort(cursos, new Comparator<Curso>() {
                 @Override
                 public int compare(Curso c1, Curso c2) {
                     return c1.getNome().compareToIgnoreCase(c2.getNome());
                 }
             });
             for (Curso c : cursos) {
                 System.out.println("  - " + c.getCodigoUnico() + ": " + c.getNome());
             }
         }
    }

     private static void listarMateriasDisponiveis() {
         System.out.println("Matérias disponíveis (Código: Nome (Curso)):");
         if (materias.isEmpty()) {
             System.out.println("  Nenhuma matéria cadastrada.");
         } else {
             //ordena por nome
             Collections.sort(materias, new Comparator<Materia>() {
                 @Override
                 public int compare(Materia m1, Materia m2) {
                     return m1.getNome().compareToIgnoreCase(m2.getNome());
                 }
             });
             for (Materia m : materias) {
                 String nomeCurso = "Sem Curso";
                 if(m.getCurso() != null){
                     nomeCurso = m.getCurso().getCodigoUnico();
                 } else if (m.getCodigoCurso() != null){
                     //tenta buscar o curso pelo codigo 
                     Curso cursoDaMateria = buscarCursoPorCodigo(m.getCodigoCurso());
                     if(cursoDaMateria != null) nomeCurso = cursoDaMateria.getCodigoUnico();
                     else nomeCurso = m.getCodigoCurso() + " (Inválido)";
                 }
                 System.out.println("  - " + m.getCodigoUnico() + ": " + m.getNome() + " (" + nomeCurso + ")");
             }
         }
    }

}

