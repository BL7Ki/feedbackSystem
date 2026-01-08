# Feedback System Serverless

## 1. Equipe
- Leonardo Felipe Ventura Ferreira - RM363339
- Wagner de Lima Braga Silva - RM364223
- Everton Cristiano de Souza Teixeira - RM362065

## 1.1 Link Repositorio:
branch: Main
```
https://github.com/BL7Ki/feedbackSystem

```

## Visão Geral

Este projeto implementa um **sistema de coleta e processamento de feedbacks** utilizando uma arquitetura **Serverless na AWS**, priorizando **escalabilidade automática**, **baixo custo operacional** e **boas práticas de design**, como o **Princípio da Responsabilidade Única (SRP)**.

---

## 2. Arquitetura da Solução

A solução é composta por serviços gerenciados da AWS, organizados de forma desacoplada para garantir resiliência e fácil manutenção.

### Componentes

* **API Gateway**
  Ponto de entrada da aplicação, expondo o endpoint `POST /avaliacao` para recebimento dos feedbacks.

* **Lambda – FeedbackIngestor**
  Responsável por:

  * Validar os dados de entrada
  * Persistir o feedback no DynamoDB
  * Enviar o identificador do feedback para a fila SQS

* **DynamoDB – FeedbackTable**
  Banco de dados NoSQL utilizado para armazenamento persistente dos feedbacks.

* **SQS – FeedbackProcessingQueue**
  Fila responsável por desacoplar a ingestão do feedback do processamento de notificações.

* **Lambda – NotificationProcessor**
  Consome mensagens da fila SQS, avalia a criticidade do feedback e, quando necessário, dispara notificações.

* **SNS – CriticalFeedbackAlert**
  Serviço de mensageria utilizado para envio de alertas por e-mail aos administradores em casos críticos.

* **Lambda – WeeklyReporter**
  Função executada de forma agendada (EventBridge/Cron) para geração de relatórios semanais, armazenados no Amazon S3.

---

## 3. Segurança e Governança

* **IAM Roles (Least Privilege)**
  Cada função Lambda possui apenas as permissões estritamente necessárias. Exemplo: a função de notificação possui permissão de leitura (`GetItem`) no DynamoDB, mas não pode excluir registros.

* **Variáveis de Ambiente**
  ARNs de tópicos SNS, URLs de filas SQS e demais configurações sensíveis não são hardcoded, sendo injetadas via variáveis de ambiente.

---

## 4. Instruções de Deploy

### Pré-requisitos

Antes de executar os comandos abaixo, é necessário ter:

* Java 21
* Maven
* AWS CLI configurado (`aws configure`)
* AWS SAM CLI

> Observação:
> Todos os comandos `sam` e `aws` devem ser executados a partir do diretório `src/` do projeto.

> Atenção:
> Os comandos utilizam recursos reais da AWS e podem gerar custos mínimos
> (Lambda, DynamoDB, S3 e SNS).

O projeto utiliza o **AWS SAM (Serverless Application Model)** para build e deploy.

### 4.1 Build do Projeto

```bash
mvn clean package
```

### 4.2 Deploy na AWS

```bash
sam deploy --guided
```

---

## 5. Testes e Monitoramento

### 5.1 Enviar Feedback Positivo (Nota 4 ou 5)

* Apenas persiste os dados no banco
* **Não** gera notificação por e-mail

```bash
curl -X POST https://j0t1vd2np9.execute-api.us-east-1.amazonaws.com/Prod/avaliacao \
  -H "Content-Type: application/json" \
  -d '{"descricao": "Amei o celular!", "nota": 5}'
```

### 5.2 Enviar Feedback Crítico (Nota 1 a 3)

* Persiste os dados
* Gera alerta automático via SNS (e-mail)

```bash
curl -X POST https://j0t1vd2np9.execute-api.us-east-1.amazonaws.com/Prod/avaliacao \
  -H "Content-Type: application/json" \
  -d '{"descricao": "O produto veio quebrado.", "nota": 2}'
```

### 5.3 Verificação de Logs

Para acompanhar o processamento em tempo real:

```bash
sam logs --stack-name feedback-system --tail
```

---

## 6. Documentação das Funções

| Função                | Gatilho     | Responsabilidade                                      |
| --------------------- | ----------- | ----------------------------------------------------- |
| FeedbackIngestor      | API Gateway | Validação inicial e persistência no DynamoDB          |
| NotificationProcessor | SQS         | Filtro de criticidade (nota ≤ 3) e disparo de SNS     |
| WeeklyReporter        | EventBridge | Cálculo de métricas e exportação de relatório para S3 |

---

## 7. Relatório Semanal

O relatório gerado pela função **WeeklyReporter** contém:

* **Média de avaliações**
  Cálculo aritmético das notas do período

* **Quantidade por dia**
  Agrupamento dos feedbacks por data

* **Quantidade por urgência**
  Total de feedbacks classificados como **CRÍTICO** vs **NORMAL**

---

## Considerações Finais

A arquitetura foi projetada para ser **escalável**, **segura** e **de fácil evolução**, aproveitando ao máximo serviços gerenciados da AWS e boas práticas de engenharia de software.
