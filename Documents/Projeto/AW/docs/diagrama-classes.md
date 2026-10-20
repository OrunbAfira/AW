# Diagrama de classes

Modelo de domínio do HemoVida: `br.umc.hemovida.model`. O banco (H2) não aparece como classe porque é infraestrutura, acessada através dos repositórios (`DoadorRepository`, `DoacaoRepository`), não parte do domínio.

```mermaid
classDiagram
    class Doador {
        -Long id
        -String nome
        -LocalDate dataNascimento
        -Sexo sexo
        -TipoSanguineo tipoSanguineo
        -FatorRh fatorRh
        -String telefone
        +statusEm(LocalDate) StatusDoador
        +historico() HistoricoDoacoes
        +atualizarAposDoacao(Double, LocalDate)
        +atualizar(...)
    }

    class Cpf {
        -String numero
        +Cpf(String)
    }

    class FichaClinica {
        -Long id
        -Double pesoEmQuilos
        -boolean impedimentoDefinitivo
        -LocalDate dataProximaDoacao
        -LocalDate dataUltimaTatuagem
        +atualizarAposDoacao(Double, LocalDate)
        +atualizarDadosClinicos(Double, boolean, LocalDate)
    }

    class Doacao {
        -Long id
        -LocalDate dataDoacao
        -String numeroBolsa
        -Integer volumeEmMililitros
        -String observacoes
        -String pressaoArterial
        -Double hemoglobina
    }

    class HistoricoDoacoes {
        -List~Doacao~ doacoes
        +estaVazio() boolean
        +ultimaDoacao() Optional~Doacao~
        +doacoesNosUltimos12Meses(LocalDate) List~Doacao~
    }

    class Sexo {
        <<enumeration>>
        MASCULINO
        FEMININO
    }

    class TipoSanguineo {
        <<enumeration>>
        A
        B
        AB
        O
    }

    class FatorRh {
        <<enumeration>>
        POSITIVO
        NEGATIVO
    }

    class StatusDoador {
        <<enumeration>>
        APTO
        INAPTO_TEMPORARIO
        INAPTO_DEFINITIVO
    }

    Doador "1" *-- "1" Cpf : possui
    Doador "1" o-- "0..1" FichaClinica : possui
    Doador "1" --> "*" Doacao : acumula
    Doador ..> HistoricoDoacoes : monta a partir das doações
    HistoricoDoacoes o-- "*" Doacao
    Doacao "*" --> "1" Doador : pertence a
    Doador --> Sexo
    Doador --> TipoSanguineo
    Doador --> FatorRh
    Doador ..> StatusDoador : resultado de statusEm()
```

## Observações

- `Cpf` é um *value object* (`record` imutável, `@Embeddable`), não uma entidade própria: vive sempre dentro de um `Doador`, sem id ou ciclo de vida independente.
- `Doador` e `FichaClinica` têm relação 1:1: a ficha só existe junto com o doador (`cascade = ALL`, `orphanRemoval = true`).
- `Doador` e `Doacao` têm relação 1:N: um doador acumula várias doações ao longo do tempo.
- `HistoricoDoacoes` não é uma entidade persistida. É montada sob demanda a partir da lista de doações de um doador (`Doador.historico()`), para agrupar as regras de consulta ao histórico (última doação, doações nos últimos 12 meses) fora da entidade `Doador`.
