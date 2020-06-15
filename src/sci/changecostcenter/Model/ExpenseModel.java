package sci.changecostcenter.Model;

import JExcel.JExcel;
import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import sci.changecostcenter.Model.Entity.Expense;

public class ExpenseModel {

    private File file;

    private XSSFWorkbook workbook;
    private XSSFSheet sheet;

    private Map<String, List<Expense>> expenses = new HashMap<>();

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

        for (int i = 0; i < sheet.getLastRowNum(); i++) {
            try {
                XSSFRow row = sheet.getRow(i);

                Expense expense = new Expense();
                expense.setDre(row.getCell(colDre).getStringCellValue());
                expense.setExpenseDescription(row.getCell(colExpenseDescription).getStringCellValue());
                expense.setCostCenterName(row.getCell(colCostCenterName).getStringCellValue());
                expense.setNatureCode(row.getCell(colNatureCode).getStringCellValue());
                expense.setNatureDescription(row.getCell(colNatureDescription).getStringCellValue());
                expense.setProvider(row.getCell(colProvider).getStringCellValue());
                expense.setProviderName(row.getCell(colProviderName).getStringCellValue());
                expense.setValue(new BigDecimal(Double.toString(row.getCell(colValue).getNumericCellValue())));
                
                Calendar date = Calendar.getInstance();
                date.setTime(row.getCell(colDate).getDateCellValue());
                expense.setDate(date);
                
                Calendar dueDate = Calendar.getInstance();
                dueDate.setTime(row.getCell(colDueDate).getDateCellValue());
                expense.setDueDate(dueDate);
                
                expense.setTitle(row.getCell(colTitle).getStringCellValue());
                
                if(!expenses.containsKey(expense.getTitle())){
                    expenses.put(expense.getTitle(), new ArrayList<>());                    
                }
                expenses.get(expense.getTitle()).add(expense);
            } catch (Exception e) {
            }
        }
    }

    public Map<String, List<Expense>> getExpenses() {
        return expenses;
    }   
}
