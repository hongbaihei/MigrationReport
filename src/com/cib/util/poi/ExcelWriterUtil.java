/**********************************************************************
 * <pre>
 * FILE : ExcelWriterUtil.java
 * CLASS : ExcelWriterUtil
 *
 * AUTHOR : Luo hanbin
 *
 * FUNCTION 
 * </pre>
 ***********************************************************************/
package com.cib.util.poi;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFDataFormat;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFPalette;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.Region;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;

/**
 * @author Administrator
 * 
 * 
 */
public class ExcelWriterUtil {

	private HSSFWorkbook wb;

//	private Log log;
	private Logger log = Logger.getLogger(this.getClass());   


	private Map sheetMap = new HashMap(); // sheet名称， sheet 总行数

	private List sheetList = new ArrayList(3);

	private SheetWriter curSheetWriter; // 当前Sheet，可能有多个Sheet

	/**
	 * ???Excel????
	 * 
	 * @param log
	 */
	public ExcelWriterUtil(Logger log) {
		this(log, "sheet1");
	}

	public ExcelWriterUtil(Logger log, String sheetName) {
		this.log = log;
		wb = new HSSFWorkbook();
		curSheetWriter = new SheetWriter(sheetName, (HSSFSheet) wb.createSheet(sheetName));
	}

	/**
	 * 针对模板， 暂时不使用???f????Excel????
	 * 
	 * @param templateInputStream
	 * @param log
	 * @throws IOException
	 */
	public ExcelWriterUtil(InputStream templateInputStream, int sheetIdx,
			Logger log) throws IOException {
		this.log = log;

		POIFSFileSystem fs = new POIFSFileSystem(templateInputStream);
		wb = new HSSFWorkbook(fs);
		HSSFSheet sheet = (HSSFSheet) wb.getSheetAt(sheetIdx);
		String sheetName = wb.getSheetName(sheetIdx);
		curSheetWriter = new SheetWriter(sheetName, sheet);
	}

	/**
	 * 
	 * @param templateInputStream
	 * @param log
	 */
	public ExcelWriterUtil(InputStream templateInputStream, Logger log)
			throws IOException {
		this(templateInputStream, 0, log);
	}

	public void createNewSheet() {
		String sheetName = "sheet" + sheetList.size();
		if (sheetMap.get(sheetName) != null) {
			sheetName += "_" + System.currentTimeMillis();
		}
		createNewSheet(sheetName);
	}

