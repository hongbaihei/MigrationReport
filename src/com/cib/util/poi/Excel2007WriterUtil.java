package com.cib.util.poi;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.streaming.SXSSFRow;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFDataFormat;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;
import org.apache.poi.xssf.usermodel.XSSFRow;

public class Excel2007WriterUtil {

	private SXSSFWorkbook wb;

	private Logger log = Logger.getLogger(this.getClass());   

	private Map sheetMap = new HashMap(); // sheet名称 ?sheet 总行 

	private List sheetList = new ArrayList(3);

	private SheetWriter curSheetWriter; // 当前Sheet，可能有多个Sheet
	/**
	 * ???Excel????
	 * 
	 * @param log
	 */
	public Excel2007WriterUtil(Logger log) {
		this(log, "sheet1");
	}

	public Excel2007WriterUtil(Logger log, String sheetName) {
		this.log = log;
//		wb = new XSSFWorkbook();
        wb = new SXSSFWorkbook(10000);//内存中保留 10000 条数据，以免内存溢出，其余写入 硬盘          

		curSheetWriter = new SheetWriter(sheetName, (SXSSFSheet) wb.createSheet(sheetName));
	}



	public void createNewSheet() {
		String sheetName = "sheet" + sheetList.size();
		if (sheetMap.get(sheetName) != null) {
			sheetName += "_" + System.currentTimeMillis();
		}
		createNewSheet(sheetName);
	}

	public void createNewSheet(String name) {
		curSheetWriter = new SheetWriter(name, (SXSSFSheet) wb.createSheet(name));
	}

	public void changeSheet(int idx) {

	}

	public void changeSheet(String sheetName) {
		SheetWriter sheetWriter = (SheetWriter) sheetMap.get(sheetName);
		if (sheetWriter == null) {
			throw new IllegalStateException(" can not find sheet " + sheetName);
		}
		this.curSheetWriter = sheetWriter;
	}

	public void write(int row, int column, Object value) {
		write(row, column, value, null);
	}

	public void write(int row, int column, Object value, XSSFCellStyle style) {
		curSheetWriter.writeCell(row, column, value, style);
	}

	/**
	 * 
	 * @param rowFrom
	 * @param colFrom
	 * @param rowTo
	 * @param colTo
	 * @param value
	 * @param style
	 */
	public void writerMergeCell(int rowFrom, int colFrom, int rowTo, int colTo,
			Object value, XSSFCellStyle style) {
		curSheetWriter.writerMergeCell(rowFrom, colFrom, rowTo, colTo, value,
				style);
	}

	public void writeCellFormula(int rowPos, int columnPos, String formula,
			XSSFCellStyle style) {
		curSheetWriter.writeCellFormula(rowPos, columnPos, formula, style);
	}

	public void setColumnWidth(short column, short width) {
		curSheetWriter.setColumnWidth(column, width);
	}
	
	public void setColumnAutoWidth(short column){
		curSheetWriter.setColumnAutoWidth(column);
	}

	/**
	 * set height in points
	 * 
	 * @param row
	 * @param height
	 */
	public void setRowHeight(short row, short height) {
		curSheetWriter.setRowHeight(row, height);
	}
	
	
	public void setDisplayGridlines(boolean bool){
		curSheetWriter.setDisplayGridlines(bool);
	}
	

	// sheet 对象
	private class SheetWriter {
		String sheetName;

		private int totalRow;

		private SXSSFSheet sheet;

		public SheetWriter(String sheetName, SXSSFSheet sheet) {
			this.sheetName = sheetName;
			this.sheet = sheet;
			sheetList.add(this);
			sheetMap.put(sheetName, this);
		}

		private void createRowIfNecessary(int writeRow) {
			while (totalRow <= writeRow) {
				sheet.createRow(totalRow);
				totalRow++;
			}
		}

		public void writeCell(int row, int column, Object value,
				XSSFCellStyle style) {
			if (value == null)
				return;
			createRowIfNecessary(row);

			SXSSFRow workRow = (SXSSFRow) sheet.getRow(row);
			Cell cell = (Cell) workRow.createCell((short) column);
			if (style != null)
				cell.setCellStyle(style);

			writeObjectValue(cell, value);
		}

