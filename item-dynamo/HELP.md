### Criar a tabela do DynamoDB

Imagem amazon/dynamodb-local. 

#### 1️⃣ Subir o container

```bash
docker-compose up -d
```

Verifique se está rodando:

```bash
docker ps
```

Você deve ver algo como `dynamodb-local` rodando na porta `8000`.

---

#### 2️⃣ Configurar credenciais (só se ainda não tiver AWS CLI configurada)

O **Amazon DynamoDB Local** exige que a AWS CLI receba credenciais, mesmo que sejam falsas.

Se você **já usa AWS CLI na sua máquina**, provavelmente já tem credenciais configuradas e pode pular isso.

Se não tiver, execute:

```bash
export AWS_ACCESS_KEY_ID=fake
export AWS_SECRET_ACCESS_KEY=fake
export AWS_DEFAULT_REGION=us-east-1
```

💡 Isso vale apenas para o terminal atual.

---

#### 3️⃣ Criar a tabela

Agora sim:

```bash
aws dynamodb create-table \
  --table-name items \
  --attribute-definitions \
      AttributeName=id,AttributeType=S \
  --key-schema \
      AttributeName=id,KeyType=HASH \
  --billing-mode PAY_PER_REQUEST \
  --endpoint-url http://localhost:8000 \
  --region us-east-1
```

Se tudo der certo, você receberá um JSON com os detalhes da tabela.

---

#### 🔎 Testar se funcionou

```bash
aws dynamodb list-tables \
  --endpoint-url http://localhost:8000 \
  --region us-east-1
```

---


### Filas SQS Standard

O script está na pasta localstack/init. Ao subir o container as filas são criadas. 