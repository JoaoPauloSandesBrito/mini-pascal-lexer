package br.uesb.compiladores.lexer;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

/**
 * Analisador léxico determinístico da linguagem Mini_Pascal.
 * Cada chamada a nextToken() retorna um par formado pelo lexema lido e seu token.
 */
public final class Lexer {
    private static final Set<String> RESERVED_WORDS = new HashSet<>(Set.of(
            "absolute", "array", "begin", "case", "char", "const", "div", "do",
            "dowto", "else", "end", "external", "file", "for", "forward", "func",
            "function", "goto", "if", "implementation", "integer", "interface",
            "interrupt", "label", "main", "nil", "nit", "of", "packed", "proc",
            "program", "real", "record", "repeat", "set", "shl", "shr", "string",
            "then", "to", "type", "unit", "until", "uses", "var", "while", "with", "xor"
    ));
    private static final Set<String> LOGICAL_OPERATORS = Set.of("and", "or", "not");

    private final String source;
    private int index;
    private int line = 1;
    private int column = 1;

    public Lexer(String source) {
        this.source = source == null ? "" : source;
    }

    public Token nextToken() {
        // Espaços, quebras de linha e comentários não pertencem à saída.
        Token skippedError = skipIgnoredCharacters();
        if (skippedError != null) {
            return skippedError;
        }
        if (isAtEnd()) {
            return new Token(TokenType.EOF, "", line, column);
        }

        int start = index;
        int startLine = line;
        int startColumn = column;
        char current = peek();

        if (Character.isLetter(current) || current == '_') {
            // Estado inicial de identificadores e palavras reconhecidas por tabela.
            advance();
            while (Character.isLetterOrDigit(peek()) || peek() == '_') {
                advance();
            }
            String lexeme = source.substring(start, index);
            String normalized = lexeme.toLowerCase(Locale.ROOT);
            if (LOGICAL_OPERATORS.contains(normalized)) {
                return new Token(TokenType.OPERADOR_LOGICO, lexeme, startLine, startColumn);
            }
            if (normalized.equals("mod")) {
                return new Token(TokenType.OPERADOR_ARITMETICO, lexeme, startLine, startColumn);
            }
            if (RESERVED_WORDS.contains(normalized)) {
                return new Token(TokenType.PALAVRA_RESERVADA, lexeme, startLine, startColumn);
            }
            return new Token(TokenType.IDENTIFICADOR, lexeme, startLine, startColumn);
        }

        if (Character.isDigit(current)) {
            // A rotina de números decide entre inteiro e real sem perder o ponto final.
            return number(start, startLine, startColumn);
        }

        if (current == '"' || current == '\'') {
            // O delimitador define se a constante é string ou caractere.
            return quotedConstant(current, start, startLine, startColumn);
        }

        // Operadores compostos seguem a regra do maior lexema válido.
        advance();
        return switch (current) {
            case '+', '-', '*' -> new Token(TokenType.OPERADOR_ARITMETICO,
                    source.substring(start, index), startLine, startColumn);
            case '/' -> new Token(TokenType.OPERADOR_ARITMETICO, "/", startLine, startColumn);
            case '=' -> new Token(TokenType.OPERADOR_RELACIONAL, "=", startLine, startColumn);
            case '>' -> match('=')
                    ? new Token(TokenType.OPERADOR_RELACIONAL, ">=", startLine, startColumn)
                    : new Token(TokenType.OPERADOR_RELACIONAL, ">", startLine, startColumn);
            case '<' -> match('=')
                    ? new Token(TokenType.OPERADOR_RELACIONAL, "<=", startLine, startColumn)
                    : match('>')
                    ? new Token(TokenType.OPERADOR_RELACIONAL, "<>", startLine, startColumn)
                    : new Token(TokenType.OPERADOR_RELACIONAL, "<", startLine, startColumn);
            case ':' -> match('=')
                    ? new Token(TokenType.ATRIBUICAO, ":=", startLine, startColumn)
                    : new Token(TokenType.SIMBOLO_ESPECIAL, ":", startLine, startColumn);
            case '(', ')', ',', ';' -> new Token(TokenType.SIMBOLO_ESPECIAL,
                    source.substring(start, index), startLine, startColumn);
            case '.' -> new Token(TokenType.FIM, ".", startLine, startColumn);
            default -> error(source.substring(start, index), startLine, startColumn,
                    "Símbolo desconhecido");
        };
    }

    public List<Token> scanAll() {
        List<Token> tokens = new ArrayList<>();
        Token token;
        do {
            token = nextToken();
            if (token.type() != TokenType.EOF) {
                tokens.add(token);
            }
        } while (token.type() != TokenType.EOF);
        return tokens;
    }

