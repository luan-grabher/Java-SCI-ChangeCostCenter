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

    public static List<Swap> getSwaps(File file) {
        List<Swap> swaps = new ArrayList<>();

        Section cols = (Section) ini.get("Swap File cols");

        if (file.exists() && !FileManager.getText(file).isBlank()) {

            Integer colEnterprise = Integer.parseInt((String) cols.get("empresa"));
            Integer colHist = Integer.parseInt((String) cols.get("historico"));
            Integer colCredit = Integer.parseInt((String) cols.get("credito"));
            Integer colDebit = Integer.parseInt((String) cols.get("debito"));
            Integer colCcDebit = Integer.parseInt((String) cols.get("cc debito"));
            Integer colCcCredit = Integer.parseInt((String) cols.get("cc credito"));
            Integer colValue = Integer.parseInt((String) cols.get("valor"));
            Integer colPercent = Integer.parseInt((String) cols.get("porcentagem"));

            //Pega linhas do arquivo de texto
            String[] lines = FileManager.getText(file).split("\r\n");

            //Percorre linhas
            for (String line : lines) {
                try {
                    //Remove espaços
                    line = line.replaceAll(" ", "");

                    //Se a linha só tiver números continua, se nao é cabeçalho
                    if (line.matches("[0-9;.,]+")) {
                        String[] collumns = line.split(";", -1);

                        //getInteger transforma em branco em null
                        Integer enterprise = getIntOrNull(collumns[colEnterprise]);
                        String hist = collumns[colHist].equals("")?null:collumns[colHist];
                        Integer credit = getIntOrNull(collumns[colCredit]);
                        Integer debit = getIntOrNull(collumns[colDebit]);
                        Integer ccCredit = getIntOrNull(collumns[colCcCredit]);
                        Integer ccDebit = getIntOrNull(collumns[colCcDebit]);
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
                        } else if(credit != null || debit != null){
                            //Se não define as contas de credito e debito
                            swap.setAccountCredit(credit);
                            swap.setAccountDebit(debit);
                        }else if(hist != null){
                            swap.setComplementFilter(hist);
                        }

                        swaps.add(swap);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    log.append("\n Erro (").append(e.getMessage()).append(") na seguinte linha do arquivo de trocas não foi inserida:;").append(line);
                }
            }
        }

        //Verifica se existe algum valor de troca
        if (swaps.isEmpty()) {
            throw new ErrorIgnore("Nenhuma troca para ser feita encontrada no arquivo de trocas.");
        }

        return swaps;
    }

    /**
     * Transforma string em integer.
     * @param str Texto que será convertido em número
     * @return Se for null, em branco ou menor que 0, retorna null.
     */
    private static Integer getIntOrNull(String str) {
        if (str != null && !str.equals("")) {
            try {
                Integer number = Integer.parseInt(str);
                if (number > 0) {
                    return number;
                }
            } catch (Exception e) {
            }
        }
        return null;
    }

    private static String brVal(String str) {
        return str.replaceAll("\\.", "").replaceAll(",", ".");
    }
}
