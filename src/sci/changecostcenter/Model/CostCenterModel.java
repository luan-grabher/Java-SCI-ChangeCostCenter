package sci.changecostcenter.Model;

import Dates.Dates;
import SimpleDotEnv.Env;
import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import sci.changecostcenter.Model.Entity.ContabilityEntry;
import sci.changecostcenter.Model.Entity.Swap;
import sql.Database;

public class CostCenterModel {

    private List<ContabilityEntry> contabilityEntries = new ArrayList<>();

    private List<ContabilityEntry> reverseEntries = new ArrayList<>();
    private List<ContabilityEntry> newEntries = new ArrayList<>();

    private List<Swap> swaps;

    public List<ContabilityEntry> getContabilityEntriesWithoutCostCenter(String reference) {
        //Reset
        contabilityEntries = new ArrayList<>();

        Map<String, String> variables = new HashMap<>();
        variables.put("enterpriseCode", Env.get("enterpriseCode"));
        variables.put("reference", reference);

        //Get result from Db
        List<String[]> results = Database.getDatabase().select(new File("sql\\selectContabilityEntriesWithoutCostCenter.sql"), variables);

        //PErcorre resultados
        for (String[] result : results) {
            ContabilityEntry entry = new ContabilityEntry();
            entry.setEnterpriseCode(Integer.valueOf(Env.get("enterpriseCode")));
            entry.setKey(Integer.valueOf(result[1]));
            entry.setDefaultPlan(Integer.valueOf(result[2]));
            entry.setAccountDebit(Integer.valueOf(result[3]));
            entry.setAccountCredit(Integer.valueOf(result[4]));
            entry.setDate(Dates.getCalendarFromFormat(result[5], "yyyy-mm-dd"));
            entry.setValue(new BigDecimal(result[6]));
            entry.setDescriptionComplement(result[7]);
            entry.setEntryType(Integer.valueOf(result[8]));
            entry.setDocument(result[9]);
            entry.setParticipantDebit(Integer.valueOf(result[11]));
            entry.setParticipantCredit(Integer.valueOf(result[12]));

            contabilityEntries.add(entry);
        }

        return contabilityEntries;
    }

    public void setSwaps(List<Swap> swaps) {
        this.swaps = swaps;
    }

    public void createReversesList() {
        for (ContabilityEntry entry : contabilityEntries) {
            for (Swap swap : swaps) {
                if (swap.getFilter().éFiltroDaString(entry.getDescriptionComplement())) {
                    if (swap.getAccountCredit() == null || Objects.equals(swap.getAccountCredit(), entry.getAccountCredit())) {
                        if (swap.getAccountDebit() == null || Objects.equals(swap.getAccountDebit(), entry.getAccountDebit())) {
                            if (swap.getDescriptionCode() == null || Objects.equals(swap.getDescriptionCode(), entry.getDescriptionCode())) {
                                if (swap.getParticipantCredit() == null || Objects.equals(swap.getParticipantCredit(), entry.getParticipantCredit())) {
                                    if (swap.getParticipantDebit()== null || Objects.equals(swap.getParticipantDebit(), entry.getParticipantDebit())) {
                                        reverseEntries.add(entry);
                                        newEntries.addAll(swap.getEntries());
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    public List<ContabilityEntry> getContabilityEntries() {
        return contabilityEntries;
    }

    public void setContabilityEntries(List<ContabilityEntry> contabilityEntries) {
        this.contabilityEntries = contabilityEntries;
    }

    public List<ContabilityEntry> getReverseEntries() {
        return reverseEntries;
    }

    public void setReverseEntries(List<ContabilityEntry> reverseEntries) {
        this.reverseEntries = reverseEntries;
    }

    public List<ContabilityEntry> getNewEntries() {
        return newEntries;
    }

    public void setNewEntries(List<ContabilityEntry> newEntries) {
        this.newEntries = newEntries;
    }
    
    
}
