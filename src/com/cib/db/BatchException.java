package com.cib.db;
/**
 * 批处理异常
 * @author wlw
 *
 */
public class BatchException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4255965112693731723L;

	public BatchException() {
	}

	public BatchException(String arg0) {
		super(arg0);
	}

	public BatchException(Throwable arg0) {
		super(arg0);
	}

	public BatchException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}

}
