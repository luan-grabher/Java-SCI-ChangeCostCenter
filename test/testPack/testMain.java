package testPack;

import SimpleDotEnv.Env;
import java.io.File;
import sci.changecostcenter.SCIChangeCostCenter;

public class testMain {

    public static void main(String[] args) {
        mainFunction();
    }
    
    public static void testFolder(){
        String cfgPath = Env.get("databaseCfgFilePath");
        
        System.out.println("Path cfg: " + cfgPath);
        
        File folder = new File(cfgPath);
        
        System.out.println(folder.getAbsolutePath());
        System.out.println(folder.exists());
        
        System.out.println(folder.listFiles().length +  " arquivos e pastas encontrados:");
        
        for (File file : folder.listFiles()) {
            System.out.println(file.getName());
        }
    }

    public static void mainFunction() {
        Integer month = 7;
        Integer year = 2020;

        String name = "Teste";

        File folder = new File("C:\\Users\\ti01\\Documents");

        File swapsFile = new File(folder.getAbsolutePath() + "\\cc 072020.csv");
        File expensesFile = new File(folder.getAbsolutePath() + "\\despesas ate 072020.xlsx");

        System.out.println(SCIChangeCostCenter.mainFunction(name, month, year, swapsFile, expensesFile));
    }

}
