package sci.changecostcenter.Model;

import fileManager.FileManager;
import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import sci.changecostcenter.Model.Entity.CostCenterEntry;
import sci.changecostcenter.Model.Entity.Swap;

public class SwapFileModel {

    private File file;
    private List<Swap> swaps = new ArrayList<>();

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
                
                //Define conta utilizada
                Integer accountCredit = Integer.valueOf(collumns[colAccountCredit]);
                Integer accountDebit = Integer.valueOf(collumns[colAccountDebit]);
                Integer costCenterCredit = Integer.valueOf(collumns[colCostCenterCredit]);
                Integer costCenterDebit = Integer.valueOf(collumns[colCostCenterDebit]);

                //Cria objeto de troca
                Swap swap = new Swap();
                
                //Define conta do da troca
                if(accountCredit != 0){
                    swap.setAccountCredit(accountCredit);
                }else{
                    swap.setAccountDebit(accountDebit);
                }
                
                //Define centro de custo da troca
                Integer costCenter;
                Integer valueType;
                if(costCenterCredit != 0){
                    costCenter = costCenterCredit;
                    valueType = CostCenterEntry.TYPE_CREDIT;
                    swap.setCostCenterCredit(costCenterCredit);
                }else{
                    costCenter = costCenterDebit;
                    valueType = CostCenterEntry.TYPE_DEBIT;
                    swap.setCostCenterDebit(costCenterDebit);
                }
                
                //Cria lançamento para a troca
                CostCenterEntry costCenterEntry = new CostCenterEntry();
                costCenterEntry.setCostCenter(costCenter);
                costCenterEntry.setValueType(valueType);
                costCenterEntry.setValue(new BigDecimal(collumns[colValue]));
                
                //Adiciona lançamento na troca
                swap.getEntries().add(costCenterEntry);
                
                swaps.add(swap);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public List<Swap> getSwaps() {
        return swaps;
    }    
}
