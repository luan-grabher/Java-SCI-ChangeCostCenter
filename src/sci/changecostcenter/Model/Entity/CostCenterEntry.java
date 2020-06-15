package sci.changecostcenter.Model.Entity;

import java.math.BigDecimal;

public class CostCenterEntry {
    private Integer key;
    private Integer centerCostPlan;
    private Integer centerCost;
    private Integer valueType; /*DEBITO(0) E CREDITO(1)*/
    private BigDecimal value;

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

    public Integer getCenterCost() {
        return centerCost;
    }

    public void setCenterCost(Integer centerCost) {
        this.centerCost = centerCost;
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
