package sci.changecostcenter.Control;

import Entity.Executavel;
import SimpleDotEnv.Env;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import sci.changecostcenter.Model.CostCenterModel;
import sci.changecostcenter.Model.Entity.Swap;
import sci.changecostcenter.Model.ExpenseModel;
import sci.changecostcenter.Model.SwapFileModel;
import sci.changecostcenter.Model.SwapModel;
import sql.Database;

public class Controller {

    //Models
    private final CostCenterModel costCenterModel = new CostCenterModel();
    private final SwapModel swapModel = new SwapModel();

    private String reference = "202001";
    private final List<Swap> swaps = new ArrayList<>();

    public void setReference(String reference) {
        this.reference = reference;
    }

    public class defineDatabase extends Executavel {

        public defineDatabase() {
            name = "Definindo banco de dados...";
        }

        @Override
        public void run() {
            File databseConfigFile = new File(Env.get("databaseCfgFilePath"));
            
            File[] folderFiles = new File("").listFiles();           

            if (databseConfigFile.exists()) {
                Database.setStaticObject(new Database(databseConfigFile));
                if (!Database.getDatabase().testConnection()) {
                    throw new Error("Erro ao conectar ao banco de dados!");
                }
            } else {
                throw new Error("O arquivo de configuração do banco de dados não foi encontrado em: "  + databseConfigFile.getAbsolutePath());
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

    public class setExpensesFile extends Executavel {

        private File file;

        public setExpensesFile(File file) {
            name = "Definindo arquivo de despesas";
            this.file = file;
        }

        @Override
        public void run() {
            swapModel.importExpenseSwaps(file);
        }
    }

    public class setSwapsFile extends Executavel {

        private File file;

        public setSwapsFile(File file) {
            name = "Definindo arquivo de trocas";
            this.file = file;
        }

        @Override
        public void run() {
            swapModel.importSwapFileSwaps(file);
        }
    }

    public class getSwapList extends Executavel {

        public getSwapList() {
            name = "Buscando lista de trocas";
        }

        @Override
        public void run() {
            //Set swaps
            swapModel.setKeysOfSwaps(costCenterModel.getContabilityEntries());
            //Function to create a swap list
            costCenterModel.setSwaps(swaps);
        }

    }

    public class importCostCenterEntriesToDatabase extends Executavel {

        public importCostCenterEntriesToDatabase() {
            name = "Importando centros de custos dos lançamentos para o banco de dados";
        }

        @Override
        public void run() {
            costCenterModel.importCostCenterEntriesToDatabase();
        }

    }
}
