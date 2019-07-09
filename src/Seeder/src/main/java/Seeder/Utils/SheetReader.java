package Seeder.Utils;

import java.io.*;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;

import Seeder.Pair;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class SheetReader {

    public static List<Pair> parseFileToList(String filePath) throws IOException {
        List<Pair> listedFile = new ArrayList<>();

        File excelFile = new File(filePath);
        FileInputStream fis;

        // Throws FileNotFoundException if excelFile not found.
        fis = new FileInputStream(excelFile);

        // Create an XSSF Workbook object for our .xlsx Excel File
        XSSFWorkbook workbook;
        workbook = new XSSFWorkbook(fis);

        // Get first sheet
        XSSFSheet sheet = workbook.getSheetAt(0);

        // Iterate on rows

        for (Row row : sheet) {
            // iterate on cells for the current row
            Iterator<Cell> cellIterator = row.cellIterator();

            Cell cell = cellIterator.next();
            var question = cell.toString();

            cell = cellIterator.next();
            var answer = cell.toString();

            listedFile.add(new Pair(question, answer));
        }
        return listedFile;
    }
}