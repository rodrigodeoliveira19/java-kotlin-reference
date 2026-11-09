# Task 13 - Estrutura do código

## Objetivo
Organizar o projeto em componentes com responsabilidades bem definidas.

## Estrutura sugerida
Separar responsabilidades em componentes para:
- Descoberta de arquivos S3.
- Movimentação de arquivos S3.
- Leitura e validação do CSV.
- Orquestração da importação.
- Entidade JPA.
- Repositório Micronaut Data.
- Configuração da aplicação.

## Critérios
- Manter baixo acoplamento entre os módulos de leitura, persistência e movimentação de arquivos.
- Facilitar testes unitários e de integração isolados por responsabilidade.

## Status atual
- Estrutura separada em config, csv, entity, lease, importer, repository, s3, service.
- Componentes isolados para autenticação AWS, processamento do CSV, lease do arquivo e persistência.
- O código está organizado para permitir testes focados por responsabilidade.
