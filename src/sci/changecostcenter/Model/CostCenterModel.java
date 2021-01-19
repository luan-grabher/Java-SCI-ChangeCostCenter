package sci.changecostcenter.Model;

import Dates.Dates;
import fileManager.FileManager;
import java.io.File;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import sci.changecostcenter.Model.Entity.ContabilityEntry;
import sci.changecostcenter.Model.Entity.CostCenter;
import sci.changecostcenter.Model.Entity.Swap;
import sci.changecostcenter.SCIChangeCostCenter;
import sql.Database;

public class CostCenterModel {

    private List<ContabilityEntry> contabilityEntries = new ArrayList<>();
    private List<CostCenter> referenceCostCenters = new ArrayList<>();
    
    private final String enterprise = SCIChangeCostCenter.ini.get("Config", "enterprise");
    private final String costCenterPlan = SCIChangeCostCenter.ini.get("Config", "costCenterPlan");

    private List<Swap> swaps;

    /**
     * Pega CCs lançados na referência informada.
     *
     * @param reference Referência no banco Ex. 202007 para julho de 2020
     * @return Uma lista com os CCs encontrados
     *
     */
    public List<CostCenter> getReferenceCostCenterEntries(String reference) {
        //Reset
        referenceCostCenters = new ArrayList<>();

        Map<String, String> variables = new HashMap<>();
        variables.put("enterpriseCode", enterprise);
        variables.put("reference", reference);

        List<String[]> results = Database.getDatabase().select(new File("sql\\selectReferenceContabilityEntriesCostCenters.sql"), variables);

        //TRansforma resultado em objetos
        for (String[] result : results) {
            //Inicia objeto
            CostCenter entry = new CostCenter();

            entry.setKey(Integer.valueOf(result[1]));
            entry.setCenterCostPlan(Integer.valueOf(result[2]));
            entry.setCostCenter(Integer.valueOf(result[3]));
            entry.setValueType(Integer.valueOf(result[4]));
            entry.setValue(new BigDecimal(result[5]));
            entry.setCreditAccount(result[6] == null ? null : Integer.valueOf(result[6]));
            entry.setDebitAccount(result[7] == null ? null : Integer.valueOf(result[7]));

            referenceCostCenters.add(entry);
        }

        return referenceCostCenters;
    }

    /**
     * A lista de CCs lançados na referência que foi informada na outra função.
     *
     * @return Uma lista com os CCs encontrados
     */
    public List<CostCenter> getReferenceCostCenters() {
        return referenceCostCenters;
    }

    /**
     * Pega e define a Lista de lançamentos da referencia informada Que não
     * possuam centro de custo
     *
     * @param reference Referência no banco Ex. 202007 para julho de 2020
     * @return Lista de lançamentos da referencia informada Que não possuam
     * centro de custo
     */
    public List<ContabilityEntry> getReferenceContabilityEntries(String reference) {
        //Reset
        contabilityEntries = new ArrayList<>();

        Map<String, String> variables = new HashMap<>();
        variables.put("enterpriseCode", enterprise);
        variables.put("reference", reference);

        //Get result from Db
        List<String[]> results = Database.getDatabase().select(new File("sql\\selectReferenceContabilityEntries.sql"), variables);

        Integer enterpriseCode = Integer.valueOf(enterprise);

        //PErcorre resultados
        for (String[] result : results) {
            ContabilityEntry entry = new ContabilityEntry();
            entry.setEnterpriseCode(enterpriseCode);
            entry.setKey(Integer.valueOf(result[1]));
            entry.setDefaultPlan(Integer.valueOf(result[2]));
            entry.setAccountDebit(result[3] == null ? null : Integer.valueOf(result[3]));
            entry.setAccountCredit(result[4] == null ? null : Integer.valueOf(result[4]));
            entry.setDate(Dates.getCalendarFromFormat(result[5], "yyyy-mm-dd"));
            entry.setValue(new BigDecimal(result[6]));
            entry.setDescriptionComplement(result[7]);
            entry.setEntryType(Integer.valueOf(result[8]));
            entry.setDocument(result[9]);
            entry.setParticipantDebit(result[11] == null ? null : Integer.valueOf(result[11]));
            entry.setParticipantCredit(result[12] == null ? null : Integer.valueOf(result[12]));
            entry.setCcCount(result[13] == null ? 0 : Integer.valueOf(result[13]));

            contabilityEntries.add(entry);
            //if((result[4]==null?0:Integer.valueOf(result[4])) == 265){
            //    System.out.println("Encontrado lançamento contabil: " + result[1]);
            //}            
        }

        return contabilityEntries;
    }

