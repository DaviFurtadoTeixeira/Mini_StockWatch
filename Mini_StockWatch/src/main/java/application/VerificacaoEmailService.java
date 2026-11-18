
package application;

import java.security.SecureRandom;

public class VerificacaoEmailService {

    private static final SecureRandom random =
        new SecureRandom();

    public static String gerarCodigo() {
        return String.format(
            "%08d",
            random.nextInt(100_000_000)
        );
    }

    public static boolean verificarEmail(String email)
            throws Exception {

        String codigo = gerarCodigo();

        EmailService.enviarCodigoCadastro(email, codigo);

        long expiracao =
            System.currentTimeMillis() + 600_000;

        int tentativas = 0;

        while (tentativas < 5) {

            var resultado = TextInputPersonalizado.mostrar(
                "Verificação de e-mail",
                "Digite o código enviado para " + email
            );

            if (resultado.isEmpty()) {
                return false;
            }

            if (System.currentTimeMillis() > expiracao) {
                return false;
            }

            if (codigo.equals(resultado.get().trim())) {
                return true;
            }

            tentativas++;

            AlertPersonalizado.mostrar(
                javafx.scene.control.Alert.AlertType.ERROR,
                "Código inválido!",
                "Código incorreto. Tentativas restantes: "
                    + (5 - tentativas)
            );
        }

        return false;
    }
}
