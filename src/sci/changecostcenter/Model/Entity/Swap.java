package sci.changecostcenter.Model.Entity;

import fileManager.StringFilter;
import java.math.BigDecimal;
import java.math.RoundingMode;

public class Swap {

    /*DEBITO(0) E CREDITO(1)*/
    public static final Integer TYPE_DEBIT = 0;
    public static final Integer TYPE_CREDIT = 1;

    //Filtros arquivo de trocas
    private Integer enterprise = null;
    private Integer accountCreditOrDebit = null;
    private Integer accountCredit = null;
    private Integer accountDebit = null;

    //Filtros arquivo de despesas
    private StringFilter complementFilter = null;
    private String document = null;
    private BigDecimal valueFilter = null;

    //Variaveis centro de custo
    private BigDecimal value = null;
    private BigDecimal percent = null;

    private Integer costCenterCredit = null;
    private Integer costCenterDebit = null;

    private Integer costCenter = null; //Definido automaticamente
    private Integer valueType = null; //Definido automaticamente
    private Integer account = null; //Definido automaticamente

    public Integer getAccount() {
        return account;
    }

    public Integer getCostCenter() {
        return costCenter;
    }

    public Integer getValueType() {
        return valueType;
    }

    public void setValueFilter(BigDecimal valueFilter) {
        this.valueFilter = valueFilter;
    }

    public BigDecimal getValueFilter() {
        return valueFilter;
    }

    public Integer getEnterprise() {
        return enterprise;
    }

    public void setEnterprise(Integer enterprise) {
        this.enterprise = enterprise;
    }

    public BigDecimal getPercent() {
        return percent;
    }

    public void setPercent(BigDecimal percent) {
        this.percent = percent == null ? null : percent.setScale(4, RoundingMode.HALF_UP);
    }

    public String getDocument() {
        return document;
    }

    public void setDocument(String document) {
        this.document = document;
    }

    public BigDecimal getValue() {
        return value;
    }

    public void setValue(BigDecimal value) {
        this.value = value == null ? null : value.setScale(4, RoundingMode.HALF_UP);
    }

    public Integer getAccountCreditOrDebit() {
        return accountCreditOrDebit;
    }

    public void setAccountCreditOrDebit(Integer accountCreditOrDebit) {
        if (accountCreditOrDebit != null) {
            account = accountCreditOrDebit;
        }
        this.accountCreditOrDebit = accountCreditOrDebit;
    }

    public Integer getAccountCredit() {
        return accountCredit;
    }

    public void setAccountCredit(Integer accountCredit) {
        if (accountCredit != null) {
            account = accountCredit;
        }
        this.accountCredit = accountCredit;
    }

    public Integer getAccountDebit() {
        return accountDebit;
    }

    public void setAccountDebit(Integer accountDebit) {
        if (accountDebit != null) {
            account = accountDebit;
        }
        this.accountDebit = accountDebit;
    }

    public Integer getCostCenterCredit() {
        return costCenterCredit;
    }

    public void setCostCenterCredit(Integer costCenterCredit) {
        if (costCenterCredit != null) {
            costCenter = costCenterCredit;
            valueType = TYPE_CREDIT;
            this.costCenterCredit = costCenterCredit;
        }
    }

    public Integer getCostCenterDebit() {
        return costCenterDebit;
    }

    public void setCostCenterDebit(Integer costCenterDebit) {
        if (costCenterDebit != null) {
            costCenter = costCenterDebit;
            valueType = TYPE_DEBIT;
            this.costCenterDebit = costCenterDebit;
        }
    }

    public StringFilter getComplementFilter() {
        return complementFilter;
    }

    public void setComplementFilter(StringFilter complementFilter) {
        this.complementFilter = complementFilter;
    }
}
