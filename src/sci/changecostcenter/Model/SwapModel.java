package sci.changecostcenter.Model;

import Selector.Entity.FiltroString;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;
import sci.changecostcenter.Model.Entity.ContabilityEntry;
import sci.changecostcenter.Model.Entity.Swap;

public class SwapModel {

    private List<Swap> swaps = new ArrayList<>();

    public List<Swap> getSwaps() {
        return swaps;
    }

    public void importExpenseSwaps(File file) {
        ExpenseModel model = new ExpenseModel();
        model.setFile(file);
        model.setExpenses();
        swaps.addAll(model.getSwapList());
    }

    public void importSwapFileSwaps(File file) {
        SwapFileModel model = new SwapFileModel();
        model.setFile(file);
        model.setSwaps();
        swaps.addAll(model.getSwaps());
    }

    public void setKeysOfSwaps(List<ContabilityEntry> entries) {
        //Percorre swaps
        for (Swap swap : swaps) {
            //Define predicado
            Predicate predicate;
           
            if (swap.getFilter() != null) {
                //Se tiver filtro
                predicate = entriesDescriptionComplementFilter(swap.getFilter());
            } else if(swap.getAccountCreditOrDebit() != 0){
                //Se tiver conta de debito e credito
                predicate = entriesAccount(swap.getAccountCreditOrDebit());
            }else if(swap.getAccountCredit() != 0){
                //Se tiver conta de credito
                predicate = entriesCreditAccount(swap.getAccountCredit());
            }else if(swap.getAccountDebit() != 0){
                //Se tiver conta de debito
                predicate = entriesDebitAccount(swap.getAccountDebit());
            }else{
                //Se nenhuma das opções
                predicate = null;
            }

            try {
                //Busca lançamento que possua o filtro no complemento
                Optional<ContabilityEntry> optionalEntry = entries.stream().filter(predicate).findFirst();
                if (optionalEntry.isPresent()) {
                    ContabilityEntry entry = optionalEntry.get();

                    swap.getEntries().get(0).setKey(entry.getKey());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public Predicate<ContabilityEntry> entriesDescriptionComplementFilter(FiltroString filter) {
        return e -> filter.éFiltroDaString(e.getDescriptionComplement());
    }

    public Predicate<ContabilityEntry> entriesAccount(Integer account) {
        return entriesDebitAccount(account).and(entriesCreditAccount(account));
    }

    public Predicate<ContabilityEntry> entriesDebitAccount(Integer account) {
        return e -> Objects.equals(e.getAccountDebit(), account);
    }

    public Predicate<ContabilityEntry> entriesCreditAccount(Integer account) {
        return e -> Objects.equals(e.getAccountCredit(), account);
    }
}
