package sci.changecostcenter.Model;

import Dates.Dates;
import SimpleDotEnv.Env;
import fileManager.FileManager;
import java.io.File;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import sci.changecostcenter.Model.Entity.ContabilityEntry;
import sci.changecostcenter.Model.Entity.CostCenterEntry;
import sci.changecostcenter.Model.Entity.Swap;
import sql.Database;

public class CostCenterModel {

    private List<ContabilityEntry> contabilityEntries = new ArrayList<>();

    private List<Swap> swaps;

    public List<ContabilityEntry> getContabilityEntriesWithoutCostCenter(String reference) {
        //Reset
        contabilityEntries = new ArrayList<>();

        Map<String, String> variables = new HashMap<>();
        variables.put("enterpriseCode", Env.get("changeCostCenterEnterpriseCode"));
        variables.put("reference", reference);

        //Get result from Db
        List<String[]> results = Database.getDatabase().select(new File("sql\\selectContabilityEntriesWithoutCostCenter.sql"), variables);

        Integer enterpriseCode = Integer.valueOf(Env.get("changeCostCenterEnterpriseCode"));
        
        //PErcorre resultados
        for (String[] result : results) {
            ContabilityEntry entry = new ContabilityEntry();
            entry.setEnterpriseCode(enterpriseCode);
            entry.setKey(Integer.valueOf(result[1]));
            entry.setDefaultPlan(Integer.valueOf(result[2]));
            entry.setAccountDebit(result[3]==null?null:Integer.valueOf(result[3]));
            entry.setAccountCredit(result[4]==null?null:Integer.valueOf(result[4]));
            entry.setDate(Dates.getCalendarFromFormat(result[5], "yyyy-mm-dd"));
            entry.setValue(new BigDecimal(result[6]));
            entry.setDescriptionComplement(result[7]);
            entry.setEntryType(Integer.valueOf(result[8]));
            entry.setDocument(result[9]);
            entry.setParticipantDebit(result[11]==null?null:Integer.valueOf(result[11]));
            entry.setParticipantCredit(result[12]==null?null:Integer.valueOf(result[12]));

            contabilityEntries.add(entry);
        }

        return contabilityEntries;
    }

    public void setSwaps(List<Swap> swaps) {
        this.swaps = swaps;
    }

    public List<ContabilityEntry> getContabilityEntries() {
        return contabilityEntries;
    }

    public void setContabilityEntries(List<ContabilityEntry> contabilityEntries) {
        this.contabilityEntries = contabilityEntries;
    }

    public void importCostCenterEntriesToDatabase() {
        for (Swap swap : swaps) {
            for (CostCenterEntry entry : swap.getEntries()) {
                if (entry.getKey() != 0) {
                    insertContabilityEntryCostCenter(entry);
                }
            }
        }
    }

    private String scriptSqlInsertContabilityEntry = "";

    public void insertContabilityEntryOnDatabase(ContabilityEntry entry) {
        if (scriptSqlInsertContabilityEntry.isBlank()) {
            scriptSqlInsertContabilityEntry = FileManager.getText(new File("sql\\insertContabilityEntry.sql"));
        }

        //Cria variavel de trocas
        Map<String, String> variableChanges = new HashMap<>();

        variableChanges.put("enterpriseCode", Env.get("enterpriseCode"));
        variableChanges.put("accountDebit", entry.getAccountDebit() + ""); //reverse account to reverse values on database
        variableChanges.put("accountCredit", entry.getAccountCredit() + ""); //reverse account to reverse values on database
        variableChanges.put("date", new SimpleDateFormat("yyyy-mm-dd", Dates.BRAZIL).format(entry.getDate().getTime()));
        variableChanges.put("value", entry.getValue().toString());
        variableChanges.put("descriptionComplement", entry.getDescriptionComplement());
        variableChanges.put("document", entry.getDocument());
        variableChanges.put("participantDebit", entry.getParticipantDebit() + "");
        variableChanges.put("participantCredit", entry.getParticipantCredit() + "");

        Database.getDatabase().query(scriptSqlInsertContabilityEntry, variableChanges);
    }

    private String scriptSqlInsertContabilityEntryCostCenter = "";

    public void insertContabilityEntryCostCenter(CostCenterEntry entry) {
        if (scriptSqlInsertContabilityEntryCostCenter.isBlank()) {
            scriptSqlInsertContabilityEntryCostCenter = FileManager.getText(new File("sql\\insertContabilityEntryCostCenter.sql"));
        }

        //Cria variavel de trocas
        Map<String, String> variableChanges = new HashMap<>();

        variableChanges.put("enterpriseCode", Env.get("enterpriseCode"));
        variableChanges.put("centerCostPlan", Env.get("centerCostPlan"));
        variableChanges.put("key", entry.getKey().toString()); // Chave do lançamento        
        variableChanges.put("value", entry.getValue().toString());
        variableChanges.replace("valueType", entry.getValueType().toString());
        variableChanges.replace("centerCost", entry.getCostCenter().toString());

        Database.getDatabase().query(scriptSqlInsertContabilityEntryCostCenter, variableChanges);
    }

    private String scriptSqlGetLastContabilityEntryKey = "";

    public Integer getLastContabilityEntryKey() {
        if (scriptSqlGetLastContabilityEntryKey.isBlank()) {
            scriptSqlGetLastContabilityEntryKey = FileManager.getText(new File("sql\\selectLastContabilityEntryKey.sql"));
        }

        Map<String, String> variableChanges = new HashMap<>();
        variableChanges.put("enterpriseCode", Env.get("enterpriseCode"));

        ArrayList<String[]> results = Database.getDatabase().select(scriptSqlGetLastContabilityEntryKey, variableChanges);

        if (!results.isEmpty()) {
            return Integer.valueOf(results.get(0)[0]);
        } else {
            return 0;
        }
    }
}
