package com.cib.util.poi;

import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;

public class ExcelCellStyle {

	private static short getNumberDataFormat(ExcelWriterUtil writer,String format){
		return writer.createDataFormat(format);
	}
	/**
	 * double格式
	 * @param writer
	 * @return
	 */
	public static HSSFCellStyle getDoubleCellStyle(ExcelWriterUtil writer,String format) {
		short doubleFormat = getNumberDataFormat(writer, format);
		
		HSSFCellStyle doubleCellStyle = writer.newCellStyle();
		doubleCellStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);//垂直对齐
		doubleCellStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);//水平对齐
		doubleCellStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN);
		doubleCellStyle.setBottomBorderColor(HSSFColor.BLACK.index);
		doubleCellStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);
		doubleCellStyle.setLeftBorderColor(HSSFColor.BLACK.index);
		doubleCellStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);
		doubleCellStyle.setRightBorderColor(HSSFColor.BLACK.index);
		doubleCellStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);
		doubleCellStyle.setTopBorderColor(HSSFColor.BLACK.index);
		doubleCellStyle.setDataFormat(doubleFormat);//保留4位小数
		return doubleCellStyle;
	}
	
	public static HSSFCellStyle getIntegerCellStyle(ExcelWriterUtil writer) {
		short integerFormat = getNumberDataFormat(writer, "0");
		HSSFCellStyle integerCellStyle = writer.newCellStyle();
		
		integerCellStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);//垂直对齐
		integerCellStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);//水平对齐
		integerCellStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN);
		integerCellStyle.setBottomBorderColor(HSSFColor.BLACK.index);
		integerCellStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);
		integerCellStyle.setLeftBorderColor(HSSFColor.BLACK.index);
		integerCellStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);
		integerCellStyle.setRightBorderColor(HSSFColor.BLACK.index);
		integerCellStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);
		integerCellStyle.setTopBorderColor(HSSFColor.BLACK.index);
		integerCellStyle.setDataFormat(integerFormat);//Integer
		return integerCellStyle;
	}
	
	/**
	 * 标准表格样式
	 * @param writer
	 * @return
	 */
	public static HSSFCellStyle getCellStyle(ExcelWriterUtil writer) {
		HSSFCellStyle contentStyle = writer.newCellStyle();
		contentStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);//垂直对齐
		contentStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);//水平对齐
		contentStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN);
		contentStyle.setBottomBorderColor(HSSFColor.BLACK.index);
		contentStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);
		contentStyle.setLeftBorderColor(HSSFColor.BLACK.index);
		contentStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);
		contentStyle.setRightBorderColor(HSSFColor.BLACK.index);
		contentStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);
		contentStyle.setTopBorderColor(HSSFColor.BLACK.index);
		contentStyle.setWrapText(true);
		return contentStyle;
	}
	public static HSSFCellStyle getCellLeftStyle(ExcelWriterUtil writer) {
		HSSFCellStyle contentStyle = writer.newCellStyle();
		contentStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);//垂直对齐
		contentStyle.setAlignment(HSSFCellStyle.ALIGN_LEFT);//向左对齐
		contentStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN);
		contentStyle.setBottomBorderColor(HSSFColor.BLACK.index);
		contentStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);
		contentStyle.setLeftBorderColor(HSSFColor.BLACK.index);
		contentStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);
		contentStyle.setRightBorderColor(HSSFColor.BLACK.index);
		contentStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);
		contentStyle.setTopBorderColor(HSSFColor.BLACK.index);
		contentStyle.setWrapText(true);
		return contentStyle;
	}
	
	/**
	 * 2007标准表格样式
	 * @param writer
	 * @return
	 */
	public static XSSFCellStyle get2007DefaultStyle(Excel2007WriterUtil writer) {
		XSSFCellStyle contentStyle = writer.newCellStyle();
		contentStyle.setVerticalAlignment(XSSFCellStyle.VERTICAL_CENTER);//垂直对齐
		contentStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);//水平对齐
		contentStyle.setBorderBottom(XSSFCellStyle.BORDER_THIN);
		contentStyle.setBottomBorderColor(HSSFColor.BLACK.index);
		contentStyle.setBorderLeft(XSSFCellStyle.BORDER_THIN);
		contentStyle.setLeftBorderColor(HSSFColor.BLACK.index);
		contentStyle.setBorderRight(XSSFCellStyle.BORDER_THIN);
		contentStyle.setRightBorderColor(HSSFColor.BLACK.index);
		contentStyle.setBorderTop(XSSFCellStyle.BORDER_THIN);
		contentStyle.setTopBorderColor(HSSFColor.BLACK.index);
		contentStyle.setWrapText(true);//自动换行
		return contentStyle;
	}
}
