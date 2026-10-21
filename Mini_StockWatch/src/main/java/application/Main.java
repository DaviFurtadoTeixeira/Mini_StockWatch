package application;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.scene.Scene;

public class Main extends Application {
	@Override
	public void start(Stage janelaprincipal) throws Exception{
		Parent root = FXMLLoader
				.load(getClass()
						.getResource("/application/telacad.fxml"));
		Scene cena = new Scene(root, 1920,1080);
		janelaprincipal.setScene(cena);
		janelaprincipal
		.setTitle("Tela principal");
		janelaprincipal.setResizable(false);
		janelaprincipal.setMaximized(false);
		janelaprincipal.show();
		
	}
	public static void main(String[] args) {
		launch(args);
	} 
}