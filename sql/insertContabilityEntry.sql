INSERT INTO VSUC_EMPRESAS_TLAN (
    BDCODEMP,
    BDCHAVE,
    BDCODPLAPADRAO,
    BDDEBITO,
    BDCREDITO,
    BDDATA,
    BDVALOR,
    BDCOMPL,
    BDTIPOLAN,
    BDDCTO,
    BDCODUSU,
    BDCODTERCEIROD,
    BDCODTERCEIROC
    )
VALUES (
    :enterpriseCode, --empresa
    (select coalesce((max(BDCHAVE)+1), 1) from vsuc_empresas_tlan where bdcodemp = :enterpriseCode), --chave
    (select BDCODPLAPADRAO from VWGR_SCI_EMPRESAS where BDCODEMP = :enterpriseCode), --plano padrao
    :accountDebit, --conta debito
    :accountCredit, -- conta credito
    ':date', -- data lançamento
    :value, -- valor
    ':descriptionComplement', -- complemento historico
    1, --tipo lanc
    ':document', -- NF/DOCTO
    32, -- usuario
    :participantDebit, -- participante debito
    :participantCredit --part credit
);