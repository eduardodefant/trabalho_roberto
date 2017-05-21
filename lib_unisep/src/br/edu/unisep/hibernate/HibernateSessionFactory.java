package br.edu.unisep.hibernate;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;


public class HibernateSessionFactory {

	private static SessionFactory factory;

	public static SessionFactory getFactory() {


		if (factory == null) { // substitui o driver manager.

			Configuration config = new Configuration().configure(); // ler o hibernate.cfg p/ criar conexao com db.
			factory = config.buildSessionFactory();

		}

		return factory;

	}

}