    /**
     * Define a lista de trocas que serão feitas
     *
     * @param swaps Lista com as trocas que serão feitas
     */
    public void setSwaps(List<Swap> swaps) {
        this.swaps = swaps;
    }

    /**
     * Pega lista de lançamentos que foram definidas anteriormente em outra
     * função
     *
     * @return lista de lançamentos que foram definidas anteriormente em outra
     * função
     */
    public List<ContabilityEntry> getContabilityEntries() {
        return contabilityEntries;
    }

    /**
     * Define a lista de lançamentos conforme a lista informada.
     *
     * @param contabilityEntries Lista de lançamentos
     */
    public void setContabilityEntries(List<ContabilityEntry> contabilityEntries) {
        this.contabilityEntries = contabilityEntries;
    }

    /**
     * Cria os CCs no banco de dados utilizando informações das trocas.
     */
    public void importCostCenterEntriesToDatabase() {
        for (Swap swap : swaps) {
            for (CostCenter entry : swap.getEntries()) {
                if (entry.getKey() != null && entry.getKey() > 0 && entry.getCostCenter() != null && entry.getCostCenter() > 0) {
                    insertContabilityEntryCostCenter(entry);
                }
            }
        }
    }

    /* Define os textos que estão nos arquivos SQL */
    private final String scriptSqlInsertContabilityEntryCostCenter = FileManager.getText(new File("sql\\insertContabilityEntryCostCenter.sql"));
    private final String scriptSqlSelectContabilityEntryCostCenterByKey = FileManager.getText(new File("sql\\selectContabilityEntryCostCenterByKey.sql"));

    /**
     * Insere Centro de Custo no banco
     * @param entry CC a ser inserido.
     */
    public void insertContabilityEntryCostCenter(CostCenter entry) {
        //Cria variavel de trocas
        Map<String, String> variableChanges = new HashMap<>();

        //Coloca chave nas trocas        
        variableChanges.put("key", entry.getKey().toString()); // Chave do lançamento
        variableChanges.put("enterpriseCode", enterprise);
        variableChanges.put("centerCostPlan", costCenterPlan);
        variableChanges.put("value", entry.getValue().toString());
        variableChanges.put("valueType", entry.getValueType().toString());
        variableChanges.put("centerCost", entry.getCostCenter().toString());

        //Procura Aquela chave nos lançamentos de CC da empresa, se não existir, insere, se não, não insere
        if (Database.getDatabase().select(scriptSqlSelectContabilityEntryCostCenterByKey, variableChanges).isEmpty()) {
            //Tenta inserir
            boolean result = true ;//Database.getDatabase().query(scriptSqlInsertContabilityEntryCostCenter, variableChanges);

            SCIChangeCostCenter.log
                    .append("\n")
                    .append("Inserido centro de custo ")
                    .append(entry.getCostCenter())
                    .append(" na chave ")
                    .append(entry.getKey())
                    .append(": ")
                    .append(result);
        } else {
            SCIChangeCostCenter.log
                    .append("\n")
                    .append("Já existe o centro de custo ")
                    .append(entry.getCostCenter())
                    .append(" na chave ")
                    .append(entry.getKey());
        }

    }
}
