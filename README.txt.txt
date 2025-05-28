==== SISTEMA DE GERENCIAMENTO DE FACULDADE ====

PASSO A PASSO PARA EXECUÇÃO:

1- Abrir o cmd e colocar um cd no diretório principal (faculdade_project)

2- Compilar com "javac -d bin -cp "lib\commons-cli-1.9.0.jar" src\br\fsa\util\*.java src\br\fsa\pessoas\*.java src\br\fsa\faculdade\*.java src\br\fsa\dao\*.java src\br\fsa\*.java"

3- Executar o comando conforme necessidade:
   java -cp "lib\commons-cli-1.9.0.jar;bin" br.fsa.GerenciaFaculdade [comando]

4- É possível usar --h ou -help para ver todos os comandos disponíveis:
   java -cp "lib\commons-cli-1.9.0.jar;bin" br.fsa.GerenciaFaculdade -help



OBSERVAÇÕES:

- Matérias devem ser criadas e associadas a um curso antes de registrar notas
- O sistema usa arquivos CSV para armazenar os dados (alunos.csv, cursos.csv, materias.csv, notas.csv)
- A média é calculada usando a fórmula: (P1 + P2) * 0.8 + (Trabalho * 0.2) / Apesar de ser uma formula errada, é a mesma das instruções
- Status de aprovação: média >= 5.0 → "APROVADO", caso contrário → "Em Recuperação"
  java -cp "lib/commons-cli-1.9.0.jar:bin" br.fsa.GerenciaFaculdade [comando]
