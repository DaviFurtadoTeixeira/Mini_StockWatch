package application;

import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Properties;

public class BancoDeDados {
	public Connection connection = null;
	public Statement statement = null;
	public ResultSet resultset = null;
	
	public void conectar () {
		Properties props = new Properties();
		try {
		
			FileInputStream file = new FileInputStream("db.properties");
			props.load(file);
			
			String servidor = props.getProperty("db.servidor");
			String usuario = props.getProperty("db.usuario");
			String senha = props.getProperty("db.senha");
			String driver = props.getProperty("db.driver");
			
			Class.forName(driver);
			this.connection = DriverManager.getConnection(servidor, usuario, senha);
			this.statement = this.connection.createStatement();
		} catch (Exception e) {
			System.out.println("Erro ao conectar: " + e.getMessage());
		}
	}
	
	public boolean estaConectado() {
		return this.connection != null;
	}
	
	public void desconectar() {
		try {
			if (this.connection != null) {
				this.connection.close();
			}
		} catch (Exception e) {
			System.out.println("Erro ao desconectar: " + e.getMessage());
		}
	}
	//teste
}