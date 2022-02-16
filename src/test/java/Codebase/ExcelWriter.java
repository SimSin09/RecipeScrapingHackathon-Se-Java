package Codebase;


import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;

public class ExcelWriter {
    public static void writeToExcel(Recipe recipe) throws IOException, InvalidFormatException {
        try {
            FileInputStream file = new FileInputStream(("Vin.xlsx"));
            XSSFWorkbook workbook = new XSSFWorkbook(file);
           // XSSFSheet sheet = workbook.getSheet(String.valueOf(recipe.getAlphabet()));
            System.out.println(recipe.getAlphabet());
            XSSFSheet sheet = workbook.getSheet(recipe.getAlphabet());
            int noOfCurrentRows = sheet.getPhysicalNumberOfRows();

            System.out.println(recipe.getTitle());
            System.out.println(recipe.getAlphabet());
            Row row = sheet.createRow(noOfCurrentRows+1);
            row.createCell(0).setCellValue(recipe.getTitle());
            row.createCell(1).setCellValue(recipe.getCategory());
            row.createCell(2).setCellValue(recipe.getIngredients());
            row.createCell(3).setCellValue(recipe.getSteps());
            row.createCell(4).setCellValue(recipe.getNutrients());
            row.createCell(5).setCellValue(recipe.getImagelink());
            row.createCell(6).setCellValue(recipe.getUrl());
            //sheet.getRow(1).getCell(0).setCellValue(recipe.getTitle());
            file.close();

            FileOutputStream outFile = new FileOutputStream(new File("Vin.xlsx"));
            workbook.write(outFile);
            outFile.close();
        }
        catch (FileNotFoundException e)
        {
            System.out.println(e.toString());
        }
        catch (IOException e){
            System.out.println(e.toString());
        }
        catch (Exception e) {
           System.out.println(e.toString());
        }
    }
}
