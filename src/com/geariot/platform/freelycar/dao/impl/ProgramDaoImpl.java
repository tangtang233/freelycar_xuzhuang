package com.geariot.platform.freelycar.dao.impl;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.geariot.platform.freelycar.dao.ProgramDao;
import com.geariot.platform.freelycar.entities.Program;
import com.geariot.platform.freelycar.utils.Constants;
import com.geariot.platform.freelycar.utils.DateHandler;

@Repository
public class ProgramDaoImpl implements ProgramDao {
	
	@Autowired
	private SessionFactory sessionFactory;
	
	private Session getSession(){
		return sessionFactory.getCurrentSession();
	}

	@Override
	public void save(Program program) {
		this.getSession().save(program);
	}

	@Override
	public void delete(Program program) {
		this.getSession().delete(program);
	}

	@Override
	public void delete(int programId) {
		String hql = "delete from Program where id = :programId";
		this.getSession().createQuery(hql).setInteger("programId", programId).executeUpdate();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Program> listPrograms(int from, int pageSize) {
		String hql = "from Program";
		return this.getSession().createQuery(hql).setFirstResult(from).setMaxResults(pageSize)
				.setCacheable(Constants.SELECT_CACHE).list();
	}

	@Override
	public long getCount() {
		String hql = "select count(*) from Program";
		return (long) this.getSession().createQuery(hql).setCacheable(Constants.SELECT_CACHE).uniqueResult();
	}

	@Override
	public Program findProgramByProgramId(int programId) {
		String hql = "from Program where id = :programId";
		return (Program) getSession().createQuery(hql).setInteger("programId", programId)
				.setCacheable(Constants.SELECT_CACHE).uniqueResult();
	}

	@Override
	public Program findProgramByName(String name) {
		String hql = "from Program where name = :name";
		return (Program) getSession().createQuery(hql).setString("name", name)
				.setCacheable(Constants.SELECT_CACHE).uniqueResult();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Program> listAll() {
		String hql = "from Program";
		return this.getSession().createQuery(hql).setCacheable(Constants.SELECT_CACHE).list();
	}

	
	private Program program;
	
	@Override
	public Program insertIfExist(int id, String programName) {
		String sql = "INSERT INTO program (id, name, createDate) SELECT :id, :programName, :createDate FROM dual WHERE not exists (select * from program where program.id = :id)";
		getSession().createSQLQuery(sql).setInteger("id", id)
										.setString("programName", programName)
										.setTimestamp("createDate", DateHandler.getCurrentDate()).executeUpdate();
		program = new Program();
		program.setId(id);
		return program;
	}
		
	
}
