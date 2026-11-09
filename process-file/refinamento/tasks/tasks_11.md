# Task 11 - Segurança e configuração

## Objetivo
Proteger a aplicação e externalizar as configurações críticas para ambientes e credenciais da AWS.

## Requisitos
- Utilizar a cadeia padrão de provedores de credenciais AWS.
- Não armazenar credenciais no código.
- Externalizar as configurações do S3 e do PostgreSQL.
- Utilizar Flyway para criar e versionar o schema.
- Evitar registrar dados sensíveis dos registros nos logs.

## Diretrizes
- A configuração deve ser feita por variáveis de ambiente, properties ou configuração externa do Micronaut.
- Logs devem evitar expor conteúdo impróprio de registros ou credenciais.

## Status atual
- Configuração externa de S3, PostgreSQL, Redis e Shedlock foi incorporada em `application.properties`.
- Credenciais AWS usam `DefaultCredentialsProvider`, sem armazenar segredos no código.
- Logs não expõem dados sensíveis do CSV nem credenciais. 
