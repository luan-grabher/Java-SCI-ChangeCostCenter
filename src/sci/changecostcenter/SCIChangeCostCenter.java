package sci.changecostcenter;

import Entity.Executavel;
import Executor.Execution;
import fileManager.Args;
import fileManager.FileManager;
import java.io.File;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.swing.JOptionPane;
import org.ini4j.Ini;
import sci.changecostcenter.Control.Controller;

public class SCIChangeCostCenter {

    public static StringBuilder log = new StringBuilder("");
    public static Ini ini = null;
    public static String reference;
    private static String iniPath = null;

    public static void main(String[] args) {
        String name = "Alterar / Inserir centros de custo ";

        try {

            int choice = SimpleView.View.chooseButton(
                    "Escolha:",
                    new String[]{
                        "Baixar arquivo de trocas CC exemplo",
                        "Inserir CCs"
                    });

            if (choice == 0) {
                FileManager.save(
                        new File(System.getProperty("user.home") + "/Desktop"), //desktop
                        "Exemplo Arquivo de Trocas CC.csv",
                        FileManager.getText(FileManager.getFile("./trocasCC.csv"))
                );
                
                JOptionPane.showMessageDialog(null, "Arquivo salvo na área de trabalho!");
            } else {

                //Start Ini file
                iniPath = Args.get(args, "ini");
                iniPath = iniPath == null ? "mgmCC.ini" : iniPath; //Se não tiver nos argumentos define como .ini
                ini = new Ini(FileManager.getFile(iniPath));

                String monthString = JOptionPane.showInputDialog("Por favor insira o MÊS:");
                //Filtra números com 0 na frente ou não e uma casa com números de 0 à 9 ou números com 1 na frente seguido de 0,1 ou 2
                if (monthString.matches("(0?[1-9]|1[012])")) {
                    Integer month = Integer.valueOf(monthString);

                    String yearString = JOptionPane.showInputDialog("Por favor insira o ANO:");
                    //Filtra números que começam com 2 seguidos de 3 casas com números de 0  a 9
                    if (yearString.matches("[2][0-9][0-9][0-9]")) {
                        Integer year = Integer.valueOf(yearString);

                        //Escolhe arquivo de trocas CSV
                        File swapsFile = FileManager.getFileFromUser("Arquivo de Trocas de CC", "csv");

                        File expenseFile = null;
                        if ("true".equals(ini.get("Config", "despesas"))) {
                            expenseFile = FileManager.getFileFromUser("Arquivo de despesas", "xlsx");
                        }

                        if (JOptionPane.YES_OPTION == JOptionPane.showConfirmDialog(
                                null, "O programa irá apagar todos os centros de custo do mês " + month + "/" + year + ". Deseja continuar SIM(Yes) ou NÃO(Not)?",
                                "Continuar?", JOptionPane.YES_NO_OPTION
                        )) {
                            //executa função principal
                            mainFunction(name, month, year, swapsFile, expenseFile);
                        } else {
                            JOptionPane.showMessageDialog(null, "Programa parado pelo usuário!", "Programa parado pelo usuário!", JOptionPane.ERROR_MESSAGE);
                        }
                    } else {
                        JOptionPane.showMessageDialog(null, "Ano inválido!", "Ano inválido!", JOptionPane.ERROR_MESSAGE);
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "Mês inválido!", "Mês inválido!", JOptionPane.ERROR_MESSAGE);
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Ocorreu um erro Java: " + e, "Erro Java", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static String mainFunction(String name, Integer month, Integer year, File swapsFile, File expensesFile) {
        String str = "";

        try {

            reference = year + (month < 10 ? "0" : "") + month;

            Controller controller = new Controller();

            //Inicia mapa
            Map<String, Executavel> execs = new LinkedHashMap();

            execs.put("Definindo banco de dados " + reference, controller.new defineDatabase()); //Define o banco de dados estático
            execs.put("Excluindo lançamentos de Centro de Custo da Referencia " + reference, controller.new deleteReferenceCCs());

            if (expensesFile != null) {
                execs.put("Definindo trocas das despesas " + reference, controller.new setExpensesFile(expensesFile));
            }

            execs.put("Definindo trocas do arquivo de trocas " + reference, controller.new setSwapsFile(swapsFile));
            execs.put("Importando para o banco de dados " + reference, controller.new importSwapsToDb());

            Execution execution = new Execution(name);
            execution.setShowMessages(true);
            execution.setExecutionMap(execs);
            execution.runExecutables();
            execution.endExecution(true);

            if (!"".equals(log.toString())) {
                FileManager.save(new File(System.getProperty("user.home")) + "\\Desktop\\log " + reference + ".csv", log.toString());
                JOptionPane.showMessageDialog(null, "Arquivo 'log " + reference + ".csv' com LOG foi salvo na área de trabalho!");
            } else {
                JOptionPane.showMessageDialog(null, "O programa não gerou nenhum log, isso é um pouco estranho...", "Algo estranho aconteceu", JOptionPane.ERROR_MESSAGE);
            }

        } catch (Exception e) {
            e.printStackTrace();
            str = "Erro interno no programa: " + e.getMessage();
            JOptionPane.showMessageDialog(null, str, "Erro!", JOptionPane.ERROR_MESSAGE);
        }

        return str;
    }

}
