# Diagrama de sequência

Fluxo de registro de uma doação (`POST /api/v1/doadores/{id}/doacoes`), o caminho que passa pelas regras de aptidão antes de gravar qualquer coisa.

```mermaid
sequenceDiagram
    actor Atendente
    participant Frontend as Frontend (React)
    participant Controller as DoacaoController
    participant Service as DoacaoService
    participant DoadorRepo as DoadorRepository
    participant Aptidao as AptidaoService
    participant Regra as RegraAptidao (cada critério)
    participant DoacaoRepo as DoacaoRepository

    Atendente->>Frontend: preenche formulário de doação
    Frontend->>Controller: POST /doadores/{id}/doacoes
    Controller->>Service: registrar(idDoador, pedido)
    Service->>DoadorRepo: findById(idDoador)
    DoadorRepo-->>Service: Doador
    Service->>Aptidao: verificar(doador, pedido)
    loop para cada regra cadastrada
        Aptidao->>Regra: verificar(doador, pedido, hoje)
        Regra-->>Aptidao: Optional~Impedimento~
    end
    Aptidao-->>Service: Aptidao (apto, motivos, próximaDataApta)
    alt doador não apto
        Service-->>Controller: lança DoacaoNaoPermitidaException
        Controller-->>Frontend: 422 com os motivos do impedimento
        Frontend-->>Atendente: mostra os impedimentos
    else doador apto
        Service->>Service: monta Doacao (gera número da bolsa)
        Service->>DoacaoRepo: save(doacao)
        DoacaoRepo-->>Service: Doacao salva
        Service->>Service: atualiza ficha clínica (próxima data apta)
        Service-->>Controller: DoacaoResponse
        Controller-->>Frontend: 201 Created
        Frontend-->>Atendente: mostra número da bolsa
    end
```

## Observações

- `AptidaoService` não decide sozinho: ele percorre todas as implementações de `RegraAptidao` (idade, peso, intervalo, limite anual, primeira doação, tatuagem, impedimento) e junta os impedimentos encontrados.
- Nada é gravado em `DoacaoRepository` se `aptidao.apto()` for falso. A verificação acontece antes da gravação, não depois.
- O mesmo `AptidaoService` é reaproveitado pelo endpoint `GET /doadores/{id}/aptidao`, que roda esse fluxo só até a consulta, sem a parte de gravar a doação.
