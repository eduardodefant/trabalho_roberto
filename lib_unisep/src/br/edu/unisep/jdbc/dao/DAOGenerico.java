package br.edu.unisep.jdbc.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public abstract class DAOGenerico {

	private String nomeBanco;

	public DAOGenerico(String banco) {
		this.nomeBanco = banco;
	}

	protected Connection obterConexao() {

		Connection con = null;

		try {
			Class.forName("org.postgresql.Driver");

			con = DriverManager.getConnection(
					"jdbc:postgresql://localhost:5433/" + nomeBanco,
						"postgres", "123");
		} catch(ClassNotFoundException e) {
			e.printStackTrace();
		} catch(SQLException e) {
			e.printStackTrace();
		}

		return con;
	}

}
