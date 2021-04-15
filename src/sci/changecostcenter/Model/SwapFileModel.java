package sci.changecostcenter.Model;

import Entity.ErrorIgnore;
import fileManager.CSV;
import fileManager.StringFilter;
import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.ini4j.Ini;
import sci.changecostcenter.Model.Entity.Swap;
import static sci.changecostcenter.SCIChangeCostCenter.ini;
import static sci.changecostcenter.SCIChangeCostCenter.log;

public class SwapFileModel {

    public static List<Swap> getSwaps(File file) {
        List<Swap> swaps = new ArrayList<>();

        Ini.Section config = (Ini.Section) ini.get("Config");

        String enterprise = config.getOrDefault("enterprise", "999").toString();
        Boolean filterValue = Boolean.valueOf((String) config.get("filtrarValor"));

        List<Map<String, String>> csvMap = CSV.getMap(file);
        csvMap.forEach((row) -> {
            try {
                //Cria objeto de troca
                Swap swap = new Swap();
                swap.setEnterprise(Integer.valueOf(row.getOrDefault("empresa", enterprise)));
                swap.setCostCenterCredit(getIntOrNull(row.getOrDefault("cc credito", null)));
                swap.setCostCenterDebit(getIntOrNull(row.getOrDefault("cc debito", null)));

                String valueStr = row.getOrDefault("valor", "");
                BigDecimal value = "".equals(valueStr) ? null : new BigDecimal(brVal(valueStr));
                swap.setValue(value);
                if (filterValue && value != null) {
                    swap.setValueFilter(value);
                }

                String percentStr = row.getOrDefault("porcentagem", "");
                BigDecimal percent = "".equals(percentStr) ? null : new BigDecimal(brVal(percentStr));
                swap.setPercent(percent);

                Integer credit = getIntOrNull(row.get("credito"));
                Integer debit = getIntOrNull(row.get("credito"));
                //Se tiver conta nos dois e a conta for igual
                if (credit != null && credit.equals(debit)) {
                    //Define filtro nas duas contas
                    swap.setAccountCreditOrDebit(credit);
                } //Se tiver filtro de conta de credito ou debito
                else if (credit != null || debit != null) {
                    //Define filtro na conta de credito e de debito
                    swap.setAccountCredit(credit);
                    swap.setAccountDebit(debit);
                }

                //Se tiver filtro de historico
                String hist = row.getOrDefault("historico", "");
                if (!"".equals(hist)) {
                    swap.setComplementFilter(new StringFilter(hist.replaceAll(" ", ";")));
                }

                swaps.add(swap);
            } catch (Exception e) {
                e.printStackTrace();
                log.append("\n Erro (").append(e.getMessage()).append(") na seguinte linha do arquivo de trocas não foi inserida:;").append(StringFilter.printMap(row, ";", true));
            }
        });

        //Verifica se existe algum valor de troca
        if (swaps.isEmpty()) {
            throw new ErrorIgnore("Nenhuma troca para ser feita encontrada no arquivo de trocas.");
        }

        return swaps;
    }

    /**
     * Transforma string em integer.
     *
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
