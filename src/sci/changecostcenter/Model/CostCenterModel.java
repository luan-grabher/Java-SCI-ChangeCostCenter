
package sci.changecostcenter.Model;

import Dates.Dates;
import SimpleDotEnv.Env;
import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import sci.changecostcenter.Model.Entity.ContabilityEntry;
import sql.Database;

public class CostCenterModel {
    private static List<ContabilityEntry> contabilityEntries = new ArrayList<>();
    
    public static List<ContabilityEntry> getContabilityEntriesWithoutCostCenter(String reference){
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
}
