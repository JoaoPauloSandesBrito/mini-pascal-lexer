package br.uesb.compiladores;

import br.uesb.compiladores.gui.MainFrame;
import br.uesb.compiladores.io.AnalysisFileService;
import br.uesb.compiladores.lexer.Lexer;
import br.uesb.compiladores.lexer.Token;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import java.nio.file.Path;
import java.util.List;

public final class Main {
    private Main() {
    }

    public static void main(String[] args) {
        // Com argumentos, o programa funciona em modo texto. Sem argumentos,
        // abre a interface gráfica solicitada para uso em ambiente Windows.
        if (args.length > 0) {
            runCommandLine(args);
            return;
        }
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {
                // O visual padrão do Swing continua funcional.
            }
            new MainFrame().setVisible(true);
        });
    }

    private static void runCommandLine(String[] args) {
        try {
            Path input = Path.of(args[0]);
            Path output = args.length >= 2 ? Path.of(args[1]) : Path.of("saida_tokens.txt");
            String source = AnalysisFileService.read(input);
            List<Token> tokens = new Lexer(source).scanAll();
            AnalysisFileService.writeTokens(output, tokens);
            long errors = tokens.stream().filter(Token::isError).count();
            System.out.printf("Análise concluída: %d token(s), %d erro(s). Saída: %s%n",
                    tokens.size(), errors, output.toAbsolutePath());
            if (errors > 0) {
                System.exit(2);
            }
        } catch (Exception exception) {
            System.err.println("Falha ao analisar o arquivo: " + exception.getMessage());
            System.exit(1);
        }
    }
}
