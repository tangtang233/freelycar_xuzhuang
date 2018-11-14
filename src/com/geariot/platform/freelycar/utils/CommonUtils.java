package com.geariot.platform.freelycar.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.poifs.filesystem.NPOIFSFileSystem;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.springframework.web.multipart.MultipartFile;

import com.geariot.platform.freelycar.exception.ExcelAnalyzeException;
import com.geariot.platform.freelycar.model.RESCODE;

public class CommonUtils {
	
	public static final Random RANDOM;
	static{
		RANDOM = new Random();
	}
	
	
	public static String generateUUID() {
		
		return UUID.randomUUID().toString().replace("-", "");
	}

	
	/**
     * 检测字符串是否不为空(null,"","null")
     * 
     * @param s
     * @return 不为空则返回true，否则返回false
     */
    public static boolean isNotEmpty(String s) {
        return s != null && !"".equals(s) && !"null".equals(s);
    }

    /**
     * 检测字符串是否为空(null,"","null")
     * 
     * @param s
     * @return 为空则返回true，不否则返回false
     */
    public static boolean isEmpty(String s) {
        return s == null || "".equals(s) || "null".equals(s);
    }

	
	/**
	 * Spring上传文件
	 * 
	 */

   // static is optional for an enum it is always static. 
   public enum FileSuffix {
	   xls,xlsx
   }
    
	//上传文件 ---
	//String baseUrl = context.getRealPath("");
	public static Map<String,Object> uploadFile(MultipartFile mf, String path, EnumSet<FileSuffix> suffixs) {
		// 获取上传路径baseUrl
		if (null != mf) {
			String fileName = mf.getOriginalFilename();
			// 获取后缀
			String suffix = fileName.substring(fileName.lastIndexOf(".")+1,
					fileName.length());
			//是否文件复合上传文件的后缀格式
		    boolean contains = false;
		    Iterator<FileSuffix> it = suffixs.iterator();  
	        while(it.hasNext()){
	        	if(it.next().name().equals(suffix)){
	        		contains = true;
	        		break;
	        	}
	        }
	        if(!contains){
	        	return RESCODE.FILE_TPYE_ERROR.getJSONRES();
	        }
	        
	        //不存在就创建目录
	        Path ph = Paths.get(path);
			if(Files.notExists(ph)){
				try {
					Files.createDirectories(ph);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			//新的文件名
			//0-999随机数 + 原来文件名
			String fileNewName = path+File.separator+RANDOM.nextInt(1000)+fileName;
			try {
				mf.transferTo(new File(fileNewName));
				return RESCODE.SUCCESS.getJSONRES(fileNewName);
			} catch (IllegalStateException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return RESCODE.WRONG_PARAM.getJSONRES();
  	}
    
    
	/**
	 * 
	 * @param filepath
	 * @return
	 * @throws ExcelAnalyzeException
	 * @throws FileNotFoundException
	 */
	
	//默认从0行开始
	private static final int DEFAULT_FROM_ROW = 0;
	//默认读取sheet 0
	private static final int DEFAULT_SHEETAT = 0;
	
	public static List<List<String>> analyzeExcel(String filepath) throws ExcelAnalyzeException, FileNotFoundException{
		return analyzeExcel(filepath, DEFAULT_FROM_ROW,DEFAULT_SHEETAT);
	}
	
	public static List<List<String>> analyzeExcel(String filepath, int fromRow) throws ExcelAnalyzeException, FileNotFoundException{
		return analyzeExcel(filepath, fromRow, DEFAULT_SHEETAT);
	}
	
	public static List<List<String>> analyzeExcel(int sheetAt, String filepath) throws ExcelAnalyzeException, FileNotFoundException{
		return analyzeExcel(filepath, DEFAULT_FROM_ROW, sheetAt);
	}
	
    public static List<List<String>> analyzeExcel(String filepath,int fromRow,int sheetAt) throws ExcelAnalyzeException, FileNotFoundException{
    	List<List<String>> result = null;
    	Workbook workbook = null;
    	NPOIFSFileSystem fs = null;
    	OPCPackage pkg = null;
    	
    	Iterator<Row> rowIterator = null;
    	
		try {
			if (filepath.endsWith(".xls")) {
				fs = new NPOIFSFileSystem(new File(filepath));
				//workbook = new HSSFWorkbook(fs.getRoot(),true);
				workbook = WorkbookFactory.create(fs);
				HSSFSheet sheet = (HSSFSheet) workbook.getSheetAt(sheetAt);
				rowIterator = sheet.iterator();
			} else if (filepath.endsWith(".xlsx")) {
				pkg = OPCPackage.open(new File(filepath));
				//workbook = new XSSFWorkbook(pkg);
				workbook = WorkbookFactory.create(pkg);
				XSSFSheet sheet = (XSSFSheet) workbook.getSheetAt(sheetAt);
				rowIterator = sheet.iterator();
			} else {
				return null;
			}

			result = new ArrayList<List<String>>();
			List<String> rowData = null;
			int row_index = 1;
			while (rowIterator.hasNext()) {
				Row row = rowIterator.next();
				if(row_index++ < fromRow){
					continue;//过滤第一行
				}
				//https://www.hellojava.com/article/1649
				int physicalNumberOfCells = row.getPhysicalNumberOfCells();
				int lastCellNum = row.getLastCellNum();
				
				if(lastCellNum!=physicalNumberOfCells){
					throw new ExcelAnalyzeException(row_index+"");
				}
				

				Iterator<Cell> cellIterator = row.cellIterator();
				rowData = new ArrayList<String>();
				while (cellIterator.hasNext()) {
					Cell cell = cellIterator.next();
					// String data = formatter.formatCellValue(cell); //Returns
					// the formatted value of a cell as a String regardless of
					// the cell type.
					if (cell.getCellType() == Cell.CELL_TYPE_STRING) {
						rowData.add(cell.getStringCellValue());
					} else {
						double value = cell.getNumericCellValue();
						rowData.add(String.valueOf(value));
					}

				}
				result.add(rowData);
			}
		} catch (FileNotFoundException e) {
			throw new FileNotFoundException();
		} catch (InvalidFormatException e) {
			throw new ExcelAnalyzeException();
		} catch(IOException e){
			e.printStackTrace();
		}finally {
			try {
				if(fs != null){
					fs.close();
				}
				
				if(pkg != null){
					pkg.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		return result;
	}

}
