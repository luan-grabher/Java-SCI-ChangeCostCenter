package sci.changecostcenter.Model.Entity;

import Auxiliar.Valor;

public class LctoContabil {

    private final Integer chave;
    private final Integer codigoEmpresa;
    private Integer filial;// = 0;

    private Valor data;
    private Integer planoPadrao;
    private Integer deb;
    private Integer cred;
    private Integer terceiroDeb;
    private Integer terceiroCred;
    private Integer historicoPadrao;
    private Valor complemento;
    private Valor documento;
    private Valor valor;

    private boolean conciliadoDeb;
    private boolean conciliadoCred;

    public void setData(Valor data) {
        this.data = data;
    }

    public void setDeb(Integer deb) {
        this.deb = deb;
    }

    public void setCred(Integer cred) {
        this.cred = cred;
    }

    public void setHistoricoPadrao(Integer historicoPadrao) {
        this.historicoPadrao = historicoPadrao;
    }

    public void setComplemento(Valor complemento) {
        this.complemento = complemento;
    }

    public void setDocumento(Valor documento) {
        this.documento = documento;
    }

    public void setValor(Valor valor) {
        this.valor = valor;
    }

    public void setConciliadoDeb(boolean conciliadoDeb) {
        this.conciliadoDeb = conciliadoDeb;
    }

    public void setConciliadoCred(boolean conciliadoCred) {
        this.conciliadoCred = conciliadoCred;
    }

    public Integer getPlanoPadrao() {
        return planoPadrao;
    }

    public void setPlanoPadrao(Integer planoPadrao) {
        this.planoPadrao = planoPadrao;
    }

    public Integer getChave() {
        return chave;
    }

    public Integer getCodigoEmpresa() {
        return codigoEmpresa;
    }

    public Integer getFilial() {
        return filial;
    }

    public Valor getData() {
        return data;
    }

    public Integer getDeb() {
        return deb;
    }

    public Integer getCred() {
        return cred;
    }

    public Integer getHistoricoPadrao() {
        return historicoPadrao;
    }

    public Valor getComplemento() {
        return complemento;
    }

    public Valor getDocumento() {
        return documento;
    }

    public Valor getValor() {
        return valor;
    }

    public boolean isConciliadoDeb() {
        return conciliadoDeb;
    }

    public boolean isConciliadoCred() {
        return conciliadoCred;
    }
    
    public boolean isConciliado(){
        return conciliadoCred || conciliadoDeb;
    }
    
    public void conciliar(){
        conciliadoCred = true;
        conciliadoDeb =  true;
    }

    private String booleanToString(boolean b) {
        if (b) {
            return "TRUE";
        } else {
            return "FALSE";
        }
    }

    public String getConciliadoDeb() {
        return booleanToString(conciliadoDeb);
    }

    public String getConciliadoCred() {
        return booleanToString(conciliadoCred);
    }

    public Integer getTerceiroDeb() {
        return terceiroDeb;
    }

    public void setTerceiroDeb(Integer terceiroDeb) {
        this.terceiroDeb = terceiroDeb;
    }

    public Integer getTerceiroCred() {
        return terceiroCred;
    }

    public void setTerceiroCred(Integer terceiroCred) {
        this.terceiroCred = terceiroCred;
    }

}
