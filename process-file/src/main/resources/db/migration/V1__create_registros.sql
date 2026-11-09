CREATE TABLE registros (
    id UUID PRIMARY KEY,
    id_cliente UUID NOT NULL,
    nome VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL,
    status VARCHAR(30) NOT NULL,
    valor NUMERIC(15,2) NOT NULL,
    data DATE NOT NULL
);

CREATE TABLE arquivos_processamento (
    id BIGSERIAL PRIMARY KEY,
    chave_s3 VARCHAR(1024) NOT NULL UNIQUE,
    status VARCHAR(30) NOT NULL CHECK (status IN ('PENDENTE', 'EM_PROCESSAMENTO', 'PROCESSADO', 'ERRO')),
    leased_by VARCHAR(100),
    lease_expires_at TIMESTAMP NULL,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW(),
    ultimo_erro TEXT
);

CREATE INDEX idx_registros_id_cliente ON registros (id_cliente);
CREATE INDEX idx_registros_status ON registros (status);
CREATE INDEX idx_arquivos_processamento_status ON arquivos_processamento (status);
CREATE INDEX idx_arquivos_processamento_lease ON arquivos_processamento (lease_expires_at);
