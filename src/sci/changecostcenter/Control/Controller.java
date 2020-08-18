package sci.changecostcenter.Control;

import Entity.Executavel;
import SimpleDotEnv.Env;
import java.io.File;
import sci.changecostcenter.Model.CostCenterModel;
import sci.changecostcenter.Model.SwapModel;
import sql.Database;

public class Controller {

    //Models
    private final CostCenterModel costCenterModel = new CostCenterModel();
    private final SwapModel swapModel = new SwapModel();

    private String reference = "202001";

    /** Define Referencia
     *  @param reference Referencia AAAAMM
     */
    public void setReference(String reference) {
        this.reference = reference;
    }    

    /** 
     * Define Database estático conforme local definido no arquivo ENV
     */
    public class defineDatabase extends Executavel {

        public defineDatabase() {
            name = "Definindo banco de dados...";
        }

        @Override
        public void run() {
            File databseConfigFile = new File(Env.get("databaseCfgFilePath"));

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

    /**
     * Cria lista de lançamentos que não tem centro de custo naquela referência
     */
    public class getContabilityEntries extends Executavel {

        public getContabilityEntries() {
            name = "Buscando lançamentos contábeis sem centro de custo";
        }

        @Override
        public void run() {
            costCenterModel.getContabilityEntriesWithoutCostCenter(reference);
        }
    }
    
    /**
     * Cria lista de Centros de custo dos lançamentos do mês da referência
     */
    public class getReferenceCostCenters extends Executavel{

        public getReferenceCostCenters() {
            name = "Buscando centros de custo da referência " + reference;
        }

        @Override
        public void run() {
            swapModel.setReferenceCostCenters(costCenterModel.getReferenceCostCenterEntries(reference));
        }
        
    }

    
    /**
     * Cria lista de trocas do arquivo de Despesas
     */
    public class setExpensesFile extends Executavel {

        private final File file;

        public setExpensesFile(File file) {
            name = "Definindo arquivo de despesas";
            this.file = file;
        }

        @Override
        public void run() {
            swapModel.importExpenseSwaps(file);
        }
    }

    /**
     *  Cria lista de trocas do arquivo de Trocas PIS COFINS
     */
    public class setSwapsFile extends Executavel {

        private final File file;

        public setSwapsFile(File file) {
            name = "Definindo arquivo de trocas";
            this.file = file;
        }

        @Override
        public void run() {
            swapModel.importSwapFileSwaps(file);
        }
    }

    /**
     * Define as chaves dos lançamentos das trocas, depois define as trocas a
     * serem realizadas no modelo do CC com as trocas do modelo de trocas.
     **/
    public class getSwapList extends Executavel {

        public getSwapList() {
            name = "Buscando lista de trocas";
        }

        @Override
        public void run() {
            //Set swaps
            swapModel.setKeysOfSwaps(costCenterModel.getContabilityEntries());
            //Function to create a swap list
            costCenterModel.setSwaps(swapModel.getSwaps());
        }

    }

    /**
     * Importa as trocas para 
     */
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
