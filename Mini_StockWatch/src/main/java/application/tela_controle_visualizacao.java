package application;

import java.sql.Date;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;

public class tela_controle_visualizacao {

    @FXML
    private AnchorPane anchor01;

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
    private ImageView imagem_escurecida;

    @FXML
    private Label label_titulo;

    @FXML
    private TableView<Produto> table_view;

}