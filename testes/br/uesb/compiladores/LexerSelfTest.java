package br.uesb.compiladores;

import br.uesb.compiladores.lexer.Lexer;
import br.uesb.compiladores.lexer.Token;
import br.uesb.compiladores.lexer.TokenType;

import java.util.List;

public final class LexerSelfTest {
    private static int checks;

    public static void main(String[] args) {
        recognizesBasicTokens();
        recognizesRealExponent();
        ignoresCommentsAndTracksLines();
        treatsKeywordsWithoutCase();
        reportsMalformedInputAndContinues();
        distinguishesFinalDotFromDecimalDot();
        System.out.println("Todos os testes passaram (" + checks + " verificações).");
    }

    private static void recognizesBasicTokens() {
        List<Token> tokens = new Lexer("x := 10 + 2;").scanAll();
        expect(tokens.size() == 6, "quantidade de tokens básicos");
        expect(tokens.get(0).type() == TokenType.IDENTIFICADOR, "identificador");
        expect(tokens.get(1).type() == TokenType.ATRIBUICAO, "atribuição");
        expect(tokens.get(2).type() == TokenType.NUMERO_INTEIRO, "inteiro");
        expect(tokens.get(3).type() == TokenType.OPERADOR_ARITMETICO, "operador");
        expect(tokens.get(5).type() == TokenType.SIMBOLO_ESPECIAL, "ponto e vírgula");
    }

    private static void recognizesRealExponent() {
        Token token = new Lexer("24.40e-04").nextToken();
        expect(token.type() == TokenType.NUMERO_REAL, "real com expoente");
        expect(token.lexeme().equals("24.40e-04"), "lexema do real");
    }

    private static void ignoresCommentsAndTracksLines() {
        List<Token> tokens = new Lexer("x /* ignorado\nem duas linhas */ y").scanAll();
        expect(tokens.size() == 2, "comentário não vira token");
        expect(tokens.get(1).line() == 2, "linha depois do comentário");
    }

    private static void treatsKeywordsWithoutCase() {
        List<Token> tokens = new Lexer("Program BEGIN end AND mod").scanAll();
        expect(tokens.get(0).type() == TokenType.PALAVRA_RESERVADA, "Program reservado");
        expect(tokens.get(1).type() == TokenType.PALAVRA_RESERVADA, "BEGIN reservado");
        expect(tokens.get(3).type() == TokenType.OPERADOR_LOGICO, "AND lógico");
        expect(tokens.get(4).type() == TokenType.OPERADOR_ARITMETICO, "mod aritmético");
    }

    private static void reportsMalformedInputAndContinues() {
        List<Token> tokens = new Lexer("12e+ @ x").scanAll();
        expect(tokens.get(0).isError(), "expoente inválido");
        expect(tokens.get(1).isError(), "símbolo desconhecido");
        expect(tokens.get(2).type() == TokenType.IDENTIFICADOR, "recuperação após erros");
    }

    private static void distinguishesFinalDotFromDecimalDot() {
        List<Token> tokens = new Lexer("2.0 2.").scanAll();
        expect(tokens.get(0).type() == TokenType.NUMERO_REAL, "ponto decimal");
        expect(tokens.get(1).type() == TokenType.NUMERO_INTEIRO, "inteiro antes do fim");
        expect(tokens.get(2).type() == TokenType.FIM, "ponto final");
    }

    private static void expect(boolean condition, String description) {
        checks++;
        if (!condition) {
            throw new AssertionError("Falhou: " + description);
        }
    }
}

