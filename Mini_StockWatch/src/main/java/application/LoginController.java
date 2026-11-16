 package application;

 import java.sql.Connection;
 import java.sql.PreparedStatement;
 import java.sql.ResultSet;
 import java.sql.SQLException;
 import java.util.Optional;

 import javafx.event.ActionEvent;
 import javafx.fxml.FXML;
 import javafx.scene.control.Alert;
 import javafx.scene.control.TextInputDialog;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import application.AlertPersonalizado;

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
    	 String usuario = txtUsuario.getText().trim();
    	    String senha = txtSenha.getText();

    	    if (usuario.isBlank() || senha.isBlank()) {
    	        AlertPersonalizado.mostrar(AlertType.WARNING,"Campo Vazio!","Preencha o e-mail e a senha.");
    	        return;
    	    }

    	    BancoDeDados bd = new BancoDeDados();

    	    if (bd.autenticarUsuario(usuario, senha)) {
    	    	AlertPersonalizado.mostrar(AlertType.INFORMATION,"Suceesso","Login realizado com sucesso!");
    	        try {
    	    		Parent root = FXMLLoader.load(getClass().getResource("/application/tela_cadastro_produto.fxml"));
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
    	    } else {
    	    	AlertPersonalizado.mostrar(AlertType.ERROR,"ERRO","E-mail ou senha incorretos.");
    	    }
    	}
    	
    

    @FXML
    private void recuperarSenha(ActionEvent event) {
    	Optional<String> resultado = TextInputPersonalizado.mostrar(
    		    "Recuperação de senha",
    		    "Digite seu e-mail cadastrado:"
    		);

        if (resultado.isEmpty()) {
            return;
        }

        String email = resultado.get().trim();

        if (!email.matches("^[\\w.+-]+@[\\w.-]+\\.[A-Za-z]{2,}$")) {
            AlertPersonalizado.mostrar(
                Alert.AlertType.ERROR,
                "E-mail inválido!",
                "Digite um endereço de e-mail válido."
            );
            return;
        }

        BancoDeDados banco = new BancoDeDados();
        banco.conectar();

        if (banco.connection == null) {
            AlertPersonalizado.mostrar(
                Alert.AlertType.ERROR,
                "Erro de conexão!",
                "Não foi possível conectar ao banco de dados."
            );
            return;
        }

        String sql = "SELECT id FROM usuarios WHERE email = ?";

        try (Connection conexao = banco.connection;
             PreparedStatement stmt = conexao.prepareStatement(sql)) {

            stmt.setString(1, email);

            try (ResultSet rs = stmt.executeQuery()) {

                AlertPersonalizado.mostrar(
                    Alert.AlertType.INFORMATION,
                    "Solicitação recebida!",
                    "Se o e-mail estiver cadastrado, você receberá "
                    + "instruções para recuperar sua senha."
                );
            }

        } catch (SQLException e) {

            AlertPersonalizado.mostrar(
                Alert.AlertType.ERROR,
                "Erro ao recuperar senha!",
                "Não foi possível verificar o e-mail."
            );

            e.printStackTrace();
        }
    }

}