		/**
		 * 
		 * @param rowFrom
		 * @param colFrom
		 * @param rowTo
		 * @param colTo
		 * @param value
		 * @param style
		 */
		public void writerMergeCell(int rowFrom, int colFrom, int rowTo,
				int colTo, Object value, XSSFCellStyle style) {
			createRowIfNecessary(rowTo);
			CellRangeAddress region = new CellRangeAddress(rowFrom, (short) colFrom, rowTo,
					(short) colTo);
			sheet.addMergedRegion(region);

			for (int i = region.getFirstRow(); i <= region.getLastRow(); i++) {
				XSSFRow row = (XSSFRow) sheet.getRow(i);
				for (int j = region.getFirstRow(); j <= region.getLastRow(); j++) {
					XSSFCell cell = (XSSFCell) row.createCell((short) j);
					if (style != null) {
						cell.setCellStyle(style);
					}
				}
			}

			XSSFRow workRow = (XSSFRow) sheet.getRow(rowFrom);
			XSSFCell cell = (XSSFCell) workRow.getCell((short) colFrom);

			writeObjectValue(cell, value);

		}

		private void writeCellFormula(int rowPos, int columnPos,
				String formula, XSSFCellStyle style) {
			createRowIfNecessary(rowPos);
			XSSFRow workRow = (XSSFRow) sheet.getRow(rowPos);
			XSSFCell cell = (XSSFCell) workRow.createCell((short) columnPos);
			if (style != null)
				cell.setCellStyle(style);
			cell.setCellFormula(formula);
		}

		private void writeObjectValue(Cell cell, Object value) {
			if (value != null) {
				if (String.class.isInstance(value)) {
					cell.setCellValue(new XSSFRichTextString(((String) value)
							.trim()));
				} else if (Double.class.isInstance(value)
						|| Integer.class.isInstance(value)
						|| Long.class.isInstance(value)
						|| Float.class.isInstance(value)) {
					cell
							.setCellValue(Double.parseDouble(String
									.valueOf(value)));
				} else if (Date.class.isInstance(value)) {
					cell.setCellValue((Date) value);
				} else if (Calendar.class.isInstance(value)) {
					cell.setCellValue((Calendar) value);
				} else {
					cell.setCellValue(new XSSFRichTextString((String
							.valueOf(value)).trim()));
				}
			}
		}

		public void setColumnWidth(short column, short width) {
			sheet.setColumnWidth(column, width);
		}
		
		public void setDisplayGridlines(boolean bool){
			sheet.setDisplayGridlines(bool);
		}
		
		public void setColumnAutoWidth(short column){
			sheet.autoSizeColumn(column);
		}

		/**
		 * set height in points
		 * 
		 * @param row
		 * @param height
		 */
		public void setRowHeight(short row, short height) {
			createRowIfNecessary(row);
			XSSFRow workRow = (XSSFRow) sheet.getRow(row);
			workRow.setHeightInPoints(height);
		}
		

		public void autoSizeColumn(int column){
			for (int i = 0; i < column; i++) {
				sheet.autoSizeColumn((short)i);//自动调整列宽
			}
		}
	}

	public XSSFCellStyle newCellStyle() {
		return (XSSFCellStyle) wb.createCellStyle();
	}

	public XSSFFont newFont() {
		return (XSSFFont) wb.createFont();
	}

	public XSSFDataFormat newDataFormat() {
		return (XSSFDataFormat) wb.createDataFormat();
	}

	public void writeExcel(OutputStream outputStream) {
		try {
			wb.write(outputStream);
		} catch (IOException e) {
			e.printStackTrace();
			log.error("", e);
		}
	}
	
	public void autoSizeColumn(int column){
		curSheetWriter.autoSizeColumn(column);
	}

	public short createDataFormat(String format){
		return wb.createDataFormat().getFormat(format);
	}
}
