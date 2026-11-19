
package application;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

public class tela_controle_prateleiras {

    @FXML private AnchorPane anchor01;

    @FXML private ImageView background_escurecido;
    @FXML private ImageView background_prateleiras;

    @FXML private Button button_setor01;
    @FXML private Button button_setor02;
    @FXML private Button button_setor03;
    @FXML private Button button_setor04;
    @FXML private Button button_setor05;
    @FXML private Button button_setor06;
    @FXML private Button button_setor07;
    @FXML private Button button_setor08;
    @FXML private Button button_setor09;
    @FXML private Button button_setor10;
    @FXML private Button button_setor11;
    @FXML private Button button_setor12;

    @FXML private TableColumn<?, ?> coluna_nome_produto;
    @FXML private TableColumn<?, ?> coluna_quantidade;

    @FXML private Rectangle fundo_icon01;
    @FXML private Rectangle fundo_icon02;
    @FXML private Rectangle fundo_icon03;
    @FXML private Rectangle fundo_icon04;
    @FXML private Rectangle fundo_icon05;
    @FXML private Rectangle fundo_icon06;
    @FXML private Rectangle fundo_icon07;
    @FXML private Rectangle fundo_icon08;
    @FXML private Rectangle fundo_icon09;
    @FXML private Rectangle fundo_icon10;
    @FXML private Rectangle fundo_icon11;
    @FXML private Rectangle fundo_icon12;

    @FXML private HBox hbox_tabelas;

    @FXML private ImageView icon_arroz_feijao;
    @FXML private ImageView icon_bebidas;
    @FXML private ImageView icon_biscoitos_bolachas;
    @FXML private ImageView icon_cafe_acucar;
    @FXML private ImageView icon_chocolate_cereal;
    @FXML private ImageView icon_doces_gulouseimas;
    @FXML private ImageView icon_higiene;
    @FXML private ImageView icon_limpeza;
    @FXML private ImageView icon_macarrao_massa;
    @FXML private ImageView icon_oleo_enlatado;
    @FXML private ImageView icon_tempero;
    @FXML private ImageView icon_utencilios;

    @FXML private ImageView imagem_setor;

    @FXML private Label label_codigo_setor;
    @FXML private Label label_informacoes;
    @FXML private Label label_nome_setor;
    @FXML private Label label_produto;

    @FXML private Label label_setor01;
    @FXML private Label label_setor02;
    @FXML private Label label_setor03;
    @FXML private Label label_setor04;
    @FXML private Label label_setor05;
    @FXML private Label label_setor06;
    @FXML private Label label_setor07;
    @FXML private Label label_setor08;
    @FXML private Label label_setor09;
    @FXML private Label label_setor10;
    @FXML private Label label_setor11;
    @FXML private Label label_setor12;

    @FXML private TableView<?> tabela_produtos;

    @FXML private VBox vbox_informacoes;
    @FXML private VBox vbox_principal;
    @FXML private VBox vbox_produtos;
    
    @FXML
    private Button button_voltar_tela_produto;


    // =====================================================
    // INICIALIZAÇÃO DA TELA
    // =====================================================

    @FXML
    private void initialize() {

        selecionarSetor(
            "S001",
            "Arroz e Feijão",
            icon_arroz_feijao
        );

    }


    // =====================================================
    // MÉTODO RESPONSÁVEL POR ATUALIZAR O PAINEL DO SETOR
    // =====================================================

    private void selecionarSetor(
            String codigo,
            String nome,
            ImageView icone) {

        label_codigo_setor.setText(codigo);

        label_nome_setor.setText(nome);

        if (icone != null && icone.getImage() != null) {
            imagem_setor.setImage(icone.getImage());
        }

    }


    // =====================================================
    // EVENTOS DOS BOTÕES DOS SETORES
    // =====================================================

    @FXML
    private void AcessarS001(ActionEvent event) {

        selecionarSetor(
            "S001",
            "Arroz e Feijão",
            icon_arroz_feijao
        );

    }

    @FXML
    private void AcessarS002(ActionEvent event) {

        selecionarSetor(
            "S002",
            "Massas e Macarrão",
            icon_macarrao_massa
        );

    }

    @FXML
    private void AcessarS003(ActionEvent event) {

        selecionarSetor(
            "S003",
            "Vinagres e Enlatados",
            icon_oleo_enlatado
        );

    }

    @FXML
    private void AcessarS004(ActionEvent event) {

        selecionarSetor(
            "S004",
            "Temperos e Especiarias",
            icon_tempero
        );

    }

    @FXML
    private void AcessarS005(ActionEvent event) {

        selecionarSetor(
            "S005",
            "Guloseimas e Doces",
            icon_doces_gulouseimas
        );

    }

    @FXML
    private void AcessarS006(ActionEvent event) {

        selecionarSetor(
            "S006",
            "Chocolates e Cereais",
            icon_chocolate_cereal
        );

    }

    @FXML
    private void AcessarS007(ActionEvent event) {

        selecionarSetor(
            "S007",
            "Bebidas",
            icon_bebidas
        );

    }

    @FXML
    private void AcessarS008(ActionEvent event) {

        selecionarSetor(
            "S008",
            "Biscoitos e Bolachas",
            icon_biscoitos_bolachas
        );

    }

    @FXML
    private void AcessarS009(ActionEvent event) {

        selecionarSetor(
            "S009",
            "Café e Açúcar",
            icon_cafe_acucar
        );

    }

    @FXML
    private void AcessarS010(ActionEvent event) {

        selecionarSetor(
            "S010",
            "Utensílios",
            icon_utencilios
        );

    }

    @FXML
    private void AcessarS011(ActionEvent event) {

        selecionarSetor(
            "S011",
            "Limpeza",
            icon_limpeza
        );

    }

    @FXML
    private void AcessarS012(ActionEvent event) {

        selecionarSetor(
            "S012",
            "Higiene",
            icon_higiene
        );

    }

    @FXML
    void Voltar_tela(ActionEvent event) {
    	try {
            Parent root = FXMLLoader.load(getClass().getResource("/application/tela_controle_visualizacao.fxml"));
            Scene scene = new Scene(root, 1920, 1080);
            Stage janela = (Stage) ((Node) event.getSource()).getScene().getWindow();
            janela.setScene(scene);
            janela.setTitle("Tela login");
            janela.setMaximized(true);
            janela.centerOnScreen();
            janela.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
