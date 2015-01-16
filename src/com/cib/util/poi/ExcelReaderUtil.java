package com.cib.util.poi;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.cib.util.poi.reader.Excel2007Reader;
import com.cib.util.poi.reader.IRowReader;

public class ExcelReaderUtil {

	private Workbook workbook;

	protected Sheet sheet;

	private int numOfSheet;

	private int lastRowNum; 

	private boolean isMergedCellUseFirstCellValue; // 是否设置合并单元格

	private Set alreadyInitMergedCellValueSet; // 当前sheet的合并单元格

	// protected final Log logger = LogFactory.getLog(getClass());
	protected final Logger logger = Logger.getLogger(this.getClass());

	public ExcelReaderUtil() {
	}
	
	/**
	 * 
	 * @param is
	 * @throws InvalidFormatException
	 * @throws IOException
	 */
	public void openExcel2007(String filename) throws InvalidFormatException, IOException{
		OPCPackage pkg = OPCPackage.open(filename);
		XSSFWorkbook xssfwb = new XSSFWorkbook(pkg);
		workbook = new SXSSFWorkbook(xssfwb, 1000);
	}
	
	/**
	 * SAXParser 处理
	 * @param reader
	 * @param fileName
	 * @throws Exception
	 */
	public static void readExcel(IRowReader reader,String fileName) throws Exception{  
        Excel2007Reader excel07 = new Excel2007Reader();  
        excel07.setRowReader(reader);  
        excel07.processOneSheet(fileName, 1);  //只读第一sheet
    }  

