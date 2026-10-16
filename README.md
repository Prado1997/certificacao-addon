# Requisições Internas

Add-on Sankhya OM (Add-on Studio) para controle de requisições internas: abertura, acompanhamento de status, criação automática a partir da confirmação de notas e consulta via serviço HTTP.

## Requisitos

| Item | Valor |
|---|---|
| Plataforma Sankhya mínima | 4.28 |
| Add-on Studio (`studio-annotations`) | 2.x |
| Java | 8 |
| Banco | Oracle |
| Servidor local | WildFly em `C:\wildfly_producao` |

## Build e deploy

```shell
./gradlew clean deployAddon
```

- O caminho do WildFly fica em `build.gradle` ? `snkmodule.serverFolder`.
- O `autoDDL` está habilitado: as tabelas são criadas e atualizadas a partir de `datadictionary/`. O Auto DDL **não** executa `DROP TABLE` nem `DROP COLUMN`.
- O dicionário de dados e os `dbscripts` só são reprocessados quando a `version` do `build.gradle` muda. Suba a versão sempre que alterar `datadictionary/` ou `dbscripts/`.

## Estrutura

```
datadictionary/   Tabelas, instâncias e menu (Auto DDL)
dbscripts/        Scripts de banco (função FN_TRQ_DIAS_ABERTO)
dashboards/       Dashboard de requisições
model/src/main/java/
  br/com/sankhya/addon/requisicoes/
    actionButtons/   Botão "Atualizar Requisição"
    businessRules/   Regra de bloqueio de parceiro
    callbacks/       Criação de requisição na confirmação de nota
    component/       Regras de aplicação
    controller/      Serviço HTTP RequisicaoControllerSP
    dto/             RequisicaoDTO
    exception/       PersistenciaException
    jobs/            Job de aviso de requisições
    listeners/       Listeners de Parceiro e Requisição
    repository/      Acesso a dados (JAPE)
    util/            Enum de status e conversor DTO
  helper/JapeHelper  Abertura de sessão JAPE com e sem transação
```

## Modelo de dados

| Tabela | Instância | Descrição |
|---|---|---|
| `TRQ_REQUISICAO` | `DRQ_Requisicao` | Requisição interna (parceiro, centro de resultado, local, datas, e-mail, pedido de origem) |
| `TRQ_STATUS_REQUISICAO` | `DRQ_StatusRequisicao` | Status atual da requisição (PK = `ID_REQ`) |
| `TRQ_INSUMO` | `DRQ_Insumo` | Insumos da requisição |
| `TRQ_LOCAL` | `DRQ_Local` | Locais (estrutura em árvore) |

Instâncias nativas usadas: `DRQ_Parceiro` (`TGFPAR`), `DRQ_CentroResultado` (`TSICUS`), `DRQ_UsuarioInclusao` e `DRQ_UsuarioAlteracao` (`TSIUSU`).

Campo calculado `DIASDURACAO`: usa a função `FN_TRQ_DIAS_ABERTO(DT_ABERTURA, DT_ENCERRAMENTO)`, criada em `dbscripts/V1.xml`.

### Status

| Código | Enum | Significado |
|---|---|---|
| `P` | `PENDENTE` | Criada, aguardando atendimento |
| `R` | `REALIZANDO` | Em atendimento |
| `F` | `FINALIZADA` | Atendimento encerrado |

## Funcionalidades

### Menu

- **Requisições Internas** (`DRQ_Requisicao`)
- **Locais** (`DRQ_Local`)
- **Dashboards ? Dashboard Requisições Internas**

### Listeners

| Classe | Instância | Evento | O que faz |
|---|---|---|---|
| `RequisicaoListener` | `DRQ_Requisicao` | `afterInsert` | Grava o status `P` em `TRQ_STATUS_REQUISICAO` para toda requisição nova (tela, serviço ou callback) |
| `RequisicaoListener` | `DRQ_Requisicao` | `beforeUpdate` | Se `EMAIL_PARCEIRO` estiver vazio, preenche com o e-mail do parceiro |
| `ParceiroListener` | `Parceiro` | `afterUpdate` | Quando o e-mail do parceiro muda, atualiza `EMAIL_PARCEIRO` nas requisições dele |

