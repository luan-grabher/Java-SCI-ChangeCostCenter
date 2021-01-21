package sci.changecostcenter.Control;

import Entity.Executavel;
import java.io.File;
import sci.changecostcenter.Model.ExpenseModel;
import sci.changecostcenter.Model.SwapFileModel;
import sci.changecostcenter.Model.SwapModel;
import static sci.changecostcenter.SCIChangeCostCenter.ini;
import sql.Database;

public class Controller {  

    /** 
     * Define Database estático conforme local definido no arquivo INI
     */
    public class defineDatabase extends Executavel {
        @Override
        public void run() {
            File databseConfigFile = new File(ini.get("Config","databaseCfgFilePath"));

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
            //Cria mapa de trocas com referencia e empresa
            //Lista empresas das trocas
            //para cada empresa exclui os cc das referencias
            //executa query sql com o arquivo sql
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
            SwapModel.addSwaps(ExpenseModel.getSwaps(file));
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
            SwapModel.addSwaps(SwapFileModel.getSwaps(file));
        }
    }
}
