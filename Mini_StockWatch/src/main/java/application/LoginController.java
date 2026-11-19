package application;

import java.util.Optional;

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

    // =====================================================
    // LOGIN
    // =====================================================

    @FXML
    void entrar(ActionEvent event) {
        String usuario = txtUsuario.getText().trim();
        String senha = txtSenha.getText();

        if (usuario.isBlank() || senha.isBlank()) {
            AlertPersonalizado.mostrar(Alert.AlertType.WARNING, "Campo Vazio!", "Preencha o e-mail e a senha.");
            return;
        }

        // Captura a janela agora, ainda na thread da interface
        Stage janela = (Stage) ((Node) event.getSource()).getScene().getWindow();

        // Conexão com o banco + verificação BCrypt em segundo plano
        Async.executar(
            () -> new BancoDeDados().autenticarUsuario(usuario, senha),

            autenticado -> {
                if (!autenticado) {
                    AlertPersonalizado.mostrar(Alert.AlertType.ERROR, "ERRO", "E-mail ou senha incorretos.");
                    return;
                }

                AlertPersonalizado.mostrar(Alert.AlertType.INFORMATION, "Sucesso", "Login realizado com sucesso!");

                try {
                    Parent root = FXMLLoader.load(getClass().getResource("/application/tela_cadastro_produto.fxml"));
                    Scene scene = new Scene(root, 1920, 1080);
                    janela.setScene(scene);
                    janela.setTitle("Tela login");
                    janela.setMaximized(true);
                    janela.centerOnScreen();
                    janela.show();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            },

            erro -> AlertPersonalizado.mostrar(
                Alert.AlertType.ERROR,
                "Erro ao entrar",
                "Não foi possível realizar o login. Tente novamente."
            ),

            btnEntrar
        );
    }

    // =====================================================
    // ESQUECI A SENHA
    // =====================================================

    @FXML
    private void recuperarSenha(ActionEvent event) {

        Optional<String> resultadoEmail = TextInputPersonalizado.mostrar(
            "Recuperação de senha",
            "Digite seu e-mail cadastrado:"
        );

        if (resultadoEmail.isEmpty()) {
            return;
        }

        String email = resultadoEmail.get().trim();

        if (!email.matches("^[\\w.+-]+@[\\w.-]+\\.[A-Za-z]{2,}$")) {
            AlertPersonalizado.mostrar(Alert.AlertType.ERROR, "E-mail inválido!", "Digite um e-mail válido.");
            return;
        }

        // Banco + envio de e-mail (SMTP) em segundo plano
        Async.executar(
            () -> {
                RecuperacaoSenhaService.solicitarCodigo(email);
                return Boolean.TRUE;
            },

            ok -> {
                AlertPersonalizado.mostrar(
                    Alert.AlertType.INFORMATION,
                    "Solicitação recebida!",
                    "Se este e-mail estiver cadastrado, um código "
                        + "de recuperação será enviado. Verifique também "
                        + "a caixa de spam."
                );
                etapaCodigo(email);
            },

            erro -> AlertPersonalizado.mostrar(
                Alert.AlertType.ERROR,
                "Erro na solicitação!",
                "Não foi possível processar a recuperação. "
                    + "Tente novamente mais tarde."
            ),

            linkRecuperarSenha,
            btnEntrar
        );
    }

    // Etapa 2 (thread da interface): usuário digita o código recebido
    private void etapaCodigo(String email) {

        Optional<String> resultadoCodigo = TextInputPersonalizado.mostrar(
            "Verificar código",
            "Digite o código de 8 dígitos recebido:"
        );

        if (resultadoCodigo.isEmpty()) {
            return;
        }

        String codigo = resultadoCodigo.get().trim();

        if (!codigo.matches("\\d{8}")) {
            AlertPersonalizado.mostrar(Alert.AlertType.ERROR, "Código inválido!", "O código deve conter 8 números.");
            return;
        }

        Optional<String> resultadoSenha = SenhaPersonalizada.mostrar();

        if (resultadoSenha.isEmpty()) {
            return;
        }

        etapaRedefinir(email, codigo, resultadoSenha.get());
    }

    // Etapa 3 (segundo plano): banco + BCrypt para gravar a nova senha
    private void etapaRedefinir(String email, String codigo, String novaSenha) {

        Async.executar(
            () -> RecuperacaoSenhaService.redefinirSenha(email, codigo, novaSenha),

            alterada -> {
                if (alterada) {
                    AlertPersonalizado.mostrar(
                        Alert.AlertType.INFORMATION,
                        "Senha alterada!",
                        "Sua senha foi redefinida com sucesso. "
                            + "Você já pode entrar no StockWatch."
                    );
                } else {
                    AlertPersonalizado.mostrar(
                        Alert.AlertType.ERROR,
                        "Recuperação não concluída!",
                        "Código incorreto, expirado ou com "
                            + "limite de tentativas atingido. "
                            + "Verifique o código ou solicite outro."
                    );
                }
            },

            erro -> AlertPersonalizado.mostrar(
                Alert.AlertType.ERROR,
                "Erro ao alterar senha!",
                "Não foi possível atualizar sua senha."
            ),

            linkRecuperarSenha,
            btnEntrar
        );
    }
}