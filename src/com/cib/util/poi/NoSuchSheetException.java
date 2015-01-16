package com.cib.util.poi;
   

public class  NoSuchSheetException extends Exception {
	
	private static final long serialVersionUID = 6269336805949836857L;
	
	public NoSuchSheetException(String message) {
    	super(message);
     }
    public NoSuchSheetException(String message, Throwable cause) {
        super(message, cause);
    }
    public NoSuchSheetException(Throwable cause) {
        super(cause);
    }
}