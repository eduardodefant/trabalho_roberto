package br.edu.unisep.email;

import java.util.Properties;

import javax.mail.PasswordAuthentication;
import javax.mail.Session;

import br.edu.unisep.utils.MsgUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;

public class LoginController {

	@FXML
	TextField EmailLogin;

	@FXML
	TextField SenhaLogin;

	public void mandarEmail(ActionEvent event) {

		try {
			Properties props = new Properties();

			props.put("mail.smtp.host", "smtp.gmail.com");
			props.put("mail.smtp.socketFactory.port", "465");
			props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
			props.put("mail.smtp.auth", "true");
			props.put("mail.smtp.port", "465");

			@SuppressWarnings("unused")
			Session session = Session.getDefaultInstance(props, new javax.mail.Authenticator() {
				protected PasswordAuthentication getPasswordAuthentication() {
					return new PasswordAuthentication(EmailLogin.getText(), SenhaLogin.getText());
				}
			});
		} catch (Exception e) { // verificar essa exception (passwordException)
			MsgUtils.exibirAlerta("Não foi possivel realizar login.");
		}
	}
}