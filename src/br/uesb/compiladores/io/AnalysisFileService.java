package br.uesb.compiladores.io;

import br.uesb.compiladores.lexer.Token;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

public final class AnalysisFileService {
    private AnalysisFileService() {
    }

    /** Lê integralmente um arquivo-fonte codificado em UTF-8. */
    public static String read(Path path) throws IOException {
        return Files.readString(path, StandardCharsets.UTF_8);
    }

    /** Grava um par lexema-token por linha, conforme o formato da atividade. */
    public static void writeTokens(Path path, List<Token> tokens) throws IOException {
        String output = tokens.stream()
                .map(Token::outputLine)
                .collect(Collectors.joining(System.lineSeparator()));
        Files.writeString(path, output + System.lineSeparator(), StandardCharsets.UTF_8);
    }
}
