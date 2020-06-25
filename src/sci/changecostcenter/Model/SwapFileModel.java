package sci.changecostcenter.Model;

import fileManager.FileManager;
import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import sci.changecostcenter.Model.Entity.CostCenterEntry;
import sci.changecostcenter.Model.Entity.Swap;

public class SwapFileModel {

    private File file;
    private List<Swap> swaps = new ArrayList<>();
    private List<CostCenterEntry> referenceCostCenters = new ArrayList<>();

    public List<CostCenterEntry> getReferenceCostCenters() {
        return referenceCostCenters;
    }

    public void setReferenceCostCenters(List<CostCenterEntry> referenceCostCenters) {
        this.referenceCostCenters = referenceCostCenters;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public void setSwaps() {       
        Integer colAccountCredit = 0;
        Integer colAccountDebit = 1;
        Integer colCostCenterDebit = 2;
        Integer colCostCenterCredit = 3;
        Integer colValue = 4;

        //Get file text lines
        String[] lines = FileManager.getText(file).split("\r\n");

        //Percorre linhas
        for (String line : lines) {
            try {
                String[] collumns = line.split(";");

                //Se "remover" as letras da coluna e a coluna continuar igual, quer dizer que não tem letras
                //Se tiver letras, ou seja, o replace trazer algo diferente, não deve continuar
                if (collumns[colAccountCredit].replaceAll("[a-zA-Z]+", "").equals(collumns[colAccountCredit])) {

                    //Define conta utilizada
                    Integer accountCredit = Integer.valueOf(collumns[colAccountCredit]);
                    Integer accountDebit = Integer.valueOf(collumns[colAccountDebit]);
                    Integer costCenterCredit = Integer.valueOf(collumns[colCostCenterCredit]);
                    Integer costCenterDebit = Integer.valueOf(collumns[colCostCenterDebit]);

                    //Cria PRedicados
                    Predicate<CostCenterEntry> predicateAccount;

                    //Cria objeto de troca
                    Swap swap = new Swap();

                    //Define conta do da troca e o predicado
                    if (accountCredit != 0) {
                        swap.setAccountCredit(accountCredit);
                        predicateAccount = centerCostHasCreditAccount(accountCredit);
                    } else {
                        swap.setAccountDebit(accountDebit);
                        predicateAccount = centerCostHasDebitAccount(accountDebit);
                    }

                    //Define centro de custo da troca
                    Integer costCenter;
                    Integer valueType;
                    if (costCenterCredit != 0) {
                        costCenter = costCenterCredit;
                        valueType = CostCenterEntry.TYPE_CREDIT;
                        swap.setCostCenterCredit(costCenterCredit);
                    } else {
                        costCenter = costCenterDebit;
                        valueType = CostCenterEntry.TYPE_DEBIT;
                        swap.setCostCenterDebit(costCenterDebit);
                    }

                    //Set value
                    String valueString = collumns[colValue].replaceAll("\\.", "").replaceAll(",", ".");
                    BigDecimal value = new BigDecimal(valueString);

                    //Veririca se nao existe no banco ja um centro de custo
                    boolean costCenterAlreadyExist = referenceCostCenters.stream().anyMatch(
                            predicateAccount.and(
                                    c -> Objects.equals(c.getCostCenter(), costCenter))
                                    .and(
                                            c -> Objects.equals(c.getValueType(), valueType))
                                    .and(
                                            c -> c.getValue().compareTo(value) == 0)
                    );
                    
                    if(!costCenterAlreadyExist){
                        //Cria lançamento para a troca
                        CostCenterEntry costCenterEntry = new CostCenterEntry();
                        costCenterEntry.setCostCenter(costCenter);
                        costCenterEntry.setValueType(valueType);

                        costCenterEntry.setValue(value);

                        //Adiciona lançamento na troca
                        swap.getEntries().add(costCenterEntry);

                        swaps.add(swap);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private Predicate<CostCenterEntry> centerCostHasCreditAccount(Integer account) {
        return c -> Objects.equals(c.getCreditAccount(), account);
    }

    private Predicate<CostCenterEntry> centerCostHasDebitAccount(Integer account) {
        return c -> Objects.equals(c.getDebitAccount(), account);
    }

    public List<Swap> getSwaps() {
        return swaps;
    }
}
