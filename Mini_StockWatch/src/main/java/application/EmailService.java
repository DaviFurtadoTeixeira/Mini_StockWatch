package application;

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

        String email = System.getenv("STOCKWATCH_EMAIL");
        String senha = System.getenv("STOCKWATCH_EMAIL_SENHA");

        if (email == null || senha == null ||
            email.isBlank() || senha.isBlank()) {
            throw new MessagingException(
                "Credenciais de e-mail não configuradas."
            );
        }

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
                        email, senha
                    );
                }
            }
        );

        Message mensagem = new MimeMessage(sessao);

        mensagem.setFrom(new InternetAddress(email));
        mensagem.setRecipients(
            Message.RecipientType.TO,
            InternetAddress.parse(destinatario)
        );

        mensagem.setSubject(
            "StockWatch - Recuperação de senha"
        );

        mensagem.setText(
            "Olá!\n\n" +
            "Recebemos uma solicitação para redefinir " +
            "sua senha do StockWatch.\n\n" +
            "Seu código de recuperação é:\n\n" +
            codigo + "\n\n" +
            "Este código expira em 10 minutos.\n\n" +
            "Se você não solicitou a recuperação, " +
            "ignore este e-mail.\n\n" +
            "Equipe StockWatch"
        );

        Transport.send(mensagem);
    }
}