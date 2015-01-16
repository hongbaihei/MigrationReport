package com.cib.db;

import java.sql.SQLException;

import org.apache.ibatis.session.ResultContext;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.SqlSessionFactory;

/**
 * 用于对查询结果逐条处理
 * 
 * @author wlw
 * 
 */
public abstract class SelectBatch extends BaseIbatisDao {

	public SelectBatch(SqlSessionFactory sqlSessionFactory) {
		super(sqlSessionFactory);
	}

	public void executeSelect(String sqlid, Object param) throws Exception {
		this.select(sqlid, param, new SqlMapRowHandler());
	}
	
	public void executeSelect(String sqlid) throws Exception {
		this.select(sqlid, new SqlMapRowHandler());
	}
	
	public void executeInsert(String sqlid, Object param) throws Exception {
		this.insert(sqlid, param);
	}

	/**
	 * 具体的处理部分 
	 * 
	 * @param row
	 *            row
	 * @throws SQLException
	 *             SQLException 2008-8-13
	 */
	public abstract void processRecord(Object row) throws Exception;

	public class SqlMapRowHandler implements ResultHandler {
		@Override
		public void handleResult(ResultContext target) {
			try {
				processRecord(target.getResultObject());
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
	}
}