> O status inicial é gravado **somente** pelo `RequisicaoListener`. Não insira status manualmente após criar uma requisição: a PK de `TRQ_STATUS_REQUISICAO` é `ID_REQ` e o segundo insert gera `ORA-00001`.

### Callback: criação de requisição na confirmação

`RequisicaoCallback`, com `@Callback(when = AFTER, event = PROCESS_CONFIRMATION)`.

- Dispara na confirmação de notas pelas **telas Centrais**. O evento `CONFIRMATION` cobre apenas os Portais.
- Atua somente na **TOP 1801** e somente quando a confirmação não teve erro (`data.get("error") == null`).
- Lê o `NUNOTA` de `data.get("nunota")`, busca o cabeçalho e cria a requisição com `CODPARC`, `NUNOTA` (em `COD_REQUISICAO_PORTAL`) e `CODCENCUS`.
- Roda em transação própria (`JapeHelper.executeWithTX`), porque no AFTER não há transação ativa. Se a criação falhar, a nota continua confirmada.

### Regra de negócio: parceiro bloqueado

`RequisicaoBusinessRule` (`@BusinessRule`).

- Em `beforeInsert` e `beforeUpdate`, bloqueia notas da **TOP 1801** cujo parceiro esteja com `TGFPAR.BLOQUEAR = 'S'`.
- Ignora itens (`ItemNota`): a regra dispara também para os itens, que não têm `CODTIPOPER`.

### Botão de ação: Atualizar Requisição

`RequisicaoActionButton`, na instância `DRQ_Requisicao`. Atualiza o status das requisições selecionadas:

- **Realizar Atendimento** ? `R`
- **Finalizar Atendimento** ? `F`

### Job: aviso de requisições

`RequisicaoAvisoJob` (`RequisicaoAvisoJobSP`) roda a cada 1 hora (`&3600000`) e grava um aviso em `AvisoSistema` com o total de requisições por status.

## Serviço HTTP: `RequisicaoControllerSP`

Base: `http://localhost:8080/requisicoes-internas/service.sbr`

Todas as chamadas exigem `mgeSession` (JSESSIONID obtido no login via `/mge/service.sbr`).

> A chave dentro de `requestBody` deve ter o **mesmo nome do parâmetro** do método no controller. O SPBean gerado lê esse nome com `jsonRequest.get("<parametro>")`.

### `testeV0`

```
GET /requisicoes-internas/service.sbr?serviceName=RequisicaoControllerSP.testeV0&mgeSession={{jsessionid}}
```

### `salvar`

```
POST /requisicoes-internas/service.sbr?serviceName=RequisicaoControllerSP.salvar&mgeSession={{jsessionid}}
Content-Type: application/json
```
```json
{
    "requestBody": {
        "requisicao": {
            "idParceiro": 130,
            "idPedido": 58,
            "idCentroResultado": 1003000
        }
    }
}
```

### `listar`

```
POST /requisicoes-internas/service.sbr?serviceName=RequisicaoControllerSP.listar&mgeSession={{jsessionid}}
Content-Type: application/json
```
```json
{
    "requestBody": {
        "idParceiro": 130
    }
}
```

Retorna as requisições do parceiro. Os campos sem valor (`idPedido` e `idCentroResultado` em requisições criadas pela tela) vêm como `null`.

## Transações

| Contexto | Helper | Motivo |
|---|---|---|
| `@Service` com `EJBTransactionType.Required` | `JapeHelper.executeWithoutTX` | O container já abre a transação. `execWithTX` gera "Já existe uma transação em andamento" |
| `@Callback` AFTER | `JapeHelper.executeWithTX` | Não há transação ativa. Sem ela, ocorre `CORE_E05913` "Operação requer uma transação ativa" |
| `@Job` com `EJBTransactionType.Supports` | `JapeHelper.executeWithTX` | O job não recebe transação do container |
