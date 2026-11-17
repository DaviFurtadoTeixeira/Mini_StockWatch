package application;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HexFormat;

import org.mindrot.jbcrypt.BCrypt;

public class RecuperacaoSenhaService {

    private static final long VALIDADE = 10 * 60 * 1000L;
    private static final long INTERVALO = 60 * 1000L;
    private static final int MAX_TENTATIVAS = 5;

    private static final SecureRandom RANDOM =
        new SecureRandom();

    private static Connection conectar() throws SQLException {

        BancoDeDados banco = new BancoDeDados();
        banco.conectar();

        if (banco.connection == null) {
            throw new SQLException(
                "Não foi possível conectar ao MySQL."
            );
        }

        return banco.connection;
    }

    private static String gerarCodigo() {
        return String.format(
            "%08d", RANDOM.nextInt(100_000_000)
        );
    }

    private static String hashCodigo(String codigo) {

        try {
            MessageDigest md =
                MessageDigest.getInstance("SHA-256");

            byte[] hash = md.digest(
                codigo.getBytes(StandardCharsets.UTF_8)
            );

            return HexFormat.of().formatHex(hash);

        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    public static void solicitarCodigo(String email)
            throws Exception {

        String codigo = gerarCodigo();
        long agora = System.currentTimeMillis();

        boolean enviar = false;

        try (Connection conexao = conectar()) {

            int usuarioId;

            String sqlUsuario =
                "SELECT id FROM usuario WHERE email = ?";

            try (PreparedStatement stmt =
                    conexao.prepareStatement(sqlUsuario)) {

                stmt.setString(1, email);

                try (ResultSet rs = stmt.executeQuery()) {

                    if (!rs.next()) {
                        return;
                    }

                    usuarioId = rs.getInt("id");
                }
            }


            String sqlIntervalo =
                "SELECT criado_em FROM recuperacao_senha " +
                "WHERE usuario_id = ?";

            try (PreparedStatement stmt =
                    conexao.prepareStatement(sqlIntervalo)) {

                stmt.setInt(1, usuarioId);

                try (ResultSet rs = stmt.executeQuery()) {

                    if (rs.next() &&
                        agora - rs.getLong("criado_em") < INTERVALO) {
                        return;
                    }
                }
            }

            String sql =
                "INSERT INTO recuperacao_senha " +
                "(usuario_id, codigo_hash, expira_em, " +
                "criado_em, tentativas) " +
                "VALUES (?, ?, ?, ?, 0) " +
                "ON DUPLICATE KEY UPDATE " +
                "codigo_hash = VALUES(codigo_hash), " +
                "expira_em = VALUES(expira_em), " +
                "criado_em = VALUES(criado_em), " +
                "tentativas = 0";

            try (PreparedStatement stmt =
                    conexao.prepareStatement(sql)) {

                stmt.setInt(1, usuarioId);
                stmt.setString(2, hashCodigo(codigo));
                stmt.setLong(3, agora + VALIDADE);
                stmt.setLong(4, agora);

                stmt.executeUpdate();
                enviar = true;
            }
        }

        if (enviar) {
            EmailService.enviarCodigo(email, codigo);
        }
    }

    public static boolean redefinirSenha(
            String email,
            String codigo,
            String novaSenha) throws SQLException {

        if (codigo == null ||
            !codigo.matches("\\d{8}")) {
            return false;
        }

        try (Connection conexao = conectar()) {

            conexao.setAutoCommit(false);

            try {
                int usuarioId;
                String hashSalvo;
                long expiracao;
                int tentativas;

                String sql =
                    "SELECT r.usuario_id, r.codigo_hash, " +
                    "r.expira_em, r.tentativas " +
                    "FROM recuperacao_senha r " +
                    "INNER JOIN usuario u " +
                    "ON u.id = r.usuario_id " +
                    "WHERE u.email = ? FOR UPDATE";

                try (PreparedStatement stmt =
                        conexao.prepareStatement(sql)) {

                    stmt.setString(1, email);

                    try (ResultSet rs = stmt.executeQuery()) {

                        if (!rs.next()) {
                            conexao.rollback();
                            return false;
                        }

                        usuarioId = rs.getInt("usuario_id");
                        hashSalvo = rs.getString("codigo_hash");
                        expiracao = rs.getLong("expira_em");
                        tentativas = rs.getInt("tentativas");
                    }
                }

                if (System.currentTimeMillis() > expiracao ||
                    tentativas >= MAX_TENTATIVAS) {

                    excluirCodigo(conexao, usuarioId);
                    conexao.commit();
                    return false;
                }

                byte[] recebido = hashCodigo(codigo)
                    .getBytes(StandardCharsets.US_ASCII);

                byte[] esperado = hashSalvo
                    .getBytes(StandardCharsets.US_ASCII);

                if (!MessageDigest.isEqual(recebido, esperado)) {

                    String sqlTentativas =
                        "UPDATE recuperacao_senha " +
                        "SET tentativas = tentativas + 1 " +
                        "WHERE usuario_id = ?";

                    try (PreparedStatement stmt =
                            conexao.prepareStatement(sqlTentativas)) {

                        stmt.setInt(1, usuarioId);
                        stmt.executeUpdate();
                    }

                    conexao.commit();
                    return false;
                }

                String hashSenha = SenhaUtil.gerarHash(novaSenha);

                String sqlSenha =
                    "UPDATE usuario SET senha = ? WHERE id = ?";

                try (PreparedStatement stmt =
                        conexao.prepareStatement(sqlSenha)) {

                    stmt.setString(1, hashSenha);
                    stmt.setInt(2, usuarioId);
                    stmt.executeUpdate();
                }


                excluirCodigo(conexao, usuarioId);

                conexao.commit();
                return true;

            } catch (Exception e) {

                conexao.rollback();

                if (e instanceof SQLException) {
                    throw (SQLException) e;
                }

                throw new SQLException(
                    "Erro ao redefinir senha.", e
                );
            }
        }
    }

    private static void excluirCodigo(
            Connection conexao, int usuarioId)
            throws SQLException {

        String sql =
            "DELETE FROM recuperacao_senha WHERE usuario_id = ?";

        try (PreparedStatement stmt =
                conexao.prepareStatement(sql)) {

            stmt.setInt(1, usuarioId);
            stmt.executeUpdate();
        }
    }
}