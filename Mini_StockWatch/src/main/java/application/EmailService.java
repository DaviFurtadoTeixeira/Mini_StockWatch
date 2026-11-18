package application;

import java.util.Properties;

import java.util.Properties;

import jakarta.mail.Authenticator;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

public class EmailService {

public static void enviarCodigo(
        String destinatario, String codigo)
        throws MessagingException {

    String assunto = "StockWatch - Recuperação de senha";

    String conteudo =
        "Olá!\n\n" +
        "Recebemos uma solicitação de recuperação " +
        "de senha da sua conta StockWatch.\n\n" +
        "Seu código de recuperação é:\n\n" +
        codigo + "\n\n" +
        "Este código expira em 10 minutos.\n\n" +
        "Se você não solicitou essa recuperação, " +
        "ignore este e-mail.\n\n" +
        "Equipe StockWatch";

    enviarEmail(destinatario, assunto, conteudo);
}

public static void enviarCodigoCadastro(
        String destinatario, String codigo)
        throws MessagingException {

    String assunto = "StockWatch - Confirmação de cadastro";

    String conteudo =
        "Olá!\n\n" +
        "Bem-vindo ao StockWatch!\n\n" +
        "Para concluir a criação da sua conta, " +
        "precisamos confirmar seu endereço de e-mail.\n\n" +
        "Seu código de verificação é:\n\n" +
        codigo + "\n\n" +
        "Este código expira em 10 minutos.\n\n" +
        "Se você não solicitou este cadastro, " +
        "ignore este e-mail.\n\n" +
        "Equipe StockWatch";

    enviarEmail(destinatario, assunto, conteudo);
}

private static void enviarEmail(
        String destinatario,
        String assunto,
        String conteudo)
        throws MessagingException {

    String email = System.getenv("STOCKWATCH_EMAIL");
    String senha = System.getenv("STOCKWATCH_EMAIL_SENHA");

    if (email == null || senha == null ||
        email.isBlank() || senha.isBlank()) {
        throw new MessagingException(
            "Credenciais de e-mail não configuradas."
        );
    }

    final String usuarioSMTP = email.trim();
    final String senhaSMTP = senha.replace(" ", "").trim();

    Properties props = new Properties();

    props.put("mail.smtp.host", "smtp.gmail.com");
    props.put("mail.smtp.port", "587");
    props.put("mail.smtp.auth", "true");
    props.put("mail.smtp.starttls.enable", "true");
    props.put("mail.smtp.starttls.required", "true");
    props.put("mail.smtp.connectiontimeout", "10000");
    props.put("mail.smtp.timeout", "10000");
    props.put("mail.smtp.writetimeout", "10000");

    Session sessao = Session.getInstance(
        props,
        new Authenticator() {

            @Override
            protected PasswordAuthentication
                    getPasswordAuthentication() {

                return new PasswordAuthentication(
                    usuarioSMTP,
                    senhaSMTP
                );
            }
        }
    );

    MimeMessage mensagem = new MimeMessage(sessao);

    mensagem.setFrom(new InternetAddress(usuarioSMTP));

    mensagem.setRecipients(
        Message.RecipientType.TO,
        InternetAddress.parse(destinatario)
    );

    mensagem.setSubject(assunto, "UTF-8");

    mensagem.setText(conteudo, "UTF-8");

    Transport.send(mensagem);
}

}