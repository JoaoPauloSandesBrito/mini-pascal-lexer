package br.uesb.compiladores.lexer;

public enum TokenType {
    PALAVRA_RESERVADA("Palavra reservada"),
    IDENTIFICADOR("Identificador"),
    NUMERO_INTEIRO("Número inteiro"),
    NUMERO_REAL("Número real"),
    CONSTANTE_CHAR("Constante caractere"),
    CONSTANTE_STRING("Constante string"),
    OPERADOR_ARITMETICO("Operador aritmético"),
    OPERADOR_RELACIONAL("Operador relacional"),
    OPERADOR_LOGICO("Operador lógico"),
    SIMBOLO_ESPECIAL("Símbolo especial"),
    ATRIBUICAO("Atribuição"),
    FIM("Fim"),
    ERRO_LEXICO("Erro léxico"),
    EOF("Fim de arquivo");

    private final String descricao;

    TokenType(String descricao) {
        this.descricao = descricao;
    }

    public String descricao() {
        return descricao;
    }
}