    private Token number(int start, int startLine, int startColumn) {
        // Parte inteira obrigatória.
        while (Character.isDigit(peek())) {
            advance();
        }
        boolean real = false;

        if (peek() == '.' && Character.isDigit(peekNext())) {
            // O ponto integra o número somente quando é seguido por dígito.
            real = true;
            advance();
            while (Character.isDigit(peek())) {
                advance();
            }
        }

        if (peek() == 'e' || peek() == 'E') {
            // Expoente: e ou E, sinal opcional e ao menos um dígito.
            real = true;
            advance();
            if (peek() == '+' || peek() == '-') {
                advance();
            }
            if (!Character.isDigit(peek())) {
                while (Character.isLetterOrDigit(peek()) || peek() == '_') {
                    advance();
                }
                return error(source.substring(start, index), startLine, startColumn,
                        "Expoente inválido: era esperado ao menos um dígito");
            }
            while (Character.isDigit(peek())) {
                advance();
            }
        }

        if (Character.isLetter(peek()) || peek() == '_') {
            // Consome toda a unidade inválida para não produzir tokens enganosos.
            while (Character.isLetterOrDigit(peek()) || peek() == '_') {
                advance();
            }
            return error(source.substring(start, index), startLine, startColumn,
                    "Número seguido diretamente por identificador");
        }

        return new Token(real ? TokenType.NUMERO_REAL : TokenType.NUMERO_INTEIRO,
                source.substring(start, index), startLine, startColumn);
    }

    private Token quotedConstant(char delimiter, int start, int startLine, int startColumn) {
        advance();
        boolean escaped = false;
        while (!isAtEnd()) {
            char c = peek();
            if (c == '\n' || c == '\r') {
                // A linguagem não permite que uma constante atravesse a linha.
                return error(source.substring(start, index), startLine, startColumn,
                        delimiter == '"' ? "String não terminada" : "Caractere não terminado");
            }
            advance();
            if (c == delimiter && !escaped) {
                String lexeme = source.substring(start, index);
                if (delimiter == '\'' && decodedLength(lexeme.substring(1, lexeme.length() - 1)) != 1) {
                    return error(lexeme, startLine, startColumn,
                            "Constante caractere deve conter exatamente um caractere");
                }
                return new Token(delimiter == '"' ? TokenType.CONSTANTE_STRING : TokenType.CONSTANTE_CHAR,
                        lexeme, startLine, startColumn);
            }
            escaped = c == '\\' && !escaped;
            if (c != '\\') {
                escaped = false;
            }
        }
        return error(source.substring(start, index), startLine, startColumn,
                delimiter == '"' ? "String não terminada" : "Caractere não terminado");
    }

    private int decodedLength(String value) {
        int count = 0;
        boolean escaped = false;
        for (int i = 0; i < value.length(); i++) {
            if (!escaped && value.charAt(i) == '\\') {
                escaped = true;
            } else {
                count++;
                escaped = false;
            }
        }
        return count;
    }

    /** Ignora espaços, quebras de linha e comentários de bloco. */
    private Token skipIgnoredCharacters() {
        while (true) {
            while (Character.isWhitespace(peek())) {
                advance();
            }
            if (peek() == '/' && peekNext() == '*') {
                int start = index;
                int startLine = line;
                int startColumn = column;
                advance();
                advance();
                while (!(peek() == '*' && peekNext() == '/') && !isAtEnd()) {
                    advance();
                }
                if (isAtEnd()) {
                    return error(source.substring(start, index), startLine, startColumn,
                            "Comentário de bloco não terminado");
                }
                advance();
                advance();
                continue;
            }
            return null;
        }
    }

    private Token error(String lexeme, int errorLine, int errorColumn, String message) {
        // O erro é devolvido como token; a próxima chamada continua a análise.
        return new Token(TokenType.ERRO_LEXICO, lexeme, errorLine, errorColumn, message);
    }

    private boolean match(char expected) {
        if (peek() != expected) {
            return false;
        }
        advance();
        return true;
    }

    private char advance() {
        if (isAtEnd()) {
            return '\0';
        }
        char c = source.charAt(index++);
        if (c == '\n') {
            line++;
            column = 1;
        } else {
            column++;
        }
        return c;
    }

    private char peek() {
        return isAtEnd() ? '\0' : source.charAt(index);
    }

    private char peekNext() {
        return index + 1 >= source.length() ? '\0' : source.charAt(index + 1);
    }

    private boolean isAtEnd() {
        return index >= source.length();
    }
}
