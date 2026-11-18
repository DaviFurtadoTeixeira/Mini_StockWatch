package application;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {
    @Override
    public void start(Stage primaryStage) {
        try {
<<<<<<< HEAD
            Parent root = FXMLLoader.load(getClass().getResource("/application/telalogin.fxml"));
=======
            Parent root = FXMLLoader.load(getClass().getResource("/application/telacad.fxml"));
>>>>>>> branch 'master' of https://github.com/DaviFurtadoTeixeira/Mini_StockWatch.git
            Scene scene = new Scene(root);
            primaryStage.setTitle("StockWatch - Cadastro de Produtos");
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
    public static void main(String[] args) {
        launch(args);
    }
}