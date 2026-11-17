
package application;

import java.util.Optional;

import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.TextField;

public class TextInputPersonalizado {

    public static Optional<String> mostrar(
            String titulo,
            String mensagem) {

        TextInputDialog dialog = new TextInputDialog();

        dialog.setTitle("StockWatch");
        dialog.setHeaderText(titulo);
        dialog.setContentText(null);
        dialog.setGraphic(null);

        DialogPane painel = dialog.getDialogPane();

        // Mensagem acima do campo
        Label lblMensagem = new Label(mensagem);
        lblMensagem.setWrapText(true);
        lblMensagem.setStyle(
            "-fx-text-fill: white;" +
            "-fx-font-size: 13px;"
        );

        // Campo de texto
        TextField campo = dialog.getEditor();
        campo.setPromptText("Digite seu e-mail");
        campo.setPrefHeight(40);
        campo.setStyle(
            "-fx-background-color: #1e2922;" +
            "-fx-text-fill: white;" +
            "-fx-prompt-text-fill: #88958c;" +
            "-fx-border-color: #35453a;" +
            "-fx-border-radius: 6px;" +
            "-fx-background-radius: 6px;" +
            "-fx-padding: 8px;"
        );

        // Organiza os elementos
        javafx.scene.layout.VBox conteudo =
            new javafx.scene.layout.VBox(12, lblMensagem, campo);

        painel.setContent(conteudo);

        // Estilo geral
        painel.setStyle(
            "-fx-background-color: #101512;" +
            "-fx-border-color: #35453a;" +
            "-fx-border-width: 1px;"
        );

        painel.setPrefWidth(430);
        painel.setMinHeight(230);

        // Estiliza o cabeçalho
        painel.lookup(".header-panel").setStyle(
            "-fx-background-color: #19251d;"
        );

        Label header = (Label) painel.lookup(
            ".header-panel .label"
        );

        if (header != null) {
            header.setStyle(
                "-fx-text-fill: #42e878;" +
                "-fx-font-size: 16px;" +
                "-fx-font-weight: bold;"
            );
        }

        // Personaliza os botões
        Button btnOK = (Button) painel.lookupButton(
            ButtonType.OK
        );

        Button btnCancelar = (Button) painel.lookupButton(
            ButtonType.CANCEL
        );

        btnOK.setText("Confirmar");
        btnCancelar.setText("Cancelar");

        btnOK.setStyle(
            "-fx-background-color: #42e878;" +
            "-fx-text-fill: black;" +
            "-fx-font-weight: bold;" +
            "-fx-background-radius: 6px;" +
            "-fx-cursor: hand;" +
            "-fx-pref-width: 110px;" +
            "-fx-pref-height: 36px;"
        );

        btnCancelar.setStyle(
            "-fx-background-color: #29352d;" +
            "-fx-text-fill: white;" +
            "-fx-background-radius: 6px;" +
            "-fx-cursor: hand;" +
            "-fx-pref-height: 36px;"
        );

        // Mantém o botão desabilitado se estiver vazio
        btnOK.disableProperty().bind(
            campo.textProperty().isEmpty()
        );

        // Usa o mesmo campo exibido para retornar o texto
        dialog.setResultConverter(botao -> {
            if (botao == ButtonType.OK) {
                return campo.getText();
            }
            return null;
        });

        return dialog.showAndWait();
    }
}
