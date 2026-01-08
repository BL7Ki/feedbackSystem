# Feedback System Serverless

## Visão Geral

Este projeto implementa um **sistema de coleta e processamento de feedbacks** utilizando uma arquitetura **Serverless na AWS**, priorizando **escalabilidade automática**, **baixo custo operacional** e **boas práticas de design**, como o **Princípio da Responsabilidade Única (SRP)**.

---

## 1. Arquitetura da Solução

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

## 2. Segurança e Governança

* **IAM Roles (Least Privilege)**
  Cada função Lambda possui apenas as permissões estritamente necessárias. Exemplo: a função de notificação possui permissão de leitura (`GetItem`) no DynamoDB, mas não pode excluir registros.

* **Variáveis de Ambiente**
  ARNs de tópicos SNS, URLs de filas SQS e demais configurações sensíveis não são hardcoded, sendo injetadas via variáveis de ambiente.

---

## 3. Instruções de Deploy

O projeto utiliza o **AWS SAM (Serverless Application Model)** para build e deploy.

### 3.1 Build do Projeto

Este comando pode rodar na raiz do projeto:

```bash
mvn clean package
```

### 3.2 Deploy na AWS

Usar cd src/ antes de executar o comando abaixo:

```bash
sam deploy --guided
```

---

## 4. Testes e Monitoramento

### 4.1 Enviar Feedback Positivo (Nota 4 ou 5)

* Apenas persiste os dados no banco
* **Não** gera notificação por e-mail

```bash
curl -X POST https://j0t1vd2np9.execute-api.us-east-1.amazonaws.com/Prod/avaliacao \
  -H "Content-Type: application/json" \
  -d '{"descricao": "Excelente mouse! amei!", "nota": 5}'
```

### 4.2 Enviar Feedback Crítico (Nota 1 a 3)

* Persiste os dados
* Gera alerta automático via SNS (e-mail)

```bash
curl -X POST https://j0t1vd2np9.execute-api.us-east-1.amazonaws.com/Prod/avaliacao \
  -H "Content-Type: application/json" \
  -d '{"descricao": "O produto veio quebrado!", "nota": 2}'
```

### 4.3 Verificação de Logs

Para acompanhar o processamento em tempo real:

```bash
sam logs --stack-name feedback-system --tail
```
Usar cd src/ antes de executar o comando acima.
---

## 5. Documentação das Funções

| Função                | Gatilho     | Responsabilidade                                      |
| --------------------- | ----------- | ----------------------------------------------------- |
| FeedbackIngestor      | API Gateway | Validação inicial e persistência no DynamoDB          |
| NotificationProcessor | SQS         | Filtro de criticidade (nota ≤ 3) e disparo de SNS     |
| WeeklyReporter        | EventBridge | Cálculo de métricas e exportação de relatório para S3 |

---

## 6. Relatório Semanal

O relatório gerado pela função **WeeklyReporter** contém:

* **Média de avaliações**
  Cálculo aritmético das notas do período

* **Quantidade por dia**
  Agrupamento dos feedbacks por data

* **Quantidade por urgência**
  Total de feedbacks classificados como **CRÍTICO** vs **NORMAL**

O EventBridge só é acionado de domingo. Sendo assim, caso seja necessário forçar a geração do relatório, pode-se utilizar o comando:

```bash
aws lambda invoke \
  --function-name feedback-system-WeeklyReporterFunction-0McK1jxSBOVR \
  --cli-binary-format raw-in-base64-out \
  --payload '{}' \
  response.json
```

Após isso, é possível ver o relatório no S3 utilizando o comando abaixo:

```bash
aws s3 ls s3://feedback-reports-183366578375-us-east-1/reports/
```

É possível ler o conteúdo do relatório:

```bash
aws s3 cp s3://feedback-reports-183366578375-us-east-1/reports/SUA_DATA_weekly_report.txt .
```
---

## Considerações Finais

A arquitetura foi projetada para ser **escalável**, **segura** e **de fácil evolução**, aproveitando ao máximo serviços gerenciados da AWS e boas práticas de engenharia de software.
