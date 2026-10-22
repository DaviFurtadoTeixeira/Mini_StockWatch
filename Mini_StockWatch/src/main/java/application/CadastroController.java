package application;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
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
    private Hyperlink linkLogin;

    @FXML
    private PasswordField txtConfirmarSenha;

    @FXML
    private TextField txtEmail;

    @FXML
    private PasswordField txtSenha;

    @FXML
    private TextField txtUsuario;
    
    public static boolean validarSenhaSegura(String senha) {
    	return senha != null
    			&& senha.length() >=8
    			&& !senha.contains(" ")
                && senha.matches(".*[a-z].*")
                && senha.matches(".*[A-Z].*")
                && senha.matches(".*[0-9].*")
                && senha.matches(".*[.!@#$%&*()?/<>_=\\-].*");
    }
  
    @FXML
    void cadastrar(ActionEvent event) {
    	String email = txtEmail.getText().toLowerCase().strip();
    	String usuario = txtUsuario.getText().toLowerCase().strip();
    	String senha = txtSenha.getText().strip();
    	String confirmarsenha = txtConfirmarSenha.getText().strip();
    	if(email.isEmpty() || usuario.isEmpty()|| senha.isEmpty() || confirmarsenha.isEmpty()) {
    		exibirAlerta((Alert.AlertType.INFORMATION), "Erro","Campo vazio!","Por favor preencha todos os campos");
    		return;
    	}
    	if (!email.matches("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$")) {
    	    exibirAlerta(Alert.AlertType.ERROR, "Erro", "E-mail inválido!", "Por favor, insira um e-mail válido.");
    	    return;
    	}
    	if (!validarSenhaSegura(senha)) {
    		System.out.println(validarSenhaSegura(senha));
    	    exibirAlerta(Alert.AlertType.ERROR, "Erro", "Senha inválida!", "A senha deve conter letras, números, pelo menos 1 letra maiúscula e caracteres especiais.");
    	    return;
    	}
    	if (!senha.equals(confirmarsenha)) {
    	    exibirAlerta(Alert.AlertType.ERROR, "Erro", "Erro nas senhas", "As senhas digitadas devem ser iguais.");
    	    return;
    	}
    }

    @FXML
    void voltarLogin(ActionEvent event) {
      	try {
    		Parent root = FXMLLoader.load(getClass().getResource("/application/telalogin.fxml"));
    		Scene scene = new Scene(root,1920,1080);
    		Stage janela = (Stage) ((Node)
    				event.getSource()).getScene().getWindow();
    		janela.setScene(scene);
    		janela.setTitle("Tela cadastro");
    		janela.setMaximized(true);
    		janela.show();
    	}catch(Exception e) {
    		e.getMessage();
    }
    }
	private void exibirAlerta(Alert.AlertType tipo, String titulo, String cabecalho, String conteudo) {
	    Alert alerta = new Alert(tipo);
	    alerta.setTitle(titulo);
	    alerta.setHeaderText(cabecalho);
	    alerta.setContentText(conteudo);
	    alerta.showAndWait();
}
}
