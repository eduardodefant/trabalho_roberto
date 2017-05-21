package br.edu.unisep.hibernate;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

public class HibernateDAO<T> {

	protected Session getSession() {

		SessionFactory factory = HibernateSessionFactory.getFactory();
		Session session = factory.openSession();

		return session;

	}

	public void salvar(T objeto) {

		Session session = getSession();

		Transaction trans = session.beginTransaction();

		try {

			session.save(objeto);
			trans.commit();

		} catch (Exception e) {

			trans.rollback();
			e.printStackTrace();

		}

		session.close();

	}

	public void excluir(T objeto) {

		Session session = getSession();

		Transaction trans = session.beginTransaction();

		try {

			session.delete(objeto);
			trans.commit();

		} catch (Exception e) {

			trans.rollback();
			e.printStackTrace();

		}

		session.close();

	}

	public void alterar(T objeto) {

		Session session = getSession();

		Transaction trans = session.beginTransaction();

		try {

			session.update(objeto);
			trans.commit();

		} catch (Exception e) {

			trans.rollback();
			e.printStackTrace();

		}

		session.close();

	}

	public List<T> listar(Class<T> classe) {

		Session session = getSession();

		@SuppressWarnings("unchecked")
		Query<T> q = session.createQuery(" from " + classe.getName());
		// q.getResultList(); // util par pegar varios campos do db.
		// q.uniqueResult(); // util para pegar 1 resultado do db.

		List<T> lista = q.getResultList();

		session.close();
		return lista;

	}

	public T consultar(Class<T> classe, Integer id) {

		Session session = getSession();

		@SuppressWarnings("unchecked")
		Query<T> q = session.createQuery(" from " + classe.getName() + "where id = ?");
		q.setParameter(0, id);

		T objeto = q.getSingleResult();
		session.close();

		return objeto;
	}

}