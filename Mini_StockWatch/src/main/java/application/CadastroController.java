package application;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class CadastroController {

    @FXML
    private Button btnCadastrar;

    @FXML
    private CheckBox chkTermos;

    @FXML
    private Hyperlink linkLogin;

    @FXML
    private PasswordField txtConfirmarSenha;

    @FXML
    private TextField txtEmail;

    @FXML
    private TextField txtNome;

    @FXML
    private PasswordField txtSenha;

    @FXML
    private TextField txtUsuario;

    @FXML
    void cadastrar(ActionEvent event) {

    }

    @FXML
    void voltarLogin(ActionEvent event) {
      	try {
    		Parent root = FXMLLoader.load(getClass().getResource("/application/telalogin.fxml"));
    		Scene scene = new Scene(root);
    		Stage janela = (Stage) ((Node)
    				event.getSource()).getScene().getWindow();
    		janela.setScene(scene);
    		janela.setTitle("Tela cadastro");
    		janela.centerOnScreen();
    		janela.show();
    	}catch(Exception e) {
    		e.getMessage();
    }
    }

}
