package com.sys.admin.common.utils;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 * excel工具(导入、导出)
 * @author duanjintang
 */
public class ExcelUtil {
	/**
	 * excel导入
	 * @param filename
	 * @return
	 */
	public static List<String[]> readexcel(InputStream inputStream,String filename) throws Exception{
		
		List<String[]> exceldata = new ArrayList<String[]>();
		//获取工作薄
		Workbook wb=null;
		//获取工作表
		Sheet 	 sheet=null; 
		try {
			BufferedInputStream input = new BufferedInputStream(inputStream);
			
			if(filename.endsWith(".xls"))
				wb=new HSSFWorkbook(input); //生成excel2003以前的版本  
			else if(filename.endsWith(".xlsx"))
				wb=new XSSFWorkbook(input); //生成excel2007版本的
			
			//System.out.println(wb);
			
			//获取第一个工作表
			sheet=wb.getSheetAt(0);
			//循环每一行
			for(Iterator it=sheet.iterator();it.hasNext();){
				Row row=(Row)it.next();//获得每一个行
				//System.out.println("第"+row.getRowNum()+"行");
				//System.out.println("共"+row.getFirstCellNum()+"-"+row.getLastCellNum()+"列");
				int col_num = row.getLastCellNum();
				if(col_num<0){
					col_num = 0;
				}
				String[] values = new String[col_num];
				for(int col=row.getFirstCellNum();col<row.getLastCellNum();col++){
					//Iterator i=row.cellIterator();i.hasNext();(Cell)i.next()
					//System.out.println("正在读取第"+col+"列");
					String value = "";
					Cell cell= row.getCell(col);
					if(cell!=null){
						switch (cell.getCellType()) {
							case Cell.CELL_TYPE_STRING:
								value = cell.getStringCellValue();
								break;
							case Cell.CELL_TYPE_NUMERIC:
								if(HSSFDateUtil.isCellDateFormatted(cell)){
									Date date = cell.getDateCellValue();
									if (date != null) {
										value = new SimpleDateFormat("yyyy-MM-dd").format(date);
									} else {
										value = "";
									}
								}else{
									value = cell.getNumericCellValue()+"";
								}
								
								break;
							default:
								break;
						}
						values[col] = value;
					}
				 }
				exceldata.add(values);
				inputStream.close();
			}
		}catch (Exception e) {
			// TODO: handle exception
			throw new Exception(e);
		}
		return exceldata;
	}
}
