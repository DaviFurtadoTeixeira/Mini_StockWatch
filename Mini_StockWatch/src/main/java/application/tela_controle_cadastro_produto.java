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
            combobox_tipo_produto.setStyle("");
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

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        aplicarFiltrosEspeciais();
        configurarValidadoresVisuais();
        
        if (combobox_tipo_produto != null) {
        	combobox_tipo_produto.getItems().addAll(
        		    "Biscoitos",
        		    "Bolachas",
        		    "Massas e Macarrão",
        		    "Arroz e Feijão",
        		    "Farinhas e Fubá",
        		    "Açúcar e Adoçantes",
        		    "Café e Chás",
        		    "Óleos e Vinagres",
        		    "Molhos e Condimentos",
        		    "Conservas e Enlatados",
        		    "Temperos e Especiarias",
        		    "Cereais e Granola",
        		    "Salgadinhos e Snacks",
        		    "Doces e Guloseimas",
        		    "Chocolates e Bombons",
        		    "Bebidas Não Alcoólicas",
        		    "Produtos de Limpeza",
        		    "Higiene Pessoal",
        		    "Utensílios Domésticos",
        		    "Papelaria e Descartáveis"
        		);
        }
    }

    private void aplicarFiltrosEspeciais() {
        // 1. FILTRO DO NOME: Apenas letras, espaços e hífens
        UnaryOperator<TextFormatter.Change> filtroNome = change -> {
            String texto = change.getControlNewText();
            if (texto.matches("[\\p{L}\\s\\-]*")) {
                return change;
            }
            return null;
        };
        campo_nome.setTextFormatter(new TextFormatter<>(filtroNome));

        // 2. FILTRO DA QUANTIDADE: Apenas números inteiros
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
        campo_nome.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.trim().isEmpty()) {
                campo_nome.setStyle("-fx-border-color: red; -fx-border-width: 2px; -fx-border-radius: 4px;");
            } else {
                campo_nome.setStyle("-fx-border-color: #2ecc71; -fx-border-width: 2px; -fx-border-radius: 4px;");
            }
        });

        campo_quantidade.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.trim().isEmpty() || !newValue.matches("\\d+")) {
                campo_quantidade.setStyle("-fx-border-color: red; -fx-border-width: 2px; -fx-border-radius: 4px;");
            } else {
                campo_quantidade.setStyle("-fx-border-color: #2ecc71; -fx-border-width: 2px; -fx-border-radius: 4px;");
            }
        });

        // 3. MÁSCARA E VALIDAÇÃO DA VALIDADE VIA LISTENER (Permite clicar e editar em qualquer lugar livremente)
        campo_validade.textProperty().addListener((observable, oldValue, newValue) -> {
            if (formatandoData) return;
            if (newValue == null || newValue.isEmpty()) {
                campo_validade.setStyle("");
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
                // Restaura a posição do cursor para que o usuário possa editar onde quiser
                campo_validade.positionCaret(Math.min(posicaoCursor, formatado.length()));
                formatandoData = false;
            }

            // Validação visual da borda (Vermelho se incompleto/passado, Verde se correto)
            if (formatado.length() < 10) {
                campo_validade.setStyle("-fx-border-color: red; -fx-border-width: 2px; -fx-border-radius: 4px;");
            } else {
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
            }
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