package testPack;

import java.io.File;
import sci.changecostcenter.SCIChangeCostCenter;

public class testMain {

    public static void main(String[] args) {
        mainFunction();
    }
    

    public static void mainFunction() {
        Integer month = 6;
        Integer year = 2020;

        String name = "Teste";

        File folder = new File("D:\\NetBeansProjects\\Java-SCI-ChangeCostCenter\\test");

        File swapsFile = new File(folder.getAbsolutePath() + "\\trocas.csv");
        File expensesFile = new File(folder.getAbsolutePath() + "\\despesas.xlsx");

        System.out.println(SCIChangeCostCenter.mainFunction(name, month, year, swapsFile, expensesFile));
    }

}
