package sci.changecostcenter;

import Entity.Executavel;
import Robo.AppRobo;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import sci.changecostcenter.Control.Controller;

public class SCIChangeCostCenter {

    public static void main(String[] args) {
        String name = "Zampieron trocar cenrtros de custo ";

        AppRobo robot = new AppRobo(name);

        robot.definirParametros();
        Integer month = robot.getParametro("mes").getMes();
        Integer year = robot.getParametro("ano").getInteger();
        robot.setNome(name + month + year);
        
        File swapsFile = new File("");
        File expensesFile = new File("");

        robot.executar(
                mainFunction(name, month, year,swapsFile, expensesFile)
        );
    }

    public static String mainFunction(String name, Integer month, Integer year, File swapsFile, File expensesFile) {
        String str = "";

        try {
            Controller controller = new Controller();
            controller.setReference(year + (month < 10 ? "0" : "") + month);

            List<Executavel> execs = new ArrayList<>();

            execs.add(controller.new defineDatabase());
            execs.add(controller.new getContabilityEntries());
            execs.add(controller.new setExpensesFile(expensesFile));
            execs.add(controller.new setSwapsFile(swapsFile));
            execs.add(controller.new getSwapList());
            execs.add(controller.new importCostCenterEntriesToDatabase());

            str = AppRobo.rodarExecutaveis(name, execs);
        } catch (Exception e) {
            e.printStackTrace();
            str = "Erro no interno no programa: " + e.getMessage();
        }

        return str;
    }
}
