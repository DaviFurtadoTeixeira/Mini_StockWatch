 package application;

 import java.sql.Connection;
 import java.sql.PreparedStatement;
 import java.sql.ResultSet;
 import java.sql.SQLException;
 import java.util.Optional;
 import org.mindrot.jbcrypt.BCrypt;
 import javafx.event.ActionEvent;
 import javafx.fxml.FXML;
 import javafx.scene.control.Alert;
 import javafx.scene.control.TextInputDialog;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import application.AlertPersonalizado;

public class LoginController {
	
	@FXML
	private AnchorPane anchor01;
	
	@FXML
    private ImageView background_usuario;
	
	@FXML
    private HBox hbox01;

    @FXML
    private HBox hbox02;

    @FXML
    private HBox hbox_principal;
	
	@FXML
    private Label label07;

    @FXML
    private Label label08;

    @FXML
    private Label label09;

    @FXML
    private Label label10;

    @FXML
    private Label label_01;

    @FXML
    private Label label_02;

    @FXML
    private Label label_03;

    @FXML
    private Label label_04;

    @FXML
    private Label label_05;

    @FXML
    private Label label_06;
    
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
    private Region region01;

    @FXML
    private Region region02;
    
    @FXML
    private VBox vbox01;

    @FXML
    private VBox vbox02;

    @FXML
    private VBox vbox03;

    @FXML
    private VBox vbox04;

    @FXML
    private VBox vbox05;
    
    @FXML
    private ImageView logo_stockwatch;

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


        Optional<String> resultadoEmail =
            TextInputPersonalizado.mostrar(
                "Recuperação de senha",
                "Digite seu e-mail cadastrado:"
            );

        if (resultadoEmail.isEmpty()) {
            return;
        }

        String email = resultadoEmail.get().trim();

        if (!email.matches(
            "^[\\w.+-]+@[\\w.-]+\\.[A-Za-z]{2,}$"
        )) {

            AlertPersonalizado.mostrar(
                Alert.AlertType.ERROR,
                "E-mail inválido!",
                "Digite um e-mail válido."
            );
            return;
        }

        try {
            RecuperacaoSenhaService.solicitarCodigo(email);

        } catch (Exception e) {

            e.printStackTrace();

            AlertPersonalizado.mostrar(
                Alert.AlertType.ERROR,
                "Erro na solicitação!",
                "Não foi possível processar a recuperação. " +
                "Tente novamente mais tarde."
            );
            return;
        }

        AlertPersonalizado.mostrar(
            Alert.AlertType.INFORMATION,
            "Solicitação recebida!",
            "Se este e-mail estiver cadastrado, um código " +
            "de recuperação será enviado. Verifique também " +
            "a caixa de spam."
        );


        Optional<String> resultadoCodigo =
            TextInputPersonalizado.mostrar(
                "Verificar código",
                "Digite o código de 8 dígitos recebido:"
            );

        if (resultadoCodigo.isEmpty()) {
            return;
        }

        String codigo = resultadoCodigo.get().trim();

        if (!codigo.matches("\\d{8}")) {

            AlertPersonalizado.mostrar(
                Alert.AlertType.ERROR,
                "Código inválido!",
                "O código deve conter 8 números."
            );
            return;
        }

        Optional<String> resultadoSenha =
            SenhaPersonalizada.mostrar();

        if (resultadoSenha.isEmpty()) {
            return;
        }

    
        try {

            boolean alterada =
                RecuperacaoSenhaService.redefinirSenha(
                    email,
                    codigo,
                    resultadoSenha.get()
                );

            if (alterada) {

                AlertPersonalizado.mostrar(
                    Alert.AlertType.INFORMATION,
                    "Senha alterada!",
                    "Sua senha foi redefinida com sucesso. " +
                    "Você já pode entrar no StockWatch."
                );

            } else {

                AlertPersonalizado.mostrar(
                    Alert.AlertType.ERROR,
                    "Recuperação não concluída!",
                    "Código incorreto, expirado ou com " +
                    "limite de tentativas atingido. " +
                    "Verifique o código ou solicite outro."
                );
            }

        } catch (SQLException e) {

            e.printStackTrace();

            AlertPersonalizado.mostrar(
                Alert.AlertType.ERROR,
                "Erro ao alterar senha!",
                "Não foi possível atualizar sua senha."
            );
        }
    }
}
