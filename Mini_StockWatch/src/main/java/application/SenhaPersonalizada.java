package application;

import java.util.Optional;

import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.layout.VBox;

public class SenhaPersonalizada {

    public static Optional<String> mostrar() {

        Dialog<String> dialog = new Dialog<>();

        dialog.setTitle("StockWatch");
        dialog.setHeaderText("Criar nova senha");

        PasswordField txtSenha = new PasswordField();
        txtSenha.setPromptText("Nova senha");

        PasswordField txtConfirmar = new PasswordField();
        txtConfirmar.setPromptText("Confirmar senha");

        Label lblMensagem = new Label(
        	    "A senha deve conter no mínimo 8 caracteres, " +
        	    "letras maiúsculas, minúsculas, números " +
        	    "e caracteres especiais."
        	);

        	lblMensagem.setWrapText(true);
        	lblMensagem.setMinHeight(
        	    javafx.scene.layout.Region.USE_PREF_SIZE
        	);
        	lblMensagem.setMaxWidth(Double.MAX_VALUE);
        	lblMensagem.setPrefWidth(390);

        Label lblErro = new Label();
        lblErro.setWrapText(true);
        lblErro.setStyle("-fx-text-fill: #ff5252;");

        VBox conteudo = new VBox(
        	    12,
        	    lblMensagem,
        	    txtSenha,
        	    txtConfirmar,
        	    lblErro
        	);

        	conteudo.setPadding(new Insets(10));
        	conteudo.setFillWidth(true);
        	txtSenha.setMaxWidth(Double.MAX_VALUE);
        	txtConfirmar.setMaxWidth(Double.MAX_VALUE);

        conteudo.setPadding(new Insets(10));

        DialogPane painel = dialog.getDialogPane();
        painel.setContent(conteudo);
        painel.getButtonTypes().addAll(
            ButtonType.OK, ButtonType.CANCEL
        );

        painel.setPrefWidth(480);
        painel.setMinWidth(480);

        painel.setPrefHeight(330);

        var css = SenhaPersonalizada.class
            .getResource("style.css");

        if (css != null) {
            painel.getStylesheets().add(css.toExternalForm());
        }

        painel.getStyleClass().add("stockwatch-dialog");

        Button btnOK =
            (Button) painel.lookupButton(ButtonType.OK);

        btnOK.setText("Alterar senha");

        Button btnCancelar =
            (Button) painel.lookupButton(ButtonType.CANCEL);

        btnCancelar.setText("Cancelar");

        btnOK.addEventFilter(
            javafx.event.ActionEvent.ACTION, event -> {

                String senha = txtSenha.getText();

                if (!senha.matches(
                    "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)" +
                    "(?=.*[^a-zA-Z0-9\\s])\\S{8,}$"
                )) {

                    lblErro.setText(
                        "A senha não atende aos requisitos."
                    );

                    event.consume();
                    return;
                }

                if (!senha.equals(txtConfirmar.getText())) {

                    lblErro.setText(
                        "As senhas não coincidem."
                    );

                    event.consume();
                }
            }
        );

        dialog.setResultConverter(botao -> {

            if (botao == ButtonType.OK) {
                return txtSenha.getText();
            }

            return null;
        });

        return dialog.showAndWait();
    }
}