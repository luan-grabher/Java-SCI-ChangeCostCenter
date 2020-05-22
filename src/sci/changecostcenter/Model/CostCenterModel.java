
package sci.changecostcenter.Model;

import SimpleDotEnv.Env;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import sci.changecostcenter.Model.Entity.ContabilityEntry;

public class CostCenterModel {
    private static List<ContabilityEntry> contabilityEntries = new ArrayList<>();
    
    public static List<ContabilityEntry> getContabilityEntriesWithoutCostCenter(String reference){
        //Reset
        contabilityEntries = new ArrayList<>();
        
        Map<String, String> variables = new HashMap<>();
        variables.put("enterpriseCode", Env.get("enterpriseCode"));
        variables.put("reference", reference);
        
        
        
        return contabilityEntries;
    }
}
