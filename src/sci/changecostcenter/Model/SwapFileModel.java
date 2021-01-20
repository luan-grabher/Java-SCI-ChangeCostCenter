package sci.changecostcenter.Model;

import Entity.ErrorIgnore;
import fileManager.FileManager;
import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import org.ini4j.Profile.Section;
import sci.changecostcenter.Model.Entity.Swap;
import static sci.changecostcenter.SCIChangeCostCenter.ini;

public class SwapFileModel {

    private File file;
    private List<Swap> swaps = new ArrayList<>();

    public void setFile(File file) {
        this.file = file;
    }

    public void setSwaps() {
        Section cols = (Section) ini.get("Swap File cols");

        if (file.exists() && !FileManager.getText(file).isBlank()) {

            Integer colEnterprise =  (Integer) cols.get("empresa");
            Integer colAccountCredit = (Integer) cols.get("credito");
            Integer colAccountDebit = (Integer) cols.get("debito");
            Integer colCostCenterDebit = (Integer) cols.get("cc debito");
            Integer colCostCenterCredit =  (Integer) cols.get("cc credito");
            Integer colValue =  (Integer) cols.get("valor");
            Integer colPercent =  (Integer) cols.get("valor");

            //Get file text lines
            String[] lines = FileManager.getText(file).split("\r\n");

            //Percorre linhas
            for (String line : lines) {
                try {
                    String[] collumns = line.split(";");

                    //Verifica se existem apenas números na coluna de conta de crédito e débito
                    if (collumns[colAccountCredit].matches("[0-9]+") && collumns[colAccountDebit].matches("[0-9]+")) {

                        //Define conta utilizada
                        Integer accountCredit = Integer.valueOf(collumns[colAccountCredit]);
                        Integer accountDebit = Integer.valueOf(collumns[colAccountDebit]);
                        Integer costCenterCredit = Integer.valueOf(collumns[colCostCenterCredit]);
                        Integer costCenterDebit = Integer.valueOf(collumns[colCostCenterDebit]);

                        //Cria Predicados
                        Predicate<CostCenterEntry> predicateAccount;

                        //Cria objeto de troca
                        Swap swap = new Swap();

                        //Define conta da troca e do predicado
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
                                predicateAccount
                                        .and(
                                                c -> Objects.equals(c.getCostCenter(), costCenter))
                                        .and(
                                                c -> Objects.equals(c.getValueType(), valueType))
                        /*.and(
                                            c -> c.getValue().compareTo(value) == 0)*/
                        );

                        if (!costCenterAlreadyExist) {
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

        //Verifica se existe algum valor de troca
        if (swaps.isEmpty()) {
            throw new ErrorIgnore("Nenhuma troca para ser feita encontrada no arquivo de trocas.");
        }
    }

    public List<Swap> getSwaps() {
        return swaps;
    }
}
