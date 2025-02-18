# Projeto de Gestão de Redes
Agente 
- Implementação de um L-MIB
- Receção e parsing  das mensagens.
- Construção da Error List e envio de uma resposta em formato L-SNMP.
- Simulação dos dispositivos (Sensores , atuadores)

Gestor
- Construtor do pacote do protocolo L-SNMP (Os 3 últimos campos estão no formato TLV, type-length-value)
|Tag | Type | Time-Stamp | Message-Identifier | IID-List | Value-List | Error-List |

|------|------|----|-------|------|------|----|
- Interface da APP.

Interface gráfica do gestor:
![image](https://github.com/user-attachments/assets/789c70d1-436d-49af-aca1-25a4d027cff8)

Observações:
O beaconRate do devices , define o tempo em segundos para as atualizações. (Fazer Set com IID 1.3.1 para atualizar este campo)
O sensor vai incrementar 1 de update em update até o valor do atuador.
