package application;

import java.net.URL;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.ResourceBundle;
import java.util.UUID;
import java.util.function.UnaryOperator;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;

public class tela_controle_cadastro_produto implements Initializable {

	@FXML
    private AnchorPane anchor_produto;

    @FXML
    private ImageView background_tela_produto;

    @FXML
    private Button button_cadastrar;

    @FXML
    private TextField campo_fornecedor;

    @FXML
    private TextField campo_nome;

    @FXML
    private TextField campo_quantidade;

    @FXML
    private TextField campo_validade;

    @FXML
    private ComboBox<String> combobox_tipo_produto;

    @FXML
    private Label label_fornecedor;

    @FXML
    private Label label_nome;

    @FXML
    private Label label_quantidade;

    @FXML
    private Label label_tipo;

    @FXML
    private Label label_titulo;

    @FXML
    private Label label_validade;

    @FXML
    private ImageView logo_stockwatch;

    @FXML
    private ImageView logo_stockwatch1;

    // Flag para evitar loop infinito ao atualizar o texto programaticamente no listener
    private boolean formatandoData = false;
    
    // Lista base de tipos para o autocompletar
    private final ObservableList<String> listaTipos = FXCollections.observableArrayList(
        "Biscoitos", "Bolachas", "Massas e Macarrão", "Arroz e Feijão",
        "Farinhas e Fubá", "Açúcar e Adoçantes", "Café e Chás",
        "Óleos e Vinagres", "Molhos e Condimentos", "Conservas e Enlatados",
        "Temperos e Especiarias", "Cereais e Granola", "Salgadinhos e Snacks",
        "Doces e Guloseimas", "Chocolates e Bombons", "Bebidas Não Alcoólicas",
        "Produtos de Limpeza", "Higiene Pessoal", "Utensílios Domésticos",
        "Papelaria e Descartáveis"
    );

