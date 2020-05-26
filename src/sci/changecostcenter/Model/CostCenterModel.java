package sci.changecostcenter.Model;

import Dates.Dates;
import SimpleDotEnv.Env;
import SimpleView.Loading;
import fileManager.FileManager;
import java.io.File;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
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
                                    if (swap.getParticipantDebit() == null || Objects.equals(swap.getParticipantDebit(), entry.getParticipantDebit())) {
                                        reverseEntries.add(entry);

                                        /*
                                            AQUI VAI TER QUE COLOCAR PARA CONFORME 
                                            OS DOIS CENTROS DE CUSTOS FOREM NULOS, 
                                            NAO ADICIONA OS LANÇAMENTOS.
                                            SE EXISTIR PELO MENOS UM CENTRO DE CUSTO
                                            E NÃO HOUVER NENHUM LANÇAMENTO, CRIA UM 
                                            LANÇAMENTO CÓPIA DO ESTORNADO.
                                         */
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

    public void reverseEntriesOnDatabase() {
        //Cria carregamento
        Loading loading = new Loading("Estornando lançamentos no banco", 0, reverseEntries.size());
        Integer count = 0;

        for (ContabilityEntry reverseEntry : reverseEntries) {
            //Atualiza carregamento
            count++;
            loading.updateBar(count);

            Integer accountCredit = reverseEntry.getAccountCredit();
            Integer accountDebit = reverseEntry.getAccountDebit();
            Integer participantCredit = reverseEntry.getParticipantCredit();
            Integer participantDebit = reverseEntry.getParticipantDebit();

            reverseEntry.setAccountCredit(accountDebit);
            reverseEntry.setAccountDebit(accountCredit);
            reverseEntry.setParticipantCredit(participantDebit);
            reverseEntry.setParticipantDebit(participantCredit);

            insertContabilityEntryOnDatabase(reverseEntry);
        }
    }

    public void insertNewEntriesOnDatabase() {
        //Cria carregamento
        Loading loading = new Loading("Inserindo novos lançamentos no banco", 0, newEntries.size());
        Integer count = 0;

        for (ContabilityEntry newEntry : newEntries) {
            count++;
            loading.updateBar(count);

            //insere lançamento
            insertContabilityEntryOnDatabase(newEntry);

            //Define key of entry
            newEntry.setKey(getLastContabilityEntryKey());

            //Insere centros de custo
            insertContabilityEntryCostCenter(newEntry);
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

    public void insertContabilityEntryCostCenter(ContabilityEntry entry) {
        if (scriptSqlInsertContabilityEntryCostCenter.isBlank()) {
            scriptSqlInsertContabilityEntryCostCenter = FileManager.getText(new File("sql\\insertContabilityEntryCostCenter.sql"));
        }

        //Cria variavel de trocas
        Map<String, String> variableChanges = new HashMap<>();

        variableChanges.put("enterpriseCode", Env.get("enterpriseCode"));
        variableChanges.put("key", "chave"); //reverse account to reverse values on database
        variableChanges.put("centerCostPlan", Env.get("centerCostPlan")); //reverse account to reverse values on database
        variableChanges.put("value", entry.getValue().toString());
        variableChanges.put("valueType", null);
        variableChanges.put("centerCost", null);

        if (entry.getCostCenterCredit() != null) {
            variableChanges.replace("valueType", "1");
            variableChanges.replace("centerCost", entry.getCostCenterCredit() + "");

            Database.getDatabase().query(scriptSqlInsertContabilityEntryCostCenter, variableChanges);
        }

        if (entry.getCostCenterDebit() != null) {
            variableChanges.replace("valueType", "0");
            variableChanges.replace("centerCost", entry.getCostCenterDebit() + "");

            Database.getDatabase().query(scriptSqlInsertContabilityEntryCostCenter, variableChanges);
        }

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
