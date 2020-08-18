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
import Entity.Warning;
import org.apache.poi.xssf.usermodel.XSSFCell;

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
        
        //Contagem de erros
        Integer errors = 0;

        for (int i = 0; i < sheet.getLastRowNum(); i++) {
            try {
                XSSFRow row = sheet.getRow(i);

                Expense expense = new Expense();
                expense.setDre(row.getCell(colDre).getStringCellValue());
                expense.setExpenseDescription(row.getCell(colExpenseDescription).getStringCellValue());
                expense.setCostCenterName(row.getCell(colCostCenterName).getStringCellValue());
                expense.setNatureCode(row.getCell(colNatureCode) == null?"": row.getCell(colNatureCode).toString());
                expense.setNatureDescription(row.getCell(colNatureDescription).getStringCellValue());
                expense.setProvider(row.getCell(colProvider).toString());
                expense.setProviderName(row.getCell(colProviderName).getStringCellValue());
                expense.setValue(new BigDecimal(Double.toString(row.getCell(colValue).getNumericCellValue())));

                //Data
                Calendar date = Calendar.getInstance();
                date.setTime(row.getCell(colDate).getDateCellValue());
                expense.setDate(date);

                //Data de pagamento
                Calendar dueDate = Calendar.getInstance();
                dueDate.setTime(row.getCell(colDueDate).getDateCellValue());
                expense.setDueDate(dueDate);

                //
                XSSFCell titleCell = row.getCell(colTitle);
                expense.setTitle(titleCell == null?"":titleCell.toString());

                if (!expenses.containsKey(expense.getTitle())) {
                    expenses.put(expense.getTitle(), new ArrayList<>());
                }
                expenses.get(expense.getTitle()).add(expense);
            } catch (Exception e) {
                errors++;
                e.printStackTrace();
            }
        }
        
        if(errors > 0){
            throw new Warning("Existem " + errors + " linhas com erros no arquivo de despesa que foram ignoradas de " + sheet.getLastRowNum()  + " linhas encontradas." );
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
            String title = entry.getKey(); //Nota fiscal
            List<Expense> titleExpense = entry.getValue();

            //Percorre despesas da nota fiscal
            for (Expense expense : titleExpense) {
                try {
                    //Adiciona no filtro
                    List<String> hasList = new ArrayList<>();
                    hasList.add(expense.getProviderName()); //Deve possuir o nome do fornecedor
                    hasList.add(expense.getTitle()); //deve possuir o título (NF)

                    //Cria troca
                    Swap swap = new Swap(); //Instancia troca
                    swap.setFilter(new FiltroString()); //Instancia filtro
                    swap.getFilter().setPossui(hasList); //Define o que o filtro deve possuir

                    //Pega o número do centro de custo no ENV pelo nome
                    String costCenterEnv = Env.get("costCenterNumber_" + expense.getCostCenterName());
                    if(costCenterEnv == null){
                        throw new Error("Centro de custo '" + expense.getCostCenterName() + "' não encontrado no arquivo .ENV");
                    }                    
                    expense.setCostCenter(Integer.valueOf(costCenterEnv));
                    //Define o Centro de custo de débito da troca como o centro de custo
                    //O CC de credito irá ficar nulo
                    swap.setCostCenterDebit(expense.getCostCenter());
                    
                    //Cria lista de entradas no banco
                    CostCenterEntry costCenterEntry = new CostCenterEntry(); //Instancia Entrada de Centro de Custo
                    costCenterEntry.setCostCenter(expense.getCostCenter()); //Define o centro de custo 
                    costCenterEntry.setValueType(CostCenterEntry.TYPE_DEBIT); // define o tipo do valor como debito
                    costCenterEntry.setValue(expense.getValue()); //define o valor
                    
                    swap.getEntries().add(costCenterEntry); //Adiciona nas entradas da troca
                    
                    //Adiciona Swap
                    swaps.add(swap);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        return swaps;
    }

    public Map<String, List<Expense>> getExpenses() {
        return expenses;
    }
}
