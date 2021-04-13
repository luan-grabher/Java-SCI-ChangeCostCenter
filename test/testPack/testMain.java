package testPack;

import fileManager.FileManager;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import sci.changecostcenter.SCIChangeCostCenter;
import sql.Database;
import sql.SQL;

public class testMain {

    public static void main(String[] args) {
        testWithFiles();
    }

    public static void deletefirstList() {
        List<Map<String, Object>> lista = new ArrayList<>();
        
        //Mapas
        Map<String, Object> map1 = new HashMap<>();
        map1.put("cod", 1);
        
        Map<String, Object> map2 = new HashMap<>();
        map2.put("cod", 2);
        
        Map<String, Object> map3 = new HashMap<>();
        map3.put("cod", 3);
        
        lista.add(map1);
        lista.add(map2);
        lista.add(map3);

        Map<String, Object> entry = lista.get(0);
        //Exclui o resto
        lista.clear();
        //coloca nas entradas a primeira entrada
        lista.add(entry);
        
        System.out.println("aaa");
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
    
    public static void testWithFiles(){
        Integer month = 7;
        Integer year = 7;
        File swapsFile = new File("D:\\Downloads\\cc072020.csv");
        File expensesFile = new File("D:\\Downloads\\despesas072020.xlsx");
        
        SCIChangeCostCenter.mainFunction("Teste", month, year, swapsFile, expensesFile);
    }

    public static void mainFunction() {
        String[] args = new String[]{"-ini","zampieronCC"};
        SCIChangeCostCenter.main(args);
    }

}
