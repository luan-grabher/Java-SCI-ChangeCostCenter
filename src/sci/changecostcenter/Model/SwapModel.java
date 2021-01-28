package sci.changecostcenter.Model;

import SimpleView.Loading;
import fileManager.FileManager;
import fileManager.StringFilter;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import sci.changecostcenter.Model.Entity.CostCenter;
import sci.changecostcenter.Model.Entity.Swap;
import static sci.changecostcenter.SCIChangeCostCenter.ini;
import static sci.changecostcenter.SCIChangeCostCenter.log;
import static sci.changecostcenter.SCIChangeCostCenter.reference;
import sql.Database;

public class SwapModel {

    private static final String sql_GetContabilityEntries = FileManager.getText(FileManager.getFile("sql\\selectChangeCostCenterContabilityEntries.sql"));
    private static final String sql_InsertCostCenter = FileManager.getText(FileManager.getFile("sql\\insertCostCenter.sql"));

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

    public static void addSwaps(List<Swap> swapsToAdd) {
        swaps.addAll(swapsToAdd);
    }

    public static void insertCcForEachSwap() {
        Map<String, Object> loading = new HashMap<>();
        loading.put("loading", new Loading("Procurando CCs " + reference, 0, swaps.size()));
        loading.put("count", (Integer) 0);
        loading.put("size", (Integer) swaps.size());
        try {
            swaps.forEach((swap) -> {
                loading.put("count", (Integer) loading.get("count") + 1);
                ((Loading) loading.get("loading")).updateBar((Integer) loading.get("count"));

                Map<String, String> usedFilter = new HashMap<>();

                Map<String, String> sqlSwaps = new HashMap<>();
                sqlSwaps.put("enterprise", swap.getEnterprise().toString());//Empresa
                sqlSwaps.put("centerCostPlan", ini.get("Config", "centerCostPlan"));//Plano Centro de Custo
                sqlSwaps.put("reference", reference);
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
                    complementfilter.getHasNot().forEach((hn, hasNot) -> {
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
                            .append(" AND BDDEBITO = ")
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

                    Map<String, Object> insertLoading = new HashMap<>();
                    insertLoading.put("loading", new Loading("Importando CCs " + reference, 0, entries.size()));
                    insertLoading.put("count", (Integer) 0);
                    insertLoading.put("size", (Integer) entries.size());

                    //Para cada lcto
                    entries.forEach((e) -> {
                        insertLoading.put("count", (Integer) insertLoading.get("count") + 1);
                        ((Loading) insertLoading.get("loading")).updateBar((Integer) insertLoading.get("count"));

                        //Cria objeto CC
                        CostCenter cc = new CostCenter();
                        cc.setKey(Integer.valueOf(e.get("BDCHAVE").toString()));
                        cc.setEnterprise(swap.getEnterprise());
                        cc.setCenterCostPlan(Integer.valueOf(sqlSwaps.get("centerCostPlan")));
                        cc.setCostCenter(swap.getCostCenter(), swap.getValueType());

                        //Se tiver o valor
                        if (swap.getValue() != null) {
                            //Define o valor
                            cc.setValue(swap.getValue());
                        } else if (swap.getPercent() != null) {
                            //Se tiver porcentagem
                            //Define o valor como a % do valor do lançamento
                            BigDecimal value = new BigDecimal(e.get("BDVALOR").toString());
                            cc.setValue(value.multiply(swap.getPercent()).setScale(2, RoundingMode.HALF_UP));
                        }

                        //Insere o CC
                        insertCC(cc);                        
                    });
                    
                    ((Loading) insertLoading.get("loading")).dispose();
                } else {
                    //Se não existirem lctos na variavel de lctos
                    //****Mostra no log que não foi encontrado lctos para aquele filtro
                    log.append("\n\nNenhum lançamento encontrado para a procura: ");
                    usedFilter.forEach((f, filter) -> {
                        log.append("\n").append(filter);
                    });
                }
            });
        } catch (Exception e) {
            ((Loading) loading.get("loading")).dispose();
            throw new Error(e);
        }

        ((Loading) loading.get("loading")).dispose();
    }

    private static void insertCC(CostCenter cc) {
        if (cc.getEnterprise() != null
                && cc.getKey() != null
                && cc.getCenterCostPlan() != null
                && cc.getCostCenter() != null
                && cc.getValue() != null
                && cc.getValueType() != null) {
            Map<String, String> sqlChanges = new HashMap<>();
            sqlChanges.put("enterprise", cc.getEnterprise().toString());
            sqlChanges.put("key", cc.getKey().toString());
            sqlChanges.put("centerCostPlan", cc.getCenterCostPlan().toString());
            sqlChanges.put("centerCost", cc.getCostCenter().toString());
            sqlChanges.put("valueType", cc.getValueType().toString());
            sqlChanges.put("value", cc.getValue().toPlainString());

            try {
                Database.getDatabase().query(sql_InsertCostCenter, sqlChanges);

                log
                        .append("\n")
                        .append("Inserido centro de custo ")
                        .append(cc.getCostCenter())
                        .append(" na chave ")
                        .append(cc.getKey());

            } catch (SQLException ex) {
                throw new Error(ex);
            }
        } else {
            log.append("\n").append("O centro de custo não foi inserido para os parametros porque um deles está nulo: ");
            log.append("Empresa (").append(cc.getEnterprise()).append("), ");
            log.append("Chave (").append(cc.getKey()).append("), ");
            log.append("CC Plano (").append(cc.getCenterCostPlan()).append("), ");
            log.append("CC (").append(cc.getCostCenter()).append("), ");
            log.append("Valor (").append(cc.getValue().toPlainString()).append("), ");
            log.append("Tipo do valor (").append(cc.getValueType()).append("), ");
        }
    }
}
