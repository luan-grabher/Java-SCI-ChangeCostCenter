package sci.changecostcenter.Model.Entity;

import java.math.BigDecimal;

public class CostCenterEntry {
    public static final Integer TYPE_DEBIT = 0;
    public static final Integer TYPE_CREDIT = 1;
    
    private Integer key= null;
    private Integer centerCostPlan = null;
    private Integer costCenter = null;
    private Integer valueType = null; /*DEBITO(0) E CREDITO(1)*/
    private BigDecimal value = null;
    private Integer debitAccount = null;
    private Integer creditAccount = null;
    private Integer account = null;

    public Integer getAccount() {
        return account;
    }

    public void setAccount(Integer account) {
        this.account = account;
    }   
    
    public Integer getDebitAccount() {
        return debitAccount;
    }

    public void setDebitAccount(Integer debitAccount) {
        this.valueType = TYPE_DEBIT;
        this.debitAccount = debitAccount;
        this.account = debitAccount;
    }

    public Integer getCreditAccount() {
        return creditAccount;
    }

    public void setCreditAccount(Integer creditAccount) {
        this.valueType = TYPE_CREDIT;
        this.creditAccount = creditAccount;
        this.account = creditAccount;
    }       

    public Integer getKey() {
        return key;
    }

    public void setKey(Integer key) {
        this.key = key;
    }

    public Integer getCenterCostPlan() {
        return centerCostPlan;
    }

    public void setCenterCostPlan(Integer centerCostPlan) {
        this.centerCostPlan = centerCostPlan;
    }

    public Integer getCostCenter() {
        return costCenter;
    }

    public void setCostCenter(Integer costCenter) {
        this.costCenter = costCenter;
    }

    public Integer getValueType() {
        return valueType;
    }

    public void setValueType(Integer valueType) {
        this.valueType = valueType;
    }

    public BigDecimal getValue() {
        return value;
    }

    public void setValue(BigDecimal value) {
        this.value = value;
    }
    
    
}
