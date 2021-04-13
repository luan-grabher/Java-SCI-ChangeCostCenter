select
    :first1
    BDCHAVE,
    BDVALOR
from VSUC_EMPRESAS_TLAN L
WHERE 
L.BDCODEMP = :enterprise
AND L.BDREFERENCIA = :reference
and L.BDTIPOLANORIG  = 100
:complement
:account
:value