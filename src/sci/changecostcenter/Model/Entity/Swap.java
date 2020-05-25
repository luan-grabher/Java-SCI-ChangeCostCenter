package sci.changecostcenter.Model.Entity;

import Selector.Entity.FiltroString;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class Swap {
    private FiltroString filters;
    private List<ContabilityEntry> entries = new ArrayList<>();

    public Swap() {
    }

    public FiltroString getFilters() {
        return filters;
    }

    public void setFilters(FiltroString filters) {
        this.filters = filters;
    }

    public List<ContabilityEntry> getEntries() {
        return entries;
    }

    public void setEntries(List<ContabilityEntry> entries) {
        this.entries = entries;
    }
    
    public BigDecimal getTotalValue(){
        BigDecimal total = new BigDecimal(0);
        for (ContabilityEntry entry : entries) {
            total.add(entry.getValue());
        }
        
        return total;
    }
}
