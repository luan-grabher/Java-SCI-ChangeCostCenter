package sci.changecostcenter.Control;

import Entity.Executavel;
import fileManager.FileManager;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import sci.changecostcenter.Model.ExpenseModel;
import sci.changecostcenter.Model.SwapFileModel;
import sci.changecostcenter.Model.SwapModel;
import static sci.changecostcenter.SCIChangeCostCenter.ini;
import static sci.changecostcenter.SCIChangeCostCenter.reference;
import sql.Database;
import sql.SQL;

public class Controller {

    /**
     * Define Database estático conforme local definido no arquivo INI
     */
    public class defineDatabase extends Executavel {

        @Override
        public void run() {
            File databseConfigFile;
            try{
                databseConfigFile = new File(ini.get("Config", "databaseCfgFilePath"));
            }catch(NullPointerException nul){
                databseConfigFile = new File("s");
            }

            if (databseConfigFile.exists()) {
                Database.setStaticObject(new Database(databseConfigFile));
                if (!Database.getDatabase().testConnection()) {
                    throw new Error("Erro ao conectar ao banco de dados!");
                }
            } else {
                throw new Error("O arquivo de configuração do banco de dados não foi encontrado em: " + databseConfigFile.getAbsolutePath());
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
            String sqlGetReferenceEntries = FileManager.getText(FileManager.getFile("sql\\getCCReferenceEntries.sql"));
            String sqlDelete = FileManager.getText(FileManager.getFile("sql\\deleteCC.sql"));

            //Lista empresas das trocas
            String enterprisesStr = ini.get("Config", "enterprises");
            String[] enterprises = enterprisesStr.split(";");

            //Cria mapa de trocas com referencia e empresa
            Map<String, String> sqlSwaps = new HashMap<>();

            //Para cada empresa
            for (String enterprise : enterprises) {
                sqlSwaps.put("enterprise", enterprise);
                sqlSwaps.put("reference", reference);

                String keys = Database.getDatabase().select(sqlGetReferenceEntries, sqlSwaps).get(0)[0];
                sqlSwaps.put("inClause", SQL.divideIn(keys, "BDCHAVE"));

                try {
                    //executa query sql com o arquivo sql
                    Database.getDatabase().query(sqlDelete, sqlSwaps);
                } catch (Exception e) {
                    throw new Error(e);
                }
            }

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
     * Cria lista de trocas do arquivo de Trocas PIS COFINS
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
    
    /**
     * Insere um cc para cada troca
     */
    public class importSwapsToDb extends Executavel {

        @Override
        public void run() {
            SwapModel.insertCcForEachSwap();
        }
    }
}
