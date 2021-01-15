package sci.changecostcenter.Control;

import Entity.Executavel;
import java.io.File;
import sci.changecostcenter.Model.CostCenterModel;
import sci.changecostcenter.Model.SwapModel;
import sci.changecostcenter.SCIChangeCostCenter;
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
        @Override
        public void run() {
            File databseConfigFile = new File(SCIChangeCostCenter.ini.get("Config","databaseCfgFilePath"));

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
     * Apaga todos os centros de custos do período
     * 
     */
    public class deleteReferenceCCs extends Executavel {

        @Override
        public void run() {
            
        }
        
    }

    /**
     * Cria lista de lançamentos que não tem centro de custo naquela referência
     */
    public class getContabilityEntries extends Executavel {

        @Override
        public void run() {
            costCenterModel.getReferenceContabilityEntries(reference);
        }
    }
    
    /**
     * Cria lista de Centros de custo dos lançamentos do mês da referência
     */
    public class getReferenceCostCenters extends Executavel{

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
            this.file = file;
        }

        @Override
        public void run() {
            swapModel.importSwapFileSwaps(file);
            costCenterModel.setSwaps(swapModel.getSwaps());
        }
    }

    /**
     * Define as chaves dos lançamentos das trocas, depois define as trocas a
     * serem realizadas no modelo do CC com as trocas do modelo de trocas.
     **/
    public class setSwapsToImport extends Executavel {

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

        @Override
        public void run() {
            costCenterModel.importCostCenterEntriesToDatabase();
        }

    }
}
