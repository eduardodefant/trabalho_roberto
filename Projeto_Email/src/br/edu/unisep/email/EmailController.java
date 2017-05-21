package br.edu.unisep.email;

import java.util.Properties;

import javax.mail.Address;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import br.edu.unisep.utils.MsgUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class EmailController {

	@FXML
	private TextField txtEmail;

	@FXML
	private PasswordField txtSenha;

	@FXML
	private TextArea txtMensagemEnviar;

	@FXML
	private TextField txtTitulo;

	@FXML
	private TextField txtEmailDestinario;

	public void mandarEmail(ActionEvent event) {
		Properties props = new Properties();

		props.put("mail.smtp.host", "smtp.gmail.com");
		props.put("mail.smtp.socketFactory.port", "465");
		props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.port", "465");

		Session session = Session.getDefaultInstance(props, new javax.mail.Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(txtEmail.getText(), txtSenha.getText());
			}
		});
		session.setDebug(true);// debug
		try {

			Message message = new MimeMessage(session);
			// message.setFrom(new InternetAddress("seuemail@gmail.com")); //
			// Remetente
			Address[] toUser = InternetAddress // Destinatário(s)
					.parse(txtEmailDestinario.getText());
			message.setRecipients(Message.RecipientType.TO, toUser);
			message.setSubject(txtTitulo.getText());// Titulo
			message.setText(txtMensagemEnviar.getText());
			Transport.send(message);
			MsgUtils.exibirInfo("Email enviado com sucesso!");
		} catch (MessagingException e) {
			// throw new RuntimeException(e); // antigo er
			e.printStackTrace();
			MsgUtils.exibirErro("Algo de errado aconteceu");
			// teste comentario por José Lucas
		}
	}
}