package sci.changecostcenter.Model.Entity;

import java.math.BigDecimal;
import java.util.Calendar;

public class ContabilityEntry {

    private Integer key = 0;
    private Integer enterpriseCode = 0;
    private Integer branch = 0;

    private Calendar date = Calendar.getInstance();
    private Integer defaultPlan = 0;
    private Integer accountDebit = null;
    private Integer accountCredit = null;
    private Integer participantCredit = null;
    private Integer participantDebit = null;
    private Integer descriptionCode = null;
    private String descriptionComplement = "";
    private String document = "";
    private BigDecimal value = BigDecimal.ZERO;

    private boolean conciliateDebit = false;
    private boolean conciliateCredit = false;
    
    private Integer costCenterCredit = null;
    private Integer costCenterDebit = null;
    
    private Integer entryType = 0;
    
    public ContabilityEntry copy(){
        ContabilityEntry entry = new ContabilityEntry();
        entry.setKey(key);
        entry.setEnterpriseCode(enterpriseCode);
        entry.setBranch(branch);
        entry.setDate(date);
        entry.setDefaultPlan(defaultPlan);
        entry.setAccountCredit(accountCredit);
        entry.setAccountDebit(accountDebit);
        entry.setParticipantDebit(participantDebit);
        entry.setParticipantCredit(participantCredit);
        entry.setDescriptionCode(descriptionCode);
        entry.setDescriptionComplement(descriptionComplement);
        entry.setDocument(document);
        entry.setValue(value);
        entry.setConciliateCredit(conciliateCredit);
        entry.setConciliateDebit(conciliateDebit);
        entry.setCostCenterCredit(costCenterCredit);
        entry.setCostCenterDebit(costCenterDebit);
        entry.setEntryType(entryType);
        
        return entry;
    }

    public Integer getEntryType() {
        return entryType;
    }

    public void setEntryType(Integer entryType) {
        this.entryType = entryType;
    }    

    public Integer getKey() {
        return key;
    }

    public void setKey(Integer key) {
        this.key = key;
    }

    public Integer getEnterpriseCode() {
        return enterpriseCode;
    }

    public void setEnterpriseCode(Integer enterpriseCode) {
        this.enterpriseCode = enterpriseCode;
    }

    public Integer getBranch() {
        return branch;
    }

    public void setBranch(Integer branch) {
        this.branch = branch;
    }

    public Calendar getDate() {
        return date;
    }

    public void setDate(Calendar date) {
        this.date = date;
    }

    public Integer getDefaultPlan() {
        return defaultPlan;
    }

    public void setDefaultPlan(Integer defaultPlan) {
        this.defaultPlan = defaultPlan;
    }

    public Integer getAccountDebit() {
        return accountDebit;
    }

    public void setAccountDebit(Integer accountDebit) {
        this.accountDebit = accountDebit;
    }

    public Integer getAccountCredit() {
        return accountCredit;
    }

    public void setAccountCredit(Integer accountCredit) {
        this.accountCredit = accountCredit;
    }

    public Integer getParticipantCredit() {
        return participantCredit;
    }

    public void setParticipantCredit(Integer participantCredit) {
        this.participantCredit = participantCredit;
    }

    public Integer getParticipantDebit() {
        return participantDebit;
    }

    public void setParticipantDebit(Integer participantDebit) {
        this.participantDebit = participantDebit;
    }

    public Integer getDescriptionCode() {
        return descriptionCode;
    }

    public void setDescriptionCode(Integer descriptionCode) {
        this.descriptionCode = descriptionCode;
    }

    public String getDescriptionComplement() {
        return descriptionComplement;
    }

    public void setDescriptionComplement(String descriptionComplement) {
        this.descriptionComplement = descriptionComplement;
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
        this.value = value;
    }

    public boolean isConciliateDebit() {
        return conciliateDebit;
    }

    public void setConciliateDebit(boolean conciliateDebit) {
        this.conciliateDebit = conciliateDebit;
    }

    public boolean isConciliateCredit() {
        return conciliateCredit;
    }

    public void setConciliateCredit(boolean conciliateCredit) {
        this.conciliateCredit = conciliateCredit;
    }
    
    public boolean isConciliate(){
        return conciliateCredit || conciliateDebit;
    }
    
    public void conciliate(){
        conciliateCredit = true;
        conciliateDebit =  true;
    }

    public Integer getCostCenterCredit() {
        return costCenterCredit;
    }

    public void setCostCenterCredit(Integer costCenterCredit) {
        this.costCenterCredit = costCenterCredit;
    }

    public Integer getCostCenterDebit() {
        return costCenterDebit;
    }

    public void setCostCenterDebit(Integer costCenterDebit) {
        this.costCenterDebit = costCenterDebit;
    }

    
}
