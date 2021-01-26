package sci.changecostcenter.Model;

import JExcel.JExcel;
import fileManager.StringFilter;
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
import sci.changecostcenter.Model.Entity.Swap;
import Entity.ErrorIgnore;
import Entity.Warning;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.ini4j.Profile.Section;
import static sci.changecostcenter.SCIChangeCostCenter.ini;
import static sci.changecostcenter.SCIChangeCostCenter.log;

public class ExpenseModel {

    /**
     * Pega Lista de Trocas do arquivo de despesas e converte em trocas
     *
     * @param file Arquivo de despesas
     * @return list of swaps of expenses of file
     */
    public static List<Swap> getSwaps(File file) {
        XSSFSheet sheet = defineWorkbook(file);
        Map<String, List<Expense>> expenses = getExpenseList(sheet);
        return convertExpenseListToSwaps(expenses);
    }

    /**
     * @param file Arquivo de despesas
     */
    private static XSSFSheet defineWorkbook(File file) {
        try {
            XSSFWorkbook workbook = new XSSFWorkbook(file);
            return workbook.getSheetAt(workbook.getFirstVisibleTab());
        } catch (Exception e) {
            throw new Error(e);
        }
    }

    private static Map<String, List<Expense>> getExpenseList(XSSFSheet sheet) {
        Map<String, List<Expense>> expenses = new HashMap<>();

        Section expenseSection = (Section) ini.get("Expense cols");

        StringFilter filterProviders = new StringFilter(ini.get("Expense", "filtroFornecedores"));

        Integer colDre = JExcel.Cell((String) expenseSection.get("DRE"));
        Integer colExpenseDescription = JExcel.Cell((String) expenseSection.get("Descrição Despesa"));
        Integer colCostCenterName = JExcel.Cell((String) expenseSection.get("Nome CC"));
        Integer colNatureCode = JExcel.Cell((String) expenseSection.get("Codigo Natureza"));
        Integer colNatureDescription = JExcel.Cell((String) expenseSection.get("Descrição Natureza"));
        Integer colProvider = JExcel.Cell((String) expenseSection.get("Fornecedor"));
        Integer colProviderName = JExcel.Cell((String) expenseSection.get("Nome Fornecedor"));

        Integer colValue = JExcel.Cell((String) expenseSection.get("Valor"));
        Integer colDate = JExcel.Cell((String) expenseSection.get("Data"));
        Integer colDueDate = JExcel.Cell((String) expenseSection.get("Data Pagamento"));
        Integer colTitle = JExcel.Cell((String) expenseSection.get("Titulo"));

        //Contagem de erros
        Integer errors = 0;

        //Percorre todas linhas
        for (int i = 0; i < sheet.getLastRowNum(); i++) {
            try {
                XSSFRow row = sheet.getRow(i);

                Expense expense = new Expense();
                expense.setDre(row.getCell(colDre).getStringCellValue());
                expense.setExpenseDescription(row.getCell(colExpenseDescription).getStringCellValue());
                expense.setCostCenterName(row.getCell(colCostCenterName).getStringCellValue());
                expense.setNatureCode(JExcel.getCellString(row.getCell(colNatureCode)));
                expense.setNatureDescription(row.getCell(colNatureDescription).getStringCellValue());
                expense.setProvider(JExcel.getCellString(row.getCell(colProvider)));
                expense.setProviderName(row.getCell(colProviderName).getStringCellValue());

                //Verifica se o fornecedor esta no filtro de fornecedores
                if (filterProviders.filterOfString(expense.getProviderName())) {

                    //Define o valor da despesa pela coluna de valor
                    expense.setValue(new BigDecimal(Double.toString(row.getCell(colValue).getNumericCellValue())));

                    //Se valor encontrado for maior que zero, segue o codigo
                    if (expense.getValue().compareTo(BigDecimal.ZERO) == 1) {

                        //Data
                        Calendar date = Calendar.getInstance();
                        date.setTime(row.getCell(colDate).getDateCellValue());
                        expense.setDate(date);

                        //Data de pagamento
                        Calendar dueDate = Calendar.getInstance();
                        dueDate.setTime(row.getCell(colDueDate).getDateCellValue());
                        expense.setDueDate(dueDate);

                        //Titulo
                        XSSFCell titleCell = row.getCell(colTitle);
                        if (titleCell != null) {
                            //Pega String da celula
                            String titleCellString = JExcel.getCellString(titleCell);
                            expense.setTitle(titleCellString);

                            //Verifica se aquela chave já tem lista, se nao tiver cria a lista
                            if (!expenses.containsKey(expense.getTitle())) {
                                expenses.put(expense.getTitle(), new ArrayList<>());
                            }

                            //Adiciona a despesa na lista de trocas da nf para aquela nota
                            expenses.get(expense.getTitle()).add(expense);
                        } else {
                            log.append("\nLinha ").append(i + 1).append(" com NF não encontrada!");
                            throw new Exception("Titulo(NF) inválido.");
                        }
                    } else {
                        log.append("\nLinha ").append(i + 1).append(" com valor menor ou igual a 0.");
                        throw new Exception("Valor inválido.");
                    }
                }
            } catch (Exception e) {
                errors++;
                e.printStackTrace();
            }
        }

        if (errors > 0) {
            throw new Warning("Existem " + errors + " linhas com erros no arquivo de despesa que foram ignoradas de " + sheet.getLastRowNum() + " linhas encontradas.");
        } else if (expenses.isEmpty()) {
            throw new ErrorIgnore("Nenhuma despesa encontrada no arquivo de despesas.");
        } else {
            return expenses;
        }
    }

    private static List<Swap> convertExpenseListToSwaps(Map<String, List<Expense>> expenses) {

        List<Swap> swaps = new ArrayList<>();

        Section ccSection = (Section) ini.get("CostCenter");

        /*Percorre despesas das notas fiscais*/
        expenses.forEach((doc, docExpenses) -> {
            docExpenses.forEach((docExpense) -> {
                //Adiciona no filtro
                Map<String, String> hasList = new HashMap<>();
                hasList.put(docExpense.getProviderName(), docExpense.getProviderName()); //Deve possuir o nome do fornecedor
                hasList.put(docExpense.getTitle(), docExpense.getTitle()); //deve possuir o título (NF)                                        

                //Cria troca
                Swap swap = new Swap(); //Instancia troca
                swap.setEnterprise(Integer.parseInt(ini.get("Config", "enterprise")));
                swap.setComplementFilter(new StringFilter()); //Instancia filtro
                swap.getComplementFilter().setHas(hasList); //Define o que o filtro deve possuir

                //Para o filtro extra
                swap.setValueFilter(docExpense.getValue());
                swap.setDocument(docExpense.getTitle());

                //Pega o número do centro de custo no ENV pelo nome
                String costCenterEnv = (String) ccSection.get(docExpense.getCostCenterName());
                if (costCenterEnv == null) {
                    throw new Error("Centro de custo '" + docExpense.getCostCenterName() + "' não encontrado no arquivo .INI");
                }
                docExpense.setCostCenter(Integer.valueOf(costCenterEnv));
                //Define o cc de débito da troca como o cc
                swap.setCostCenterDebit(docExpense.getCostCenter());

                swap.setValue(docExpense.getValue());

                //Adiciona Swap
                swaps.add(swap);
            });
        });

        return swaps;
    }

}