	public void createNewSheet(String name) {
		curSheetWriter = new SheetWriter(name, (HSSFSheet) wb.createSheet(name));
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

	public void write(int row, int column, Object value, HSSFCellStyle style) {
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
			Object value, HSSFCellStyle style) {
		curSheetWriter.writerMergeCell(rowFrom, colFrom, rowTo, colTo, value,
				style);
	}

	public void writeCellFormula(int rowPos, int columnPos, String formula,
			HSSFCellStyle style) {
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

		private HSSFSheet sheet;

		public SheetWriter(String sheetName, HSSFSheet sheet) {
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
				HSSFCellStyle style) {
			if (value == null)
				return;
			createRowIfNecessary(row);

			HSSFRow workRow = (HSSFRow) sheet.getRow(row);
			HSSFCell cell = (HSSFCell) workRow.createCell((short) column);
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
				int colTo, Object value, HSSFCellStyle style) {
			createRowIfNecessary(rowTo);
			Region region = new Region(rowFrom, (short) colFrom, rowTo,
					(short) colTo);
			sheet.addMergedRegion(region);

			for (int i = region.getRowFrom(); i <= region.getRowTo(); i++) {
				HSSFRow row = (HSSFRow) sheet.getRow(i);
				for (int j = region.getColumnFrom(); j <= region.getColumnTo(); j++) {
					HSSFCell cell = (HSSFCell) row.createCell((short) j);
					if (style != null) {
						cell.setCellStyle(style);
					}
				}
			}

			HSSFRow workRow = (HSSFRow) sheet.getRow(rowFrom);
			HSSFCell cell = (HSSFCell) workRow.getCell((short) colFrom);

			writeObjectValue(cell, value);

		}

		private void writeCellFormula(int rowPos, int columnPos,
				String formula, HSSFCellStyle style) {
			createRowIfNecessary(rowPos);
			HSSFRow workRow = (HSSFRow) sheet.getRow(rowPos);
			HSSFCell cell = (HSSFCell) workRow.createCell((short) columnPos);
			if (style != null)
				cell.setCellStyle(style);
			cell.setCellFormula(formula);
		}

		private void writeObjectValue(HSSFCell cell, Object value) {
			if (value != null) {
				if (String.class.isInstance(value)) {
					cell.setCellValue(new HSSFRichTextString(((String) value)
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
					cell.setCellValue(new HSSFRichTextString((String
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
			HSSFRow workRow = (HSSFRow) sheet.getRow(row);
			workRow.setHeightInPoints(height);
		}
		
		public void adjustRowHeight() {
			// 默认行间距，pixel为单位
			float defaultRowGapInPoint = 4f;
			// 得到所有的合并区域
			List regions = new ArrayList();
			for (int i = 0; i < sheet.getNumMergedRegions(); i++) {
				regions.add(sheet.getMergedRegionAt(i));
			}
			int rows = sheet.getPhysicalNumberOfRows();
			for (int r = 0; r < rows; r++) {
				HSSFRow row = sheet.getRow(r);
				int cells = row.getPhysicalNumberOfCells();
				for (short c = 0; c < cells; c++) {
					HSSFCell cell = row.getCell(c);
					// 只对String类型单元格调整高度
					if (cell != null
							&& cell.getCellType() == HSSFCell.CELL_TYPE_STRING) {
						boolean isBelongToRegion = false;
						for (int i = 0; i < regions.size(); i++) {
							Region region = (Region) regions.get(i);
							if (region.contains(r, c)) {
								isBelongToRegion = true;
								int rowFrom = region.getRowFrom();
								int rowTo = region.getRowTo();
								int colFrom = region.getColumnFrom();
								int colTo = region.getColumnTo();
								int regionWidths = 0;
								for (int ii = colFrom; ii <= colTo; ii++) {
									regionWidths += sheet.getColumnWidth(ii);
								}
								long stringWidths = cell
										.getRichStringCellValue().toString()
										.getBytes().length * 256;
								// 计算所需高度为默认高度的多少倍
								long aRows = stringWidths / regionWidths + 1 + 1;
								// 得到每行应该有的高度
								HSSFFont font = cell.getCellStyle().getFont(
										sheet.getWorkbook());
								float rowHeightInPoint = font
										.getFontHeightInPoints()
										+ defaultRowGapInPoint;
								float height = rowHeightInPoint * aRows
										/ (rowTo - rowFrom +1 );
								for (int jj = rowFrom; jj <= rowTo; jj++) {
									HSSFRow RegionRow = sheet.getRow(jj);
									if (RegionRow.getHeightInPoints() < height) {
										RegionRow.setHeightInPoints(height);
									}
								}
								break;
							}
						}
						if (!isBelongToRegion) {
							long stringWidths = cell.getRichStringCellValue()
									.toString().getBytes().length * 256;
							// 得到列宽为一个字符的1/256
							long colWidth = sheet.getColumnWidth(c);
							long aRows = stringWidths / colWidth + 1;
							// 得到每行应该有的高度
							HSSFFont font = cell.getCellStyle().getFont(
									sheet.getWorkbook());
							float rowHeightInPoint = font
									.getFontHeightInPoints()
									+ defaultRowGapInPoint;
							if (row.getHeightInPoints() < aRows
									* rowHeightInPoint) {
								row.setHeightInPoints(aRows * rowHeightInPoint);
							}
						}
					}
				}
			}
		}

		public void autoSizeColumn(int column){
			for (int i = 0; i < column; i++) {
				sheet.autoSizeColumn((short)i);//自动调整列宽
			}
		}
	}

	public HSSFCellStyle newCellStyle() {
		return (HSSFCellStyle) wb.createCellStyle();
	}

	public HSSFPalette getPalette() {
		return wb.getCustomPalette();
	}

	public HSSFFont newFont() {
		return (HSSFFont) wb.createFont();
	}

	public HSSFDataFormat newDataFormat() {
		return (HSSFDataFormat) wb.createDataFormat();
	}

	public void writeExcel(OutputStream outputStream) {
		try {
			wb.write(outputStream);
		} catch (IOException e) {
			e.printStackTrace();
			log.error("", e);
		}
	}
	
	public void adjustRowHeight(){
		curSheetWriter.adjustRowHeight();
	}
	
	public void autoSizeColumn(int column){
		curSheetWriter.autoSizeColumn(column);
	}

	public short createDataFormat(String format){
		return wb.createDataFormat().getFormat(format);
	}
}
