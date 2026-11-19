package application;

import java.security.SecureRandom;

import javafx.scene.control.Alert;

/**
 * Verificação de e-mail no cadastro, dividida em duas etapas:
 *
 * 1) enviarCodigo(...)      -> lento (SMTP). Chamar em SEGUNDO PLANO (Async).
 * 2) confirmarNaTela(...)   -> abre diálogos. Chamar na thread do JavaFX.
 *
 * Antes, as duas coisas ficavam juntas e o envio do e-mail congelava a tela.
 */
public class VerificacaoEmailService {

    private static final SecureRandom random = new SecureRandom();

    private static final long VALIDADE_MS = 600_000;
    private static final int MAX_TENTATIVAS = 5;

    public record CodigoEnviado(String codigo, long expiracao) {
    }

    public static String gerarCodigo() {
        return String.format("%08d", random.nextInt(100_000_000));
    }

    /** Gera o código e envia por e-mail. BLOQUEANTE: não chamar na thread do JavaFX. */
    public static CodigoEnviado enviarCodigo(String email) throws Exception {

        String codigo = gerarCodigo();

        EmailService.enviarCodigoCadastro(email, codigo);

        return new CodigoEnviado(
            codigo,
            System.currentTimeMillis() + VALIDADE_MS
        );
    }

    /** Pede o código ao usuário. Deve rodar na thread do JavaFX. */
    public static boolean confirmarNaTela(String email, CodigoEnviado enviado) {

        int tentativas = 0;

        while (tentativas < MAX_TENTATIVAS) {

            var resultado = TextInputPersonalizado.mostrar(
                "Verificação de e-mail",
                "Digite o código enviado para " + email
            );

            if (resultado.isEmpty()) {
                return false;
            }

            if (System.currentTimeMillis() > enviado.expiracao()) {
                return false;
            }

            if (enviado.codigo().equals(resultado.get().trim())) {
                return true;
            }

            tentativas++;

            AlertPersonalizado.mostrar(
                Alert.AlertType.ERROR,
                "Código inválido!",
                "Código incorreto. Tentativas restantes: "
                    + (MAX_TENTATIVAS - tentativas)
            );
        }

        return false;
    }
}