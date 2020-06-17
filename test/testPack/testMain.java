package testPack;

import java.io.File;
import sci.changecostcenter.SCIChangeCostCenter;

public class testMain {


    public static void main(String[] args) {
         Integer month = 4;
         Integer year  = 2020;
         
         String name = "Teste";
         
         File folder = new File("G:\\Contábil\\Clientes\\Zampieron & Dalallacorte\\Escrituração Mensal\\2020\\Movimento");
         
         File swapsFile = new File(folder.getAbsolutePath() + "\\");
         File expensesFile = new File(folder.getAbsolutePath() + "\\");
         
         SCIChangeCostCenter.mainFunction(name, month, year, swapsFile, expensesFile);
    }
    
}
