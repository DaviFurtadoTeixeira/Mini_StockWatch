package application;

import java.net.URL;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;

public class tela_controle_cadastro_produto {

    @FXML
    private AnchorPane anchor_produto;

    @FXML
    private ImageView background_tela_produto;

    @FXML
    private Button button_cadastrar;

    @FXML
    private TextField campo_fornecedor;

    @FXML
    private TextField campo_id;

    @FXML
    private TextField campo_nome;

    @FXML
    private TextField campo_quantidade;

    @FXML
    private TextField campo_tipo;

    @FXML
    private TextField campo_validade;

    @FXML
    private Label label_fornecedor;

    @FXML
    private Label label_id;

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

    private void exibirAlerta(String titulo, String mensagem, Alert.AlertType tipo) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }

    private void limparCampos() {
        if (campo_id != null) campo_id.clear();
        if (campo_nome != null) campo_nome.clear();
        if (campo_fornecedor != null) campo_fornecedor.clear();
        if (campo_tipo != null) campo_tipo.clear();
        if (campo_quantidade != null) campo_quantidade.clear();
        if (campo_validade != null) campo_validade.clear();
    }
    
    public void initialize(URL location, ResourceBundle resources) {
        aplicarMascaraData();
    }
    
    private void aplicarMascaraData() {
        TextFormatter<String> mascaraFiltro = new TextFormatter<>(change -> {
            String novoTexto = change.getControlNewText();
            if (novoTexto.length() < change.getControlText().length()) {
                return change;
            }
            if (novoTexto.length() > 10) {
                return null;
            }
            if (!change.getText().matches("[0-9]*")) {
                return null;
            }
            if (novoTexto.length() == 2 || novoTexto.length() == 5) {
                change.setText(change.getText() + "/");
                change.setCaretPosition(novoTexto.length() + 1);
            }
            return change;
        });
        campo_validade.setTextFormatter(mascaraFiltro);
    }
    
    @FXML
    void CadastrarProduto(ActionEvent event) {
        if (campo_nome.getText().trim().isEmpty() || campo_quantidade.getText().trim().isEmpty()) {
            exibirAlerta("Atenção", "Preencha pelo menos o Nome e a Quantidade do produto!", Alert.AlertType.WARNING);
            return;
        }
        try {
            String nome = campo_nome.getText();
            String fornecedor = campo_fornecedor.getText();
            String tipo = campo_tipo.getText();
            int quantidade = Integer.parseInt(campo_quantidade.getText());
            Date validade = null;
            String textoValidade = campo_validade.getText().trim();
            if (!textoValidade.isEmpty()) {
                try {
                    java.time.format.DateTimeFormatter formatter = java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy");
                    java.time.LocalDate localDate = java.time.LocalDate.parse(textoValidade, formatter);
                    validade = Date.valueOf(localDate);
                } catch (java.time.format.DateTimeParseException e) {
                    exibirAlerta("Erro de Formato", "A validade deve estar completa no formato DD/MM/AAAA (ex: 15/08/2026).", Alert.AlertType.ERROR);
                    return;
                }
            }
            String url = "jdbc:mysql://localhost:3306/stockwatch";
            String user = "root";
            String password = "sua_senha";
            String sql = "INSERT INTO produtos (nome, fornecedor, tipo, quantidade, validade) VALUES (?, ?, ?, ?, ?)";
            try (Connection conn = DriverManager.getConnection(url, user, password);
                 PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, nome);
                stmt.setString(2, fornecedor);
                stmt.setString(3, tipo);
                stmt.setInt(4, quantidade);
                stmt.setDate(5, validade);
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
