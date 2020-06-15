package sci.changecostcenter.Model;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import sci.changecostcenter.Model.Entity.Swap;

public class SwapModel {
    private List<Swap> swaps = new ArrayList<>();

    public List<Swap> getSwaps() {
        return swaps;
    }

    public void setSwaps() {
        
    }
    
    public void importExpenseSwaps(File file){
        ExpenseModel model = new ExpenseModel();
        model.setFile(file);
        model.setExpenses();
        swaps.addAll(model.getSwapList());
    }
    
    
}
