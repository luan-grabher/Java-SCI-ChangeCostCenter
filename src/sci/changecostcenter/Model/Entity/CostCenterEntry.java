package sci.changecostcenter.Model.Entity;

import java.math.BigDecimal;

public class CostCenterEntry {
    public static Integer TYPE_DEBIT = 0;
    public static Integer TYPE_CREDIT = 1;
    
    private Integer key;
    private Integer costCenterPlan;
    private Integer costCenter;
    private Integer valueType; /*DEBITO(0) E CREDITO(1)*/
    private BigDecimal value;

    public Integer getKey() {
        return key;
    }

    public void setKey(Integer key) {
        this.key = key;
    }

    public Integer getCostCenterPlan() {
        return costCenterPlan;
    }

    public void setCostCenterPlan(Integer costCenterPlan) {
        this.costCenterPlan = costCenterPlan;
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
