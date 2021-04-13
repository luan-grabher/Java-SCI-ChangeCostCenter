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
import sci.changecostcenter.Model.Entity.Swap;
import static sci.changecostcenter.SCIChangeCostCenter.ini;
import static sci.changecostcenter.SCIChangeCostCenter.log;
import static sci.changecostcenter.SCIChangeCostCenter.reference;
import sql.Database;

public class SwapModel {

    private static final String sql_GetContabilityEntries = FileManager.getText(FileManager.getFile("sql\\selectChangeCostCenterContabilityEntries.sql"));
    private static final String sql_InsertCostCenter = FileManager.getText(FileManager.getFile("sql\\insertCostCenter.sql"));
    private static final String sql_GetIncorrectCCs = FileManager.getText(FileManager.getFile("sql\\getIncorrectCCs.sql"));
    private static final String sql_UpdateCCVal = FileManager.getText(FileManager.getFile("sql\\updateCCVal.sql"));

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

        try {
            //Desativa fechamento automatico do banco
            Database.getDatabase().setAutoClose(false);

            swaps.forEach((swap) -> {
                ((Loading) loading.get("loading")).next();

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
                    //Se tiver valor para inserir ou filtro de valor
                    if (entries.size() > 1 && swap.getValue() != null || swap.getValueFilter() != null) {
                        //exclui todos lançamentos menos so primeiro
                        //grava o primeiro
                        Map<String, Object> entry = entries.get(0);
                        //Exclui o resto
                        entries.clear();
                        //coloca nas entradas a primeira entrada
                        entries.add(entry);
                    }

                    Map<String, Object> insertLoading = new HashMap<>();
                    insertLoading.put("loading", new Loading("Importando CCs " + reference, 0, entries.size()));

                    //Para cada lcto
                    entries.forEach((e) -> {
                        ((Loading) insertLoading.get("loading")).next();

                        Map<String, String> cc = new HashMap<>();
                        cc.put("enterprise", swap.getEnterprise().toString());
                        cc.put("key", e.get("BDCHAVE").toString());
                        cc.put("centerCostPlan", sqlSwaps.get("centerCostPlan"));
                        cc.put("centerCost", swap.getCostCenter().toString());
                        cc.put("valueType", swap.getValueType().toString());

                        //Se tiver o valor
                        if (swap.getValue() != null) {
                            //Define o valor
                            cc.put("value", swap.getValue().toPlainString());
                        }//Se tiver porcentagem 
                        else if (swap.getPercent() != null) {
                            //Define o valor como a % do valor do lançamento
                            BigDecimal value = (new BigDecimal(e.get("BDVALOR").toString())).setScale(4, RoundingMode.HALF_UP);
                            cc.put("value", value.multiply(swap.getPercent()).toPlainString());
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

        //Fecha conexao com o banco de dados
        Database.getDatabase().setAutoClose(true);
        Database.getDatabase().close(true);
    }

    private static void insertCC(Map<String, String> cc) {
        if (//Verifica se todos ampos estão ok
                !"".equals(cc.getOrDefault("enterprise",""))
                && !"".equals(cc.getOrDefault("key",""))
                && !"".equals(cc.getOrDefault("centerCostPlan",""))
                && !"".equals(cc.getOrDefault("centerCost",""))
                && !"".equals(cc.getOrDefault("valueType",""))
                && !"".equals(cc.getOrDefault("value",""))) {

            try {
                Database.getDatabase().query(sql_InsertCostCenter, cc);

                log
                        .append("\n")
                        .append("Inserido centro de custo ")
                        .append(cc.get("centerCost"))
                        .append(" na chave ")
                        .append(cc.get("key"));

            } catch (SQLException ex) {
                throw new Error(ex);
            }
        } else {
            log.append("\n").append("O centro de custo não foi inserido para os parametros porque um deles está nulo: ");
            log.append("Empresa (").append(cc.get("enterprise")).append("), ");
            log.append("Chave (").append(cc.get("key")).append("), ");
            log.append("CC Plano (").append(cc.get("centerCostPlan")).append("), ");
            log.append("CC (").append(cc.get("costCenter")).append("), ");
            log.append("Valor (").append(cc.get("value")).append("), ");
            log.append("Tipo do valor (").append(cc.get("valueType")).append("), ");
        }
    }

    /**
     * Procura centros de custo de porcentagem que quando inseridos ficaram com
     * diferença de 1 centavo negativo. Para cada Chave com diferença, procura o
     * cc equivalente e altera o valor do primeiro cc adicionando o valor da
     * diferença
     */
    public static void correctCCs() {
        Map<Integer, Integer> verifiedAccounts = new HashMap<>();
        Map<String, String> sqlSwaps = new HashMap<>();
        sqlSwaps.put("reference", reference);

        swaps.forEach((swap) -> {
            //Se tiver conta, não for verificada ainda e tiver percentual
            if (swap.getAccount() != null
                    && !verifiedAccounts.containsKey(swap.getAccount())
                    && swap.getPercent() != null) {
                verifiedAccounts.put(swap.getAccount(), swap.getAccount());

                sqlSwaps.put("enterprise", swap.getEnterprise().toString());

                //Account
                String account = " AND BD";
                if (swap.getValueType().equals(Swap.TYPE_CREDIT)) {
                    account += "CREDITO = ";
                } else {
                    account += "DEBITO = ";
                }
                account += swap.getAccount() + " ";
                sqlSwaps.put("account", account);

                //Procura diferenças
                List<Map<String, Object>> entries = Database.getDatabase().getMap(sql_GetIncorrectCCs, sqlSwaps);

                if (!entries.isEmpty()) {
                    //Para cada chave
                    entries.forEach((entry) -> {
                        Map<String, String> updateSwaps = new HashMap<>();
                        updateSwaps.put("enterprise", swap.getEnterprise().toString());
                        updateSwaps.put("key", entry.get("CHAVE").toString());
                        updateSwaps.put("difference", entry.get("DIFERENCA").toString());
                        updateSwaps.put("cc", swap.getCostCenter().toString());

                        //Atuliza valor no banco de um cc
                        try {
                            Database.getDatabase().query(sql_UpdateCCVal, updateSwaps);
                        } catch (Exception e) {
                            throw new Error(e);
                        }
                    });
                }
            }
        });
    }
}
