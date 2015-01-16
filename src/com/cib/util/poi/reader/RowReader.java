package com.cib.util.poi.reader;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.cib.db.BaseIbatisDao;

public class RowReader implements IRowReader {
	private BaseIbatisDao mensqldao;

	/*
	 * 业务逻辑实现方法
	 * 
	 * @see com.eprosun.util.excel.IRowReader#getRows(int, int, java.util.List)
	 */
	@Override
	public void getRows(int sheetIndex, int curRow, List<String> rowlist, String curFileName) {
		if (curRow != 0) {// 不处理表头
			if (rowlist.size() == 7) {
				Map<String, Object> data = new HashMap<String, Object>();
				data.put("SK_DATE", rowlist.get(0));
				data.put("UPDATE_NO", rowlist.get(1));
				data.put("UPDATE_BY", rowlist.get(2));
				data.put("UPDATE_ORG", rowlist.get(3));
				data.put("UPDATE_SUB", rowlist.get(4));
				data.put("IDENTITY_NUM", rowlist.get(5));
				data.put("LEVEL", rowlist.get(6));
				if (curFileName.contains("table1"))// table1
					mensqldao.addBatch("mem.insert_derive1", data);
				if (curFileName.contains("table2"))// table2
					mensqldao.addBatch("mem.insert_derive2", data);
			}
			else if (rowlist.size() == 4) {
				//TODO
			}
		}

	}

	public void setMensqldao(BaseIbatisDao mensqldao) {
		this.mensqldao = mensqldao;
	}

}
