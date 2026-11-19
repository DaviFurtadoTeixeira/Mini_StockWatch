package application;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
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
                && senha.length() >= 8
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

        if (email.isEmpty() || usuario.isEmpty() || senha.isEmpty() || confirmarsenha.isEmpty()) {
            AlertPersonalizado.mostrar(Alert.AlertType.WARNING, "Erro ao cadastrar", "Nenhum campo pode ser deixado vazio!");
            return;
        }
        if (usuario.length() < 4 || usuario.matches("[0-9]+") || !usuario.matches("[a-zA-Z0-9_]+")) {
            AlertPersonalizado.mostrar(Alert.AlertType.ERROR, "Usuário inválido", "Use no mínimo 4 caracteres, com letras, números ou _ e não apenas números.");
            return;
        }
        if (!email.matches("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$")) {
            AlertPersonalizado.mostrar(Alert.AlertType.ERROR, "E-mail inválido!", "Por favor, insira um e-mail válido.");
            return;
        }
        if (!validarSenhaSegura(senha)) {
            AlertPersonalizado.mostrar(Alert.AlertType.ERROR, "Senha inválida!", "A senha deve conter letras, números, pelo menos 1 letra maiúscula e caracteres especiais.");
            return;
        }
        if (!senha.equals(confirmarsenha)) {
            AlertPersonalizado.mostrar(Alert.AlertType.WARNING, "Erro nas senhas", "As senhas digitadas devem ser iguais.");
            return;
        }


        Async.executar(
            () -> VerificacaoEmailService.enviarCodigo(email),

            enviado -> etapaConfirmarCodigo(email, usuario, senha, enviado),

            erro -> AlertPersonalizado.mostrar(
                Alert.AlertType.ERROR,
                "Erro na verificação!",
                "Não foi possível enviar o código de confirmação. "
                    + "Verifique sua conexão e tente novamente."
            ),

            btnCadastrar
        );
    }

    // ---- ETAPA 2 (thread da interface): usuário digita o código ----
    private void etapaConfirmarCodigo(
            String email,
            String usuario,
            String senha,
            VerificacaoEmailService.CodigoEnviado enviado) {

        boolean confirmado = VerificacaoEmailService.confirmarNaTela(email, enviado);

        if (!confirmado) {
            AlertPersonalizado.mostrar(
                Alert.AlertType.WARNING,
                "Cadastro cancelado!",
                "O e-mail não foi confirmado."
            );
            return;
        }

        // ---- ETAPA 3 (segundo plano): BCrypt + INSERT no banco ----
        Async.executar(
            () -> new BancoDeDados().inserirUsuario(usuario, email, senha),

            sucesso -> {
                if (sucesso) {
                    AlertPersonalizado.mostrar(Alert.AlertType.INFORMATION, "Cadastro realizado!", "Cadastro realizado com sucesso!");
                    txtEmail.clear();
                    txtUsuario.clear();
                    txtSenha.clear();
                    txtConfirmarSenha.clear();
                } else {
                    AlertPersonalizado.mostrar(
                        Alert.AlertType.ERROR,
                        "Erro no cadastro",
                        "Não foi possível concluir o cadastro. O usuário ou e-mail "
                            + "pode já existir, ou o banco de dados está indisponível."
                    );
                }
            },

            erro -> AlertPersonalizado.mostrar(
                Alert.AlertType.ERROR,
                "Erro no cadastro",
                "Erro ao conectar com o banco de dados!"
            ),

            btnCadastrar
        );
    }

    @FXML
    void voltarLogin(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/application/telalogin.fxml"));
            Scene scene = new Scene(root, 1920, 1080);
            Stage janela = (Stage) ((Node) event.getSource()).getScene().getWindow();
            janela.setScene(scene);
            janela.setTitle("Tela cadastro");
            janela.setMaximized(true);
            janela.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}