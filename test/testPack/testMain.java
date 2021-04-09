package testPack;

import fileManager.FileManager;
import java.util.HashMap;
import java.util.Map;
import sci.changecostcenter.SCIChangeCostCenter;
import sql.Database;
import sql.SQL;

public class testMain {

    public static void main(String[] args) {
        mainFunction();
    }

    public static void deleteCCs(String mes) {
        Database.setStaticObject(new Database("sci.cfg"));

        String sqlGetReferenceEntries = FileManager.getText(FileManager.getFile("sql\\getCCReferenceEntries.sql"));
        String sqlDelete = FileManager.getText(FileManager.getFile("sql\\deleteCC.sql"));

        sqlGetReferenceEntries = sqlGetReferenceEntries.replaceAll("L.BDTIPOLANORIG  = 100", "(L.BDCREDITO = 555 OR L.BDDEBITO = 555)");

        //Lista empresas das trocas
        String enterprisesStr = "1;5;17";
        String[] enterprises = enterprisesStr.split(";");

        //Cria mapa de trocas com referencia e empresa
        Map<String, String> sqlSwaps = new HashMap<>();

        //Para cada empresa
        for (String enterprise : enterprises) {
            sqlSwaps.put("enterprise", enterprise);
            sqlSwaps.put("reference", "2020" + mes);

            String keys = Database.getDatabase().select(sqlGetReferenceEntries, sqlSwaps).get(0)[0];

            if (keys != null && !keys.equals("")) {
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

    public static void mainFunction() {
        String[] args = new String[]{"mgmCC.ini"};
        SCIChangeCostCenter.main(args);
    }

}
