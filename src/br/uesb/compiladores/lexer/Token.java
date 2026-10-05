package br.uesb.compiladores.lexer;

/** Representa o resultado de uma chamada ao analisador léxico. */
public record Token(TokenType type, String lexeme, int line, int column, String message) {
    public Token(TokenType type, String lexeme, int line, int column) {
        this(type, lexeme, line, column, "");
    }

    public boolean isError() {
        return type == TokenType.ERRO_LEXICO;
    }

    public String outputLine() {
        // A saída persistida mantém apenas o par solicitado no enunciado.
        return lexeme + "\t" + type.descricao();
    }
}
