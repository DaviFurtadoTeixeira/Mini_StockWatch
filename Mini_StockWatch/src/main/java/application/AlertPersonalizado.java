package application;

import javafx.scene.control.Alert;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.paint.Color;

public class AlertPersonalizado {

    public static void mostrar(
            Alert.AlertType tipo,
            String titulo,
            String mensagem) {

        Alert alert = new Alert(tipo);

        alert.setTitle("StockWatch");
        alert.setHeaderText(titulo);
        alert.setContentText(mensagem);

      
        alert.setGraphic(criarIcone(tipo));

      
        DialogPane dialog = alert.getDialogPane();

        dialog.setStyle(
            "-fx-background-color: #101512;" +
            "-fx-border-color: #35453a;" +
            "-fx-border-width: 1px;"
        );

        
        var css = AlertPersonalizado.class.getResource("style.css");

        if (css != null) {
            dialog.getStylesheets().add(css.toExternalForm());
        }

        alert.showAndWait();
    }

    private static StackPane criarIcone(Alert.AlertType tipo) {

        String simbolo;
        String cor;
        String corTexto;

        switch (tipo) {
            case ERROR:
                simbolo = "\u2715";
                cor = "#ff5252";
                corTexto = "#FFFFFF";
                break;

            case WARNING:
                simbolo = "!";
                cor = "#ffca28";
                corTexto = "#FFFFFF";
                break;

            case CONFIRMATION:
                simbolo = "?";
                cor = "#42e878";
                corTexto = "#FFFFFF";
                break;

            case INFORMATION:
            default:
                simbolo = "\u2713";
                cor = "#42e878";
                corTexto = "#FFFFFF";
                break;
        }

        Circle circulo = new Circle(25);
        circulo.setFill(Color.web(cor));

        Label texto = new Label(simbolo);
        texto.setTextFill(Color.web(corTexto));
        texto.setFont(Font.font("System", 27));
        texto.setStyle("-fx-font-weight: bold;");

        StackPane icone = new StackPane(circulo, texto);
        icone.setMinSize(54, 54);
        icone.setPrefSize(54, 54);
        icone.setMaxSize(54, 54);

        return icone;
    }
}