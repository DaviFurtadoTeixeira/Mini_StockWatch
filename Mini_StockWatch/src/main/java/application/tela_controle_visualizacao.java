package application;

import java.sql.Date;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;

public class tela_controle_visualizacao {

	@FXML
    private AnchorPane anchor_visualizacao;

    @FXML
    private TableColumn<Produto, String> column_fornecedor;

    @FXML
    private TableColumn<Produto, Integer> column_id;

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
    public void initialize() {
        column_id.setCellValueFactory(new PropertyValueFactory<>("id"));
        column_nome.setCellValueFactory(new PropertyValueFactory<>("nome"));
        column_fornecedor.setCellValueFactory(new PropertyValueFactory<>("fornecedor"));
        column_tipo.setCellValueFactory(new PropertyValueFactory<>("tipo"));
        column_quantidade.setCellValueFactory(new PropertyValueFactory<>("quantidade"));
        column_validade.setCellValueFactory(new PropertyValueFactory<>("validade"));
        
        Produto p1 = new Produto(1, "Arroz Integral", "Fornecedor Alfa", "Alimento", 50, Date.valueOf("2027-10-22"));
        Produto p2 = new Produto(2, "Detergente Neutro", "Fornecedor Beta", "Limpeza", 30, null);
        
        ObservableList<Produto> listaDeTeste = FXCollections.observableArrayList(p1, p2);
        
        table_view.setItems(listaDeTeste);
    }
}