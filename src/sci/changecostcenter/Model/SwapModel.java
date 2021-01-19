package sci.changecostcenter.Model;

import fileManager.FileManager;
import fileManager.StringFilter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import sci.changecostcenter.Model.Entity.CostCenter;
import sci.changecostcenter.Model.Entity.Swap;
import static sci.changecostcenter.SCIChangeCostCenter.ini;
import static sci.changecostcenter.SCIChangeCostCenter.log;
import sql.Database;

public class SwapModel {

    private static final String sql_GetContabilityEntries = FileManager.getText(FileManager.getFile("\\sql\\selectChangeCostCenterContabilityEntries.sql"));

    /**
     * Recebe as trocas na sua lista de trocas. Percorre trocas e cria cc no
     * banco. Faz call para CC Model para importar para o banco.
     */
    private static final List<Swap> swaps = new ArrayList<>();

    public static List<Swap> getSwaps() {
        return swaps;
    }

    public static void addSwap(Swap swap) {
        swaps.add(swap);
    }

    public static void setKeysOfSwaps() {
        swaps.forEach((swap) -> {
            Map<String, String> usedFilter = new HashMap<>();

            Map<String, String> sqlSwaps = new HashMap<>();
            sqlSwaps.put("enterprise", swap.getEnterprise().toString());//Empresa
            sqlSwaps.put("centerCostPlan", ini.get("Config", "centerCostPlan"));//Plano Centro de Custo
            sqlSwaps.put("complement", "");//Plano Centro de Custo

            if (swap.getComplementFilter() != null) {//Filtro para o complemento
                //Cria variavel local
                StringFilter complementfilter = swap.getComplementFilter();

                //Mostra o filtro usado
                String hasFilter = "Complemento de Histórico que possua os termos: '"
                        + complementfilter.printMap(complementfilter.getHas(), ", ") + "'";
                String hasNotFilter = "Complemento de Histórico que NÃO possua os termos: '"
                        + complementfilter.printMap(complementfilter.getHasNot(), ", ") + "'";
                usedFilter.put(hasFilter, hasFilter);
                usedFilter.put(hasNotFilter, hasNotFilter);

                //Adicionar no complemento um AND para cada has e um AND para cada hasNot                                  
                StringBuilder sqlSwap = new StringBuilder();
                complementfilter.getHas().forEach((h, has) -> {
                    sqlSwap.append(" AND BDCOMPL LIKE '%").append(has).append("%'");
                });
                complementfilter.getHas().forEach((hn, hasNot) -> {
                    sqlSwap.append(" AND BDCOMPL NOT LIKE '%").append(hasNot).append("%'");
                });

                sqlSwaps.put("complement", sqlSwap.toString());
            } else if (swap.getAccountCreditOrDebit() != null) {
                //Mostra filtro usado
                String filter = "Conta de débito ou crédito igual a: " + swap.getAccountCreditOrDebit();
                usedFilter.put(filter, filter);
                //Filtro sql
                StringBuilder sqlSwap = new StringBuilder();
                sqlSwap
                        .append(" AND (BDDEBITO = ")
                        .append(swap.getAccountCreditOrDebit())
                        .append(" OR BDCREDITO = ")
                        .append(swap.getAccountCreditOrDebit())
                        .append(")");

                sqlSwaps.put("account", sqlSwap.toString());
            } else if (swap.getAccountCredit() != null) {
                //Mostra filtro usado
                String filter = "Conta de crédito igual a: " + swap.getAccountCredit();
                usedFilter.put(filter, filter);
                //Filtro sql
                StringBuilder sqlSwap = new StringBuilder();
                sqlSwap
                        .append(" AND BDCREDITO = ")
                        .append(swap.getAccountCredit());

                sqlSwaps.put("account", sqlSwap.toString());
            } else if (swap.getAccountDebit() != null) {
                //Mostra filtro usado
                String filter = "Conta de débito igual a: " + swap.getAccountDebit();
                usedFilter.put(filter, filter);
                //Filtro sql
                StringBuilder sqlSwap = new StringBuilder();
                sqlSwap
                        .append(" AND BDCREDITO = ")
                        .append(swap.getAccountDebit());

                sqlSwaps.put("account", sqlSwap.toString());
            }

            //Cria variavel de lctos
            List<Map<String, Object>> entries = Database.getDatabase().getMap(sql_GetContabilityEntries, sqlSwaps);

            //Se não encontrar nenhum lcto
            if (entries.isEmpty() && swap.getComplementFilter() != null && swap.getDocument() != null && swap.getValueFilter() != null) {
                //Define o sqlSwap complement para o titulo(documento)
                sqlSwaps.put("complement", " AND BDCOMPL LIKE '%" + swap.getDocument() + "%' ");
                //Define o sqlSwap value para o valor
                sqlSwaps.put("value", " AND BDVALOR = " + swap.getValueFilter().toPlainString());

                //Mostra filtro usado
                String filter = "Ou complemento de histórico que possua: '" + swap.getDocument() + "' e tenha o valor de " + swap.getValueFilter();
                usedFilter.put(filter, filter);

                //Procura Lctos
                entries = Database.getDatabase().getMap(sql_GetContabilityEntries, sqlSwaps);
            }

            //Se existirem lctos na variavel de lctos
            if (!entries.isEmpty()) {
                //Se tiver valor para inserir ou filtro de valor, exclui todos lançamentos menos so primeiro
                if (swap.getValue() != null || swap.getValueFilter() != null) {
                    while (entries.size() > 1) {
                        entries.remove(entries.size() - 1);
                    }
                }
                //Para cada lcto
                entries.forEach((e) ->{
                    //Cria objeto CC
                    CostCenter cc = new CostCenter();
                    cc.setEnterprise(swap.getEnterprise());
                    cc.setCenterCostPlan(Integer.valueOf(sqlSwaps.get("centerCostPlan")));
                });                                
            } else {
                //Se não existirem lctos na variavel de lctos
                //****Mostra no log que não foi encontrado lctos para aquele filtro
                log.append("\n\nNenhum lançamento encontrado para a procura: ");
                usedFilter.forEach((f, filter) -> {
                    log.append("\n").append(filter);
                });
            }
        });
    }
}
