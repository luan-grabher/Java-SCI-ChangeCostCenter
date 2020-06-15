package sci.changecostcenter.Control;

import Entity.Executavel;
import SimpleDotEnv.Env;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import sci.changecostcenter.Model.CostCenterModel;
import sci.changecostcenter.Model.Entity.Swap;
import sci.changecostcenter.Model.SwapModel;
import sql.Database;

public class Controller {
    //Models
    private static final CostCenterModel costCenterModel = new CostCenterModel();
    private static final SwapModel swapModel = new SwapModel();
    
    private static String reference = "202001";
    private static List<Swap> swaps = new ArrayList<>();

    public class defineDatabase extends Executavel {

        public defineDatabase() {
            name = "Definindo banco de dados...";
        }

        @Override
        public void run() {
            Database.setStaticObject(new Database(new File(Env.get("databaseCfgFilePath"))));
            if(!Database.getDatabase().testConnection()){
                throw new Error("Erro ao conectar ao banco de dados!");
            }
        }
    }

    public class getContabilityEntries extends Executavel {

        public getContabilityEntries() {
            name = "Buscando lançamentos contábeis sem centro de custo";
        }

        @Override
        public void run() {
            costCenterModel.getContabilityEntriesWithoutCostCenter(reference);
        }
    }

    public class getSwapList extends Executavel {

        public getSwapList() {
            name = "Buscando lista de trocas";
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
            name = "Criando lista de lançamentos que serão estornados e serão inseridos outros com centro de custo";
        }

        @Override
        public void run() {
            costCenterModel.createReversesList();
        }
    }
    
    public class reverseEntriesOnDatabase extends Executavel{

        public reverseEntriesOnDatabase() {
            name = "Estornando lançamentos no banco de dados...";
        }

        @Override
        public void run() {
            costCenterModel.reverseEntriesOnDatabase();
        }        
    }
    
    public class insertNewEntriesOnDatabase extends Executavel{

        public insertNewEntriesOnDatabase() {
            name = "Inserindo novos lançamentos no banco de dados...";
        }

        @Override
        public void run() {
            costCenterModel.insertNewEntriesOnDatabase();
        }
        
    }
}
