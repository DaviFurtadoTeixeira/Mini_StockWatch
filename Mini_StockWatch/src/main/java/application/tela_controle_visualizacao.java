package application;

import java.io.IOException;
import java.net.URL;
import java.sql.Date;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.ResourceBundle;
import java.util.List;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class tela_controle_visualizacao implements Initializable {

    @FXML
    private AnchorPane anchor_visualizacao;

    @FXML
    private TableColumn<Produto, String> column_fornecedor;

    @FXML
    private TableColumn<Produto, String> column_id;

    @FXML
    private TableColumn<Produto, String> column_nome;

    @FXML
    private TableColumn<Produto, Integer> column_quantidade;

    @FXML
    private TableColumn<Produto, String> column_tipo;

    @FXML
    private TableColumn<Produto, Date> column_validade;

    @FXML
    private ImageView imagem_background;

    @FXML
    private Label label_titulo;

    @FXML
    private ImageView logo_stockwatch;

    @FXML
    private TableView<Produto> table_view;

    @FXML
    private ImageView tela_escurecida;

    @FXML
    private TextField txt_pesquisa;

    private final ObservableList<Produto> listaProdutos = FXCollections.observableArrayList();
    private FilteredList<Produto> filtroProdutos;

    private void exibirAlerta(String titulo, String mensagem, Alert.AlertType tipo) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Aplica o arredondamento na tabela inteira
        table_view.setStyle(
            "-fx-background-radius: 15px; " +
            "-fx-border-radius: 15px; " +
            "-fx-background-color: white; " +
            "-fx-padding: 0px;"
        );

        // Força os componentes internos do cabeçalho a acompanharem o arredondamento
        Platform.runLater(() -> {
            Node headerBackground = table_view.lookup(".column-header-background");
            if (headerBackground != null) {
                headerBackground.setStyle(
                    "-fx-background-color: #e8e8e8; " +
                    "-fx-background-radius: 15px 15px 0 0; " +
                    "-fx-border-radius: 15px 15px 0 0;"
                );
            }
            
            // Arredonda a primeira coluna (canto superior esquerdo)
            Node firstHeader = table_view.lookup(".column-header:first-child");
            if (firstHeader != null) {
                firstHeader.setStyle(
                    "-fx-background-color: transparent; " +
                    "-fx-background-radius: 15px 0 0 0;"
                );
            }
            
            // Arredonda a última coluna (canto superior direito)
            Node lastHeader = table_view.lookup(".column-header:last-child");
            if (lastHeader != null) {
                lastHeader.setStyle(
                    "-fx-background-color: transparent; " +
                    "-fx-background-radius: 0 15px 0 0;"
                );
            }
        });

        column_id.setCellValueFactory(new PropertyValueFactory<>("id"));
        column_nome.setCellValueFactory(new PropertyValueFactory<>("nome"));
        column_fornecedor.setCellValueFactory(new PropertyValueFactory<>("fornecedor"));
        column_tipo.setCellValueFactory(new PropertyValueFactory<>("tipo"));
        column_quantidade.setCellValueFactory(new PropertyValueFactory<>("quantidade"));
        column_validade.setCellValueFactory(new PropertyValueFactory<>("validade"));
        
        // Bloqueia a alteração de posição (arrastar) de todas as colunas
        column_id.setReorderable(false);
        column_nome.setReorderable(false);
        column_fornecedor.setReorderable(false);
        column_tipo.setReorderable(false);
        column_quantidade.setReorderable(false);
        column_validade.setReorderable(false);

        // Corrige a ordenação cronológica da coluna de Validade
        column_validade.setComparator(new Comparator<Date>() {
            @Override
            public int compare(Date d1, Date d2) {
                if (d1 == null && d2 == null) return 0;
                if (d1 == null) return 1; // Itens sem validade vão para o final
                if (d2 == null) return -1;
                return d1.compareTo(d2);
            }
        });

        // Formata a data para o padrão brasileiro (DD/MM/AAAA)
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        column_validade.setCellFactory(column -> new TableCell<Produto, Date>() {
            @Override
            protected void updateItem(Date date, boolean empty) {
                super.updateItem(date, empty);
                if (empty || date == null) {
                    setText(null);
                } else {
                    LocalDate localDate = date.toLocalDate();
                    setText(localDate.format(formatter));
                }
            }
        });

        carregarProdutosDoBanco();

        // Configuração do Filtro Dinâmico de Pesquisa em Tempo Real
        filtroProdutos = new FilteredList<>(listaProdutos, b -> true);

        if (txt_pesquisa != null) {
            txt_pesquisa.textProperty().addListener((observable, oldValue, newValue) -> {
                filtroProdutos.setPredicate(produto -> {
                    if (newValue == null || newValue.isEmpty()) {
                        return true;
                    }

                    String lowerCaseFilter = newValue.toLowerCase();

                    if (produto.getId() != null && produto.getId().toLowerCase().contains(lowerCaseFilter)) {
                        return true;
                    } else if (produto.getNome() != null && produto.getNome().toLowerCase().contains(lowerCaseFilter)) {
                        return true;
                    } else if (produto.getFornecedor() != null && produto.getFornecedor().toLowerCase().contains(lowerCaseFilter)) {
                        return true;
                    } else if (produto.getTipo() != null && produto.getTipo().toLowerCase().contains(lowerCaseFilter)) {
                        return true;
                    } else if (String.valueOf(produto.getQuantidade()).contains(lowerCaseFilter)) {
                        return true;
                    }
                    return false;
                });
            });
        }

        SortedList<Produto> produtosOrdenados = new SortedList<>(filtroProdutos);
        produtosOrdenados.comparatorProperty().bind(table_view.comparatorProperty());
        table_view.setItems(produtosOrdenados);
    }


private void carregarProdutosDoBanco() {

    listaProdutos.clear();

    BancoDeDados banco = new BancoDeDados();

    List<Produto> produtos = banco.listarProdutos();

    if (produtos == null) {
        exibirAlerta(
            "Erro ao carregar produtos",
            "Não foi possível carregar os produtos do banco de dados.",
            Alert.AlertType.ERROR
        );

        return;
    }

    listaProdutos.addAll(produtos);
}


    private void carregarDadosDeTeste() {
        Produto p1 = new Produto("1", "Arroz Integral", "Fornecedor Alfa", "Alimento", 50, Date.valueOf("2027-10-22"));
        Produto p2 = new Produto("2", "Detergente Neutro", "Fornecedor Beta", "Limpeza", 30, null);
        
        listaProdutos.addAll(p1, p2);
    }

    @FXML
    void CadastroDeProduto(ActionEvent event) {
        try {
            // Carrega de volta o FXML da tela de cadastro
            FXMLLoader loader = new FXMLLoader(getClass().getResource("tela_cadastro_produto.fxml")); // Ajuste o nome do fxml de cadastro se necessário
            Parent root = loader.load();
            
            Stage stage = (Stage) anchor_visualizacao.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("StockWatch - Cadastro de Produtos");
            stage.centerOnScreen();
            stage.show();
            
        } catch (IOException e) {
            e.printStackTrace();
            exibirAlerta("Erro", "Não foi possível retornar à tela de cadastro.", Alert.AlertType.ERROR);
        }
    }
}