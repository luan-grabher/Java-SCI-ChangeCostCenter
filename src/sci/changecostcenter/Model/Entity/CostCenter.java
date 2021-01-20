package sci.changecostcenter.Model.Entity;

import java.math.BigDecimal;

public class CostCenter {

    /*DEBITO(0) E CREDITO(1)*/
    public static final Integer TYPE_DEBIT = 0;
    public static final Integer TYPE_CREDIT = 1;

    private Integer enterprise = null;
    private Integer key = null;
    private Integer centerCostPlan = null;
    private Integer costCenter = null;
    private Integer valueType = null; //Debito(0) ou Credito(1)    
    private BigDecimal value = null;
    private Integer account = null;    
    

    public Integer getEnterprise() {
        return enterprise;
    }

    public void setEnterprise(Integer enterprise) {
        this.enterprise = enterprise;
    }

    public Integer getAccount() {
        return account;
    }
    
    public void setAccount(Integer account, Integer type){
        this.valueType = type;
        this.account = account;
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

    public BigDecimal getValue() {
        return value;
    }

    public void setValue(BigDecimal value) {
        this.value = value;
    }

}
