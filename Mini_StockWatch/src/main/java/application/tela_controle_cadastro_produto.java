package application;

import java.net.URL;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
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

    private void exibirAlerta(String titulo, String mensagem, Alert.AlertType tipo) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }

    private void limparCampos() {
        if (campo_nome != null) campo_nome.clear();
        if (campo_fornecedor != null) campo_fornecedor.clear();
        if (combobox_tipo_produto != null) combobox_tipo_produto.setValue(null);
        if (campo_quantidade != null) campo_quantidade.clear();
        if (campo_validade != null) campo_validade.clear();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        aplicarMascaraData();
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

    private void aplicarMascaraData() {
        UnaryOperator<TextFormatter.Change> filtro = change -> {
            String novoTexto = change.getControlNewText().replaceAll("[^0-9]", "");
            if (novoTexto.length() > 8) {
                novoTexto = novoTexto.substring(0, 8);
            }
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < novoTexto.length(); i++) {
                if (i == 2 || i == 4) {
                    sb.append('/');
                }
                sb.append(novoTexto.charAt(i));
            }
            String formatado = sb.toString();
            change.setRange(0, change.getControlText().length());
            change.setText(formatado);
            change.selectRange(formatado.length(), formatado.length());
            return change;
        };
        campo_validade.setTextFormatter(new TextFormatter<>(filtro));
    }

    @FXML
    void CadastrarProduto(ActionEvent event) {
        if (campo_nome.getText().trim().isEmpty() || campo_quantidade.getText().trim().isEmpty()) {
            exibirAlerta("Atenção", "Preencha pelo menos o Nome e a Quantidade do produto!", Alert.AlertType.WARNING);
            return;
        }
        try {
            String idAleatorio = UUID.randomUUID().toString();
            String nome = campo_nome.getText();
            String fornecedor = campo_fornecedor.getText();
            String tipo = (combobox_tipo_produto.getValue() != null) ? combobox_tipo_produto.getValue() : "";
            int quantidade = Integer.parseInt(campo_quantidade.getText().trim());
            Date validade = null;
            String textoValidade = campo_validade.getText().trim();
            if (!textoValidade.isEmpty()) {
                try {
                    java.time.format.DateTimeFormatter formatter = java.time.format.DateTimeFormatter
                            .ofPattern("dd/MM/uuuu")
                            .withResolverStyle(java.time.format.ResolverStyle.STRICT);
                    java.time.LocalDate localDate = java.time.LocalDate.parse(textoValidade, formatter);
                    
                    if (localDate.isBefore(java.time.LocalDate.now())) {
                        exibirAlerta("Data Inválida", "A data de validade não pode ser anterior ao dia de hoje!", Alert.AlertType.ERROR);
                        return;
                    }
                    
                    validade = Date.valueOf(localDate);
                } catch (java.time.format.DateTimeParseException e) {
                    exibirAlerta("Erro de Formato", "A validade deve ser uma data válida no formato DD/MM/AAAA (ex: 15/08/2026).", Alert.AlertType.ERROR);
                    return;
                }
            }
            
            // IP atualizado para o endereço correto da rede do seu colega
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