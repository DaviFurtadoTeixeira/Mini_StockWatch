package application;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class LoginController {

    @FXML
    private Button btnEntrar;

    @FXML
    private Hyperlink linkCadastro;

    @FXML
    private Hyperlink linkRecuperarSenha;

    @FXML
    private PasswordField txtSenha;

    @FXML
    private TextField txtUsuario;

    @FXML
    void abrirCadastro(ActionEvent event) {
    	try {
    		Parent root = FXMLLoader.load(getClass().getResource("/application/telacad.fxml"));
    		Scene scene = new Scene(root,1920,1080);
    		Stage janela = (Stage) ((Node)
    				event.getSource()).getScene().getWindow();
    		janela.setScene(scene);
    		janela.setTitle("Tela login");
    		janela.setMaximized(true);
    		janela.centerOnScreen();
    		janela.show();
    	}catch(Exception e) {
    		e.getMessage();
    }
    }
    

    @FXML
    void entrar(ActionEvent event) {

    }

    @FXML
    void recuperarSenha(ActionEvent event) {

    }

}
