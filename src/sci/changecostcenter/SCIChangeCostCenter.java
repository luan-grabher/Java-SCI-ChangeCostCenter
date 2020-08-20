package sci.changecostcenter;

import Entity.Executavel;
import Executor.Execution;
import SimpleDotEnv.Env;
import fileManager.FileManager;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;
import sci.changecostcenter.Control.Controller;

public class SCIChangeCostCenter {
    public static StringBuilder log = new StringBuilder("");

    public static void main(String[] args) {
        String name = "Zampieron trocar centros de custo ";

        try {
            String monthString = JOptionPane.showInputDialog("Por favor insira o MÊS:");
            //Filtra números com 0 na frente ou não e uma casa com números de 0 à 9 ou números com 1 na frente seguido de 0,1 ou 2
            if (monthString.matches("(0?[1-9]|1[012])")) {
                Integer month = Integer.valueOf(monthString);

                String yearString = JOptionPane.showInputDialog("Por favor insira o ANO:");
                //Filtra números que começam com 2 seguidos de 3 casas com números de 0  a 9
                if (yearString.matches("[2][0-9][0-9][0-9]")) {
                    Integer year = Integer.valueOf(yearString);

                    //Escolhe arquivo de trocas CSV
                    JOptionPane.showMessageDialog(null, "Por favor escolha o arquivo de trocas CSV:");
                    File swapsFile = Selector.Arquivo.selecionar("", "Arquivo de trocas CSV", "csv");
                    //Se arquivo existir, continua a execução, se não exibe erro
                    if (swapsFile.exists()) {
                        JOptionPane.showMessageDialog(null, "Por favor escolha o arquivo de Despesas XLSX:");
                        File expensesFile = Selector.Arquivo.selecionar("", "Arquivo de Despesas XLSX", "xlsx");
                        
                        if(expensesFile.exists()){
                            //executa função principal
                            mainFunction(name, month, year, swapsFile, expensesFile);
                        }else{
                            JOptionPane.showMessageDialog(null, "Arquivo de Despesas inválido!", "Arquivo de Despesas inválido!", JOptionPane.ERROR_MESSAGE);
                        }
                    } else {
                        JOptionPane.showMessageDialog(null, "Arquivo de trocas inválido!", "Arquivo de trocas inválido!", JOptionPane.ERROR_MESSAGE);
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "Ano inválido!", "Ano inválido!", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(null, "Mês inválido!", "Mês inválido!", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
        }
    }

    public static String mainFunction(String name, Integer month, Integer year, File swapsFile, File expensesFile) {
        String str = "";

        try {
            Env.setEncoding("utf-8");
            Env.setPath("zampieron_change_cc");
            if(Env.getEnvs().isEmpty()){
                throw new Exception("Arquivo Env não encontrado ou vazio!");
            }

            Controller controller = new Controller();
            controller.setReference(year + (month < 10 ? "0" : "") + month);

            List<Executavel> execs = new ArrayList<>();

            execs.add(controller.new defineDatabase());
            execs.add(controller.new getReferenceCostCenters());
            execs.add(controller.new getContabilityEntries());
            execs.add(controller.new setExpensesFile(expensesFile));
            execs.add(controller.new setSwapsFile(swapsFile));
            execs.add(controller.new getKeysOfSwaps());
            execs.add(controller.new setSwapsToImport());
            execs.add(controller.new importCostCenterEntriesToDatabase());
            
            Execution execution = new Execution(name);
            execution.setShowMessages(true);
            execution.setExecutables(execs);
            execution.runExecutables();
            execution.endExecution(true);
            
            
            if(!"".equals(log.toString())){
                FileManager.save(new File(System.getProperty("user.home")) + "\\Desktop\\log.csv", log.toString());
                JOptionPane.showMessageDialog(null, "Arquivo log.csv com LOG foi salvo na área de trabalho!");
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            str = "Erro interno no programa: " + e.getMessage();
            JOptionPane.showMessageDialog(null, str, "Erro!", JOptionPane.ERROR_MESSAGE);
        }

        return str;
    }
}
