package application;

import org.mindrot.jbcrypt.BCrypt;
import java.security.SecureRandom;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;
import java.util.ArrayList;
import java.util.List;
public class BancoDeDados {
	public Connection connection = null;
	public Statement statement = null;
	public ResultSet resultset = null;
	
	public void conectar () {
		Properties props = new Properties();
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
	public boolean inserirProduto(String id, String nome, String fornecedor, String tipo, int quantidade, Date validade) {
		String sql = "INSERT INTO produtos (id, nome, fornecedor, tipo, quantidade, validade) VALUES (?, ?, ?, ?, ?, ?)";
		
		conectar();
		
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
			desconectar();
		}
	}
	public boolean inserirUsuario(String nome, String email, String senha) {
	    String sql = "INSERT INTO usuario (id, nome, email, senha) VALUES (?, ?, ?, ?)";

	    conectar();

	    if (!estaConectado()) {
	        desconectar();
	        return false;
	    }

	    try (PreparedStatement stmt = this.connection.prepareStatement(sql)) {
	        stmt.setString(1, nome.trim());
	        stmt.setString(2, email.trim().toLowerCase());
	        stmt.setString(3, SenhaUtil.gerarHash(senha));
	        
	        for (int tentativa = 0; tentativa < 10; tentativa++) {

	            int id = gerarIdAleatorio();

	            try (PreparedStatement verificar =
	                    this.connection.prepareStatement(
	                        "SELECT id FROM usuario WHERE id = ?")) {

	                verificar.setInt(1, id);

	                try (ResultSet rs = verificar.executeQuery()) {
	                    if (rs.next()) {
	                        continue;
	                    }
	                }
	            }

	            stmt.setInt(1, id);
	            stmt.setString(2, nome.trim());
	            stmt.setString(3, email.trim().toLowerCase());
	            stmt.setString(4, SenhaUtil.gerarHash(senha));

	            try {
	                return stmt.executeUpdate() == 1;

	            } catch (java.sql.SQLIntegrityConstraintViolationException e) {
	                // Pode ocorrer uma colisão do ID ou outro
	                // problema de restrição (como e-mail duplicado).
	                // Não repetir automaticamente nesse caso.
	                throw e;
	            }
	        }

	        return false;

	    } catch (Exception e) {

	        System.out.println(
	            "Erro ao inserir usuário: " + e.getMessage()
	        );

	        return false;

	    } finally {
	        desconectar();
	    }
	}
	public boolean autenticarUsuario(String usuario, String senha) {
	    String sql = """
	        SELECT senha
	        FROM usuario
	        WHERE email = ? OR nome = ?
	        """;

	    conectar();

	    if (!estaConectado()) {
	        desconectar();
	        return false;
	    }

	    try (PreparedStatement stmt = this.connection.prepareStatement(sql)) {
	        stmt.setString(1, usuario.trim().toLowerCase());
	        stmt.setString(2, usuario.trim());

	        try (ResultSet rs = stmt.executeQuery()) {
	            if (rs.next()) {
	                String hash = rs.getString("senha");
	                return SenhaUtil.verificar(senha, hash);
	            }

	            return false;
	        }

	    } catch (Exception e) {
	        System.out.println("Erro ao autenticar usuário: " + e.getMessage());
	        return false;

	    } finally {
	        desconectar();
	    }
	}

public List<Produto> listarProdutos() {
    List<Produto> produtos = new ArrayList<>();

    String sql = """
        SELECT id, nome, fornecedor, tipo, quantidade, validade
        FROM produtos
        """;

    conectar();

    if (!estaConectado()) {
        desconectar();
        return null;
    }

    try (PreparedStatement stmt = connection.prepareStatement(sql);
         ResultSet rs = stmt.executeQuery()) {

        while (rs.next()) {
            String id = rs.getString("id");
            String nome = rs.getString("nome");
            String fornecedor = rs.getString("fornecedor");
            String tipo = rs.getString("tipo");
            int quantidade = rs.getInt("quantidade");
            Date validade = rs.getDate("validade");

            Produto produto = new Produto(
                id,
                nome,
                fornecedor,
                tipo,
                quantidade,
                validade
            );

            produtos.add(produto);
        }

        return produtos;

    } catch (Exception e) {
        System.out.println("Erro ao carregar produtos: " + e.getMessage());
        return null;

    } finally {
        desconectar();
    }
}
private static final SecureRandom random = new SecureRandom();

private int gerarIdAleatorio() {
    return 100000000 + random.nextInt(900000000);
}


public void criarTabelaRecuperacao() {

    String sql = """
        CREATE TABLE IF NOT EXISTS recuperacao_senha (
            usuario_id INT NOT NULL PRIMARY KEY,
            codigo_hash CHAR(64) NOT NULL,
            expira_em BIGINT NOT NULL,
            criado_em BIGINT NOT NULL,
            tentativas INT NOT NULL DEFAULT 0,

            CONSTRAINT fk_recuperacao_usuario
            FOREIGN KEY (usuario_id)
            REFERENCES usuario(id)
            ON DELETE CASCADE
        )
        """;

    conectar();

    if (!estaConectado()) {
        System.out.println("Erro ao conectar ao banco!");
        desconectar();
        return;
    }

    try (Statement stmt = connection.createStatement()) {

        stmt.executeUpdate(sql);

        System.out.println(
            "Tabela recuperacao_senha criada ou já existente!"
        );

    } catch (SQLException e) {

        System.err.println(
            "Erro ao criar tabela: " + e.getMessage()
        );

        e.printStackTrace();

    } finally {
        desconectar();
    }
}

}