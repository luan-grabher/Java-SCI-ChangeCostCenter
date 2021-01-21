package sci.changecostcenter.Model;

import Entity.ErrorIgnore;
import fileManager.FileManager;
import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import org.ini4j.Profile.Section;
import sci.changecostcenter.Model.Entity.Swap;
import static sci.changecostcenter.SCIChangeCostCenter.ini;
import static sci.changecostcenter.SCIChangeCostCenter.log;

public class SwapFileModel {

    public static List<Swap> setSwaps(File file) {
        List<Swap> swaps = new ArrayList<>();

        Section cols = (Section) ini.get("Swap File cols");

        if (file.exists() && !FileManager.getText(file).isBlank()) {

            Integer colEnterprise = (Integer) cols.get("empresa");
            Integer colCredit = (Integer) cols.get("credito");
            Integer colDebit = (Integer) cols.get("debito");
            Integer colCcDebit = (Integer) cols.get("cc debito");
            Integer colCcCredit = (Integer) cols.get("cc credito");
            Integer colValue = (Integer) cols.get("valor");
            Integer colPercent = (Integer) cols.get("porcentagem");

            //Pega linhas do arquivo de texto
            String[] lines = FileManager.getText(file).split("\r\n");

            //Percorre linhas
            for (String line : lines) {
                try {
                    //Remove espaços
                    line = line.replaceAll(" ", "");

                    //Se a linha só tiver números continua, se nao é cabeçalho
                    if (line.matches("[0-9;.,]+")) {
                        String[] collumns = line.split(";");

                        //getInteger transforma em branco em null
                        Integer enterprise = Integer.getInteger(collumns[colEnterprise]);
                        Integer credit = Integer.getInteger(collumns[colCredit]);
                        Integer debit = Integer.getInteger(collumns[colDebit]);
                        Integer ccCredit = Integer.getInteger(collumns[colCcCredit]);
                        Integer ccDebit = Integer.getInteger(collumns[colCcDebit]);
                        BigDecimal value = collumns[colValue].equals("") ? null : new BigDecimal(brVal(collumns[colValue]));
                        BigDecimal percent = collumns[colPercent].equals("") ? null : new BigDecimal(brVal(collumns[colPercent]));

                        //Cria objeto de troca
                        Swap swap = new Swap();
                        swap.setEnterprise(enterprise);
                        swap.setCostCenterCredit(ccCredit);
                        swap.setCostCenterDebit(ccDebit);
                        swap.setValue(value);
                        swap.setPercent(percent);

                        //Se tiver conta nos dois e a conta for igual
                        if (credit != null && credit.equals(debit)) {
                            swap.setAccountCreditOrDebit(ccCredit);
                        } else {
                            //Se não define as contas de credito e debito
                            swap.setAccountCredit(credit);
                            swap.setAccountDebit(debit);
                        }

                        swaps.add(swap);
                    }
                } catch (Exception e) {
                    log.append("\n A seguinte linha do arquivo de trocas não foi inserida: ").append(line);
                }
            }
        }

        //Verifica se existe algum valor de troca
        if (swaps.isEmpty()) {
            throw new ErrorIgnore("Nenhuma troca para ser feita encontrada no arquivo de trocas.");
        }

        return swaps;
    }

    private static String brVal(String str) {
        return str.replaceAll("\\.", "").replaceAll(",", ".");
    }
}
