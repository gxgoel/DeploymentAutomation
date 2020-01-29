package com.sap.ich;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.math.NumberUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.opencsv.CSVReader;

public class ExcelUtils {

	public boolean setRowExcelData(String DownloadPath, String fileName, String sheetName, int rownumber, String [] data) 
	{
		try 
		{
			//Create an object of File class to open xlsx file
			File file =    new File(DownloadPath+"\\"+fileName);

			//Create an object of FileInputStream class to read excel file
			FileInputStream inputStream;


			inputStream = new FileInputStream(file);


			Workbook workbook = null;

			//Find the file extension by splitting file name in substring  and getting only extension name
			String fileExtensionName = fileName.substring(fileName.indexOf("."));

			//Check condition if the file is xlsx file
			if(fileExtensionName.equals(".xlsx")){
				//If it is xlsx file then create object of XSSFWorkbook class
				workbook = new XSSFWorkbook(inputStream);
			}

			//Check condition if the file is xls file
			else if(fileExtensionName.equals(".xls")){
				//If it is xls file then create object of XSSFWorkbook class
				workbook = new HSSFWorkbook(inputStream);
			}

			//Read sheet inside the workbook by its name
			Sheet sheet = workbook.getSheet(sheetName);

			int rowCount = sheet.getLastRowNum();

			Row row;
			if(rownumber < rowCount) {
				row = sheet.getRow(rownumber);
			}else {
				row = sheet.createRow(++rowCount);
			}

			Cell cell = null;

			for (int j = 0; j < data.length; j++) {
				cell = row.createCell(j);
				cell.setCellValue((String) data[j]);
			}
			inputStream.close(); //Close the InputStream

			FileOutputStream output_file =new FileOutputStream(new File(DownloadPath+"\\"+fileName));  //Open FileOutputStream to write updates

			workbook.write(output_file); //write changes

			output_file.close();  //close the stream 

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		return true;
	}

	public void resetOutputSheet(String DownloadPath, String fileName, String sheetName){
		try
		{
			//Create an object of File class to open xlsx file
			File file =    new File(DownloadPath+"\\"+fileName);

			//Create an object of FileInputStream class to read excel file
			FileInputStream inputStream;


			inputStream = new FileInputStream(file);


			Workbook workbook = null;

			//Find the file extension by splitting file name in substring  and getting only extension name
			String fileExtensionName = fileName.substring(fileName.indexOf("."));

			//Check condition if the file is xlsx file
			if(fileExtensionName.equals(".xlsx")){
				//If it is xlsx file then create object of XSSFWorkbook class
				workbook = new XSSFWorkbook(inputStream);
			}

			//Check condition if the file is xls file
			else if(fileExtensionName.equals(".xls")){
				//If it is xls file then create object of XSSFWorkbook class
				workbook = new HSSFWorkbook(inputStream);
			}

			//Read sheet inside the workbook by its name
			Sheet sheet = workbook.getSheet(sheetName);

			int rowCount = sheet.getLastRowNum();

			Row row;

			for (int j = rowCount; j > 0; j--) {
				row = sheet.getRow(j);
				sheet.removeRow(row);
			}
			inputStream.close(); //Close the InputStream

			FileOutputStream output_file =new FileOutputStream(new File(DownloadPath+"\\"+fileName));  //Open FileOutputStream to write updates

			workbook.write(output_file); //write changes

			output_file.close();  //close the stream

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void addToSheet(String DownloadPath, String fileName, String sheetName, List<List<String>> content){
		try {
			//Create an object of File class to open xlsx file
			File file = new File(DownloadPath + "\\" + fileName);

			//Create an object of FileInputStream class to read excel file
			FileInputStream inputStream;


			inputStream = new FileInputStream(file);


			Workbook workbook = null;

			//Find the file extension by splitting file name in substring  and getting only extension name
			String fileExtensionName = fileName.substring(fileName.indexOf("."));

			//Check condition if the file is xlsx file
			if (fileExtensionName.equals(".xlsx")) {
				//If it is xlsx file then create object of XSSFWorkbook class
				workbook = new XSSFWorkbook(inputStream);
			}

			//Check condition if the file is xls file
			else if (fileExtensionName.equals(".xls")) {
				//If it is xls file then create object of XSSFWorkbook class
				workbook = new HSSFWorkbook(inputStream);
			}

			//Read sheet inside the workbook by its name
			Sheet sheet;
			sheet = workbook.getSheet(sheetName);
			if(sheet == null) {
				sheet = workbook.createSheet(sheetName);
			}
			int i=0;
			for(List<String> aRow:content) {
				Row row = sheet.createRow(i);
				int j = 0;
				for(String cellData: aRow){
					Cell cell = null;
					System.out.println(cellData);

						cell = row.createCell(j);
						cell.setCellValue(cellData);
					j = j + 1;
				}
				i = i + 1;
			}
			inputStream.close(); //Close the InputStream

			FileOutputStream output_file =new FileOutputStream(new File(DownloadPath+"\\"+fileName));  //Open FileOutputStream to write updates

			workbook.write(output_file); //write changes

			output_file.close();  //close the stream
		}catch(Exception e){

			System.out.println(e.getStackTrace().toString());
		}

	}
	public List<List<String>> getExcelData(String DownloadPath, String fileName, String sheetName) 
	{

		List<List<String>> allRowsData = new ArrayList() ;

		try {
			//Create an object of File class to open xlsx file
			File file =    new File(DownloadPath+"\\"+fileName);

			//Create an object of FileInputStream class to read excel file
			FileInputStream inputStream;

			inputStream = new FileInputStream(file);

			Workbook workbook = null;

			//Find the file extension by splitting file name in substring  and getting only extension name
			String fileExtensionName = fileName.substring(fileName.indexOf("."));

			//Check condition if the file is xlsx file
			if(fileExtensionName.equals(".xlsx")){
				//If it is xlsx file then create object of XSSFWorkbook class
				workbook = new XSSFWorkbook(inputStream);
			}

			//Check condition if the file is xls file
			else if(fileExtensionName.equals(".xls")){
				//If it is xls file then create object of XSSFWorkbook class
				workbook = new HSSFWorkbook(inputStream);
			}



			Sheet sheet = workbook.getSheetAt(0);

			Iterator<Row> iterator = sheet.iterator();

			while (iterator.hasNext()) {
				Row nextRow = iterator.next();
				Iterator<Cell> cellIterator = nextRow.cellIterator();
				List<String> rowData = new ArrayList<String>();
				while (cellIterator.hasNext()) {
					Cell cell = cellIterator.next();

					switch (cell.getCellType()) {

					case Cell.CELL_TYPE_STRING:
						rowData.add(cell.getStringCellValue());
						break;

					case Cell.CELL_TYPE_BOOLEAN:
						//	                        System.out.print(cell.getBooleanCellValue());
						break;
					case Cell.CELL_TYPE_NUMERIC:
						rowData.add(""+cell.getNumericCellValue());
						break;
					}

				}
				allRowsData.add(rowData);
			}

			//	      //Read sheet inside the workbook by its name
			//		    Sheet sheet = workbook.getSheet(sheetName);

			//		    //Find number of rows in excel file
			//		    int rowCount = sheet.getLastRowNum()-sheet.getFirstRowNum();
			//
			//		    //Create a loop over all the rows of excel file to read it
			//
			//		    for (int i = 0; i < rowCount+1; i++) {
			//		        Row row = sheet.getRow(i);
			//
			//		        //Create a loop to print cell values in a row
			//		        for (int j = 0; j < row.getLastCellNum(); j++) {
			//		            //Print Excel data in console
			//		        	rowData.add(row.getCell(j).getStringCellValue());
			//		        }
			//
			//		        allRowsData.add(rowData);
			//		    } 
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return allRowsData;
	}

	public boolean ReplaceExcelValue(String DownloadPath, String fileName, String sheetName,String sOldData, String sNewData) 
	{
		try 
		{	
			//Create an object of File class to open xlsx file
			File file =    new File(DownloadPath+"\\"+fileName);

			//Create an object of FileInputStream class to read excel file
			FileInputStream inputStream;


			inputStream = new FileInputStream(file);


			Workbook workbook = null;

			//Find the file extension by splitting file name in substring  and getting only extension name
			String fileExtensionName = fileName.substring(fileName.indexOf("."));

			//Check condition if the file is xlsx file
			if(fileExtensionName.equals(".xlsx")){
				//If it is xlsx file then create object of XSSFWorkbook class
				workbook = new XSSFWorkbook(inputStream);
			}

			//Check condition if the file is xls file
			else if(fileExtensionName.equals(".xls")){
				//If it is xls file then create object of XSSFWorkbook class
				workbook = new HSSFWorkbook(inputStream);
			}

			//Read sheet inside the workbook by its name
			//		    Sheet sheet = workbook.getSheet(sheetName);
			Sheet sheet = workbook.getSheetAt(0);

			Iterator<Row> iterator = sheet.iterator();

			while (iterator.hasNext()) {
				Row nextRow = iterator.next();
				Iterator<Cell> cellIterator = nextRow.cellIterator();

				while (cellIterator.hasNext()) {
					Cell cell = cellIterator.next();

					switch (cell.getCellType()) {

					case Cell.CELL_TYPE_STRING:
						if(cell.getStringCellValue().equals(sOldData))
						{
							cell.setCellValue((String) sNewData);
						}
						break;

					case Cell.CELL_TYPE_BOOLEAN:
						//	                        System.out.print(cell.getBooleanCellValue());
						break;
					case Cell.CELL_TYPE_NUMERIC:
						if(cell.getNumericCellValue() == (Integer.parseInt(sOldData)))
						{
							cell.setCellValue((Integer) (Integer.parseInt(sNewData)));
						}
						break;
					}

				}

			}

			inputStream.close(); //Close the InputStream

			FileOutputStream output_file =new FileOutputStream(new File(DownloadPath+"\\"+fileName));  //Open FileOutputStream to write updates

			workbook.write(output_file); //write changes

			output_file.close();  //close the stream 

			return true;

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}

	}


	public boolean convertCsvToXls( String csvFilePath, String xlsFileLocation, String FILE_NAME, String FILE_EXTN )
	{
		final char FILE_DELIMITER = ',';
		SXSSFSheet sheet = null;
		CSVReader reader = null;
		Workbook workBook = null;
		String generatedXlsFilePath = "";
		FileOutputStream fileOutputStream = null;

		try {

			/**** Get the CSVReader Instance & Specify The Delimiter To Be Used ****/
			String[] nextLine;
			reader = new CSVReader(new FileReader(csvFilePath), FILE_DELIMITER);

			workBook = new SXSSFWorkbook();
			sheet = (SXSSFSheet) workBook.createSheet("Sheet");

			int rowNum = 0;
			while((nextLine = reader.readNext()) != null) {
				Row currentRow = sheet.createRow(rowNum++);
				for(int i=0; i < nextLine.length; i++) {
					if(NumberUtils.isDigits(nextLine[i])) {
						currentRow.createCell(i).setCellValue(Integer.parseInt(nextLine[i]));
					} else if (NumberUtils.isNumber(nextLine[i])) {
						currentRow.createCell(i).setCellValue(Double.parseDouble(nextLine[i]));
					} else {
						currentRow.createCell(i).setCellValue(nextLine[i]);
					}
				}
			}

			generatedXlsFilePath = xlsFileLocation +"\\"+ FILE_NAME +"."+ FILE_EXTN;

			fileOutputStream = new FileOutputStream(generatedXlsFilePath.trim());
			workBook.write(fileOutputStream);

		} catch(Exception exObj) {
			exObj.printStackTrace();
			return false;
		} finally {         
			try {

				/**** Closing The Excel Workbook Object ****/
				workBook.close();

				/**** Closing The File-Writer Object ****/
				fileOutputStream.close();

				/**** Closing The CSV File-ReaderObject ****/
				reader.close();

			} catch (IOException ioExObj) {
				ioExObj.printStackTrace();
				return false;
			}
		}
		return true;
	}


}
