package br.uesb.compiladores.gui;

import br.uesb.compiladores.io.AnalysisFileService;
import br.uesb.compiladores.lexer.Lexer;
import br.uesb.compiladores.lexer.Token;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.AbstractTableModel;
import javax.swing.plaf.basic.BasicButtonUI;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public final class MainFrame extends JFrame {
    private final JTextArea sourceArea = new JTextArea();
    private final TokenTableModel tableModel = new TokenTableModel();
    private final JLabel status = new JLabel("Abra um arquivo .txt ou digite o código-fonte.");
    private Path currentFile;

    public MainFrame() {
        super("Compilador Mini_Pascal - Analisador Léxico");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(960, 640));
        setLocationByPlatform(true);
        createUi();
    }

    private void createUi() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(23, 54, 93));
        header.setBorder(BorderFactory.createEmptyBorder(14, 18, 14, 18));
        JLabel title = new JLabel("Mini_Pascal | Analisador Léxico");
        title.setForeground(Color.WHITE);
        title.setFont(title.getFont().deriveFont(Font.BOLD, 20f));
        header.add(title, BorderLayout.WEST);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 7));
        JButton open = new JButton("Abrir arquivo");
        JButton analyze = new JButton("Analisar");
        JButton save = new JButton("Salvar tokens");
        JButton clear = new JButton("Limpar");
        analyze.setUI(new BasicButtonUI());
        analyze.setBackground(new Color(32, 117, 74));
        analyze.setForeground(Color.WHITE);
        analyze.setOpaque(true);
        analyze.setContentAreaFilled(true);
        analyze.setBorderPainted(false);
        analyze.setFocusPainted(false);
        analyze.setFont(analyze.getFont().deriveFont(Font.BOLD));
        analyze.setBorder(BorderFactory.createEmptyBorder(7, 16, 7, 16));
        actions.add(open);
        actions.add(analyze);
        actions.add(save);
        actions.add(clear);

        sourceArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 14));
        sourceArea.setTabSize(4);
        sourceArea.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        JTable table = new JTable(tableModel);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setRowHeight(24);
        table.getColumnModel().getColumn(0).setPreferredWidth(55);
        table.getColumnModel().getColumn(1).setPreferredWidth(55);
        table.getColumnModel().getColumn(2).setPreferredWidth(210);
        table.getColumnModel().getColumn(3).setPreferredWidth(190);
        table.getColumnModel().getColumn(4).setPreferredWidth(300);

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
                titled("Código-fonte", new JScrollPane(sourceArea)),
                titled("Resultado da análise", new JScrollPane(table)));
        split.setResizeWeight(0.43);
        split.setDividerLocation(410);

        status.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
        status.setHorizontalAlignment(SwingConstants.LEFT);

        add(header, BorderLayout.NORTH);
        JPanel center = new JPanel(new BorderLayout());
        center.add(actions, BorderLayout.NORTH);
        center.add(split, BorderLayout.CENTER);
        add(center, BorderLayout.CENTER);
        add(status, BorderLayout.SOUTH);

        open.addActionListener(event -> openFile());
        analyze.addActionListener(event -> analyze());
        save.addActionListener(event -> saveTokens());
        clear.addActionListener(event -> {
            sourceArea.setText("");
            tableModel.setTokens(List.of());
            currentFile = null;
            status.setText("Conteúdo removido.");
        });
    }

    private JPanel titled(String title, JScrollPane content) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder(title));
        panel.add(content, BorderLayout.CENTER);
        return panel;
    }

    private void openFile() {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileFilter(new FileNameExtensionFilter("Arquivos de texto (*.txt)", "txt"));
        if (chooser.showOpenDialog(this) != JFileChooser.APPROVE_OPTION) {
            return;
        }
        try {
            currentFile = chooser.getSelectedFile().toPath();
            sourceArea.setText(AnalysisFileService.read(currentFile));
            sourceArea.setCaretPosition(0);
            status.setText("Arquivo aberto: " + currentFile.getFileName());
        } catch (Exception exception) {
            showError("Não foi possível abrir o arquivo", exception);
        }
    }

    private void analyze() {
        List<Token> tokens = new Lexer(sourceArea.getText()).scanAll();
        tableModel.setTokens(tokens);
        long errors = tokens.stream().filter(Token::isError).count();
        status.setText(tokens.size() + " token(s) reconhecido(s); " + errors + " erro(s) léxico(s).");
    }

    private void saveTokens() {
        if (tableModel.tokens.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Execute a análise antes de salvar.",
                    "Nenhum resultado", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        JFileChooser chooser = new JFileChooser();
        chooser.setSelectedFile(new File(defaultOutputName()));
        chooser.setFileFilter(new FileNameExtensionFilter("Arquivos de texto (*.txt)", "txt"));
        if (chooser.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) {
            return;
        }
        try {
            Path destination = ensureTxt(chooser.getSelectedFile().toPath());
            AnalysisFileService.writeTokens(destination, tableModel.tokens);
            status.setText("Resultado salvo em: " + destination);
        } catch (Exception exception) {
            showError("Não foi possível salvar o resultado", exception);
        }
    }

    private String defaultOutputName() {
        if (currentFile == null) {
            return "saida_tokens.txt";
        }
        String name = currentFile.getFileName().toString().replaceFirst("(?i)\\.txt$", "");
        return name + "_tokens.txt";
    }

    private Path ensureTxt(Path path) {
        return path.toString().toLowerCase().endsWith(".txt")
                ? path
                : Path.of(path + ".txt");
    }

    private void showError(String title, Exception exception) {
        JOptionPane.showMessageDialog(this, title + ":\n" + exception.getMessage(),
                "Erro", JOptionPane.ERROR_MESSAGE);
    }

    private static final class TokenTableModel extends AbstractTableModel {
        private final String[] columns = {"Linha", "Coluna", "Lexema", "Token", "Observação"};
        private List<Token> tokens = new ArrayList<>();

        public void setTokens(List<Token> tokens) {
            this.tokens = new ArrayList<>(tokens);
            fireTableDataChanged();
        }

        @Override
        public int getRowCount() {
            return tokens.size();
        }

        @Override
        public int getColumnCount() {
            return columns.length;
        }

        @Override
        public String getColumnName(int column) {
            return columns[column];
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            Token token = tokens.get(rowIndex);
            return switch (columnIndex) {
                case 0 -> token.line();
                case 1 -> token.column();
                case 2 -> token.lexeme();
                case 3 -> token.type().descricao();
                case 4 -> token.message();
                default -> "";
            };
        }
    }
}
