package application;

import java.io.IOException;
import java.io.InputStream;
import java.security.SecureRandom;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.mindrot.jbcrypt.BCrypt;

public class BancoDeDados {

	public Connection connection = null;
	public Statement statement = null;
	public ResultSet resultset = null;

	private static final SecureRandom random = new SecureRandom();

	// db.properties e o driver são carregados UMA vez só (antes era a cada conexão)
	private static Properties propriedades;

	static {
		// Se o banco estiver fora do ar, falha em até 8s em vez de travar por muito tempo
		DriverManager.setLoginTimeout(8);
	}

	private static synchronized Properties carregarPropriedades()
			throws IOException, ClassNotFoundException {

		if (propriedades == null) {
			try (InputStream file = BancoDeDados.class
					.getClassLoader().getResourceAsStream("db.properties")) {

				if (file == null) {
					throw new IOException(
						"O arquivo db.properties não foi encontrado no classpath.");
				}

				Properties props = new Properties();
				props.load(file);

				Class.forName(props.getProperty("db.driver"));

				propriedades = props;
			}
		}

		return propriedades;
	}

	/**
	 * Aquece classes e a primeira conexão em segundo plano, para que o
	 * primeiro login/cadastro do usuário não pague o custo de "partida a frio".
	 */
	public static void preaquecer() {
		Thread t = new Thread(() -> {
			try {
				BCrypt.hashpw("aquecimento", BCrypt.gensalt(4));

				BancoDeDados bd = new BancoDeDados();
				bd.conectar();
				bd.desconectar();
			} catch (Exception ignorado) {
				// aquecimento é opcional
			}
		}, "stockwatch-warmup");

		t.setDaemon(true);
		t.start();
	}

	public void conectar() {
		try {
			Properties props = carregarPropriedades();

			this.connection = DriverManager.getConnection(
				props.getProperty("db.servidor"),
				props.getProperty("db.usuario"),
				props.getProperty("db.senha")
			);
			// Statement não é mais criado aqui: nenhum método o utilizava
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
				this.connection = null;
			}
		} catch (Exception e) {
			System.out.println("Erro ao desconectar: " + e.getMessage());
		}
	}

	public boolean inserirProduto(String id, String nome, String fornecedor,
			String tipo, int quantidade, Date validade) {

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

		String sqlInserir = "INSERT INTO usuario (id, nome, email, senha) VALUES (?, ?, ?, ?)";
		String sqlVerificar = "SELECT 1 FROM usuario WHERE id = ?";

		String nomeLimpo = nome.trim();
		String emailLimpo = email.trim().toLowerCase();

		// BCrypt é caro (cost 12): calculado UMA vez, ANTES de abrir a conexão.
		// Antes era recalculado a cada tentativa de ID, com a conexão aberta.
		String hash = SenhaUtil.gerarHash(senha);

		conectar();

		if (!estaConectado()) {
			desconectar();
			return false;
		}

		try (PreparedStatement inserir = this.connection.prepareStatement(sqlInserir);
				PreparedStatement verificar = this.connection.prepareStatement(sqlVerificar)) {

			for (int tentativa = 0; tentativa < 10; tentativa++) {

				int id = gerarIdAleatorio();

				verificar.setInt(1, id);

				try (ResultSet rs = verificar.executeQuery()) {
					if (rs.next()) {
						continue; // ID já existe, sorteia outro
					}
				}

				inserir.setInt(1, id);
				inserir.setString(2, nomeLimpo);
				inserir.setString(3, emailLimpo);
				inserir.setString(4, hash);

				return inserir.executeUpdate() == 1;
			}

			return false;

		} catch (Exception e) {
			System.out.println("Erro ao inserir usuário: " + e.getMessage());
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

		String hash = null;

		try (PreparedStatement stmt = this.connection.prepareStatement(sql)) {
			stmt.setString(1, usuario.trim().toLowerCase());
			stmt.setString(2, usuario.trim());

			try (ResultSet rs = stmt.executeQuery()) {
				if (rs.next()) {
					hash = rs.getString("senha");
				}
			}

		} catch (Exception e) {
			System.out.println("Erro ao autenticar usuário: " + e.getMessage());
			return false;

		} finally {
			// Fecha a conexão ANTES da verificação do BCrypt (que é lenta)
			desconectar();
		}

		return hash != null && SenhaUtil.verificar(senha, hash);
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
				produtos.add(new Produto(
					rs.getString("id"),
					rs.getString("nome"),
					rs.getString("fornecedor"),
					rs.getString("tipo"),
					rs.getInt("quantidade"),
					rs.getDate("validade")
				));
			}

			return produtos;

		} catch (Exception e) {
			System.out.println("Erro ao carregar produtos: " + e.getMessage());
			return null;

		} finally {
			desconectar();
		}
	}

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

			System.out.println("Tabela recuperacao_senha criada ou já existente!");

		} catch (SQLException e) {

			System.err.println("Erro ao criar tabela: " + e.getMessage());
			e.printStackTrace();

		} finally {
			desconectar();
		}
	}
}