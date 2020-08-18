package sci.changecostcenter.Model;

import JExcel.JExcel;
import Selector.Entity.FiltroString;
import SimpleDotEnv.Env;
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
import sci.changecostcenter.Model.Entity.CostCenterEntry;
import sci.changecostcenter.Model.Entity.Expense;
import sci.changecostcenter.Model.Entity.Swap;
import Entity.ErrorIgnore;

public class ExpenseModel {

    private File file;

    private XSSFWorkbook workbook;
    private XSSFSheet sheet;

    private Map<String, List<Expense>> expenses = new HashMap<>();

    public void setFile(File file) {
        this.file = file;
    }
    
    public void setExpenses(){
        defineWorkbook();
        getExpenseList();
    }

    private void defineWorkbook() {
        try {
            workbook = new XSSFWorkbook(file);
            sheet = workbook.getSheetAt(workbook.getFirstVisibleTab());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getExpenseList() {
        Integer colDre = JExcel.Cell(Env.get("coluna_DRE"));
        Integer colExpenseDescription = JExcel.Cell(Env.get("coluna_Descrição Despesa"));
        Integer colCostCenterName = JExcel.Cell(Env.get("coluna_Nome CC"));
        Integer colNatureCode = JExcel.Cell(Env.get("coluna_Codigo Natureza"));
        Integer colNatureDescription = JExcel.Cell(Env.get("coluna_Descrição Natureza"));
        Integer colProvider = JExcel.Cell(Env.get("coluna_Fornecedor"));
        Integer colProviderName = JExcel.Cell(Env.get("coluna_Nome Fornecedor"));
        Integer colValue = JExcel.Cell(Env.get("coluna_Valor"));
        Integer colDate = JExcel.Cell(Env.get("coluna_Data"));
        Integer colDueDate = JExcel.Cell(Env.get("coluna_Data Pagamento"));
        Integer colTitle = JExcel.Cell(Env.get("coluna_Titulo"));

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

                if (!expenses.containsKey(expense.getTitle())) {
                    expenses.put(expense.getTitle(), new ArrayList<>());
                }
                expenses.get(expense.getTitle()).add(expense);
            } catch (Exception e) {
            }
        }
        
        if(expenses.isEmpty()){
            throw new ErrorIgnore("Nenhuma despesa encontrada no arquivo de despesas.");
        }
    }

    /**
     * Get swap list from expenses of file
     * @return list of swaps of expenses of file
     */
    public List<Swap> getSwapList() {
        List<Swap> swaps = new ArrayList<>();

        /*Percorre despesas das notas fiscais*/
        for (Map.Entry<String, List<Expense>> entry : expenses.entrySet()) {
            String title = entry.getKey();
            List<Expense> titleExpense = entry.getValue();

            //Percorre despesas da nota fiscal
            for (Expense expense : titleExpense) {
                try {
                    //Adiciona no filtro
                    List<String> hasList = new ArrayList<>();
                    hasList.add(expense.getProviderName());
                    hasList.add(expense.getTitle());

                    //Cria troca
                    Swap swap = new Swap();
                    swap.setFilter(new FiltroString());
                    swap.getFilter().setPossui(hasList);

                    //Pega o número do centro de custo
                    Integer costCenter = Integer.valueOf(Env.get("costCenterNumber_" + expense.getCostCenterName()));
                    swap.setCostCenterDebit(costCenter);      
                    
                    //Cria lista de inserts
                    CostCenterEntry costCenterEntry = new CostCenterEntry();
                    costCenterEntry.setCostCenter(expense.getCostCenter());
                    costCenterEntry.setValueType(CostCenterEntry.TYPE_DEBIT);
                    costCenterEntry.setValue(expense.getValue());
                    
                    swap.getEntries().add(costCenterEntry);
                    
                    //Adiciona Swap
                    swaps.add(swap);
                } catch (Exception e) {
                }
            }
        }

        return swaps;
    }

    public Map<String, List<Expense>> getExpenses() {
        return expenses;
    }
}
