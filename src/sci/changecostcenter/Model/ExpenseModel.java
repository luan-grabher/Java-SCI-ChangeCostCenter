package sci.changecostcenter.Model;

import JExcel.JExcel;
import java.io.File;
import java.math.BigDecimal;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import sci.changecostcenter.Model.Entity.Expense;

public class ExpenseModel {

    private File file;

    private XSSFWorkbook workbook;
    private XSSFSheet sheet;

    private Map<String, Expense> expenses = new HashMap<>();

    public void setFile(File file) {
        this.file = file;
    }

    private void defineWorkbook() {
        try {
            workbook = new XSSFWorkbook(file);
            sheet = workbook.getSheetAt(1);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getExpenseList() {
        Integer colDre = JExcel.Cell("A");
        Integer colExpenseDescription = JExcel.Cell("B");
        Integer colCostCenterName = JExcel.Cell("C");
        Integer colNatureCode = JExcel.Cell("D");
        Integer colNatureDescription = JExcel.Cell("E");
        Integer colProvider = JExcel.Cell("F");
        Integer colProviderName = JExcel.Cell("G");
        Integer colValue = JExcel.Cell("H");
        Integer colDate = JExcel.Cell("I");
        Integer colDueDate = JExcel.Cell("J");
        Integer colTitle = JExcel.Cell("L");

        for (int i = 0;
                i < sheet.getLastRowNum();
                i++) {
            XSSFRow XSSFRow = sheet.getRow(i);
        }
    }

}
