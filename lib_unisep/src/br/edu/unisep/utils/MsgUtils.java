package br.edu.unisep.utils;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

public class MsgUtils {

	public static void exibirInfo(String texto) {
		exibirMensagem(AlertType.INFORMATION, texto);
	}
	
	public static void exibirAlerta(String texto) {
		exibirMensagem(AlertType.WARNING, texto);
	}

	public static void exibirErro(String texto) {
		exibirMensagem(AlertType.ERROR, texto);
	}

	private static void exibirMensagem(AlertType tipo, String texto) {
		Alert msg = new Alert(tipo);
		msg.setHeaderText(null);
		msg.setContentText(texto);
		msg.showAndWait();
	}
}
