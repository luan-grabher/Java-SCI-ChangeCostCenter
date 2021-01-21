package testPack;

import java.io.File;
import sci.changecostcenter.SCIChangeCostCenter;

public class testMain {

    public static void main(String[] args) {
        testStr();
    }
    
    public static void testStr(){
        String str = "";

        System.out.println(str.matches("[0-9]+"));
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
