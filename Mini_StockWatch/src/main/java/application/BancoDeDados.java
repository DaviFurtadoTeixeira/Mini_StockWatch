package application;

import java.io.InputStream; // IMPORTANTE: Mudar de FileInputStream para InputStream
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Properties;

public class BancoDeDados {
	public Connection connection = null;
	public Statement statement = null;
	public ResultSet resultset = null;
	
	public void conectar () {
		Properties props = new Properties();
		// Usando o ClassLoader para ler de src/main/resources/db.properties
		try (InputStream file = BancoDeDados.class.getClassLoader().getResourceAsStream("db.properties")) {
			
			if (file == null) {
				System.out.println("Erro ao conectar: O arquivo db.properties não foi encontrado no classpath.");
				return;
			}
			
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

	// NOVO MÉTODO PARA CADASTRAR O PRODUTO
	public boolean inserirProduto(String id, String nome, String fornecedor, String tipo, int quantidade, Date validade) {
		String sql = "INSERT INTO produtos (id, nome, fornecedor, tipo, quantidade, validade) VALUES (?, ?, ?, ?, ?, ?)";
		
		conectar(); // Abre a conexão usando o db.properties
		
		if (!estaConectado()) {
			return false;
		}
		
		try (PreparedStatement stmt = this.connection.prepareStatement(sql)) {
			stmt.setString(1, id);
			stmt.setString(2, nome);
			stmt.setString(3, fornecedor);
			stmt.setString(4, tipo);
			stmt.setInt(5, quantidade);
			stmt.setDate(6, validade);
			
			stmt.executeUpdate();
			return true;
		} catch (Exception e) {
			System.out.println("Erro ao inserir produto: " + e.getMessage());
			return false;
		} finally {
			desconectar(); // Garante que a conexão será fechada
		}
	}
	
}