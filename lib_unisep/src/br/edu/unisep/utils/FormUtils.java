package br.edu.unisep.utils;

import javafx.scene.control.TextField;

public class FormUtils {

	public static boolean vazio(TextField campo, String nomeCampo) {
		String conteudo = campo.getText().trim();
		
		if (conteudo.equals("")) {
			MsgUtils.exibirAlerta("O campo " + nomeCampo + " é obrigatório!");
			campo.requestFocus();

			return true;
		}
		
		return false;
	}
	
}
