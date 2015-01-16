package com.cib.db;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.ibatis.exceptions.PersistenceException;
import org.apache.ibatis.executor.BatchExecutorException;
import org.apache.ibatis.executor.BatchResult;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.util.CollectionUtils;

/**
 * ibatis数据访问接口<br>
 * 
 * 该类不允许继承，直接用spring方式注入使用即可
 * 
 * @author wpf 2011-9-25
 */
public class BaseIbatisDao extends SqlSessionTemplate {
	private ThreadLocal<SqlSession> sessionOnThread= new ThreadLocal<SqlSession>();
	private ThreadLocal<Long> insertCountOnThread= new ThreadLocal<Long>();
	private Logger logger = LoggerFactory.getLogger(BaseIbatisDao.class);
	private int defaultDoFlushSize =10000;
	public BaseIbatisDao(SqlSessionFactory sqlSessionFactory) {
		super(sqlSessionFactory);
	}

	/**
	 * 批量插入数据 <br/>
	 * 1、数据批量插入，默认一次提交100条，当发生异常后继续提交异常行以后的数据，待集合全部进行提交后返回批量处理结果<br/>
	 * 2、数据批量插入，如果需要回滚，当发生异常后，数据库异常即向外抛出，不会进行至全部执行后再抛出异常 <br/>
	 * <功能详细描述>
	 * 
	 * @param statement
	 * @param objectCollection
	 * @param isRollback
	 * @return [参数说明]
	 * 
	 * @return BatchResult<T> [返回类型说明]
	 * @exception throws [异常类型] [异常说明]
	 * @see [类、类#方法、类#成员]
	 */
	public int batchInsert(String statement, List<?> objectList,
			boolean isStopWhenFlushHappenedException) {
		return batchInsert(statement, objectList, defaultDoFlushSize,
				isStopWhenFlushHappenedException);
	}

	/**
	 * 批量插入数据
	 * 
	 * @param statement
	 * @param objectList
	 *            对象列表
	 * @param doFlushSize
	 * @param isStopWhenFlushHappenedException
	 *            当在flush发生异常时是否停止，如果在调用insert时抛出的异常，不在此设置影响范围内
	 * @return void [返回类型说明]
	 * @exception throws [异常类型] [异常说明]
	 * @see [类、类#方法、类#成员]
	 */
	// 批量插入
	public int batchInsert(String statement, List<?> objectList,
			int doFlushSize, boolean isStopWhenFlushHappenedException) {
		if (CollectionUtils.isEmpty(objectList)) {
			return 0;
		}
		if (doFlushSize <= 0) {
			doFlushSize = defaultDoFlushSize;
		}
		// 设置总条数
		int okRow = objectList.size();

		// 从当前环境中根据connection生成批量提交的sqlSession
		SqlSession sqlSession = this.getSqlSessionFactory().openSession(
				ExecutorType.BATCH,true);
		try {
			// 本次flush的列表开始行行索引
			int startFlushRowIndex = 0;
			for (int index = 0; index < objectList.size(); index++) {
				// 插入对象
				insertForBatch(sqlSession, statement, objectList.get(index),
						null);
				if ((index > 0 && index % doFlushSize == 0)
						|| index == objectList.size() - 1) {
					try {
						List<org.apache.ibatis.executor.BatchResult> test = flushBatchStatements(sqlSession);
						startFlushRowIndex = index + 1;
					} catch (Exception ex) {
						if (!(ex.getCause() instanceof BatchExecutorException)
								|| isStopWhenFlushHappenedException) {
							DataAccessException translated = this
									.getPersistenceExceptionTranslator()
									.translateExceptionIfPossible(
											(PersistenceException) ex);
							throw translated;
						}

						BatchExecutorException e = (BatchExecutorException) ex
								.getCause();
						// 如果为忽略错误异常则记录警告日志即可，无需打印堆栈，如果需要堆栈，需将日志级别配置为debug
						logger
								.warn(
										"batchInsert hanppend Exception:{},the exception be igorned.",
										ex.toString());
						if (logger.isDebugEnabled()) {
							logger.debug(ex.toString(), ex);
						}

						// 获取错误行数，由于错误行发生的地方
						int errorRownumIndex = startFlushRowIndex
								+ e.getSuccessfulBatchResults().size();
//						result.addErrorInfoWhenException(objectList.get(index),
//								errorRownumIndex, ex);
						okRow = okRow - e.getSuccessfulBatchResults().size();
						// 将行索引调整为错误行的行号，即从发生错误的行后面一行继续执行
						index = errorRownumIndex;
						startFlushRowIndex = errorRownumIndex + 1;
					}
				}
			}
		} finally {
			sqlSession.close();
		}
		return okRow;
	}
	
	public void startBatch(){
		if(sessionOnThread.get()==null){
			SqlSession sqlSession = this.getSqlSessionFactory().openSession(
					ExecutorType.BATCH,true);
			sessionOnThread.set(sqlSession);
			insertCountOnThread.set(new Long(0));
		}
	}
	
	public void endBatch(){
		if(sessionOnThread.get()!=null){
			SqlSession sqlSession =sessionOnThread.get();
			long count = insertCountOnThread.get().longValue();
			if (count > 0){
				flushBatchStatements(sqlSession);
				logger.info("commit count:"+count);
			}
			sqlSession.close();
			sessionOnThread.remove();
			insertCountOnThread.remove();
		}
	}
	
	public void addBatch(String sqlID,Object data) {
		if(sessionOnThread.get()==null){
			throw new BatchException("add batch error,call startBatch first");
		}
		SqlSession sqlSession =sessionOnThread.get();
		//如果达到defaultDoFlushSize，先提交
		long count = insertCountOnThread.get().longValue();
		if (count > 0 && count % defaultDoFlushSize == 0){
			logger.info("commit count:"+count);
			flushBatchStatements(sqlSession);
			insertCountOnThread.set(new Long(0));
		}
		if (data != null) {
			sqlSession.insert(sqlID, data);
		} else {
			sqlSession.insert(sqlID);
		}
		insertCountOnThread.set(new Long(count+1));
	}

	/**
	 * 提供给批量插入使用 需要使用用户自己控制异常处理， 以及flush的时候 需要自己调用
	 * 
	 * @param statement
	 * @param parameter
	 *            [参数说明]
	 * 
	 * @return void [返回类型说明]
	 * @exception throws [异常类型] [异常说明]
	 * @see [类、类#方法、类#成员]
	 */
	private void insertForBatch(SqlSession sqlSession, String statement,
			Object parameter, String keyProperty) {
		if (!StringUtils.isEmpty(keyProperty)) {
			// 如果指定了keyProperty
//			MetaObject metaObject = MetaObject.forObject(parameter);
//			if (metaObject.hasSetter(keyProperty)
//					&& String.class.equals(metaObject
//							.getSetterType(keyProperty))
//					&& StringUtils.isEmpty((String) metaObject
//							.getValue(keyProperty))) {
//				metaObject.setValue(keyProperty, UUID.randomUUID().toString());
//			}
		}

		if (parameter != null) {
			sqlSession.insert(statement, parameter);
		} else {
			sqlSession.insert(statement);
		}
	}

	private List<BatchResult> flushBatchStatements(
			SqlSession sqlSession) {
		List<BatchResult> result =  sqlSession.flushStatements();
		for(BatchResult br:result){
//			logger.info("commit count:"+br.getUpdateCounts()[0]);
		}
		
		return result;
	}

}
