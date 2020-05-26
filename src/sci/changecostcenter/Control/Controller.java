package sci.changecostcenter.Control;

import Entity.Executavel;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import sci.changecostcenter.Model.CostCenterModel;
import sci.changecostcenter.Model.Entity.ContabilityEntry;
import sci.changecostcenter.Model.Entity.Swap;
import sql.Database;

public class Controller {
    //Models
    private static final CostCenterModel costCenterModel = new CostCenterModel();
    
    private static String reference = "202001";
    private static List<ContabilityEntry> contabilityEntries = new ArrayList<>();
    private static List<Swap> swaps = new ArrayList<>();

    public class defineDatabase extends Executavel {

        public defineDatabase() {
            nome = "Definindo banco de dados...";
        }

        @Override
        public void run() {
            Database.setStaticObject(new Database(new File("sci.cfg")));
        }
    }

    public class getContabilityEntries extends Executavel {

        public getContabilityEntries() {
            nome = "Buscando lançamentos contábeis sem centro de custo";
        }

        @Override
        public void run() {
            contabilityEntries = costCenterModel.getContabilityEntriesWithoutCostCenter(reference);
        }
    }

    public class getSwapList extends Executavel {

        public getSwapList() {
            nome = "Buscando lista de trocas";
        }

        @Override
        public void run() {
            //Set swaps
            //Function to create a swap list
            costCenterModel.setSwaps(swaps);
        }

    }

    public class createReversesList extends Executavel {

        public createReversesList() {
            nome = "Criando lista de lançamentos que serão estornados e serão inseridos outros com centro de custo";
        }

        @Override
        public void run() {
            costCenterModel.createReversesList();
        }
    }
    
    public class reverseEntriesOnDatabase extends Executavel{

        public reverseEntriesOnDatabase() {
            nome = "Estornando lançamentos no banco de dados...";
        }

        @Override
        public void run() {
            costCenterModel.reverseEntriesOnDatabase();
        }
        
    }
}
