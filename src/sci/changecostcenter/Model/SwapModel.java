package sci.changecostcenter.Model;

import Entity.Warning;
import Selector.Entity.FiltroString;
import java.io.File;
import java.math.BigDecimal;
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
                predicate = entriesOfDescriptionComplementFilter(swap.getFilter());
                predicateString = "Complemento de Histórico que possua os termos: '" + swap.getFilter().getPossui() + "'";
            } else if (swap.getAccountCreditOrDebit() != null) {
                //Se tiver conta de debito e credito
                predicate = entriesOfAccount(swap.getAccountCreditOrDebit());
                predicateString = "Conta de débito ou crédito igual a: " + swap.getAccountCreditOrDebit();
            } else if (swap.getAccountCredit() != null) {
                //Se tiver conta de credito
                predicate = entriesOfCreditAccount(swap.getAccountCredit());
                predicateString = "Conta de crédito igual a: " + swap.getAccountCredit();
            } else if (swap.getAccountDebit() != null) {
                //Se tiver conta de debito
                predicate = entriesOfDebitAccount(swap.getAccountDebit());
                predicateString = "Conta de débito igual a: " + swap.getAccountDebit();
            } else {
                //Se nenhuma das opções
                predicate = null;
            }

            try {
                if (predicate != null) {
                    if(addKeyOfPredicateInSwapIfExists(swap, entries, predicate)){
                        finds++;
                    }else {
                        //Se tiver um filtro, valor e nf, ai irá tentar outra pesquisa
                        if (swap.getFilter() != null) {
                            //Se tiver filtro
                            predicate = entriesOfTitleAndValue(swap.getTitle(), swap.getValue());
                            predicateString = "Complemento de Histórico que possua: '" + swap.getTitle() + "' e tenha o valor de " + swap.getValue().setScale(2);
                            
                            if(addKeyOfPredicateInSwapIfExists(swap, entries, predicate)){
                                finds++;
                            }else{
                                log.append("\nNenhum lançamento encontrado para a procura: ").append(predicateString);
                            }
                        }else{
                            log.append("\nNenhum lançamento encontrado para a procura: ").append(predicateString);
                        }                        
                    }
                } else {
                    throw new Exception("Não foi possível encontrar lançamentos de contabilidade com os dados fornecidos.");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        throw new Warning("Foram encontrados " + finds + " lançamentos contábeis das " + swaps.size() + " trocas que deveriam ser feitas.");
    }

    private boolean addKeyOfPredicateInSwapIfExists(Swap swap, List<ContabilityEntry> entries, Predicate<ContabilityEntry> predicate) {
        //Busca lançamento que possua o filtro no complemento
        Optional<ContabilityEntry> optionalEntry = entries.stream().filter(predicate).findFirst();
        if (optionalEntry.isPresent()) {
            //Cria Objeto do lançamento
            ContabilityEntry entry = optionalEntry.get();

            //Pega o primeiro lançamento da troca e define a chave do lançamento
            swap.getEntries().get(0).setKey(entry.getKey());
            return true;
        }else{
            return false;
        }
    }

    private Predicate<ContabilityEntry> entriesOfTitleAndValue(String title, BigDecimal value) {
        return e -> e.getDescriptionComplement().contains(title) && e.getValue().compareTo(value) == 0;
    }

    private Predicate<ContabilityEntry> entriesOfDescriptionComplementFilter(FiltroString filter) {
        return e -> filter.éFiltroDaString(e.getDescriptionComplement());
    }

    private Predicate<ContabilityEntry> entriesOfAccount(Integer account) {
        return entriesOfDebitAccount(account).or(entriesOfCreditAccount(account));
    }

    private Predicate<ContabilityEntry> entriesOfDebitAccount(Integer account) {
        return e -> Objects.equals(e.getAccountDebit(), account);
    }

    private Predicate<ContabilityEntry> entriesOfCreditAccount(Integer account) {
        return e -> Objects.equals(e.getAccountCredit(), account);
    }
}
