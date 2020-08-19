package sci.changecostcenter.Model;

import Entity.Warning;
import Selector.Entity.FiltroString;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;
import sci.changecostcenter.Model.Entity.ContabilityEntry;
import sci.changecostcenter.Model.Entity.CostCenterEntry;
import sci.changecostcenter.Model.Entity.Swap;
import static sci.changecostcenter.SCIChangeCostCenter.log;

public class SwapModel {

    private List<Swap> swaps = new ArrayList<>();
    private List<CostCenterEntry> referenceCostCenters = new ArrayList<>();

    public List<CostCenterEntry> getReferenceCostCenters() {
        return referenceCostCenters;
    }

    public void setReferenceCostCenters(List<CostCenterEntry> referenceCostCenters) {
        this.referenceCostCenters = referenceCostCenters;
    }

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
        model.setReferenceCostCenters(referenceCostCenters);
        model.setSwaps();
        swaps.addAll(model.getSwaps());
    }

    public void setKeysOfSwaps(List<ContabilityEntry> entries) {
        Integer finds = 0;
        
        //Percorre trocas
        for (Swap swap : swaps) {
            //Define predicado
            Predicate predicate;
            String predicateString = "";

            if (swap.getFilter() != null) {
                //Se tiver filtro
                predicate = entriesDescriptionComplementFilter(swap.getFilter());
                predicateString = "Complemento de Histórico que possua um dos termos: '" + swap.getFilter().getPossui() + "' e não possua nenhum dos termos: '" + swap.getFilter().getNaoPossui();
            } else if (swap.getAccountCreditOrDebit() != null) {
                //Se tiver conta de debito e credito
                predicate = entriesAccount(swap.getAccountCreditOrDebit());
                predicateString = "Conta de débito ou crédito igual a: " + swap.getAccountCreditOrDebit();
            } else if (swap.getAccountCredit() != null) {
                //Se tiver conta de credito
                predicate = entriesCreditAccount(swap.getAccountCredit());
                predicateString = "Conta de crédito igual a: " + swap.getAccountCredit();
            } else if (swap.getAccountDebit() != null) {
                //Se tiver conta de debito
                predicate = entriesDebitAccount(swap.getAccountDebit());
                predicateString = "Conta de débito igual a: " + swap.getAccountDebit();
            } else {
                //Se nenhuma das opções
                predicate = null;
            }

            try {
                if(predicate != null){
                    //Busca lançamento que possua o filtro no complemento
                    Optional<ContabilityEntry> optionalEntry = entries.stream().filter(predicate).findFirst();
                    if (optionalEntry.isPresent()) {
                        //Cria Objeto do lançamento
                        ContabilityEntry entry = optionalEntry.get();

                        //Pega o primeiro lançamento da troca e define a chave do lançamento
                        swap.getEntries().get(0).setKey(entry.getKey());
                        finds++;
                    }else{
                        log.append("\nNenhum lançamento encontrado para a procura: ").append(predicateString);
                    }
                }else{
                    throw new Exception("Não foi possível encontrar lançamentos de contabilidade com os dados fornecidos.");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        
        throw new Warning("Foram encontrados " + finds  + " lançamentos contábeis das " + swaps.size() + " trocas que deveriam ser feitas.");
    }

    private Predicate<ContabilityEntry> entriesDescriptionComplementFilter(FiltroString filter) {
        return e -> filter.éFiltroDaString(e.getDescriptionComplement());
    }

    private Predicate<ContabilityEntry> entriesAccount(Integer account) {
        return entriesDebitAccount(account).and(entriesCreditAccount(account));
    }

    private Predicate<ContabilityEntry> entriesDebitAccount(Integer account) {
        return e -> Objects.equals(e.getAccountDebit(), account);
    }

    private Predicate<ContabilityEntry> entriesCreditAccount(Integer account) {
        return e -> Objects.equals(e.getAccountCredit(), account);
    }
}
