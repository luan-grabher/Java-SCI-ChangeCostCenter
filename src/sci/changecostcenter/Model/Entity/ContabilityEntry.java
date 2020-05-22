package sci.changecostcenter.Model.Entity;

import java.math.BigDecimal;
import java.util.Calendar;

public class ContabilityEntry {

    private Integer key = 0;
    private Integer enterpriseCode = 0;
    private Integer branch = 0;

    private Calendar date = Calendar.getInstance();
    private Integer defaultPlan = 0;
    private Integer accountDebit = 0;
    private Integer accountCredit = 0;
    private Integer participantCredit = 0;
    private Integer participantDebit = 0;
    private Integer descriptionCode = 0;
    private String descriptionComplement = "";
    private String document = "";
    private BigDecimal value = BigDecimal.ZERO;

    private boolean conciliateDebit;
    private boolean conciliateCredit;

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

}
