package com.cib.util.poi.reader;

import java.util.List;

public interface IRowReader {
	 /**业务逻辑实现方法 
     * @param sheetIndex 
     * @param curRow 
     * @param rowlist 
	 * @param curFileName 
     */  
    public  void getRows(int sheetIndex,int curRow, List<String> rowlist, String curFileName);  
}
