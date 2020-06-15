package sci.changecostcenter.Model.Entity;

import Selector.Entity.FiltroString;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class Swap {

    private Integer accountCreditOrDebit = null;
    private Integer accountCredit = null;
    private Integer accountDebit = null;
    private Integer participantCredit = null;
    private Integer participantDebit = null;
    private Integer descriptionCode = null;
    private FiltroString filter;

    private List<ContabilityEntry> entries = new ArrayList<>();
    private Integer costCenterCredit = null;
    private Integer costCenterDebit = null;

    public Swap() {
    }

    public Integer getAccountCreditOrDebit() {
        return accountCreditOrDebit;
    }

    public void setAccountCreditOrDebit(Integer accountCreditOrDebit) {
        this.accountCreditOrDebit = accountCreditOrDebit;
    }       

    public Integer getAccountCredit() {
        return accountCredit;
    }

    public void setAccountCredit(Integer accountCredit) {
        this.accountCredit = accountCredit;
    }

    public Integer getAccountDebit() {
        return accountDebit;
    }

    public void setAccountDebit(Integer accountDebit) {
        this.accountDebit = accountDebit;
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

    public FiltroString getFilter() {
        return filter;
    }

    public void setFilter(FiltroString filter) {
        this.filter = filter;
    }

    public List<ContabilityEntry> getEntries() {
        return entries;
    }

    public void setEntries(List<ContabilityEntry> entries) {
        this.entries = entries;
    }

    public BigDecimal getTotalValue() {
        BigDecimal total = new BigDecimal(0);
        for (ContabilityEntry entry : entries) {
            total.add(entry.getValue());
        }

        return total;
    }
}
