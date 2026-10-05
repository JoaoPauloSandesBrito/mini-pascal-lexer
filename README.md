# Mini_Pascal Lexer

Analisador léxico desenvolvido em Java 17 para a linguagem Mini_Pascal.

O programa lê um arquivo-fonte, reconhece os lexemas e apresenta a classe léxica, a linha e a coluna de cada token. A interface gráfica foi construída com Swing.

## Classes reconhecidas

- Palavras reservadas
- Identificadores
- Números inteiros e reais
- Constantes caractere e string
- Operadores aritméticos, relacionais e lógicos
- Símbolos especiais
- Atribuição e fim de programa
- Erros léxicos

## Requisitos

- Windows
- Java 17 ou superior

## Execução

Para abrir a interface gráfica:

```bat
executar.bat
```

Também é possível executar pelo terminal:

```bat
java -jar dist\MiniPascal-Lexico.jar entrada.txt saida.txt
```

## Compilação

```bat
build.bat
```

O JAR será gerado em `dist\MiniPascal-Lexico.jar`.

## Testes

```bat
testar.bat
```

O projeto contém seis arquivos de entrada e vinte verificações automatizadas.

## Estrutura

```text
src/                código-fonte Java
testes/             testes automatizados e arquivos de entrada
saidas-esperadas/   resultados usados na verificação
dist/               JAR executável
documentacao/       relatório em LaTeX e PDF
apresentacao/       apresentação em Beamer e PDF
```

## Autor

João Paulo Sandes Brito  
Ciência da Computação - UESB