	/**
	 * 根据文件判断是否为EXCEL，并返回相应的Workbook
	 * 
	 * @param finName
	 * @param is
	 * @return
	 * @throws IOException
	 */
	public void createWorkBook(String fileName, BufferedInputStream is,boolean isBigData) throws IOException {

		if (!fileName.toLowerCase().endsWith(".xls") && !fileName.toLowerCase().endsWith(".xlsx")) {
			throw new IllegalArgumentException("fileName error!");
		}

		try {
			if (fileName.toLowerCase().endsWith(".xls")) {
				workbook = new HSSFWorkbook(is);
			}else if(isBigData){
				XSSFWorkbook xssworkbook = new XSSFWorkbook(is);
				workbook = new SXSSFWorkbook(xssworkbook,1000);
			}else {
				workbook = new XSSFWorkbook(is);
			}
		} catch (Exception e) {
			logger.warn("excel cannot open ");
			throw new IllegalArgumentException(e.getMessage(), e);
		}

		numOfSheet = workbook.getNumberOfSheets();
		alreadyInitMergedCellValueSet = new HashSet(numOfSheet);

		try {
			locateSheet(0);
		} catch (NoSuchSheetException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 合并单元格是否使用第�?��单元格�?，�?是�?则在获取合并单元格其余单元格时数据不会为�?
	 * 
	 * @param isMergedCellUseFirstCellValue
	 */
	public ExcelReaderUtil(boolean isMergedCellUseFirstCellValue) {
		this.isMergedCellUseFirstCellValue = isMergedCellUseFirstCellValue;
	}

	/**
	 * 创建poi文件解析对象
	 * 
	 * @param inputStream
	 */
	public void read(InputStream inputStream) throws IOException {
		POIFSFileSystem fs = new POIFSFileSystem(inputStream);
		workbook = new HSSFWorkbook(fs);
		numOfSheet = workbook.getNumberOfSheets();
		alreadyInitMergedCellValueSet = new HashSet(numOfSheet);
		try {
			locateSheet(0);
		} catch (NoSuchSheetException e) {
			e.printStackTrace();
		}

	}

	/**
	 * 定位sheet
	 * 
	 * @param index
	 * @throws NoSuchSheetException
	 */
	public void locateSheet(int index) throws NoSuchSheetException {
		if (index >= workbook.getNumberOfSheets()) {
			logger.info("Sheet越界");
			throw new NoSuchSheetException("Sheet index (" + index + ") is out of range (0.."
					+ (workbook.getNumberOfSheets() - 1) + ")");
		}
		sheet = (Sheet) workbook.getSheetAt(index);
		sheetInit();
	}

	/**
	 * 定位到某个名称的sheet
	 * 
	 * @param sheetName
	 * @throws NoSuchSheetException
	 */
	public void locateSheet(String sheetName) throws NoSuchSheetException {
		Sheet sheet = (Sheet) workbook.getSheet(sheetName);
		if (sheet == null)
			throw new NoSuchSheetException("Sheet " + sheetName + "no exist");
		this.sheet = sheet;
		sheetInit();
	}

	protected void sheetInit() {
		this.lastRowNum = sheet.getLastRowNum();
	}

	public String getSheetName() {
		return sheet.getSheetName();
	}

	public int getLastCellNum(int row) {
		Row hssfRow = getRow(sheet, row);
		if (hssfRow != null)
			return hssfRow.getLastCellNum() - 1;
		return 0;
	}

	/**
	 * 可返回单元格的内容
	 * 
	 * @param row
	 * @param col
	 * @return
	 */
	public Object getCellValue(int row, int col) {
		Cell cell = getCell(sheet, row, col);
		return getCellValue(cell);
	}

	private Object getCellValue(Cell cell) {
		if (cell == null)
			return null;
		int cellType = cell.getCellType();
		if (cellType == Cell.CELL_TYPE_BLANK) {
			return cell.getRichStringCellValue().getString();
		}
		if (cellType == Cell.CELL_TYPE_NUMERIC) {
			if (DateUtil.isCellDateFormatted(cell)) {
				return cell.getDateCellValue();
			}
			return new Double((cell.getNumericCellValue()));
		}
		if (cellType == Cell.CELL_TYPE_BOOLEAN) {
			return Boolean.valueOf(cell.getBooleanCellValue());
		}
		if (cellType == Cell.CELL_TYPE_ERROR) {
		}
		if (cellType == Cell.CELL_TYPE_FORMULA) {
			try {
				return new Double(cell.getNumericCellValue());
			} catch (RuntimeException e) {
			}

			try {
				return Boolean.valueOf(cell.getBooleanCellValue());
			} catch (RuntimeException e) {
			}

			try {
				return cell.getStringCellValue();
			} catch (RuntimeException e) {
				System.err.println("Error: " + e.getMessage() + " \n Cell[" + cell.getRow().getRowNum() + ","
						+ cell.getColumnIndex() + "]  Formula:" + cell.getCellFormula());
			}
		}
		if (cellType == Cell.CELL_TYPE_STRING) {
			return cell.getStringCellValue();
		}
		return null;
	}

	/**
	 * 返回Date类型数据
	 * 
	 * @param row
	 * @param col
	 * @param format
	 * @return
	 * @throws java.text.ParseException
	 */
	public Date getCellDateValue(int row, int col, String format) throws java.text.ParseException {
		Object o = getCellValue(row, col);
		if (o == null)
			return null;

		if (java.util.Date.class.isInstance(o)) {
			return (java.util.Date) o;
		}

		String parseStr = o.toString();
		if (parseStr.trim().equals(""))
			return null;

		DateFormat df = new SimpleDateFormat(format);
		return df.parse(o.toString());
	}

	/**
	 * 获取字符型的行列�?如果是日期返回格式为"yyy-MM-dd"
	 * 
	 * @param cell
	 *            列对�?
	 * @param rowNo
	 *            行号
	 * @param cellNo
	 *            列号
	 * @return
	 */
	public String getStringCellValue(int rowNo, int columnNo) {
		return getStringCellValue(sheet, rowNo, columnNo);
	}

	public Date getCellValueByDateType(int rowNo, int columnNo) {
		Cell cell = getCell(sheet, rowNo, columnNo);
		Date date = cell.getDateCellValue();
		return date;
	}

	/**
	 * 获取字符型的行列�?
	 * @param cell
	 *            列对�?
	 * 
	 * @param rowNo
	 *            行号
	 * @param cellNo
	 *            列号
	 * @return
	 */
	protected static String getStringCellValue(Sheet sheet, int rowNo, int columnNo) {
		Cell cell = getCell(sheet, rowNo, columnNo);
		if (cell == null)
			return null;
		int cellType = cell.getCellType();
		if (cellType == Cell.CELL_TYPE_BLANK) {
			return cell.getRichStringCellValue().getString();
		}
		if (cellType == Cell.CELL_TYPE_NUMERIC) {
			if (DateUtil.isCellDateFormatted(cell)) {
				return new SimpleDateFormat("yyyy-MM-dd").format(cell.getDateCellValue());
			}
			double val = cell.getNumericCellValue();
			return String.valueOf(val);
		}
		if (cellType == Cell.CELL_TYPE_BOOLEAN) {
			return String.valueOf(cell.getBooleanCellValue());
		}
		if (cellType == Cell.CELL_TYPE_ERROR) {
		}
		if (cellType == Cell.CELL_TYPE_FORMULA) {
			return cell.getRichStringCellValue().getString();
		}

		return StringUtils.trimToEmpty(cell.getRichStringCellValue().getString());
	}

	/**
	 * 获取数字型的行列�?
	 * 
	 * @param cell
	 *            列对�?
	 * @param rowNo
	 *            行号
	 * @param cellNo
	 *            列号
	 * @throws NumberFormatException
	 *             不是数字时抛出异�?
	 * 
	 * @return
	 */
	public Double getNumericCellValue(int rowNo, int columnNo) {
		return getNumericCellValue(sheet, rowNo, columnNo);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.svw.vps.framework.util.ExcelReader#getIntCellValue(int, int)
	 */
	public Integer getIntCellValue(int rowNo, int columnNo) {
		Double val = getNumericCellValue(rowNo, columnNo);
		if (val == null)
			return null;
		return new Integer(val.intValue());
	}

	private static Row getRow(Sheet sheet, int row) {
		Row hssfRow = (Row) sheet.getRow(row);
		if (hssfRow == null) {
			if (row > sheet.getLastRowNum())
				throw new IndexOutOfBoundsException(" row[" + row + "]");
			else {
				return null;
			}
		}
		return hssfRow;
	}

	private static Cell getCell(Sheet sheet, int row, int column) {
		Row hssfRow = getRow(sheet, row);
		if (hssfRow != null) {
			return (Cell) hssfRow.getCell(column, Row.CREATE_NULL_AS_BLANK);
		}
		return null;
	}

	/**
	 * 获取数字型的行列�?
	 * 
	 * @param cell
	 *            列对�?
	 * 
	 * @param rowNo
	 *            行号
	 * @param cellNo
	 *            列号
	 * @throws NumberFormatException
	 *             不是数字时抛出异�?
	 * 
	 * @return
	 */
	protected static Double getNumericCellValue(Sheet sheet, int rowNo, int columnNo) {

		Cell cell = getCell(sheet, rowNo, columnNo);
		if (cell == null)
			return null;

		int cellType = cell.getCellType();

		if (cellType == Cell.CELL_TYPE_NUMERIC) {
			return new Double(cell.getNumericCellValue());
		}
		if (cellType == Cell.CELL_TYPE_FORMULA) {
			return new Double(cell.getNumericCellValue());
		}

		// 剩余的当作是string处理
		String value = cell.getRichStringCellValue().getString();
		if (value == null || "".equals(value.trim())) {
			return null;
		} else {
			Double val = Double.valueOf(value.trim());
			return val;
		}
	}

	/**
	 * (non-Javadoc)
	 * 
	 * @see com.svw.vps.framework.util.ExcelReader#getLastRowNum()
	 */
	public int getLastRowNum() {
		return lastRowNum;
	}

	/**
	 * (non-Javadoc)
	 * 
	 * @see com.svw.vps.framework.util.ExcelReader#getNumOfSheet()
	 */
	public int getNumOfSheet() {
		return numOfSheet;
	}

	/**
	 * (non-Javadoc)
	 * 
	 * @see com.svw.vps.framework.util.ExcelReader#isMergedCellUseFirstCellValue()
	 */
	public boolean isMergedCellUseFirstCellValue() {
		return isMergedCellUseFirstCellValue;
	}

	/**
	 * (non-Javadoc)
	 * 
	 * @see com.svw.vps.framework.util.ExcelReader#setMergedCellUseFirstCellValue(boolean)
	 */
	public void setMergedCellUseFirstCellValue(boolean isMergedCellUseFirstCellValue) {
		this.isMergedCellUseFirstCellValue = isMergedCellUseFirstCellValue;
	}

	/**
	 * 返回的单元格类型
	 * 
	 * @param rowNo
	 * @param columnNo
	 * @return
	 */
	public int getCellType(int rowNo, int columnNo) {
		Row rows = (Row) sheet.getRow(rowNo); // 读取sheet1中的第i�?

		if (rows != null) {
			Cell cell = (Cell) rows.getCell(columnNo);
			if (cell != null) {
				return cell.getCellType();
			}
		}
		return -1;
	}

	public Workbook getWorkbook() {
		return workbook;
	}

	public void setWorkbook(Workbook workbook) {
		this.workbook = workbook;
	}

	public Sheet getSheet() {
		return sheet;
	}

	public void setSheet(Sheet sheet) {
		this.sheet = sheet;
	}

	public Set getAlreadyInitMergedCellValueSet() {
		return alreadyInitMergedCellValueSet;
	}

	public void setAlreadyInitMergedCellValueSet(Set alreadyInitMergedCellValueSet) {
		this.alreadyInitMergedCellValueSet = alreadyInitMergedCellValueSet;
	}

	public void setNumOfSheet(int numOfSheet) {
		this.numOfSheet = numOfSheet;
	}

	public void setLastRowNum(int lastRowNum) {
		this.lastRowNum = lastRowNum;
	}

	/**
	 * 获取字符型的行列�?
	 * 
	 * @param cell
	 *            列对�?
	 * 
	 * @param rowNo
	 *            行号
	 * @param cellNo
	 *            列号
	 * @return 返回Excel原样的�?
	 * @author zhoujunyang
	 */
	protected static String getOrigianlValue(Sheet sheet, int rowNo, int columnNo) {
		Cell cell = getCell(sheet, rowNo, columnNo);
		if (cell == null)
			return null;
		cell.setCellType(HSSFCell.CELL_TYPE_STRING);

		return StringUtils.trimToEmpty(cell.getRichStringCellValue().getString());
	}

	/**
	 * 获取字符型的行列�?如果是日期返回格式为"yyy-MM-dd"
	 * 
	 * @param cell
	 *            列对�?
	 * @param rowNo
	 *            行号
	 * @param cellNo
	 *            列号
	 * @return
	 */
	public String getOrigianlValue(int rowNo, int columnNo) {
		return getOrigianlValue(sheet, rowNo, columnNo);
	}
}