    private void exibirAlerta(String titulo, String mensagem, Alert.AlertType tipo) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }

    private void limparCampos() {
        if (campo_nome != null) {
            campo_nome.clear();
            campo_nome.setStyle("");
        }
        if (campo_fornecedor != null) {
            campo_fornecedor.clear();
            campo_fornecedor.setStyle("");
        }
        if (combobox_tipo_produto != null) {
            combobox_tipo_produto.setValue(null);
            combobox_tipo_produto.getItems().setAll(listaTipos);
            aplicarEstiloCombo("");
        }
        if (campo_quantidade != null) {
            campo_quantidade.clear();
            campo_quantidade.setStyle("");
        }
        if (campo_validade != null) {
            campo_validade.clear();
            campo_validade.setStyle("");
        }
    }

    // Método centralizado para aplicar o estilo unificado (fundo, bordas arredondadas e setinha transparente)
    private void aplicarEstiloCombo(String corBorda) {
        String estiloBase = "-fx-background-radius: 15px; -fx-border-radius: 15px; -fx-background-color: white;";
        if (!corBorda.isEmpty()) {
            combobox_tipo_produto.setStyle(estiloBase + " -fx-border-color: " + corBorda + "; -fx-border-width: 2px;");
        } else {
            combobox_tipo_produto.setStyle(estiloBase);
        }
        
        if (combobox_tipo_produto.getEditor() != null) {
            combobox_tipo_produto.getEditor().setStyle("-fx-background-color: transparent;");
        }
        
        // Força a setinha a acompanhar o arredondamento perfeito sem sobras quadradas
        SetNodeStyleInline(combobox_tipo_produto, ".arrow-button", "-fx-background-color: transparent; -fx-background-radius: 0 15px 15px 0;");
    }

    private void SetNodeStyleInline(ComboBox<?> combo, String selector, String style) {
        javafx.scene.Node node = combo.lookup(selector);
        if (node != null) {
            node.setStyle(style);
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        aplicarFiltrosEspeciais();
        configurarValidadoresVisuais();
        
        // Retira o foco automático do campo nome ao iniciar a tela
        Platform.runLater(() -> {
            if (anchor_produto != null) {
                anchor_produto.requestFocus();
            }
        });
        
        if (combobox_tipo_produto != null) {
            combobox_tipo_produto.setEditable(true);
            combobox_tipo_produto.getItems().addAll(listaTipos);

            // Aplica o estilo limpo imediatamente ao iniciar a tela
            Platform.runLater(() -> aplicarEstiloCombo(""));

            // Lógica de Autocompletar (Filtra enquanto digita)
            combobox_tipo_produto.getEditor().textProperty().addListener((obs, oldValue, newValue) -> {
                Platform.runLater(() -> {
                    String corAtual = (combobox_tipo_produto.getValue() != null && !combobox_tipo_produto.getValue().trim().isEmpty()) ? "#2ecc71" : (combobox_tipo_produto.isFocused() ? "red" : "");
                    aplicarEstiloCombo(corAtual);
                    
                    if (newValue == null || newValue.isEmpty()) {
                        combobox_tipo_produto.getItems().setAll(listaTipos);
                        return;
                    }
                    
                    String filtro = newValue.toLowerCase();
                    ObservableList<String> filtrados = FXCollections.observableArrayList();
                    for (String tipo : listaTipos) {
                        if (tipo.toLowerCase().contains(filtro)) {
                            filtrados.add(tipo);
                        }
                    }
                    
                    combobox_tipo_produto.getItems().setAll(filtrados);
                    if (!combobox_tipo_produto.isShowing() && !filtrados.isEmpty()) {
                        combobox_tipo_produto.show();
                    }
                });
            });
        }
    }

    private void aplicarFiltrosEspeciais() {
        // 1. FILTRO DO NOME: Apenas letras, espaços, hífens e limite máximo de 255 caracteres
        UnaryOperator<TextFormatter.Change> filtroNome = change -> {
            String texto = change.getControlNewText();
            if (texto.length() <= 255 && texto.matches("[\\p{L}\\s\\-]*")) {
                return change;
            }
            return null;
        };
        campo_nome.setTextFormatter(new TextFormatter<>(filtroNome));

        // 2. FILTRO DO FORNECEDOR: Limite máximo de 255 caracteres
        UnaryOperator<TextFormatter.Change> filtroFornecedor = change -> {
            String texto = change.getControlNewText();
            if (texto.length() <= 255) {
                return change;
            }
            return null;
        };
        campo_fornecedor.setTextFormatter(new TextFormatter<>(filtroFornecedor));

        // 3. FILTRO DA QUANTIDADE: Apenas números inteiros
        UnaryOperator<TextFormatter.Change> filtroQuantidade = change -> {
            String texto = change.getControlNewText();
            if (texto.matches("\\d*")) {
                return change;
            }
            return null;
        };
        campo_quantidade.setTextFormatter(new TextFormatter<>(filtroQuantidade));
    }

    private void configurarValidadoresVisuais() {
        // --- 1. CAMPO NOME ---
        campo_nome.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                if (campo_nome.getText().trim().isEmpty()) {
                    campo_nome.setStyle("-fx-border-color: red; -fx-border-width: 2px; -fx-border-radius: 4px;");
                }
            } else {
                if (campo_nome.getText().trim().isEmpty()) {
                    campo_nome.setStyle("");
                }
            }
        });
        campo_nome.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.trim().isEmpty()) {
                campo_nome.setStyle("-fx-border-color: #2ecc71; -fx-border-width: 2px; -fx-border-radius: 4px;");
            } else if (campo_nome.isFocused()) {
                campo_nome.setStyle("-fx-border-color: red; -fx-border-width: 2px; -fx-border-radius: 4px;");
            } else {
                campo_nome.setStyle("");
            }
        });

        // --- 2. CAMPO FORNECEDOR ---
        campo_fornecedor.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                if (campo_fornecedor.getText().trim().isEmpty()) {
                    campo_fornecedor.setStyle("-fx-border-color: red; -fx-border-width: 2px; -fx-border-radius: 4px;");
                }
            } else {
                if (campo_fornecedor.getText().trim().isEmpty()) {
                    campo_fornecedor.setStyle("");
                }
            }
        });
        campo_fornecedor.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.trim().isEmpty()) {
                campo_fornecedor.setStyle("-fx-border-color: #2ecc71; -fx-border-width: 2px; -fx-border-radius: 4px;");
            } else if (campo_fornecedor.isFocused()) {
                campo_fornecedor.setStyle("-fx-border-color: red; -fx-border-width: 2px; -fx-border-radius: 4px;");
            } else {
                campo_fornecedor.setStyle("");
            }
        });

        // --- 3. CAMPO QUANTIDADE ---
        campo_quantidade.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                if (campo_quantidade.getText().trim().isEmpty() || !campo_quantidade.getText().matches("\\d+")) {
                    campo_quantidade.setStyle("-fx-border-color: red; -fx-border-width: 2px; -fx-border-radius: 4px;");
                }
            } else {
                if (campo_quantidade.getText().trim().isEmpty()) {
                    campo_quantidade.setStyle("");
                }
            }
        });
        campo_quantidade.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.trim().isEmpty() && newValue.matches("\\d+")) {
                campo_quantidade.setStyle("-fx-border-color: #2ecc71; -fx-border-width: 2px; -fx-border-radius: 4px;");
            } else if (campo_quantidade.isFocused()) {
                campo_quantidade.setStyle("-fx-border-color: red; -fx-border-width: 2px; -fx-border-radius: 4px;");
            } else {
                campo_quantidade.setStyle("");
            }
        });

        // --- 4. CAMPO VALIDADE ---
        campo_validade.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                if (campo_validade.getText() == null || campo_validade.getText().trim().length() < 10) {
                    campo_validade.setStyle("-fx-border-color: red; -fx-border-width: 2px; -fx-border-radius: 4px;");
                }
            } else {
                if (campo_validade.getText() == null || campo_validade.getText().trim().isEmpty()) {
                    campo_validade.setStyle("");
                }
            }
        });
        
        campo_validade.textProperty().addListener((observable, oldValue, newValue) -> {
            if (formatandoData) return;
            if (newValue == null || newValue.isEmpty()) {
                if (campo_validade.isFocused()) {
                    campo_validade.setStyle("-fx-border-color: red; -fx-border-width: 2px; -fx-border-radius: 4px;");
                } else {
                    campo_validade.setStyle("");
                }
                return;
            }

            // Remove tudo o que não for número
            String numeros = newValue.replaceAll("[^0-9]", "");
            if (numeros.length() > 8) {
                numeros = numeros.substring(0, 8);
            }

            // Reconstrói a formatação DD/MM/AAAA mantendo a naturalidade
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < numeros.length(); i++) {
                if (i == 2 || i == 4) {
                    sb.append('/');
                }
                sb.append(numeros.charAt(i));
            }

            String formatado = sb.toString();
            
            if (!formatado.equals(newValue)) {
                formatandoData = true;
                int posicaoCursor = campo_validade.getCaretPosition();
                campo_validade.setText(formatado);
                campo_validade.positionCaret(Math.min(posicaoCursor, formatado.length()));
                formatandoData = false;
            }

            // Validação visual da borda
            if (formatado.length() == 10) {
                try {
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/uuuu").withResolverStyle(ResolverStyle.STRICT);
                    LocalDate localDate = LocalDate.parse(formatado, formatter);
                    if (localDate.isBefore(LocalDate.now())) {
                        campo_validade.setStyle("-fx-border-color: red; -fx-border-width: 2px; -fx-border-radius: 4px;");
                    } else {
                        campo_validade.setStyle("-fx-border-color: #2ecc71; -fx-border-width: 2px; -fx-border-radius: 4px;");
                    }
                } catch (Exception e) {
                    campo_validade.setStyle("-fx-border-color: red; -fx-border-width: 2px; -fx-border-radius: 4px;");
                }
            } else if (campo_validade.isFocused()) {
                campo_validade.setStyle("-fx-border-color: red; -fx-border-width: 2px; -fx-border-radius: 4px;");
            }
        });

        // --- 5. COMBOBOX TIPO DE PRODUTO ---
        combobox_tipo_produto.focusedProperty().addListener((observable, oldValue, newValue) -> {
            Platform.runLater(() -> {
                if (newValue) {
                    if (combobox_tipo_produto.getValue() == null || combobox_tipo_produto.getValue().trim().isEmpty()) {
                        aplicarEstiloCombo("red");
                    }
                } else {
                    if (combobox_tipo_produto.getValue() == null || combobox_tipo_produto.getValue().trim().isEmpty()) {
                        aplicarEstiloCombo("");
                    }
                }
            });
        });

        combobox_tipo_produto.valueProperty().addListener((observable, oldValue, newValue) -> {
            Platform.runLater(() -> {
                if (newValue != null && !newValue.trim().isEmpty()) {
                    aplicarEstiloCombo("#2ecc71");
                } else if (combobox_tipo_produto.isFocused()) {
                    aplicarEstiloCombo("red");
                } else {
                    aplicarEstiloCombo("");
                }
            });
        });
    }

    @FXML
    void CadastrarProduto(ActionEvent event) {
        if (campo_nome.getText().trim().isEmpty() || campo_quantidade.getText().trim().isEmpty()) {
            exibirAlerta("Atenção", "Preencha corretamente os campos obrigatórios (Nome e Quantidade)!", Alert.AlertType.WARNING);
            return;
        }
        
        try {
            String idAleatorio = UUID.randomUUID().toString();
            String nome = campo_nome.getText().trim();
            String fornecedor = campo_fornecedor.getText() != null ? campo_fornecedor.getText().trim() : "";
            String tipo = (combobox_tipo_produto.getValue() != null) ? combobox_tipo_produto.getValue() : "";
            int quantidade = Integer.parseInt(campo_quantidade.getText().trim());
            Date validade = null;
            String textoValidade = campo_validade.getText().trim();
            
            if (!textoValidade.isEmpty()) {
                if (textoValidade.length() < 10) {
                    exibirAlerta("Data Incompleta", "Por favor, preencha a data de validade inteira no formato DD/MM/AAAA.", Alert.AlertType.ERROR);
                    return;
                }

                try {
                    DateTimeFormatter formatter = DateTimeFormatter
                            .ofPattern("dd/MM/uuuu")
                            .withResolverStyle(ResolverStyle.STRICT);
                    
                    LocalDate localDate = LocalDate.parse(textoValidade, formatter);
                    
                    if (localDate.isBefore(LocalDate.now())) {
                        exibirAlerta("Data Inválida", "A data de validade não pode ser anterior ao dia de hoje!", Alert.AlertType.ERROR);
                        return;
                    }
                    
                    validade = Date.valueOf(localDate);
                } catch (DateTimeParseException e) {
                    exibirAlerta("Data Inválida", "A data informada não existe no calendário (verifique dias e meses).", Alert.AlertType.ERROR);
                    return;
                }
            }
            
            String url = "jdbc:mysql://10.0.30.179:3306/stockwatch";
            String user = "stockwatch_user";
            String password = "senha123";
            
            String sql = "INSERT INTO produtos (id, nome, fornecedor, tipo, quantidade, validade) VALUES (?, ?, ?, ?, ?, ?)";
            
            try (Connection conn = DriverManager.getConnection(url, user, password);
                 PreparedStatement stmt = conn.prepareStatement(sql)) {
                
                stmt.setString(1, idAleatorio);
                stmt.setString(2, nome);
                stmt.setString(3, fornecedor);
                stmt.setString(4, tipo);
                stmt.setInt(5, quantidade);
                stmt.setDate(6, validade);
                
                stmt.executeUpdate();
                exibirAlerta("Sucesso", "Produto cadastrado com sucesso!", Alert.AlertType.INFORMATION);
                limparCampos();
            } catch (SQLException e) {
                e.printStackTrace();
                exibirAlerta("Erro de Banco de Dados", "Não foi possível salvar no banco: " + e.getMessage(), Alert.AlertType.ERROR);
            }
            
        } catch (NumberFormatException e) {
            exibirAlerta("Erro de Formato", "O campo Quantidade deve conter apenas números inteiros.", Alert.AlertType.ERROR);
        }
    }
}